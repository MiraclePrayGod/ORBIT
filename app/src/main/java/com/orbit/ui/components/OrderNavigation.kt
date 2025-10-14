package com.orbit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.ui.theme.*

@Composable
fun OrderTabNavigation(
    steps: List<String>,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFF000000),
                ambientColor = Color(0xFF000000)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            steps.forEachIndexed { index, step ->
                val isSelected = pagerState.currentPage == index
                val isCompleted = pagerState.currentPage > index
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    // Número del paso con diseño Apple
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> Color(0xFF007AFF)
                                    isCompleted -> Color(0xFF34C759)
                                    else -> Color(0xFFF2F2F7)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600,
                            color = when {
                                isSelected -> Color.White
                                isCompleted -> Color.White
                                else -> Color(0xFF8E8E93)
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Texto del paso con tipografía Apple
                    Text(
                        text = step,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.W500,
                        color = when {
                            isSelected -> Color(0xFF007AFF)
                            isCompleted -> Color(0xFF34C759)
                            else -> Color(0xFF8E8E93)
                        },
                        textAlign = TextAlign.Center,
                        letterSpacing = (-0.1).sp
                    )
                }
            }
        }
    }
}
