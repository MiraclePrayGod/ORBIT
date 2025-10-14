package com.orbit.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun ResponsiveContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp

    val horizontalPadding = when {
        screenWidthDp >= 1200 -> 48.dp
        screenWidthDp >= 840 -> 32.dp
        screenWidthDp >= 600 -> 24.dp
        else -> 16.dp
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        content()
    }
}


