package aragon.game.assets.data;

import aragon.game.level.TileType;
import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TileData {
    @NotNull(message="Tile must have a valid sprite coordinate.")
    @SerializedName("sprite")
    private Vector2Data spriteCoordinates;

    @NotBlank(message ="Tile must have a tile type.")
    private String type;

    public Vector2Data getSpriteCoordinates() { return spriteCoordinates; }
    public String getType() { return type; }

    public TileType getResolvedType() {
        if (type != null && !type.trim().isEmpty()) {
            return TileType.get(type);
        }
        throw new IllegalArgumentException("No valid tile type for specified type: " + type);
    }

    public boolean isValid() {
        return getResolvedType() != null;
    }
}
