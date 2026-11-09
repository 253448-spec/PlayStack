package com.upchiapas.videojuegos.feature_videojuegos.data.mapper

import com.upchiapas.videojuegos.feature_videojuegos.data.dto.VideojuegoDto
import com.upchiapas.videojuegos.feature_videojuegos.data.dto.VideojuegoRequestDto
import com.upchiapas.videojuegos.feature_videojuegos.domain.model.Videojuego

fun VideojuegoDto.toDomain(): Videojuego = Videojuego(
    id = this.id,
    titulo = this.titulo,
    plataforma = this.plataforma,
    precio = this.precio,
    horasJugadas = this.horasJugadas,
    completado = this.completado,
    genero = this.genero
)

fun Videojuego.toRequestDto(): VideojuegoRequestDto = VideojuegoRequestDto(
    titulo = this.titulo,
    plataforma = this.plataforma,
    precio = this.precio,
    horasJugadas = this.horasJugadas,
    completado = this.completado,
    genero = this.genero
)
