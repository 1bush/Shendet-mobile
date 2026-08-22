package com.example.shendet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: String,
    val name: String,
    val score: Int,
    val grade: String,
    val nutriScore: String? = null,
    val healthImpact: String = "",
    val positives: String = "", // Stored as comma separated or JSON string
    val negatives: String = "",
    val vitamins: String = "",
    val minerals: String = "",
    val imagePath: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "allergy_profile")
data class AllergyEntity(
    @PrimaryKey val name: String,
    val isEnabled: Boolean = false
)
