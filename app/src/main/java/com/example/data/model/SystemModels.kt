package com.example.data.model

data class JunkCategoryItem(
  val id: String,
  val name: String,
  val description: String,
  val sizeBytes: Long,
  val itemCount: Int,
  val isSelected: Boolean = true,
  val categoryType: JunkCategoryType
)

enum class JunkCategoryType {
  DEEP_CACHE,
  RESIDUAL_FILES,
  EMPTY_FOLDERS,
  APP_LOGS_CRASH,
  SYSTEM_TEMP,
  DOWNLOADS_JUNK,
  APK_INSTALLERS,
  THUMBNAIL_DB,
  AD_CACHE,
  SOCIAL_MEDIA_CACHE,
  STREAMING_CACHE,
  CLIPBOARD
}

data class RamProcessItem(
  val id: Int,
  val packageName: String,
  val appName: String,
  val memoryBytes: Long,
  val isProtected: Boolean = false,
  val cpuPercent: Float = 0f
)

data class BatteryStatusInfo(
  val level: Int = 85,
  val isCharging: Boolean = false,
  val statusText: String = "Discharging",
  val healthText: String = "Good",
  val temperatureC: Float = 31.5f,
  val voltageMv: Int = 4120,
  val chargeType: String = "Battery",
  val estimatedMinutesRemaining: Int = 420,
  val dischargeRateMa: Int = 380,
  val cycleCount: Int = 142,
  val degradationPercent: Int = 4
)

enum class PowerProfileType(val title: String, val desc: String, val cpuCap: String, val displayHz: String) {
  MAX_PERFORMANCE("Max Performance", "Full CPU clock, 120Hz display, no background limits", "100%", "120Hz"),
  BALANCED("Balanced", "Intelligent dynamic scaling for daily use", "80%", "Auto 60-120Hz"),
  POWER_SAVER("Power Saver", "Restricts background sync, limits CPU to 60%", "60%", "60Hz"),
  EXTREME_SAVER("Extreme Saver", "Minimal core services, monochrome OLED profile", "40%", "48Hz")
}

data class StorageSpaceInfo(
  val totalBytes: Long = 128L * 1024 * 1024 * 1024,
  val freeBytes: Long = 42L * 1024 * 1024 * 1024,
  val usedBytes: Long = 86L * 1024 * 1024 * 1024,
  val appsBytes: Long = 34L * 1024 * 1024 * 1024,
  val mediaBytes: Long = 28L * 1024 * 1024 * 1024,
  val systemBytes: Long = 18L * 1024 * 1024 * 1024,
  val cacheBytes: Long = 6L * 1024 * 1024 * 1024
)

data class MediaCleanItem(
  val id: String,
  val title: String,
  val type: String, // Screenshot, Duplicate, Large Video, Low Quality
  val sizeBytes: Long,
  val count: Int,
  val isSelected: Boolean = true
)

data class SecurityAuditItem(
  val id: String,
  val title: String,
  val description: String,
  val severity: SecuritySeverity,
  val isResolved: Boolean = false,
  val actionLabel: String = "Fix"
)

enum class SecuritySeverity {
  CRITICAL,
  WARNING,
  INFO,
  SECURE
}

data class PermissionStat(
  val permission: String,
  val displayName: String,
  val appCount: Int,
  val dangerLevel: String,
  val description: String
)

data class DnsBenchmarkItem(
  val provider: String,
  val primaryIp: String,
  val secondaryIp: String,
  val pingMs: Int,
  val isRecommended: Boolean = false,
  val isSelected: Boolean = false
)

data class DeviceSpecReport(
  val model: String,
  val manufacturer: String,
  val androidVersion: String,
  val apiLevel: Int,
  val cpuCores: Int,
  val cpuArch: String,
  val totalRamMb: Long,
  val totalStorageGb: Long,
  val screenResolution: String,
  val buildId: String
)
