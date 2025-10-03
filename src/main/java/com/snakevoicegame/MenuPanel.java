package com.snakevoicegame;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;

public final class MenuPanel extends JPanel {

    public MenuPanel(ActionListener onStart, ActionListener onSettings) {
        setLayout(new BorderLayout());
        setBackground(new Color(25, 25, 35));

        // Title panel with styling
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(25, 25, 35));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));
        JLabel title = new JLabel("🐍 Snake Voice Game 🎮", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(new Color(100, 255, 100));
        titlePanel.add(title);
        add(titlePanel, BorderLayout.NORTH);

        // Center panel with buttons
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(25, 25, 35));
        center.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        JButton startBtn = createStyledButton("▶ Start Game");
        JButton settingsBtn = createStyledButton("⚙ Settings");
        
        startBtn.addActionListener(onStart);
        settingsBtn.addActionListener(onSettings);
        
        startBtn.setAlignmentX(CENTER_ALIGNMENT);
        settingsBtn.setAlignmentX(CENTER_ALIGNMENT);
        
        center.add(Box.createVerticalGlue());
        center.add(startBtn);
        center.add(Box.createRigidArea(new Dimension(0, 20)));
        center.add(settingsBtn);
        center.add(Box.createVerticalGlue());
        
        add(center, BorderLayout.CENTER);

        // Bottom hint panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(25, 25, 35));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 30, 20));
        JLabel hint = new JLabel("Choose a control mode in Settings (Keyboard or Voice)", SwingConstants.CENTER);
        hint.setFont(new Font("SansSerif", Font.PLAIN, 14));
        hint.setForeground(new Color(180, 180, 200));
        bottomPanel.add(hint);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(new Color(50, 150, 50));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(80, 200, 80));
                } else {
                    g2.setColor(new Color(60, 180, 60));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(250, 50));
        btn.setMaximumSize(new Dimension(250, 50));
        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        return btn;
    }
}


