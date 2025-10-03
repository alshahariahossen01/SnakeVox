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
    
    private long lastCommandTime = 0;
    private static final long COMMAND_COOLDOWN_MS = 300;

    public VoskVoiceController() {
        this(true);
    }

    public VoskVoiceController(boolean autoStart) {
        try {
            statusMessage = "Loading Vosk model...";
            LibVosk.setLogLevel(LogLevel.WARNINGS);
            
            // Try to find the model in the project directory
            File modelDir = new File("vosk-model-small-en-us-0.15");
            if (!modelDir.exists()) {
                // Try absolute path
                modelDir = new File("D:\\Site\\Voice-Controlled Snake\\SnakeVoiceGame\\vosk-model-small-en-us-0.15");
            }
            
            if (!modelDir.exists()) {
                throw new Exception("Model not found. Please ensure vosk-model-small-en-us-0.15 is in the project directory.");
            }
            
            System.out.println("VoskVoiceController: Loading model from: " + modelDir.getAbsolutePath());
            model = new Model(modelDir.getPath());
            recognizer = new Recognizer(model, 16000);
            recognizer.setWords(true);
            
            enabled = true;
            statusMessage = "Ready";
            System.out.println("VoskVoiceController: Successfully initialized! Say UP, DOWN, LEFT, RIGHT, or RESTART.");
            
            if (autoStart) {
                start();
            }
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
        TargetDataLine microphone = null;
        try {
            // Get microphone
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            
            if (!AudioSystem.isLineSupported(info)) {
                statusMessage = "Microphone not supported";
                System.err.println("VoskVoiceController: Microphone not supported");
                return;
            }
            
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();
            
            listening.set(true);
            statusMessage = "Listening...";
            System.out.println("VoskVoiceController: Now listening for commands...");
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            
            while (running.get()) {
                bytesRead = microphone.read(buffer, 0, buffer.length);
                
                if (bytesRead > 0) {
                    if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                        String result = recognizer.getResult();
                        processResult(result);
                    }
                }
            }
            
        } catch (Exception e) {
            statusMessage = "Error: " + e.getMessage();
            System.err.println("VoskVoiceController error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (microphone != null) {
                microphone.stop();
                microphone.close();
            }
            listening.set(false);
        }
    }

    private void processResult(String jsonResult) {
        try {
            JsonObject obj = JsonParser.parseString(jsonResult).getAsJsonObject();
            if (obj.has("text")) {
                String text = obj.get("text").getAsString().toLowerCase().trim();
                
                if (!text.isEmpty()) {
                    System.out.println("VoskVoiceController: Heard: '" + text + "'");
                    
                    // Extract command from text
                    String command = extractCommand(text);
                    if (command != null) {
                        long now = System.currentTimeMillis();
                        if (now - lastCommandTime >= COMMAND_COOLDOWN_MS) {
                            lastCommand.set(command);
                            lastCommandTime = now;
                            System.out.println("VoskVoiceController: Command accepted: " + command);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("VoskVoiceController: Error parsing result: " + e.getMessage());
        }
    }

    private String extractCommand(String text) {
        // Check for direction commands (most specific matches first)
        if (text.contains("up") || text.contains("app")) return "up";
        if (text.contains("down")) return "down";
        if (text.contains("left")) return "left";
        if (text.contains("right") || text.contains("write")) return "right";
        if (text.contains("restart") || text.contains("start") || text.contains("begin")) return "restart";
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
