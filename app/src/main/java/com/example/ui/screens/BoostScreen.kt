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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.RamProcessItem
import com.example.ui.components.CyberCard
import com.example.ui.components.LinearUsageMetricBar
import com.example.ui.components.formatBytes
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCrimson
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.OmniViewModel

@Composable
fun BoostScreen(
  viewModel: OmniViewModel,
  modifier: Modifier = Modifier
) {
  val ramUsage by viewModel.ramUsage.collectAsStateWithLifecycle()
  val processes by viewModel.processes.collectAsStateWithLifecycle()
  val isBoosting by viewModel.isBoostingRam.collectAsStateWithLifecycle()
  val gameMode by viewModel.gameModeActive.collectAsStateWithLifecycle()
  val isCooling by viewModel.isEmergencyCooling.collectAsStateWithLifecycle()
  val batteryInfo by viewModel.batteryInfo.collectAsStateWithLifecycle()

  val (usedRam, totalRam) = ramUsage
  val ramRatio = if (totalRam > 0) usedRam.toFloat() / totalRam.toFloat() else 0f

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // RAM Overview & Booster Header
    item {
      CyberCard(
        borderColor = ElectricEmerald.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "REAL-TIME RAM ENGINE",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.2.sp,
              color = ElectricEmerald
            )
            Text(
              text = "${(ramRatio * 100).toInt()}% USED",
              fontSize = 32.sp,
              fontWeight = FontWeight.Black,
              color = TextPrimary,
              fontFamily = FontFamily.Monospace
            )
            Text(
              text = "${formatBytes(totalRam - usedRam)} free of ${formatBytes(totalRam)}",
              fontSize = 12.sp,
              color = TextSecondary
            )
          }

          IconButton(
            onClick = { viewModel.refreshProcesses() },
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(CyberSurfaceVariant)
          ) {
            Icon(Icons.Default.Refresh, contentDescription = "Refresh Processes", tint = ElectricEmerald)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LinearUsageMetricBar(
          title = "Active Memory Allocations",
          subtitle = "${processes.size} Running Tasks",
          fraction = ramRatio,
          accentColor = if (ramRatio > 0.8f) CyberCrimson else ElectricEmerald
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = { viewModel.boostRam() },
          enabled = !isBoosting,
          colors = ButtonDefaults.buttonColors(
            containerColor = ElectricEmerald,
            contentColor = Color(0xFF090D16)
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("boost_ram_action_button")
        ) {
          if (isBoosting) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFF090D16), strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("OPTIMIZING MEMORY PAGES...", fontWeight = FontWeight.Bold)
          } else {
            Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("BOOST RAM & KILL HOGS", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Performance Switches: Game Speed Booster & Emergency Cooldown
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CyberPurple.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Games, contentDescription = null, tint = CyberPurple, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Game Speed Booster", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
              Text("Allocates maximum GPU/CPU affinity and suppresses background services", fontSize = 11.sp, color = TextSecondary)
            }
          }
          Switch(
            checked = gameMode,
            onCheckedChange = { viewModel.toggleGameMode() },
            colors = SwitchDefaults.colors(
              checkedThumbColor = CyberPurple,
              checkedTrackColor = CyberPurple.copy(alpha = 0.3f)
            )
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CyberCrimson.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.AcUnit, contentDescription = null, tint = CyberCrimson, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Thermal Throttling Shield", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
              Text("Device Temp: ${batteryInfo.temperatureC}°C", fontSize = 11.sp, color = if (batteryInfo.temperatureC > 38f) CyberCrimson else TextSecondary)
            }
          }
          Button(
            onClick = { viewModel.emergencyCoolDown() },
            enabled = !isCooling,
            colors = ButtonDefaults.buttonColors(
              containerColor = CyberCrimson.copy(alpha = 0.2f),
              contentColor = CyberCrimson
            ),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text(if (isCooling) "COOLING..." else "COOL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Active Processes Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "ACTIVE PROCESSES & RAM CONSUMPTION",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.1.sp,
          color = TextSecondary
        )
        Text(
          text = "${processes.size} detected",
          fontSize = 11.sp,
          color = NeonCyan
        )
      }
    }

    // Process list
    items(processes) { proc ->
      ProcessRowItem(process = proc)
    }
  }
}

@Composable
fun ProcessRowItem(
  process: RamProcessItem,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(CyberSurface)
      .border(1.dp, CyberCardBorder.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
      .padding(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(CyberSurfaceVariant),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (process.isProtected) Icons.Default.Lock else Icons.Default.Memory,
            contentDescription = null,
            tint = if (process.isProtected) NeonCyan else TextSecondary,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = process.appName,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
          )
          Text(
            text = process.packageName,
            fontSize = 10.sp,
            color = TextSecondary,
            maxLines = 1
          )
        }
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = formatBytes(process.memoryBytes),
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = if (process.memoryBytes > 200L * 1024 * 1024) CyberAmber else ElectricEmerald,
          fontFamily = FontFamily.Monospace
        )
        Text(
          text = if (process.isProtected) "PROTECTED" else "KILLABLE",
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          color = if (process.isProtected) NeonCyan else TextSecondary
        )
      }
    }
  }
}
