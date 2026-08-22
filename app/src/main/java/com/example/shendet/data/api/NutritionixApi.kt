package com.example.shendet.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

// ── Response models ────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class NixSearchResponse(
    @Json(name = "branded") val branded: List<NixBrandedItem>?,
    @Json(name = "common") val common: List<NixCommonItem>?
)

@JsonClass(generateAdapter = true)
data class NixBrandedItem(
    @Json(name = "food_name") val foodName: String?,
    @Json(name = "brand_name") val brandName: String?,
    @Json(name = "nix_item_id") val nixItemId: String?,
    @Json(name = "nix_brand_id") val nixBrandId: String?,
    @Json(name = "upc") val upc: String?,
    @Json(name = "photo") val photo: NixPhoto?,
    @Json(name = "nf_calories") val calories: Double?,
    @Json(name = "serving_qty") val servingQty: Double?,
    @Json(name = "serving_unit") val servingUnit: String?
)

@JsonClass(generateAdapter = true)
data class NixCommonItem(
    @Json(name = "food_name") val foodName: String?,
    @Json(name = "photo") val photo: NixPhoto?
)

@JsonClass(generateAdapter = true)
data class NixPhoto(
    @Json(name = "thumb") val thumb: String?
)

@JsonClass(generateAdapter = true)
data class NixNutrientsResponse(
    @Json(name = "foods") val foods: List<NixFoodNutrient>?
)

@JsonClass(generateAdapter = true)
data class NixFoodNutrient(
    @Json(name = "food_name") val foodName: String?,
    @Json(name = "brand_name") val brandName: String?,
    @Json(name = "upc") val upc: String?,
    @Json(name = "serving_qty") val servingQty: Double?,
    @Json(name = "serving_unit") val servingUnit: String?,
    @Json(name = "serving_weight_grams") val servingWeightGrams: Double?,
    @Json(name = "nf_calories") val calories: Double?,
    @Json(name = "nf_total_fat") val totalFat: Double?,
    @Json(name = "nf_saturated_fat") val saturatedFat: Double?,
    @Json(name = "nf_cholesterol") val cholesterol: Double?,
    @Json(name = "nf_sodium") val sodium: Double?,
    @Json(name = "nf_total_carbohydrate") val totalCarbs: Double?,
    @Json(name = "nf_dietary_fiber") val fiber: Double?,
    @Json(name = "nf_sugars") val sugars: Double?,
    @Json(name = "nf_protein") val protein: Double?,
    @Json(name = "nf_potassium") val potassium: Double?,
    @Json(name = "nf_ingredient_statement") val ingredients: String?,
    @Json(name = "photo") val photo: NixPhoto?,
    @Json(name = "full_nutrients") val fullNutrients: List<NixFullNutrient>?
)

@JsonClass(generateAdapter = true)
data class NixFullNutrient(
    @Json(name = "attr_id") val attrId: Int?,
    @Json(name = "value") val value: Double?
)

@JsonClass(generateAdapter = true)
data class NixNaturalQuery(
    @Json(name = "query") val query: String,
    @Json(name = "timezone") val timezone: String = "US/Eastern"
)

@JsonClass(generateAdapter = true)
data class NixItemQuery(
    @Json(name = "upc") val upc: String? = null,
    @Json(name = "nix_item_id") val nixItemId: String? = null
)

// ── Retrofit interface ─────────────────────────────────────────────────────────

interface NutritionixApi {

    /** Instant search — returns branded + common results */
    @GET("v2/search/instant")
    suspend fun searchInstant(
        @Header("x-app-id") appId: String,
        @Header("x-app-key") appKey: String,
        @Query("query") query: String,
        @Query("branded") branded: Boolean = true,
        @Query("common") common: Boolean = false,
        @Query("branded_food_name_only") brandedFoodNameOnly: Boolean = false
    ): NixSearchResponse

    /** Get full nutrient info for a branded item or common food by natural language */
    @POST("v2/nutrients")
    suspend fun getNutrients(
        @Header("x-app-id") appId: String,
        @Header("x-app-key") appKey: String,
        @Body query: NixNaturalQuery
    ): NixNutrientsResponse

    /** Look up a branded item by UPC barcode */
    @POST("v2/search/item")
    suspend fun getItemByUpc(
        @Header("x-app-id") appId: String,
        @Header("x-app-key") appKey: String,
        @Body query: NixItemQuery
    ): NixNutrientsResponse
}
