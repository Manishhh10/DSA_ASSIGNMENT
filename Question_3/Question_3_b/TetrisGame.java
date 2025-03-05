/*
 * TetrisGame.java
 *
 * This file implements a Tetris game using Java Swing.
 * The algorithm initializes the game board, manages the falling tetromino blocks,
 * detects collisions and complete lines, updates the score, and handles user input
 * for moving and rotating the blocks. The game loop continuously moves the blocks down
 * at a speed that increases with the score, and checks for game over conditions.
 *
 * The algorithm works by:
 * - Initializing a shuffled bag of tetromino blocks.
 * - Continuously moving the current block down until it collides.
 * - Placing the block on the grid upon collision and checking for full lines.
 * - Clearing full lines, updating the score, and applying gravity to the grid.
 * - Allowing the user to move the block left/right or rotate it.
 * - Restarting the game upon game over.
 *
*/

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
 
     /**
      * Constructor for TetrisGame.
      * Initializes the game window, UI components, game board, and control buttons.
      * Also starts a new game.
      */
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
 
     /**
      * Getter method for retrieving the game grid.
      * @return a 2D boolean array representing occupied cells in the grid.
      */
     public boolean[][] getGrid() { 
         return grid; 
     }
 
     /**
      * Getter method for retrieving the grid colors.
      * @return a 2D Color array representing the colors of blocks on the grid.
      */
     public Color[][] getColors() { 
         return colors; 
     }
 
     /**
      * Getter method for retrieving the current active block.
      * @return the Block that is currently falling.
      */
     public Block getCurrentBlock() { 
         return currentBlock; 
     }
 
     /**
      * Getter method for retrieving the next block in the queue.
      * @return the Block that will follow the current block.
      */
     public Block getNextBlock() { 
         return blockQueue.peek(); 
     }
 
     /**
      * Starts a new game by resetting the game state,
      * clearing the grid, initializing the block bag, and starting the game loop.
      */
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
 
     /**
      * Clears the grid by resetting all cells to false and removing any colors.
      */
     private void clearGrid() {
         for (int x = 0; x < WIDTH; x++) {
             Arrays.fill(grid[x], false);
             Arrays.fill(colors[x], null);
         }
     }
 
     /**
      * Initializes the block bag with all available tetromino shapes and their corresponding colors.
      * Shuffles the bag to ensure random block selection.
      */
     private void initializeBlockBag() {
         blockBag.clear();
         int[][][] shapes = {
             {{1,1,1,1}}, 
             {{1,1}, {1,1}}, 
             {{1,1,1}, {0,1,0}},
             {{1,1,1}, {1,0,0}}, 
             {{1,1,1}, {0,0,1}},
             {{1,1,0}, {0,1,1}}, 
             {{0,1,1}, {1,1,0}}
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
 
     /**
      * Retrieves the next block from the block bag.
      * Reinitializes the block bag if it is empty.
      * @return the next Block to be used in the game.
      */
     private Block getNextBlockFromBag() {
         if (blockBag.isEmpty()) initializeBlockBag();
         return blockBag.remove(0);
     }
 
     /**
      * Main game loop that continuously updates the game state.
      * Moves the current block down periodically, checks for collisions,
      * places the block if a collision occurs, clears full lines, and updates the game board.
      */
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
 
     /**
      * Attempts to move the current block horizontally.
      * If a collision is detected after moving, the block is reset to its original position.
      * @param dx the horizontal movement offset (negative for left, positive for right)
      * @return true if the move was successful, false if reverted due to collision.
      */
     private boolean moveHorizontal(int dx) {
         int originalX = currentBlock.getX();
         currentBlock.move(dx, 0);
         if (collision()) {
             currentBlock.setX(originalX);
             return false;
         }
         return true;
     }
 
     /**
      * Attempts to move the current block downward.
      * If a collision is detected after moving, the block is reset to its original position.
      * @return true if the move down was successful, false otherwise.
      */
     private boolean moveDown() {
         int originalY = currentBlock.getY();
         currentBlock.move(0, 1);
         if (collision()) {
             currentBlock.setY(originalY);
             return false;
         }
         return true;
     }
 
     /**
      * Checks whether the current block is colliding with the boundaries or occupied cells.
      * @return true if a collision is detected, false otherwise.
      */
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
 
     /**
      * Places the current block on the grid by marking its cells as occupied
      * and assigning the block's color to the corresponding grid cells.
      */
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
 
     /**
      * Checks the grid for complete lines.
      * If full rows are detected, increases the score and applies gravity to remove the full rows.
      */
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
 
         // Apply gravity to shift down non-full rows.
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
 
     /**
      * Rotates the current block.
      * If the rotated block collides with other blocks or boundaries, the rotation is undone.
      */
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
 
     /**
      * Checks if the game is over by verifying whether any cell of the current block
      * is positioned above the visible grid.
      * @return true if game over condition is met, false otherwise.
      */
     private boolean isGameOver() {
         return currentBlock.getCells().stream()
                 .anyMatch(c -> c.getY() < 0);
     }
 
     /**
      * Displays a game over dialog to the user with the final score.
      * If the user chooses to play again, a new game is started; otherwise, the application exits.
      */
     private void showGameOver() {
         SwingUtilities.invokeLater(() -> {
             int choice = JOptionPane.showConfirmDialog(this,
                     "Game Over! Score: " + score + "\nPlay again?",
                     "Game Over", JOptionPane.YES_NO_OPTION);
             if (choice == JOptionPane.YES_OPTION) startNewGame();
             else System.exit(0);
         });
     }
 
     /**
      * Updates the score label displayed at the top of the game window.
      */
     private void updateScore() {
         scoreLabel.setText("Score: " + score);
     }
 
     /**
      * Main entry point of the Tetris game.
      * Initializes the game window and makes it visible.
      * @param args command line arguments (not used)
      */
     public static void main(String[] args) {
         SwingUtilities.invokeLater(() -> new TetrisGame().setVisible(true));
     }
 }
//  * Summary:
//  * In this implementation, we built a functional Tetris game where blocks fall, and players can control them.
//  * We managed game state, collision detection, line clearing, and score updating.
//  * Testing shows that the algorithm performs as expected, providing a playable Tetris experience.
//  */