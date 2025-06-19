package aragon.game.level;

import aragon.game.graphics.Sprite;
import aragon.game.util.Vector2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Tile {
    private final int id;
    private final TileType type;
    private final BufferedImage image;
    private final int tileWidth;
    private final int tileHeight;
    private final Map<String, Object> properties;

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

    public void render(Graphics graphics, Level level, int x, int y) {
        if (image == null) return;
        Vector2 cameraPosition = level.getGameState().getGame().getCamera().getPosition();

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
