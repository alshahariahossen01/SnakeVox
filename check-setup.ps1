# Snake Voice Game - Setup Checker
Write-Host "`n=== Snake Voice Game Setup Checker ===" -ForegroundColor Cyan
Write-Host ""

# Check Java
Write-Host "Checking Java..." -ForegroundColor Yellow
$javaOK = $false
try {
    $javaOutput = java -version 2>&1
    $javaVersion = $javaOutput | Select-String "version" | Select-Object -First 1
    if ($javaVersion) {
        Write-Host "  Java is installed: $javaVersion" -ForegroundColor Green
        $javaOK = $true
    }
} catch {
    Write-Host "  Java not found. Please install Java 17 or higher." -ForegroundColor Red
    Write-Host "  Download from: https://adoptium.net/" -ForegroundColor Yellow
}

# Check Maven
Write-Host "`nChecking Maven..." -ForegroundColor Yellow
$mavenOK = $false
try {
    $mvnOutput = mvn -version 2>&1
    $mvnVersion = $mvnOutput | Select-String "Apache Maven" | Select-Object -First 1
    if ($mvnVersion) {
        Write-Host "  Maven is installed: $mvnVersion" -ForegroundColor Green
        $mavenOK = $true
    }
} catch {
    Write-Host "  Maven not found." -ForegroundColor Red
    Write-Host "  Download from: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
}

# Summary
Write-Host "" 
Write-Host "=== Summary ===" -ForegroundColor Cyan
Write-Host ""

if ($javaOK -and $mavenOK) {
    Write-Host "  All requirements met! You can build with voice control." -ForegroundColor Green
    Write-Host ""
    Write-Host "Run these commands:" -ForegroundColor Yellow
    Write-Host "  mvn clean package" -ForegroundColor White
    Write-Host "  java -jar target/SnakeVoiceGame-0.1.0-SNAPSHOT.jar" -ForegroundColor White
} elseif ($javaOK) {
    Write-Host "  Java is ready" -ForegroundColor Green
    Write-Host "  Maven is missing - Voice control won't work" -ForegroundColor Red
    Write-Host ""
    Write-Host "You can still play with keyboard mode:" -ForegroundColor Yellow
    Write-Host "  java -jar target/SnakeVoiceGame-0.1.0-SNAPSHOT.jar" -ForegroundColor White
    Write-Host ""
    Write-Host "To enable voice control, install Maven from:" -ForegroundColor Yellow
    Write-Host "  https://maven.apache.org/download.cgi" -ForegroundColor Cyan
} else {
    Write-Host "  Java is missing - Cannot run the game" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please install Java 17+ from:" -ForegroundColor Yellow
    Write-Host "  https://adoptium.net/" -ForegroundColor Cyan
}

Write-Host ""
