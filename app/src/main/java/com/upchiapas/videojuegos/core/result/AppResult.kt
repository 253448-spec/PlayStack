package com.upchiapas.videojuegos.core.result

import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.serialization.SerializationException

sealed interface DataError {
    data object NoInternet : DataError
    data object Timeout : DataError
    data class Http(val code: Int) : DataError
    data object Serialization : DataError
    data object Unknown : DataError

    fun toUserMessage(): String = when (this) {
        is NoInternet -> "No hay conexión a internet o el servidor no responde"
        is Timeout -> "El servidor tardó demasiado en responder (Tiempo de espera agotado)"
        is Http -> when (code) {
            400 -> "Petición inválida o datos incorrectos"
            401 -> "Credenciales incorrectas o sesión expirada"
            404 -> "Recurso no encontrado"
            500 -> "Error interno del servidor"
            else -> "Error del servidor (Código $code)"
        }
        is Serialization -> "Error al procesar la respuesta del servidor"
        is Unknown -> "Ocurrió un error inesperado"
    }
}

sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Failure(val error: DataError) : AppResult<Nothing>
}

suspend fun <T> safeApiCall(call: suspend () -> T): AppResult<T> =
    try {
        AppResult.Success(call())
    } catch (e: CancellationException) {
        throw e // Nunca silenciar la cancelación en corrutinas
    } catch (e: SocketTimeoutException) {
        AppResult.Failure(DataError.Timeout)
    } catch (e: IOException) {
        AppResult.Failure(DataError.NoInternet)
    } catch (e: HttpException) {
        AppResult.Failure(DataError.Http(e.code()))
    } catch (e: SerializationException) {
        AppResult.Failure(DataError.Serialization)
    } catch (e: Exception) {
        AppResult.Failure(DataError.Unknown)
    }
