package com.example.shendet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shendet.data.model.ProductComparison
import com.example.shendet.data.model.ComparisonReport
import com.example.shendet.data.model.LookupSource

@Composable
fun ProductComparisonScreen(
    modifier: Modifier = Modifier,
    comparison: ProductComparison? = null,
    report: ComparisonReport? = null,
    isLoading: Boolean = false
) {
    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        comparison?.let { comp ->
            item {
                ProductComparisonCard(comparison = comp)
            }
        }

        report?.let { rep ->
            item {
                ComparisonReportSummary(report = rep)
            }
            items(rep.comparisons) { comp ->
                ProductComparisonCard(comparison = comp)
            }
        }
    }
}

@Composable
fun ProductComparisonCard(
    comparison: ProductComparison,
    modifier: Modifier = Modifier,
    onAddProductClick: () -> Unit = {}
) {
    val uriHandler = LocalUriHandler.current

    // NEW: Show "Product Not Found" card
    if (!comparison.isProductFound) {
        ProductNotFoundCard(
            comparison = comparison,
            modifier = modifier,
            onAddProductClick = {
                uriHandler.openUri("https://world.openfoodfacts.org/cgi/product.pl")
            }
        )
        return
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // NEW: Source badge (Barcode vs Photo)
            LookupSourceBadge(source = comparison.lookupSource)

            // Header with product name and score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = comparison.productName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "AI Score: ${comparison.aiAnalysis.score}/10 (${comparison.aiAnalysis.grade})",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                ConcordanceScore(score = comparison.comparisonScore)
            }

            HorizontalDivider()

            // Concordance status
            ConcordanceStatus(concordance = comparison.concordance)

            // Transparent explanation inspired by consumer-safety apps:
            // show the reasons instead of presenting an unexplained score.
            ScoreExplanationCard(analysis = comparison.aiAnalysis)

            // Discrepancies
            if (comparison.discrepancies.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "⚠️ Ndryshime të Gjetura",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFC62828)
                    )
                    comparison.discrepancies.forEach { discrepancy ->
                        Text(
                            text = "• $discrepancy",
                            fontSize = 12.sp,
                            color = Color(0xFFC62828)
                        )
                    }
                }
            }

            // Additive discrepancies
            if (comparison.concordance.additiveDiscrepancies.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "🔬 Ndryshime në Shtesa",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE65100)
                    )
                    comparison.concordance.additiveDiscrepancies.forEach { discrepancy ->
                        Text(
                            text = "• $discrepancy",
                            fontSize = 12.sp,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }

            // Recommendations
            if (comparison.recommendations.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "💡 Rekomandime",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2E7D32)
                    )
                    comparison.recommendations.forEach { recommendation ->
                        Text(
                            text = "• $recommendation",
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            // OFF Data Summary
            comparison.offData?.let { offData ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "📊 OpenFoodFacts",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1565C0)
                    )
                    offData.nutriscore?.let {
                        Text(
                            text = "Nutriscore: $it",
                            fontSize = 12.sp,
                            color = Color(0xFF1565C0)
                        )
                    }
                    offData.ecoscore?.let {
                        Text(
                            text = "EcoScore: $it",
                            fontSize = 12.sp,
                            color = Color(0xFF1565C0)
                        )
                    }
                    if (!offData.additives.isNullOrEmpty()) {
                        Text(
                            text = "Shtesa: ${offData.additives.size}",
                            fontSize = 12.sp,
                            color = Color(0xFF1565C0)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreExplanationCard(
    analysis: com.example.shendet.data.model.ProductAnalysis
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Pse ky rezultat?",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1565C0)
            )
            Text(
                text = "Rezultati bazohet në përbërësit, ndikimin në shëndet dhe të dhënat e databazës.",
                fontSize = 12.sp,
                color = Color(0xFF334155)
            )
            analysis.positives.take(3).forEach {
                Text(text = "✓ $it", fontSize = 12.sp, color = Color(0xFF2E7D32))
            }
            analysis.negatives.take(3).forEach {
                Text(text = "⚠ $it", fontSize = 12.sp, color = Color(0xFFC62828))
            }
            Text(
                text = "Burimi i analizës: databaza + Ollama AI. Verifiko etiketën përfundimtare.",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

@Composable
fun ConcordanceScore(
    score: Float,
    modifier: Modifier = Modifier
) {
    val color = when {
        score >= 80f -> Color(0xFF4CAF50)  // Green
        score >= 60f -> Color(0xFFFFC107)  // Amber
        else -> Color(0xFFf44336)          // Red
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(60.dp),
            shape = RoundedCornerShape(12.dp),
            color = color.copy(alpha = 0.2f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${score.toInt()}%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
        Text(
            text = if (score >= 75f) "✓ Concordant" else "✗ Discordant",
            fontSize = 10.sp,
            color = color,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun ConcordanceStatus(
    concordance: com.example.shendet.data.model.ComparianceConcordance,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatusBadge(
            label = "Emri",
            isMatch = concordance.nameMatch
        )
        StatusBadge(
            label = "Përbërës",
            isMatch = concordance.ingredientsMatch
        )
        StatusBadge(
            label = "Pikëz.",
            isMatch = concordance.scoreAlignment == "HIGH"
        )
    }
}

@Composable
fun StatusBadge(
    label: String,
    isMatch: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .background(
                if (isMatch) Color(0xFFC8E6C9) else Color(0xFFFFCDD2),
                RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        color = Color.Transparent
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (isMatch) Icons.Filled.Check else Icons.Filled.Close,
                contentDescription = label,
                modifier = Modifier.size(14.dp),
                tint = if (isMatch) Color(0xFF388E3C) else Color(0xFFC62828)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isMatch) Color(0xFF388E3C) else Color(0xFFC62828)
            )
        }
    }
}

// NEW: Composable to display when product is not found
@Composable
fun ProductNotFoundCard(
    comparison: ProductComparison,
    modifier: Modifier = Modifier,
    onAddProductClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Source badge
            LookupSourceBadge(source = comparison.lookupSource)

            // Large warning icon
            Text(
                text = "🔍",
                fontSize = 48.sp
            )

            // Main message
            Text(
                text = "Produkti nuk u gjet në data baze",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100)
            )

            // Groq Analysis (what was found in the image/input)
            if (comparison.aiAnalysis.name.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFE0B2), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "✓ Cila u detektua:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = comparison.aiAnalysis.name,
                            fontSize = 14.sp,
                            color = Color(0xFFE65100)
                        )
                        if (comparison.aiAnalysis.healthImpact.isNotEmpty()) {
                            Text(
                                text = comparison.aiAnalysis.healthImpact,
                                fontSize = 11.sp,
                                color = Color(0xFFF57C00)
                            )
                        }
                    }
                }
            }

            // Error message
            if (comparison.errorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))
                ) {
                    Text(
                        text = comparison.errorMessage,
                        fontSize = 12.sp,
                        color = Color(0xFFC62828),
                        modifier = Modifier.padding(12.dp),
                        lineHeight = 18.sp
                    )
                }
            }

            // Recommendations
            if (comparison.recommendations.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF9C4), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "💡 Rekomandime",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF57F17)
                    )
                    comparison.recommendations.forEach { rec ->
                        Text(
                            text = "• $rec",
                            fontSize = 11.sp,
                            color = Color(0xFFF57F17)
                        )
                    }
                }
            }

            // Action buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAddProductClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE65100)
                    )
                ) {
                    Text(
                        "Shto produktin në OpenFoodFacts 🌐",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "Klikoje butonin më lart për të shtuar produktin në databazën e përgjithshme",
                    fontSize = 10.sp,
                    color = Color(0xFFF57F17),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

// NEW: Badge showing lookup source (Barcode vs Photo)
@Composable
fun LookupSourceBadge(
    source: LookupSource,
    modifier: Modifier = Modifier
) {
    val (icon, label, color) = when (source) {
        LookupSource.BARCODE -> Triple("📊", "Barcode Scan", Color(0xFF1565C0))
        LookupSource.PHOTO -> Triple("📸", "Photo Analysis", Color(0xFFF57C00))
        LookupSource.MANUAL_INPUT -> Triple("✏️", "Manual Input", Color(0xFF6B8E23))
    }

    Surface(
        modifier = modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        color = Color.Transparent
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = icon,
                fontSize = 14.sp
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

@Composable
fun ComparisonReportSummary(
    report: ComparisonReport,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "📋 Përmbledhje Krahasimi",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Produktet të analizuara: ${report.comparisons.size}",
                fontSize = 14.sp,
                color = Color.White
            )
            Text(
                text = "Konkordanca e përgjithshme: ${report.overallConcordance.toInt()}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            val concordantCount = report.comparisons.count { it.isConcordant }
            Text(
                text = "Produktet konkordant: $concordantCount/${report.comparisons.size}",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

