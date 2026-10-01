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
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
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
import com.example.data.model.PowerProfileType
import com.example.ui.components.CyberCard
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
fun BatteryScreen(
  viewModel: OmniViewModel,
  modifier: Modifier = Modifier
) {
  val batteryInfo by viewModel.batteryInfo.collectAsStateWithLifecycle()
  val activeProfile by viewModel.activePowerProfile.collectAsStateWithLifecycle()
  val overchargeAlert by viewModel.overchargeProtection.collectAsStateWithLifecycle()
  val nightPower by viewModel.nightPowerMode.collectAsStateWithLifecycle()
  val fastCharge by viewModel.fastChargeOptimizer.collectAsStateWithLifecycle()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Battery Status Core Card
    item {
      CyberCard(
        borderColor = CyberAmber.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "BATTERY TELEMETRY",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.2.sp,
              color = CyberAmber
            )
            Text(
              text = "${batteryInfo.level}%",
              fontSize = 38.sp,
              fontWeight = FontWeight.Black,
              color = TextPrimary,
              fontFamily = FontFamily.Monospace
            )
            Text(
              text = "${batteryInfo.statusText} • ${batteryInfo.chargeType}",
              fontSize = 12.sp,
              color = TextSecondary
            )
          }

          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(CircleShape)
              .background(CyberAmber.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.BatteryChargingFull,
              contentDescription = null,
              tint = CyberAmber,
              modifier = Modifier.size(30.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid of 4 stats: Health, Temp, Voltage, Discharge
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          StatChip(title = "Health", value = batteryInfo.healthText, modifier = Modifier.weight(1f))
          StatChip(
            title = "Temperature",
            value = "${String.format(java.util.Locale.US, "%.1f", batteryInfo.temperatureC)}°C",
            color = if (batteryInfo.temperatureC > 38f) CyberCrimson else ElectricEmerald,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          StatChip(title = "Voltage", value = "${batteryInfo.voltageMv} mV", modifier = Modifier.weight(1f))
          StatChip(
            title = "Discharge",
            value = "${batteryInfo.dischargeRateMa} mA",
            color = NeonCyan,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Cycle Count: ${batteryInfo.cycleCount} | Wear Degradation: ${batteryInfo.degradationPercent}%",
          fontSize = 11.sp,
          color = TextSecondary,
          fontWeight = FontWeight.Medium
        )
      }
    }

    // Power Profiles Selector
    item {
      Text(
        text = "INTELLIGENT POWER PROFILES",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.1.sp,
        color = TextSecondary
      )
      Spacer(modifier = Modifier.height(4.dp))

      PowerProfileType.values().forEach { profile ->
        val isSelected = activeProfile == profile
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) CyberSurfaceVariant else CyberSurface)
            .border(
              width = if (isSelected) 1.5.dp else 1.dp,
              color = if (isSelected) CyberAmber else CyberCardBorder.copy(alpha = 0.3f),
              shape = RoundedCornerShape(12.dp)
            )
            .clickable { viewModel.setPowerProfile(profile) }
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = profile.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) CyberAmber else TextPrimary
              )
              Text(
                text = profile.desc,
                fontSize = 11.sp,
                color = TextSecondary
              )
              Text(
                text = "CPU Cap: ${profile.cpuCap} | Display: ${profile.displayHz}",
                fontSize = 10.sp,
                color = NeonCyan,
                fontWeight = FontWeight.SemiBold
              )
            }
            if (isSelected) {
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(CyberAmber),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF090D16), modifier = Modifier.size(16.dp))
              }
            }
          }
        }
      }
    }

    // Power Saving Automations
    item {
      Text(
        text = "BATTERY PROTECTION SUITE",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.1.sp,
        color = TextSecondary
      )
      Spacer(modifier = Modifier.height(4.dp))

      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Overcharging Protection Alert", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("Chimes alarm at 80% to protect lithium-ion cycle lifespan", fontSize = 11.sp, color = TextSecondary)
          }
          Switch(
            checked = overchargeAlert,
            onCheckedChange = { viewModel.toggleOvercharge() },
            colors = SwitchDefaults.colors(checkedThumbColor = CyberAmber, checkedTrackColor = CyberAmber.copy(alpha = 0.3f))
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Automated Night Power Mode", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("Deep sleep governor between 11:00 PM and 7:00 AM", fontSize = 11.sp, color = TextSecondary)
          }
          Switch(
            checked = nightPower,
            onCheckedChange = { viewModel.toggleNightPower() },
            colors = SwitchDefaults.colors(checkedThumbColor = CyberAmber, checkedTrackColor = CyberAmber.copy(alpha = 0.3f))
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Fast Charge Optimizer", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("Temporarily suspends background network sync while plugged in", fontSize = 11.sp, color = TextSecondary)
          }
          Switch(
            checked = fastCharge,
            onCheckedChange = { viewModel.toggleFastCharge() },
            colors = SwitchDefaults.colors(checkedThumbColor = CyberAmber, checkedTrackColor = CyberAmber.copy(alpha = 0.3f))
          )
        }
      }
    }
  }
}

@Composable
fun StatChip(
  title: String,
  value: String,
  color: Color = TextPrimary,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(CyberSurfaceVariant)
      .padding(10.dp)
  ) {
    Column {
      Text(text = title, fontSize = 10.sp, color = TextSecondary)
      Text(
        text = value,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        fontFamily = FontFamily.Monospace
      )
    }
  }
}
