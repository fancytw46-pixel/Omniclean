package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.BatteryScreen
import com.example.ui.screens.BoostScreen
import com.example.ui.screens.CleanScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DiagnosticsScreen
import com.example.ui.screens.SecurityScreen
import com.example.ui.screens.StorageScreen
import com.example.ui.screens.ToolboxScreen
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.OmniViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: OmniViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        OmniCleanApp(viewModel = viewModel)
      }
    }
  }
}

data class NavItem(
  val index: Int,
  val title: String,
  val icon: ImageVector,
  val tag: String
)

@Composable
fun OmniCleanApp(viewModel: OmniViewModel) {
  var selectedTab by remember { mutableIntStateOf(0) }

  // If on sub-tab, pressing system back returns to Dashboard
  BackHandler(enabled = selectedTab != 0) {
    selectedTab = 0
  }

  val navItems = listOf(
    NavItem(0, "Dashboard", Icons.Default.Dashboard, "nav_dashboard"),
    NavItem(1, "Clean", Icons.Default.DeleteSweep, "nav_clean"),
    NavItem(2, "Boost", Icons.Default.Memory, "nav_boost"),
    NavItem(3, "Battery", Icons.Default.BatteryChargingFull, "nav_battery"),
    NavItem(4, "Storage", Icons.Default.Storage, "nav_storage"),
    NavItem(5, "Security", Icons.Default.Security, "nav_security"),
    NavItem(6, "Diagnostics", Icons.Default.HealthAndSafety, "nav_diagnostics"),
    NavItem(7, "Toolbox", Icons.Default.Build, "nav_toolbox")
  )

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = CyberBackground,
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(CyberBackground)
          .statusBarsPadding()
      ) {
        // App Header Brand
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
          Column {
            Text(
              text = "OMNICLEAN PRO",
              fontSize = 18.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.5.sp,
              color = NeonCyan
            )
            Text(
              text = "System Optimizer & Hardware Suite",
              fontSize = 11.sp,
              color = TextSecondary
            )
          }
        }

        // Horizontal Scrollable Navigation Strip
        ScrollableTabRow(
          selectedTabIndex = selectedTab,
          containerColor = CyberSurface,
          contentColor = NeonCyan,
          edgePadding = 12.dp,
          indicator = { tabPositions ->
            if (selectedTab < tabPositions.size) {
              TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                color = NeonCyan,
                height = 3.dp
              )
            }
          },
          divider = {}
        ) {
          navItems.forEach { item ->
            val isSelected = selectedTab == item.index
            Tab(
              selected = isSelected,
              onClick = { selectedTab = item.index },
              modifier = Modifier.testTag(item.tag),
              text = {
                Text(
                  text = item.title,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) NeonCyan else TextSecondary
                )
              },
              icon = {
                Icon(
                  imageVector = item.icon,
                  contentDescription = item.title,
                  modifier = Modifier.size(18.dp),
                  tint = if (isSelected) NeonCyan else TextSecondary
                )
              }
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (selectedTab) {
        0 -> DashboardScreen(
          viewModel = viewModel,
          onNavigateToTab = { selectedTab = it }
        )
        1 -> CleanScreen(viewModel = viewModel)
        2 -> BoostScreen(viewModel = viewModel)
        3 -> BatteryScreen(viewModel = viewModel)
        4 -> StorageScreen(viewModel = viewModel)
        5 -> SecurityScreen(viewModel = viewModel)
        6 -> DiagnosticsScreen(viewModel = viewModel)
        7 -> ToolboxScreen(viewModel = viewModel)
      }
    }
  }
}
