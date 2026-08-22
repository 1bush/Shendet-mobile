package com.example.shendet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shendet.data.local.entity.AllergyEntity
import com.example.shendet.data.repository.AllergyRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllergyProfileScreen(
    repository: AllergyRepository,
    onBack: () -> Unit
) {
    val allergies by repository.allAllergies.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        repository.initializeDefaultAllergies()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profili i Alergjive") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Kthehu")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Zgjidhni përbërësit që dëshironi të shmangni. Aplikacioni do t'ju paralajmërojë automatikisht.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(allergies) { allergy ->
                    AllergyItem(
                        allergy = allergy,
                        onToggle = {
                            scope.launch {
                                repository.toggleAllergy(allergy)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AllergyItem(
    allergy: AllergyEntity,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = allergy.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        Switch(
            checked = allergy.isEnabled,
            onCheckedChange = { onToggle() }
        )
    }
}
