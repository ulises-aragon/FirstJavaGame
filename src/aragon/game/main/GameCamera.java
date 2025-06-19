package aragon.game.main;

import aragon.game.entity.Entity;
import aragon.game.level.Level;
import aragon.game.level.TileSet;
import aragon.game.main.states.State;
import aragon.game.util.Vector2;

public class GameCamera {
    private final Game game;
    private Vector2 position = Vector2.zero;

    public GameCamera(Game game) {
        this.game = game;
    }

    public Vector2 getPosition() { return new Vector2(position); }

    private void applyLimits() {
        Level level = State.getState().getLevel();
        if (level == null) return;
        TileSet levelTileSet = level.getTileSet();

        int cameraXLimit = (level.getWidth()*levelTileSet.getTileWidth()) - game.getScreenWidth();
        int cameraYLimit = (level.getHeight()*levelTileSet.getTileHeight()) - game.getScreenHeight();

        if (position.x < 0 && position.y < 0) {
            position = Vector2.zero;
        } else if (position.x < 0) {
            position = new Vector2(0, position.y);
        } else if (position.y < 0) {
            position = new Vector2(position.x, 0);
        }

        if (position.x > cameraXLimit && position.y > cameraYLimit) {
            position = new Vector2(cameraXLimit, cameraYLimit);
        } else if (position.x > cameraXLimit) {
            position = new Vector2(cameraXLimit, position.y);
        } else if (position.y > cameraYLimit) {
            position = new Vector2(position.x, cameraYLimit);
        }
    }

    public void move(int x, int y) {
        position = position.add(new Vector2(x, y));
        applyLimits();
    }

    public void setCameraSubject(Entity entity) {
        Vector2 entitySize = entity.getSize();
        int screenWidth = game.getScreenWidth();
        int screenHeight = game.getScreenHeight();
        position = entity.position.subtract(
                new Vector2(
                        (double) screenWidth/2 - entitySize.x/2,
                        (double) screenHeight/2 - entitySize.y/2
                )
        );
        applyLimits();
    }
}
