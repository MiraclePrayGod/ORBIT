package com.orbit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.orbit.ui.components.DateSelector
import java.time.LocalDate
import com.orbit.ui.theme.cupertinoCardShadow

@Composable
fun DateSelectorCard(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .cupertinoCardShadow(cornerRadius = 16.dp)
            .graphicsLayer {
                shadowElevation = 0f
                shape = RoundedCornerShape(16.dp)
                clip = true
            }
            .border(
                width = 0.5.dp,
                color = Color(0x08000000),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        DateSelector(
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}


