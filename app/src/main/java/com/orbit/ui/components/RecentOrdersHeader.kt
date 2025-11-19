package com.orbit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.orbit.ui.theme.HomeStrings
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.ui.theme.HomeColors

@Composable
fun RecentOrdersHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = HomeStrings.RecentOrdersTitle,
            fontSize = 20.sp,
            fontWeight = FontWeight.W600,
            color = HomeColors.TextPrimary
        )

        IconButton(
            onClick = { /* Calendar filter functionality not implemented yet */ },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Filled.CalendarMonth,
                contentDescription = HomeStrings.Calendar,
                tint = HomeColors.Blue.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


