# Barcode vs Photo Product Lookup - Shqip Guide

## 🎯 Karakteristikat e Reja

Aplikacioni tani bën **dallim midis barcodit dhe fotove** normale të produkteve, dhe shfaq mesazhe të qarta në shqip kur produkti nuk gjendet në bazën e të dhënave.

---

## 📊 Llojet e Kërkimit

### 1. **📊 Barcode Scan** (Skenim Barkodi)
- Scanner lexon barcodin e produktit
- Barkodi dërgohet në OpenFoodFacts
- Nëse produkti ekziston në bazën e të dhënave, shfaqen të dhënat

**Përdorues:** Kur ke barcode në shtratin
**Shembull:** Skenim produkti me kamera

### 2. **📸 Photo Analysis** (Analiza e Fotove)
- Zgjidh fotografi të produktit ose përpakimit
- Groq AI analizon foton
- Kërkon produktin në OpenFoodFacts sipas emrit

**Përdorues:** Kur nuk ke barcode ose nuk funksionon scanneri
**Shembull:** Fotografi e një produkti pa barcode

### 3. **✏️ Manual Input** (Hyrje Manuale)
- Përdoruesi futet emrin e produktit me dorë
- Sistemi kërkon në bazën e të dhënave

**Përdorues:** Kur dësh të kerkosh produktin me emrin
**Shembull:** "Qumëzhe Bulit", "Bimë Bletë"

---

## ❌ Produkti Nuk U Gjet - Si Të Veprosh

### Mesazhi në Shqip
```
🔍 Produkti nuk u gjet në data baze

Të lutem, shto produktin në OpenFoodFacts:
📱 https://world.openfoodfacts.org/cgi/product.pl

Ose kontakto administratorin.
```

### Çfarë Nënkupton?

**Barcode:** Ky barcode nuk ekziston në OpenFoodFacts
**Foto:** Kjo fotografi nuk u përshëmbye me asnjë produkt në bazë
**Manual:** Ky emër nuk gjendet në bazën e të dhënave

### Si Të Rregullosh

#### Opsioni 1: Shto Produktin në OpenFoodFacts
1. Kliko butonin "🌐 Shto produktin në OpenFoodFacts"
2. Plotëso të dhënat e produktit (emri, ingredienti, nutriscore)
3. Prit deri të miratohet (zakonisht 24-48 orë)

#### Opsioni 2: Kontakto Administratorin
- Dërgo email me detajet e produktit
- Përfshij barcode (nëse ka)
- Përfshij foton e produktit

---

## 🎨 UI Ndryshimet

### Kartelë e Gjetjes së Produktit (Normal)
```
📊 Barcode Scan ← Badge e burimit
━━━━━━━━━━━━━━━━━━━━
Emri i Produktit
Groq Score: 8/10 (A)        ✓ 85%
━━━━━━━━━━━━━━━━━━━━
✓ Emri  ✓ Përbërës  ✓ Pikëz.
[Detajet e produktit...]
```

### Kartelë e Mosgjentes (Produkti Nuk Gjendet)
```
📸 Photo Analysis ← Badge e burimit (e ndryshme)
━━━━━━━━━━━━━━━━━━━━
🔍 (Ikonë e madhe)

Produkti nuk u gjet në data baze
━━━━━━━━━━━━━━━━━━━━

✓ Cila u detektua:
  Qumëzhe Bulit
  Bulit është i mirë për të...

⚠️ Produkti nuk ekziston në bazën e të dhënave
💡 Kjo fotografi nuk u përshëmbye...

[BUTON] 🌐 Shto produktin në OpenFoodFacts
```

---

## 💻 Kodi - Si Të Përdorosh

### Barcode Lookup
```kotlin
viewModel.analyzeFromBarcode("5201360066862", "food")
```

### Photo Lookup
```kotlin
viewModel.analyzeFromPhoto("produto-image.jpg", "food")
```

### Manual Lookup
```kotlin
viewModel.analyzeFromManualInput("Qumëzhe Bulit", "food")
```

### Me Parametra të Hollësishëm
```kotlin
viewModel.analyzeAndCompareProduct(
    barcode = "5201360066862",
    target = "food",
    lookupSource = LookupSource.BARCODE  // Ose PHOTO, MANUAL_INPUT
)
```

---

## 🔍 Statusi i Kërkimit

Në çdo kartela produkti, do të shohësh badge-in që tregon burimin:

| Badge | Kupimi | Ngjyra |
|-------|--------|--------|
| 📊 Barcode Scan | Lexim barkodi | Blu |
| 📸 Photo Analysis | Analiza foto | Portokalli |
| ✏️ Manual Input | Hyrje manuale | Jeshilë |

---

## 📋 Mesazhet në Shqip

### Kur Produkti Gjendet
```
✓ Produkti u analizua me sukses!
  Konkordanca: 85%
```

### Kur Produkti Nuk Gjendet
```
🔍 Produkti nuk u gjet në data baze
   (të lutem shto produktin)
```

### Rekomandime Automatike
```
⚠️ Produkti nuk ekziston në bazën e të dhënave
💡 Ky barcode nuk gjendet - siguro që barcode është i saktë
```

---

## 🚀 Përdorimi Praktik

### Skenari 1: Barcode Scanner (Më i Thjeshtë)
```
1. Skeno barcodin
2. Nëse produkti gjendet → Shfaq rezultate
3. Nëse nuk gjendet → Kliko "Shto në OpenFoodFacts"
```

### Skenari 2: Fotografi Produkti
```
1. Zgjidh fotografi
2. Groq analizon foton
3. Kërkon në OpenFoodFacts
4. Nëse nuk gjendet → Kliko "Shto në OpenFoodFacts"
```

### Skenari 3: Hyrje Manuale
```
1. Fut emrin e produktit
2. Kërkon në bazën e të dhënave
3. Nëse nuk gjendet → Propozim për shtim
```

---

## 🗂️ Ndryshimet në Kodin

### Models
- `LookupSource` enum (BARCODE, PHOTO, MANUAL_INPUT)
- `isProductFound` boolean
- `lookupSource` në ProductComparison
- `errorMessage` string

### Repository
- `analyzeFromBarcode()` ← Barcode specifically
- `analyzeFromPhoto()` ← Photo specifically
- `analyzeFromManualInput()` ← Manual input
- `analyzeAndCompareProduct()` ← Generic (me lookupSource param)

### Service
- Detektion automatike e produktit jo i gjetur
- Mesazhe në shqip për "produkti nuk u gjet"
- Rekomandime sipas llojit të kërkimit

### UI
- `ProductNotFoundCard()` ← Card për produktin jo të gjetur
- `LookupSourceBadge()` ← Badge për burimin
- Ngjyra të ndryshme për barcode vs photo

### ViewModel
- `analyzeFromBarcode()` convenience method
- `analyzeFromPhoto()` convenience method
- `analyzeFromManualInput()` convenience method

---

## ✅ Kontrollista

### Para Implementimit
- [ ] Lexo këtë guide
- [ ] Kupto LookupSource enum
- [ ] Shiko UI components

### Gjatë Integrimit
- [ ] Shto analyzeFromBarcode() calls në scanner
- [ ] Shto analyzeFromPhoto() calls në photo picker
- [ ] Shto analyzeFromManualInput() calls në input screen
- [ ] Test të tre metodat

### Pas Integrimit
- [ ] Test me barcode ekzistent
- [ ] Test me barcode jo-ekzistent
- [ ] Test me fotografi
- [ ] Test mesazhe në shqip
- [ ] Verifikoje linku "Shto në OpenFoodFacts"

---

## 📞 Shembull Komplete

### Scanner Screen
```kotlin
@Composable
fun BarcodeScannerScreen(
    viewModel: ProductComparisonViewModel = hiltViewModel()
) {
    var scannedBarcode by remember { mutableStateOf<String?>(null) }
    val uiState by viewModel.uiState.collectAsState()

    if (scannedBarcode != null) {
        // Përdor analyzeFromBarcode për barcode
        LaunchedEffect(scannedBarcode) {
            viewModel.analyzeFromBarcode(scannedBarcode!!, "food")
        }
    }

    // Shfaq rezultate
    ProductComparisonScreen(
        comparison = uiState.singleComparison,
        isLoading = uiState.isLoading
    )
}
```

### Photo Picker Screen
```kotlin
@Composable
fun PhotoPickerScreen(
    viewModel: ProductComparisonViewModel = hiltViewModel()
) {
    var selectedPhoto by remember { mutableStateOf<String?>(null) }
    val uiState by viewModel.uiState.collectAsState()

    if (selectedPhoto != null) {
        // Përdor analyzeFromPhoto për foto
        LaunchedEffect(selectedPhoto) {
            viewModel.analyzeFromPhoto(selectedPhoto!!, "food")
        }
    }

    // Shfaq rezultate
    ProductComparisonScreen(
        comparison = uiState.singleComparison,
        isLoading = uiState.isLoading
    )
}
```

---

## 📊 Tabela e Krahasimit

| Veçori | Barcode | Photo | Manual |
|--------|---------|-------|--------|
| Precisioni | Shumë i lartë | I mesëm | I varur |
| Shpejtësia | Shpejt | I ngadaltë | Shpejt |
| Kërkon në OFF | Po | Po | Po |
| Groq AI | Jo | Po | Jo |
| Ikonë | 📊 | 📸 | ✏️ |

---

## 🎯 Përfundim

Aplikacioni tani:
✅ Bën dallim midis barcodit dhe fotove
✅ Shfaq mesazhe të qarta në shqip
✅ Nuk ngat nëse produkti nuk gjendet
✅ Ofron opsion për shtim produkti
✅ Shfaq rekomandime të personalizuara

**Gati për prodhim!** 🚀

