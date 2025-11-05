package com.dam.simondice

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.dam.simondice.ui.theme.SimonDiceTheme

@Composable
fun IU(miViewModel: VM) {

    // 1. Manejo del Toast
    val context = LocalContext.current
    if (Datos.mostrarToast) {
        LaunchedEffect(Datos.mostrarToast) {
            Toast.makeText(context, Datos.mensajeToast, Toast.LENGTH_LONG).show()
            Datos.mostrarToast = false
        }
    }

    // 2. Estructura Principal con fondo morado claro
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8DAFF)) // Morado muy clarito
    ) {
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

                // Cuadrícula 2x2 de colores - AHORA CUADRADOS
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

                Spacer(modifier = Modifier.height(20.dp))

                // Recuadro de Texto simple
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(300.dp)
                        .height(60.dp)
                        .border(2.dp, Color.Black)
                        .background(Color.White) // Fondo blanco para mejor contraste
                ) {
                    Text(
                        text = Datos.colorNameHint,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

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
                        .height(60.dp)
                        .border(1.dp, Color.Black),
                ) {
                    Text(text = Datos.text, fontSize = 20.sp, color = Color.Black)
                }
            }
        }
    }
}

// Composable auxiliar para Ronda/Record
@Composable
fun InfoBox(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Box(
            modifier = Modifier
                .border(1.dp, Color.Black)
                .width(80.dp)
                .padding(5.dp)
                .background(Color.White) // Fondo blanco para mejor contraste
        ) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun BotonColor(miViewModel: VM, colorEnum: Colores) {

    // Obtiene el color reactivo directamente del objeto Datos
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
        shape = RoundedCornerShape(12.dp), // CUADRADOS con esquinas ligeramente redondeadas
        colors = buttonColors(reactiveColor),
        modifier = Modifier
            .size(140.dp)
            .padding(8.dp)
            .border(3.dp, Color.Black, RoundedCornerShape(12.dp)), // Borde cuadrado
    ) {
        // Botón vacío
    }
}

@Preview(showBackground = true)
@Composable
fun IUPreview() {
    SimonDiceTheme {
        val viewModel = remember { VM() }
        IU(miViewModel = viewModel)
    }
}