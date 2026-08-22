package com.example.shendet.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.shendet.data.model.ProductAnalysis
import com.example.shendet.data.repository.UnifiedProductRepository
import kotlinx.coroutines.launch
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetNutritionScreen(
    repository: UnifiedProductRepository,
    capturedBitmap: Bitmap? = null,
    onNavigateToScanner: () -> Unit,
    onNavigateToAddProduct: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var analysis by remember { mutableStateOf<ProductAnalysis?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var isOfflineMode by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Mbrojtësi i Kafshëve", fontWeight = FontWeight.Bold)
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToScanner,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Rounded.QrCodeScanner, contentDescription = "Skano")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (analysis == null && !isLoading && error == null) {
                PetNutritionDashboard(
                    onSearchClick = { query ->
                        scope.launch {
                            val isOnline = com.example.shendet.util.NetworkUtils.isNetworkAvailable(context)
                            if (!isOnline) {
                                error = "Lidhja me internetin mungon. Kërkimi kërkon internet."
                                return@launch
                            }

                            /* if (apiKey.isBlank()) {
                                error = "Kërkohet API_KEY. Ju lutem shtoni 'API_KEY' në skedarin local.properties."
                                return@launch
                            } */
                            isLoading = true
                            repository.analyzeProduct(query, target = "pet")
                                .onSuccess { 
                                    analysis = it 
                                    isOfflineMode = false
                                }
                                .onFailure { error = it.message }
                            isLoading = false
                        }
                    }
                )
            } else if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Duke analizuar me AI Router...", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Icon(Icons.Rounded.ErrorOutline, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val displayError = when {
                            error!!.contains("404") || error!!.contains("decommissioned") || error!!.contains("deprecated") || error!!.contains("model_not_found") -> 
                                "Ju lutem skanoni vetëm barkodin"
                            error!!.contains("403") -> "Gabim lejeje (403). Çelësi juaj API mund të jetë i pavlefshëm."
                            error!!.contains("401") -> "Gabim autentikimi (401). Çelësi API është i gabuar."
                            error!!.contains("nuk u gjet") || error!!.contains("not found") -> 
                                "KY PRODUKT NUK ESHTE NE DATA BAZEN TONE ,JU LUTEM SHTOHENI TEK DRITARJA SHTO PRODUKT"
                            else -> "Gabim: $error"
                        }
                        
                        Text(displayError, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (displayError.contains("SHTO PRODUKT")) {
                            Button(onClick = onNavigateToAddProduct) {
                                Icon(Icons.Rounded.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Shto Produkt")
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        
                        Button(onClick = { error = null }, colors = ButtonDefaults.outlinedButtonColors()) { Text("Provo Përsëri") }
                    }
                }
            } else if (analysis != null) {
                PetAnalysisContent(analysis!!, capturedBitmap) {
                    analysis = null
                }
            }
        }
    }
}

@Composable
fun PetNutritionDashboard(onSearchClick: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.Pets,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Sigurohuni që ushqimi i kafshëve tuaja të jetë i sigurt dhe i shëndetshëm.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Kërko produktin për kafshë") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { if (searchQuery.isNotBlank()) onSearchClick(searchQuery) }) {
                        Icon(Icons.Rounded.Search, contentDescription = "Kërko")
                    }
                },
                shape = RoundedCornerShape(16.dp)
            )
        }

        item {
            Text("Këshilla të Shpejta", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        items(listOf(
            "Mos i jepni kurrë çokollatë qenve tuaj.",
            "Qepa dhe hudhra janë toksike për macet dhe qentë.",
            "Rrushti mund të shkaktojë dështim të veshkave.",
            "Avokado përmban persin, i cili është i dëmshëm për shumë kafshë."
        )) { keshilla ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(keshilla, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun PetAnalysisContent(analysis: ProductAnalysis, capturedBitmap: Bitmap?, onReset: () -> Unit) {
    val isWarning = analysis.healthImpact.startsWith("KUJDES", ignoreCase = true) || analysis.healthImpact.startsWith("GABIM", ignoreCase = true)
    val displayImpact = analysis.healthImpact.removePrefix("GABIM_KATEGORIA: ")

    val scoreColor = when {
        analysis.score >= 8 -> Color(0xFF009688)
        analysis.score >= 5 -> Color(0xFFFFC107)
        else -> Color(0xFFE91E63)
    }
    
    val statusIcon = when {
        analysis.score >= 8 -> Icons.Rounded.SentimentVerySatisfied
        analysis.score >= 5 -> Icons.Rounded.SentimentDissatisfied
        else -> Icons.Rounded.SentimentVeryDissatisfied
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (capturedBitmap != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Image(
                        bitmap = capturedBitmap.asImageBitmap(),
                        contentDescription = "Foto e produktit",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = scoreColor.copy(alpha = 0.12f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
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
                                    colors = listOf(scoreColor.copy(alpha = 0.9f), scoreColor),
                                    center = Offset(40f, 40f)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Depth shadow for icon
                            Icon(
                                statusIcon, 
                                contentDescription = null, 
                                tint = Color.Black.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .size(64.dp)
                                    .offset(y = 3.dp, x = 2.dp)
                            )
                            // Main icon
                            Icon(
                                statusIcon, 
                                contentDescription = null, 
                                tint = Color.White,
                                modifier = Modifier
                                    .size(64.dp)
                                    .offset(y = (-3).dp)
                            )
                            Text(
                                "${analysis.score * 10}%",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.offset(y = (-4).dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(analysis.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                    Text(analysis.grade, style = MaterialTheme.typography.titleLarge, color = scoreColor, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Text("Vlerësimi për Sigurinë", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isWarning) Color(0xFFFFEBEE) else scoreColor.copy(alpha = 0.05f)
                ),
                border = if (isWarning) androidx.compose.foundation.BorderStroke(2.dp, Color.Red) else null
            ) {
                Text(
                    displayImpact, 
                    modifier = Modifier.padding(20.dp), 
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isWarning) Color.Red else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (isWarning) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        item {
            Button(
                onClick = onReset,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Kërko një tjetër")
            }
        }
    }
}
