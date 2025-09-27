package com.snakevoicegame;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;

public final class SnakeVoiceGame {

    private static final String CARD_MENU = "menu";
    private static final String CARD_SETTINGS = "settings";
    private static final String CARD_GAME = "game";

    private JFrame frame;
    private java.awt.Container cards;
    private ControlMode controlMode = ControlMode.KEYBOARD;
    private GameMode gameMode = GameMode.CLASSIC;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SnakeVoiceGame().createAndShowGui());
    }

    private void createAndShowGui() {
        frame = new JFrame("Snake Voice Game");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        cards = new JPanel(new CardLayout());

        MenuPanel menu = new MenuPanel(
                e -> showGame(),
                e -> showSettings()
        );

        SettingsPanel settings = new SettingsPanel(
                controlMode,
                mode -> controlMode = mode,
                gameMode,
                gm -> gameMode = gm,
                e -> showMenu()
        );

        cards.add(menu, CARD_MENU);
        cards.add(settings, CARD_SETTINGS);
        cards.add(new GamePanel(controlMode, gameMode), CARD_GAME); // placeholder; replaced on start

        frame.add(cards, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        showMenu();
    }

    private void showMenu() {
        switchCard(CARD_MENU);
    }

    private void showSettings() {
        switchCard(CARD_SETTINGS);
    }

    private void showGame() {
        // Recreate game panel with the chosen control and game mode
        cards.remove(2);
        cards.add(new GamePanel(controlMode, gameMode), CARD_GAME);
        switchCard(CARD_GAME);
        frame.pack();
    }

    private void switchCard(String name) {
        CardLayout cl = (CardLayout) cards.getLayout();
        cl.show(cards, name);
    }
}


