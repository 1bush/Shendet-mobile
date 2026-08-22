package com.example.shendet

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.launch
import com.example.shendet.data.local.ShendetDatabase
import com.example.shendet.data.repository.AllergyRepository
import com.example.shendet.data.repository.UnifiedProductRepository
import com.example.shendet.data.repository.HistoryRepository
import com.example.shendet.data.repository.NocoDbRepository
import com.example.shendet.ui.navigation.Route
import com.example.shendet.ui.screens.*
import com.example.shendet.ui.theme.SHENDETTheme

class MainActivity : FragmentActivity() {
    private val database by lazy { ShendetDatabase.getDatabase(this) }
    private val allergyRepository by lazy { AllergyRepository(database.allergyDao()) }
    private val historyRepository by lazy { HistoryRepository(database.scanHistoryDao()) }
    
    companion object {
        var capturedBitmap: Bitmap? = null
        var ingredientsBitmap: Bitmap? = null
        
        // Add Product State - mutableStateOf so Compose observes changes automatically
        var addProductBarcode by mutableStateOf<String?>(null)
        var addProductFrontBitmap by mutableStateOf<Bitmap?>(null)
        var addProductIngredientsBitmap by mutableStateOf<Bitmap?>(null)

        // Comparison State - barcodes scanned for product comparison
        var compareBarcodeA by mutableStateOf<String?>(null)
        var compareBarcodeB by mutableStateOf<String?>(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            var isAuthenticated by remember { mutableStateOf(value = false) }
            
            SHENDETTheme {
                if (isAuthenticated) {
                    SHENDETApp(allergyRepository, historyRepository)
                } else {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.fillMaxSize())
                    }
                    LaunchedEffect(Unit) {
                        authenticate { success ->
                            isAuthenticated = success
                            if (!success) finish()
                        }
                    }
                }
            }
        }
    }

    private fun authenticate(onResult: (Boolean) -> Unit) {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                        Toast.makeText(applicationContext, "Autentikimi u anulua", Toast.LENGTH_SHORT).show()
                    }
                    onResult(false)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onResult(true)
                }
            },
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("SHENDETI IM")
            .setSubtitle("Autentikohuni për të hapur aplikacionin")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}

@Composable
fun SHENDETApp(
    allergyRepository: AllergyRepository,
    historyRepository: HistoryRepository,
) {
    val context = LocalContext.current
    val backStack = rememberNavBackStack(Route.Dashboard as NavKey)
    val unifiedRepository = remember {
        UnifiedProductRepository(
            ollamaBaseUrl = BuildConfig.OLLAMA_BASE_URL,
            ollamaModel = BuildConfig.OLLAMA_MODEL,
            inciBeautyBaseUrl = BuildConfig.INCI_BEAUTY_BASE_URL,
            usdaApiKey = BuildConfig.USDA_API_KEY,
            nixAppId = BuildConfig.NUTRITIONIX_APP_ID,
            nixAppKey = BuildConfig.NUTRITIONIX_APP_KEY,
            nocoDbRepository = NocoDbRepository(
                baseUrl = BuildConfig.NOCODB_BASE_URL,
                token = BuildConfig.NOCODB_TOKEN,
                orgId = BuildConfig.NOCODB_ORG_ID,
                baseId = BuildConfig.NOCODB_BASE_ID,
                productsTableId = BuildConfig.NOCODB_PRODUCTS_TABLE_ID,
                allergensTableId = BuildConfig.NOCODB_ALLERGENS_TABLE_ID,
                inciTableId = BuildConfig.NOCODB_INCI_TABLE_ID
            )
        )
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
    ) { key ->
        when (key) {
            is Route.Dashboard -> NavEntry(key) {
                DashboardScreen(
                    onNavigateToScanner = { target -> backStack.add(Route.Scanner(target)) },
                    onNavigateToPetNutrition = { backStack.add(Route.PetNutrition) },
                    onNavigateToAllergyProfile = { backStack.add(Route.AllergyProfile) },
                    onNavigateToHistory = { backStack.add(Route.History) },
                    onNavigateToComparison = { backStack.add(Route.Comparison) },
                    onNavigateToVitamins = { backStack.add(Route.VitaminsMinerals) },
                    onNavigateToComposition = { backStack.add(Route.Composition) },
                ) {
                    MainActivity.addProductBarcode = null
                    MainActivity.addProductFrontBitmap = null
                    MainActivity.addProductIngredientsBitmap = null
                    backStack.add(Route.AddProduct(target = "food"))
                }
            }
            is Route.Scanner -> NavEntry(key) {
                val target = key.target
                ScannerScreen(
                    target = target,
                    onBack = { backStack.removeLastOrNull() },
                    onProductDetected = { productId ->
                        if (target == "add_barcode") {
                            MainActivity.addProductBarcode = productId
                            backStack.removeLastOrNull()
                        } else if (target == "compareA") {
                            MainActivity.compareBarcodeA = productId
                            backStack.removeLastOrNull()
                        } else if (target == "compareB") {
                            MainActivity.compareBarcodeB = productId
                            backStack.removeLastOrNull()
                        } else {
                            backStack.removeLastOrNull()
                            backStack.add(Route.Analysis(productId, target = target))
                        }
                    },
                ) { bitmap ->
                    when (target) {
                        "ingredients" -> {
                            MainActivity.ingredientsBitmap = bitmap
                            backStack.removeLastOrNull()
                            backStack.add(Route.Analysis("composition_scan", target = "cosmetics", useImage = true))
                        }
                        "add_front" -> {
                            MainActivity.addProductFrontBitmap = bitmap
                            backStack.removeLastOrNull()
                        }
                        "add_ingredients" -> {
                            MainActivity.addProductIngredientsBitmap = bitmap
                            backStack.removeLastOrNull()
                        }
                        "detergents" -> {
                            MainActivity.capturedBitmap = bitmap
                            backStack.removeLastOrNull()
                            backStack.add(Route.Analysis("image_scan", target = "detergents", useImage = true))
                        }
                        else -> {
                            MainActivity.capturedBitmap = bitmap
                            backStack.removeLastOrNull()
                            backStack.add(Route.Analysis("image_scan", target = target, useImage = true))
                        }
                    }
                }
            }
            is Route.Analysis -> NavEntry(key) {
                val analysisTarget = key.target
                AnalysisScreen(
                    productId = key.productId,
                    target = analysisTarget,
                    useImage = key.useImage,
                    capturedBitmap = MainActivity.capturedBitmap, // Always pass if available
                    ingredientsBitmap = MainActivity.ingredientsBitmap,
                    unifiedRepository = unifiedRepository,
                    allergyRepository = allergyRepository,
                    historyRepository = historyRepository,
                    onAddIngredients = {
                        backStack.add(Route.Scanner("ingredients"))
                    },
                    onNavigateToAddProduct = {
                        backStack.add(Route.AddProduct(target = analysisTarget))
                    },
                ) { backStack.removeLastOrNull() }
            }
            is Route.PetNutrition -> NavEntry(key) {
                PetNutritionScreen(
                    repository = unifiedRepository,
                    capturedBitmap = MainActivity.capturedBitmap,
                    onNavigateToScanner = { backStack.add(Route.Scanner("pet")) },
                    onNavigateToAddProduct = { backStack.add(Route.AddProduct(target = "pet")) },
                ) { backStack.removeLastOrNull() }
            }
            is Route.VitaminsMinerals -> NavEntry(key) {
                VitaminsMineralsScreen(
                    repository = unifiedRepository,
                    capturedBitmap = MainActivity.capturedBitmap,
                    onNavigateToScanner = { backStack.add(Route.Scanner("vitamins")) },
                    onNavigateToAddProduct = { backStack.add(Route.AddProduct(target = "vitamins")) },
                ) { backStack.removeLastOrNull() }
            }
            is Route.Comparison -> NavEntry(key) {
                ComparisonScreen(
                    repository = historyRepository,
                    unifiedRepository = unifiedRepository,
                    barcodeA = MainActivity.compareBarcodeA,
                    barcodeB = MainActivity.compareBarcodeB,
                    onNavigateToScanner = { target -> backStack.add(Route.Scanner(target)) },
                    onBarcodeConsumed = { slot ->
                        when (slot) {
                            "A" -> MainActivity.compareBarcodeA = null
                            "B" -> MainActivity.compareBarcodeB = null
                        }
                    },
                ) { backStack.removeLastOrNull() }
            }
            is Route.AllergyProfile -> NavEntry(key) {
                AllergyProfileScreen(repository = allergyRepository) { backStack.removeLastOrNull() }
            }
            is Route.History -> NavEntry(key) {
                HistoryScreen(
                    repository = historyRepository,
                    onNavigateToDetails = { backStack.add(Route.Analysis(it)) },
                ) { backStack.removeLastOrNull() }
            }
            is Route.Composition -> NavEntry(key) {
                CompositionScreen(
                    onNavigateToCamera = { backStack.add(Route.Scanner("ingredients")) },
                    onAnalyzeText = { text ->
                        backStack.add(Route.Analysis(text, target = "cosmetics"))
                    },
                ) { backStack.removeLastOrNull() }
            }
            is Route.AddProduct -> NavEntry(key) {
                val scope = rememberCoroutineScope()

                AddProductScreen(
                    target = key.target,
                    onNavigateToScanner = { backStack.add(Route.Scanner(it)) },
                    barcode = MainActivity.addProductBarcode,
                    frontBitmap = MainActivity.addProductFrontBitmap,
                    ingredientsBitmap = MainActivity.addProductIngredientsBitmap,
                    onBack = { backStack.removeLastOrNull() },
                ) { name, bCode, front, ingredients, target ->
                    scope.launch {
                        val result = unifiedRepository.addProductToOpenData(name, bCode, front, ingredients, target)
                        if (result.isSuccess) {
                            Toast.makeText(context, "Produkti '$name' u dërgua me sukses!", Toast.LENGTH_LONG).show()
                            MainActivity.addProductBarcode = null
                            MainActivity.addProductFrontBitmap = null
                            MainActivity.addProductIngredientsBitmap = null
                            backStack.removeLastOrNull()
                        } else {
                            Toast.makeText(context, "Gabim gjatë dërgimit: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            else -> error("Unknown route")
        }
    }
}
