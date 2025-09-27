package com.snakevoicegame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public final class ScoreManager {

    private static final String FILE_NAME = ".snakevoicegame-scores.properties";
    private static final String KEY_PREFIX_HIGH = "high.";
    private static final String KEY_PREFIX_LOW = "low.";

    private final File file;
    private final Properties props = new Properties();

    public ScoreManager() {
        String home = System.getProperty("user.home", ".");
        this.file = new File(home, FILE_NAME);
        load();
    }

    private void load() {
        if (!file.exists()) {
            return;
        }
        try (FileInputStream in = new FileInputStream(file)) {
            props.load(in);
        } catch (IOException ignored) {
        }
    }

    private void save() {
        try (FileOutputStream out = new FileOutputStream(file)) {
            props.store(out, "Snake Voice Game scores");
        } catch (IOException ignored) {
        }
    }

    public int getHighScore(GameMode mode) {
        Objects.requireNonNull(mode, "mode");
        String v = props.getProperty(KEY_PREFIX_HIGH + mode.name(), "0");
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getLowScore(GameMode mode) {
        Objects.requireNonNull(mode, "mode");
        String v = props.getProperty(KEY_PREFIX_LOW + mode.name(), "");
        if (v == null || v.isEmpty()) {
            return 0; // not set yet
        }
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public boolean submitScore(GameMode mode, int score) {
        Objects.requireNonNull(mode, "mode");
        boolean updated = false;

        int currentHigh = getHighScore(mode);
        if (score > currentHigh) {
            props.setProperty(KEY_PREFIX_HIGH + mode.name(), Integer.toString(score));
            updated = true;
        }

        int currentLow = getLowScore(mode);
        if (currentLow == 0 || score < currentLow) {
            props.setProperty(KEY_PREFIX_LOW + mode.name(), Integer.toString(score));
            updated = true;
        }

        if (updated) {
            save();
        }
        return updated;
    }
}


