# Product Comparison Feature - Documentation
te 

## Overview

This feature enables the SHENDET app to analyze products using Groq AI and compare the results with OpenFoodFacts data to ensure data concordance and identify discrepancies.

## Features

### 1. **Single Product Analysis & Comparison**
- Analyzes a product using Groq AI
- Fetches corresponding data from OpenFoodFacts
- Compares results and generates a concordance score (0-100)

### 2. **Batch Product Comparison**
- Compare multiple products at once
- Generates a comparison report with overall concordance metrics
- Identifies patterns across multiple products

### 3. **Concordance Scoring**
The system calculates a concordance score based on:
- **Name Matching** (+20%): Validates if product names match across sources
- **Ingredients Matching** (+15%): Compares harmful ingredients identified by Groq with OpenFoodFacts additives
- **Score Alignment** (+10%): Aligns Groq score (1-10) with Nutriscore (A-E)

### 4. **Detailed Discrepancies Report**
Identifies:
- Product name mismatches
- Harmful ingredient discrepancies
- Nutritional score variations
- Additive detection differences

## Usage

### Setup

The API keys are already configured in `local.properties`:
```properties
GROQ_API_KEY=gsk_VENDOS_CELESIN_TEND_KETU
MULEROUTER_API_KEY=sk-mr-...
```

### Integration in Code

#### Single Product Comparison
```kotlin
val repository = UnifiedProductRepository(groqApiKey, muleRouterApiKey)

// Analyze a product and compare with OpenFoodFacts
val result = repository.analyzeAndCompareProduct(
    barcode = "5201360066862",
    target = "food"  // or "cosmetics", "pet", "vitamins"
)

if (result.isSuccess) {
    val comparison = result.getOrNull()
    // comparison.comparisonScore  // 0-100
    // comparison.isConcordant     // true if score >= 75
    // comparison.discrepancies    // List of differences found
    // comparison.recommendations  // Suggested actions
}
```

#### Multiple Product Comparison
```kotlin
val barcodes = listOf(
    "5201360066862",
    "5000159615119",
    "8001234567890"
)

val reportResult = repository.compareMultipleBarcodesWithOFF(
    barcodes = barcodes,
    target = "food"
)

if (reportResult.isSuccess) {
    val report = reportResult.getOrNull()
    println("Overall Concordance: ${report?.overallConcordance}%")
    println("Products Compared: ${report?.comparisons?.size}")
}
```

### Data Models

#### ProductComparison
```kotlin
data class ProductComparison(
    val productName: String,
    val groqAnalysis: ProductAnalysis,        // Groq AI analysis
    val offData: OFFProductData?,             // OpenFoodFacts data
    val comparisonScore: Float,               // 0-100
    val concordance: ComparianceConcordance,  // Detailed match metrics
    val discrepancies: List<String>,          // Differences found
    val recommendations: List<String>         // Suggested actions
)
```

#### ComparianceConcordance
```kotlin
data class ComparianceConcordance(
    val nameMatch: Boolean,
    val ingredientsMatch: Boolean,
    val scoreAlignment: String,               // HIGH, MEDIUM, LOW
    val offAdditives: List<String>,
    val groqIdentifiedHarmful: List<String>,
    val additiveDiscrepancies: List<String>
)
```

#### ComparisonReport (for batch operations)
```kotlin
data class ComparisonReport(
    val timestamp: Long,
    val comparisons: List<ProductComparison>,
    val overallConcordance: Float             // Average score across all products
)
```

## UI Component

The `ProductComparisonScreen` composable provides a complete UI for displaying:
- Individual product comparisons with scores
- Concordance status indicators
- Discrepancies and discordances
- OpenFoodFacts data summary
- Actionable recommendations

### Usage in Compose
```kotlin
ProductComparisonScreen(
    comparison = productComparison,
    report = comparisonReport,
    isLoading = isLoading,
    modifier = Modifier.fillMaxSize()
)
```

## Scoring Logic

### Concordance Score Calculation
1. **Base Score**: 50%
2. **Name Match**: +20% (if product names align)
3. **Ingredients Match**: +15% (if harmful ingredients align)
4. **Score Alignment**: +10% (if Groq and Nutriscore align closely)

**Total Range**: 0-100%

### Score Interpretation
- **80-100%**: ✓ Highly Concordant (green)
- **60-79%**: ⚠️ Moderately Concordant (amber)
- **0-59%**: ✗ Discordant (red) - Manual review recommended

## Supported Product Categories

The system supports analysis for:
- **food**: Food products (default)
- **cosmetics**: Cosmetic and personal care products
- **pet**: Pet food and treats
- **vitamins**: Dietary supplements and vitamins

## Error Handling

All operations return `Result<T>` types:
```kotlin
when {
    result.isSuccess -> {
        val comparison = result.getOrNull()
        // Process comparison
    }
    result.isFailure -> {
        val error = result.exceptionOrNull()
        Log.e("Comparison", "Error: ${error?.message}")
    }
}
```

## Performance Considerations

- **Single product comparison**: ~3-5 seconds (depends on Groq/OpenFoodFacts response)
- **Batch operations**: Sequential processing for reliability
- **Network timeouts**: 45 seconds for API calls
- **MuleRouter fallback**: Automatically used if Groq fails

## API Integrations

### Groq AI
- **Model**: llama-3.3-70b-versatile (text), llama-3.2-90b-vision-preview (vision)
- **Endpoint**: https://api.groq.com/openai/v1/chat/completions
- **Language**: Albanian ( albania)

### OpenFoodFacts
- **Endpoint**: https://world.openfoodfacts.org/
- **Data**: Barcodes, ingredients, Nutriscore, additives, nutritional info

### MuleRouter (Fallback)
- **Model**: meta-llama/llama-3.3-70b-instruct
- **Endpoint**: https://api.mulerouter.ai/

## Troubleshooting

### No OpenFoodFacts Data Found
- Some products may not be in the OpenFoodFacts database
- The comparison will still work with Groq analysis only
- Discrepancies will reflect "OFF data not available"

### Low Concordance Score
- Could indicate product name variations across databases
- May reveal missing or incorrect ingredient data in one source
- Manual verification is recommended

### API Key Errors
- Verify GROQ_API_KEY is correctly set in local.properties
- Check network connectivity
- Ensure API key has sufficient quota

## Future Enhancements

- [ ] Image-based product comparison
- [ ] Allergenic component tracking
- [ ] Historical trend analysis
- [ ] User feedback integration
- [ ] Batch import from CSV
- [ ] Export comparison reports

## Files Added

- `ProductComparison.kt` - Data models for comparison results
- `ProductComparisonService.kt` - Core comparison logic
- `ProductComparisonScreen.kt` - UI components
- Updated `UnifiedProductRepository.kt` - Added comparison methods

