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
    private boolean paused = false;
    private int score = 0;

    private final Timer timer = new Timer(GAME_SPEED_MS, this);
    private VoskVoiceController voiceController;
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
        
        // Always add key listener for pause functionality
        addKeyListener(new KeyHandler());
        
        if (this.controlMode == ControlMode.VOICE) {
            voiceController = new VoskVoiceController();
        } else {
            voiceController = null;
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
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw header background with gradient
        java.awt.GradientPaint headerGradient = new java.awt.GradientPaint(
                0, 0, new Color(30, 30, 40),
                0, 50, new Color(20, 20, 30)
        );
        g2.setPaint(headerGradient);
        g2.fillRect(0, 0, getWidth(), 55);

        // Draw instruction at top-center
        g2.setColor(new Color(220, 220, 255));
        g2.setFont(getFont().deriveFont(Font.BOLD, 14f));
        String instruction = controlMode == ControlMode.VOICE
                ? "Say UP, DOWN, LEFT, or RIGHT to move (P to pause)"
                : "Use ARROW KEYS to move (P to pause)";
        FontMetrics fmInstr = g2.getFontMetrics();
        int instrX = (getWidth() - fmInstr.stringWidth(instruction)) / 2;
        g2.drawString(instruction, instrX, 18);

        // Draw score at top-left with better styling
        g2.setFont(getFont().deriveFont(Font.BOLD, 16f));
        g2.setColor(new Color(255, 215, 0)); // Gold
        g2.drawString("Score: " + score, 10, 40);

        // Draw mode at top-right
        g2.setColor(new Color(150, 200, 255));
        String modeText = "Mode: " + gameMode.name();
        int modeX = getWidth() - fmInstr.stringWidth(modeText) - 10;
        g2.drawString(modeText, modeX, 40);

        // Draw voice status indicator if using voice control
        if (controlMode == ControlMode.VOICE && voiceController != null) {
            drawVoiceStatus(g2);
        }

        // Draw food with 3D effect
        int fx = foodX * TILE_SIZE;
        int fy = foodY * TILE_SIZE;
        if (isBoltFood) {
            // Gold bolt food with gradient
            java.awt.GradientPaint foodGradient = new java.awt.GradientPaint(
                    fx, fy, new Color(255, 230, 100),
                    fx + TILE_SIZE, fy + TILE_SIZE, new Color(255, 180, 0)
            );
            g2.setPaint(foodGradient);
            g2.fillOval(fx + 2, fy + 2, TILE_SIZE - 4, TILE_SIZE - 4);
            g2.setColor(new Color(255, 215, 0));
            g2.setStroke(new java.awt.BasicStroke(2));
            g2.drawOval(fx + 2, fy + 2, TILE_SIZE - 4, TILE_SIZE - 4);
        } else {
            // Regular food with gradient
            java.awt.GradientPaint foodGradient = new java.awt.GradientPaint(
                    fx, fy, new Color(255, 100, 100),
                    fx + TILE_SIZE, fy + TILE_SIZE, new Color(200, 40, 40)
            );
            g2.setPaint(foodGradient);
            g2.fillOval(fx + 3, fy + 3, TILE_SIZE - 6, TILE_SIZE - 6);
        }

        // Draw obstacles (labyrinth) with 3D effect
        if (obstacleGrid != null) {
            for (int x = 0; x < GRID_COLS; x++) {
                for (int y = 0; y < GRID_ROWS; y++) {
                    if (obstacleGrid[x][y]) {
                        int ox = x * TILE_SIZE;
                        int oy = y * TILE_SIZE;
                        java.awt.GradientPaint obstacleGradient = new java.awt.GradientPaint(
                                ox, oy, new Color(100, 100, 110),
                                ox + TILE_SIZE, oy + TILE_SIZE, new Color(60, 60, 70)
                        );
                        g2.setPaint(obstacleGradient);
                        g2.fillRect(ox, oy, TILE_SIZE, TILE_SIZE);
                        g2.setColor(new Color(40, 40, 50));
                        g2.drawRect(ox, oy, TILE_SIZE - 1, TILE_SIZE - 1);
                    }
                }
            }
        }

        // Draw snake with gradient and rounded edges
        for (int i = 0; i < snakeLength; i++) {
            int sx = snakeX[i] * TILE_SIZE;
            int sy = snakeY[i] * TILE_SIZE;
            
            if (i == 0) {
                // Head with bright gradient
                java.awt.GradientPaint headGradient = new java.awt.GradientPaint(
                        sx, sy, new Color(140, 255, 140),
                        sx + TILE_SIZE, sy + TILE_SIZE, new Color(80, 200, 80)
                );
                g2.setPaint(headGradient);
                g2.fillRoundRect(sx + 1, sy + 1, TILE_SIZE - 2, TILE_SIZE - 2, 6, 6);
                g2.setColor(new Color(60, 180, 60));
                g2.setStroke(new java.awt.BasicStroke(2));
                g2.drawRoundRect(sx + 1, sy + 1, TILE_SIZE - 2, TILE_SIZE - 2, 6, 6);
            } else {
                // Body with subtle gradient
                java.awt.GradientPaint bodyGradient = new java.awt.GradientPaint(
                        sx, sy, new Color(100, 200, 100),
                        sx + TILE_SIZE, sy + TILE_SIZE, new Color(60, 160, 60)
                );
                g2.setPaint(bodyGradient);
                g2.fillRoundRect(sx + 2, sy + 2, TILE_SIZE - 4, TILE_SIZE - 4, 4, 4);
            }
        }

        if (!running) {
            drawGameOver(g2);
        } else if (paused) {
            drawPaused(g2);
        }
        g2.dispose();
    }

    private void drawVoiceStatus(Graphics2D g2) {
        String status = voiceController.getStatusMessage();
        boolean isListening = voiceController.isListening();
        
        // Draw status indicator in top-right corner
        int x = getWidth() - 150;
        int y = 15;
        
        // Draw background
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(x - 5, y - 12, 145, 22, 8, 8);
        
        // Draw listening indicator
        if (isListening) {
            g2.setColor(new Color(100, 255, 100));
            g2.fillOval(x, y - 6, 12, 12);
            g2.setColor(new Color(50, 200, 50));
            g2.drawOval(x, y - 6, 12, 12);
        } else {
            g2.setColor(new Color(150, 150, 150));
            g2.fillOval(x, y - 6, 12, 12);
        }
        
        // Draw status text
        g2.setColor(isListening ? new Color(200, 255, 200) : new Color(200, 200, 200));
        g2.setFont(getFont().deriveFont(Font.PLAIN, 11f));
        g2.drawString(status, x + 18, y + 4);
    }

    private void drawPaused(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        g2.setColor(new Color(255, 255, 100));
        g2.setFont(getFont().deriveFont(Font.BOLD, 56f));
        FontMetrics fm = g2.getFontMetrics();
        String pausedText = "PAUSED";
        int x = (getWidth() - fm.stringWidth(pausedText)) / 2;
        int y = getHeight() / 2;
        
        // Draw shadow
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(pausedText, x + 3, y + 3);
        
        // Draw text
        g2.setColor(new Color(255, 255, 100));
        g2.drawString(pausedText, x, y);
        
        g2.setFont(getFont().deriveFont(Font.PLAIN, 18f));
        FontMetrics fm2 = g2.getFontMetrics();
        String hint = "Press P to resume";
        int x2 = (getWidth() - fm2.stringWidth(hint)) / 2;
        g2.setColor(Color.WHITE);
        g2.drawString(hint, x2, y + 40);
    }

    private void drawGameOver(Graphics2D g2) {
        String over = "Game Over!";
        String prompt = controlMode == ControlMode.VOICE ? "Say RESTART to play again" : "Press ENTER to restart";

        // Draw semi-transparent overlay with gradient
        java.awt.GradientPaint overlayGradient = new java.awt.GradientPaint(
                0, 0, new Color(0, 0, 0, 180),
                0, getHeight(), new Color(20, 20, 40, 200)
        );
        g2.setPaint(overlayGradient);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Draw "Game Over" with shadow
        g2.setFont(getFont().deriveFont(Font.BOLD, 56f));
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(over)) / 2;
        int y = getHeight() / 2 - fm.getHeight();
        
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(over, x + 4, y + 4);
        g2.setColor(new Color(255, 100, 100));
        g2.drawString(over, x, y);

        g2.setFont(getFont().deriveFont(Font.PLAIN, 18f));
        FontMetrics fm2 = g2.getFontMetrics();
        int x2 = (getWidth() - fm2.stringWidth(prompt)) / 2;
        int y2 = y + 50;
        g2.setColor(new Color(200, 200, 255));
        g2.drawString(prompt, x2, y2);

        // Score summary and new high indicator
        String summary = "Score: " + score + "   High: " + finalHighScore + "   Low: " + (finalLowScore == 0 ? "-" : Integer.toString(finalLowScore));
        int x3 = (getWidth() - fm2.stringWidth(summary)) / 2;
        g2.setColor(Color.WHITE);
        g2.drawString(summary, x3, y2 + 35);
        
        if (achievedNewHigh) {
            String nh = "★ NEW HIGH SCORE! ★";
            g2.setFont(getFont().deriveFont(Font.BOLD, 24f));
            FontMetrics fm3 = g2.getFontMetrics();
            int x4 = (getWidth() - fm3.stringWidth(nh)) / 2;
            
            // Pulsing effect with gradient
            java.awt.GradientPaint highScoreGradient = new java.awt.GradientPaint(
                    x4, y2 + 60, new Color(255, 230, 100),
                    x4 + fm3.stringWidth(nh), y2 + 80, new Color(255, 180, 0)
            );
            g2.setPaint(highScoreGradient);
            g2.drawString(nh, x4, y2 + 70);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            if (controlMode == ControlMode.VOICE) {
                applyVoiceDirectionIfAny();
            }
            moveSnake();
            checkFood();
            checkCollisions();
        } else if (!running) {
            if (controlMode == ControlMode.VOICE) {
                applyRestartIfAny();
            }
        }
        repaint();
    }

    private void applyRestartIfAny() {
        if (voiceController == null || !voiceController.isEnabled()) {
            return;
        }
        String cmd = voiceController.getCommand();
        if (cmd == null) {
            return;
        }
        if ("restart".equals(cmd)) {
            System.out.println("GamePanel: Restarting game via voice command");
            startGame();
        }
    }

    private void applyVoiceDirectionIfAny() {
        if (voiceController == null || !voiceController.isEnabled()) {
            return;
        }
        String cmd = voiceController.getCommand();
        if (cmd == null) {
            return;
        }
        System.out.println("GamePanel: Applying voice command: " + cmd);
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
            int key = e.getKeyCode();
            
            // Pause works for both keyboard and voice modes
            if (key == KeyEvent.VK_P && running) {
                paused = !paused;
                return;
            }
            
            if (controlMode != ControlMode.KEYBOARD) {
                return;
            }
            
            if (running && !paused) {
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
            } else if (!running) {
                if (key == KeyEvent.VK_ENTER) {
                    startGame();
                }
            }
        }
    }
}


