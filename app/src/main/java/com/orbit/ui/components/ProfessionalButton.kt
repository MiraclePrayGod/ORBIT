package com.orbit.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.orbit.ui.theme.*

@Composable
fun ProfessionalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Primary,
    size: ButtonSize = ButtonSize.Large,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    fullWidth: Boolean = false
) {
    val (backgroundColor, contentColor, borderColor) = when (variant) {
        ButtonVariant.Primary -> Triple(
            if (enabled) ButtonPrimary else ButtonDisabled,
            TextInverse,
            Color.Transparent
        )
        ButtonVariant.Secondary -> Triple(
            ButtonSecondary,
            TextPrimary,
            BorderLight
        )
        ButtonVariant.Outlined -> Triple(
            Color.Transparent,
            ButtonPrimary,
            ButtonPrimary
        )
        ButtonVariant.Text -> Triple(
            Color.Transparent,
            ButtonPrimary,
            Color.Transparent
        )
        ButtonVariant.Success -> Triple(
            StatusSuccess,
            TextInverse,
            Color.Transparent
        )
        ButtonVariant.Warning -> Triple(
            StatusWarning,
            TextInverse,
            Color.Transparent
        )
        ButtonVariant.Error -> Triple(
            StatusError,
            TextInverse,
            Color.Transparent
        )
    }
    
    data class ButtonMetrics(
        val height: androidx.compose.ui.unit.Dp,
        val textStyle: androidx.compose.ui.text.TextStyle,
        val iconSize: androidx.compose.ui.unit.Dp,
        val horizontalPadding: androidx.compose.ui.unit.Dp
    )

    val metrics: ButtonMetrics = when (size) {
        ButtonSize.Small -> ButtonMetrics(32.dp, ButtonTextSmall, 16.dp, 8.dp)
        ButtonSize.Medium -> ButtonMetrics(40.dp, ButtonTextMedium, 18.dp, 12.dp)
        ButtonSize.Large -> ButtonMetrics(48.dp, ButtonTextLarge, 20.dp, 16.dp)
        ButtonSize.ExtraLarge -> ButtonMetrics(56.dp, ButtonTextLarge, 24.dp, 20.dp)
    }
    
    val buttonModifier = if (fullWidth) {
        modifier.fillMaxWidth()
    } else {
        modifier
    }
    
    if (variant == ButtonVariant.Outlined) {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled && !loading,
            modifier = buttonModifier.height(metrics.height),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = contentColor,
                disabledContentColor = TextTertiary
            ),
            border = BorderStroke(1.dp, ButtonPrimary)
        ) {
            ButtonContent(
                text = text,
                textStyle = metrics.textStyle,
                contentColor = contentColor,
                loading = loading,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                iconSize = metrics.iconSize,
                padding = metrics.horizontalPadding
            )
        }
    } else {
        Button(
            onClick = onClick,
            enabled = enabled && !loading,
            modifier = buttonModifier.height(metrics.height),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = contentColor,
                disabledContainerColor = ButtonDisabled,
                disabledContentColor = TextTertiary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = if (variant == ButtonVariant.Primary) 2.dp else 0.dp,
                pressedElevation = if (variant == ButtonVariant.Primary) 4.dp else 0.dp
            )
        ) {
            ButtonContent(
                text = text,
                textStyle = metrics.textStyle,
                contentColor = contentColor,
                loading = loading,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                iconSize = metrics.iconSize,
                padding = metrics.horizontalPadding
            )
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    textStyle: androidx.compose.ui.text.TextStyle,
    contentColor: Color,
    loading: Boolean,
    leadingIcon: ImageVector?,
    trailingIcon: ImageVector?,
    iconSize: androidx.compose.ui.unit.Dp,
    padding: androidx.compose.ui.unit.Dp
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = padding)
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = contentColor,
                strokeWidth = 2.dp,
                modifier = Modifier.size(iconSize)
            )
            if (text.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        } else {
            leadingIcon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(iconSize)
                )
                if (text.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
        
        if (text.isNotEmpty()) {
            Text(
                text = text,
                style = textStyle,
                color = contentColor,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        if (!loading) {
            trailingIcon?.let { icon ->
                if (text.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

enum class ButtonVariant {
    Primary,
    Secondary,
    Outlined,
    Text,
    Success,
    Warning,
    Error
}

enum class ButtonSize {
    Small,
    Medium,
    Large,
    ExtraLarge
}

@Composable
fun IconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Int = 24,
    contentDescription: String? = null,
    tint: Color = TextPrimary
) {
    androidx.compose.material3.IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(size.dp)
        )
    }
}

@Composable
fun FloatingActionButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    backgroundColor: Color = ButtonPrimary,
    contentColor: Color = TextInverse
) {
    androidx.compose.material3.FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = backgroundColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp)
        )
    }
}
