# Compile and run Snake game without Maven
# This works for keyboard mode only (no voice control)

Write-Host "`n=== Compiling Snake Voice Game ===" -ForegroundColor Cyan
Write-Host ""

# Create output directory
if (-not (Test-Path "out")) {
    New-Item -ItemType Directory -Path "out" | Out-Null
}

# Find all Java files
$sourceFiles = Get-ChildItem -Path "src/main/java" -Filter "*.java" -Recurse

Write-Host "Compiling Java files..." -ForegroundColor Yellow

# Compile
$sourceFilePaths = $sourceFiles | ForEach-Object { $_.FullName }
javac -d out -sourcepath "src/main/java" @sourceFilePaths

if ($LASTEXITCODE -eq 0) {
    Write-Host "  Compilation successful!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Starting game..." -ForegroundColor Yellow
    Write-Host "Note: Voice control disabled (needs Maven build)" -ForegroundColor Gray
    Write-Host ""
    
    # Run the game
    java -cp out com.snakevoicegame.SnakeVoiceGame
    
    Write-Host ""
    Write-Host "Game closed." -ForegroundColor Cyan
} else {
    Write-Host "  Compilation failed!" -ForegroundColor Red
}

Write-Host ""
