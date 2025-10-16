package com.orbit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.orbit.ui.map.PanamaMapScreen
import com.orbit.ui.screens.HomeScreen
import com.orbit.ui.screens.OrdersScreen
import com.orbit.ui.screens.InventoryScreen
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
                onAddOrderClick = {
                    navController.navigate("new_order")
                },
                onMapClick = {
                    navController.navigate("panama_map")
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
                },
                onMapClick = {
                    navController.navigate("panama_map")
                }
            )
        }
        
        composable("inventory") {
            InventoryScreen(
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
        composable("panama_map") {
            PanamaMapScreen()
        }

    }
}

