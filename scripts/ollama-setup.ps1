# ─────────────────────────────────────────────────────────────────
#  Ollama — konfigurimi i AI lokale për SHËNDETI-IM
#  Ekzekuto në PowerShell:  .\scripts\ollama-setup.ps1
# ─────────────────────────────────────────────────────────────────

Write-Host "`n=== SHËNDETI-IM · Konfigurimi i Ollama ===`n" -ForegroundColor Green

# 1. A është instaluar Ollama?
$ollama = Get-Command ollama -ErrorAction SilentlyContinue
if (-not $ollama) {
    Write-Host "[X] Ollama nuk u gjet në PATH." -ForegroundColor Red
    Write-Host "    Shkarkoje nga: https://ollama.com/download/windows" -ForegroundColor Yellow
    Write-Host "    Pas instalimit, mbyll dhe rihap terminalin, pastaj provo përsëri.`n"
    exit 1
}
Write-Host "[OK] Ollama u gjet: $($ollama.Source)" -ForegroundColor Green

# 2. A është duke punuar serveri?
$aktiv = $false
try {
    $null = Invoke-RestMethod -Uri 'http://localhost:11434/api/tags' -TimeoutSec 4
    $aktiv = $true
    Write-Host "[OK] Serveri Ollama është aktiv (localhost:11434)" -ForegroundColor Green
} catch {
    Write-Host "[!] Serveri nuk përgjigjet. Po e nis 'ollama serve'..." -ForegroundColor Yellow
    Start-Process -FilePath 'ollama' -ArgumentList 'serve' -WindowStyle Minimized
    Start-Sleep -Seconds 5
    try {
        $null = Invoke-RestMethod -Uri 'http://localhost:11434/api/tags' -TimeoutSec 6
        $aktiv = $true
        Write-Host "[OK] Serveri u nis me sukses." -ForegroundColor Green
    } catch {
        Write-Host "[X] Serveri nuk u nis. Hape 'ollama' manualisht dhe provo përsëri." -ForegroundColor Red
    }
}

# 3. Tërhiq modelët e nevojshme
if ($aktiv) {
    $tekerkuar = @(
        @{ Emri = $env:EXPO_PUBLIC_OLLAMA_MODEL; Parazgjedhur = 'llama3.2'; Lloji = 'tekst' },
        @{ Emri = $env:EXPO_PUBLIC_OLLAMA_VISION_MODEL; Parazgjedhur = 'llava'; Lloji = 'foto' }
    )

    $instaluar = @()
    try {
        $instaluar = (Invoke-RestMethod -Uri 'http://localhost:11434/api/tags' -TimeoutSec 6).models.name
    } catch { }

    foreach ($m in $tekerkuar) {
        $emri = if ($m.Emri) { $m.Emri } else { $m.Parazgjedhur }
        if ($instaluar -match "^$([regex]::Escape($emri))") {
            Write-Host "[OK] Modeli '$emri' ($($m.Lloji)) është i instaluar." -ForegroundColor Green
        } else {
            Write-Host "[>] Duke tërhequr '$emri' ($($m.Lloji)) — mund të zgjasë disa minuta..." -ForegroundColor Cyan
            & ollama pull $emri
            if ($LASTEXITCODE -eq 0) {
                Write-Host "[OK] '$emri' u instalua." -ForegroundColor Green
            } else {
                Write-Host "[X] Tërheqja e '$emri' dështoi." -ForegroundColor Red
            }
        }
    }
}

# 4. IP-ja e PC-së për pajisje fizike
Write-Host "`n=== Adresa për pajisje fizike ===" -ForegroundColor Green
$ip = (Get-NetIPAddress -AddressFamily IPv4 -ErrorAction SilentlyContinue |
    Where-Object { $_.IPAddress -notlike '127.*' -and $_.IPAddress -notlike '169.254.*' } |
    Select-Object -First 1 -ExpandProperty IPAddress)

if ($ip) {
    Write-Host "IP e këtij PC-je: $ip" -ForegroundColor Cyan
    Write-Host "Vendose në skedarin .env:" -ForegroundColor Yellow
    Write-Host "  EXPO_PUBLIC_OLLAMA_URL=http://${ip}:11434`n" -ForegroundColor White
} else {
    Write-Host "Nuk u gjet IP. Kontrollo lidhjen e rrjetit.`n" -ForegroundColor Yellow
}

Write-Host "Kujdes: Ollama duhet të dëgjojë në rrjetin lokal." -ForegroundColor Yellow
Write-Host "Nëse pajisja nuk lidhet, vendos variablin:" -ForegroundColor Yellow
Write-Host '  [Environment]::SetEnvironmentVariable("OLLAMA_HOST","0.0.0.0:11434","User")' -ForegroundColor White
Write-Host "pastaj rinis Ollama.`n"

Write-Host "Test i shpejtë nga Node:  npm run ollama`n" -ForegroundColor Green
