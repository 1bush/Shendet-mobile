package com.example.shendet.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    target: String = "food",
    onNavigateToScanner: (String) -> Unit,
    barcode: String?,
    frontBitmap: Bitmap?,
    ingredientsBitmap: Bitmap?,
    onBack: () -> Unit,
    onSubmit: (String, String, Bitmap?, Bitmap?, String) -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Shto Produkt të Ri", fontWeight = FontWeight.Bold)
                        Text(
                            if (target == "cosmetics") "Kategoria: Kozmetikë" else "Kategoria: Ushqim",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Progress Indicator
            LinearProgressIndicator(
                progress = { step / 4f },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            )

            Text(
                "Hapi $step nga 4",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            when (step) {
                1 -> StepBarcode(barcode, onNavigateToScanner, onNext = { step = 2 })
                2 -> StepPhoto("Fotoja e Produktit (Para)", frontBitmap, onTakePhoto = { onNavigateToScanner("add_front") }, onNext = { step = 3 })
                3 -> StepPhoto("Fotoja e Përbërësve", ingredientsBitmap, onTakePhoto = { onNavigateToScanner("add_ingredients") }, onNext = { step = 4 })
                4 -> StepFinal(productName, onNameChange = { productName = it }, onSubmit = {
                    if (barcode.isNullOrBlank()) {
                        // Should not happen due to step 1 validation, but safety check
                        return@StepFinal
                    }
                    onSubmit(productName, barcode, frontBitmap, ingredientsBitmap, target)
                })
            }
        }
    }
}

@Composable
fun StepBarcode(barcode: String?, onScan: (String) -> Unit, onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Icon(Icons.Rounded.QrCodeScanner, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
        Text("Skanoni Barkodin", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        Text("Filloni duke skanuar barkodin e produktit që dëshironi të shtoni.", textAlign = TextAlign.Center)
        
        if (barcode != null) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Text(barcode, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
            }
            Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) { Text("Vazhdo") }
        } else {
            Button(onClick = { onScan("add_barcode") }, modifier = Modifier.fillMaxWidth()) { Text("Hap Skanerin") }
        }
    }
}

@Composable
fun StepPhoto(title: String, bitmap: Bitmap?, onTakePhoto: () -> Unit, onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(200.dp).clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onTakePhoto, modifier = Modifier.weight(1f)) { Text("Ndrysho") }
                Button(onClick = onNext, modifier = Modifier.weight(1f)) { Text("Vazhdo") }
            }
        } else {
            Box(
                modifier = Modifier.size(200.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.AddAPhoto, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(onClick = onTakePhoto, modifier = Modifier.fillMaxWidth()) { Text("Bëj Foto") }
        }
    }
}

@Composable
fun StepFinal(name: String, onNameChange: (String) -> Unit, onSubmit: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Detajet e Fundit", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Emri i Produktit") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = name.isNotBlank(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Dërgo në Open Data", fontWeight = FontWeight.Bold)
        }
    }
}
