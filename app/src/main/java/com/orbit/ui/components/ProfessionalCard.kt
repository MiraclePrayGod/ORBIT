package com.orbit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.ui.theme.*

@Composable
fun ProfessionalCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    content: @Composable () -> Unit = {},
    onClick: (() -> Unit)? = null,
    variant: CardVariant = CardVariant.Default,
    elevation: CardElevation = CardElevation.Medium,
    showArrow: Boolean = false
) {
    val (backgroundColor, borderColor, borderWidth) = when (variant) {
        CardVariant.Default -> Triple(SurfacePrimary, Color.Transparent, 0.dp)
        CardVariant.Outlined -> Triple(SurfacePrimary, BorderLight, 1.dp)
        CardVariant.Elevated -> Triple(SurfacePrimary, Color.Transparent, 0.dp)
        CardVariant.Secondary -> Triple(SurfaceSecondary, Color.Transparent, 0.dp)
        CardVariant.Success -> Triple(StatusSuccess.copy(alpha = 0.1f), StatusSuccess, 1.dp)
        CardVariant.Warning -> Triple(StatusWarning.copy(alpha = 0.1f), StatusWarning, 1.dp)
        CardVariant.Error -> Triple(StatusError.copy(alpha = 0.1f), StatusError, 1.dp)
    }
    
    val cardElevation = when (elevation) {
        CardElevation.None -> 0.dp
        CardElevation.Small -> 2.dp
        CardElevation.Medium -> 4.dp
        CardElevation.Large -> 8.dp
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
        border = if (borderWidth > 0.dp) {
            androidx.compose.foundation.BorderStroke(borderWidth, borderColor)
        } else null
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = CardTitle,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    subtitle?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it,
                            style = CardSubtitle,
                            color = TextSecondary
                        )
                    }
                }
                
                if (showArrow && onClick != null) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Ver más",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // Content
            if (content != null) {
                Spacer(modifier = Modifier.height(16.dp))
                content()
            }
        }
    }
}

@Composable
fun IconCard(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    iconColor: Color = ButtonPrimary,
    iconBackgroundColor: Color = ButtonPrimary.copy(alpha = 0.1f)
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = CardTitle,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                subtitle?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it,
                        style = CardSubtitle,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun StatusCard(
    title: String,
    value: String,
    status: CardStatus,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null
) {
    val (backgroundColor, borderColor, valueColor) = when (status) {
        CardStatus.Success -> Triple(
            StatusSuccess.copy(alpha = 0.1f),
            StatusSuccess,
            StatusSuccess
        )
        CardStatus.Warning -> Triple(
            StatusWarning.copy(alpha = 0.1f),
            StatusWarning,
            StatusWarning
        )
        CardStatus.Error -> Triple(
            StatusError.copy(alpha = 0.1f),
            StatusError,
            StatusError
        )
        CardStatus.Info -> Triple(
            StatusInfo.copy(alpha = 0.1f),
            StatusInfo,
            StatusInfo
        )
        CardStatus.Neutral -> Triple(
            SurfaceSecondary,
            BorderLight,
            TextPrimary
        )
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                style = CardSubtitle,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = CardTitle.copy(fontSize = 24.sp),
                color = valueColor,
                fontWeight = FontWeight.Bold
            )
            
            subtitle?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    style = CardSubtitle,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun ListItemCard(
    title: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = CardTitle,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                subtitle?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it,
                        style = CardSubtitle,
                        color = TextSecondary
                    )
                }
            }
            
            trailing?.invoke()
        }
    }
}

enum class CardVariant {
    Default,
    Outlined,
    Elevated,
    Secondary,
    Success,
    Warning,
    Error
}

enum class CardElevation {
    None,
    Small,
    Medium,
    Large
}

enum class CardStatus {
    Success,
    Warning,
    Error,
    Info,
    Neutral
}
