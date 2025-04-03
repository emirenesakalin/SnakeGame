import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SnakeGame extends JPanel implements ActionListener {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private static final int UNIT_SIZE = 20;
    private static final int GAME_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int DELAY = 100;

    private final int[] x = new int[GAME_UNITS];
    private final int[] y = new int[GAME_UNITS];
    private int bodyParts = 3;
    private int foodX;
    private int foodY;
    private char direction = 'R'; // R: Right, L: Left, U: Up, D: Down
    private boolean running = false;
    private Timer timer;
    private final JButton restartButton;

    public SnakeGame() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        // Initialize restart button
        restartButton = new JButton("Restart");
        restartButton.setBounds(WIDTH / 2 - 50, HEIGHT / 2 + 60, 100, 30);
        restartButton.addActionListener(e -> restartGame());
        this.setLayout(null); // Use absolute positioning for the button
        this.add(restartButton);
        restartButton.setVisible(false); // Initially hidden

        startGame();
    }

    public void startGame() {
        newFood();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
        restartButton.setVisible(false); // Hide restart button when the game starts
    }

    public void restartGame() {
        bodyParts = 3;
        direction = 'R';
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        startGame();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            // Draw food
            g.setColor(Color.RED);
            g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

            // Draw snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(new Color(45, 180, 0));
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            // Draw score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + (bodyParts - 3), 10, 20);
        } else {
            gameOver(g);
        }
    }

    public void newFood() {
        foodX = (int) (Math.random() * (WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        foodY = (int) (Math.random() * (HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U' -> y[0] -= UNIT_SIZE;
            case 'D' -> y[0] += UNIT_SIZE;
            case 'L' -> x[0] -= UNIT_SIZE;
            case 'R' -> x[0] += UNIT_SIZE;
        }

        // Wrap around the screen
        if (x[0] < 0) x[0] = WIDTH - UNIT_SIZE;
        if (x[0] >= WIDTH) x[0] = 0;
        if (y[0] < 0) y[0] = HEIGHT - UNIT_SIZE;
        if (y[0] >= HEIGHT) y[0] = 0;
    }

    public void checkFood() {
        if (x[0] == foodX && y[0] == foodY) {
            bodyParts++;
            newFood();
        }
    }

    public void checkCollisions() {
        // Check if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }

        if (!running) {
            timer.stop();
            restartButton.setVisible(true); // Show restart button when the game ends
        }
    }

    public void gameOver(Graphics g) {
        // Game Over text
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("Game Over", WIDTH / 2 - 100, HEIGHT / 2);

        // Final score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Final Score: " + (bodyParts - 3), WIDTH / 2 - 80, HEIGHT / 2 + 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    if (direction != 'R') direction = 'L';
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != 'L') direction = 'R';
                }
                case KeyEvent.VK_UP -> {
                    if (direction != 'D') direction = 'U';
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != 'U') direction = 'D';
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}