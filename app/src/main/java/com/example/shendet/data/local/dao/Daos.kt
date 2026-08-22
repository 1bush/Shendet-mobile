package com.example.shendet.data.local.dao

import androidx.room.*
import com.example.shendet.data.local.entity.AllergyEntity
import com.example.shendet.data.local.entity.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {
    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<ScanHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanHistoryEntity)

    @Query("SELECT * FROM scan_history WHERE productId = :productId LIMIT 1")
    suspend fun getScanByProductId(productId: String): ScanHistoryEntity?

    @Query("DELETE FROM scan_history")
    suspend fun clearHistory()
}

@Dao
interface AllergyDao {
    @Query("SELECT * FROM allergy_profile")
    fun getAllAllergies(): Flow<List<AllergyEntity>>

    @Query("SELECT name FROM allergy_profile WHERE isEnabled = 1")
    suspend fun getEnabledAllergies(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllergies(allergies: List<AllergyEntity>)

    @Update
    suspend fun updateAllergy(allergy: AllergyEntity)
}
