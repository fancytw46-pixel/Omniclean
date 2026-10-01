package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface

@Composable
fun CyberCard(
  modifier: Modifier = Modifier,
  borderColor: Color = CyberCardBorder.copy(alpha = 0.5f),
  backgroundColor: Color = CyberSurface,
  cornerRadius: Dp = 16.dp,
  onClick: (() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit
) {
  val shape = RoundedCornerShape(cornerRadius)
  val clickModifier = if (onClick != null) {
    Modifier.clickable(onClick = onClick)
  } else {
    Modifier
  }

  Box(
    modifier = modifier
      .clip(shape)
      .background(backgroundColor)
      .border(1.dp, borderColor, shape)
      .then(clickModifier)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      content = content
    )
  }
}
