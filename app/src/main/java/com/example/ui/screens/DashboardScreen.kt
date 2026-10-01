package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CircularHudHealthGauge
import com.example.ui.components.CyberCard
import com.example.ui.components.LinearUsageMetricBar
import com.example.ui.components.formatBytes
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCrimson
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.OmniViewModel

@Composable
fun DashboardScreen(
  viewModel: OmniViewModel,
  onNavigateToTab: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val ramUsage by viewModel.ramUsage.collectAsStateWithLifecycle()
  val storageInfo by viewModel.storageInfo.collectAsStateWithLifecycle()
  val batteryInfo by viewModel.batteryInfo.collectAsStateWithLifecycle()
  val junkItems by viewModel.junkItems.collectAsStateWithLifecycle()
  val totalCleanedRoom by viewModel.totalCleanedBytes.collectAsStateWithLifecycle()
  val isCleaning by viewModel.isCleaningJunk.collectAsStateWithLifecycle()
  val isBoosting by viewModel.isBoostingRam.collectAsStateWithLifecycle()
  val isCooling by viewModel.isEmergencyCooling.collectAsStateWithLifecycle()

  val healthScore = viewModel.calculateDeviceHealthScore()
  val pendingJunkBytes = junkItems.filter { it.isSelected }.sumOf { it.sizeBytes }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Health HUD Card
    item {
      CyberCard(
        borderColor = NeonCyan.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          CircularHudHealthGauge(score = healthScore)

          Spacer(modifier = Modifier.height(16.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = { viewModel.executeDeepClean() },
              enabled = !isCleaning,
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("one_touch_deep_clean_button"),
              colors = ButtonDefaults.buttonColors(
                containerColor = NeonCyan,
                contentColor = Color(0xFF090D16)
              ),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CleaningServices,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isCleaning) "CLEANING..." else "ONE-TOUCH CLEAN",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }

            Button(
              onClick = { viewModel.boostRam() },
              enabled = !isBoosting,
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("one_tap_ram_boost_button"),
              colors = ButtonDefaults.buttonColors(
                containerColor = ElectricEmerald,
                contentColor = Color(0xFF090D16)
              ),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Speed,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isBoosting) "BOOSTING..." else "ONE-TAP BOOST",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }

    // Real-Time System Metrics Overview
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "SYSTEM TELEMETRY",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.2.sp,
          color = NeonCyan
        )
        Spacer(modifier = Modifier.height(12.dp))

        // RAM Bar
        val (usedRam, totalRam) = ramUsage
        val ramFraction = if (totalRam > 0) usedRam.toFloat() / totalRam.toFloat() else 0f
        LinearUsageMetricBar(
          title = "Active RAM Load",
          subtitle = "${formatBytes(usedRam)} / ${formatBytes(totalRam)} (${(ramFraction * 100).toInt()}%)",
          fraction = ramFraction,
          accentColor = if (ramFraction > 0.8f) CyberCrimson else ElectricEmerald
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Storage Bar
        val storageFraction = if (storageInfo.totalBytes > 0) {
          storageInfo.usedBytes.toFloat() / storageInfo.totalBytes.toFloat()
        } else 0f
        LinearUsageMetricBar(
          title = "Internal Storage",
          subtitle = "${formatBytes(storageInfo.usedBytes)} / ${formatBytes(storageInfo.totalBytes)} (${(storageFraction * 100).toInt()}%)",
          fraction = storageFraction,
          accentColor = if (storageFraction > 0.85f) CyberCrimson else NeonCyan
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Battery & Temperature Badges
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(CyberSurfaceVariant)
              .padding(10.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.BatteryChargingFull,
                contentDescription = null,
                tint = ElectricEmerald,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text("Battery", fontSize = 11.sp, color = TextSecondary)
                Text(
                  "${batteryInfo.level}% (${batteryInfo.statusText})",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimary
                )
              }
            }
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(CyberSurfaceVariant)
              .padding(10.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.AcUnit,
                contentDescription = null,
                tint = if (batteryInfo.temperatureC > 38f) CyberCrimson else NeonCyan,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text("CPU Thermal", fontSize = 11.sp, color = TextSecondary)
                Text(
                  "${String.format(java.util.Locale.US, "%.1f", batteryInfo.temperatureC)}°C",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (batteryInfo.temperatureC > 38f) CyberCrimson else TextPrimary,
                  fontFamily = FontFamily.Monospace
                )
              }
            }
          }
        }
      }
    }

    // Quick Modules Navigation Grid
    item {
      Text(
        text = "OPTIMIZATION MODULES",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color = TextSecondary
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        QuickModuleButton(
          title = "Junk Clean",
          subtitle = formatBytes(pendingJunkBytes),
          icon = Icons.Default.DeleteSweep,
          color = NeonCyan,
          onClick = { onNavigateToTab(1) },
          modifier = Modifier.weight(1f)
        )
        QuickModuleButton(
          title = "RAM Boost",
          subtitle = "Kill hogs",
          icon = Icons.Default.Memory,
          color = ElectricEmerald,
          onClick = { onNavigateToTab(2) },
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        QuickModuleButton(
          title = "Battery",
          subtitle = "${batteryInfo.estimatedMinutesRemaining}m left",
          icon = Icons.Default.BatteryChargingFull,
          color = CyberAmber,
          onClick = { onNavigateToTab(3) },
          modifier = Modifier.weight(1f)
        )
        QuickModuleButton(
          title = "Storage",
          subtitle = formatBytes(storageInfo.freeBytes) + " free",
          icon = Icons.Default.Storage,
          color = NeonCyan,
          onClick = { onNavigateToTab(4) },
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        QuickModuleButton(
          title = "Security Guard",
          subtitle = "Shield active",
          icon = Icons.Default.Security,
          color = ElectricEmerald,
          onClick = { onNavigateToTab(5) },
          modifier = Modifier.weight(1f)
        )
        QuickModuleButton(
          title = "Diagnostics",
          subtitle = "Hardware tests",
          icon = Icons.Default.HealthAndSafety,
          color = CyberCrimson,
          onClick = { onNavigateToTab(6) },
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Emergency Thermal Cool Down banner
    item {
      CyberCard(
        borderColor = CyberCrimson.copy(alpha = 0.4f),
        backgroundColor = CyberSurface
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "EMERGENCY COOL DOWN",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = CyberCrimson
            )
            Text(
              text = "Terminates CPU-intensive threads and drops thermal throttle threshold immediately.",
              fontSize = 11.sp,
              color = TextSecondary
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = { viewModel.emergencyCoolDown() },
            enabled = !isCooling,
            colors = ButtonDefaults.buttonColors(
              containerColor = CyberCrimson.copy(alpha = 0.2f),
              contentColor = CyberCrimson
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("emergency_cooldown_button")
          ) {
            Text(if (isCooling) "COOLING..." else "COOL DOWN", fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }
      }
    }

    // Lifetime Space Recovered from Room
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "LIFETIME RECOVERED STORAGE",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = TextSecondary
            )
            Text(
              text = formatBytes(totalCleanedRoom ?: 0L),
              fontSize = 24.sp,
              fontWeight = FontWeight.Black,
              color = ElectricEmerald,
              fontFamily = FontFamily.Monospace
            )
          }
          Icon(
            imageVector = Icons.Default.Build,
            contentDescription = null,
            tint = NeonCyan,
            modifier = Modifier
              .size(36.dp)
              .clickable { onNavigateToTab(7) }
          )
        }
      }
    }
  }
}

@Composable
fun QuickModuleButton(
  title: String,
  subtitle: String,
  icon: ImageVector,
  color: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(CyberSurface)
      .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .padding(14.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = color,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = title,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
        Text(
          text = subtitle,
          fontSize = 11.sp,
          color = TextSecondary
        )
      }
    }
  }
}
