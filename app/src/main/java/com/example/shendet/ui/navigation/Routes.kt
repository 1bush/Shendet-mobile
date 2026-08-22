package com.example.shendet.ui.navigation

import kotlinx.serialization.Serializable
import androidx.navigation3.runtime.NavKey

@Serializable
sealed interface Route : NavKey {
    @Serializable
    data object Dashboard : Route

    @Serializable
    data class Scanner(val target: String = "general") : Route

    @Serializable
    data class Analysis(
        val productId: String, 
        val target: String = "general", 
        val useImage: Boolean = false,
        val hasIngredients: Boolean = false
    ) : Route

    @Serializable
    data object PetNutrition : Route

    @Serializable
    data object AllergyProfile : Route

    @Serializable
    data object History : Route

    @Serializable
    data object Comparison : Route

    @Serializable
    data object VitaminsMinerals : Route

    @Serializable
    data object Composition : Route

    @Serializable
    data class AddProduct(val target: String = "food") : Route
}
