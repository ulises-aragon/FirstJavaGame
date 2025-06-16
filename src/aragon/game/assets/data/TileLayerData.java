package aragon.game.assets.data;

import aragon.game.level.TileLayerType;
import aragon.game.level.TileType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class TileLayerData {
    @NotBlank(message="Tile layer must have a valid name.")
    private String name;

    @NotBlank(message="Tile layer must have a layer type.")
    private String type;

    @NotNull(message="Tile layer must contain data.")
    private List<List<Integer>> data;

    public String getName() { return name; }
    public String getType() { return type; }
    public List<List<Integer>> getData() { return data; }

    public TileLayerType getResolvedType() {
        if (type != null && !type.trim().isEmpty()) {
            return TileLayerType.get(type);
        }
        throw new IllegalArgumentException("No valid tile type for specified type: " + type);
    }

    public boolean isValid() {
        return getResolvedType() != null;
    }
}
