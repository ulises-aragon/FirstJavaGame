package aragon.game.entity;

import aragon.game.util.Vector2;

import java.awt.*;

public abstract class Entity {
    protected final EntityManager entityManager;
    public Vector2 position;
    protected Vector2 size;
    protected Rectangle collisionBounds;
    private final Vector2 originalSize;
    private double scale=1;
    private boolean solid;
    private boolean alive=true;

    public Entity(EntityManager entityManager, int x, int y, int w, int h, boolean solid) {
        this.entityManager = entityManager;
        this.position = new Vector2(x, y);
        this.size = new Vector2(w, h);
        this.solid = solid;
        this.originalSize = new Vector2(w, h);
        this.collisionBounds = new Rectangle(0, 0, w, h);
    }

    public Entity(EntityManager entityManager, int x, int y, int size, boolean solid) {
        this(entityManager, x, y, size, size, solid);
    }

    public boolean checkEntityCollisions() {
        return checkEntityCollisions(0, 0);
    }

    public boolean checkEntityCollisions(int xOffset, int yOffset) {
        for (Entity entity : entityManager.getLevel().getEntityManager().getEntities()) {
            if (entity.equals(this)) continue;
            if (entity.getCollisionBounds().intersects(getCollisionBounds(xOffset, yOffset))) return true;
        }
        return false;
    }

    public boolean checkEntitySolidCollisions() {
        return checkEntitySolidCollisions(0, 0);
    }

    public boolean checkEntitySolidCollisions(int xOffset, int yOffset) {
        for (Entity entity : entityManager.getLevel().getEntityManager().getEntities()) {
            if (entity.equals(this)) continue;
            if (!entity.isSolid()) continue;
            if (entity.getCollisionBounds().intersects(getCollisionBounds(xOffset, yOffset))) return true;
        }
        return false;
    }

    public Rectangle getCollisionBounds() {
        return getCollisionBounds(0,0);
    }

    public Rectangle getCollisionBounds(int xOffset, int yOffset) {
        return new Rectangle(
                (int) (position.x + collisionBounds.x + xOffset),
                (int) (position.y + collisionBounds.y + yOffset),
                collisionBounds.width,
                collisionBounds.height
        );
    }

    public Vector2 getSize() { return new Vector2(size); }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
        size = originalSize.scale(scale);
    }

    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    protected void suicide() {
        alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    public abstract void update();
    public abstract void render(Graphics graphics);
}
