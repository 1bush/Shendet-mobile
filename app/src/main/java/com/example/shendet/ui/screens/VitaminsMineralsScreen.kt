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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.shendet.R
import com.example.shendet.data.model.ProductAnalysis
import com.example.shendet.data.repository.UnifiedProductRepository
import kotlinx.coroutines.launch
import com.example.shendet.util.NetworkUtils
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitaminsMineralsScreen(
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
                        Text("Vitamina & Minerale", fontWeight = FontWeight.Bold)
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
                VitaminsDashboard(
                    onSearchClick = { query ->
                        scope.launch {
                            val isOnline = NetworkUtils.isNetworkAvailable(context)
                            if (!isOnline) {
                                error = "Lidhja me internetin mungon. Kërkimi kërkon internet."
                                return@launch
                            }

                            /* if (apiKey.isBlank()) {
                                error = "Kërkohet API_KEY në local.properties"
                                return@launch
                            } */
                            isLoading = true
                            repository.analyzeProduct(query, target = "vitamins")
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
                VitaminsAnalysisContent(analysis!!, capturedBitmap) {
                    analysis = null
                }
            }
        }
    }
}

@Composable
fun VitaminsDashboard(onSearchClick: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth().height(160.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.food_pyramid),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().alpha(0.3f),
                        contentScale = ContentScale.Crop
                    )
                    Row(
                        modifier = Modifier.padding(24.dp).align(Alignment.CenterStart),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Zbulo vlerat ushqyese më të mira.",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Kërko për ushqim ose suplement") },
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
            Text("Top Burimet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        items(listOf(
            "Spinaqi - I pasur me Hekur dhe Vitaminë K (Rate: 90%)",
            "Salmoni - Burim i shkëlqyer i Vitaminës D (Rate: 100%)",
            "Bajamet - Të pasura me Magnez dhe Vitaminë E (Rate: 80%)",
            "Portokalli - I mbushur me Vitaminë C (Rate: 90%)"
        )) { keshilla ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Star, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(keshilla, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun VitaminsAnalysisContent(analysis: ProductAnalysis, capturedBitmap: Bitmap?, onReset: () -> Unit) {
    val isWarning = analysis.healthImpact.startsWith("KUJDES", ignoreCase = true) || analysis.healthImpact.startsWith("GABIM", ignoreCase = true)
    val displayImpact = analysis.healthImpact.removePrefix("GABIM_KATEGORIA: ")

    val scoreColor = when {
        isWarning -> Color(0xFFE91E63)
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
                            .size(140.dp)
                            .graphicsLayer {
                                shadowElevation = 30f
                                shape = CircleShape
                                clip = true
                            }
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(scoreColor.copy(alpha = 0.85f), scoreColor),
                                    center = Offset(50f, 50f)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // 3D Shadow icon
                            Icon(
                                statusIcon,
                                contentDescription = null,
                                tint = Color.Black.copy(alpha = 0.25f),
                                modifier = Modifier
                                    .size(72.dp)
                                    .offset(y = 4.dp, x = 2.dp)
                            )
                            // Foreground Icon
                            Icon(
                                statusIcon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(72.dp)
                                    .offset(y = (-4).dp)
                            )
                            Text(
                                "${analysis.score * 10}%",
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.offset(y = (-8).dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(analysis.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text(analysis.grade, style = MaterialTheme.typography.titleLarge, color = scoreColor, fontWeight = FontWeight.Bold)
                }
            }
        }

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
                }
            }
        }

        if (analysis.vitamins.isNotEmpty() || analysis.minerals.isNotEmpty()) {
            item {
                Text("Përmbajtja e Vitaminave & Mineraleve", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    (analysis.vitamins + analysis.minerals).forEach { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color(0xFF009688), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(item, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
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
