package com.orbit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.orbit.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Composable
fun DatePickerDialog(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentDate by remember { mutableStateOf(selectedDate ?: LocalDate.now()) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Seleccionar Fecha",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = TextSecondary
                        )
                    }
                }
                
                // Month Navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            currentDate = currentDate.minusMonths(1)
                        }
                    ) {
                        Icon(
                            Icons.Default.ChevronLeft,
                            contentDescription = "Mes anterior",
                            tint = ButtonPrimary
                        )
                    }
                    
                    Text(
                        text = currentDate.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es"))),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    
                    IconButton(
                        onClick = {
                            currentDate = currentDate.plusMonths(1)
                        }
                    ) {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = "Mes siguiente",
                            tint = ButtonPrimary
                        )
                    }
                }
                
                // Days of week header
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("L", "M", "X", "J", "V", "S", "D").forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    }
                }
                
                // Calendar grid
                CalendarGrid(
                    currentDate = currentDate,
                    selectedDate = selectedDate,
                    onDateSelected = onDateSelected
                )
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextSecondary
                        )
                    ) {
                        Text("Cancelar")
                    }
                    
                    Button(
                        onClick = {
                            onDateSelected(currentDate)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ButtonPrimary
                        )
                    ) {
                        Text(
                            text = "Seleccionar",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarGrid(
    currentDate: LocalDate,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDayOfMonth = currentDate.withDayOfMonth(1)
    val lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth())
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Convert to 0-6 (Monday = 0)
    
    val daysInMonth = currentDate.lengthOfMonth()
    val totalCells = firstDayOfWeek + daysInMonth
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.height(200.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(totalCells) { index ->
            if (index < firstDayOfWeek) {
                // Empty cells before first day
                Box(modifier = Modifier.size(32.dp))
            } else {
                val day = index - firstDayOfWeek + 1
                val date = currentDate.withDayOfMonth(day)
                val isSelected = selectedDate?.isEqual(date) == true
                val isToday = date.isEqual(LocalDate.now())
                
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                isSelected -> ButtonPrimary
                                isToday -> ButtonPrimary.copy(alpha = 0.2f)
                                else -> Color.Transparent
                            }
                        )
                        .clickable { onDateSelected(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                        color = when {
                            isSelected -> Color.White
                            isToday -> ButtonPrimary
                            else -> TextPrimary
                        }
                    )
                }
            }
        }
    }
}
