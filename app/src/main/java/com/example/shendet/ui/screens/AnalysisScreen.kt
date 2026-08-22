package com.example.shendet.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shendet.data.model.ProductAnalysis
import com.example.shendet.data.repository.AllergyRepository
import com.example.shendet.data.repository.UnifiedProductRepository
import com.example.shendet.data.repository.HistoryRepository
import com.example.shendet.data.local.entity.ScanHistoryEntity
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer

import com.example.shendet.util.NetworkUtils
import com.example.shendet.util.ImageStorageUtils
import com.example.shendet.ui.components.GifImage
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import android.os.Build
import coil.request.ImageRequest
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    productId: String,
    target: String = "general",
    useImage: Boolean = false,
    capturedBitmap: Bitmap? = null,
    ingredientsBitmap: Bitmap? = null,
    unifiedRepository: UnifiedProductRepository,
    allergyRepository: AllergyRepository,
    historyRepository: HistoryRepository,
    onAddIngredients: () -> Unit,
    onNavigateToAddProduct: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    var analysis by remember { mutableStateOf<ProductAnalysis?>(null) }
    var isLoading by remember { mutableStateOf(value = true) }
    var error by remember { mutableStateOf<String?>(null) }
    var allergyWarnings by remember { mutableStateOf<List<String>>(emptyList()) }
    var isOfflineMode by remember { mutableStateOf(false) }

    LaunchedEffect(productId, useImage, ingredientsBitmap) {
        val isOnline = NetworkUtils.isNetworkAvailable(context)
        
        if (!isOnline && !useImage) {
            // Offline mode: try to fetch from local history
            val cachedScan = historyRepository.getScan(productId)
            if (cachedScan != null) {
                analysis = ProductAnalysis(
                    name = cachedScan.name,
                    score = cachedScan.score,
                    grade = cachedScan.grade,
                    healthImpact = cachedScan.healthImpact,
                    positives = cachedScan.positives.split(",").filter { it.isNotBlank() },
                    negatives = cachedScan.negatives.split(",").filter { it.isNotBlank() },
                    vitamins = cachedScan.vitamins.split(",").filter { it.isNotBlank() },
                    minerals = cachedScan.minerals.split(",").filter { it.isNotBlank() },
                    alternatives = emptyList(),
                    ingredientsList = emptyList(),
                    productImageUrl = null
                )
                isOfflineMode = true
                isLoading = false
                return@LaunchedEffect
            } else {
                error = "Lidhja me internetin mungon dhe produkti nuk është në historik."
                isLoading = false
                return@LaunchedEffect
            }
        }

        /* if (apiKey.isBlank()) {
            error = "Kërkohet API_KEY në local.properties"
            isLoading = false
            return@LaunchedEffect
        } */
        
        isLoading = true
        
        val result = if (ingredientsBitmap != null) {
            unifiedRepository.analyzeImages(capturedBitmap, ingredientsBitmap, target)
        } else if (useImage && (capturedBitmap != null)) {
            unifiedRepository.analyzeImages(capturedBitmap, null, target)
        } else {
            unifiedRepository.analyzeProduct(productId, target)
        }

        result.onSuccess { data ->
            analysis = data
            
            val enabled = allergyRepository.getEnabledAllergies()
            val warnings = mutableListOf<String>()
            enabled.forEach { allergy ->
                if (data.healthImpact.contains(allergy, ignoreCase = true) || 
                    data.negatives.any { it.contains(allergy, ignoreCase = true) }) {
                    warnings.add(allergy)
                }
            }
            allergyWarnings = warnings

            val savedImagePath = (ingredientsBitmap ?: capturedBitmap)?.let {
                ImageStorageUtils.saveBitmapToInternalStorage(context, it)
            }

            historyRepository.addScan(
                ScanHistoryEntity(
                    productId = productId,
                    name = data.name,
                    score = data.score,
                    grade = data.grade,
                    nutriScore = data.nutriScore,
                    healthImpact = data.healthImpact,
                    positives = data.positives.joinToString(","),
                    negatives = data.negatives.joinToString(","),
                    vitamins = data.vitamins.joinToString(","),
                    minerals = data.minerals.joinToString(","),
                    imagePath = savedImagePath
                )
            )
            isLoading = false
        }.onFailure {
            error = it.message ?: "Gabim i panjohur"
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Rezultati i Analizës", fontWeight = FontWeight.Bold)
                        if (isOfflineMode) {
                            Text("Modaliteti Offline", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Kthehu")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (isLoading) {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Duke analizuar me databaza dhe Ollama AI...", style = MaterialTheme.typography.bodyLarge)
                }
            } else if (error != null) {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.ErrorOutline, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val displayError = when {
                        error!!.contains("TE LUTEM SKANONI NE KATEGORINE PERKATESE") -> 
                            "TE LUTEM SKANONI NE KATEGORINE PERKATESE"
                        error!!.contains("404") || error!!.contains("decommissioned") || error!!.contains("deprecated") || error!!.contains("model_not_found") -> 
                            "Ju lutem skanoni vetëm barkodin"
                        error!!.contains("403") -> "Gabim lejeje (403). Çelësi juaj API mund të jetë i pavlefshëm."
                        error!!.contains("401") -> "Gabim autentikimi (401). Çelësi API është i gabuar."
                        error!!.contains("nuk u gjet") || error!!.contains("not found") || error!!.contains("shqipe") || error!!.contains("503") || error!!.contains("500") ->
                            "KY PRODUKT NUK ESHTE NE DATA BAZEN TONE ,JU LUTEM SHTOHENI TEK DRITARJA SHTO PRODUKT"
                        else -> "Gabim: $error"
                    }
                    
                    Text(
                        text = displayError,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    if (displayError.contains("SHTO PRODUKT")) {
                        Button(onClick = onNavigateToAddProduct, modifier = Modifier.padding(top = 16.dp)) {
                            Icon(Icons.Rounded.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Shto Produkt")
                        }
                    }
                    
                    Button(onClick = onBack, modifier = Modifier.padding(top = 12.dp), colors = ButtonDefaults.outlinedButtonColors()) { Text("Provo Përsëri") }
                }
            } else if (analysis != null) {
                AnalysisContent(analysis!!, allergyWarnings, capturedBitmap, onAddIngredients)
            }
        }
    }
}

@Composable
fun AnalysisContent(
    analysis: ProductAnalysis, 
    allergyWarnings: List<String>,
    capturedBitmap: Bitmap?,
    onAddIngredients: () -> Unit
) {
    val impactUpper = analysis.healthImpact.uppercase()
    val isWarning = analysis.healthImpact.startsWith("KUJDES", ignoreCase = true) || 
                    analysis.healthImpact.startsWith("GABIM", ignoreCase = true) ||
                    impactUpper.contains("TOKSIK") || 
                    impactUpper.contains("RREZIK") ||
                    impactUpper.contains("GABIM_KATEGORIA")
    
    val displayImpact = analysis.healthImpact.removePrefix("GABIM_KATEGORIA: ")
    
    val scoreColor = when {
        isWarning -> Color(0xFFE91E63)
        analysis.score >= 8 -> Color(0xFF009688)
        analysis.score >= 5 -> Color(0xFFFFC107)
        else -> Color(0xFFE91E63)
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (capturedBitmap != null || analysis.productImageUrl != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    if (capturedBitmap != null) {
                        androidx.compose.foundation.Image(
                            bitmap = capturedBitmap.asImageBitmap(),
                            contentDescription = "Foto e produktit",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        AsyncImage(
                            model = analysis.productImageUrl,
                            contentDescription = "Foto e produktit nga databaza",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }
                }
            }
        }

        if (allergyWarnings.isNotEmpty()) {
            item { AllergyWarningCard(allergyWarnings) }
        }
        item { ScoreHeader(analysis, scoreColor) }
        item {
            Text("Ndikimi në Shëndet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isWarning) Color(0xFFFFEBEE) else scoreColor.copy(alpha = 0.08f)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(top = 8.dp),
                border = if (isWarning) androidx.compose.foundation.BorderStroke(2.dp, Color.Red) else null
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        displayImpact, 
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isWarning) Color.Red else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isWarning) FontWeight.Bold else FontWeight.Normal
                    )
                    
                    if (isWarning) {
                        Spacer(modifier = Modifier.height(16.dp))
                        GifImage(
                            data = "https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Symbols/Warning.png",
                            modifier = Modifier.size(48.dp)
                        )
                    } else if (analysis.positives.isEmpty() && analysis.negatives.isEmpty()) {
                        // Product not well known, offer to add ingredients photo
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onAddIngredients,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                        ) {
                            Icon(Icons.Rounded.Camera, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Shto foto të përbërësve")
                        }
                    }
                }
            }
        }

        if (analysis.alternatives.isNotEmpty()) {
            item { AnalysisSectionTitle("Alternativa më të Mirë", "https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Light%20Bulb.png") }
            items(analysis.alternatives) { alt ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color(0xFF009688))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(alt, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        
        item { AnalysisSectionTitle("Pikat Pozitive", "https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Symbols/Check%20Mark%20Button.png") }
        items(analysis.positives) { AnalysisItem(it, Color(0xFF009688)) }

        item { AnalysisSectionTitle("Pikat Negative", "https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Symbols/Cross%20Mark.png") }
        items(analysis.negatives) { AnalysisItem(it, Color(0xFFE91E63)) }

        if (analysis.ingredientsList.isNotEmpty()) {
            item { AnalysisSectionTitle("Lista e Përbërësve", "https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Memo.png") }
            items(analysis.ingredientsList) { ingredient ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (ingredient.isHarmful) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surface
                    ),
                    border = if (ingredient.isHarmful) androidx.compose.foundation.BorderStroke(1.dp, Color.Red) else null
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (ingredient.isHarmful) Color.Red else Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = ingredient.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (ingredient.isHarmful) FontWeight.Bold else FontWeight.Normal,
                                color = if (ingredient.isHarmful) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        ingredient.description?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 20.dp, top = 4.dp),
                                color = if (ingredient.isHarmful) Color.Red.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AllergyWarningCard(warnings: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.error)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            GifImage(
                data = "https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Symbols/Warning.png",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("KUJDES: Alergjenë!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                Text("Ky produkt përmban: ${warnings.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun ScoreHeader(analysis: ProductAnalysis, color: Color) {
    val gifRes = when {
        analysis.score >= 8 -> "https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Smilies/Grinning%20Face%20with%20Big%20Eyes.png" // Placeholder URL for GIF-like behavior
        analysis.score >= 5 -> "https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Smilies/Neutral%20Face.png"
        else -> "https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Smilies/Pleading%20Face.png"
    }

    Card(
        modifier = Modifier.fillMaxWidth(), 
        shape = RoundedCornerShape(32.dp), 
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier.padding(24.dp), 
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer {
                        shadowElevation = 20f
                        shape = CircleShape
                        clip = true
                    }
                    .background(
                        Brush.radialGradient(
                            colors = listOf(color.copy(alpha = 0.9f), color),
                            center = Offset(40f, 40f)
                        )
                    ), 
                contentAlignment = Alignment.Center
            ) {
                GifImage(
                    data = gifRes,
                    modifier = Modifier.size(80.dp)
                )
            }
            Spacer(modifier = Modifier.width(24.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        analysis.name, 
                        style = MaterialTheme.typography.headlineSmall, 
                        fontWeight = FontWeight.Black,
                        lineHeight = 28.sp,
                        modifier = Modifier.weight(1f)
                    )
                    analysis.nutriScore?.let { score ->
                        NutriScoreBadge(score.uppercase())
                    }
                }
                Text(
                    analysis.grade, 
                    style = MaterialTheme.typography.titleLarge, 
                    color = color, 
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun NutriScoreBadge(score: String) {
    val (bgColor, textColor) = when (score) {
        "A" -> Color(0xFF008000) to Color.White
        "B" -> Color(0xFF80C000) to Color.White
        "C" -> Color(0xFFFFCC00) to Color.Black
        "D" -> Color(0xFFEE8100) to Color.White
        "E" -> Color(0xFFE63E11) to Color.White
        else -> Color.Gray to Color.White
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            text = score,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = textColor
        )
    }
}

@Composable
fun AnalysisSectionTitle(title: String, gifUrl: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
        GifImage(data = gifUrl, modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AnalysisItem(text: String, color: Color) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
