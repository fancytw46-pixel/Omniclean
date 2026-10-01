package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.SecurityAuditItem
import com.example.data.model.SecuritySeverity
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
fun SecurityScreen(
  viewModel: OmniViewModel,
  modifier: Modifier = Modifier
) {
  val securityItems by viewModel.securityItems.collectAsStateWithLifecycle()
  val isScanning by viewModel.isAuditingSecurity.collectAsStateWithLifecycle()
  val permissionStats = viewModel.permissionStats

  var breachEmailInput by remember { mutableStateOf("") }
  var breachStatusResult by remember { mutableStateOf<String?>(null) }
  var isCheckingBreach by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Shield Banner Header
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
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(ElectricEmerald.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Shield, contentDescription = null, tint = ElectricEmerald, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text("REAL-TIME SECURITY SHIELD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricEmerald)
              Text("DEVICE PROTECTED", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
              Text("SELinux: Enforcing • Antivirus: Active", fontSize = 11.sp, color = TextSecondary)
            }
          }

          IconButton(
            onClick = { viewModel.runSecurityScan() },
            enabled = !isScanning,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(CyberSurfaceVariant)
          ) {
            if (isScanning) {
              CircularProgressIndicator(modifier = Modifier.size(18.dp), color = ElectricEmerald, strokeWidth = 2.dp)
            } else {
              Icon(Icons.Default.Refresh, contentDescription = "Rescan", tint = ElectricEmerald)
            }
          }
        }
      }
    }

    // Security Items / Warnings List
    item {
      Text(
        text = "SECURITY AUDIT & THREAT DETECTION",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.1.sp,
        color = TextSecondary
      )
    }

    items(securityItems) { audit ->
      SecurityAuditRow(
        audit = audit,
        onResolve = { viewModel.resolveSecurityItem(audit.id) }
      )
    }

    // App Permissions Sensitivity Index
    item {
      Text(
        text = "HIGH-RISK APP PERMISSIONS AUDIT",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.1.sp,
        color = TextSecondary
      )
      Spacer(modifier = Modifier.height(4.dp))

      CyberCard(modifier = Modifier.fillMaxWidth()) {
        permissionStats.forEachIndexed { index, perm ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(perm.displayName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
              Text(perm.description, fontSize = 10.sp, color = TextSecondary)
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(
                  if (perm.dangerLevel == "HIGH") CyberCrimson.copy(alpha = 0.15f)
                  else CyberAmber.copy(alpha = 0.15f)
                )
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = "${perm.appCount} Apps",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (perm.dangerLevel == "HIGH") CyberCrimson else CyberAmber
              )
            }
          }
          if (index < permissionStats.size - 1) {
            Spacer(modifier = Modifier.height(6.dp))
          }
        }
      }
    }

    // Data Breach Exposure Monitor
    item {
      Text(
        text = "DATA BREACH & LEAK MONITOR",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.1.sp,
        color = TextSecondary
      )
      Spacer(modifier = Modifier.height(4.dp))

      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Text("Check if your accounts appeared in known public corporate data breaches.", fontSize = 11.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = breachEmailInput,
            onValueChange = { breachEmailInput = it },
            placeholder = { Text("user@example.com", fontSize = 12.sp, color = TextSecondary) },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("breach_email_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = NeonCyan,
              unfocusedBorderColor = CyberCardBorder
            ),
            shape = RoundedCornerShape(8.dp)
          )

          Button(
            onClick = {
              if (breachEmailInput.isNotBlank()) {
                isCheckingBreach = true
                breachStatusResult = "Good news: No unencrypted breaches detected for $breachEmailInput!"
                isCheckingBreach = false
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF090D16)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(52.dp)
          ) {
            Text("CHECK", fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }

        if (breachStatusResult != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = breachStatusResult!!,
            fontSize = 11.sp,
            color = ElectricEmerald,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}

@Composable
fun SecurityAuditRow(
  audit: SecurityAuditItem,
  onResolve: () -> Unit,
  modifier: Modifier = Modifier
) {
  val severityColor = when (audit.severity) {
    SecuritySeverity.CRITICAL -> CyberCrimson
    SecuritySeverity.WARNING -> CyberAmber
    SecuritySeverity.SECURE -> ElectricEmerald
    SecuritySeverity.INFO -> NeonCyan
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(CyberSurface)
      .border(1.dp, severityColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
      .padding(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = if (audit.severity == SecuritySeverity.SECURE) Icons.Default.CheckCircle else Icons.Default.Warning,
        contentDescription = null,
        tint = severityColor,
        modifier = Modifier.size(22.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(audit.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(audit.description, fontSize = 11.sp, color = TextSecondary)
      }
      if (audit.severity != SecuritySeverity.SECURE) {
        Spacer(modifier = Modifier.width(8.dp))
        Button(
          onClick = onResolve,
          colors = ButtonDefaults.buttonColors(
            containerColor = severityColor.copy(alpha = 0.2f),
            contentColor = severityColor
          ),
          shape = RoundedCornerShape(6.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(audit.actionLabel, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
