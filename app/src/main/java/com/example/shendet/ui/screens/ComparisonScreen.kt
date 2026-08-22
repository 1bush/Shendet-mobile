package com.example.shendet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.CompareArrows
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.shendet.data.local.entity.ScanHistoryEntity
import com.example.shendet.data.repository.HistoryRepository
import com.example.shendet.data.repository.UnifiedProductRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonScreen(
    repository: HistoryRepository,
    unifiedRepository: UnifiedProductRepository,
    barcodeA: String? = null,
    barcodeB: String? = null,
    onNavigateToScanner: (String) -> Unit,
    onBarcodeConsumed: (String) -> Unit = {},
    onBack: () -> Unit
) {
    val history by repository.allHistory.collectAsState(initial = emptyList())
    var selectedProduct1 by remember { mutableStateOf<ScanHistoryEntity?>(null) }
    var selectedProduct2 by remember { mutableStateOf<ScanHistoryEntity?>(null) }
    var showSelectionDialog by remember { mutableStateOf<Int?>(null) } // 1 or 2
    var isLoadingA by remember { mutableStateOf(false) }
    var isLoadingB by remember { mutableStateOf(false) }

    // Analyze pending barcode for slot A
    LaunchedEffect(barcodeA) {
        if (barcodeA != null) {
            isLoadingA = true
            val result = unifiedRepository.analyzeProduct(barcodeA, target = "food")
            result.onSuccess { data ->
                selectedProduct1 = ScanHistoryEntity(
                    productId = barcodeA,
                    name = data.name,
                    score = data.score,
                    grade = data.grade,
                    nutriScore = data.nutriScore,
                    healthImpact = data.healthImpact,
                    positives = data.positives.joinToString(","),
                    negatives = data.negatives.joinToString(","),
                    vitamins = data.vitamins.joinToString(","),
                    minerals = data.minerals.joinToString(",")
                )
            }
            isLoadingA = false
            onBarcodeConsumed("A")
        }
    }

    // Analyze pending barcode for slot B
    LaunchedEffect(barcodeB) {
        if (barcodeB != null) {
            isLoadingB = true
            val result = unifiedRepository.analyzeProduct(barcodeB, target = "food")
            result.onSuccess { data ->
                selectedProduct2 = ScanHistoryEntity(
                    productId = barcodeB,
                    name = data.name,
                    score = data.score,
                    grade = data.grade,
                    nutriScore = data.nutriScore,
                    healthImpact = data.healthImpact,
                    positives = data.positives.joinToString(","),
                    negatives = data.negatives.joinToString(","),
                    vitamins = data.vitamins.joinToString(","),
                    minerals = data.minerals.joinToString(",")
                )
            }
            isLoadingB = false
            onBarcodeConsumed("B")
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Krahasimi i Produkteve") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Kthehu")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ComparisonSlot(
                    product = selectedProduct1,
                    label = "Produkti A",
                    modifier = Modifier.weight(1f),
                    onScan = { onNavigateToScanner("compareA") },
                    onClick = { showSelectionDialog = 1 },
                    isLoading = isLoadingA
                )
                ComparisonSlot(
                    product = selectedProduct2,
                    label = "Produkti B",
                    modifier = Modifier.weight(1f),
                    onScan = { onNavigateToScanner("compareB") },
                    onClick = { showSelectionDialog = 2 },
                    isLoading = isLoadingB
                )
            }

            if (selectedProduct1 != null && selectedProduct2 != null) {
                ComparisonResult(selectedProduct1!!, selectedProduct2!!)
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Zgjidhni dy produkte nga historiku ose skanojini ato për t'i krahasuar",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }

    if (showSelectionDialog != null) {
        AlertDialog(
            onDismissRequest = { showSelectionDialog = null },
            title = { Text("Zgjidh Produktin") },
            text = {
                if (history.isEmpty()) {
                    Text("Nuk ka produkte në historik.")
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                        items(history) { item ->
                            ListItem(
                                headlineContent = { Text(item.name) },
                                supportingContent = { Text("Pikët: ${item.score * 10}%") },
                                modifier = Modifier.clickable {
                                    if (showSelectionDialog == 1) selectedProduct1 = item
                                    else selectedProduct2 = item
                                    showSelectionDialog = null
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSelectionDialog = null }) {
                    Text("Mbyll")
                }
            }
        )
    }
}

@Composable
fun ComparisonSlot(
    product: ScanHistoryEntity?,
    label: String,
    modifier: Modifier,
    onScan: () -> Unit,
    onClick: () -> Unit,
    isLoading: Boolean = false
) {
    val color = if (product != null) {
        when {
            product.score >= 8 -> Color(0xFF009688)
            product.score >= 5 -> Color(0xFFFFC107)
            else -> Color(0xFFE91E63)
        }
    } else MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = modifier.height(250.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Duke analizuar...", style = MaterialTheme.typography.labelSmall)
            } else if (product == null) {
                IconButton(onClick = onScan, modifier = Modifier.size(64.dp)) {
                    Icon(Icons.Rounded.QrCodeScanner, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onClick, shape = RoundedCornerShape(12.dp)) {
                    Text("Zgjidh", style = MaterialTheme.typography.labelSmall)
                }
                Text(label, style = MaterialTheme.typography.labelLarge)
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(color)
                        .clickable { onClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${product.score * 10}%",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                IconButton(onClick = onScan) {
                    Icon(Icons.Rounded.QrCodeScanner, contentDescription = "Scan New")
                }
            }
        }
    }
}

@Composable
fun ComparisonResult(p1: ScanHistoryEntity, p2: ScanHistoryEntity) {
    val winner = if (p1.score >= p2.score) p1 else p2
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.AutoMirrored.Rounded.CompareArrows, contentDescription = null, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Opsioni më i shëndetshëm është:",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                winner.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Ky produkt ka një vlerësim prej ${winner.score * 10}%.",
                textAlign = TextAlign.Center
            )
        }
    }
}
