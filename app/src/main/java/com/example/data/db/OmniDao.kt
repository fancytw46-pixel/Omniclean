package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OmniDao {
  @Query("SELECT * FROM clean_logs ORDER BY timestamp DESC")
  fun getAllLogs(): Flow<List<CleanLogEntity>>

  @Query("SELECT SUM(bytesCleaned) FROM clean_logs")
  fun getTotalBytesCleaned(): Flow<Long?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLog(log: CleanLogEntity): Long

  @Query("DELETE FROM clean_logs")
  suspend fun clearAllLogs()

  @Query("SELECT * FROM app_whitelist ORDER BY appName ASC")
  fun getWhitelist(): Flow<List<WhitelistEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertWhitelist(item: WhitelistEntity): Long

  @Query("DELETE FROM app_whitelist WHERE packageName = :packageName")
  suspend fun deleteWhitelist(packageName: String)

  @Query("SELECT * FROM automation_rules")
  fun getRules(): Flow<List<AutomationRuleEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRule(rule: AutomationRuleEntity): Long

  @Update
  suspend fun updateRule(rule: AutomationRuleEntity)
}
