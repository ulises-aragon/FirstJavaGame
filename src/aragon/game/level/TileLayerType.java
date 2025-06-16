package aragon.game.level;

import java.util.HashMap;
import java.util.Map;

public enum TileLayerType {
    BACKGROUND(true, 0),
    TERRAIN(true, 500),
    FOREGROUND(true, 1500),
    COLLISION(false, 0),
    TRIGGER(false, 0),
    DECORATION(true, 1000);

    private final boolean visible;
    private final int zOrder;

    private static final Map<String, TileLayerType> MAPPING = new HashMap<>();

    static {
        for (TileLayerType type : TileLayerType.values()) {
            MAPPING.put(type.name().toLowerCase(), type);
        }
    }

    public static TileLayerType get(String tileLayerTypeName) {
        String normalized = tileLayerTypeName.toLowerCase().trim();
        TileLayerType type = MAPPING.get(normalized);
        if (type == null) {
            throw new IllegalArgumentException("Unknown tile type: " + tileLayerTypeName);
        }
        return type;
    }

    TileLayerType(boolean visible, int zOrder) {
        this.visible = visible;
        this.zOrder = zOrder;
    }

    public boolean isVisible() { return visible; }
    public int getZOrder() { return zOrder; }
}
