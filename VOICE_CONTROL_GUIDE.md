# Voice Control Troubleshooting Guide

## Why Voice Control Might Not Work

Voice control in this game uses **CMU Sphinx4**, an offline speech recognition library. Here are common reasons it might not work and how to fix them:

### 1. Missing Dependencies
**Problem**: Sphinx4 libraries not installed
**Solution**:
```bash
cd SnakeVoiceGame
mvn clean install
```

This downloads and installs the required Sphinx4 libraries.

### 2. Microphone Not Working
**Problem**: No microphone detected or permissions denied
**Solution**:
- **Windows**: Go to Settings → Privacy → Microphone → Allow desktop apps to access microphone
- **Test microphone**: Open Sound Recorder or another app to verify microphone works
- **Check default device**: Ensure your microphone is set as the default recording device

### 3. Grammar File Missing
**Problem**: The `commands.gram` file is not in the right location
**Check**:
```
SnakeVoiceGame/src/main/resources/grammars/commands.gram
```

The file should contain:
```
#JSGF V1.0;
grammar commands;

public <direction> = up | down | left | right | restart;
```

### 4. Java Permissions
**Problem**: Java doesn't have permission to access the microphone
**Solution**:
- Run as administrator (Windows)
- Check Java security settings
- On macOS: System Preferences → Security & Privacy → Microphone

## How to Test Voice Control

### Step 1: Check Console Output
When you start the game with voice control enabled, look for these messages in the console:

**✅ GOOD (Voice Working)**:
```
VoiceController: Successfully initialized. Say UP, DOWN, LEFT, RIGHT, or RESTART.
VoiceController: Now listening for commands...
VoiceController: Heard: 'up' -> 'up'
```

**❌ BAD (Voice Not Working)**:
```
VoiceController: Sphinx not available (ClassNotFoundException) - voice disabled.
```
→ This means Sphinx libraries are missing. Run `mvn clean install`.

```
VoiceController: error in recognition loop: ...
```
→ This means there's a problem with the microphone or audio setup.

### Step 2: Watch the Status Indicator
When playing the game in voice mode:
- **🟢 Green dot** in top-right = Voice is listening ✅
- **⚪ Gray dot** = Voice is not active ❌
- Status message shows what the voice system is doing

### Step 3: Test Commands
Speak these commands clearly:
- **"UP"** - Move up
- **"DOWN"** - Move down
- **"LEFT"** - Move left  
- **"RIGHT"** - Move right
- **"RESTART"** - Restart after game over

**Tips for Better Recognition**:
- Speak clearly and at normal volume
- Avoid background noise
- Wait a moment after starting before speaking
- Each command needs to be distinct (don't say "up up up" rapidly)
- The system has a 300ms cooldown between commands

### Step 4: Check What Sphinx Hears
The console will show:
```
VoiceController: Heard: 'your words here' -> 'normalized command'
```

If you see:
- Nothing printed → Sphinx isn't detecting any sound (check microphone)
- Wrong words → Sphinx is hearing you but misrecognizing (speak more clearly)
- Correct words → Everything is working! 🎉

## Common Issues and Fixes

### Issue: "ClassNotFoundException: edu.cmu.sphinx..."
**Fix**: Run `mvn clean install` to download dependencies

### Issue: Gray indicator, never turns green
**Fix**: 
1. Check console for errors
2. Verify microphone is plugged in
3. Test microphone in another app
4. Check Java has microphone permissions

### Issue: Green indicator but commands don't work
**Fix**:
1. Check console to see what Sphinx is hearing
2. Speak more clearly
3. Reduce background noise
4. Move microphone closer

### Issue: Commands work but very slow
**Fix**:
- This is normal for Sphinx4 (it's not as fast as modern cloud-based recognition)
- The code already optimized response time (reduced from 2 matches to 1, cooldown from 500ms to 300ms)
- For faster gameplay, use keyboard mode

### Issue: Build fails with Maven
**Fix**:
1. Install Maven: https://maven.apache.org/download.cgi
2. Add Maven to your PATH
3. Verify Java 17+ is installed: `java -version`
4. Run `mvn clean install`

## Alternative: Use Keyboard Mode

If voice control continues to have issues, you can always use keyboard mode:
1. Go to Settings
2. Select "⌨ Keyboard"
3. Use arrow keys to play

You can still pause with **P** in both modes!

## System Requirements for Voice Control

### Minimum:
- Java 17 or higher
- Maven 3.6+
- Working microphone
- 2GB RAM (Sphinx4 uses memory for models)
- Quiet environment

### Recommended:
- Java 17+
- 4GB RAM
- USB or built-in microphone (not Bluetooth - may have lag)
- Headset microphone for best recognition
- Quiet room

## Advanced Debugging

### Enable More Logging
The VoiceController already logs extensively. To see all output, run from terminal:

**Windows**:
```bash
cd SnakeVoiceGame
mvn exec:java > log.txt 2>&1
```

**Linux/Mac**:
```bash
cd SnakeVoiceGame
mvn exec:java 2>&1 | tee log.txt
```

Then check `log.txt` for detailed error messages.

### Test Sphinx Separately
Create a simple test:
```java
public class MicTest {
    public static void main(String[] args) throws Exception {
        edu.cmu.sphinx.api.Configuration config = new edu.cmu.sphinx.api.Configuration();
        config.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        config.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        
        edu.cmu.sphinx.api.LiveSpeechRecognizer recognizer = 
            new edu.cmu.sphinx.api.LiveSpeechRecognizer(config);
        
        recognizer.startRecognition(true);
        System.out.println("Say something...");
        
        edu.cmu.sphinx.api.SpeechResult result = recognizer.getResult();
        System.out.println("You said: " + result.getHypothesis());
        
        recognizer.stopRecognition();
    }
}
```

If this doesn't work, the issue is with Sphinx/microphone setup, not the game code.

## Still Having Issues?

If you've tried everything and voice control still doesn't work:

1. **Check the console** - all errors are logged there
2. **Use keyboard mode** - the game works perfectly with keyboard controls
3. **Verify dependencies**: Make sure `pom.xml` has Sphinx dependencies
4. **Check Java version**: Must be Java 17+
5. **File an issue**: Report the console output for debugging help

---

**Remember**: Voice control is an experimental feature using offline speech recognition. For the best experience and fastest response, keyboard mode is recommended! 🎮
