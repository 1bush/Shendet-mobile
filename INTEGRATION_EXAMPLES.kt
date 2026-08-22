package com.example.shendet.ui.screens

/**
 * INTEGRATION EXAMPLE - How to Use Product Comparison Feature
 *
 * This file shows how to integrate the ProductComparison feature
 * into your existing scanner or product screens.
 */

// Example 1: In a Scanner Result Screen
/*
@Composable
fun ScannerResultScreen(
    barcode: String,
    onNavigateBack: () -> Unit,
    viewModel: ProductComparisonViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTarget by remember { mutableStateOf("food") }

    LaunchedEffect(barcode) {
        // Automatically analyze on barcode scan
        viewModel.analyzeAndCompareProduct(barcode, selectedTarget)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Analiza Produkti", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Filled.Close, "Mbrapa")
            }
        }

        // Product Category Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("food", "cosmetics", "pet", "vitamins").forEach { target ->
                Button(
                    onClick = { selectedTarget = target },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTarget == target)
                            Color(0xFF1565C0) else Color(0xFFCCC)
                    )
                ) {
                    Text(target, fontSize = 10.sp)
                }
            }
        }

        // Loading State
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text(
                        "Po analizohet...",
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }

        // Error State
        if (uiState.error != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "❌ Gabim",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC62828)
                    )
                    Text(
                        uiState.error ?: "",
                        color = Color(0xFFC62828),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Button(
                        onClick = { viewModel.dismissError() },
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 8.dp)
                    ) {
                        Text("Përfundo")
                    }
                }
            }
        }

        // Success Display
        if (uiState.singleComparison != null) {
            ProductComparisonScreen(
                modifier = Modifier.weight(1f),
                comparison = uiState.singleComparison,
                isLoading = false
            )
        }

        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.analyzeAndCompareProduct(barcode, selectedTarget) },
                modifier = Modifier.weight(1f),
                enabled = !uiState.isLoading
            ) {
                Text("Rianalizo")
            }
            Button(
                onClick = onNavigateBack,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFCCC)
                )
            ) {
                Text("Mbrapa", color = Color.Black)
            }
        }
    }
}
*/


// Example 2: In a Product List with Batch Comparison
/*
@Composable
fun ComparisonListScreen(
    viewModel: ProductComparisonViewModel = hiltViewModel(),
    selectedBarcodes: List<String> = emptyList()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedItems by remember { mutableStateOf<List<String>>(emptyList()) }

    if (selectedItems.isNotEmpty() && !uiState.isLoading) {
        // Auto-trigger comparison when items selected
        LaunchedEffect(selectedItems) {
            viewModel.compareMultipleProducts(selectedItems)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Text("Krahasim Produktesh", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        if (uiState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        // Report Summary
        if (uiState.comparisonReport != null) {
            val report = uiState.comparisonReport!!
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "📊 Rezultate",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "Konkordanca: ${report.overallConcordance.toInt()}%",
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        "Produktet: ${report.comparisons.size}",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Comparisons List
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(uiState.comparisonReport?.comparisons ?: emptyList()) { comparison ->
                ProductComparisonCard(comparison = comparison)
            }
        }
    }
}
*/


// Example 3: ViewModel with Hilt Integration
/*
@HiltViewModel
class ProductComparisonViewModel @Inject constructor(
    private val repository: UnifiedProductRepository
) : ViewModel() {
    // ... implementation from ProductComparisonViewModel.kt
}

// In your Module:
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideUnifiedProductRepository(
        @Named("groqApiKey") groqKey: String,
        @Named("muleRouterApiKey") muleKey: String
    ): UnifiedProductRepository {
        return UnifiedProductRepository(groqKey, muleKey)
    }
}
*/


// Example 4: Navigation Integration
/*
sealed class Screen {
    object Home : Screen()
    data class Scanner(val barcode: String) : Screen()
    object Comparison : Screen()
}

@Composable
fun AppNavigation(startDestination: String = "home") {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        // ... other screens

        composable("scanner/{barcode}") { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
            ScannerResultScreen(
                barcode = barcode,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("comparison") {
            ComparisonListScreen(
                selectedBarcodes = listOf() // Pass your barcodes here
            )
        }
    }
}
*/


// Example 5: Integration with Barcode Scanner
/*
@Composable
fun BarcodeScannerScreen() {
    val context = LocalContext.current
    val viewModel: ProductComparisonViewModel = hiltViewModel()
    var scannedBarcode by remember { mutableStateOf<String?>(null) }

    MLKitBarcodeScannerSetup()

    if (scannedBarcode != null) {
        // Automatically compare when barcode is scanned
        LaunchedEffect(scannedBarcode) {
            viewModel.analyzeAndCompareProduct(scannedBarcode!!, "food")
        }
    }

    ScannerResultScreen(
        barcode = scannedBarcode ?: "",
        onNavigateBack = { scannedBarcode = null }
    )
}
*/


// Example 6: Usage in Tests
/*
@RunWith(RobolectricTestRunner::class)
class ProductComparisonIntegrationTest {

    private lateinit var repository: UnifiedProductRepository
    private lateinit var service: ProductComparisonService

    @Before
    fun setup() {
        repository = UnifiedProductRepository(
            groqApiKey = "test-key",
            muleRouterApiKey = "test-key"
        )
    }

    @Test
    fun testSingleProductComparison() = runBlocking {
        val result = repository.analyzeAndCompareProduct(
            barcode = "5201360066862",
            target = "food"
        )

        assertTrue(result.isSuccess)
        val comparison = result.getOrNull()
        assertNotNull(comparison)
        assertTrue(comparison?.comparisonScore!! in 0f..100f)
    }

    @Test
    fun testBatchComparison() = runBlocking {
        val barcodes = listOf("5201360066862", "5000159615119")
        val result = repository.compareMultipleBarcodesWithOFF(barcodes)

        assertTrue(result.isSuccess)
        val report = result.getOrNull()
        assertNotNull(report)
        assertEquals(2, report?.comparisons?.size)
    }
}
*/


// Example 7: Analytics Integration
/*
class ComparisonAnalytics(private val firebaseAnalytics: FirebaseAnalytics) {

    fun logComparison(comparison: ProductComparison) {
        firebaseAnalytics.logEvent("product_comparison") {
            param("product_name", comparison.productName)
            param("concordance_score", comparison.comparisonScore.toInt())
            param("is_concordant", comparison.isConcordant)
            param("discrepancies_count", comparison.discrepancies.size)
        }
    }

    fun logBatchComparison(report: ComparisonReport) {
        firebaseAnalytics.logEvent("batch_comparison") {
            param("products_count", report.comparisons.size)
            param("overall_concordance", report.overallConcordance.toInt())
            param("timestamp", report.timestamp)
        }
    }
}
*/

/**
 * INTEGRATION CHECKLIST
 *
 * [ ] Add models to build.gradle (already included)
 * [ ] Add services to repository (already included)
 * [ ] Create ViewModel with Hilt (ProductComparisonViewModel.kt)
 * [ ] Add navigation routes
 * [ ] Create UI screens using ProductComparisonScreen composable
 * [ ] Connect barcode scanner to analyzeAndCompareProduct()
 * [ ] Add error handling and snackbars
 * [ ] Test with sample barcodes
 * [ ] Add analytics tracking (optional)
 * [ ] Deploy and monitor API usage
 *
 * SAMPLE BARCODES FOR TESTING:
 * - 5201360066862 (Zafora Product - Greek)
 * - 5000159615119 (Sample UK product)
 * - 8001234567890 (Test format)
 * - Any EAN-13 or UPC-A barcode
 *
 * API RESPONSE TIME:
 * - Average: 3-5 seconds per product
 * - With MuleRouter fallback: 5-7 seconds
 * - Batch (5 products): 15-25 seconds
 *
 * TROUBLESHOOTING:
 * - No data: Product not in OpenFoodFacts
 * - Low score: Check barcode format (needs to be valid EAN/UPC)
 * - Timeout: Network issue or API overload
 * - API error: Check credentials in local.properties
 */

