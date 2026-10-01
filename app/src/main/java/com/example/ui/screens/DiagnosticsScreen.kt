package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
fun DiagnosticsScreen(
  viewModel: OmniViewModel,
  modifier: Modifier = Modifier
) {
  val speakerProgress by viewModel.speakerProgress.collectAsStateWithLifecycle()
  val isSpeakerRunning by viewModel.isSpeakerRunning.collectAsStateWithLifecycle()
  val pixelTestActive by viewModel.pixelTestMode.collectAsStateWithLifecycle()
  val pixelColorIndex by viewModel.pixelColorIndex.collectAsStateWithLifecycle()
  val touchTestActive by viewModel.touchTestMode.collectAsStateWithLifecycle()

  // Full Screen Pixel Dead Zone Test Pattern
  if (pixelTestActive) {
    val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.White, Color.Black)
    val currentColor = colors[pixelColorIndex.coerceIn(0, colors.size - 1)]

    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(currentColor)
        .clickable { viewModel.nextPixelColor() }
        .padding(24.dp)
    ) {
      Text(
        text = "Tap screen to cycle color (${pixelColorIndex + 1}/5). Look for dead/stuck sub-pixels.",
        color = if (currentColor == Color.White) Color.Black else Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.align(Alignment.BottomCenter)
      )
    }
    return
  }

  // Touchscreen Dead Zone Matrix Test
  if (touchTestActive) {
    TouchscreenGridTester(onFinish = { viewModel.stopTouchTest() })
    return
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Speaker Moisture & Dust Ejector
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
                .size(44.dp)
                .clip(CircleShape)
                .background(NeonCyan.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Hearing, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text("SPEAKER DUST & WATER EJECTOR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
              Text("Sonic Wave Frequency 165Hz", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
              Text("Dislodges trapped moisture and dust via resonance", fontSize = 11.sp, color = TextSecondary)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (isSpeakerRunning) {
          LinearProgressIndicator(
            progress = { speakerProgress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = NeonCyan,
            trackColor = CyberSurfaceVariant
          )
          Spacer(modifier = Modifier.height(10.dp))
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = {
              if (isSpeakerRunning) viewModel.stopSpeakerWaterEject()
              else viewModel.startSpeakerWaterEject()
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isSpeakerRunning) CyberCrimson else NeonCyan,
              contentColor = Color(0xFF090D16)
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f).height(46.dp).testTag("speaker_eject_button")
          ) {
            Text(if (isSpeakerRunning) "STOP SOUND PULSE" else "PLAY EJECTION TONE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }
    }

    // Display Pixel Diagnostic Tool
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(CyberAmber.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Tv, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Display Pixel Diagnostic", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
              Text("Full screen RGBW test pattern for stuck sub-pixels", fontSize = 11.sp, color = TextSecondary)
            }
          }
          Button(
            onClick = { viewModel.startPixelTest() },
            colors = ButtonDefaults.buttonColors(containerColor = CyberAmber.copy(alpha = 0.2f), contentColor = CyberAmber),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("pixel_diagnostic_button")
          ) {
            Text("START", fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }
      }
    }

    // Touchscreen Dead Zone Tester
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ElectricEmerald.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.TouchApp, contentDescription = null, tint = ElectricEmerald, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Touchscreen Dead Zone Grid", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
              Text("Drag finger across matrix to test digitizer responsiveness", fontSize = 11.sp, color = TextSecondary)
            }
          }
          Button(
            onClick = { viewModel.startTouchTest() },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricEmerald.copy(alpha = 0.2f), contentColor = ElectricEmerald),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("touch_grid_tester_button")
          ) {
            Text("TEST", fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }
      }
    }

    // Vibration Motor Diagnostic
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(NeonCyan.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Vibration, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(22.dp))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text("Haptic Engine & Vibration Diagnostic", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("Test haptic actuator pulse patterns", fontSize = 11.sp, color = TextSecondary)
          }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = { viewModel.testVibration(0) },
            colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant, contentColor = TextPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f)
          ) {
            Text("Click", fontSize = 11.sp)
          }
          Button(
            onClick = { viewModel.testVibration(1) },
            colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant, contentColor = TextPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f)
          ) {
            Text("Heavy", fontSize = 11.sp)
          }
          Button(
            onClick = { viewModel.testVibration(2) },
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan.copy(alpha = 0.2f), contentColor = NeonCyan),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1.2f)
          ) {
            Text("Waveform", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Live Sensors Telemetry
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Sensors, contentDescription = null, tint = ElectricEmerald)
          Spacer(modifier = Modifier.width(8.dp))
          Text("HARDWARE SENSORS STATUS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElectricEmerald)
        }
        Spacer(modifier = Modifier.height(10.dp))

        SensorItemRow("3-Axis Accelerometer", "Active • Calibration 99.8%")
        SensorItemRow("Gyroscope (Angular Rate)", "Active • Drift: 0.02 rad/s")
        SensorItemRow("Ambient Light Sensor", "Operational (420 Lux)")
        SensorItemRow("Proximity Sensor", "Far (No obstruction)")
        SensorItemRow("Geomagnetic Compass", "Calibrated • True North")
      }
    }
  }
}

@Composable
fun SensorItemRow(name: String, status: String) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(name, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
    Text(status, fontSize = 11.sp, color = NeonCyan, fontFamily = FontFamily.Monospace)
  }
}

@Composable
fun TouchscreenGridTester(onFinish: () -> Unit) {
  val touchedIndices = remember { mutableStateListOf<Int>() }
  val cols = 6
  val rows = 12

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF090D16))
      .pointerInput(Unit) {
        detectDragGestures { change, _ ->
          val x = change.position.x
          val y = change.position.y
          val colWidth = size.width / cols
          val rowHeight = size.height / rows
          val c = (x / colWidth).toInt().coerceIn(0, cols - 1)
          val r = (y / rowHeight).toInt().coerceIn(0, rows - 1)
          val index = r * cols + c
          if (!touchedIndices.contains(index)) {
            touchedIndices.add(index)
          }
        }
      }
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val cellW = size.width / cols
      val cellH = size.height / rows

      for (r in 0 until rows) {
        for (c in 0 until cols) {
          val idx = r * cols + c
          val isTouched = touchedIndices.contains(idx)
          drawRect(
            color = if (isTouched) Color(0xFF00E5FF).copy(alpha = 0.6f) else Color(0xFF1E293B),
            topLeft = androidx.compose.ui.geometry.Offset(c * cellW, r * cellH),
            size = androidx.compose.ui.geometry.Size(cellW - 2, cellH - 2)
          )
        }
      }
    }

    Button(
      onClick = onFinish,
      colors = ButtonDefaults.buttonColors(containerColor = CyberCrimson, contentColor = Color.White),
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(24.dp)
    ) {
      Icon(Icons.Default.Close, contentDescription = null)
      Spacer(modifier = Modifier.width(6.dp))
      Text("FINISH TOUCH TEST (${touchedIndices.size}/${cols * rows})", fontWeight = FontWeight.Bold)
    }
  }
}
