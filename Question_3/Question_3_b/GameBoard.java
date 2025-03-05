package Question_3.Question_3_b;

import javax.swing.*;
import java.awt.*;

public class GameBoard extends JPanel {
    private final TetrisGame game;

    public GameBoard(TetrisGame game) {
        this.game = game;
        setPreferredSize(new Dimension(450, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawBlocks(g);
        drawCurrentBlock(g);
        drawPreview(g);
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        for (int x = 0; x < TetrisGame.WIDTH; x++) {
            for (int y = 0; y < TetrisGame.HEIGHT; y++) {
                g.drawRect(x * 30, y * 30, 30, 30);
            }
        }
    }

    private void drawBlocks(Graphics g) {
        boolean[][] grid = game.getGrid();
        Color[][] colors = game.getColors();
        for (int x = 0; x < TetrisGame.WIDTH; x++) {
            for (int y = 0; y < TetrisGame.HEIGHT; y++) {
                if (grid[x][y]) {
                    g.setColor(colors[x][y]);
                    g.fillRect(x * 30 + 1, y * 30 + 1, 28, 28);
                }
            }
        }
    }

    private void drawCurrentBlock(Graphics g) {
        Block current = game.getCurrentBlock();
        if (current != null) {
            g.setColor(current.getColor());
            for (Cell cell : current.getCells()) {
                int x = cell.getX();
                int y = cell.getY();
                if (y >= 0) {
                    g.fillRect(x * 30 + 1, y * 30 + 1, 28, 28);
                }
            }
        }
    }

    private void drawPreview(Graphics g) {
        int previewX = 330;
        int previewY = 50;
        int previewSize = 100;

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(previewX, previewY, previewSize, previewSize);
        g.setColor(Color.BLACK);
        g.drawString("Next Block:", previewX + 10, previewY - 10);

        Block next = game.getNextBlock();
        if (next != null) {
            int[][] shape = next.getShape();
            int startX = previewX + (previewSize - shape[0].length * 20) / 2;
            int startY = previewY + (previewSize - shape.length * 20) / 2;

            g.setColor(next.getColor());
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[i].length; j++) {
                    if (shape[i][j] == 1) {
                        g.fillRect(startX + j * 20, startY + i * 20, 19, 19);
                    }
                }
            }
        }
    }
}