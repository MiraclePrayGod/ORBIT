package com.orbit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.ui.theme.HomeDimens
import com.orbit.ui.theme.cupertinoCardShadow

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    isAmountVisible: Boolean = true,
    onEyeClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(HomeDimens.SummaryHeight)
            .cupertinoCardShadow(cornerRadius = HomeDimens.SummaryCorner)
            .graphicsLayer {
                shadowElevation = 0f
                shape = RoundedCornerShape(HomeDimens.SummaryCorner)
                clip = true
            }
            .border(
                width = 0.5.dp,
                color = Color(0x08000000),
                shape = RoundedCornerShape(HomeDimens.SummaryCorner)
            ),
        shape = RoundedCornerShape(HomeDimens.SummaryCorner),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(HomeDimens.SummaryPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(HomeDimens.SummaryIconContainer)
                        .background(
                            iconColor.copy(alpha = 0.15f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(HomeDimens.SummaryInnerIcon)
                    )
                }

                Spacer(modifier = Modifier.width(HomeDimens.SummaryGap))

                Text(
                    text = if (isAmountVisible) value else "••••",
                    fontSize = HomeDimens.SummaryValueSize,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFF1D1D1F),
                    letterSpacing = (-0.6).sp
                )

                Spacer(modifier = Modifier.width(HomeDimens.SummaryGap))

                IconButton(
                    onClick = onEyeClick,
                    modifier = Modifier.size(16.dp)
                ) {
                    Icon(
                        if (isAmountVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isAmountVisible) "Ocultar" else "Mostrar",
                        tint = Color(0xFF181717),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = title,
                fontSize = HomeDimens.SummaryTitleSize,
                fontWeight = FontWeight.W600,
                color = Color(0xFF48484A),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                letterSpacing = (-0.2).sp
            )
        }
    }
}
