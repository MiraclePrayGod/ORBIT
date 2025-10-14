package com.orbit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AppleButtonVariant {
    PRIMARY,
    SECONDARY,
    SUCCESS,
    DESTRUCTIVE
}

enum class AppleButtonSize {
    SMALL,
    MEDIUM,
    LARGE
}

@Composable
fun AppleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppleButtonVariant = AppleButtonVariant.PRIMARY,
    size: AppleButtonSize = AppleButtonSize.MEDIUM,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    iconPosition: IconPosition = IconPosition.END
) {
    val backgroundColor = when (variant) {
        AppleButtonVariant.PRIMARY -> Color(0xFF007AFF)
        AppleButtonVariant.SECONDARY -> Color(0xFFF2F2F7)
        AppleButtonVariant.SUCCESS -> Color(0xFF34C759)
        AppleButtonVariant.DESTRUCTIVE -> Color(0xFFFF3B30)
    }
    
    val textColor = when (variant) {
        AppleButtonVariant.PRIMARY -> Color.White
        AppleButtonVariant.SECONDARY -> Color(0xFF007AFF)
        AppleButtonVariant.SUCCESS -> Color.White
        AppleButtonVariant.DESTRUCTIVE -> Color.White
    }
    
    val elevation = when (variant) {
        AppleButtonVariant.PRIMARY -> 4.dp
        AppleButtonVariant.SECONDARY -> 2.dp
        AppleButtonVariant.SUCCESS -> 4.dp
        AppleButtonVariant.DESTRUCTIVE -> 4.dp
    }
    
    val height = when (size) {
        AppleButtonSize.SMALL -> 36.dp
        AppleButtonSize.MEDIUM -> 48.dp
        AppleButtonSize.LARGE -> 56.dp
    }
    
    val fontSize = when (size) {
        AppleButtonSize.SMALL -> 14.sp
        AppleButtonSize.MEDIUM -> 16.sp
        AppleButtonSize.LARGE -> 18.sp
    }
    
    val horizontalPadding = when (size) {
        AppleButtonSize.SMALL -> 16.dp
        AppleButtonSize.MEDIUM -> 24.dp
        AppleButtonSize.LARGE -> 32.dp
    }
    
    val verticalPadding = when (size) {
        AppleButtonSize.SMALL -> 8.dp
        AppleButtonSize.MEDIUM -> 12.dp
        AppleButtonSize.LARGE -> 16.dp
    }
    
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(height)
            .shadow(
                elevation = if (enabled) elevation else 0.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color(0xFF000000),
                ambientColor = Color(0xFF000000)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) backgroundColor else Color(0xFFF2F2F7),
            contentColor = if (enabled) textColor else Color(0xFF8E8E93)
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null && iconPosition == IconPosition.START) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) textColor else Color(0xFF8E8E93),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = FontWeight.W600,
                color = if (enabled) textColor else Color(0xFF8E8E93),
                letterSpacing = (-0.3).sp
            )
            
            if (icon != null && iconPosition == IconPosition.END) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) textColor else Color(0xFF8E8E93),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

enum class IconPosition {
    START,
    END
}

@Composable
fun AppleOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: AppleButtonSize = AppleButtonSize.MEDIUM,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    val height = when (size) {
        AppleButtonSize.SMALL -> 36.dp
        AppleButtonSize.MEDIUM -> 48.dp
        AppleButtonSize.LARGE -> 56.dp
    }
    
    val fontSize = when (size) {
        AppleButtonSize.SMALL -> 14.sp
        AppleButtonSize.MEDIUM -> 16.sp
        AppleButtonSize.LARGE -> 18.sp
    }
    
    val horizontalPadding = when (size) {
        AppleButtonSize.SMALL -> 16.dp
        AppleButtonSize.MEDIUM -> 24.dp
        AppleButtonSize.LARGE -> 32.dp
    }
    
    val verticalPadding = when (size) {
        AppleButtonSize.SMALL -> 8.dp
        AppleButtonSize.MEDIUM -> 12.dp
        AppleButtonSize.LARGE -> 16.dp
    }
    
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(height)
            .shadow(
                elevation = if (enabled) 2.dp else 0.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color(0xFF000000),
                ambientColor = Color(0xFF000000)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = if (enabled) Color(0xFF007AFF) else Color(0xFF8E8E93)
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 1.dp,
            brush = if (enabled) 
                SolidColor(Color(0xFF007AFF)) 
            else 
                SolidColor(Color(0xFFE5E5EA))
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (enabled) Color.Transparent else Color(0xFFF2F2F7)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) Color(0xFF007AFF) else Color(0xFF8E8E93),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                Text(
                    text = text,
                    fontSize = fontSize,
                    fontWeight = FontWeight.W600,
                    color = if (enabled) Color(0xFF007AFF) else Color(0xFF8E8E93),
                    letterSpacing = (-0.3).sp
                )
            }
        }
    }
}
