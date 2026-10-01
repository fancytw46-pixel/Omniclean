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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.example.data.model.MediaCleanItem
import com.example.ui.components.CyberCard
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
fun StorageScreen(
  viewModel: OmniViewModel,
  modifier: Modifier = Modifier
) {
  val storageInfo by viewModel.storageInfo.collectAsStateWithLifecycle()
  val mediaItems by viewModel.mediaItems.collectAsStateWithLifecycle()
  val storageSpeed by viewModel.storageSpeedMb.collectAsStateWithLifecycle()
  val isTestingSpeed by viewModel.isTestingStorage.collectAsStateWithLifecycle()

  val totalSelectedMedia = mediaItems.filter { it.isSelected }.sumOf { it.sizeBytes }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Storage Treemap Breakdown Card
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
          Column {
            Text(
              text = "STORAGE VISUALIZER",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.2.sp,
              color = NeonCyan
            )
            Text(
              text = "${formatBytes(storageInfo.usedBytes)} USED",
              fontSize = 28.sp,
              fontWeight = FontWeight.Black,
              color = TextPrimary,
              fontFamily = FontFamily.Monospace
            )
            Text(
              text = "${formatBytes(storageInfo.freeBytes)} available of ${formatBytes(storageInfo.totalBytes)}",
              fontSize = 12.sp,
              color = TextSecondary
            )
          }

          Icon(
            imageVector = Icons.Default.Storage,
            contentDescription = null,
            tint = NeonCyan,
            modifier = Modifier.size(40.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Treemap Segmented Bar
        val total = storageInfo.totalBytes.toFloat()
        val appsFrac = (storageInfo.appsBytes / total).coerceIn(0f, 1f)
        val mediaFrac = (storageInfo.mediaBytes / total).coerceIn(0f, 1f)
        val systemFrac = (storageInfo.systemBytes / total).coerceIn(0f, 1f)
        val cacheFrac = (storageInfo.cacheBytes / total).coerceIn(0f, 1f)

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(RoundedCornerShape(8.dp))
        ) {
          Box(modifier = Modifier.weight(appsFrac.coerceAtLeast(0.01f)).fillMaxSize().background(NeonCyan))
          Box(modifier = Modifier.weight(mediaFrac.coerceAtLeast(0.01f)).fillMaxSize().background(CyberPurple))
          Box(modifier = Modifier.weight(systemFrac.coerceAtLeast(0.01f)).fillMaxSize().background(CyberAmber))
          Box(modifier = Modifier.weight(cacheFrac.coerceAtLeast(0.01f)).fillMaxSize().background(ElectricEmerald))
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Legend
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          StorageLegendItem(color = NeonCyan, label = "Apps", size = formatBytes(storageInfo.appsBytes))
          StorageLegendItem(color = CyberPurple, label = "Media", size = formatBytes(storageInfo.mediaBytes))
          StorageLegendItem(color = CyberAmber, label = "System", size = formatBytes(storageInfo.systemBytes))
          StorageLegendItem(color = ElectricEmerald, label = "Cache", size = formatBytes(storageInfo.cacheBytes))
        }
      }
    }

    // Storage Read/Write Speed Benchmark
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "STORAGE SPEED BENCHMARK",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Text(
              text = if (storageSpeed != null) "Sequential Flash I/O: ${storageSpeed?.toInt()} MB/s" else "Test internal UFS / NVMe read & write latency",
              fontSize = 11.sp,
              color = if (storageSpeed != null) ElectricEmerald else TextSecondary,
              fontWeight = if (storageSpeed != null) FontWeight.Bold else FontWeight.Normal
            )
          }

          Button(
            onClick = { viewModel.runStorageBenchmark() },
            enabled = !isTestingSpeed,
            colors = ButtonDefaults.buttonColors(
              containerColor = NeonCyan.copy(alpha = 0.2f),
              contentColor = NeonCyan
            ),
            shape = RoundedCornerShape(8.dp)
          ) {
            if (isTestingSpeed) {
              CircularProgressIndicator(modifier = Modifier.size(16.dp), color = NeonCyan, strokeWidth = 2.dp)
            } else {
              Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("TEST I/O", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // Media Analyzer & Purger Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "MEDIA & DUPLICATE ANALYZER",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.1.sp,
            color = TextSecondary
          )
          Text(
            text = "${formatBytes(totalSelectedMedia)} selected for purging",
            fontSize = 11.sp,
            color = NeonCyan
          )
        }

        Button(
          onClick = { viewModel.cleanSelectedMedia() },
          enabled = totalSelectedMedia > 0,
          colors = ButtonDefaults.buttonColors(
            containerColor = CyberCrimson,
            contentColor = TextPrimary
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("purge_media_button")
        ) {
          Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("PURGE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Media Items List
    items(mediaItems) { item ->
      MediaRowItem(
        item = item,
        onToggle = { viewModel.toggleMediaItem(item.id) }
      )
    }
  }
}

@Composable
fun StorageLegendItem(color: Color, label: String, size: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(RoundedCornerShape(2.dp))
        .background(color)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Column {
      Text(label, fontSize = 10.sp, color = TextSecondary)
      Text(size, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
  }
}

@Composable
fun MediaRowItem(
  item: MediaCleanItem,
  onToggle: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(CyberSurface)
      .border(1.dp, CyberCardBorder.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
      .clickable(onClick = onToggle)
      .padding(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Checkbox(
        checked = item.isSelected,
        onCheckedChange = { onToggle() },
        colors = CheckboxDefaults.colors(checkedColor = NeonCyan, checkmarkColor = Color(0xFF090D16))
      )
      Spacer(modifier = Modifier.width(8.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(text = item.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = "${item.count} files identified", fontSize = 11.sp, color = TextSecondary)
      }
      Text(
        text = formatBytes(item.sizeBytes),
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = if (item.sizeBytes > 0) TextPrimary else TextSecondary,
        fontFamily = FontFamily.Monospace
      )
    }
  }
}
