package aragon.game.level;

import aragon.game.graphics.SpriteSheet;

import java.util.HashMap;
import java.util.Map;

public class TileSet {
    private final SpriteSheet sheet;
    private final int tileWidth;
    private final int tileHeight;
    private final Map<Integer, Tile> tiles;

    public TileSet(SpriteSheet sheet, int tileWidth, int tileHeight) {
        this.sheet = sheet;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tiles = new HashMap<>();
    }

    public void addNewTile(int id, TileType type, int x, int y) {
        tiles.put(id, new Tile(id, tileWidth, tileHeight, type, sheet.getSprite(x, y)));
    }

    public int getTileWidth() { return tileWidth; }
    public int getTileHeight() { return tileHeight; }

    public Tile getTile(int id) {
        return tiles.get(id);
    }
}
