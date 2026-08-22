package com.example.shendet.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// ── Response models ────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class FdcSearchResponse(
    @Json(name = "foods") val foods: List<FdcFoodItem>?,
    @Json(name = "totalHits") val totalHits: Int?
)

@JsonClass(generateAdapter = true)
data class FdcFoodItem(
    @Json(name = "fdcId") val fdcId: Int?,
    @Json(name = "description") val description: String?,
    @Json(name = "brandOwner") val brandOwner: String?,
    @Json(name = "brandName") val brandName: String?,
    @Json(name = "gtinUpc") val gtinUpc: String?,           // barcode (UPC/EAN)
    @Json(name = "ingredients") val ingredients: String?,
    @Json(name = "foodCategory") val foodCategory: String?,
    @Json(name = "foodNutrients") val foodNutrients: List<FdcNutrient>?
)

@JsonClass(generateAdapter = true)
data class FdcNutrient(
    @Json(name = "nutrientId") val nutrientId: Int?,
    @Json(name = "nutrientName") val nutrientName: String?,
    @Json(name = "unitName") val unitName: String?,
    @Json(name = "value") val value: Double?
)

@JsonClass(generateAdapter = true)
data class FdcFoodDetail(
    @Json(name = "fdcId") val fdcId: Int?,
    @Json(name = "description") val description: String?,
    @Json(name = "brandOwner") val brandOwner: String?,
    @Json(name = "brandName") val brandName: String?,
    @Json(name = "gtinUpc") val gtinUpc: String?,
    @Json(name = "ingredients") val ingredients: String?,
    @Json(name = "foodCategory") val foodCategory: String?,
    @Json(name = "foodNutrients") val foodNutrients: List<FdcNutrient>?
)

// ── Retrofit interface ─────────────────────────────────────────────────────────

interface FoodDataCentralApi {

    /** Search by keyword or product name */
    @GET("v1/foods/search")
    suspend fun searchFoods(
        @Query("query") query: String,
        @Query("api_key") apiKey: String,
        @Query("pageSize") pageSize: Int = 5,
        @Query("dataType") dataType: String = "Branded,Foundation,SR Legacy"
    ): FdcSearchResponse

    /** Search by UPC/EAN barcode — pass barcode as query term with gtinUpc filter */
    @GET("v1/foods/search")
    suspend fun searchByBarcode(
        @Query("query") barcode: String,
        @Query("api_key") apiKey: String,
        @Query("pageSize") pageSize: Int = 3
    ): FdcSearchResponse

    /** Fetch full nutrient details for a single food by FDC ID */
    @GET("v1/food/{fdcId}")
    suspend fun getFoodDetail(
        @Path("fdcId") fdcId: Int,
        @Query("api_key") apiKey: String
    ): FdcFoodDetail
}
