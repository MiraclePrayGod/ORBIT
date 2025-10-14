package com.orbit.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import com.orbit.ui.theme.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateSelector(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val dates = remember {
        val today = LocalDate.now()
        val startOfWeek = today.minusDays(today.dayOfWeek.value - 1L) // Lunes de la semana actual
        (0..6).map { startOfWeek.plusDays(it.toLong()) } // Lunes a Domingo
    }
    
    val listState = rememberLazyListState()
    
    // Auto-scroll al final si es miércoles o más tarde
    LaunchedEffect(Unit) {
        val today = LocalDate.now()
        val dayOfWeek = today.dayOfWeek.value
        if (dayOfWeek >= 3) { // Miércoles = 3, Jueves = 4, etc.
            listState.animateScrollToItem(dates.size - 1)
        }
    }
    
    LazyRow(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .clipToBounds(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        items(dates) { date ->
            DateCard(
                date = date,
                isSelected = date == selectedDate,
                onClick = { onDateSelected(date) }
            )
        }
    }
}

@Composable
private fun DateCard(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dayOfWeek = date.format(DateTimeFormatter.ofPattern("EEE", java.util.Locale("es")))
    val dayOfMonth = date.dayOfMonth.toString()
    
    Card(
        modifier = Modifier
            .width(60.dp)
            .height(80.dp)
            .shadow(
                elevation = if (isSelected) 8.dp else 6.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color(0xFF000000),
                ambientColor = Color(0xFF000000)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF007AFF) else Color.White
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayOfWeek,
                fontSize = 12.sp,
                color = if (isSelected) Color.White else Color(0xFF8E8E93),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = dayOfMonth,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color(0xFF1D1D1F),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DateSelectorPreview() {
    DateSelector(
        selectedDate = LocalDate.now(),
        onDateSelected = {}
    )
}
