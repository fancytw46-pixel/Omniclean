package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clean_logs")
data class CleanLogEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val timestamp: Long = System.currentTimeMillis(),
  val title: String,
  val category: String,
  val bytesCleaned: Long,
  val details: String
)

@Entity(tableName = "app_whitelist")
data class WhitelistEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val packageName: String,
  val appName: String,
  val isProtected: Boolean = true
)

@Entity(tableName = "automation_rules")
data class AutomationRuleEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val ruleName: String,
  val triggerType: String,
  val isEnabled: Boolean = true,
  val description: String = ""
)
