package Question_3.Question_3_b;

import java.awt.Color;

/**
 * This class represents a single cell in a grid-based game like Tetris.
 * Each cell has an x and y coordinate along with a color.
 * The class provides getter methods to retrieve cell properties
 * and a setter method to update the y-coordinate.
 */
public class Cell {
    private int x;      // X position of the cell
    private int y;      // Y position of the cell
    private Color color; // Color of the cell

    /**
     * Constructor to initialize a cell with a position and color.
     * 
     * @param x The x-coordinate of the cell
     * @param y The y-coordinate of the cell
     * @param color The color of the cell
     */
    public Cell(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    /**
     * Retrieves the x-coordinate of the cell.
     * 
     * @return The x-coordinate
     */
    public int getX() { return x; }

    /**
     * Retrieves the y-coordinate of the cell.
     * 
     * @return The y-coordinate
     */
    public int getY() { return y; }

    /**
     * Retrieves the color of the cell.
     * 
     * @return The color of the cell
     */
    public Color getColor() { return color; }

    /**
     * Sets the y-coordinate of the cell.
     * 
     * @param y The new y-coordinate
     */
    public void setY(int y) { this.y = y; }

    /**
     * Summary:
     * This class implements a simple representation of a grid cell with coordinates and a color.
     * It allows retrieving and modifying the y-coordinate of the cell while keeping x and color immutable.
     * The algorithm works as expected by properly managing cell properties.
     */
}
