package com.orbit.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.orbit.ui.theme.HomeStrings

@Composable
fun QuickActionsSection(
    onOrdersClick: () -> Unit,
    onInventoryClick: () -> Unit,
    onMapClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        QuickActionButton(
            title = HomeStrings.Orders,
            icon = Icons.Filled.ShoppingCart,
            iconColor = Color(0xFF2196F3),
            onClick = onOrdersClick,
            modifier = Modifier.weight(1f)
        )

        QuickActionButton(
            title = HomeStrings.Inventory,
            icon = Icons.Filled.Inventory,
            iconColor = Color(0xFFFF9800),
            onClick = onInventoryClick,
            modifier = Modifier.weight(1f)
        )

        QuickActionButton(
            title = HomeStrings.Map,
            icon = Icons.Filled.LocationOn,
            iconColor = Color(0xFF4CAF50),
            onClick = onMapClick,
            modifier = Modifier.weight(1f)
        )

        QuickActionButton(
            title = HomeStrings.Reports,
            icon = Icons.Filled.BarChart,
            iconColor = Color(0xFF9C27B0),
            onClick = { },
            modifier = Modifier.weight(1f)
        )
    }
}


