## SHENDET Product Comparison - Quick Reference

### 🚀 Quick Start

#### 1. Single Product Comparison
```kotlin
val repository = UnifiedProductRepository(groqApiKey, muleRouterApiKey)
val result = repository.analyzeAndCompareProduct("5201360066862")
val comparison = result.getOrNull()
```

#### 2. Multiple Products
```kotlin
val result = repository.compareMultipleBarcodesWithOFF(
    listOf("5201360066862", "5000159615119")
)
val report = result.getOrNull()
```

### 📊 Understanding the Scores

| Score | Status | Action |
|-------|--------|--------|
| 80-100% | ✓ Highly Concordant | Safe to use data |
| 60-79% | ⚠️ Moderately Concordant | Review differences |
| 0-59% | ✗ Discordant | Manual verification needed |

### 🔍 What Gets Compared

1. **Product Name**
   - Groq analysis vs OpenFoodFacts
   - Must closely match for +20% score

2. **Ingredients**
   - Harmful ingredients (Groq) vs Additives (OFF)
   - Matches increase score by +15%

3. **Nutritional Score**
   - Groq score (1-10) vs Nutriscore (A-E)
   - Close alignment = +10% bonus

### ⚠️ Discrepancies

The system identifies:
- **Name mismatches**: Different product names across sources
- **Additive differences**: Harmful ingredients found in one source but not the other
- **Score variations**: Different health ratings
- **Missing data**: Products not in OpenFoodFacts database

### 🎯 Common Scenarios

**Scenario 1: High Concordance (90%)**
- Names match perfectly
- Same harmful ingredients identified
- Nutritional scores aligned
- ✅ Data is reliable

**Scenario 2: Medium Concordance (65%)**
- Names similar but not exact
- Some ingredient discrepancies
- Scores moderately aligned
- ⚠️ Review manually, some data may be inconsistent

**Scenario 3: Low Concordance (35%)**
- Names don't match
- Major ingredient differences
- Very different scores
- ❌ Product may not exist in OFF or data quality issues

### 📱 UI Components

**ProductComparisonScreen**
- Display single or batch comparisons
- Visual score indicators
- Discrepancy highlighting
- Recommendations

**StatusBadge**
- Name match status
- Ingredients match status
- Score alignment status

**ConcordanceScore**
- Large visual score display
- Color-coded (green/amber/red)
- Match/mismatch indicator

### 🔧 API Endpoints Used

**Groq AI**
- POST /v1/chat/completions
- Models: llama-3.3-70b-versatile, llama-3.2-90b-vision-preview

**OpenFoodFacts**
- GET /api/v2/product/{barcode}.json
- GET /cgi/search.pl?json=1

### ⚡ Performance Tips

- Single product: ~3-5 seconds
- Batch operations: ~5-15 seconds (depends on number of products)
- First request slower (API initialization)
- Subsequent requests faster

### 🛠️ Integration Steps

1. **Add to ViewModel**
   ```kotlin
   val repository = UnifiedProductRepository(apiKey, muleKey)
   ```

2. **Call comparison**
   ```kotlin
   val result = repository.analyzeAndCompareProduct(barcode)
   ```

3. **Display results**
   ```kotlin
   ProductComparisonScreen(
       comparison = result.getOrNull(),
       isLoading = false
   )
   ```

### 🐛 Troubleshooting

**Issue: "OFF data not found"**
- Product not in OpenFoodFacts database
- Solution: Continue with Groq analysis only

**Issue: Low score despite good data**
- Name variations (e.g., "Product XYZ" vs "Product X Y Z")
- Different ingredient bases
- Solution: Manual review of recommendations

**Issue: API timeout**
- Network slow or API overloaded
- Solution: Retry after 2-3 seconds

### 📋 Data Models Quick Reference

```kotlin
ProductComparison
├── productName: String
├── groqAnalysis: ProductAnalysis
├── offData: OFFProductData?
├── comparisonScore: Float (0-100)
├── concordance: ComparianceConcordance
├── discrepancies: List<String>
├── recommendations: List<String>
└── isConcordant: Boolean (score >= 75)

ComparisonReport
├── timestamp: Long
├── comparisons: List<ProductComparison>
└── overallConcordance: Float (0-100)
```

### 🌐 Supported Languages

- **Albanian** (default) - ალბ
- System uses Albanian for analysis, UI, and recommendations

### 📞 Support

For issues:
1. Check API keys are valid
2. Verify network connectivity
3. Check API key quotas
4. Review error messages in logcat

---
**Last Updated**: July 2026
**Version**: 1.0

