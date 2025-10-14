package com.orbit.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AppleCardElevation {
    NONE,
    SMALL,
    MEDIUM,
    LARGE
}

@Composable
fun AppleCard(
    modifier: Modifier = Modifier,
    elevation: AppleCardElevation = AppleCardElevation.MEDIUM,
    backgroundColor: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit
) {
    val elevationValue = when (elevation) {
        AppleCardElevation.NONE -> 0.dp
        AppleCardElevation.SMALL -> 4.dp
        AppleCardElevation.MEDIUM -> 8.dp
        AppleCardElevation.LARGE -> 16.dp
    }
    
    Card(
        modifier = modifier
            .shadow(
                elevation = elevationValue,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color(0xFF000000),
                ambientColor = Color(0xFF000000)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            content = content
        )
    }
}

@Composable
fun AppleSectionCard(
    title: String,
    modifier: Modifier = Modifier,
    elevation: AppleCardElevation = AppleCardElevation.MEDIUM,
    backgroundColor: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit
) {
    AppleCard(
        modifier = modifier,
        elevation = elevation,
        backgroundColor = backgroundColor
    ) {
        // Section Title
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.W600,
            color = Color(0xFF1D1D1F),
            letterSpacing = (-0.2).sp
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Section Content
        content()
    }
}

@Composable
fun AppleInfoCard(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    elevation: AppleCardElevation = AppleCardElevation.SMALL,
    backgroundColor: Color = Color.White,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = when (elevation) {
                    AppleCardElevation.NONE -> 0.dp
                    AppleCardElevation.SMALL -> 4.dp
                    AppleCardElevation.MEDIUM -> 8.dp
                    AppleCardElevation.LARGE -> 16.dp
                },
                shape = RoundedCornerShape(12.dp),
                spotColor = Color(0xFF000000),
                ambientColor = Color(0xFF000000)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        onClick = onClick ?: {}
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                color = Color(0xFF1D1D1F),
                letterSpacing = (-0.2).sp
            )
            
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    color = Color(0xFF8E8E93),
                    letterSpacing = (-0.1).sp
                )
            }
        }
    }
}
