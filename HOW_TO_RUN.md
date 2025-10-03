# How to Run Snake Voice Game

## 🎮 Quick Start (Play NOW with Keyboard)

Your game is **already working** with all the UI improvements! Just run:

```powershell
java -jar target/SnakeVoiceGame-0.1.0-SNAPSHOT.jar
```

Then:
1. Click **Settings**
2. Select **"⌨ Keyboard"** mode  
3. Click **Start Game**
4. Use **Arrow Keys** to play
5. Press **P** to pause

**Note**: Voice control won't work yet because Maven dependencies aren't installed. But keyboard mode works perfectly!

---

## 🎤 Enable Voice Control (Requires Maven)

### Step 1: Check What You Have

Run this checker script:
```powershell
.\check-setup.ps1
```

### Step 2: Install Maven (if needed)

1. **Download Maven**:
   - Go to: https://maven.apache.org/download.cgi
   - Download: `apache-maven-3.9.x-bin.zip` (Binary zip archive)

2. **Extract**:
   - Extract to: `C:\Program Files\Apache\maven`

3. **Add to PATH**:
   - Press `Windows + X` → System
   - Advanced system settings → Environment Variables
   - Under "System Variables" find `Path` → Edit
   - Click "New" → Add: `C:\Program Files\Apache\maven\bin`
   - Click OK on all dialogs

4. **Verify** (open NEW PowerShell):
   ```powershell
   mvn -version
   ```
   
   Should show: `Apache Maven 3.x.x`

### Step 3: Build with Voice Support

```powershell
mvn clean package
```

This will:
- Download CMU Sphinx libraries
- Compile your updated code
- Create a new JAR with all dependencies

### Step 4: Run the Game

```powershell
java -jar target/SnakeVoiceGame-0.1.0-SNAPSHOT.jar
```

Or use the launcher script:
```powershell
.\run-game.ps1
```

Now voice control should work! Look for the **green dot** 🟢 in the top-right corner when playing.

---

## 🎯 Game Controls

### Keyboard Mode:
- **Arrow Keys** - Move snake
- **P** - Pause/Resume
- **Enter** - Restart after game over

### Voice Mode:
- Say **"UP"** - Move up
- Say **"DOWN"** - Move down
- Say **"LEFT"** - Move left
- Say **"RIGHT"** - Move right
- Say **"RESTART"** - Restart after game over
- **P** - Pause (keyboard still works)

---

## 🐛 Troubleshooting

### "mvn is not recognized"
➜ Maven not installed. Follow Step 2 above.

### "VoiceController: Sphinx not available"
➜ Normal if you didn't build with Maven. Use keyboard mode, or install Maven and rebuild.

### Voice control not responding
➜ Check:
1. Microphone is working
2. Green dot 🟢 shows in top-right
3. Console output (should show "Listening...")
4. See `VOICE_CONTROL_GUIDE.md` for detailed help

### Game won't start
➜ Make sure you have Java 17+:
```powershell
java -version
```

---

## 📁 Project Structure

```
SnakeVoiceGame/
├── run-game.ps1          ← Easy launcher
├── check-setup.ps1       ← Check your setup
├── HOW_TO_RUN.md         ← This file
├── VOICE_CONTROL_GUIDE.md ← Voice troubleshooting
├── IMPROVEMENTS.md       ← What was improved
├── pom.xml              ← Maven config
├── src/                 ← Source code
└── target/              ← Compiled files
    └── SnakeVoiceGame-0.1.0-SNAPSHOT.jar  ← The game!
```

---

## ✨ What's New in This Version

✅ **Modern UI** - Dark theme, gradients, rounded edges  
✅ **Better Snake Graphics** - 3D gradient snake  
✅ **Enhanced Food** - Red and gold gradient circles  
✅ **Pause Feature** - Press P to pause  
✅ **Voice Status Indicator** - See if voice is listening  
✅ **Improved Voice Control** - Faster response time  
✅ **Better Menus** - Emojis and styled buttons  

See `IMPROVEMENTS.md` for full details!

---

## 🎓 Summary

### Without Maven (Keyboard Only):
```powershell
# Just run this:
java -jar target/SnakeVoiceGame-0.1.0-SNAPSHOT.jar

# Select keyboard mode in settings
# Play with arrow keys!
```

### With Maven (Voice + Keyboard):
```powershell
# One-time setup:
# 1. Install Maven from maven.apache.org
# 2. Add to PATH
# 3. Open new PowerShell

# Build once:
mvn clean package

# Then run:
java -jar target/SnakeVoiceGame-0.1.0-SNAPSHOT.jar

# Select voice mode in settings
# Say commands to play!
```

---

**Enjoy your enhanced Snake Voice Game!** 🐍🎮🎤
