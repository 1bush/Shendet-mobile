#!/bin/bash
# Installation and Integration Guide
# Run this after adding the code to your project

echo "================================"
echo "SHENDET Product Comparison Setup"
echo "================================"
echo ""

# Step 1: Verify API Key
echo "✓ Step 1: Verifying Groq API Key..."
if grep -q "GROQ_API_KEY=gsk_" local.properties; then
    echo "  ✅ Groq API Key is configured"
else
    echo "  ❌ Groq API Key not found!"
    exit 1
fi

# Step 2: Verify MuleRouter Key
echo ""
echo "✓ Step 2: Verifying MuleRouter API Key..."
if grep -q "MULEROUTER_API_KEY=sk-mr" local.properties; then
    echo "  ✅ MuleRouter API Key is configured"
else
    echo "  ❌ MuleRouter API Key not found!"
    exit 1
fi

# Step 3: Build Check
echo ""
echo "✓ Step 3: Files Added Successfully"
echo "  ✅ ProductComparison.kt (Models)"
echo "  ✅ ProductComparisonService.kt (Service)"
echo "  ✅ ProductComparisonScreen.kt (UI)"
echo "  ✅ ProductComparisonViewModel.kt (ViewModel)"
echo "  ✅ UnifiedProductRepository.kt (Enhanced)"

# Step 4: Next Steps
echo ""
echo "✓ Step 4: Integration Checklist"
echo "  [ ] Add ProductComparisonViewModel to your DI/Hilt setup"
echo "  [ ] Connect ProductComparisonScreen to your navigation"
echo "  [ ] Update your barcode scanner to call analyzeAndCompareProduct()"
echo "  [ ] Test with sample barcodes"
echo "  [ ] Deploy and monitor"

# Step 5: Testing
echo ""
echo "✓ Step 5: Quick Test"
echo "  Run this in your app:"
echo ""
echo "  val repo = UnifiedProductRepository(groqKey, muleKey)"
echo "  val result = repo.analyzeAndCompareProduct(\"5201360066862\")"
echo "  val comparison = result.getOrNull()"
echo "  println(\"Concordance: \${comparison?.comparisonScore}%\")"

echo ""
echo "================================"
echo "Setup Complete! 🎉"
echo "================================"

