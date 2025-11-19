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
import com.orbit.ui.theme.HomeColors

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
            iconColor = HomeColors.Blue,
            onClick = onOrdersClick,
            modifier = Modifier.weight(1f)
        )

        QuickActionButton(
            title = HomeStrings.Inventory,
            icon = Icons.Filled.Inventory,
            iconColor = HomeColors.Orange,
            onClick = onInventoryClick,
            modifier = Modifier.weight(1f)
        )

        QuickActionButton(
            title = HomeStrings.Map,
            icon = Icons.Filled.LocationOn,
            iconColor = HomeColors.Green,
            onClick = onMapClick,
            modifier = Modifier.weight(1f)
        )

        QuickActionButton(
            title = HomeStrings.Reports,
            icon = Icons.Filled.BarChart,
            iconColor = HomeColors.Purple,
            onClick = { /* Reports functionality not implemented yet */ },
            modifier = Modifier.weight(1f)
        )
    }
}


