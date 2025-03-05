package Question_3.Question_3_b;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Block {
    private int[][] shape;
    private Color color;
    private int x;
    private int y;

    public Block(int[][] shape, Color color) {
        this.shape = shape;
        this.color = color;
        this.x = 4;  // Start position
        this.y = 0;
    }

    // Add these new methods
    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public void centerAdjustment(int[][] originalShape) {
        int xOffset = (originalShape[0].length - shape[0].length) / 2;
        x += xOffset;
    }

    public void rotate() {
        int[][] rotated = new int[shape[0].length][shape.length];
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                rotated[j][shape.length - 1 - i] = shape[i][j];
            }
        }
        shape = rotated;
    }

    // Existing methods
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
}