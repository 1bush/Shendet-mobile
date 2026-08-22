package com.example.shendet.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shendet.data.model.ComparisonReport
import com.example.shendet.data.model.LookupSource
import com.example.shendet.data.model.ProductComparison
import com.example.shendet.data.repository.UnifiedProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ComparisonUIState(
    val isLoading: Boolean = false,
    val singleComparison: ProductComparison? = null,
    val comparisonReport: ComparisonReport? = null,
    val error: String? = null,
    val successMessage: String? = null
)

class ProductComparisonViewModel(
    private val repository: UnifiedProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComparisonUIState())
    val uiState: StateFlow<ComparisonUIState> = _uiState.asStateFlow()

    /**
     * Analyze a single product and compare with OpenFoodFacts
     * NEW: Accept lookupSource to distinguish barcode vs photo
     */
    fun analyzeAndCompareProduct(
        barcode: String,
        target: String = "food",
        lookupSource: LookupSource = LookupSource.BARCODE
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val result = repository.analyzeAndCompareProduct(barcode, target, lookupSource)

                if (result.isSuccess) {
                    val comparison = result.getOrNull()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            singleComparison = comparison,
                            successMessage = if (comparison?.isProductFound == true)
                                "Produkti u analizua me sukses! Konkordanca: ${comparison.comparisonScore.toInt()}%"
                            else
                                "Produkti nuk u gjet në bazën e të dhënave"
                        )
                    }
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Gabim i panjohur"
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = errorMsg
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ComparisonViewModel", "Error analyzing product", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Gabim i panjohur gjatë analizës"
                    )
                }
            }
        }
    }

    /**
     * NEW: Analyze from barcode scan
     */
    fun analyzeFromBarcode(barcode: String, target: String = "food") {
        analyzeAndCompareProduct(barcode, target, LookupSource.BARCODE)
    }

    /**
     * NEW: Analyze from photo
     */
    fun analyzeFromPhoto(identifier: String, target: String = "food") {
        analyzeAndCompareProduct(identifier, target, LookupSource.PHOTO)
    }

    /**
     * NEW: Analyze from manual input
     */
    fun analyzeFromManualInput(productName: String, target: String = "food") {
        analyzeAndCompareProduct(productName, target, LookupSource.MANUAL_INPUT)
    }

    /**
     * Compare multiple products
     */
    fun compareMultipleProducts(
        barcodes: List<String>,
        target: String = "food"
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                if (barcodes.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Të paktën një barkod është i nevojshëm"
                        )
                    }
                    return@launch
                }

                val result = repository.compareMultipleBarcodesWithOFF(barcodes, target)

                if (result.isSuccess) {
                    val report = result.getOrNull()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            comparisonReport = report,
                            successMessage = "Raport përfunduar! Konkordanca e përgjithshme: ${report?.overallConcordance?.toInt()}%"
                        )
                    }
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Gabim i panjohur"
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = errorMsg
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ComparisonViewModel", "Error comparing products", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Gabim i panjohur gjatë krahasimit"
                    )
                }
            }
        }
    }

    /**
     * Clear the current comparison
     */
    fun clearComparison() {
        _uiState.update {
            it.copy(
                singleComparison = null,
                comparisonReport = null,
                error = null,
                successMessage = null
            )
        }
    }

    /**
     * Dismiss error message
     */
    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Dismiss success message
     */
    fun dismissSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }

    /**
     * Get concordance status text
     */
    fun getConcordanceStatus(score: Float): String {
        return when {
            score >= 80f -> "✓ Shumë i Concordant"
            score >= 60f -> "⚠️ Përfundsisht Concordant"
            else -> "✗ Jo Concordant"
        }
    }

    /**
     * Get concordance color based on score
     */
    fun getConcordanceColor(score: Float): String {
        return when {
            score >= 80f -> "GREEN"
            score >= 60f -> "AMBER"
            else -> "RED"
        }
    }
}

/**
 * Example usage in a Composable:
 *
 * @Composable
 * fun ComparisonScreen(
 *     viewModel: ProductComparisonViewModel = hiltViewModel(),
 *     onNavigateBack: () -> Unit
 * ) {
 *     val uiState by viewModel.uiState.collectAsState()
 *     var barcodeInput by remember { mutableStateOf("") }
 *
 *     LaunchedEffect(uiState.successMessage) {
 *         if (uiState.successMessage != null) {
 *             // Show snackbar with success message
 *             delay(3000)
 *             viewModel.dismissSuccessMessage()
 *         }
 *     }
 *
 *     Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
 *         // Input field
 *         OutlinedTextField(
 *             value = barcodeInput,
 *             onValueChange = { barcodeInput = it },
 *             label = { Text("Barkodi ose emri i produktit") },
 *             modifier = Modifier.fillMaxWidth()
 *         )
 *
 *         // Compare button
 *         Button(
 *             onClick = {
 *                 if (barcodeInput.isNotEmpty()) {
 *                     viewModel.analyzeAndCompareProduct(barcodeInput)
 *                 }
 *             },
 *             modifier = Modifier
 *                 .fillMaxWidth()
 *                 .padding(top = 16.dp),
 *             enabled = !uiState.isLoading
 *         ) {
 *             Text(if (uiState.isLoading) "Po analizohet..." else "Analizo & Krahaso")
 *         }
 *
 *         // Error display
 *         if (uiState.error != null) {
 *             Card(
 *                 modifier = Modifier
 *                     .fillMaxWidth()
 *                     .padding(top = 16.dp),
 *                 colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))
 *             ) {
 *                 Column(modifier = Modifier.padding(12.dp)) {
 *                     Text(
 *                         text = "Gabim",
 *                         fontWeight = FontWeight.Bold,
 *                         color = Color(0xFFC62828)
 *                     )
 *                     Text(
 *                         text = uiState.error ?: "",
 *                         color = Color(0xFFC62828),
 *                         fontSize = 12.sp
 *                     )
 *                     Button(
 *                         onClick = { viewModel.dismissError() },
 *                         modifier = Modifier.align(Alignment.End)
 *                     ) {
 *                         Text("Përfundo")
 *                     }
 *                 }
 *             }
 *         }
 *
 *         // Comparison display
 *         if (uiState.isLoading) {
 *             Box(
 *                 modifier = Modifier
 *                     .fillMaxSize()
 *                     .weight(1f),
 *                 contentAlignment = Alignment.Center
 *             ) {
 *                 CircularProgressIndicator()
 *             }
 *         } else {
 *             ProductComparisonScreen(
 *                 comparison = uiState.singleComparison,
 *                 report = uiState.comparisonReport,
 *                 isLoading = false,
 *                 modifier = Modifier
 *                     .weight(1f)
 *                     .fillMaxWidth()
 *             )
 *         }
 *     }
 * }
 */

