package Question_3.Question_3_b;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Queue;

public class TetrisGame extends JFrame {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;
    private final boolean[][] grid = new boolean[WIDTH][HEIGHT];
    private final Color[][] colors = new Color[WIDTH][HEIGHT];
    private final Queue<Block> blockQueue = new LinkedList<>();
    private Block currentBlock;
    private boolean gameOver = false;
    private int score = 0;
    private final GameBoard gameBoard;
    private final JLabel scoreLabel;
    private List<Block> blockBag = new ArrayList<>();

    public TetrisGame() {
        setTitle("Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        gameBoard = new GameBoard(this);
        add(gameBoard, BorderLayout.CENTER);

        JPanel controls = new JPanel();
        JButton left = new JButton("←"), right = new JButton("→"), rotate = new JButton("↻");
        controls.add(left);
        controls.add(rotate);
        controls.add(right);

        left.addActionListener(e -> moveHorizontal(-1));
        right.addActionListener(e -> moveHorizontal(1));
        rotate.addActionListener(e -> rotateBlock());

        scoreLabel = new JLabel("Score: 0");
        add(scoreLabel, BorderLayout.NORTH);
        add(controls, BorderLayout.SOUTH);

        setSize(500, 700);
        setLocationRelativeTo(null);
        startNewGame();
    }

    // Getters for GameBoard access
    public boolean[][] getGrid() { return grid; }
    public Color[][] getColors() { return colors; }
    public Block getCurrentBlock() { return currentBlock; }
    public Block getNextBlock() { return blockQueue.peek(); }

    private void startNewGame() {
        gameOver = false;
        score = 0;
        clearGrid();
        blockQueue.clear();
        initializeBlockBag();
        blockQueue.add(getNextBlockFromBag());
        blockQueue.add(getNextBlockFromBag());
        currentBlock = blockQueue.poll();
        updateScore();
        new Thread(this::gameLoop).start();
    }

    private void clearGrid() {
        for (int x = 0; x < WIDTH; x++) {
            Arrays.fill(grid[x], false);
            Arrays.fill(colors[x], null);
        }
    }

    private void initializeBlockBag() {
        blockBag.clear();
        int[][][] shapes = {
            {{1,1,1,1}}, {{1,1}, {1,1}}, {{1,1,1}, {0,1,0}},
            {{1,1,1}, {1,0,0}}, {{1,1,1}, {0,0,1}},
            {{1,1,0}, {0,1,1}}, {{0,1,1}, {1,1,0}}
        };
        Color[] blockColors = {
            Color.CYAN, Color.YELLOW, Color.MAGENTA,
            Color.ORANGE, Color.BLUE, Color.GREEN, Color.RED
        };
        
        for (int i = 0; i < shapes.length; i++) {
            blockBag.add(new Block(shapes[i], blockColors[i]));
        }
        Collections.shuffle(blockBag);
    }

    private Block getNextBlockFromBag() {
        if (blockBag.isEmpty()) initializeBlockBag();
        return blockBag.remove(0);
    }

    private void gameLoop() {
        while (!gameOver) {
            try {
                Thread.sleep(1000 - Math.min(score / 5 * 25, 700));
            } catch (InterruptedException ignored) {}

            if (!moveDown()) {
                placeBlock();
                checkLines();
                if (isGameOver()) {
                    gameOver = true;
                    showGameOver();
                } else {
                    currentBlock = blockQueue.poll();
                    blockQueue.add(getNextBlockFromBag());
                }
            }
            SwingUtilities.invokeLater(gameBoard::repaint);
        }
    }

    // Movement and collision detection methods
    private boolean moveHorizontal(int dx) {
        int originalX = currentBlock.getX();
        currentBlock.move(dx, 0);
        if (collision()) {
            currentBlock.setX(originalX);
            return false;
        }
        return true;
    }

    private boolean moveDown() {
        int originalY = currentBlock.getY();
        currentBlock.move(0, 1);
        if (collision()) {
            currentBlock.setY(originalY);
            return false;
        }
        return true;
    }

    private boolean collision() {
        for (Cell cell : currentBlock.getCells()) {
            int x = cell.getX();
            int y = cell.getY();
            if (x < 0 || x >= WIDTH || y >= HEIGHT || (y >= 0 && grid[x][y])) {
                return true;
            }
        }
        return false;
    }

    private void placeBlock() {
        for (Cell cell : currentBlock.getCells()) {
            int x = cell.getX();
            int y = cell.getY();
            if (y >= 0 && y < HEIGHT && x >= 0 && x < WIDTH) {
                grid[x][y] = true;
                colors[x][y] = currentBlock.getColor();
            }
        }
    }

    private void checkLines() {
        List<Integer> fullRows = new ArrayList<>();
        for (int y = 0; y < HEIGHT; y++) {
            boolean full = true;
            for (int x = 0; x < WIDTH; x++) {
                if (!grid[x][y]) {
                    full = false;
                    break;
                }
            }
            if (full) fullRows.add(y);
        }

        score += fullRows.size() * 100;
        updateScore();

        // Apply gravity
        for (int x = 0; x < WIDTH; x++) {
            int writeY = HEIGHT - 1;
            for (int readY = HEIGHT - 1; readY >= 0; readY--) {
                if (!fullRows.contains(readY)) {
                    grid[x][writeY] = grid[x][readY];
                    colors[x][writeY] = colors[x][readY];
                    writeY--;
                }
            }
            while (writeY >= 0) {
                grid[x][writeY] = false;
                colors[x][writeY] = null;
                writeY--;
            }
        }
    }

    private void rotateBlock() {
        int[][] originalShape = currentBlock.getShape();
        int originalX = currentBlock.getX();
        int originalY = currentBlock.getY();

        currentBlock.rotate();
        currentBlock.centerAdjustment(originalShape);

        if (collision()) {
            currentBlock.setShape(originalShape);
            currentBlock.setX(originalX);
            currentBlock.setY(originalY);
        }
    }

    private boolean isGameOver() {
        return currentBlock.getCells().stream()
                .anyMatch(c -> c.getY() < 0);
    }

    private void showGameOver() {
        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Game Over! Score: " + score + "\nPlay again?",
                    "Game Over", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) startNewGame();
            else System.exit(0);
        });
    }

    private void updateScore() {
        scoreLabel.setText("Score: " + score);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TetrisGame().setVisible(true));
    }
}