package aragon.game.main;

import aragon.game.entity.Entity;
import aragon.game.level.Level;
import aragon.game.level.TileSet;
import aragon.game.util.Vector2D;

public class GameCamera {
    private final GameHandler gameHandler;
    private Vector2D position = Vector2D.zero;

    public GameCamera(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    public Vector2D getPosition() { return new Vector2D(position); }

    private void applyLimits() {
        Level level = gameHandler.getLevel();
        TileSet levelTileSet = level.getTileSet();

        int cameraXLimit = (level.getWidth()*levelTileSet.getTileWidth()) - gameHandler.getScreenWidth();
        int cameraYLimit = (level.getHeight()*levelTileSet.getTileHeight()) - gameHandler.getScreenHeight();

        if (position.x < 0 && position.y < 0) {
            position = Vector2D.zero;
        } else if (position.x < 0) {
            position = new Vector2D(0, position.y);
        } else if (position.y < 0) {
            position = new Vector2D(position.x, 0);
        } else if (position.x > cameraXLimit && position.y > cameraYLimit) {
            position = new Vector2D(cameraXLimit, cameraYLimit);
        } else if (position.x > cameraXLimit) {
            position = new Vector2D(cameraXLimit, position.y);
        } else if (position.y > cameraYLimit) {
            position = new Vector2D(position.x, cameraYLimit);
        }
    }

    public void move(int x, int y) {
        position = position.add(new Vector2D(x, y));
        applyLimits();
    }

    public void setCameraSubject(Entity entity) {
        Vector2D entitySize = entity.getSize();
        int screenWidth = gameHandler.getScreenWidth();
        int screenHeight = gameHandler.getScreenHeight();
        position = entity.position.subtract(
                new Vector2D(
                        (double) screenWidth/2 - entitySize.x/2,
                        (double) screenHeight/2 - entitySize.y/2
                )
        );
        applyLimits();
    }
}
