package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    CleanLogEntity::class,
    WhitelistEntity::class,
    AutomationRuleEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class OmniDatabase : RoomDatabase() {
  abstract fun omniDao(): OmniDao

  companion object {
    @Volatile
    private var INSTANCE: OmniDatabase? = null

    fun getDatabase(context: Context): OmniDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          OmniDatabase::class.java,
          "omni_cleaner.db"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
