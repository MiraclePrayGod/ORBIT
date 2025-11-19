package com.orbit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.orbit.ui.screens.HomeScreen
import com.orbit.ui.screens.OrdersScreen
import com.orbit.ui.screens.InventoryScreen
import com.orbit.ui.screens.InventoryVisualScreen
import com.orbit.ui.screens.NewOrderScreen
import com.orbit.ui.screens.OrderDetailScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onOrdersClick = {
                    navController.navigate("orders")
                },
                onInventoryClick = {
                    navController.navigate("inventory")
                },
                onMapClick = {
                    // Map functionality not implemented yet
                },
                onAddOrderClick = {
                    navController.navigate("new_order")
                },
                onOrderClick = { orderId ->
                    navController.navigate("order_detail/$orderId")
                }
            )
        }
        
        composable("orders") {
            OrdersScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onOrderClick = { orderWithDetails ->
                    navController.navigate("order_detail/${orderWithDetails.order.id}")
                }
            )
        }
        
        composable("inventory") {
            InventoryScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onNewVisualClick = {
                    navController.navigate("inventory_visual")
                }
            )
        }
        
        composable("inventory_visual") {
            InventoryVisualScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("new_order") {
            NewOrderScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("order_detail/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")?.toLongOrNull() ?: 0L
            OrderDetailScreen(
                orderId = orderId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

