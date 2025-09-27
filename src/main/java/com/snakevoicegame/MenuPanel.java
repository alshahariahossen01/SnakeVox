package com.snakevoicegame;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;

public final class MenuPanel extends JPanel {

    public MenuPanel(ActionListener onStart, ActionListener onSettings) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Snake Voice Game", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 40));
        JButton startBtn = new JButton("Start Game");
        JButton settingsBtn = new JButton("Settings");
        startBtn.addActionListener(onStart);
        settingsBtn.addActionListener(onSettings);
        center.add(startBtn);
        center.add(settingsBtn);
        add(center, BorderLayout.CENTER);

        JLabel hint = new JLabel("Choose a control mode in Settings (Keyboard or Voice)", SwingConstants.CENTER);
        hint.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(hint, BorderLayout.SOUTH);
    }
}


