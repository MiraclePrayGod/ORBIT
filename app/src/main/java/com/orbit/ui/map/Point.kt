package com.orbit.ui.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

data class Point(val x: Double, val y: Double)

@Composable
fun PanamaMapScreen() {
    val points = remember { providedPoints() }
    var zoom by remember { mutableStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoomChange, _ ->
                            zoom = (zoom * zoomChange).coerceIn(0.1f, 20f)
                            panOffset += pan
                        }
                    }
            ) {
                // Sistema tipo GeoGebra: origen centrado, X hacia la derecha, Y hacia arriba, escala uniforme
                if (points.isNotEmpty()) {
                    val minX = points.minOf { it.x }.toFloat()
                    val maxX = points.maxOf { it.x }.toFloat()
                    val minY = points.minOf { it.y }.toFloat()
                    val maxY = points.maxOf { it.y }.toFloat()

                    val maxAbsX = maxOf(kotlin.math.abs(minX), kotlin.math.abs(maxX))
                    val maxAbsY = maxOf(kotlin.math.abs(minY), kotlin.math.abs(maxY))
                    val dataWidth = (maxAbsX * 2f).coerceAtLeast(1f)
                    val dataHeight = (maxAbsY * 2f).coerceAtLeast(1f)

                    val margin = 16.dp.toPx()
                    val availableWidth = (size.width - margin * 2).coerceAtLeast(1f)
                    val availableHeight = (size.height - margin * 2).coerceAtLeast(1f)

                    val baseScale = minOf(availableWidth / dataWidth, availableHeight / dataHeight)
                    val scale = baseScale * zoom

                    // Origen en el centro del Canvas + paneo en espacio de pantalla
                    val centerX = size.width / 2f + panOffset.x
                    val centerY = size.height / 2f + panOffset.y
                    val contentWidth = dataWidth * scale
                    val contentHeight = dataHeight * scale
                    val contentLeft = centerX - contentWidth / 2f
                    val contentRight = centerX + contentWidth / 2f
                    val contentTop = centerY - contentHeight / 2f
                    val contentBottom = centerY + contentHeight / 2f

                    // Rejilla tipo GeoGebra (sólida) y ejes con flechas y ticks
                    run {
                        val gridColor = Color(0xFFE0E0E0)
                        val axisColor = Color(0xFF9E9E9E)
                        val axisStroke = 2f
                        // Ticks cada 1 unidad (si el rango es grande, podemos espaciar más)
                        val stepX = when {
                            dataWidth > 200f -> 10f
                            dataWidth > 100f -> 5f
                            else -> 1f
                        }
                        val stepY = when {
                            dataHeight > 200f -> 10f
                            dataHeight > 100f -> 5f
                            else -> 1f
                        }

                        // Líneas verticales de rejilla centradas en el origen (X=0)
                        var gx = kotlin.math.ceil(-maxAbsX / stepX) * stepX
                        while (gx <= maxAbsX + 1e-3f) {
                            val x = centerX + gx * scale
                            drawLine(
                                color = gridColor,
                                start = Offset(x, contentBottom),
                                end = Offset(x, contentTop),
                                strokeWidth = 1f
                            )
                            gx += stepX
                        }
                        // Líneas horizontales de rejilla centradas en el origen (Y=0), con Y hacia arriba
                        var gy = kotlin.math.ceil(-maxAbsY / stepY) * stepY
                        while (gy <= maxAbsY + 1e-3f) {
                            val y = centerY - gy * scale
                            drawLine(
                                color = gridColor,
                                start = Offset(contentLeft, y),
                                end = Offset(contentRight, y),
                                strokeWidth = 1f
                            )
                            gy += stepY
                        }

                        // Eje Y (x = 0) siempre
                        val x0 = centerX
                        drawLine(
                            color = axisColor,
                            start = Offset(x0, contentBottom),
                            end = Offset(x0, contentTop),
                            strokeWidth = axisStroke
                        )
                        // Flecha hacia arriba
                        drawLine(axisColor, Offset(x0, contentTop), Offset(x0 - 6f, contentTop + 12f), 2f)
                        drawLine(axisColor, Offset(x0, contentTop), Offset(x0 + 6f, contentTop + 12f), 2f)

                        // Eje X (y = 0) siempre
                        val y0 = centerY
                        drawLine(
                            color = axisColor,
                            start = Offset(contentLeft, y0),
                            end = Offset(contentRight, y0),
                            strokeWidth = axisStroke
                        )
                        // Flecha hacia la derecha
                        drawLine(axisColor, Offset(contentRight, y0), Offset(contentRight - 12f, y0 - 6f), 2f)
                        drawLine(axisColor, Offset(contentRight, y0), Offset(contentRight - 12f, y0 + 6f), 2f)
                    }

                    // Construir un Path conectando todos los puntos en orden
                    val path = Path()
                    points.firstOrNull()?.let { first ->
                        val sx = centerX + first.x.toFloat() * scale
                        val sy = centerY - first.y.toFloat() * scale
                        path.moveTo(sx, sy)
                    }
                    for (i in 1 until points.size) {
                        val p = points[i]
                        val x = centerX + p.x.toFloat() * scale
                        val y = centerY - p.y.toFloat() * scale
                        path.lineTo(x, y)
                    }

                    // Usar un grosor igual al diámetro de los puntos (2 * 3f = 6f)
                    drawPath(
                        path = path,
                        color = Color.Blue,
                        style = Stroke(width = 6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )

                    // Dibujar puntos como círculos sobre el trazo
                    points.forEach { p ->
                        val cx = centerX + p.x.toFloat() * scale
                        val cy = centerY - p.y.toFloat() * scale
                        drawCircle(
                            color = Color.Blue,
                            radius = 3f,
                            center = Offset(cx, cy)
                        )
                    }
                }
            }
            
            // Título
            Text(
                text = "Mapa de Panamá - Puntos",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                style = MaterialTheme.typography.headlineSmall
            )

            // Controles simples de zoom (+/-) sin tocar los datos
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { zoom = (zoom * 0.8f).coerceAtLeast(0.1f) }) { Text("-") }
                Spacer(modifier = Modifier.size(8.dp))
                Button(onClick = { zoom = (zoom * 1.25f).coerceAtMost(10f) }) { Text("+") }
                Spacer(modifier = Modifier.size(8.dp))
                Button(onClick = { zoom = 1f; panOffset = Offset.Zero }) { Text("Reset") }
            }
        }
    }
}

private fun providedPoints(): List<Point> = listOf(
    Point(640.48852, 215.6255),
    Point(641.75852, 216.7355),
    Point(643.03852, 217.4655),
    Point(644.31852, 218.1955),
    Point(645.45852, 225.3755),
    Point(648.54852, 223.5955),
    Point(651.68852, 221.8155),
    Point(654.12852, 224.3655),
    Point(658.12852, 236.8955),
    Point(658.68852, 241.0055),
    Point(659.97852, 241.7855),
    Point(661.19852, 242.2555),
    Point(662.31852, 244.2355),
    Point(662.75852, 246.4855),
    Point(662.27852, 248.2155),
    Point(661.75852, 248.9755),
    Point(663.19852, 260.4155),
    Point(662.55852, 263.7055),
    Point(660.11852, 265.9555),
    Point(658.13852, 267.4355),
    Point(656.13852, 266.6755),
    Point(654.28852, 265.1155),
    Point(653.08852, 264.2755),
    Point(651.56852, 262.6255),
    Point(650.34852, 260.8555),
    Point(648.46852, 259.5855),
    Point(647.08852, 258.7155),
    Point(645.97852, 256.7755),
    Point(644.58852, 254.3955),
    Point(644.34852, 251.5955),
    Point(645.19852, 249.0455),
    Point(645.46852, 248.3955),
    Point(644.36852, 245.2755),
    Point(644.14852, 244.4855),
    Point(643.33852, 243.2155),
    Point(643.11852, 242.4555),
    Point(642.70852, 241.2155),
    Point(641.85852, 240.0155),
    Point(641.26852, 239.4655),
    Point(640.13852, 239.0855),
    Point(639.45852, 238.3955),
    Point(639.11852, 237.4555),
    Point(638.63852, 237.0455),
    Point(638.33852, 236.7155),
    Point(637.85852, 235.9555),
    Point(637.45852, 235.2755),
    Point(636.98852, 234.5955),
    Point(636.62852, 234.1955),
    Point(636.11852, 233.9755),
    Point(635.86852, 233.5555),
    Point(635.46852, 233.0355),
    Point(635.26852, 232.6355),
    Point(634.95852, 232.2155),
    Point(634.26852, 232.0155),
    Point(634.13852, 231.7955),
    Point(633.76852, 231.2555),
    Point(633.54852, 230.8955),
    Point(633.16852, 230.6755),
    Point(633.13852, 230.1455),
    Point(633.41852, 229.8355),
    Point(633.75852, 229.3355),
    Point(634.11852, 228.7155),
    Point(634.46852, 228.4555),
    Point(634.70852, 228.0355),
    Point(635.15852, 227.8955),
    Point(635.41852, 227.3355)
)




