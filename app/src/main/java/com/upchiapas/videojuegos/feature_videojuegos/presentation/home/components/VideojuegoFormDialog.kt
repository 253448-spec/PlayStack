package com.upchiapas.videojuegos.feature_videojuegos.presentation.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.upchiapas.videojuegos.feature_videojuegos.domain.model.Videojuego

@Composable
fun VideojuegoFormDialog(
    videojuegoToEdit: Videojuego?,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (titulo: String, plataforma: String, precio: Double, horasJugadas: Int, completado: Boolean, genero: String) -> Unit
) {
    var titulo by remember { mutableStateOf(videojuegoToEdit?.titulo ?: "") }
    var plataforma by remember { mutableStateOf(videojuegoToEdit?.plataforma ?: "") }
    var precioText by remember { mutableStateOf(videojuegoToEdit?.precio?.toString() ?: "") }
    var horasText by remember { mutableStateOf(videojuegoToEdit?.horasJugadas?.toString() ?: "0") }
    var completado by remember { mutableStateOf(videojuegoToEdit?.completado ?: false) }
    var genero by remember { mutableStateOf(videojuegoToEdit?.genero ?: "Acción") }

    var tituloError by remember { mutableStateOf<String?>(null) }
    var plataformaError by remember { mutableStateOf<String?>(null) }
    var precioError by remember { mutableStateOf<String?>(null) }
    var horasError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        var valid = true

        if (titulo.isBlank() || titulo.trim().length < 3) {
            tituloError = "El título debe tener al menos 3 caracteres"
            valid = false
        } else {
            tituloError = null
        }

        if (plataforma.isBlank()) {
            plataformaError = "La plataforma es obligatoria (ej: PC, PS5, Switch)"
            valid = false
        } else {
            plataformaError = null
        }

        val parsedPrecio = precioText.toDoubleOrNull()
        if (parsedPrecio == null || parsedPrecio < 0) {
            precioError = "Ingresa un precio válido (mayor o igual a 0)"
            valid = false
        } else {
            precioError = null
        }

        val parsedHoras = horasText.toIntOrNull()
        if (parsedHoras == null || parsedHoras < 0) {
            horasError = "Ingresa horas válidas (entero mayor o igual a 0)"
            valid = false
        } else {
            horasError = null
        }

        return valid
    }

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = {
            Text(
                text = if (videojuegoToEdit == null) "Nuevo Videojuego" else "Editar Videojuego",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = {
                        titulo = it
                        tituloError = null
                    },
                    label = { Text("Título") },
                    isError = tituloError != null,
                    supportingText = tituloError?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = plataforma,
                    onValueChange = {
                        plataforma = it
                        plataformaError = null
                    },
                    label = { Text("Plataforma") },
                    placeholder = { Text("PC, PS5, Switch, Xbox...") },
                    isError = plataformaError != null,
                    supportingText = plataformaError?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = genero,
                    onValueChange = { genero = it },
                    label = { Text("Género") },
                    placeholder = { Text("RPG, Aventura, Acción...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = precioText,
                        onValueChange = {
                            precioText = it
                            precioError = null
                        },
                        label = { Text("Precio ($)") },
                        isError = precioError != null,
                        supportingText = precioError?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = horasText,
                        onValueChange = {
                            horasText = it
                            horasError = null
                        },
                        label = { Text("Horas") },
                        isError = horasError != null,
                        supportingText = horasError?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("¿Campaña completada?", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = if (completado) "Juego terminado" else "En progreso",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = completado,
                        onCheckedChange = { completado = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (validate()) {
                        onSave(
                            titulo,
                            plataforma,
                            precioText.toDouble(),
                            horasText.toInt(),
                            completado,
                            genero.ifBlank { "General" }
                        )
                    }
                },
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (videojuegoToEdit == null) "Guardar" else "Actualizar")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSaving
            ) {
                Text("Cancelar")
            }
        }
    )
}
