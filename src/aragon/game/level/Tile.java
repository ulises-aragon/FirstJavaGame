package aragon.game.level;

import aragon.game.graphics.Sprite;
import aragon.game.main.GameHandler;
import aragon.game.main.states.State;
import aragon.game.util.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Tile {
    private final int id;
    private TileType type;
    private final BufferedImage image;
    private final int tileWidth;
    private final int tileHeight;
    private Map<String, Object> properties;

    public Tile(int id, int tileWidth, int tileHeight, TileType type, Sprite sprite) {
        this.id = id;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.type = type;
        this.image = sprite.getImage();
        this.properties = new HashMap<>();
    }

    public int getId() { return id; }
    public TileType getType() { return type; }

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    public <T> T getProperty(String key, Class<T> type) {
        Object value = properties.get(key);
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    public <T> T getProperty(String key, Class<T> type, T defaultValue) {
        T value = getProperty(key, type);
        if (value != null) {
            return  value;
        }
        return defaultValue;
    }

    public void render(Graphics graphics, GameHandler gameHandler, int x, int y) {
        if (image == null) return;
        Vector2D cameraPosition = gameHandler.getCamera().getPosition();

        graphics.drawImage(
                image,
                (int) (x - cameraPosition.x),
                (int) (y - cameraPosition.y),
                tileWidth,
                tileHeight,
                null
        );
    }
}
