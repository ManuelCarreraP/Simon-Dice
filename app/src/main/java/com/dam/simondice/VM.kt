package com.dam.simondice

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

/**
 * Función de extensión para oscurecer un color.
 */
fun Color.darken(factor: Float): Color {
    val a = alpha
    val r = red * factor
    val g = green * factor
    val b = blue * factor
    return Color(r, g, b, a)
}

/**
 * ViewModel del juego SimonDice. Gestiona la lógica y el estado.
 */
class VM : ViewModel() {

    fun generaNumero(maximo: Int): Int {
        return (0 until maximo).random()
    }

    fun inicializaJuego() {
        Datos.speed = 250
        Datos.ronda = 0
        Datos.secuencia.clear()
        Datos.secuenciaUsuario.clear()
        Datos.status = Status.INICIO
        Datos.text = "¡A JUGAR!"
        Datos.colorNameHint = ""
        aumentarSecuencia()
    }

    fun aumentarSecuencia() {
        Datos.status = Status.SECUENCIA
        Datos.secuencia.add(generaNumero(4))
        Datos.secuenciaUsuario.clear()
        Datos.ronda = Datos.secuencia.size
        mostrarSecuencia()
    }

    /**
     * Muestra la secuencia de colores al usuario solo con el flash de los botones.
     * Muestra la pista de texto SOLO para el nuevo color añadido.
     */
    fun mostrarSecuencia() {
        viewModelScope.launch {
            Datos.text = "¡MEMORIZA LA SECUENCIA!"

            // Iterar sobre la secuencia completa, SOLO FLASH
            for (colorInt in Datos.secuencia) {
                val colorEnum = Colores.values().first { it.colorInt == colorInt }

                // Pista visual: flashea el botón.
                // NO se actualiza Datos.colorNameHint aquí (según tu requisito).
                cambiaColorBotonSecuencia(colorEnum, true)

                // Tiempo de visualización es la velocidad del juego
                delay(Datos.speed.toLong())

                // Lo borra
                cambiaColorBotonSecuencia(colorEnum, false)
                delay(Datos.speed.toLong() / 2) // Pausa de separación
            }

            // Finalizada la secuencia, comienza el turno del usuario.
            Datos.text = "¡TU TURNO!"
            Datos.status = Status.ESPERANDO

            // Muestra la pista de texto SOLO para el NUEVO color añadido (el último de la secuencia)
            if (Datos.secuencia.isNotEmpty()) {
                val lastColorInt = Datos.secuencia.last()
                muestraPistaDeTexto(lastColorInt)
            }
        }
    }

    /**
     * Muestra la pista de texto solo por colorHintDuration (2 segundos).
     */
    fun muestraPistaDeTexto(colorInt: Int) {
        viewModelScope.launch {
            val colorName = Colores.values().first { it.colorInt == colorInt }.nombre

            Datos.colorNameHint = colorName
            delay(Datos.colorHintDuration) // Pista visible por 2 segundos

            // Si el jugador sigue esperando el input, borra la pista
            if (Datos.status != Status.FINALIZADO) {
                Datos.colorNameHint = ""
            }
        }
    }

    fun aumentarSecuenciaUsuario(colorPulsado: Int) {
        if (Datos.status == Status.ESPERANDO || Datos.status == Status.ENTRADA) {
            Datos.status = Status.ENTRADA
            Datos.secuenciaUsuario.add(colorPulsado)
            compruebaSecuencia()
        }
    }

    private fun compruebaSecuencia(){
        val indiceActual = Datos.secuenciaUsuario.lastIndex

        // 1. Comprueba si el último clic es correcto
        if (Datos.secuenciaUsuario[indiceActual] != Datos.secuencia[indiceActual]) {
            Datos.colorNameHint = ""
            compruebaSecuenciaUsuario(false) // Game Over
            return
        }

        // 2. Comprueba si la secuencia COMPLETA está terminada
        if (Datos.secuenciaUsuario.size == Datos.secuencia.size) {
            Datos.colorNameHint = ""
            compruebaSecuenciaUsuario(true) // Pasa de ronda
        } else {
            // 3. El clic fue correcto. Si la pista de texto está vacía, no se muestra nada más.
            Datos.status = Status.ESPERANDO
        }
    }

    private fun compruebaSecuenciaUsuario(esCorrecta: Boolean) {
        viewModelScope.launch {
            if (esCorrecta) {
                // Aumentar dificultad
                if (Datos.ronda % 4 == 0 && Datos.speed > 50) {
                    Datos.speed -= 25
                }

                if (Datos.ronda > Datos.record) {
                    Datos.record = Datos.ronda
                }
                Datos.text = "¡RONDA ${Datos.ronda} SUPERADA!"
                delay(1000)
                aumentarSecuencia()
            } else {
                finalizaJuego()
            }
        }
    }

    fun finalizaJuego() {
        viewModelScope.launch {
            Datos.status = Status.FINALIZADO
            Datos.colorNameHint = "GAME OVER"
            Datos.mensajeToast = "Has perdido. Tu récord es: ${Datos.record}"
            Datos.mostrarToast = true
            Datos.text = "START"
        }
    }

    /**
     * Flash del botón (feedback de click o parte de la secuencia).
     * Usa Colores.baseColor() definido en Datos.kt.
     * @param activate true para oscurecer, false para restaurar.
     */
    fun cambiaColorBotonSecuencia(colores: Colores, activate: Boolean) {
        val newColor = if (activate) {
            colores.baseColor().darken(0.2f)
        } else {
            colores.baseColor()
        }

        when (colores) {
            Colores.ROJO -> Datos.colorRed = newColor
            Colores.VERDE -> Datos.colorGreen = newColor
            Colores.AZUL -> Datos.colorBlue = newColor
            Colores.AMARILLO -> Datos.colorYellow = newColor
        }
    }


    /**
     * Flash corto como feedback de click.
     */
    fun cambiaColorBoton(colores: Colores) {
        val delayDuration = 150L

        viewModelScope.launch {
            // Activa el flash
            cambiaColorBotonSecuencia(colores, true)
            delay(delayDuration)
            // Desactiva el flash
            cambiaColorBotonSecuencia(colores, false)
        }
    }
}