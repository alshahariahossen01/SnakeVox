## SnakeVoiceGame

Voice-controlled Snake built with Java 17 and Swing. Play using either the keyboard or your voice (via CMU Sphinx4, fully offline).

### Key Features
- **Two control modes**: `Keyboard` or `Voice`.
- **Four game modes**: `Classic`, `Intermediate`, `Labyrinth`, `Expert` (different speeds and obstacle layouts).
- **Dynamic difficulty**: speed ramps up as your score increases.
- **Special food**: gold "bolt" dots worth 50 points appear occasionally based on mode.
- **Persistent scores**: high/low scores per game mode are saved in your user home.

## Requirements
- **Java 17 (JDK 17)**
- **Maven 3.8+**
- **Microphone** (for Voice mode)

> On Windows/macOS/Linux, ensure microphone permissions are granted to Java. Close any app exclusively locking the mic.

## Quick Start
```bash
# From the SnakeVoiceGame/ directory
mvn -q -DskipTests package
java -jar target/SnakeVoiceGame-0.1.0-SNAPSHOT.jar
```

Alternatively, run directly with Maven during development:
```bash
mvn -q exec:java
```

## How to Play
### Controls
- **Keyboard mode**: Arrow keys to move. When game over, press `Enter` to restart.
- **Voice mode**: Say `UP`, `DOWN`, `LEFT`, `RIGHT` to move; say `RESTART` on game over.

### Game Modes
- **Classic**: Very slow start; balanced bolt-dot spawn.
- **Intermediate**: Slightly different speed ramp; moderate bolt-dot chance.
- **Labyrinth**: Adds vertical pillar obstacles with staggered gaps; slow start, higher bolt-dot chance.
- **Expert**: Fastest ramp among modes; lower bolt-dot chance.

### Scoring & Speed
- Red dot = +10 points; Gold bolt dot = +50 points.
- Game speed increases at score thresholds (100/200/300/400/500+).

## Voice Recognition (Sphinx4)
This project uses CMU Sphinx4 (snapshots) and ships a shaded JAR so models are available at runtime.

- Models: `en-us` acoustic model and CMU dict are loaded from resources.
- Grammar: JSGF grammar located at `src/main/resources/grammars/commands.gram`.
- Recognized commands: `up`, `down`, `left`, `right`, `restart`.

If Sphinx4 is unavailable or fails to load, the console will log:
```
VoiceController: Sphinx not available (...) - voice disabled.
```
In that case, select `Keyboard` mode in Settings.

### Microphone Tips
- Select your default input device in the OS settings.
- Reduce background noise; use a headset mic if possible.
- Speak clearly with short, distinct commands.

## Build
```bash
mvn -q -DskipTests package
```
Artifacts:
- Shaded JAR: `target/SnakeVoiceGame-0.1.0-SNAPSHOT.jar`

## Run
### Using Maven
```bash
mvn -q exec:java
```
### Using the shaded JAR
```bash
java -jar target/SnakeVoiceGame-0.1.0-SNAPSHOT.jar
```

## Project Structure
```
SnakeVoiceGame/
  pom.xml
  src/
    main/
      java/com/snakevoicegame/
        SnakeVoiceGame.java        # Application entry point, screen navigation
        GamePanel.java             # Game loop, rendering, input handling
        VoiceController.java       # Sphinx4 integration and noise filtering
        MenuPanel.java             # Main menu UI
        SettingsPanel.java         # Settings UI (control & game modes, scores)
        ScoreManager.java          # Persistent high/low scores per mode
        ControlMode.java           # KEYBOARD or VOICE
        GameMode.java              # CLASSIC, INTERMEDIATE, LABYRINTH, EXPERT
      resources/grammars/
        commands.gram              # JSGF grammar for voice commands
```

## Settings and Persistence
- Change control mode and game mode from the `Settings` screen.
- Scores are stored in your home directory:
  - Windows: `C:\\Users\\<you>\\.snakevoicegame-scores.properties`
  - macOS/Linux: `/Users/<you>/.snakevoicegame-scores.properties` or `/home/<you>/.snakevoicegame-scores.properties`

## Troubleshooting
- **No voice control / voice disabled log**: Ensure you ran the shaded JAR or used Maven so dependencies and models are on the classpath.
- **Mic not heard**: Check OS mic permissions for Java, confirm default input device, reduce noise.
- **Laggy recognition**: Try speaking commands one at a time. Close other heavy applications.

## Extending Voice Commands
To add a new command (example: `pause`):
1. Update grammar `src/main/resources/grammars/commands.gram` to include the new token.
2. Update validation in `VoiceController.isValidCommand(...)` to accept `"pause"`.
3. Handle the command in `GamePanel` (e.g., pause/resume logic).
4. Rebuild the shaded JAR.

Noise filtering parameters you can tweak in `VoiceController`:
- `REQUIRED_CONSECUTIVE_MATCHES` (default 2): require repeated recognition before accepting.
- `ACCEPT_COOLDOWN_MS` (default 500 ms): minimum time between accepted commands.

## Developing
- Run from an IDE by launching `com.snakevoicegame.SnakeVoiceGame`.
- Maven useful commands:
```bash
mvn -q clean package
mvn -q exec:java
```

## License
No license specified. Add one if you intend to redistribute.
