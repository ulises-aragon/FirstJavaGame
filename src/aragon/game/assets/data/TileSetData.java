package aragon.game.assets.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class TileSetData {
    @NotBlank(message="TileSet must have a valid name.")
    private String name;

    @NotNull(message="TileSet must have a sprite reference.")
    private TileSetSpriteReference sheet;

    @NotNull(message="TileSet must contain a tile list.")
    @NotEmpty(message="TileSet must contain one or more tiles.")
    private List<TileData> tiles;

    public String getName() { return name; }
    public TileSetSpriteReference getSheet() { return sheet; }
    public List<TileData> getTiles() { return tiles; }
}
