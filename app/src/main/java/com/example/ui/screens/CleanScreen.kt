package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.JunkCategoryItem
import com.example.ui.components.CyberCard
import com.example.ui.components.formatBytes
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.OmniViewModel

@Composable
fun CleanScreen(
  viewModel: OmniViewModel,
  modifier: Modifier = Modifier
) {
  val junkItems by viewModel.junkItems.collectAsStateWithLifecycle()
  val isScanning by viewModel.isScanningJunk.collectAsStateWithLifecycle()
  val isCleaning by viewModel.isCleaningJunk.collectAsStateWithLifecycle()
  val lastCleaned by viewModel.lastCleanedAmount.collectAsStateWithLifecycle()

  val totalSelectedBytes = junkItems.filter { it.isSelected }.sumOf { it.sizeBytes }
  val allSelected = junkItems.isNotEmpty() && junkItems.all { it.isSelected }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header summary card
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
              text = "RECOVERABLE JUNK",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.2.sp,
              color = NeonCyan
            )
            Text(
              text = formatBytes(totalSelectedBytes),
              fontSize = 32.sp,
              fontWeight = FontWeight.Black,
              color = TextPrimary,
              fontFamily = FontFamily.Monospace
            )
            Text(
              text = "${junkItems.count { it.isSelected }} categories selected",
              fontSize = 12.sp,
              color = TextSecondary
            )
          }

          IconButton(
            onClick = { viewModel.scanJunk() },
            enabled = !isScanning && !isCleaning,
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(CyberSurfaceVariant)
          ) {
            if (isScanning) {
              CircularProgressIndicator(modifier = Modifier.size(20.dp), color = NeonCyan, strokeWidth = 2.dp)
            } else {
              Icon(Icons.Default.Refresh, contentDescription = "Rescan Junk", tint = NeonCyan)
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons: Select All & Clean
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Button(
            onClick = { viewModel.selectAllJunk(!allSelected) },
            colors = ButtonDefaults.buttonColors(
              containerColor = CyberSurfaceVariant,
              contentColor = TextPrimary
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
          ) {
            Text(if (allSelected) "DESELECT ALL" else "SELECT ALL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = { viewModel.executeDeepClean() },
            enabled = !isCleaning && totalSelectedBytes > 0,
            colors = ButtonDefaults.buttonColors(
              containerColor = NeonCyan,
              contentColor = Color(0xFF090D16)
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .weight(1.5f)
              .height(48.dp)
              .testTag("clean_selected_junk_button")
          ) {
            if (isCleaning) {
              CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFF090D16), strokeWidth = 2.dp)
              Spacer(modifier = Modifier.width(6.dp))
              Text("PURGING...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            } else {
              Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("PURGE SELECTED", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // Success banner when clean finishes
    if (lastCleaned > 0) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ElectricEmerald.copy(alpha = 0.15f))
            .border(1.dp, ElectricEmerald.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(14.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Check, contentDescription = null, tint = ElectricEmerald)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Successfully reclaimed ${formatBytes(lastCleaned)} of device storage!",
              color = ElectricEmerald,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    }

    // List of Junk Categories
    items(junkItems) { item ->
      JunkCategoryRow(
        item = item,
        onToggle = { viewModel.toggleJunkItem(item.id) }
      )
    }
  }
}

@Composable
fun JunkCategoryRow(
  item: JunkCategoryItem,
  onToggle: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(CyberSurface)
      .border(1.dp, CyberCardBorder.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
      .clickable(onClick = onToggle)
      .padding(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Checkbox(
        checked = item.isSelected,
        onCheckedChange = { onToggle() },
        colors = CheckboxDefaults.colors(
          checkedColor = NeonCyan,
          uncheckedColor = TextSecondary,
          checkmarkColor = Color(0xFF090D16)
        )
      )

      Spacer(modifier = Modifier.width(8.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = item.name,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
        Text(
          text = item.description,
          fontSize = 11.sp,
          color = TextSecondary,
          maxLines = 1
        )
        Text(
          text = "${item.itemCount} items identified",
          fontSize = 10.sp,
          color = NeonCyan.copy(alpha = 0.8f),
          fontWeight = FontWeight.SemiBold
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Text(
        text = formatBytes(item.sizeBytes),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = if (item.sizeBytes > 0) TextPrimary else TextSecondary,
        fontFamily = FontFamily.Monospace
      )
    }
  }
}
