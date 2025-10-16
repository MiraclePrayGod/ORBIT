package com.orbit.ui.theme

import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Aplica una sombra estilo iOS con dos capas (base difusa + contacto) y un highlight superior.
 * Produce un efecto 3D limpio y profesional sin ensuciar otros elementos.
 */
fun Modifier.cupertinoCardShadow(
    cornerRadius: Dp,
    baseBlur: Float = 24f,
    baseDy: Float = 8f,
    baseColor: Color = Color(0x33000000), // 20%
    contactBlur: Float = 8f,
    contactDy: Float = 2f,
    contactColor: Color = Color(0x26000000), // 15%
    highlightAlpha: Int = 24 // ~9% blanco
): Modifier = this.drawWithContent {
    drawIntoCanvas { canvas ->
        val r = cornerRadius.toPx()
        val rect = RectF(0f, 0f, size.width, size.height)

        // Sombra base muy difusa
        val basePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.TRANSPARENT
            setShadowLayer(baseBlur, 0f, baseDy, baseColor.toArgb())
        }
        canvas.nativeCanvas.drawRoundRect(rect, r, r, basePaint)

        // Sombra de contacto (más concentrada)
        val contactPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.TRANSPARENT
            setShadowLayer(contactBlur, 0f, contactDy, contactColor.toArgb())
        }
        canvas.nativeCanvas.drawRoundRect(rect, r, r, contactPaint)

        // Highlight superior sutil (simula rebote de luz)
        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.argb(highlightAlpha, 255, 255, 255)
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        canvas.nativeCanvas.drawRoundRect(rect, r, r, strokePaint)
    }
    drawContent()
}



