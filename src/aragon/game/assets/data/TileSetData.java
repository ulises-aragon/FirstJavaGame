package aragon.game.assets.data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class TileSetData {
    @NotNull(message="TileSet must have a sprite reference.")
    private TileSetSpriteReference sheet;

    @NotNull(message="TileSet must contain a tile list.")
    @NotEmpty(message="TileSet must contain one or more tiles.")
    private List<TileData> tiles;

    public TileSetSpriteReference getSheet() { return sheet; }
    public List<TileData> getTiles() { return tiles; }
}
