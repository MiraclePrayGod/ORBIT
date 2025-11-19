package com.orbit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.ui.theme.HomeColors
import com.orbit.ui.theme.HomeDimens

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(HomeDimens.SummaryHeight)
            .shadow(
                elevation = 7.dp,
                shape = RoundedCornerShape(HomeDimens.SummaryCorner),
                spotColor = Color(0xFF000000),
                ambientColor = Color(0xFF000000)
            ),
        shape = RoundedCornerShape(HomeDimens.SummaryCorner),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                    text = value,
                    fontSize = HomeDimens.SummaryValueSize,
                    fontWeight = FontWeight.W600,
                    color = HomeColors.TextPrimary,
                    letterSpacing = (-0.6).sp
                )
            }

            Text(
                text = title,
                fontSize = HomeDimens.SummaryTitleSize,
                fontWeight = FontWeight.W600,
                color = HomeColors.TextSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                letterSpacing = (-0.2).sp
            )
        }
    }
}
