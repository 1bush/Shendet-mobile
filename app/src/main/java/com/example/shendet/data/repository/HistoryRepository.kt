package com.example.shendet.data.repository

import com.example.shendet.data.local.dao.ScanHistoryDao
import com.example.shendet.data.local.entity.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val scanHistoryDao: ScanHistoryDao) {

    val allHistory: Flow<List<ScanHistoryEntity>> = scanHistoryDao.getAllHistory()

    suspend fun addScan(scan: ScanHistoryEntity) {
        scanHistoryDao.insertScan(scan)
    }

    suspend fun getScan(productId: String): ScanHistoryEntity? {
        return scanHistoryDao.getScanByProductId(productId)
    }

    suspend fun clearHistory() {
        scanHistoryDao.clearHistory()
    }
}
