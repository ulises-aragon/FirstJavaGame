package aragon.game.entity;

import aragon.game.main.GameHandler;
import aragon.game.util.Vector2D;

import java.awt.Graphics;

public abstract class Entity {
    protected final GameHandler gameHandler;
    public Vector2D position;
    protected Vector2D size;
    private final Vector2D originalSize;
    private double scale=1;

    public Entity(GameHandler gameHandler, int x, int y, int w, int h) {
        this.gameHandler = gameHandler;
        this.position = new Vector2D(x, y);
        this.size = new Vector2D(w, h);
        this.originalSize = new Vector2D(w, h);
    }

    public Vector2D getSize() { return new Vector2D(size); }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
        size = originalSize.scale(scale);
    }

    public abstract void update();
    public abstract void render(Graphics graphics);
}
