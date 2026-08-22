package com.example.shendet.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductComparison(
    val productName: String,
    val aiAnalysis: ProductAnalysis,
    val offData: OFFProductData?,
    val comparisonScore: Float, // 0-100, how well they match
    val concordance: ComparianceConcordance,
    val discrepancies: List<String>,
    val recommendations: List<String>,
    val lookupSource: LookupSource = LookupSource.BARCODE,  // NEW: Barcode vs Photo
    val isProductFound: Boolean = true,  // NEW: Track if product was found
    val errorMessage: String? = null  // NEW: Error message in Albanian
) {
    val isConcordant: Boolean get() = comparisonScore >= 75f
}

// NEW: Enum to distinguish barcode vs photo lookup
enum class LookupSource {
    BARCODE,      // From barcode scanner
    PHOTO,        // From product photo
    MANUAL_INPUT  // From user manual input
}

@JsonClass(generateAdapter = true)
data class OFFProductData(
    val productName: String?,
    val ingredients: String?,
    val nutriscore: String?,
    val ecoscore: String?,
    val brands: String?,
    val categories: String?,
    val additives: List<String>?,
    val nutrientLevels: Map<String, String>?
)

@JsonClass(generateAdapter = true)
data class ComparianceConcordance(
    val nameMatch: Boolean,
    val ingredientsMatch: Boolean,
    val scoreAlignment: String, // HIGH, MEDIUM, LOW
    val offAdditives: List<String> = emptyList(),
    val aiIdentifiedHarmful: List<String> = emptyList(),
    val additiveDiscrepancies: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ComparisonReport(
    val timestamp: Long,
    val comparisons: List<ProductComparison>,
    val overallConcordance: Float // average score
)
