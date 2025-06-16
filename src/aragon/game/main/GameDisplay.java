package aragon.game.main;

import javax.swing.JFrame;
import java.awt.Canvas;
import java.awt.Dimension;

public class GameDisplay {
    private JFrame frame;
    private Canvas canvas;
    private final String title;
    private int width, height;

    public GameDisplay(Game game, String title, int width, int height) {
        this.title = title;
        this.width = game.getScreenWidth();
        this.height = game.getScreenHeight();

        display();
    }

    private void display() {
        frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        canvas.setMinimumSize(new Dimension(width, height));
        canvas.setPreferredSize(new Dimension(width, height));

        frame.add(canvas);
        frame.pack();
    }

    public JFrame getFrame() {
        return frame;
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
