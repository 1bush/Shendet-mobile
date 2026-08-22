package com.example.shendet.data.api

import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class NocoListResponse<T>(
    val list: List<T> = emptyList(),
    val page: Int? = null,
    val pageSize: Int? = null,
    val totalRows: Int? = null
)

@JsonClass(generateAdapter = true)
data class NocoProductRecord(
    val barcode: String? = null,
    val name: String? = null,
    val brand: String? = null,
    val category: String? = null,
    val ingredients: String? = null,
    val source: String? = null,
    val score: Double? = null
)

@JsonClass(generateAdapter = true)
data class NocoAllergenRecord(
    val name: String? = null,
    val aliases: String? = null,
    val category: String? = null,
    val severity: String? = null
)

@JsonClass(generateAdapter = true)
data class NocoInciRecord(
    val name: String? = null,
    val function: String? = null,
    val risk: String? = null,
    val explanation: String? = null,
    val categories: String? = null
)

interface NocoDbApi {
    @GET("api/v1/db/data/v1/{org}/{base}/{table}")
    suspend fun listRecords(
        @Header("xc-token") token: String,
        @Path("org") org: String,
        @Path("base") base: String,
        @Path("table") table: String,
        @Query("where") where: String? = null,
        @Query("limit") limit: Int = 25
    ): Response<NocoListResponse<Map<String, Any?>>>
}