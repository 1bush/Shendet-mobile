package com.example.shendet.data.repository

import android.util.Log
import com.example.shendet.data.api.OpenFoodFactsApi
import com.example.shendet.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Normalizer
import kotlin.math.max

class ProductComparisonService {
    /**
     * Compares AI analysis with OpenFoodFacts/OpenBeautyFacts data for concordance
     */
    suspend fun compareProductWithOFF(
        barcode: String,
        aiAnalysis: ProductAnalysis,
        lookupSource: LookupSource = LookupSource.BARCODE,
        api: OpenFoodFactsApi
    ): Result<ProductComparison> = withContext(Dispatchers.IO) {
        try {
            // Fetch data from the provided API (OFF or OBF)
            val response = try {
                api.getProduct(barcode).product
            } catch (e: Exception) {
                Log.w("ProductComparison", "API lookup failed: ${e.message}")
                null
            }

            val offData = response?.let {
                OFFProductData(
                    productName = it.productName,
                    ingredients = it.ingredients,
                    nutriscore = it.nutriscore ?: it.nutritionGrades, // Try alternative field
                    ecoscore = it.ecoscore,
                    brands = it.brands,
                    categories = it.categories,
                    additives = it.additives,
                    nutrientLevels = it.nutrientLevels
                )
            }
            // ... the rest remains the same ...

            // Check if product was found in OpenFoodFacts
            val isProductFound = offData?.productName != null

            // Perform comparison
            val comparison = performComparison(
                aiAnalysis,
                offData,
                lookupSource = lookupSource,
                isProductFound = isProductFound
            )
            Result.success(comparison)
        } catch (e: Exception) {
            Log.e("ProductComparison", "Error comparing product", e)
            Result.failure(e)
        }
    }

    private fun performComparison(
        aiAnalysis: ProductAnalysis,
        offData: OFFProductData?,
        lookupSource: LookupSource = LookupSource.BARCODE,
        isProductFound: Boolean = true
    ): ProductComparison {
        val discrepancies = mutableListOf<String>()
        val recommendations = mutableListOf<String>()
        var matchScore = 50f

        // NEW: Generate error message if product not found
        var errorMessage: String? = null
        if (!isProductFound) {
            errorMessage = "ðŸ” Produkti nuk u gjet nÃ« data baze.\n\n" +
                    "TÃ« lutem, shto produktin nÃ« OpenFoodFacts:\n" +
                    "ðŸ“± https://world.openfoodfacts.org/cgi/product.pl\n\n" +
                    "Ose kontakto administratorin."
            recommendations.add("âš ï¸ Produkti nuk ekziston nÃ« bazÃ«n e tÃ« dhÃ«nave")
            if (lookupSource == LookupSource.BARCODE) {
                recommendations.add("ðŸ’¡ Ky barcode nuk gjendet - siguro qÃ« barcode Ã«shtÃ« i saktÃ«")
            } else {
                recommendations.add("ðŸ’¡ Kjo fotografi nuk u pÃ«rshÃ«mbye me asnjÃ« produkt nÃ« bazÃ«")
            }
        }

        // 1. Name matching
        val nameMatch = offData?.productName?.let {
            normalizeText(it).contains(normalizeText(aiAnalysis.name)) ||
            normalizeText(aiAnalysis.name).contains(normalizeText(it))
        } ?: false

        if (nameMatch) {
            matchScore += 20f
        } else if (offData?.productName != null) {
            discrepancies.add("Emri nuk pÃ«rputhet: AI='${aiAnalysis.name}' vs OFF='${offData.productName}'")
            matchScore -= 15f
        }

        // 2. Ingredients comparison
        val ingredientsMatch = offData?.ingredients?.let { offIngredients ->
            val aiHarmful = aiAnalysis.ingredientsList
                .filter { it.isHarmful }
                .map { normalizeText(it.name) }
                .toSet()

            val offAdditives = offData.additives?.map { normalizeText(it) }?.toSet() ?: emptySet()
            val offIngredientsNormalized = normalizeText(offIngredients)

            // Check overlap between AI harmful findings and OFF additives
            val harmfulFound = aiHarmful.any { harmful ->
                offAdditives.any { it.contains(harmful) || harmful.contains(it) } ||
                offIngredientsNormalized.contains(harmful)
            }

            harmfulFound || aiHarmful.isEmpty() || offAdditives.isEmpty()
        } ?: true

        if (ingredientsMatch) {
            matchScore += 15f
        } else {
            discrepancies.add("Ndryshim nÃ« pÃ«rputhjen e pÃ«rbÃ«rÃ«sve tÃ« dÃ«mshÃ«m")
            matchScore -= 10f
        }

        // 3. Nutritional score alignment
        val scoreAlignment = compareScores(aiAnalysis.score, offData?.nutriscore)
        when (scoreAlignment) {
            "HIGH" -> matchScore += 10f
            "MEDIUM" -> matchScore += 5f
            "LOW" -> {
                matchScore -= 5f
                discrepancies.add("PikÃ«zimi i ndryshÃ«m: AI=${aiAnalysis.score} vs Nutriscore=${offData?.nutriscore}")
            }
        }

        // 4. Analyze additives
        val concordance = ComparianceConcordance(
            nameMatch = nameMatch,
            ingredientsMatch = ingredientsMatch,
            scoreAlignment = scoreAlignment,
            offAdditives = offData?.additives ?: emptyList(),
            aiIdentifiedHarmful = aiAnalysis.ingredientsList
                .filter { it.isHarmful }
                .map { it.name },
            additiveDiscrepancies = findAdditiveDiscrepancies(aiAnalysis, offData)
        )

        // 5. Generate recommendations
        if (matchScore < 60f) {
            recommendations.add("âš ï¸ Konkordanca e ulÃ«t - verifikoni manualisht produktin")
        }
        if (offData?.additives?.isNotEmpty() == true) {
            recommendations.add("Produkti pÃ«rmban ${offData.additives.size} shtesa - shqyrtoni me kujdes")
        }
        if (aiAnalysis.negatives.isNotEmpty()) {
            recommendations.add("AI ka identifikuar ${aiAnalysis.negatives.size} efekte negative")
        }
        if (offData?.nutriscore?.uppercase() in listOf("D", "E")) {
            recommendations.add("Nutriscore ${offData?.nutriscore} - kualitet nutritiv i ulÃ«t")
        }

        // Ensure score is within bounds
        matchScore = max(0f, minOf(100f, matchScore))

        return ProductComparison(
            productName = aiAnalysis.name,
            aiAnalysis = aiAnalysis,
            offData = offData,
            comparisonScore = matchScore,
            concordance = concordance,
            discrepancies = discrepancies,
            recommendations = recommendations,
            lookupSource = lookupSource,  // NEW
            isProductFound = isProductFound,  // NEW
            errorMessage = errorMessage  // NEW
        )
    }

    /**
     * Compare AI score (1-10) with Nutriscore (A-E)
     */
    private fun compareScores(aiScore: Int, nutriscore: String?): String {
        if (nutriscore == null) return "LOW"

        val nutriscoreValue = when (nutriscore.uppercase()) {
            "A" -> 9
            "B" -> 7
            "C" -> 5
            "D" -> 3
            "E" -> 1
            else -> 5
        }

        val difference = kotlin.math.abs(aiScore - nutriscoreValue)
        return when {
            difference <= 1 -> "HIGH"
            difference <= 2 -> "MEDIUM"
            else -> "LOW"
        }
    }

    private fun findAdditiveDiscrepancies(
        aiAnalysis: ProductAnalysis,
        offData: OFFProductData?
    ): List<String> {
        val discrepancies = mutableListOf<String>()

        val aiHarmful = aiAnalysis.ingredientsList
            .filter { it.isHarmful }
            .map { normalizeText(it.name) }
            .toSet()

        val offAdditives = offData?.additives
            ?.map { normalizeText(it) }
            ?.toSet() ?: emptySet()

        // Harmful identified by AI but not in OFF
        val aiOnly = aiHarmful.subtract(offAdditives)
        if (aiOnly.isNotEmpty()) {
            discrepancies.add("AI identifikoi shtesa tÃ« dÃ«mshme qÃ« nuk janÃ« nÃ« OFF: $aiOnly")
        }

        // Harmful in OFF but not identified by AI
        val offOnly = offAdditives.subtract(aiHarmful)
        if (offOnly.isNotEmpty()) {
            discrepancies.add("OFF tregon shtesa tÃ« dÃ«mshme qÃ« Groq nuk identifikoi: $offOnly")
        }

        return discrepancies
    }

    private fun normalizeText(text: String): String {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace(Regex("[^\\p{ASCII}]"), "")
            .lowercase()
            .trim()
    }

    /**
     * Compare multiple products
     */
    suspend fun compareMultipleProducts(
        products: List<Pair<String, ProductAnalysis>>,
        api: OpenFoodFactsApi
    ): Result<ComparisonReport> = withContext(Dispatchers.IO) {
        try {
            val comparisons = mutableListOf<ProductComparison>()
            
            for ((barcode, analysis) in products) {
                val result = compareProductWithOFF(barcode, analysis, api = api)
                if (result.isSuccess) {
                    comparisons.add(result.getOrNull()!!)
                }
            }

            val overallConcordance = if (comparisons.isNotEmpty()) {
                comparisons.map { it.comparisonScore }.average().toFloat()
            } else {
                0f
            }

            val report = ComparisonReport(
                timestamp = System.currentTimeMillis(),
                comparisons = comparisons,
                overallConcordance = overallConcordance
            )

            Result.success(report)
        } catch (e: Exception) {
            Log.e("ProductComparison", "Error in batch comparison", e)
            Result.failure(e)
        }
    }
}


