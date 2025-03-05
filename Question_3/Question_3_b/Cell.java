package Question_3.Question_3_b;

import java.awt.Color;

public class Cell {
    private int x;
    private int y;
    private Color color;

    public Cell(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public Color getColor() { return color; }
    public void setY(int y) { this.y = y; }
}