package aragon.game.level;

import java.util.HashMap;
import java.util.Map;

public enum TileType {
    VOID(false, false),
    GROUND(false, true),
    WALL(true, false);

    private final boolean solid;
    private final boolean walkable;

    private static final Map<String, TileType> MAPPING = new HashMap<>();

    static {
        for (TileType type : TileType.values()) {
            MAPPING.put(type.name().toLowerCase(), type);
        }
    }

    public static TileType get(String tileTypeName) throws IllegalArgumentException {
        String normalized = tileTypeName.toLowerCase().trim();
        TileType type = MAPPING.get(normalized);
        if (type == null) {
            throw new IllegalArgumentException("Unknown tile type: " + tileTypeName);
        }
        return type;
    }

    TileType(boolean solid, boolean walkable) {
        this.solid = solid;
        this.walkable = walkable;
    }

    public boolean isSolid() { return solid; }
    public boolean isWalkable() { return walkable; }
}
