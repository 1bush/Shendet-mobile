package com.example.shendet.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material.icons.rounded.Flip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import java.io.File
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    target: String = "general",
    onBack: () -> Unit,
    onProductDetected: (String) -> Unit,
    onImageCaptured: (Bitmap) -> Unit,
) {
    val context = LocalContext.current
    
    // Clear previous capture on start
    LaunchedEffect(Unit) {
        com.example.shendet.MainActivity.capturedBitmap = null
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var isCurvedMode by remember { mutableStateOf(value = false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    val title = when (target) {
        "food" -> "Skano Ushqimin"
        "cosmetics" -> "Skano Kozmetikën"
        "pet" -> "Skano Ushqimin për Kafshë"
        "detergents" -> "Skano Detergjentin / Kujdesin Personal"
        "compareA" -> "Skano Produktin A"
        "compareB" -> "Skano Produktin B"
        "vitamins" -> "Skano për Vitamina"
        "add_barcode" -> "Skano Barkodin"
        "add_front" -> "Foto e Produktit"
        "add_ingredients" -> "Foto e Përbërësve"
        "ingredients" -> "Skano Përbërësit"
        else -> "Skaneri"
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val mainExecutor = ContextCompat.getMainExecutor(context)
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    val options = remember {
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
    }
    val barcodeScanner = remember { BarcodeScanning.getClient(options) }

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setImageAnalysisAnalyzer(
                mainExecutor,
                MlKitAnalyzer(
                    listOf(barcodeScanner),
                    ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
                    mainExecutor
                ) { result: MlKitAnalyzer.Result? ->
                    val barcodes = result?.getValue(barcodeScanner)
                    if (!barcodes.isNullOrEmpty()) {
                        val barcode = barcodes[0].rawValue ?: return@MlKitAnalyzer
                        
                        // For barcode-only targets, just return the barcode immediately
                        val barcodeOnlyTargets = listOf("add_barcode", "food", "cosmetics", "pet", "detergents", "vitamins", "compareA", "compareB", "general")
                        if (target in barcodeOnlyTargets) {
                            clearImageAnalysisAnalyzer()
                            onProductDetected(barcode)
                            return@MlKitAnalyzer
                        }
                        
                        // For image-only targets (add product photos), ignore barcode detection
                        // User should use the manual photo button instead
                        val imageOnlyTargets = listOf("add_front", "add_ingredients")
                        if (target in imageOnlyTargets) {
                            return@MlKitAnalyzer
                        }
                        
                        // For combined targets (like "ingredients"), capture frame then return both
                        val file = File(context.cacheDir, "auto_scan_${System.currentTimeMillis()}.jpg")
                        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                        takePicture(
                            outputOptions,
                            cameraExecutor,
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                    mainExecutor.execute { 
                                        onImageCaptured(bitmap)
                                        onProductDetected(barcode)
                                    }
                                }
                                override fun onError(exception: ImageCaptureException) {
                                    mainExecutor.execute { onProductDetected(barcode) }
                                }
                            }
                        )
                        clearImageAnalysisAnalyzer()
                    }
                }
            )
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Kthehu", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.4f)
                )
            )
        }
    ) { innerPadding ->
        if (hasCameraPermission) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            this.controller = cameraController
                            cameraController.bindToLifecycle(lifecycleOwner)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
                
                ScannerOverlay(isCurved = isCurvedMode)
                
                // Top controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 100.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { isCurvedMode = !isCurvedMode },
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .size(48.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Flip,
                            contentDescription = "Curved Mode",
                            tint = if (isCurvedMode) MaterialTheme.colorScheme.primary else Color.White
                        )
                    }
                }
                
                // Photo button for AI Vision
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LargeFloatingActionButton(
                        onClick = {
                            val file = File(context.cacheDir, "scan_${System.currentTimeMillis()}.jpg")
                            val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                            cameraController.takePicture(
                                outputOptions,
                                cameraExecutor,
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                        mainExecutor.execute { onImageCaptured(bitmap) }
                                    }
                                    override fun onError(exception: ImageCaptureException) {
                                        // Handle error
                                    }
                                }
                            )
                        },
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Rounded.Camera, contentDescription = "Bëj Foto")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            when (target) {
                                "add_barcode" -> "Vendosni barkodin brenda kornizës"
                                "add_front" -> "Fotoni pjesën e parë të produktit"
                                "add_ingredients" -> "Fotoni listën e përbërësve"
                                "ingredients" -> "Skanoni listën e përbërësve"
                                else -> "Ju lutem skanoni vetëm barkodin"
                            },
                            modifier = Modifier.padding(16.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            // ... (permission view remains the same)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text("Kërkohet leja e kamerës", fontWeight = FontWeight.Bold)
                    Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                        Text("Jep Lejen")
                    }
                }
            }
        }
    }
}

@Composable
fun ScannerOverlay(isCurved: Boolean = false) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        val scannerWidth = width * 0.8f
        val scannerHeight = if (isCurved) width * 0.4f else width * 0.7f
        
        val left = (width - scannerWidth) / 2
        val top = (height - scannerHeight) / 2
        val rect = Rect(left, top, left + scannerWidth, top + scannerHeight)

        val path = Path().apply {
            addRect(Rect(0f, 0f, width, height))
            addRoundRect(RoundRect(rect, CornerRadius(24.dp.toPx())))
            fillType = PathFillType.EvenOdd
        }
        drawPath(path, Color.Black.copy(alpha = 0.5f))

        val cornerLength = 40.dp.toPx()
        val strokeWidth = 4.dp.toPx()
        val color = if (isCurved) Color(0xFF00BCD4) else Color.White

        // Corners
        drawLine(color, Offset(left, top + cornerLength), Offset(left, top), strokeWidth)
        drawLine(color, Offset(left, top), Offset(left + cornerLength, top), strokeWidth)
        
        drawLine(color, Offset(left + scannerWidth - cornerLength, top), Offset(left + (scannerWidth), top), strokeWidth)
        drawLine(color, Offset(left + scannerWidth, top), Offset(left + scannerWidth, top + cornerLength), strokeWidth)
        
        drawLine(color, Offset(left, top + scannerHeight - cornerLength), Offset(left, top + scannerHeight), strokeWidth)
        drawLine(color, Offset(left, top + scannerHeight), Offset(left + cornerLength, top + scannerHeight), strokeWidth)
        
        drawLine(color, Offset(left + scannerWidth - cornerLength, top + scannerHeight), Offset(left + scannerWidth, top + scannerHeight), strokeWidth)
        drawLine(color, Offset(left + scannerWidth, top + scannerHeight), Offset(left + scannerWidth, top + scannerHeight - cornerLength), strokeWidth)

        if (isCurved) {
            // Draw a guide line for curved surface
            drawLine(
                color = color.copy(alpha = 0.5f),
                start = Offset(left, top + scannerHeight / 2),
                end = Offset(left + scannerWidth, top + scannerHeight / 2),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        }
    }
}
