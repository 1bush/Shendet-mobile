package com.example.shendet.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompositionScreen(
    onNavigateToCamera: () -> Unit,
    onAnalyzeText: (String) -> Unit,
    onBack: () -> Unit
) {
    var ingredientsText by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf(0) } // 0: Selection, 1: Text Entry

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Analizo Përbërësit", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedOption == 0) onBack() else selectedOption = 0
                    }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Kthehu")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Background brush
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        )
                    )
            )

            Crossfade(targetState = selectedOption, label = "screen_fade") { state ->
                when (state) {
                    0 -> SelectionView(
                        onPhotoClick = onNavigateToCamera,
                        onTextClick = { selectedOption = 1 }
                    )
                    1 -> TextEntryView(
                        text = ingredientsText,
                        onTextChange = { ingredientsText = it },
                        onAnalyze = { if (ingredientsText.isNotBlank()) onAnalyzeText(ingredientsText) }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectionView(onPhotoClick: () -> Unit, onTextClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Icon(
            Icons.Rounded.AutoFixHigh,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            "Analizoni përbërjen pa barkod",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )

        Text(
            "Ky mjet ju lejon të analizoni produktet që nuk mund t'i skanoni me barkod.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        CompositionOptionCard(
            title = "#1 Bëj një foto",
            description = "Bëj një foto të listës së përbërësve dhe AI do t'i detektojë automatikisht.",
            icon = Icons.Rounded.CameraAlt,
            color = MaterialTheme.colorScheme.primary,
            onClick = onPhotoClick
        )

        CompositionOptionCard(
            title = "#2 Shkruaj tekstin",
            description = "Ideale kur mund të kopjoni përbërësit nga një faqe interneti ose t'i shkruani manualisht.",
            icon = Icons.Rounded.EditNote,
            color = MaterialTheme.colorScheme.tertiary,
            onClick = onTextClick
        )
    }
}

@Composable
fun TextEntryView(
    text: String,
    onTextChange: (String) -> Unit,
    onAnalyze: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Shkruaj Përbërësit",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            placeholder = { Text("Kopjo ose shkruaj përbërësit këtu (psh: Aqua, Glycerin, Phenoxyethanol...)") },
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )

        Button(
            onClick = onAnalyze,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = text.isNotBlank()
        ) {
            Icon(Icons.Rounded.Analytics, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Analizo Përbërjen", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun CompositionOptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = color.copy(alpha = 0.1f)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.padding(14.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
