package aragon.game.entity;

import aragon.game.level.TileLayerType;
import aragon.game.level.TileSet;
import aragon.game.util.Vector2;

public abstract class Creature extends Entity {
    protected int moveSpeed;
    protected int moveXAxis = 0;
    protected int moveYAxis = 0;
    protected Vector2 facingDirection = Vector2.xAxis;

    public Creature(EntityManager entityManager, int x, int y, int size, int moveSpeed) {
        this(entityManager, x, y, size, size, moveSpeed);
    }

    public Creature(EntityManager entityManager, int x, int y, int w, int h, int moveSpeed) {
        super(entityManager, x, y, w, h, true);
        this.moveSpeed = moveSpeed;
    }

    public void faceAt(Vector2 lookAtPosition) {
        facingDirection = lookAtPosition.subtract(position).normalize();
    }

    public Vector2 getMoveVector() {
        return new Vector2(moveXAxis, moveYAxis).normalize();
    }

    public Vector2 getFacingDirectionVector() {
        return new Vector2(facingDirection);
    }

    public FacingDirection getFacingDirection() {
        double absoluteLookX = Math.abs(facingDirection.x);
        double absoluteLookY = Math.abs(facingDirection.y);

        if (absoluteLookX > absoluteLookY) {
            return facingDirection.x > 0 ? FacingDirection.RIGHT : FacingDirection.LEFT;
        } else {
            return facingDirection.y > 0 ? FacingDirection.DOWN : FacingDirection.UP;
        }
    }

    public void move() {
        Vector2 moveVector = getMoveVector().scale(moveSpeed);
        Vector2 newPosition = position.add(moveVector);

        if (moveVector.magnitude() > 0) {
            facingDirection = moveVector.normalize();
        }

        if (!wouldCollide(newPosition)) {
            position = newPosition;
        } else {
            tryMoveOnAxes(moveVector);
        }
    }

    private void tryMoveOnAxes(Vector2 moveVector) {
        Vector2 horizontalMove = moveVector.multiply(Vector2.xAxis);
        Vector2 newXPosition = position.add(horizontalMove);

        if (!wouldCollide(newXPosition)) {
            position = newXPosition;
        }

        Vector2 verticalMove = moveVector.multiply(Vector2.yAxis);
        Vector2 newYPosition = position.add(verticalMove);

        if (!wouldCollide(newYPosition)) {
            position = newYPosition;
        }
    }

    private boolean wouldCollide(Vector2 newPosition) {
        if (checkEntitySolidCollisions((int) newPosition.subtract(position).x, (int) newPosition.subtract(position).y)) {
            return true;
        }

        double boundsLeft = newPosition.x + collisionBounds.x;
        double boundsRight = boundsLeft + collisionBounds.width;
        double boundsTop = newPosition.y + collisionBounds.y;
        double boundsBottom = boundsTop + collisionBounds.height;

        TileSet currentTileSet = entityManager.getLevel().getTileSet();
        int tileHeight = currentTileSet.getTileHeight();
        int tileWidth = currentTileSet.getTileWidth();

        int leftTile = (int) Math.floor(boundsLeft / tileWidth);
        int rightTile = (int) Math.floor(boundsRight / tileWidth);
        int topTile = (int) Math.floor(boundsTop / tileHeight);
        int bottomTile = (int) Math.floor(boundsBottom / tileHeight);

        for (int x = leftTile; x <= rightTile; x++) {
            for (int y = topTile; y <= bottomTile; y++) {
                if (collisionWithTile(x, y)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean collisionWithTile(int x, int y) {
        if (x < 0 || y < 0 || x >= entityManager.getLevel().getWidth() || y >= entityManager.getLevel().getHeight()) {
            return true;
        }
        return entityManager.getLevel().getTileAt(x, y, TileLayerType.TERRAIN).getType().isSolid();
    }
}
