package com.example.shendet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shendet.data.local.dao.AllergyDao
import com.example.shendet.data.local.dao.ScanHistoryDao
import com.example.shendet.data.local.entity.AllergyEntity
import com.example.shendet.data.local.entity.ScanHistoryEntity

@Database(entities = [ScanHistoryEntity::class, AllergyEntity::class], version = 3, exportSchema = false)
abstract class ShendetDatabase : RoomDatabase() {
    abstract fun scanHistoryDao(): ScanHistoryDao
    abstract fun allergyDao(): AllergyDao

    companion object {
        @Volatile
        private var INSTANCE: ShendetDatabase? = null

        fun getDatabase(context: Context): ShendetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShendetDatabase::class.java,
                    "shendet_database"
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
