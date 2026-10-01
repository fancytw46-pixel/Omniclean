package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCrimson
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun CircularHudHealthGauge(
  score: Int,
  modifier: Modifier = Modifier
) {
  val animatedScore = remember { Animatable(0f) }
  LaunchedEffect(score) {
    animatedScore.animateTo(
      targetValue = score.toFloat(),
      animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
    )
  }

  val infiniteTransition = rememberInfiniteTransition(label = "hud_pulse")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.35f,
    targetValue = 0.95f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  val gaugeColor = when {
    score >= 85 -> ElectricEmerald
    score >= 70 -> NeonCyan
    score >= 50 -> CyberAmber
    else -> CyberCrimson
  }

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .size(200.dp)
      .testTag("health_score_gauge")
  ) {
    Canvas(modifier = Modifier.size(190.dp)) {
      val strokeWidth = 14.dp.toPx()
      val diameter = size.minDimension - strokeWidth
      val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
      val arcSize = Size(diameter, diameter)

      // Background track
      drawArc(
        color = CyberSurfaceVariant.copy(alpha = 0.6f),
        startAngle = 135f,
        sweepAngle = 270f,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
      )

      // Active sweep
      val sweepRatio = (animatedScore.value / 100f).coerceIn(0f, 1f)
      val sweep = 270f * sweepRatio

      drawArc(
        brush = Brush.sweepGradient(
          colors = listOf(
            gaugeColor.copy(alpha = 0.6f),
            gaugeColor,
            gaugeColor
          )
        ),
        startAngle = 135f,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
      )

      // Outer delicate radar ring
      drawCircle(
        color = gaugeColor.copy(alpha = 0.15f * pulseAlpha),
        radius = (size.minDimension / 2) - 2.dp.toPx(),
        style = Stroke(width = 1.5.dp.toPx())
      )
    }

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = "${animatedScore.value.toInt()}",
        fontSize = 44.sp,
        fontWeight = FontWeight.Black,
        color = TextPrimary,
        fontFamily = FontFamily.Monospace
      )
      Text(
        text = "HEALTH SCORE",
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp,
        color = gaugeColor
      )
      Spacer(modifier = Modifier.height(4.dp))
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(gaugeColor.copy(alpha = 0.15f))
          .border(0.8.dp, gaugeColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
          .padding(horizontal = 8.dp, vertical = 2.dp)
      ) {
        val statusLabel = when {
          score >= 85 -> "EXCELLENT"
          score >= 70 -> "OPTIMIZED"
          score >= 50 -> "NEEDS ATTENTION"
          else -> "CRITICAL"
        }
        Text(
          text = statusLabel,
          fontSize = 9.sp,
          fontWeight = FontWeight.SemiBold,
          color = gaugeColor
        )
      }
    }
  }
}

@Composable
fun LinearUsageMetricBar(
  title: String,
  subtitle: String,
  fraction: Float,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = TextPrimary
      )
      Text(
        text = subtitle,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = accentColor,
        fontFamily = FontFamily.Monospace
      )
    }
    Spacer(modifier = Modifier.height(6.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(8.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(CyberSurfaceVariant)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(fraction.coerceIn(0f, 1f))
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(
            Brush.horizontalGradient(
              colors = listOf(accentColor.copy(alpha = 0.7f), accentColor)
            )
          )
      )
    }
  }
}

fun formatBytes(bytes: Long): String {
  if (bytes <= 0) return "0 B"
  val kb = bytes / 1024.0
  val mb = kb / 1024.0
  val gb = mb / 1024.0
  return when {
    gb >= 1.0 -> String.format(Locale.US, "%.1f GB", gb)
    mb >= 1.0 -> String.format(Locale.US, "%.1f MB", mb)
    kb >= 1.0 -> String.format(Locale.US, "%.1f KB", kb)
    else -> "$bytes B"
  }
}
