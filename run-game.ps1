# Snake Voice Game Launcher
Write-Host "`n🐍 Starting Snake Voice Game... 🎮`n" -ForegroundColor Green

# Check if JAR exists
if (Test-Path "target/SnakeVoiceGame-0.1.0-SNAPSHOT.jar") {
    java -jar target/SnakeVoiceGame-0.1.0-SNAPSHOT.jar
} else {
    Write-Host "Error: Game JAR not found!" -ForegroundColor Red
    Write-Host "Please build the game first:" -ForegroundColor Yellow
    Write-Host "  mvn clean package" -ForegroundColor White
    Write-Host "`nOr check if you're in the SnakeVoiceGame directory." -ForegroundColor Yellow
}

Write-Host "`nGame closed.`n" -ForegroundColor Cyan
