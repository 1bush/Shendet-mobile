package com.example.shendet.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

@JsonClass(generateAdapter = true)
data class OFFResponse(
    @Json(name = "product") val product: OFFProduct?,
    @Json(name = "products") val products: List<OFFProduct>?, // Added for search
    @Json(name = "status") val status: Int?,
    @Json(name = "count") val count: Int?
)

@JsonClass(generateAdapter = true)
data class OFFProduct(
    @Json(name = "product_name") val productName: String?,
    @Json(name = "ingredients_text") val ingredients: String?,
    @Json(name = "nutriscore_grade") val nutriscore: String?,
    @Json(name = "nutrition_grades") val nutritionGrades: String?, // Alternative field name
    @Json(name = "image_url") val imageUrl: String?,
    @Json(name = "image_front_url") val imageFrontUrl: String?,
    @Json(name = "image_small_url") val imageSmallUrl: String?,
    @Json(name = "brands") val brands: String?,
    @Json(name = "categories") val categories: String?,
    @Json(name = "additives_tags") val additives: List<String>?,
    @Json(name = "ecoscore_grade") val ecoscore: String?,
    @Json(name = "nutrient_levels") val nutrientLevels: Map<String, String>?,
    @Json(name = "nutriments") val nutriments: Map<String, Any>? // For flexible nutrient reading
)

@JsonClass(generateAdapter = true)
data class OFFWriteRequest(
    @Json(name = "user_id") val userId: String = "off",
    @Json(name = "password") val password: String = "off",
    @Json(name = "product") val product: OFFWriteProduct
)

@JsonClass(generateAdapter = true)
data class OFFWriteProduct(
    @Json(name = "product_name") val productName: String,
    @Json(name = "lang") val lang: String = "sq",
    @Json(name = "brands") val brands: String? = null,
    @Json(name = "categories") val categories: String? = null
)

interface OpenFoodFactsApi {
    @GET("api/v2/product/{barcode}.json")
    suspend fun getProduct(@Path("barcode") barcode: String): OFFResponse

    @GET("cgi/search.pl?json=1")
    suspend fun searchProducts(
        @Query("search_terms") query: String,
        @Query("page_size") pageSize: Int = 24
    ): OFFResponse

    @POST("cgi/product_jqm2.pl")
    @FormUrlEncoded
    suspend fun addProduct(
        @Field("code") barcode: String,
        @Field("product_name") name: String,
        @Field("comment") comment: String = "Shtuar nga aplikacioni SHENDET",
        @Field("user_id") userId: String = "off",
        @Field("password") password: String = "off"
    ): retrofit2.Response<Unit>

    // API v3 - Recommended for updates
    @PATCH("api/v3/product/{barcode}")
    suspend fun updateProduct(
        @Path("barcode") barcode: String,
        @Body request: OFFWriteRequest
    ): retrofit2.Response<Map<String, Any>> // Use Map to inspect structured response

    @Multipart
    @POST("cgi/product_image_upload.pl")
    suspend fun uploadImage(
        @Part("code") barcode: RequestBody,
        @Part("imagefield") imageField: RequestBody,
        @Part("user_id") userId: RequestBody,
        @Part("password") password: RequestBody,
        @Part image: MultipartBody.Part
    ): retrofit2.Response<Map<String, Any>>
}
