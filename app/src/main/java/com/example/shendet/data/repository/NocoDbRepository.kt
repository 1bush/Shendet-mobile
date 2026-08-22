package com.example.shendet.data.repository

import android.util.Log
import com.example.shendet.data.api.NocoDbApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/** Optional admin-managed NocoDB provider. It is disabled when not configured. */
class NocoDbRepository(
    baseUrl: String,
    private val token: String,
    private val orgId: String,
    private val baseId: String,
    private val productsTableId: String,
    private val allergensTableId: String,
    private val inciTableId: String
) {
    private val enabled = baseUrl.isNotBlank() && token.isNotBlank() && orgId.isNotBlank() && baseId.isNotBlank()
    private val api: NocoDbApi? = if (enabled) {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        Retrofit.Builder()
            .baseUrl(if (baseUrl.endsWith('/')) baseUrl else "$baseUrl/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(NocoDbApi::class.java)
    } else null

    suspend fun findProduct(barcode: String): ProductLookup? = withContext(Dispatchers.IO) {
        if (!enabled || productsTableId.isBlank()) return@withContext null
        try {
            val response = api?.listRecords(token, orgId, baseId, productsTableId, "(barcode,eq,$barcode)") ?: return@withContext null
            val row = response.body()?.list?.firstOrNull() ?: return@withContext null
            ProductLookup(
                name = row["name"]?.toString() ?: return@withContext null,
                brand = row["brand"]?.toString(),
                ingredients = row["ingredients"]?.toString(),
                source = DataSource.NOCO_DB,
                confidence = 0.95f
            )
        } catch (e: Exception) {
            Log.w("NocoDb", "Product lookup unavailable", e)
            null
        }
    }

    suspend fun findInciIngredient(name: String): InciIngredientResult? = withContext(Dispatchers.IO) {
        if (!enabled || inciTableId.isBlank()) return@withContext null
        try {
            val response = api?.listRecords(token, orgId, baseId, inciTableId, "(name,eq,$name)") ?: return@withContext null
            val row = response.body()?.list?.firstOrNull() ?: return@withContext null
            InciIngredientResult(
                ingredient = row["name"]?.toString() ?: name,
                risk = when (row["risk"]?.toString()?.uppercase()) {
                    "HIGH" -> InciRisk.HIGH
                    "MEDIUM" -> InciRisk.MEDIUM
                    else -> InciRisk.LOW
                },
                explanation = row["explanation"]?.toString() ?: "Informacion nga NocoDB"
            )
        } catch (e: Exception) {
            Log.w("NocoDb", "INCI lookup unavailable", e)
            null
        }
    }

    suspend fun findAllergen(name: String): NocoAllergen? = withContext(Dispatchers.IO) {
        if (!enabled || allergensTableId.isBlank()) return@withContext null
        try {
            val response = api?.listRecords(token, orgId, baseId, allergensTableId, "(name,eq,$name)")
                ?: return@withContext null
            val row = response.body()?.list?.firstOrNull() ?: return@withContext null
            NocoAllergen(
                name = row["name"]?.toString() ?: name,
                aliases = row["aliases"]?.toString(),
                category = row["category"]?.toString(),
                severity = row["severity"]?.toString()
            )
        } catch (e: Exception) {
            Log.w("NocoDb", "Allergen lookup unavailable", e)
            null
        }
    }
}

data class NocoAllergen(
    val name: String,
    val aliases: String?,
    val category: String?,
    val severity: String?
)