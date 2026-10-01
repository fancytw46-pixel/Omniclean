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
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.db.AutomationRuleEntity
import com.example.data.db.CleanLogEntity
import com.example.data.model.DnsBenchmarkItem
import com.example.ui.components.CyberCard
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
import com.example.ui.theme.TextTertiary
import com.example.viewmodel.OmniViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ToolboxScreen(
  viewModel: OmniViewModel,
  modifier: Modifier = Modifier
) {
  val dnsList by viewModel.dnsBenchmark.collectAsStateWithLifecycle()
  val isTestingNetwork by viewModel.isTestingNetwork.collectAsStateWithLifecycle()
  val pingMs by viewModel.pingMs.collectAsStateWithLifecycle()
  val downloadSpeed by viewModel.downloadMbps.collectAsStateWithLifecycle()
  val uploadSpeed by viewModel.uploadMbps.collectAsStateWithLifecycle()
  val cleanLogs by viewModel.cleanLogs.collectAsStateWithLifecycle()
  val automationRules by viewModel.automationRules.collectAsStateWithLifecycle()
  val specs = viewModel.deviceSpecs

  LaunchedEffect(Unit) {
    if (dnsList.isEmpty()) {
      viewModel.runDnsBenchmark()
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Network Speed Tester
    item {
      CyberCard(
        borderColor = NeonCyan.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(NeonCyan.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Speed, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("NETWORK SPEED TESTER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
              Text("Ping Latency & Bandwidth", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
          }

          Button(
            onClick = { viewModel.runNetworkSpeedTest() },
            enabled = !isTestingNetwork,
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF090D16)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("test_network_speed_button")
          ) {
            if (isTestingNetwork) {
              CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color(0xFF090D16), strokeWidth = 2.dp)
            } else {
              Text("TEST SPEED", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          SpeedMetricBox("PING", "$pingMs ms", NeonCyan, modifier = Modifier.weight(1f))
          SpeedMetricBox("DOWNLOAD", "${String.format(Locale.US, "%.1f", downloadSpeed)} Mbps", ElectricEmerald, modifier = Modifier.weight(1f))
          SpeedMetricBox("UPLOAD", "${String.format(Locale.US, "%.1f", uploadSpeed)} Mbps", CyberAmber, modifier = Modifier.weight(1f))
        }
      }
    }

    // DNS Benchmark & Switcher
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Dns, contentDescription = null, tint = CyberAmber)
          Spacer(modifier = Modifier.width(8.dp))
          Text("DNS SPEED BENCHMARK & SWITCHER", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyberAmber)
        }
        Spacer(modifier = Modifier.height(10.dp))

        dnsList.forEach { dns ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(dns.provider, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                if (dns.isRecommended) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("FASTEST", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ElectricEmerald)
                }
              }
              Text("${dns.primaryIp} • ${dns.secondaryIp}", fontSize = 10.sp, color = TextSecondary)
            }
            Text("${dns.pingMs} ms", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (dns.pingMs < 20) ElectricEmerald else TextSecondary, fontFamily = FontFamily.Monospace)
          }
        }
      }
    }

    // Automation Rules
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Schedule, contentDescription = null, tint = ElectricEmerald)
          Spacer(modifier = Modifier.width(8.dp))
          Text("SCHEDULED AUTO-CLEAN ENGINE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElectricEmerald)
        }
        Spacer(modifier = Modifier.height(10.dp))

        automationRules.forEach { rule ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(rule.ruleName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
              Text(rule.description, fontSize = 11.sp, color = TextSecondary)
            }
            Switch(
              checked = rule.isEnabled,
              onCheckedChange = { viewModel.toggleRule(rule) },
              colors = SwitchDefaults.colors(checkedThumbColor = ElectricEmerald, checkedTrackColor = ElectricEmerald.copy(alpha = 0.3f))
            )
          }
        }
      }
    }

    // Optimization History Log (Persisted in Room)
    item {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.History, contentDescription = null, tint = NeonCyan)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "OPTIMIZATION HISTORY LOG",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.1.sp,
          color = TextSecondary
        )
      }
    }

    if (cleanLogs.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberSurface)
            .padding(20.dp),
          contentAlignment = Alignment.Center
        ) {
          Text("No prior cleanup logs recorded yet.", fontSize = 12.sp, color = TextSecondary)
        }
      }
    } else {
      items(cleanLogs.take(8)) { log ->
        CleanLogRow(log = log)
      }
    }

    // Device Technical Specifications
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Info, contentDescription = null, tint = TextPrimary)
          Spacer(modifier = Modifier.width(8.dp))
          Text("DEVICE HARDWARE SPECIFICATIONS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        Spacer(modifier = Modifier.height(10.dp))

        SpecRow("Model & Manufacturer", "${specs.manufacturer} ${specs.model}")
        SpecRow("Operating System", "${specs.androidVersion} (API ${specs.apiLevel})")
        SpecRow("CPU Architecture", "${specs.cpuArch} (${specs.cpuCores} Cores)")
        SpecRow("Installed RAM", "${specs.totalRamMb} MB")
        SpecRow("Internal Storage", "${specs.totalStorageGb} GB")
        SpecRow("Display Resolution", specs.screenResolution)
        SpecRow("Build ID", specs.buildId)
      }
    }
  }
}

@Composable
fun SpeedMetricBox(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(CyberSurfaceVariant)
      .padding(10.dp)
  ) {
    Column {
      Text(title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
      Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color, fontFamily = FontFamily.Monospace)
    }
  }
}

@Composable
fun CleanLogRow(log: CleanLogEntity) {
  val dateStr = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(log.timestamp))
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(CyberSurface)
      .border(1.dp, CyberCardBorder.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
      .padding(10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(log.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(log.details, fontSize = 10.sp, color = TextSecondary)
        Text(dateStr, fontSize = 9.sp, color = TextTertiary)
      }
      Text(
        formatBytes(log.bytesCleaned),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = ElectricEmerald,
        fontFamily = FontFamily.Monospace
      )
    }
  }
}

@Composable
fun SpecRow(key: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(key, fontSize = 11.sp, color = TextSecondary)
    Text(value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
  }
}
