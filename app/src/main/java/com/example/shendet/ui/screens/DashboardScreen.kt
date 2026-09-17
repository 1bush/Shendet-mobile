package com.example.shendet.ui.screens

import com.example.shendet.ui.components.GifImage
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shendet.R
import com.example.shendet.ui.theme.SHENDETIIMTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToScanner: (String) -> Unit,
    onNavigateToPetNutrition: () -> Unit,
    onNavigateToAllergyProfile: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToComparison: () -> Unit,
    onNavigateToVitamins: () -> Unit,
    onNavigateToComposition: () -> Unit,
    onNavigateToAddProduct: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    var showCosmeticDialog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    if (showCosmeticDialog) {
        AlertDialog(
            onDismissRequest = { showCosmeticDialog = false },
            title = { Text("Zgjidhni Mënyrën", fontWeight = FontWeight.Black) },
            text = { Text("Si dëshironi ta analizoni produktin kozmetik?") },
            confirmButton = {
                Button(
                    onClick = { 
                        showCosmeticDialog = false
                        onNavigateToScanner("cosmetics")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Rounded.QrCode, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Skano Barkodin")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showCosmeticDialog = false
                        onNavigateToComposition()
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Icon(Icons.Rounded.AutoFixHigh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analizo Përbërjen (pa barkod)")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Decorative Elements
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = (-150).dp, y = (-150).dp)
                .background(
                    Brush.radialGradient(
                        listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), Color.Transparent)
                    ), 
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 100.dp)
                .background(
                    Brush.radialGradient(
                        listOf(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f), Color.Transparent)
                    ), 
                    CircleShape
                )
        )

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "SHENDETI IM",
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 3.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "SHENDETI JOTE FILLON KETU!",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                val infiniteTransition = rememberInfiniteTransition(label = "fab")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.15f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "scale"
                )

                LargeFloatingActionButton(
                    onClick = { onNavigateToScanner("general") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
                ) {
                    Icon(
                        Icons.Rounded.QrCodeScanner,
                        contentDescription = "Skano",
                        modifier = Modifier.size(42.dp)
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            containerColor = Color.Transparent
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 120.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn() + expandVertically()
                    ) {
                        WelcomeCard()
                    }
                }
                item {
                    Text(
                        "KATEGORITË",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                    )
                }
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(600)) + slideInHorizontally()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ActionCard(
                                title = "USHQIM",
                                gifUrl = "https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Food/Green%20Apple.png",
                                color = Color(0xFF00E676),
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigateToScanner("food") }
                            )
                            ActionCard(
                                title = "KOZMETIKE",
                                gifUrl = "https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Lipstick.png",
                                color = Color(0xFFFF4081),
                                modifier = Modifier.weight(1f),
                                onClick = { showCosmeticDialog = true }
                            )
                        }
                    }
                }
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(800)) + slideInHorizontally()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ActionCard(
                                title = "VITAMINA",
                                gifUrl = "https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Pill.png",
                                color = Color(0xFFB388FF),
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToVitamins
                            )
                            ActionCard(
                                title = "KAFSHË",
                                gifUrl = "https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Animals/Dog.png",
                                color = Color(0xFFFFAB00),
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToPetNutrition
                            )
                        }
                    }
                }
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(1000)) + slideInHorizontally()
                    ) {
                        ActionCard(
                            title = "DETERGJENT & KUJDESI PERSONAL",
                            gifUrl = "https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Soap.png",
                            color = Color(0xFF00E5FF),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onNavigateToScanner("detergents") }
                        )
                    }
                }
                item {
                    ActionCard(
                        title = "KONTRIBUO: SHTO PRODUKT",
                        gifUrl = "https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Camera%20with%20Flash.png",
                        color = Color(0xFFFF5252),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onNavigateToAddProduct
                    )
                }
                item {
                    Text(
                        "MJETET",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ActionCard(
                            title = "HISTORIKU",
                            gifUrl = "https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Books.png",
                            color = Color(0xFF448AFF),
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToHistory
                        )
                        ActionCard(
                            title = "KRAHASO",
                            gifUrl = "https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Symbols/Currency%20Exchange.png",
                            color = Color(0xFF64FFDA),
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToComparison
                        )
                    }
                }
                item {
                    ActionCard(
                        title = "PROFILI I ALERGJIVE",
                        gifUrl = "https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Symbols/Warning.png",
                        color = Color(0xFFFF6E40),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onNavigateToAllergyProfile
                    )
                }
                
                item {
                    Text(
                        "AKTIVITETI I FUNDIT",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                    )
                }
                items(3) { index ->
                    RecentItemPlaceholder(index)
                }
            }
        }
    }
}

@Composable
fun WelcomeCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.health_lifestyle),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().alpha(0.15f),
                contentScale = ContentScale.Crop
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "JETONI SHËNDETSHEM",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Skanoni produktet për të parë ndikimin e tyre në shëndetin tuaj dhe të kafshëve tuaja!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    gifUrl: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    Card(
        modifier = modifier
            .height(130.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Gradient accent
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(color.copy(alpha = 0.15f), color.copy(alpha = 0.02f))
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = CircleShape,
                    color = color.copy(alpha = 0.1f)
                ) {
                    GifImage(
                        data = gifUrl,
                        modifier = Modifier.padding(8.dp).size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    ),
                    color = color,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun RecentItemPlaceholder(index: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.ShoppingBasket,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "Produkti Mostër ${index + 1}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Skanuar sot",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "80%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardPreview() {
    SHENDETIIMTheme {
        DashboardScreen({_ ->}, {}, {}, {}, {}, {}, {}, {})
    }
}
