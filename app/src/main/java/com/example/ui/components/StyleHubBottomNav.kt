package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.CustomerTab

@Composable
fun StyleHubBottomNav(
    currentTab: CustomerTab,
    onTabSelected: (CustomerTab) -> Unit,
    appointmentBadgeCount: Int = 0
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("stylehub_bottom_nav")
    ) {
        NavigationBarItem(
            selected = currentTab == CustomerTab.HOME,
            onClick = { onTabSelected(CustomerTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentTab == CustomerTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    "Discover",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == CustomerTab.HOME) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("tab_discover")
        )

        NavigationBarItem(
            selected = currentTab == CustomerTab.EXPLORE,
            onClick = { onTabSelected(CustomerTab.EXPLORE) },
            icon = {
                Icon(
                    imageVector = if (currentTab == CustomerTab.EXPLORE) Icons.Filled.Search else Icons.Outlined.Search,
                    contentDescription = "Explore",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    "Explore",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == CustomerTab.EXPLORE) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("tab_explore")
        )

        NavigationBarItem(
            selected = currentTab == CustomerTab.SAVED,
            onClick = { onTabSelected(CustomerTab.SAVED) },
            icon = {
                Icon(
                    imageVector = if (currentTab == CustomerTab.SAVED) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Saved",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    "Saved",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == CustomerTab.SAVED) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("tab_saved")
        )

        NavigationBarItem(
            selected = currentTab == CustomerTab.APPOINTMENTS,
            onClick = { onTabSelected(CustomerTab.APPOINTMENTS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (appointmentBadgeCount > 0) {
                            Badge(
                                containerColor = GoldPrimary
                            ) {
                                Text(appointmentBadgeCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentTab == CustomerTab.APPOINTMENTS) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                        contentDescription = "Appointments",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = {
                Text(
                    "Bookings",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == CustomerTab.APPOINTMENTS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("tab_appointments")
        )

        NavigationBarItem(
            selected = currentTab == CustomerTab.PROFILE,
            onClick = { onTabSelected(CustomerTab.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (currentTab == CustomerTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    "Profile",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == CustomerTab.PROFILE) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("tab_profile")
        )
    }
}
