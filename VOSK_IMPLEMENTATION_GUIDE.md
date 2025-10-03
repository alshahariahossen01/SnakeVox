# How to Add Vosk Voice Control

## ✅ **Vosk - Modern Offline Speech Recognition**

Vosk is actively maintained and works great for simple commands!

### Step 1: Add Vosk to pom.xml

```xml
<dependencies>
    <!-- Vosk Speech Recognition -->
    <dependency>
        <groupId>com.alphacephei</groupId>
        <artifactId>vosk</artifactId>
        <version>0.3.45</version>
    </dependency>
    
    <!-- JSON parsing for Vosk results -->
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>
</dependencies>
```

### Step 2: Download Vosk Model

1. Go to: https://alphacephei.com/vosk/models
2. Download: **vosk-model-small-en-us-0.15.zip** (~40MB) - Small English model
3. Extract to: `SnakeVoiceGame/models/vosk-model-small-en-us-0.15/`

### Step 3: Create VoskVoiceController.java

Create this new file: `src/main/java/com/snakevoicegame/VoskVoiceController.java`

```java
package com.snakevoicegame;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class VoskVoiceController implements AutoCloseable {

    private final AtomicReference<String> lastCommand = new AtomicReference<>(null);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean listening = new AtomicBoolean(false);
    private Thread workerThread;
    private String statusMessage = "Initializing...";
    
    private Model model;
    private Recognizer recognizer;
    private boolean enabled = false;

    public VoskVoiceController() {
        try {
            statusMessage = "Loading Vosk model...";
            LibVosk.setLogLevel(LogLevel.WARNINGS);
            
            // Load model from models directory
            File modelDir = new File("models/vosk-model-small-en-us-0.15");
            if (!modelDir.exists()) {
                throw new Exception("Model not found at: " + modelDir.getAbsolutePath());
            }
            
            model = new Model(modelDir.getPath());
            recognizer = new Recognizer(model, 16000);
            
            // Configure for simple commands
            recognizer.setWords(true);
            
            enabled = true;
            statusMessage = "Ready";
            System.out.println("VoskVoiceController: Successfully initialized!");
            start();
        } catch (Exception e) {
            enabled = false;
            statusMessage = "Voice unavailable: " + e.getMessage();
            System.err.println("VoskVoiceController: Failed to initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void start() {
        if (!enabled || !running.compareAndSet(false, true)) {
            return;
        }
        workerThread = new Thread(this::runLoop, "VoskVoiceController");
        workerThread.setDaemon(true);
        workerThread.start();
    }

    private void runLoop() {
        try {
            // Get microphone
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            
            if (!AudioSystem.isLineSupported(info)) {
                statusMessage = "Microphone not supported";
                System.err.println("Microphone not supported");
                return;
            }
            
            TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();
            
            listening.set(true);
            statusMessage = "Listening...";
            System.out.println("VoskVoiceController: Listening for commands...");
            
            byte[] buffer = new byte[4096];
            
            while (running.get()) {
                int bytesRead = microphone.read(buffer, 0, buffer.length);
                
                if (bytesRead > 0) {
                    if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                        String result = recognizer.getResult();
                        processResult(result);
                    }
                }
            }
            
            microphone.stop();
            microphone.close();
            listening.set(false);
            
        } catch (Exception e) {
            listening.set(false);
            statusMessage = "Error: " + e.getMessage();
            System.err.println("VoskVoiceController error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processResult(String jsonResult) {
        try {
            JsonObject obj = JsonParser.parseString(jsonResult).getAsJsonObject();
            if (obj.has("text")) {
                String text = obj.get("text").getAsString().toLowerCase().trim();
                
                System.out.println("Heard: " + text);
                
                // Extract command from text
                String command = extractCommand(text);
                if (command != null) {
                    lastCommand.set(command);
                    System.out.println("Command: " + command);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing result: " + e.getMessage());
        }
    }

    private String extractCommand(String text) {
        // Check for direction commands
        if (text.contains("up")) return "up";
        if (text.contains("down")) return "down";
        if (text.contains("left")) return "left";
        if (text.contains("right")) return "right";
        if (text.contains("restart") || text.contains("start")) return "restart";
        return null;
    }

    public String getCommand() {
        return lastCommand.getAndSet(null); // Get and clear
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isListening() {
        return listening.get();
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void stop() {
        if (running.compareAndSet(true, false)) {
            try {
                if (workerThread != null) {
                    workerThread.join(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void close() {
        stop();
        if (recognizer != null) {
            recognizer.close();
        }
        if (model != null) {
            model.close();
        }
    }
}
```

### Step 4: Update GamePanel.java

Replace VoiceController with VoskVoiceController:

```java
// In GamePanel.java, line 76
if (this.controlMode == ControlMode.VOICE) {
    voiceController = new VoskVoiceController(); // Changed from VoiceController
} else {
    voiceController = null;
}
```

And update the field declaration:

```java
// Line 40-41
private VoskVoiceController voiceController; // Changed type
```

### Step 5: Update pom.xml

```xml
<dependencies>
    <!-- Vosk Speech Recognition -->
    <dependency>
        <groupId>com.alphacephei</groupId>
        <artifactId>vosk</artifactId>
        <version>0.3.45</version>
    </dependency>
    
    <!-- Gson for JSON parsing -->
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>
</dependencies>
```

### Step 6: Build and Run

```bash
mvn clean package
java -jar target/SnakeVoiceGame-0.1.0-SNAPSHOT.jar
```

---

## 📦 **Pros & Cons**

### ✅ Pros:
- Works offline (no internet needed)
- Free and open source
- Actively maintained
- Good accuracy for simple commands
- Fast response time

### ❌ Cons:
- Requires ~40MB model download
- Needs model files in project directory
- More complex setup than Sphinx4 was

---

## 🎯 **Testing Vosk**

After setup, when you run the game:
1. Select Voice mode in settings
2. Look for green indicator (listening)
3. Say: "up", "down", "left", "right", "restart"
4. Check console for "Heard: ..." messages

---

## 🔧 **Troubleshooting**

### "Model not found"
- Make sure you extracted the model to `models/vosk-model-small-en-us-0.15/`
- Path should be relative to where you run the JAR

### "Microphone not supported"
- Check Windows sound settings
- Ensure microphone is connected
- Try a different microphone

### Commands not recognized
- Speak clearly
- Reduce background noise
- Say command words alone: "UP" not "go up"

---

## 📝 **Model Download Links**

Small models (faster, less accurate):
- English US Small: https://alphacephei.com/vosk/models/vosk-model-small-en-us-0.15.zip (40MB)

Large models (slower, more accurate):
- English US: https://alphacephei.com/vosk/models/vosk-model-en-us-0.22.zip (1.8GB)

Choose small for this game - it's perfect for simple commands!

---

**This is the best modern alternative to Sphinx4!** 🎤✨
