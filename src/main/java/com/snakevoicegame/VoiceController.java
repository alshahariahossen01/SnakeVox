package com.snakevoicegame;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class VoiceController implements AutoCloseable {

    private final AtomicReference<String> lastCommand = new AtomicReference<>(null);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread workerThread;

    // Noise filtering parameters
    private static final int REQUIRED_CONSECUTIVE_MATCHES = 2; // require same command twice
    private static final long ACCEPT_COOLDOWN_MS = 500; // minimal time between accepted commands
    private String candidateCommand;
    private int consecutiveMatches;
    private long lastAcceptedAtMillis;

    // Reflection-based fields to avoid compile-time dependency when building without Maven
    private boolean enabled = false;
    private Class<?> configurationClass;
    private Class<?> recognizerClass;
    private Object recognizer;

    public VoiceController() {
        this(true);
    }

    public VoiceController(boolean autoStart) {
        try {
            configurationClass = Class.forName("edu.cmu.sphinx.api.Configuration");
            recognizerClass = Class.forName("edu.cmu.sphinx.api.LiveSpeechRecognizer");

            Object configuration = configurationClass.getDeclaredConstructor().newInstance();
            configurationClass.getMethod("setAcousticModelPath", String.class)
                    .invoke(configuration, "resource:/edu/cmu/sphinx/models/en-us/en-us");
            configurationClass.getMethod("setDictionaryPath", String.class)
                    .invoke(configuration, "resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
            configurationClass.getMethod("setGrammarPath", String.class)
                    .invoke(configuration, "resource:/grammars");
            configurationClass.getMethod("setGrammarName", String.class)
                    .invoke(configuration, "commands");
            configurationClass.getMethod("setUseGrammar", boolean.class)
                    .invoke(configuration, true);

            Constructor<?> ctor = recognizerClass.getConstructor(configurationClass);
            recognizer = ctor.newInstance(configuration);
            enabled = true;
            if (autoStart) {
                start();
            }
        } catch (Throwable t) {
            enabled = false; // Sphinx not available; run in disabled mode
            System.out.println("VoiceController: Sphinx not available (" + t.getClass().getSimpleName() + ") - voice disabled.");
        }
    }

    public void start() {
        if (!enabled) {
            return;
        }
        if (running.compareAndSet(false, true)) {
            workerThread = new Thread(this::runLoop, "VoiceController-Recognizer");
            workerThread.setDaemon(true);
            workerThread.start();
        }
    }

    private void runLoop() {
        try {
            Method startRecognition = recognizerClass.getMethod("startRecognition", boolean.class);
            Method stopRecognition = recognizerClass.getMethod("stopRecognition");
            Method getResult = recognizerClass.getMethod("getResult");
            startRecognition.invoke(recognizer, true);
            try {
                while (running.get()) {
                    Object result = getResult.invoke(recognizer);
                    if (result == null) {
                        continue;
                    }
                    Method getHypothesis = result.getClass().getMethod("getHypothesis");
                    Object hypothesisObj = getHypothesis.invoke(result);
                    if (hypothesisObj instanceof String) {
                        String hypothesis = (String) hypothesisObj;
                        String cmd = normalize(hypothesis);
                        applyNoiseFilteredUpdate(cmd);
                    }
                }
            } finally {
                stopRecognition.invoke(recognizer);
            }
        } catch (Throwable t) {
            System.out.println("VoiceController: error in recognition loop: " + t.getMessage());
        }
    }

    private void applyNoiseFilteredUpdate(String cmd) {
        if (!isValidCommand(cmd)) {
            candidateCommand = null;
            consecutiveMatches = 0;
            return;
        }
        // Debounce multi-word or empty results just in case
        if (cmd.isEmpty() || cmd.contains(" ")) {
            return;
        }

        if (cmd.equals(candidateCommand)) {
            consecutiveMatches++;
        } else {
            candidateCommand = cmd;
            consecutiveMatches = 1;
        }

        long now = System.currentTimeMillis();
        if (consecutiveMatches >= REQUIRED_CONSECUTIVE_MATCHES && (now - lastAcceptedAtMillis) >= ACCEPT_COOLDOWN_MS) {
            String current = lastCommand.get();
            if (!cmd.equals(current)) {
                lastCommand.set(cmd);
            }
            lastAcceptedAtMillis = now;
        }
    }

    private static String normalize(String text) {
        return text == null ? null : text.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean isValidCommand(String cmd) {
        return "up".equals(cmd) || "down".equals(cmd) || "left".equals(cmd) || "right".equals(cmd) || "restart".equals(cmd);
    }

    public String getCommand() {
        return lastCommand.get();
    }

    public void stop() {
        if (!enabled) {
            return;
        }
        if (running.compareAndSet(true, false)) {
            try {
                if (workerThread != null) {
                    workerThread.join(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void close() {
        stop();
    }
}


