package aragon.game.level;

import aragon.game.util.Vector2;

import java.awt.*;

public class TileLayer implements Comparable<TileLayer> {
    private final String name;
    private final int width, height;
    private final TileSet tileSet;
    private final TileLayerType type;

    private boolean visible;
    private float opacity;
    private int zOrder;
    private final int[][] tiles;

    public TileLayer(String name, int width, int height, TileLayerType type, TileSet tileSet) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.tileSet = tileSet;
        this.type = type;

        this.visible = type.isVisible();
        this.opacity = 1f;
        this.zOrder = type.getZOrder();
        this.tiles = new int[height][width];

        initializeEmptyLayer();
    }

    private void initializeEmptyLayer() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[y][x] = -1;
            }
        }
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public int getTileId(int x, int y) {
        if (isValidPosition(x, y)) {
            return tiles[y][x];
        }
        return -1;
    }

    public void setTileId(int x, int y, int tileId) {
        if (isValidPosition(x, y)) {
            tiles[y][x] = tileId;
        }
    }

    public boolean hasTileAt(int x, int y) {
        return isValidPosition(x, y) && tiles[y][x] != -1;
    }

    public void render(Graphics graphics, Level level) {
        if (!visible) return;

        Vector2 cameraPosition = level.getGameState().getGame().getCamera().getPosition();

        int startX = (int) Math.max(0, cameraPosition.x / tileSet.getTileWidth()-1);
        int endX = (int) Math.min(width, (cameraPosition.x + level.getGameState().getGame().getScreenWidth()) / tileSet.getTileWidth()+1);
        int startY = (int) Math.max(0, cameraPosition.y / tileSet.getTileHeight()-1);
        int endY = (int) Math.min(height, (cameraPosition.y + level.getGameState().getGame().getScreenHeight()) / tileSet.getTileHeight()+1);

        // TODO: Apply opacity to Sprites.

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                int tileId = tiles[y][x];
                if (tileId != -1) {
                    Tile tile = tileSet.getTile(tileId);
                    if (tile != null) {
                        tile.render(graphics, level,x * tileSet.getTileWidth(), y * tileSet.getTileHeight());
                    }
                }
            }
        }
    }

    public boolean isSolidAt(int x, int y) {
        int tileId = getTileId(x, y);
        if (tileId == -1) return false;

        Tile tile = tileSet.getTile(tileId);
        return tile != null && tile.getType().isSolid();
    }

    public Vector2 getPositionAt(int x, int y, float scale) {
        return new Vector2(x * tileSet.getTileWidth() * scale, y * tileSet.getTileHeight() * scale);
    }

    public String getName() { return name; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public float getOpacity() { return opacity; }
    public void setOpacity(float opacity) {
        this.opacity = Math.max(0.0f, Math.min(1.0f, opacity));
    }

    public int getZOrder() { return zOrder; }
    public void setZOrder(int zOrder) { this.zOrder = zOrder; }

    public TileLayerType getLayerType() { return type; }

    @Override
    public int compareTo(TileLayer other) {
        return Integer.compare(zOrder, other.zOrder);
    }
}
