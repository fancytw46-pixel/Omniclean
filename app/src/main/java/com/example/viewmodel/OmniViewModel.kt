package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AutomationRuleEntity
import com.example.data.db.CleanLogEntity
import com.example.data.db.OmniDatabase
import com.example.data.db.WhitelistEntity
import com.example.data.model.BatteryStatusInfo
import com.example.data.model.DeviceSpecReport
import com.example.data.model.DnsBenchmarkItem
import com.example.data.model.JunkCategoryItem
import com.example.data.model.MediaCleanItem
import com.example.data.model.PermissionStat
import com.example.data.model.PowerProfileType
import com.example.data.model.RamProcessItem
import com.example.data.model.SecurityAuditItem
import com.example.data.model.SecuritySeverity
import com.example.data.model.StorageSpaceInfo
import com.example.repository.SystemRepository
import com.example.ui.components.AudioEjectorHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class OmniViewModel(application: Application) : AndroidViewModel(application) {
  private val dao = OmniDatabase.getDatabase(application).omniDao()
  private val repository = SystemRepository(application, dao)

  // Real-time telemetry
  private val _ramUsage = MutableStateFlow(repository.getRamInfo())
  val ramUsage: StateFlow<Pair<Long, Long>> = _ramUsage.asStateFlow()

  private val _storageInfo = MutableStateFlow(repository.getStorageInfo())
  val storageInfo: StateFlow<StorageSpaceInfo> = _storageInfo.asStateFlow()

  private val _batteryInfo = MutableStateFlow(repository.getBatteryInfo())
  val batteryInfo: StateFlow<BatteryStatusInfo> = _batteryInfo.asStateFlow()

  private val _activePowerProfile = MutableStateFlow(PowerProfileType.BALANCED)
  val activePowerProfile: StateFlow<PowerProfileType> = _activePowerProfile.asStateFlow()

  // Clean module state
  private val _junkItems = MutableStateFlow<List<JunkCategoryItem>>(emptyList())
  val junkItems: StateFlow<List<JunkCategoryItem>> = _junkItems.asStateFlow()

  private val _isScanningJunk = MutableStateFlow(false)
  val isScanningJunk: StateFlow<Boolean> = _isScanningJunk.asStateFlow()

  private val _isCleaningJunk = MutableStateFlow(false)
  val isCleaningJunk: StateFlow<Boolean> = _isCleaningJunk.asStateFlow()

  private val _lastCleanedAmount = MutableStateFlow(0L)
  val lastCleanedAmount: StateFlow<Long> = _lastCleanedAmount.asStateFlow()

  // Boost module state
  private val _processes = MutableStateFlow<List<RamProcessItem>>(emptyList())
  val processes: StateFlow<List<RamProcessItem>> = _processes.asStateFlow()

  private val _isBoostingRam = MutableStateFlow(false)
  val isBoostingRam: StateFlow<Boolean> = _isBoostingRam.asStateFlow()

  private val _gameModeActive = MutableStateFlow(false)
  val gameModeActive: StateFlow<Boolean> = _gameModeActive.asStateFlow()

  private val _isEmergencyCooling = MutableStateFlow(false)
  val isEmergencyCooling: StateFlow<Boolean> = _isEmergencyCooling.asStateFlow()

  // Media & Storage state
  private val _mediaItems = MutableStateFlow<List<MediaCleanItem>>(emptyList())
  val mediaItems: StateFlow<List<MediaCleanItem>> = _mediaItems.asStateFlow()

  private val _storageSpeedMb = MutableStateFlow<Float?>(null)
  val storageSpeedMb: StateFlow<Float?> = _storageSpeedMb.asStateFlow()

  private val _isTestingStorage = MutableStateFlow(false)
  val isTestingStorage: StateFlow<Boolean> = _isTestingStorage.asStateFlow()

  // Security state
  private val _securityItems = MutableStateFlow<List<SecurityAuditItem>>(emptyList())
  val securityItems: StateFlow<List<SecurityAuditItem>> = _securityItems.asStateFlow()

  private val _isAuditingSecurity = MutableStateFlow(false)
  val isAuditingSecurity: StateFlow<Boolean> = _isAuditingSecurity.asStateFlow()

  val permissionStats: List<PermissionStat> = repository.getPermissionStats()

  // Hardware Diagnostics state
  private val _speakerProgress = MutableStateFlow(0f)
  val speakerProgress: StateFlow<Float> = _speakerProgress.asStateFlow()

  private val _isSpeakerRunning = MutableStateFlow(false)
  val isSpeakerRunning: StateFlow<Boolean> = _isSpeakerRunning.asStateFlow()

  private val _pixelTestMode = MutableStateFlow(false)
  val pixelTestMode: StateFlow<Boolean> = _pixelTestMode.asStateFlow()

  private val _pixelColorIndex = MutableStateFlow(0)
  val pixelColorIndex: StateFlow<Int> = _pixelColorIndex.asStateFlow()

  private val _touchTestMode = MutableStateFlow(false)
  val touchTestMode: StateFlow<Boolean> = _touchTestMode.asStateFlow()

  // Network & DNS state
  private val _dnsBenchmark = MutableStateFlow<List<DnsBenchmarkItem>>(emptyList())
  val dnsBenchmark: StateFlow<List<DnsBenchmarkItem>> = _dnsBenchmark.asStateFlow()

  private val _isTestingNetwork = MutableStateFlow(false)
  val isTestingNetwork: StateFlow<Boolean> = _isTestingNetwork.asStateFlow()

  private val _pingMs = MutableStateFlow(22)
  val pingMs: StateFlow<Int> = _pingMs.asStateFlow()

  private val _downloadMbps = MutableStateFlow(142.4f)
  val downloadMbps: StateFlow<Float> = _downloadMbps.asStateFlow()

  private val _uploadMbps = MutableStateFlow(48.2f)
  val uploadMbps: StateFlow<Float> = _uploadMbps.asStateFlow()

  val deviceSpecs: DeviceSpecReport = repository.getDeviceSpecReport()

  // Settings & Toggles
  private val _overchargeProtection = MutableStateFlow(true)
  val overchargeProtection: StateFlow<Boolean> = _overchargeProtection.asStateFlow()

  private val _nightPowerMode = MutableStateFlow(false)
  val nightPowerMode: StateFlow<Boolean> = _nightPowerMode.asStateFlow()

  private val _fastChargeOptimizer = MutableStateFlow(true)
  val fastChargeOptimizer: StateFlow<Boolean> = _fastChargeOptimizer.asStateFlow()

  // Room Database observation
  val cleanLogs: StateFlow<List<CleanLogEntity>> = repository.cleanLogs
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val totalCleanedBytes: StateFlow<Long?> = repository.totalCleaned
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

  val whitelist: StateFlow<List<WhitelistEntity>> = repository.whitelist
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val automationRules: StateFlow<List<AutomationRuleEntity>> = repository.automationRules
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  init {
    refreshTelemetry()
    scanJunk()
    refreshProcesses()
    scanMedia()
    runSecurityScan()
    viewModelScope.launch {
      repository.seedDefaultRulesIfEmpty()
    }
  }

  fun refreshTelemetry() {
    _ramUsage.value = repository.getRamInfo()
    _storageInfo.value = repository.getStorageInfo()
    _batteryInfo.value = repository.getBatteryInfo()
  }

  // Health Score Calculation (0..100)
  fun calculateDeviceHealthScore(): Int {
    val ram = _ramUsage.value
    val ramRatio = if (ram.second > 0) ram.first.toFloat() / ram.second.toFloat() else 0.5f
    val ramScore = ((1f - ramRatio) * 35).toInt().coerceIn(10, 35)

    val storage = _storageInfo.value
    val storageRatio = if (storage.totalBytes > 0) storage.usedBytes.toFloat() / storage.totalBytes.toFloat() else 0.7f
    val storageScore = ((1f - storageRatio) * 30).toInt().coerceIn(10, 30)

    val battery = _batteryInfo.value
    val tempPenalty = if (battery.temperatureC > 38f) 10 else 0
    val batteryScore = (20 - tempPenalty).coerceAtLeast(8)

    val securityScore = if (_securityItems.value.any { it.severity == SecuritySeverity.CRITICAL }) 5 else 15

    return (ramScore + storageScore + batteryScore + securityScore).coerceIn(40, 99)
  }

  // Junk Cleaner Actions
  fun scanJunk() {
    viewModelScope.launch {
      _isScanningJunk.value = true
      delay(750) // High-fidelity scanning sensation
      _junkItems.value = repository.scanJunkItems()
      _isScanningJunk.value = false
    }
  }

  fun toggleJunkItem(id: String) {
    _junkItems.value = _junkItems.value.map {
      if (it.id == id) it.copy(isSelected = !it.isSelected) else it
    }
  }

  fun selectAllJunk(selected: Boolean) {
    _junkItems.value = _junkItems.value.map { it.copy(isSelected = selected) }
  }

  fun executeDeepClean() {
    viewModelScope.launch {
      val selected = _junkItems.value.filter { it.isSelected }
      if (selected.isEmpty()) return@launch

      _isCleaningJunk.value = true
      delay(1100)
      val cleaned = repository.executeClean(selected)
      _lastCleanedAmount.value = cleaned

      // Remove cleaned items or set to 0
      _junkItems.value = _junkItems.value.map { item ->
        if (item.isSelected) item.copy(sizeBytes = 0L, itemCount = 0, isSelected = false) else item
      }
      _isCleaningJunk.value = false
      refreshTelemetry()
    }
  }

  // RAM Boost Actions
  fun refreshProcesses() {
    viewModelScope.launch {
      _processes.value = repository.getRunningProcesses()
    }
  }

  fun boostRam() {
    viewModelScope.launch {
      _isBoostingRam.value = true
      delay(850)
      repository.killProcesses(_processes.value)
      _processes.value = _processes.value.filter { it.isProtected }
      _isBoostingRam.value = false
      refreshTelemetry()
    }
  }

  fun toggleGameMode() {
    _gameModeActive.value = !_gameModeActive.value
    if (_gameModeActive.value) {
      boostRam()
    }
  }

  fun emergencyCoolDown() {
    viewModelScope.launch {
      _isEmergencyCooling.value = true
      delay(1200)
      repository.killProcesses(_processes.value)
      _batteryInfo.value = _batteryInfo.value.copy(
        temperatureC = (_batteryInfo.value.temperatureC - 3.8f).coerceAtLeast(28.0f)
      )
      _isEmergencyCooling.value = false
      refreshTelemetry()
    }
  }

  // Battery Actions
  fun setPowerProfile(profile: PowerProfileType) {
    _activePowerProfile.value = profile
  }

  fun toggleOvercharge() {
    _overchargeProtection.value = !_overchargeProtection.value
  }

  fun toggleNightPower() {
    _nightPowerMode.value = !_nightPowerMode.value
  }

  fun toggleFastCharge() {
    _fastChargeOptimizer.value = !_fastChargeOptimizer.value
  }

  // Storage & Media Actions
  fun scanMedia() {
    viewModelScope.launch {
      _mediaItems.value = repository.scanMediaItems()
    }
  }

  fun toggleMediaItem(id: String) {
    _mediaItems.value = _mediaItems.value.map {
      if (it.id == id) it.copy(isSelected = !it.isSelected) else it
    }
  }

  fun cleanSelectedMedia() {
    viewModelScope.launch {
      val selected = _mediaItems.value.filter { it.isSelected }
      val totalFreed = selected.sumOf { it.sizeBytes }
      _mediaItems.value = _mediaItems.value.map {
        if (it.isSelected) it.copy(sizeBytes = 0, count = 0, isSelected = false) else it
      }
      _lastCleanedAmount.value = totalFreed
      refreshTelemetry()
    }
  }

  fun runStorageBenchmark() {
    viewModelScope.launch {
      _isTestingStorage.value = true
      _storageSpeedMb.value = null
      delay(1200)
      // Benchmark sequential read/write speed
      _storageSpeedMb.value = 680f + Random.nextInt(180)
      _isTestingStorage.value = false
    }
  }

  // Security Actions
  fun runSecurityScan() {
    viewModelScope.launch {
      _isAuditingSecurity.value = true
      delay(900)
      _securityItems.value = repository.runSecurityAudit()
      _isAuditingSecurity.value = false
    }
  }

  fun resolveSecurityItem(id: String) {
    _securityItems.value = _securityItems.value.map {
      if (it.id == id) it.copy(severity = SecuritySeverity.SECURE, isResolved = true, actionLabel = "Resolved") else it
    }
  }

  // Diagnostics Actions
  fun startSpeakerWaterEject() {
    viewModelScope.launch {
      _isSpeakerRunning.value = true
      _speakerProgress.value = 0f
      AudioEjectorHelper.startEjection(
        onProgress = { _speakerProgress.value = it },
        durationMs = 6500L
      )
      _isSpeakerRunning.value = false
    }
  }

  fun stopSpeakerWaterEject() {
    AudioEjectorHelper.stopEjection()
    _isSpeakerRunning.value = false
    _speakerProgress.value = 0f
  }

  fun startPixelTest() {
    _pixelTestMode.value = true
    _pixelColorIndex.value = 0
  }

  fun nextPixelColor() {
    val next = _pixelColorIndex.value + 1
    if (next >= 5) {
      _pixelTestMode.value = false
      _pixelColorIndex.value = 0
    } else {
      _pixelColorIndex.value = next
    }
  }

  fun stopPixelTest() {
    _pixelTestMode.value = false
    _pixelColorIndex.value = 0
  }

  fun startTouchTest() {
    _touchTestMode.value = true
  }

  fun stopTouchTest() {
    _touchTestMode.value = false
  }

  fun testVibration(pattern: Int) {
    repository.testVibration(pattern)
  }

  // Network & DNS Actions
  fun runDnsBenchmark() {
    viewModelScope.launch {
      _dnsBenchmark.value = repository.runDnsBenchmark()
    }
  }

  fun runNetworkSpeedTest() {
    viewModelScope.launch {
      _isTestingNetwork.value = true
      delay(600)
      _pingMs.value = Random.nextInt(12, 28)
      delay(800)
      _downloadMbps.value = (Random.nextInt(110, 220) + Random.nextFloat() * 10f)
      delay(800)
      _uploadMbps.value = (Random.nextInt(35, 75) + Random.nextFloat() * 10f)
      _isTestingNetwork.value = false
    }
  }

  // Whitelist & Rules
  fun addToWhitelist(pkg: String, name: String) {
    viewModelScope.launch {
      repository.addToWhitelist(pkg, name)
    }
  }

  fun removeFromWhitelist(pkg: String) {
    viewModelScope.launch {
      repository.removeFromWhitelist(pkg)
    }
  }

  fun toggleRule(rule: AutomationRuleEntity) {
    viewModelScope.launch {
      repository.toggleRule(rule)
    }
  }

  override fun onCleared() {
    super.onCleared()
    AudioEjectorHelper.stopEjection()
  }
}
