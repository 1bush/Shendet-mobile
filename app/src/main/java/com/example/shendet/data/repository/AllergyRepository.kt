package com.example.shendet.data.repository

import com.example.shendet.data.local.dao.AllergyDao
import com.example.shendet.data.local.entity.AllergyEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AllergyRepository(private val allergyDao: AllergyDao) {

    val allAllergies: Flow<List<AllergyEntity>> = allergyDao.getAllAllergies()

    suspend fun initializeDefaultAllergies() {
        val existing = allAllergies.first()
        if (existing.isEmpty()) {
            val defaults = listOf(
                "Gluten", "Laktozë", "Kikirikë", "Arra", "Vezë", "Peshk", "Soja", "Susami"
            ).map { AllergyEntity(it) }
            allergyDao.insertAllergies(defaults)
        }
    }

    suspend fun toggleAllergy(allergy: AllergyEntity) {
        allergyDao.updateAllergy(allergy.copy(isEnabled = !allergy.isEnabled))
    }

    suspend fun getEnabledAllergies(): List<String> {
        return allergyDao.getEnabledAllergies()
    }
}
