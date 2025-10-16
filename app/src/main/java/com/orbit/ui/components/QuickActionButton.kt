package com.orbit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.ui.theme.HomeDimens
import com.orbit.ui.theme.cupertinoCardShadow

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(HomeDimens.QuickHeight)
            .cupertinoCardShadow(cornerRadius = HomeDimens.QuickCorner)
            .graphicsLayer {
                shadowElevation = 0f
                shape = RoundedCornerShape(HomeDimens.QuickCorner)
                clip = true
            }
            .border(
                width = 0.5.dp,
                color = Color(0x08000000),
                shape = RoundedCornerShape(HomeDimens.QuickCorner)
            ),
        shape = RoundedCornerShape(HomeDimens.QuickCorner),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(HomeDimens.QuickPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(HomeDimens.QuickIcon)
            )
            Spacer(modifier = Modifier.height(HomeDimens.QuickGap))
            Text(
                text = title,
                fontSize = HomeDimens.QuickTitleSize,
                fontWeight = FontWeight.W600,
                color = Color(0xFF1D1D1F),
                letterSpacing = (-0.1).sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                lineHeight = HomeDimens.QuickTitleSize
            )
        }
    }
}
