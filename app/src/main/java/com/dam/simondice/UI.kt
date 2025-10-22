package com.dam.simondice

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape // <-- IMPORTADO
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Reemplaza con tu importación correcta
import com.dam.simondice.ui.theme.SimonDiceTheme


@Composable
fun IU(miViewModel: VM) {

    // 1. Manejo del Toast
    val context = LocalContext.current
    if (Datos.mostrarToast) {
        // Usamos LaunchedEffect para ejecutar el Toast solo una vez
        LaunchedEffect(Datos.mostrarToast) {
            Toast.makeText(context, Datos.mensajeToast, Toast.LENGTH_LONG).show()
            Datos.mostrarToast = false // Consume el evento para que no se repita
        }
    }

    // 2. Estructura Principal: Columna centrada
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {

        // Fila Superior: Ronda y Record
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            InfoBox("RONDA", Datos.ronda.toString())
            InfoBox("RECORD", Datos.record.toString())
        }

        // --- Contenedor Central de Juego (Botones + Pista de Texto) ---
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Cuadrícula 2x2 de colores
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Fila Superior (Azul y Verde)
                Row(horizontalArrangement = Arrangement.Center) {
                    BotonColor(miViewModel, Colores.AZUL)
                    BotonColor(miViewModel, Colores.VERDE)
                }
                // Fila Inferior (Rojo y Amarillo)
                Row(horizontalArrangement = Arrangement.Center) {
                    BotonColor(miViewModel, Colores.ROJO)
                    BotonColor(miViewModel, Colores.AMARILLO)
                }
            }

            Spacer(modifier = Modifier.height(20.dp)) // Espacio de separación

            // Recuadro de Texto debajo de los colores
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(300.dp)
                    .height(80.dp)
                    .padding(8.dp)
                    .border(2.dp, Color.Black)
            ) {
                // Muestra el nombre del color (o vacío si el tiempo ha pasado)
                Text(
                    text = Datos.colorNameHint,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
        // --- FIN Contenedor Central ---

        // Fila Inferior: Botón START/RESET
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (Datos.status == Status.INICIO || Datos.status == Status.FINALIZADO) {
                        miViewModel.inicializaJuego()
                    } else {
                        miViewModel.finalizaJuego()
                    }
                },
                shape = RectangleShape,
                colors = buttonColors(Color.White),
                modifier = Modifier
                    .width(180.dp)
                    .height(80.dp)
                    .padding(8.dp)
                    .border(1.dp, Color.Black),
            ) {
                Text(text = Datos.text, fontSize = 24.sp, color = Color.Black)
            }
        }
    }
}

// Composable auxiliar para Ronda/Record
@Composable
fun InfoBox(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 20.sp)
        Box(
            modifier = Modifier
                .border(1.dp, Color.Black)
                .width(80.dp)
                .padding(5.dp)
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun BotonColor(miViewModel: VM, colorEnum: Colores) {

    // Obtiene el color reactivo (para los flashes) directamente del objeto Datos
    val reactiveColor = when (colorEnum) {
        Colores.ROJO -> Datos.colorRed
        Colores.VERDE -> Datos.colorGreen
        Colores.AZUL -> Datos.colorBlue
        Colores.AMARILLO -> Datos.colorYellow
    }

    // Deshabilita el click si el juego está mostrando secuencia o finalizado
    val enabled = Datos.status == Status.ESPERANDO || Datos.status == Status.ENTRADA

    Button(
        enabled = enabled,
        onClick = {
            // Flash corto de feedback
            miViewModel.cambiaColorBoton(colorEnum)
            // Lógica de juego
            miViewModel.aumentarSecuenciaUsuario(colorEnum.colorInt)
        },
        // Botón circular
        shape = CircleShape,
        colors = buttonColors(reactiveColor), // Usa el color reactivo
        modifier = Modifier
            .size(150.dp)
            .padding(8.dp)
            .border(3.dp, Color.Black, CircleShape),
    ) {
        // Botón vacío
    }
}

@Preview(showBackground = true)
@Composable
fun IUPreview() {
    SimonDiceTheme {
        val viewModel = remember { VM() }
        IU (miViewModel = viewModel)
    }
}