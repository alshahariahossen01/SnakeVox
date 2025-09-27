package com.snakevoicegame;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public final class GamePanel extends JPanel implements ActionListener {

    private static final int TILE_SIZE = 20;
    private static final int GRID_COLS = 30; // width = 600
    private static final int GRID_ROWS = 28; // height = 560
    private static final int PANEL_WIDTH = GRID_COLS * TILE_SIZE;
    private static final int PANEL_HEIGHT = GRID_ROWS * TILE_SIZE;
    private static final int GAME_SPEED_MS = 150; // classic base delay (ms). smaller = faster

    private final int[] snakeX = new int[GRID_COLS * GRID_ROWS];
    private final int[] snakeY = new int[GRID_COLS * GRID_ROWS];
    private int snakeLength;

    private int foodX;
    private int foodY;
    private final Random random = new Random();

    private char direction = 'R'; // U, D, L, R
    private boolean running = false;
    private int score = 0;

    private final Timer timer = new Timer(GAME_SPEED_MS, this);
    private VoiceController voiceController;
    private final ControlMode controlMode;
    private final GameMode gameMode;
    private final ScoreManager scoreManager = new ScoreManager();
    private boolean achievedNewHigh = false;
    private int finalHighScore = 0;
    private int finalLowScore = 0;

    // Special food ("bolt dot") support
    private boolean isBoltFood = false; // false = small dot (10 pts), true = bolt dot (50 pts)
    private int lastBoltAttemptSegment = 0; // floor(score / 50) when we last attempted to spawn bolt
    // bolt spawn chance depends on game mode (see getBoltSpawnChance)

    // Labyrinth mode obstacles
    private boolean[][] obstacleGrid; // [x][y] true means blocked

    public GamePanel() {
        this(ControlMode.KEYBOARD, GameMode.CLASSIC);
    }

    public GamePanel(ControlMode controlMode) {
        this(controlMode, GameMode.CLASSIC);
    }

    public GamePanel(ControlMode controlMode, GameMode gameMode) {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        this.controlMode = controlMode == null ? ControlMode.KEYBOARD : controlMode;
        this.gameMode = gameMode == null ? GameMode.CLASSIC : gameMode;
        if (this.controlMode == ControlMode.VOICE) {
            voiceController = new VoiceController();
        } else {
            voiceController = null;
            addKeyListener(new KeyHandler());
        }
        startGame();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (voiceController != null) {
            voiceController.close();
        }
    }

    private void startGame() {
        score = 0;
        snakeLength = 3;
        int startX = GRID_COLS / 2;
        int startY = GRID_ROWS / 2;
        for (int i = 0; i < snakeLength; i++) {
            snakeX[i] = startX - i;
            snakeY[i] = startY;
        }
        direction = 'R';
        // reset special food / speed state
        isBoltFood = false;
        lastBoltAttemptSegment = 0;
        timer.setDelay(getBaseDelay());

        // obstacles for labyrinth mode
        if (gameMode == GameMode.LABYRINTH) {
            generateLabyrinth(startX, startY);
        } else {
            obstacleGrid = null;
        }

        placeFood();
        running = true;
        timer.start();
    }

    private void placeFood() {
        boolean onSnake;
        do {
            onSnake = false;
            foodX = random.nextInt(GRID_COLS);
            foodY = random.nextInt(GRID_ROWS);
            for (int i = 0; i < snakeLength; i++) {
                if (snakeX[i] == foodX && snakeY[i] == foodY) {
                    onSnake = true;
                    break;
                }
            }
            if (!onSnake && obstacleGrid != null && obstacleGrid[foodX][foodY]) {
                onSnake = true; // retry if on obstacle
            }
        } while (onSnake);

        // Decide food type for the newly placed food
        isBoltFood = shouldSpawnBoltNow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw instruction at top-center
        g2.setColor(Color.WHITE);
        g2.setFont(getFont().deriveFont(Font.BOLD, 16f));
        String instruction = controlMode == ControlMode.VOICE
                ? "Say UP, DOWN, LEFT, or RIGHT to move"
                : "Use ARROW KEYS to move";
        FontMetrics fmInstr = g2.getFontMetrics();
        int instrX = (getWidth() - fmInstr.stringWidth(instruction)) / 2;
        g2.drawString(instruction, instrX, 20);

        // Draw score at top-left
        g2.drawString("Score: " + score, 10, 40);

        // Draw mode at top-right
        String modeText = "Mode: " + gameMode.name();
        int modeX = getWidth() - fmInstr.stringWidth(modeText) - 10;
        g2.drawString(modeText, modeX, 40);

        // Draw food (bolt dot in gold, small dot in red)
        if (isBoltFood) {
            g2.setColor(new Color(255, 215, 0)); // gold
        } else {
            g2.setColor(new Color(220, 60, 60)); // red
        }
        g2.fillRect(foodX * TILE_SIZE, foodY * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // Draw obstacles (labyrinth)
        if (obstacleGrid != null) {
            g2.setColor(new Color(80, 80, 80));
            for (int x = 0; x < GRID_COLS; x++) {
                for (int y = 0; y < GRID_ROWS; y++) {
                    if (obstacleGrid[x][y]) {
                        g2.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }

        // Draw snake
        for (int i = 0; i < snakeLength; i++) {
            if (i == 0) {
                g2.setColor(new Color(120, 200, 120)); // head
            } else {
                g2.setColor(new Color(80, 160, 80)); // body
            }
            g2.fillRect(snakeX[i] * TILE_SIZE, snakeY[i] * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        if (!running) {
            drawGameOver(g2);
        }
        g2.dispose();
    }

    private void drawGameOver(Graphics2D g2) {
        String over = "Game Over!";
        String prompt = controlMode == ControlMode.VOICE ? "Say RESTART to play again" : "Press ENTER to restart";

        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(Color.WHITE);
        g2.setFont(getFont().deriveFont(Font.BOLD, 48f));
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(over)) / 2;
        int y = getHeight() / 2 - fm.getHeight();
        g2.drawString(over, x, y);

        g2.setFont(getFont().deriveFont(Font.PLAIN, 18f));
        FontMetrics fm2 = g2.getFontMetrics();
        int x2 = (getWidth() - fm2.stringWidth(prompt)) / 2;
        int y2 = y + 40;
        g2.drawString(prompt, x2, y2);

        // Score summary and new high indicator
        String summary = "Score: " + score + "   High: " + finalHighScore + "   Low: " + (finalLowScore == 0 ? "-" : Integer.toString(finalLowScore));
        int x3 = (getWidth() - fm2.stringWidth(summary)) / 2;
        g2.drawString(summary, x3, y2 + 28);
        if (achievedNewHigh) {
            String nh = "New HIGH SCORE!";
            g2.setFont(getFont().deriveFont(Font.BOLD, 20f));
            FontMetrics fm3 = g2.getFontMetrics();
            int x4 = (getWidth() - fm3.stringWidth(nh)) / 2;
            g2.setColor(new Color(255, 215, 0));
            g2.drawString(nh, x4, y2 + 54);
            g2.setColor(Color.WHITE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            if (controlMode == ControlMode.VOICE) {
                applyVoiceDirectionIfAny();
            }
            moveSnake();
            checkFood();
            checkCollisions();
        } else {
            if (controlMode == ControlMode.VOICE) {
                applyRestartIfAny();
            }
        }
        repaint();
    }

    private void applyRestartIfAny() {
        if (voiceController == null) {
            return;
        }
        String cmd = voiceController.getCommand();
        if (cmd == null) {
            return;
        }
        if ("restart".equals(cmd)) {
            startGame();
        }
    }

    private void applyVoiceDirectionIfAny() {
        if (voiceController == null) {
            return;
        }
        String cmd = voiceController.getCommand();
        if (cmd == null) {
            return;
        }
        switch (cmd) {
            case "up":
                if (direction != 'D') direction = 'U';
                break;
            case "down":
                if (direction != 'U') direction = 'D';
                break;
            case "left":
                if (direction != 'R') direction = 'L';
                break;
            case "right":
                if (direction != 'L') direction = 'R';
                break;
            default:
                break;
        }
    }

    private void moveSnake() {
        for (int i = snakeLength - 1; i > 0; i--) {
            snakeX[i] = snakeX[i - 1];
            snakeY[i] = snakeY[i - 1];
        }

        switch (direction) {
            case 'U':
                snakeY[0] -= 1;
                break;
            case 'D':
                snakeY[0] += 1;
                break;
            case 'L':
                snakeX[0] -= 1;
                break;
            case 'R':
                snakeX[0] += 1;
                break;
            default:
                break;
        }
    }

    private void checkFood() {
        if (snakeX[0] == foodX && snakeY[0] == foodY) {
            snakeLength++;
            score += isBoltFood ? 50 : 10;
            updateSpeedBasedOnScore();
            placeFood();
        }
    }

    private void checkCollisions() {
        // Wall collision
        if (snakeX[0] < 0 || snakeX[0] >= GRID_COLS || snakeY[0] < 0 || snakeY[0] >= GRID_ROWS) {
            gameOver();
            return;
        }
        // Labyrinth obstacle collision
        if (obstacleGrid != null) {
            int hx = snakeX[0];
            int hy = snakeY[0];
            if (hx >= 0 && hx < GRID_COLS && hy >= 0 && hy < GRID_ROWS && obstacleGrid[hx][hy]) {
                gameOver();
                return;
            }
        }
        // Self collision
        for (int i = 1; i < snakeLength; i++) {
            if (snakeX[0] == snakeX[i] && snakeY[0] == snakeY[i]) {
                gameOver();
                return;
            }
        }
    }

    private void gameOver() {
        running = false;
        timer.stop();
        // submit score and capture hi/low for display
        achievedNewHigh = score > scoreManager.getHighScore(gameMode);
        scoreManager.submitScore(gameMode, score);
        finalHighScore = scoreManager.getHighScore(gameMode);
        finalLowScore = scoreManager.getLowScore(gameMode);
    }

    // Increase speed based on score thresholds (start slow, then speed up)
    private void updateSpeedBasedOnScore() {
        int base = getBaseDelay();
        int newDelay = base;
        if (score >= 500) {
            newDelay = (int) Math.round(base * 0.4);
        } else if (score >= 400) {
            newDelay = (int) Math.round(base * 0.5);
        } else if (score >= 300) {
            newDelay = (int) Math.round(base * 0.6);
        } else if (score >= 200) {
            newDelay = (int) Math.round(base * 0.7);
        } else if (score >= 100) {
            newDelay = (int) Math.round(base * 0.85);
        }
        timer.setDelay(newDelay);
    }

    // Decide whether to spawn a bolt dot now. We try at most once per 50-score segment.
    private boolean shouldSpawnBoltNow() {
        int segment = score / 50; // 0..n
        if (segment >= 1 && segment != lastBoltAttemptSegment) {
            lastBoltAttemptSegment = segment;
            return random.nextDouble() < getBoltSpawnChance();
        }
        return false;
    }

    private int getBaseDelay() {
        switch (gameMode) {
            case INTERMEDIATE:
                return 270; // slower start
            case EXPERT:
                return 240; // still slow at start, but a bit faster
            case LABYRINTH:
                return 300; // slow start due to obstacles
            case CLASSIC:
            default:
                return 300; // very slow start
        }
    }

    private double getBoltSpawnChance() {
        switch (gameMode) {
            case INTERMEDIATE:
                return 0.4;
            case EXPERT:
                return 0.3;
            case LABYRINTH:
                return 0.5;
            case CLASSIC:
            default:
                return 0.5;
        }
    }

    private void generateLabyrinth(int startX, int startY) {
        obstacleGrid = new boolean[GRID_COLS][GRID_ROWS];
        // vertical pillars every 4 columns with periodic gaps
        for (int x = 4; x < GRID_COLS - 4; x += 4) {
            boolean gapToggle = (x / 4) % 2 == 0;
            for (int y = 2; y < GRID_ROWS - 2; y++) {
                boolean isGap = (y % 6 == 0) ^ gapToggle; // staggered gaps
                if (!isGap) {
                    obstacleGrid[x][y] = true;
                }
            }
        }
        // clear a 5x5 safe box around the starting snake position
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                int cx = startX + dx;
                int cy = startY + dy;
                if (cx >= 0 && cx < GRID_COLS && cy >= 0 && cy < GRID_ROWS) {
                    obstacleGrid[cx][cy] = false;
                }
            }
        }
    }

    private final class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (controlMode != ControlMode.KEYBOARD) {
                return;
            }
            int key = e.getKeyCode();
            if (running) {
                switch (key) {
                    case KeyEvent.VK_UP:
                        if (direction != 'D') direction = 'U';
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != 'U') direction = 'D';
                        break;
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R') direction = 'L';
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 'L') direction = 'R';
                        break;
                    default:
                        break;
                }
            } else {
                if (key == KeyEvent.VK_ENTER) {
                    startGame();
                }
            }
        }
    }
}


