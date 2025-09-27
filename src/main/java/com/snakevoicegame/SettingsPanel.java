package com.snakevoicegame;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;

public final class SettingsPanel extends JPanel {

    public interface ModeChangeListener {
        void onModeChanged(ControlMode mode);
    }

    public interface GameModeChangeListener {
        void onGameModeChanged(GameMode mode);
    }

    private final ScoreManager scoreManager = new ScoreManager();

    public SettingsPanel(ControlMode currentMode,
                         ModeChangeListener controlListener,
                         GameMode currentGameMode,
                         GameModeChangeListener gameModeListener,
                         ActionListener onBack) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Settings", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // Control mode selection
        JLabel controlLabel = new JLabel("Control:");
        JRadioButton keyboard = new JRadioButton("Keyboard");
        JRadioButton voice = new JRadioButton("Voice");
        ButtonGroup controlGroup = new ButtonGroup();
        controlGroup.add(keyboard);
        controlGroup.add(voice);
        if (currentMode == ControlMode.VOICE) {
            voice.setSelected(true);
        } else {
            keyboard.setSelected(true);
        }
        keyboard.addActionListener(e -> controlListener.onModeChanged(ControlMode.KEYBOARD));
        voice.addActionListener(e -> controlListener.onModeChanged(ControlMode.VOICE));

        // Game mode selection
        JLabel modeLabel = new JLabel("Game Mode:");
        JRadioButton classic = new JRadioButton("Classic");
        JRadioButton intermediate = new JRadioButton("Intermediate");
        JRadioButton labyrinth = new JRadioButton("Labyrinth");
        JRadioButton expert = new JRadioButton("Expert");
        ButtonGroup gameGroup = new ButtonGroup();
        gameGroup.add(classic);
        gameGroup.add(intermediate);
        gameGroup.add(labyrinth);
        gameGroup.add(expert);
        switch (currentGameMode) {
            case INTERMEDIATE:
                intermediate.setSelected(true);
                break;
            case LABYRINTH:
                labyrinth.setSelected(true);
                break;
            case EXPERT:
                expert.setSelected(true);
                break;
            case CLASSIC:
            default:
                classic.setSelected(true);
                break;
        }

        JLabel scoreLabel = new JLabel(scoresText(currentGameMode));
        scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        ActionListener updateGameMode = e -> {
            GameMode chosen = currentGameMode;
            if (classic.isSelected()) chosen = GameMode.CLASSIC;
            else if (intermediate.isSelected()) chosen = GameMode.INTERMEDIATE;
            else if (labyrinth.isSelected()) chosen = GameMode.LABYRINTH;
            else if (expert.isSelected()) chosen = GameMode.EXPERT;
            gameModeListener.onGameModeChanged(chosen);
            scoreLabel.setText(scoresText(chosen));
        };
        classic.addActionListener(updateGameMode);
        intermediate.addActionListener(updateGameMode);
        labyrinth.addActionListener(updateGameMode);
        expert.addActionListener(updateGameMode);

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row1.add(controlLabel);
        row1.add(keyboard);
        row1.add(voice);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row2.add(modeLabel);
        row2.add(classic);
        row2.add(intermediate);
        row2.add(labyrinth);
        row2.add(expert);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row3.add(new JLabel("Scores:"));
        row3.add(scoreLabel);

        center.add(row1);
        center.add(row2);
        center.add(row3);
        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton back = new JButton("Back");
        back.addActionListener(onBack);
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);
    }

    private String scoresText(GameMode mode) {
        int hi = scoreManager.getHighScore(mode);
        int lo = scoreManager.getLowScore(mode);
        String loText = lo == 0 ? "-" : Integer.toString(lo);
        return "High: " + hi + "   Low: " + loText;
    }
}


