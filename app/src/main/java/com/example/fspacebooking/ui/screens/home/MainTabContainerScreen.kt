package com.example.fspacebooking.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import com.example.fspacebooking.domain.model.ResourceType
import com.example.fspacebooking.ui.screens.booking.MyBookingsScreen
import com.example.fspacebooking.ui.screens.booking.ResourceSearchScreen
import com.example.fspacebooking.ui.viewmodels.AuthViewModel
import com.example.fspacebooking.ui.viewmodels.HomeViewModel
import com.example.fspacebooking.ui.viewmodels.MyBookingsViewModel
import com.example.fspacebooking.ui.viewmodels.SearchViewModel

@Composable
fun MainTabContainerScreen(
    homeViewModel: HomeViewModel,
    searchViewModel: SearchViewModel,
    myBookingsViewModel: MyBookingsViewModel,
    authViewModel: AuthViewModel,
    onNavigateToBooking: (String) -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTabIndex by rememberSaveable { mutableIntStateOf(0) }
    
    // For quick action tab transfers
    var searchInitialType by rememberSaveable { mutableStateOf<ResourceType?>(null) }

    val tabsList = listOf(
        TabItem("Trang chủ", Icons.Default.Home),
        TabItem("Đặt phòng", Icons.Default.Search),
        TabItem("Lịch trình", Icons.Default.CalendarToday),
        TabItem("Cá nhân", Icons.Default.Person)
    )

    val primaryColor = Color(0xFF2563EB)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                tabsList.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = activeTabIndex == index,
                        onClick = { activeTabIndex = index },
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label, fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = primaryColor,
                            selectedTextColor = primaryColor,
                            indicatorColor = primaryColor.copy(alpha = 0.1f),
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B)
                        )
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = activeTabIndex,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "MainTabTransition",
                modifier = Modifier.fillMaxSize()
            ) { targetIndex ->
                when (targetIndex) {
                    0 -> HomeScreen(
                        viewModel = homeViewModel,
                        onNavigateToSearch = { type ->
                            searchInitialType = type
                            activeTabIndex = 1 // Switch to Booking/Search tab
                        },
                        onNavigateToMyBookings = {
                            activeTabIndex = 2 // Switch to Schedule tab
                        },
                        onNavigateToAdmin = onNavigateToAdmin,
                        onNavigateToBooking = onNavigateToBooking,
                        onLogout = onLogout
                    )
                    1 -> ResourceSearchScreen(
                        viewModel = searchViewModel,
                        initialType = searchInitialType,
                        onNavigateBack = { activeTabIndex = 0 },
                        onResourceSelect = onNavigateToBooking
                    )
                    2 -> MyBookingsScreen(
                        viewModel = myBookingsViewModel,
                        onNavigateBack = { activeTabIndex = 0 }
                    )
                    3 -> ProfileScreen(
                        viewModel = authViewModel,
                        onNavigateToAdmin = onNavigateToAdmin,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

private data class TabItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
