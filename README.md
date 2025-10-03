# 🐍 Voice-Controlled Snake Game

A stunning, modern Snake game with **voice control**, beautiful graphics, and smooth gameplay!

![Java](https://img.shields.io/badge/Java-21-orange)
![Maven](https://img.shields.io/badge/Maven-3.9-blue)
![Vosk](https://img.shields.io/badge/Vosk-0.3.45-green)
![Status](https://img.shields.io/badge/Status-Production_Ready-brightgreen)

> **New!** 🎤 Now with **working voice control** powered by Vosk AI!

---

## ✨ Features

### 🎤 Voice Control (NEW!)
- **Offline Speech Recognition** - Powered by Vosk AI
- **Real-time Commands** - Instant response to voice
- **Visual Indicator** - Green dot shows when listening
- **Simple Commands** - Just say "UP", "DOWN", "LEFT", "RIGHT"
- **No Internet Required** - 100% offline operation
- **Privacy-First** - Audio never leaves your computer

### 🎨 Beautiful Modern UI
- **Dark Theme** - Sleek navy blue interface with gradients
- **3D Graphics** - Snake and food with depth and shadows
- **Enhanced Food** - Red dots (10 pts) and special gold dots (50 pts!)
- **Styled Menus** - Custom buttons with hover effects and emojis (🐍 🎮 ⚙️)
- **Smooth Animations** - Anti-aliased rendering for crisp visuals
- **Professional Polish** - Modern color scheme and typography

### 🎮 Game Features
- **4 Game Modes:**
  - 🟢 **Classic** - Traditional snake, slow start, good for learning
  - 🟡 **Intermediate** - Medium difficulty with special food bonuses
  - 🔴 **Labyrinth** - Navigate around obstacles and walls!
  - ⚫ **Expert** - Maximum speed and challenge
- **Pause System** - Press P anytime to pause/resume
- **High Score Tracking** - Separate high/low scores for each mode
- **Progressive Difficulty** - Speed increases as you score points
- **Special Gold Food** - 50-point bonuses appear randomly
- **New High Score Celebration** - Gold star animation when you win!

### 🎯 Dual Control Modes
**🎤 Voice Control:**
- Say "UP" - Move up
- Say "DOWN" - Move down
- Say "LEFT" - Move left
- Say "RIGHT" - Move right
- Say "RESTART" - Restart after game over

**⌨️ Keyboard Control:**
- Arrow Keys (↑ ↓ ← →) - Move the snake
- P - Pause/Resume
- Enter - Restart after game over

---

## 🚀 Quick Start

### Requirements
- ✅ **Java 17 or higher** - [Download here](https://adoptium.net/)
- ✅ **Maven 3.6+** - For building from source
- ✅ **Microphone** - For voice control (optional)
- ✅ **Vosk Model** - Already included at `vosk-model-small-en-us-0.15/`

### Run the Game (Easiest Way)

**Double-click the JAR file:**
```
SnakeVoiceGame/target/SnakeVoiceGame-0.1.0-SNAPSHOT.jar
```

**Or run from command line:**
```bash
cd SnakeVoiceGame
java -jar target/SnakeVoiceGame-0.1.0-SNAPSHOT.jar
```

### Build from Source
```bash
cd SnakeVoiceGame
mvn clean package
java -jar target/SnakeVoiceGame-0.1.0-SNAPSHOT.jar
```

> **Note:** The Vosk model (`vosk-model-small-en-us-0.15`) must be in the same directory as where you run the JAR from.

---

## 📖 How to Play

### 🎮 Getting Started

1. **Launch the game** - Run the JAR file
2. **Choose Control Mode:**
   - Click **"⚙ Settings"**
   - Select **"🎤 Voice"** or **"⌨ Keyboard"**
3. **Select Game Mode:**
   - Choose from Classic, Intermediate, Labyrinth, or Expert
   - View current high scores for each mode
4. **Click "← Back to Menu"** then **"▶ Start Game"**
5. **Play!**
   - Eat food to grow and score points
   - Avoid walls, obstacles (in Labyrinth), and yourself
   - Try to beat your high score!

### 🎤 Using Voice Control

1. **Enable Voice Mode** in Settings
2. **Start the game**
3. **Look for the green indicator** 🟢 in the top-right corner
   - Green = Listening
   - Gray = Not active
4. **Speak commands clearly:**
   - "UP" - Move upward
   - "DOWN" - Move downward
   - "LEFT" - Move left
   - "RIGHT" - Move right
   - "RESTART" - Restart after game over

**Tips for Best Results:**
- Speak clearly and at normal volume
- Reduce background noise
- Use a good quality microphone
- Say just the command word (not "go up", just "UP")
- Wait briefly between commands (~300ms cooldown)

### 🏆 Scoring System
- 🔴 **Red Food:** 10 points (common)
- 🟡 **Gold Food:** 50 points (rare, special!)
- **Speed Bonus:** Game speeds up as you score more
- **High Scores:** Tracked separately for each game mode

### 🎯 Game Modes Explained

**🟢 Classic Mode:**
- Perfect for beginners
- Slow starting speed
- Standard difficulty progression
- 50% chance for gold food

**🟡 Intermediate Mode:**
- Medium starting speed
- Faster progression
- 40% chance for gold food
- Good challenge for experienced players

**🔴 Labyrinth Mode:**
- **Obstacles on the field!**
- Navigate around walls and barriers
- Slower speed to compensate
- 50% chance for gold food
- Most strategic mode

**⚫ Expert Mode:**
- Fastest starting speed
- Rapid difficulty scaling
- 30% chance for gold food
- Maximum challenge for pros

---

## 🎨 Visual Features

### Modern Interface
- **Dark Theme** - Easy on the eyes, professional look
- **Gradient Effects** - Smooth color transitions
- **Rounded Corners** - Modern, polished appearance
- **Shadow Effects** - Depth and dimension
- **Emoji Integration** - Fun, expressive UI

### In-Game Graphics
- **3D Snake** - Bright green gradient with rounded segments
- **Enhanced Food** - Circular dots with gradient fills
- **Obstacle Blocks** - Gray gradient walls (Labyrinth mode)
- **Pause Overlay** - Semi-transparent with large text
- **Game Over Screen** - Gradient background with gold star celebration
- **Score Display** - Gold-colored score counter

### Menu System
- **Custom Buttons** - Hand-crafted with hover effects
- **Styled Radio Buttons** - Enhanced selection indicators
- **Real-time Scores** - Live high/low score display
- **Smooth Transitions** - Card-based navigation

---

## 📁 Project Structure

```
SnakeVoiceGame/
├── src/main/java/com/snakevoicegame/
│   ├── SnakeVoiceGame.java          # Main application class
│   ├── GamePanel.java               # Core game logic & rendering
│   ├── MenuPanel.java               # Main menu UI
│   ├── SettingsPanel.java           # Settings screen UI
│   ├── ScoreManager.java            # High score persistence
│   ├── VoskVoiceController.java     # Vosk voice control (NEW!)
│   ├── VoiceController.java         # Legacy Sphinx controller
│   ├── GameMode.java                # Game mode enumeration
│   └── ControlMode.java             # Control mode enumeration
│
├── src/main/resources/
│   └── grammars/commands.gram       # Voice command grammar
│
├── vosk-model-small-en-us-0.15/     # Vosk speech model
│   ├── am/                          # Acoustic model
│   ├── conf/                        # Configuration
│   ├── graph/                       # Language graph
│   └── ...
│
├── target/
│   └── SnakeVoiceGame-0.1.0-SNAPSHOT.jar  # Executable JAR (with dependencies)
│
├── pom.xml                          # Maven build configuration
├── README.md                        # This file
├── VOICE_SETUP_COMPLETE.md          # Voice control guide
├── VOSK_IMPLEMENTATION_GUIDE.md     # Technical Vosk details
├── VOICE_ALTERNATIVES.md            # Other voice options
├── IMPROVEMENTS.md                  # Enhancement changelog
└── HOW_TO_RUN.md                   # Detailed run instructions
```

---

## 🔧 Technical Details

### Core Technologies
- **Language:** Java 21 (LTS)
- **Build Tool:** Maven 3.9.11
- **GUI Framework:** Java Swing
- **Graphics Engine:** Java2D with anti-aliasing
- **Speech Recognition:** Vosk 0.3.45

### Dependencies
```xml
<!-- Voice Control -->
<dependency>
    <groupId>com.alphacephei</groupId>
    <artifactId>vosk</artifactId>
    <version>0.3.45</version>
</dependency>

<!-- JSON Parsing -->
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>
```

### Architecture
- **MVC Pattern:** Separation of game logic, UI, and data
- **Event-Driven:** Swing event handlers and timers
- **Multi-threaded:** Separate thread for voice recognition
- **Resource Management:** Proper cleanup with AutoCloseable
- **State Management:** Atomic operations for thread safety

### Performance
- **FPS:** 6.67 (150ms game tick)
- **Voice Latency:** ~200-500ms
- **Memory:** ~100MB with Vosk model loaded
- **Startup Time:** 2-5 seconds

### AI/ML Components
- **Vosk ASR:** Lightweight offline speech recognition
- **Model:** Small English US (40MB)
- **Accuracy:** ~85-95% for simple commands
- **Processing:** Real-time audio stream processing

---

## 📝 Version History

### 🎉 Version 2.1 - Voice Control Update (October 2025)
**Major Update: Working Voice Control!**
- 🎤 **NEW:** Vosk AI integration for offline speech recognition
- 🎤 **NEW:** Real-time voice command processing
- 🎤 **NEW:** Visual listening indicator (green/gray dot)
- 🎤 **NEW:** Console logging for voice debugging
- ✨ Voice control now fully functional
- ✨ Privacy-first: All processing happens locally
- ✨ No internet required for voice features
- 🔧 Improved error handling and status reporting
- 🔧 Added comprehensive voice documentation

### Version 2.0 - UI Overhaul (October 2025)
**Complete Visual Redesign**
- ✨ Modern dark theme with navy blue gradients
- ✨ 3D gradient graphics for snake and food
- ✨ Enhanced menus with emojis and styled buttons
- ✨ Pause functionality (Press P)
- ✨ Improved game over screen with gold star animation
- ✨ Better color scheme and typography
- ✨ Rounded corners and shadow effects
- ✨ Smooth animations with anti-aliasing
- 🎨 Professional polish throughout
- 🔧 Optimized rendering performance

### Version 1.0 - Initial Release
- Basic snake game functionality
- 4 game modes (Classic, Intermediate, Labyrinth, Expert)
- Keyboard controls
- High score tracking
- Simple graphics

---

## 🐛 Troubleshooting

### Voice Control Issues

**🔴 Problem: No green indicator / Voice not working**
- ✅ **Check:** Is Voice mode selected in Settings?
- ✅ **Check:** Console for error messages
- ✅ **Check:** Microphone is connected and working
- ✅ **Check:** Windows microphone permissions enabled
- ✅ **Check:** Vosk model exists at `vosk-model-small-en-us-0.15/`

**🔴 Problem: Commands not recognized**
- ✅ Speak clearly and at normal volume
- ✅ Reduce background noise
- ✅ Say just the command word ("UP" not "go up")
- ✅ Check console - is it hearing you? Look for "Heard: ..."
- ✅ Try a better microphone (headset recommended)

**🔴 Problem: Model not found error**
- ✅ Ensure `vosk-model-small-en-us-0.15/` folder exists
- ✅ Model must be in same directory as where you run JAR
- ✅ Check path in console error message
- ✅ Re-extract the model if needed

### General Issues

**🔴 Problem: Game won't start**
- ✅ Check Java version: `java -version` (need Java 17+)
- ✅ Re-download JAR file
- ✅ Check console for error messages

**🔴 Problem: Poor performance / lag**
- ✅ Close other applications
- ✅ Update graphics drivers
- ✅ Try running in keyboard mode
- ✅ Reduce background processes

**🔴 Problem: High scores not saving**
- ✅ Check file permissions in game directory
- ✅ Ensure not running from read-only location
- ✅ Try running as administrator

---

## 🤝 Contributing

We welcome contributions! Here are some ideas:

### Potential Enhancements
- 🎵 Add sound effects and background music
- 🎨 Create additional themes/skins
- 🎮 Add more game modes (Speed Run, Survival, etc.)
- 💪 Implement power-ups (speed boost, shield, etc.)
- 👥 Add multiplayer support
- 🌍 Add internationalization (multiple languages)
- 📊 Add statistics tracking (games played, total score, etc.)
- 🏆 Add achievements system
- 🎨 Add particle effects
- 🎤 Add more voice command variations

### Development Guidelines
- Use Java 17+ features
- Follow existing code style
- Add JavaDoc comments
- Test all game modes
- Update documentation
- Ensure voice control compatibility

---

## 📚 Documentation

- **README.md** (this file) - Main documentation
- **VOICE_SETUP_COMPLETE.md** - Voice control usage guide
- **VOSK_IMPLEMENTATION_GUIDE.md** - Technical Vosk integration details
- **VOICE_ALTERNATIVES.md** - Alternative voice control options
- **IMPROVEMENTS.md** - Complete list of enhancements
- **HOW_TO_RUN.md** - Detailed run and build instructions
- **CHANGELOG.md** - Detailed version history

---

## 📄 License

This project is open source and available for educational purposes.

**MIT License** - Feel free to use, modify, and distribute.

---

## 🙏 Acknowledgments

- **Vosk** - Offline speech recognition (Alpha Cephei)
- **CMU Sphinx** - Original voice control inspiration
- **Java Community** - For Swing and Java2D
- **You!** - For playing and contributing

---

## 📞 Support

Having issues? Check:
1. **VOICE_SETUP_COMPLETE.md** - Voice control troubleshooting
2. **HOW_TO_RUN.md** - Setup and run instructions
3. **Console output** - Look for error messages
4. **Documentation files** - Comprehensive guides included

---

## 🎮 Enjoy Playing!

Have fun with your voice-controlled Snake game! Try to beat your high scores in all modes!

**Features:**
- ✅ Voice control
- ✅ Beautiful graphics  
- ✅ 4 game modes
- ✅ Offline play
- ✅ No ads
- ✅ Free forever

### 🌟 Pro Tips
1. **Start with Classic mode** to learn the game
2. **Try Labyrinth mode** for a unique challenge
3. **Use keyboard mode** for fastest response
4. **Use voice mode** for hands-free fun
5. **Press P to pause** when you need a break
6. **Watch for gold food** - 50 points!

---

**Made with ❤️ using Java, Swing, and Vosk AI**

*A modern take on a classic game, enhanced with voice control!* 🐍🎤✨