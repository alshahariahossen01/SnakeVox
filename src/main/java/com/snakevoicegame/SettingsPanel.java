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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
        setBackground(new Color(25, 25, 35));

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(25, 25, 35));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        JLabel title = new JLabel("⚙ Settings", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(new Color(150, 200, 255));
        titlePanel.add(title);
        add(titlePanel, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(25, 25, 35));
        center.setBorder(BorderFactory.createEmptyBorder(16, 40, 16, 40));

        // Control mode selection
        JLabel controlLabel = new JLabel("Control:");
        controlLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        controlLabel.setForeground(new Color(220, 220, 255));
        
        JRadioButton keyboard = createStyledRadioButton("⌨ Keyboard");
        JRadioButton voice = createStyledRadioButton("🎤 Voice");
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
        modeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        modeLabel.setForeground(new Color(220, 220, 255));
        
        JRadioButton classic = createStyledRadioButton("Classic");
        JRadioButton intermediate = createStyledRadioButton("Intermediate");
        JRadioButton labyrinth = createStyledRadioButton("Labyrinth");
        JRadioButton expert = createStyledRadioButton("Expert");
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
        scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        scoreLabel.setForeground(new Color(255, 215, 100));

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

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        row1.setBackground(new Color(25, 25, 35));
        row1.add(controlLabel);
        row1.add(keyboard);
        row1.add(voice);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        row2.setBackground(new Color(25, 25, 35));
        row2.add(modeLabel);
        row2.add(classic);
        row2.add(intermediate);
        row2.add(labyrinth);
        row2.add(expert);

        JLabel scoresLabel = new JLabel("📊 Scores:");
        scoresLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        scoresLabel.setForeground(new Color(220, 220, 255));
        
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        row3.setBackground(new Color(25, 25, 35));
        row3.add(scoresLabel);
        row3.add(scoreLabel);

        center.add(row1);
        center.add(javax.swing.Box.createRigidArea(new Dimension(0, 15)));
        center.add(row2);
        center.add(javax.swing.Box.createRigidArea(new Dimension(0, 15)));
        center.add(row3);
        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setBackground(new Color(25, 25, 35));
        bottom.setBorder(BorderFactory.createEmptyBorder(20, 20, 30, 20));
        JButton back = createStyledButton("← Back to Menu");
        back.addActionListener(onBack);
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);
    }

    private JRadioButton createStyledRadioButton(String text) {
        JRadioButton btn = new JRadioButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 15));
        btn.setForeground(new Color(200, 200, 220));
        btn.setBackground(new Color(25, 25, 35));
        btn.setFocusPainted(false);
        return btn;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(new Color(100, 120, 180));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(140, 160, 220));
                } else {
                    g2.setColor(new Color(120, 140, 200));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        return btn;
    }

    private String scoresText(GameMode mode) {
        int hi = scoreManager.getHighScore(mode);
        int lo = scoreManager.getLowScore(mode);
        String loText = lo == 0 ? "-" : Integer.toString(lo);
        return "High: " + hi + "   Low: " + loText;
    }
}


