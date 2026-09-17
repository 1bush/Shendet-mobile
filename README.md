# SHËNDETI-IM

Aplikacion React Native (Expo) që skanon produkte ushqimore, kozmetikë dhe ushqim për kafshë, analizon fatura, jep informacion për barna dhe ofron një asistent AI — **me AI lokale (Ollama), pa server cloud, pa çelës API, pa pagesë**.

> **Historik:** projekti më parë quhej `Shendet-mobile` / `SHËNDETI`. Emri i ri është **SHËNDETI-IM** (`app.json` → `name: "SHËNDETI-IM"`, `slug: "shendeti-im"`, paketa Android `al.shendeti.im`).

---

## Çfarë është SHËNDETI-IM?

**SHËNDETI-IM** (shqip: *"Shëndeti im"*) është një aplikacion celular në gjuhën shqipe që të ndihmon të **zgjedhësh produkte më të shëndetshme** duke skanuar barkodin ose duke fotografuar faturën e supermarketit.

**Ideja:** etiketat e produkteve janë shpesh në gjuhë të huaj, me shkronja të vogla dhe pa shpjegim. SHËNDETI-IM i kthen ato në shqip të thjeshtë dhe jep një **pikë nga 0–100** për çdo produkt, kështu që krahasimi bëhet i menjëhershëm — pa pasur nevojë të deshifrosh tabelën ushqyese.

### Për kë është

- Prindër që blejnë ushqim dhe duan të lexojnë etiketat
- Njerëz me alergji ose intolerancë (gluten, laktozë, arra, vezë)
- Persona me diabet, hipertension ose kolesterol të lartë
- Kushdo që do të dijë çfarë ka vërtet brenda paketimit

### Çfarë bën

| Funksioni | Çfarë bën |
|---|---|
| 🥗 **Ushqim** | Skanon barkodin → NutriScore, 11 vitamina & 11 minerale me % RDA, alergjenët, përbërësit, historiku |
| 🐾 **Kafshë** | Skanon ushqimin e qenit/maces → skor sipas proteinave, karbohidrateve dhe aditivëve |
| ✨ **Kozmetikë** | Skanon shampon/krem → zbulon paraben, SLS, formaldehyde, triclosan, phthalate |
| 🤖 **Asistenti** | Pyet në shqip për ushqimin, etiketat, alergjitë dhe dietat |
| 🧾 **Faturë** | Foto e faturës → produktet nxirren dhe vlerësohen një nga një |
| 💊 **Barnat** | Informacion bazë: përdorimi, dozimi, efektet anësore, ndërveprimet |
| 🗺️ **Harta** | Prodhues vendorë shqiptarë (ferma, bulmetore, bio) sipas kategorisë |
| 👤 **Profili** | Alergji dhe dieta për çdo anëtar të familjes |

### Parimet e projektit

| Parimi | Si zbatohet |
|---|---|
| **AI lokale, pa pagesë** | Ollama punon në kompjuterin tënd — pa abonim, pa kuota, pa çelës API |
| **Privatësi** | Fotot dhe pyetjet nuk dalin nga rrjeti yt lokal |
| **Punon offline** | Historia, kalkulimet e skorit dhe AI-ja lokale nuk kërkojnë internet |
| **Pa diagnoza mjekësore** | Vetëm edukim; te moduli i barnave shfaqet gjithmonë kujtesa për mjekun |

### Si funksionon pikëzimi

Skori **0–100** bazohet në NutriScore kur ai ekziston (`a`=92, `b`=72, `c`=50, `d`=28, `e`=10); përndryshe llogaritet nga vlerat për 100 g — sheqeri, yndyra e ngopur dhe kripa e ulin, ndërsa fibrat dhe proteinat e rrisin. I njëjti parim përdoret për kozmetikën (përbërës të rrezikshëm) dhe ushqimin e kafshëve (proteinat dhe aditivët).

---

## Përmbajtja

- [Çfarë është SHËNDETI-IM?](#çfarë-është-shëndeti-im)
- [Instalimi](#instalimi)
- [Struktura e projektit](#struktura-e-projektit)
- [AI lokale (Ollama)](#ai-lokale-ollama)
- [Ekranet dhe lidhjet](#ekranet-dhe-lidhjet)
- [Build për Android](#build-për-android)
- [Auditimi — problemet e rregulluara](#auditimi--problemet-e-rregulluara)
- [Probleme të njohura](#probleme-të-njohura)

---

## Instalimi

```powershell
cd "C:\Users\roven\Shendet-mobile"
npm install
npm start              # pastaj skano QR-në me Expo Go
# ose
npm run android        # emulator / pajisje e lidhur
```

Kërkesat: **Node.js 18+**, **Expo Go** në telefon ose Android Studio për build lokal.

---

## Struktura e projektit

```
Shendet-mobile/
├── App.js                      # Navigimi: 5 tab-e + 4 module në "Më shumë"
├── app.json                    # Konfigurimi Expo (emri, slug, paketa)
├── package.json                # Varësitë dhe skriptet
├── .env.example                # Shembull konfigurimi për Ollama
├── eas.json.example            # Konfigurimi EAS (build në cloud) — shembull
├── src/
│   ├── screens/                # Të gjitha ekranet
│   │   ├── FoodScreen.js       # Skanim ushqimi + vitamina/minerale + historik
│   │   ├── AnimalFoodScreen.js # Ushqim për kafshë
│   │   ├── CosmeticsScreen.js  # Kozmetikë (përbërës të rrezikshëm)
│   │   ├── SkanimFature.tsx    # Foto e faturës → analizë AI
│   │   ├── ModuliBarnas.tsx    # Barnat & ilaçet
│   │   ├── HartaProdukteve.tsx # Prodhues vendorë
│   │   ├── ProfiletFamiljare.tsx # Alergji & dieta
│   │   ├── AsistentAI.tsx      # Asistenti AI
│   │   └── Menu.tsx            # Ekrani "Më shumë"
│   ├── components/
│   │   ├── BarcodeScanner.js   # Skaner barkodi (expo-camera)
│   │   └── HistoryPanel.js     # Paneli i historikut
│   ├── lib/
│   │   ├── ollama.ts           # Klienti Ollama (AI lokale)
│   │   └── aiFallback.ts       # Përgjigje lokale kur AI nuk është aktiv
│   └── utils/
│       └── storage.js          # AsyncStorage: historia e kërkimeve
├── scripts/
│   ├── ollama-setup.ps1        # Instalon/konfiguron Ollama
│   └── ollama-check.js         # Test i shpejtë i Ollama
└── web/                        # PWA legacy (jo e nevojshme për aplikacionin)
    ├── index.html
    ├── manifest.json
    ├── service-worker.js
    └── YukaShqip.jsx           # Referencë e vjetër (React për web)
```

---

## AI lokale (Ollama)

Të gjitha veçoritë AI përdorin **Ollama** në rrjetin tënd lokal. Nuk ka më thirrje drejt shërbimeve cloud dhe **asnjë çelës API nuk ruhet në aplikacion**.

### 1. Instalimi i Ollama (një herë)

```powershell
cd "C:\Users\roven\Shendet-mobile"
powershell -ExecutionPolicy Bypass -File .\scripts\ollama-setup.ps1
```

Skripti kontrollon instalimin, nis serverin, tërheq modelët dhe shfaq IP-në e PC-së.

### 2. Testimi

```powershell
npm run ollama                                            # localhost
node scripts/ollama-check.js http://192.168.1.50:11434    # pajisje fizike
```

### 3. Adresa (`OLLAMA_BASE_URL`)

| Ambient | Adresa | Shpjegim |
|---|---|---|
| Emulator Android | `http://10.0.2.2:11434` | `10.0.2.2` = localhost i PC-së |
| iOS simulator | `http://localhost:11434` | i njëjti kompjuter |
| Pajisje fizike | `http://<IP-e-PC-së>:11434` | të dyja në të njëjtin WiFi |

Kopjo `.env.example` → `.env` dhe vendos:

```env
EXPO_PUBLIC_OLLAMA_URL=http://192.168.1.50:11434
EXPO_PUBLIC_OLLAMA_MODEL=llama3.2
EXPO_PUBLIC_OLLAMA_VISION_MODEL=llava
```

Për pajisje fizike, Ollama duhet të dëgjojë në rrjet:

```powershell
[Environment]::SetEnvironmentVariable("OLLAMA_HOST","0.0.0.0:11434","User")
# rinis Ollama dhe lejo portin në Windows Firewall
```

### 4. Modelët

| Modeli | Përdorimi | Komanda |
|---|---|---|
| `llama3.2` | Asistenti AI, barnat | `ollama pull llama3.2` |
| `llava` | Analiza e faturës (foto) | `ollama pull llava` |

**Pa Ollama aplikacioni punon gjithsesi:** asistenti kthen përgjigje nga baza lokale, barnat kërkohen në bazën e brendshme (5 ilaçe), fatura shfaq një udhëzim. Asnjë ekran nuk bllokohet.

---

## Ekranet dhe lidhjet

`App.js` regjistron **9 ekrane**: 5 shfaqen në shiritin e tab-eve dhe 4 hapen nga ekrani **"Më shumë"**.

| Tab i dukshëm | Skedari | Çfarë bën |
|---|---|---|
| 🥗 Ushqim | `src/screens/FoodScreen.js` | Barkod → Open Food Facts; vitamina/minerale, NutriScore, historik, skaner |
|  Kafshë | `src/screens/AnimalFoodScreen.js` | Barkod → ushqim për kafshë, skor sipas proteinave/aditivëve |
| ✨ Kozmetikë | `src/screens/CosmeticsScreen.js` | Barkod → përbërës të rrezikshëm (paraben, SLS, etj.) |
| 🤖 Asistenti | `src/screens/AsistentAI.tsx` | Bisedë me Ollama; pa server kthen përgjigje lokale |
| ➕ Më shumë | `src/screens/Menu.tsx` | Hyrje për modulet dytësore |

| Modul (nga "Më shumë") | Skedari | Çfarë bën |
|---|---|---|
| 🧾 Faturë | `src/screens/SkanimFature.tsx` | Foto → `llava` lexon produktet dhe jep skor |
|  Barnat | `src/screens/ModuliBarnas.tsx` | Ollama ose baza lokale (Paracetamol, Ibuprofen, …) |
| ️ Harta | `src/screens/HartaProdukteve.tsx` | 10 prodhues vendorë, filtrim sipas kategorisë |
|  Profili | `src/screens/ProfiletFamiljare.tsx` | Alergji & dieta (gjendje lokale) |

**Zinxhiri i varësive:**

```
App.js
 └── src/screens/*  ──> src/lib/ollama.ts        (AI lokale)
                   ──> src/lib/aiFallback.ts     (kur Ollama mungon)
                   ──> src/utils/storage.js      (AsyncStorage)
                   ──> src/components/HistoryPanel.js
                   ──> src/components/BarcodeScanner.js  (FoodScreen, butoni 📷)
```

---

## Build për Android

### Opsioni A — Expo Go (më i shpejti)

```powershell
npm start          # skano QR-në me aplikacionin Expo Go
```

### Opsioni B — APK lokal (pa internet, pa EAS)

```powershell
npm run build:apk:local
# APK: android\app\build\outputs\apk\debug\app-debug.apk
```

Kërkon **JDK 17** dhe **Android SDK**. Për build **release**:

```powershell
npx expo prebuild --platform android
cd android
.\gradlew.bat assembleRelease
```

### Opsioni C — EAS (cloud)

`eas.json` u riemërtua në `eas.json.example` sepse kërkon llogari Expo.

```powershell
Copy-Item eas.json.example eas.json
npx eas-cli build --platform android --profile preview
```

> **Kujdes:** dosja `android/` nuk ruhet në git (shih `.gitignore`) sepse gjenerohet nga `expo prebuild`.

---

## Auditimi — problemet e rregulluara

Auditimi i plotë i kodit zbuloi probleme që e bënin aplikacionin **të papërdorshëm**. Të gjitha u rregulluan:

| # | Problemi | Ndikimi | Rregullimi |
|---|---|---|---|
| 1 | `App.js` importonte `./src/screens/*` që **nuk ekzistonin** | Aplikacioni nuk nisej | Skedarët u zhvendosën në `src/screens/` |
| 2 | `FoodScreen.js` përmbante **dy skedarë të bashkuar** (rreshti `}); React, { useState }`) | Sintaksë e pavlefshme JS | U rindërtua versioni i plotë (357 rreshta) |
| 3 | Mungonin `src/utils/storage.js` dhe `src/components/HistoryPanel.js` | Importet dështonin | U krijuan të dy |
| 4 | Skedarë me emra të pavlefshëm: `Asistentai · TSX`, `Skanimfature · TSX`, `Json`, `Screens` | Nuk importoheshin dot | → `AsistentAI.tsx`, `SkanimFature.tsx`, `web/manifest.json`; `Screens` u fshi |
| 5 | `app.json`/`4.index.html` referonin `./assets/icon.png` që **mungon** | `expo-doctor` gabim | Referencat u hoqën; dosja `assets/` u krijua |
| 6 | **3 ekrane** thërrisnin `api.anthropic.com` me `claude-sonnet-4-6` (model i pavlefshëm) + çelës API në klient | Nuk punonin fare; çelësi do ekspozohej | U zëvendësuan me **Ollama lokal** |
| 7 | **Groq**: 0 referenca në repo dhe në historikun git | — | Nuk kishte çfarë të hiqej; AI tani është vetëm Ollama |
| 8 | `service-worker.js` cache-onte `/static/js/main.js` që nuk ekziston | Service worker nuk instalohej | Lista u kufizua në skedarë ekzistues |
| 9 | `BarcodeScanner.js` ishte i palidhur (asnjë ekran nuk e përdorte) | Kod i vdekur | U lidh në `FoodScreen` (butoni 📷) |
| 10 | Teksti shqip i korruptuar (*mojibake*) në 3 skedarë: `ShkÃ«lqyer`, `L'OrÃ©al` | Etiketat e gabuara | Riparim i synuar i kodimit UTF-8 |
| 11 | Mungonin varësitë `expo-image-picker` (e përdorur nga `SkanimFature`) dhe `@react-native-async-storage/async-storage`, `@expo/vector-icons` | `npm install` i pamjaftueshëm | U shtuan në `package.json` |
| 12 | `eas.json` kërkonte llogari EAS | Build lokal i bllokuar | → `eas.json.example` |
| 13 | Nuk kishte `.gitignore` | `node_modules` dhe `.env` rrezikonin commit | U krijua |

---

## Probleme të njohura

1. **Ikona / splash:** dosja `assets/` shtë bosh — Expo përdor ikonën e paracaktuar. Shto `icon.png`, `adaptive-icon.png`, `splash.png` dhe riktheji referencat në `app.json`.
2. **PWA (`web/`):** dosja `web/icons/` mungon; `web/YukaShqip.jsx` është version i vjetër web (React + CSS inline), jo i lidhur me aplikacionin mobil. Mund të fshihet nëse nuk përdoret.
3. **`ProfiletFamiljare.tsx`:** importon `AsyncStorage` por profilin e mban vetëm në memorie (nuk ruhet pas mbylljes). Mund të lidhet me `utils/storage.js`.
4. **Internet:** kërkimi i barkodit kërkon `world.openfoodfacts.org`. Pa internet produktet e reja nuk gjenden; historiku lokal punon gjithsesi.
5. **Pajisje fizike + Ollama:** kërkon `OLLAMA_HOST=0.0.0.0:11434` dhe portë të hapur në Firewall.
6. **Emri i paketës:** `al.shendeti.im` (më parë `al.shendet.mobile`) — instalon si aplikacion i veçantë. Riktheje nëse do vazhdimësi.

---

## Licenca

Projekt privat. Të dhënat e produkteve vijnë nga [Open Food Facts](https://world.openfoodfacts.org) (ODbL).