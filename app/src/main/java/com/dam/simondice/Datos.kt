package com.dam.simondice
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/**
 * Clase que almacena los datos del juego (VERSION BASADA EN TEXTO).
 */
object Datos {
    // Variables de Estado Reactivas
    var ronda by mutableStateOf(0)
    var record by mutableStateOf(0)
    var status by mutableStateOf(Status.INICIO)

    // Pista de Texto. Contiene el nombre del color a pulsar o está vacía.
    var colorNameHint by mutableStateOf("")

    // Variables internas de la secuencia
    var secuencia = mutableListOf<Int>()
    var secuenciaUsuario = mutableListOf<Int>()

    // Variables de UI/Control
    var text by mutableStateOf("START") // Texto del botón inferior
    var speed = 250 // Velocidad base del destello (en ms)
    var colorHintDuration = 2000L // Duración en milisegundos para mostrar el nombre del color

    // Colores de los botones (reactivos para el destello)
    var colorRed by mutableStateOf(Color.Red)
    var colorGreen by mutableStateOf(Color.Green)
    var colorBlue by mutableStateOf(Color.Blue)
    var colorYellow by mutableStateOf(Color.Yellow)

    // Variables para el manejo de Toast
    var mostrarToast by mutableStateOf(false)
    var mensajeToast by mutableStateOf("")
}

/**
 * Enum con los estados del juego.
 */
enum class Status {
    INICIO, SECUENCIA, ESPERANDO, ENTRADA, COMPROBANDO, FINALIZADO, CLICK
}

// Con el índice y el nombre
enum class Colores(val colorInt: Int, val nombre: String) {
    ROJO(0, "ROJO"),
    VERDE(1, "VERDE"),
    AZUL(2, "AZUL"),
    AMARILLO(3, "AMARILLO")
}

// Función de extensión para obtener el color base (MOVEMOS AQUÍ)
fun Colores.baseColor(): Color {
    return when (this) {
        Colores.ROJO -> Color.Red
        Colores.VERDE -> Color.Green
        Colores.AZUL -> Color.Blue
        Colores.AMARILLO -> Color.Yellow
    }
}