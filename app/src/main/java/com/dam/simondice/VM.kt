package com.dam.simondice

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

/**
 * Función de extensión para aclarar un color (en vez de oscurecer).
 */
fun Color.lighten(factor: Float): Color {
    val a = alpha
    val r = (1f - (1f - red) * factor).coerceIn(0f, 1f)
    val g = (1f - (1f - green) * factor).coerceIn(0f, 1f)
    val b = (1f - (1f - blue) * factor).coerceIn(0f, 1f)
    return Color(r, g, b, a)
}

/**
 * ViewModel del juego SimonDice. Gestiona la lógica y el estado.
 */
class VM : ViewModel() {

    private fun obtenerNumeroAleatorio(): Int {
        return (0..3).random()
    }

    fun inicializaJuego() {
        Datos.speed = 250
        Datos.ronda = 0
        Datos.secuencia.clear()
        Datos.secuenciaUsuario.clear()
        Datos.status = Status.INICIO
        Datos.text = "¡COMIENZA!"
        Datos.colorNameHint = ""
        generarNuevaSecuencia()
    }

    private fun generarNuevaSecuencia() {
        Datos.status = Status.SECUENCIA
        Datos.secuencia.add(obtenerNumeroAleatorio())
        Datos.secuenciaUsuario.clear()
        Datos.ronda = Datos.secuencia.size
        reproducirSecuencia()
    }

    /**
     * Muestra la secuencia de colores al usuario.
     */
    private fun reproducirSecuencia() {
        viewModelScope.launch {
            Datos.text = "¡RECUERDA!"

            // Iterar sobre la secuencia completa
            for (indice in Datos.secuencia.indices) {
                val colorInt = Datos.secuencia[indice]
                val colorEnum = Colores.values().first { it.colorInt == colorInt }

                // Efecto visual: flashea el botón
                activarEfectoColor(colorEnum, true)
                delay(Datos.speed.toLong())

                activarEfectoColor(colorEnum, false)

                // Pausa más corta entre colores
                if (indice < Datos.secuencia.size - 1) {
                    delay(Datos.speed.toLong() / 3)
                }
            }

            // Preparar turno del usuario
            Datos.text = "¡REPITE!"
            Datos.status = Status.ESPERANDO

            // Mostrar pista solo para el nuevo color
            if (Datos.secuencia.isNotEmpty()) {
                mostrarIndicadorColor(Datos.secuencia.last())
            }
        }
    }

    /**
     * Muestra la pista de texto por un tiempo limitado.
     */
    private fun mostrarIndicadorColor(colorInt: Int) {
        viewModelScope.launch {
            val nombreColor = Colores.values().first { it.colorInt == colorInt }.nombre
            Datos.colorNameHint = nombreColor
            delay(Datos.colorHintDuration)

            // Solo limpiar si el juego sigue activo
            if (Datos.status == Status.ESPERANDO || Datos.status == Status.ENTRADA) {
                Datos.colorNameHint = ""
            }
        }
    }

    fun procesarEntradaUsuario(colorPulsado: Int) {
        if (Datos.status == Status.ESPERANDO || Datos.status == Status.ENTRADA) {
            Datos.status = Status.ENTRADA
            Datos.secuenciaUsuario.add(colorPulsado)
            verificarSecuencia()
        }
    }

    private fun verificarSecuencia() {
        val ultimoIndice = Datos.secuenciaUsuario.lastIndex

        // Verificar si el último clic es correcto
        if (Datos.secuenciaUsuario[ultimoIndice] != Datos.secuencia[ultimoIndice]) {
            Datos.colorNameHint = ""
            manejarResultadoSecuencia(false) // Game Over
            return
        }

        // Verificar si se completó la secuencia
        if (Datos.secuenciaUsuario.size == Datos.secuencia.size) {
            Datos.colorNameHint = ""
            manejarResultadoSecuencia(true) // Pasa de ronda
        } else {
            // Continuar ingresando la secuencia
            Datos.status = Status.ESPERANDO
        }
    }

    private fun manejarResultadoSecuencia(acierto: Boolean) {
        viewModelScope.launch {
            if (acierto) {
                // Incrementar dificultad cada 3 rondas
                if (Datos.ronda % 3 == 0 && Datos.speed > 60) {
                    Datos.speed -= 20
                }

                // Actualizar récord si es necesario
                if (Datos.ronda > Datos.record) {
                    Datos.record = Datos.ronda
                }

                Datos.text = "¡CORRECTO!"
                delay(800) // Tiempo ligeramente menor
                generarNuevaSecuencia()
            } else {
                terminarJuego()
            }
        }
    }

    fun terminarJuego() {
        viewModelScope.launch {
            Datos.status = Status.FINALIZADO
            Datos.colorNameHint = "FIN DEL JUEGO"
            Datos.mensajeToast = "Juego terminado. Récord: ${Datos.record}"
            Datos.mostrarToast = true
            Datos.text = "REINICIAR"
        }
    }

    /**
     * Efecto visual para los botones (usa aclarado en vez de oscurecido).
     */
    fun activarEfectoColor(colores: Colores, activar: Boolean) {
        val nuevoColor = if (activar) {
            colores.baseColor().lighten(0.3f) // Aclara en vez de oscurecer
        } else {
            colores.baseColor()
        }

        when (colores) {
            Colores.ROJO -> Datos.colorRed = nuevoColor
            Colores.VERDE -> Datos.colorGreen = nuevoColor
            Colores.AZUL -> Datos.colorBlue = nuevoColor
            Colores.AMARILLO -> Datos.colorYellow = nuevoColor
        }
    }

    /**
     * Feedback visual al pulsar un botón.
     */
    fun efectoPulsacionBoton(colores: Colores) {
        viewModelScope.launch {
            activarEfectoColor(colores, true)
            delay(120L) // Tiempo ligeramente diferente
            activarEfectoColor(colores, false)
        }
    }

    // Función de compatibilidad (para no romper UI.kt)
    fun aumentarSecuenciaUsuario(colorPulsado: Int) {
        procesarEntradaUsuario(colorPulsado)
    }

    fun cambiaColorBoton(colores: Colores) {
        efectoPulsacionBoton(colores)
    }

    fun cambiaColorBotonSecuencia(colores: Colores, activate: Boolean) {
        activarEfectoColor(colores, activate)
    }

    fun aumentarSecuencia() {
        generarNuevaSecuencia()
    }

    fun finalizaJuego() {
        terminarJuego()
    }
}