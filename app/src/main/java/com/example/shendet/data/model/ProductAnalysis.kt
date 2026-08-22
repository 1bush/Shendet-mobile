package com.example.shendet.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductAnalysis(
    val name: String,
    val score: Int,
    val grade: String,
    val nutriScore: String? = null, // Nutri-Score A, B, C, D, E
    val healthImpact: String,
    val positives: List<String>,
    val negatives: List<String>,
    val vitamins: List<String>,
    val minerals: List<String>,
    val alternatives: List<String> = emptyList(),
    val ingredientsList: List<IngredientDetail> = emptyList(),
    val productImageUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class IngredientDetail(
    val name: String,
    val isHarmful: Boolean,
    val description: String? = null
)
