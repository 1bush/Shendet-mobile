package com.example.shendet.data.repository

/** Common contract for barcode and ingredient data providers. */
interface ProductDataSource {
    suspend fun findByBarcode(barcode: String): ProductLookup?
    suspend fun search(query: String): ProductLookup?
}

enum class DataSource {
    OPEN_FOOD_FACTS,
    YUKA,
    FOOD_DATA_CENTRAL,
    INCI_BEAUTY,
    OPEN_BEAUTY_FACTS,
    OLLAMA
}

data class ProductLookup(
    val name: String,
    val brand: String? = null,
    val ingredients: String? = null,
    val nutrients: String? = null,
    val source: DataSource,
    val confidence: Float
)

data class InciIngredientResult(
    val ingredient: String,
    val risk: InciRisk,
    val explanation: String
)

enum class InciRisk { LOW, MEDIUM, HIGH }

/** Offline safety hints for common shampoo/cosmetic ingredients. */
class InciIngredientDataSource : ProductDataSource {
    private val rules = listOf(
        Rule("methylisothiazolinone", InciRisk.HIGH, "Konservues që mund të shkaktojë sensibilizim dhe alergji."),
        Rule("methylchloroisothiazolinone", InciRisk.HIGH, "Konservues i lidhur me irritim dhe reaksione alergjike."),
        Rule("parfum", InciRisk.MEDIUM, "Aromë; mund të irritojë lëkurën sensitive."),
        Rule("fragrance", InciRisk.MEDIUM, "Aromë; mund të irritojë lëkurën sensitive."),
        Rule("sodium laureth sulfate", InciRisk.MEDIUM, "Surfaktant që mund të thajë ose irritojë lëkurën sensitive."),
        Rule("sodium lauryl sulfate", InciRisk.MEDIUM, "Surfaktant pastrues; mund të shkaktojë tharje ose irritim."),
        Rule("limonene", InciRisk.MEDIUM, "Alergjen aromatik i deklarueshëm në kozmetikë."),
        Rule("linalool", InciRisk.MEDIUM, "Alergjen aromatik i deklarueshëm në kozmetikë."),
        Rule("paraben", InciRisk.MEDIUM, "Familje konservuesish që kërkon vlerësim sipas llojit dhe përqendrimit.")
    )

    fun analyze(ingredients: String): List<InciIngredientResult> {
        val normalized = ingredients.lowercase()
        return rules.filter { normalized.contains(it.term) }.map {
            InciIngredientResult(it.term, it.risk, it.explanation)
        }
    }

    override suspend fun findByBarcode(barcode: String): ProductLookup? = null
    override suspend fun search(query: String): ProductLookup? = null

    private data class Rule(val term: String, val risk: InciRisk, val explanation: String)
}