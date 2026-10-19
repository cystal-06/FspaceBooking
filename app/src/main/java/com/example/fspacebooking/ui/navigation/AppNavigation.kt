package com.example.fspacebooking.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fspacebooking.domain.model.ResourceType
import com.example.fspacebooking.ui.screens.admin.AdminTabContainerScreen
import com.example.fspacebooking.ui.screens.admin.ResourceManagementScreen
import com.example.fspacebooking.ui.screens.admin.StatisticsScreen
import com.example.fspacebooking.ui.screens.auth.LoginScreen
import com.example.fspacebooking.ui.screens.auth.SplashScreen
import com.example.fspacebooking.ui.screens.booking.BookingScreen
import com.example.fspacebooking.ui.screens.booking.MyBookingsScreen
import com.example.fspacebooking.ui.screens.booking.ResourceSearchScreen
import com.example.fspacebooking.ui.screens.home.HomeScreen
import com.example.fspacebooking.ui.screens.home.MainTabContainerScreen
import com.example.fspacebooking.ui.viewmodels.AdminViewModel
import com.example.fspacebooking.ui.viewmodels.AuthViewModel
import com.example.fspacebooking.ui.viewmodels.BookingViewModel
import com.example.fspacebooking.ui.viewmodels.HomeViewModel
import com.example.fspacebooking.ui.viewmodels.MyBookingsViewModel
import com.example.fspacebooking.ui.viewmodels.SearchViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object Search : Screen("search?type={type}") {
        fun createRoute(type: ResourceType?) = if (type != null) "search?type=${type.name}" else "search"
    }
    object Booking : Screen("booking/{resourceId}") {
        fun createRoute(resourceId: String) = "booking/$resourceId"
    }
    object MyBookings : Screen("my_bookings")
    object AdminDashboard : Screen("admin_dashboard")
    object AdminCatalog : Screen("admin_catalog")
    object AdminStats : Screen("admin_stats")
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            SplashScreen(
                viewModel = authViewModel,
                onRedirectToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onRedirectToMain = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            val viewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val searchViewModel: SearchViewModel = hiltViewModel()
            val myBookingsViewModel: MyBookingsViewModel = hiltViewModel()
            val authViewModel: AuthViewModel = hiltViewModel()
            
            MainTabContainerScreen(
                homeViewModel = homeViewModel,
                searchViewModel = searchViewModel,
                myBookingsViewModel = myBookingsViewModel,
                authViewModel = authViewModel,
                onNavigateToBooking = { resourceId ->
                    navController.navigate(Screen.Booking.createRoute(resourceId))
                },
                onNavigateToAdmin = {
                    navController.navigate(Screen.AdminDashboard.route)
                },
                onLogout = {
                    authViewModel.logout {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(
            route = Screen.Search.route,
            arguments = listOf(
                navArgument("type") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val typeStr = backStackEntry.arguments?.getString("type")
            val initialType = typeStr?.let { ResourceType.valueOf(it) }
            val viewModel: SearchViewModel = hiltViewModel()
            
            ResourceSearchScreen(
                viewModel = viewModel,
                initialType = initialType,
                onNavigateBack = { navController.popBackStack() },
                onResourceSelect = { resourceId ->
                    navController.navigate(Screen.Booking.createRoute(resourceId))
                }
            )
        }

        composable(
            route = Screen.Booking.route,
            arguments = listOf(
                navArgument("resourceId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val resourceId = backStackEntry.arguments?.getString("resourceId") ?: ""
            val viewModel: BookingViewModel = hiltViewModel()
            
            BookingScreen(
                viewModel = viewModel,
                resourceId = resourceId,
                onNavigateBack = { navController.popBackStack() },
                onBookingSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.MyBookings.route) {
            val viewModel: MyBookingsViewModel = hiltViewModel()
            MyBookingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminDashboard.route) {
            val viewModel: AdminViewModel = hiltViewModel()
            AdminTabContainerScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AdminCatalog.route) {
            val viewModel: AdminViewModel = hiltViewModel()
            ResourceManagementScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminStats.route) {
            val viewModel: AdminViewModel = hiltViewModel()
            StatisticsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
