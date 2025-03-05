package Question_3.Question_3_b;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a Tetris-like block that can be moved and rotated on a grid.
 * It maintains the block's shape, color, and position.
 * The algorithm allows the block to move horizontally and vertically, rotate,
 * and adjust its center alignment. The getCells method returns the occupied cells
 * for rendering or collision detection.
 */
public class Block {
    private int[][] shape; // 2D array representing the block's shape
    private Color color;   // Color of the block
    private int x;         // X position of the block
    private int y;         // Y position of the block

    /**
     * Constructor to initialize a block with a given shape and color.
     * The block starts at a default position on the grid.
     * 
     * @param shape The 2D array representing the shape of the block
     * @param color The color of the block
     */
    public Block(int[][] shape, Color color) {
        this.shape = shape;
        this.color = color;
        this.x = 4;  // Default start position
        this.y = 0;
    }

    /**
     * Moves the block by a specified amount in x and y directions.
     * 
     * @param dx Change in x-coordinate
     * @param dy Change in y-coordinate
     */
    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    /**
     * Adjusts the block's position to center it based on its original shape.
     * Useful when rotating or modifying the block shape.
     * 
     * @param originalShape The original shape of the block before transformation
     */
    public void centerAdjustment(int[][] originalShape) {
        int xOffset = (originalShape[0].length - shape[0].length) / 2;
        x += xOffset;
    }

    /**
     * Rotates the block 90 degrees clockwise by transposing and reversing the rows.
     * This method modifies the block's shape in-place.
     */
    public void rotate() {
        int[][] rotated = new int[shape[0].length][shape.length];
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                rotated[j][shape.length - 1 - i] = shape[i][j];
            }
        }
        shape = rotated;
    }

    /**
     * Returns a list of cells occupied by the block.
     * Each cell contains its position and color for rendering or collision detection.
     * 
     * @return List of occupied cells
     */
    public List<Cell> getCells() {
        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    cells.add(new Cell(x + j, y + i, color));
                }
            }
        }
        return cells;
    }

    // Getters and setters
    public int[][] getShape() { return shape; }
    public void setShape(int[][] shape) { this.shape = shape; }
    public Color getColor() { return color; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    /**
     * Summary:
     * This class implements a block that can move, rotate, and provide its occupied cells.
     * The move method updates the block's position, while the rotate method rotates the block 90 degrees clockwise.
     * The getCells method helps retrieve the cells occupied by the block for rendering.
     * 
     * The algorithm successfully works as expected by correctly updating the block’s position
     * and structure when transformations are applied.
     */
}
