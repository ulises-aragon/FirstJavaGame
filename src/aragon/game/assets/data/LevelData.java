package aragon.game.assets.data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class LevelData {
    @Min(value=0, message="Level must have a positive width.")
    private int width;

    @Min(value=0, message="Level must have a positive height.")
    private int height;

    @NotBlank(message="Level must have a tile set reference.")
    private String tileSet;

    private Vector2Data spawnPoint;

    @NotEmpty(message="Level must have a map.")
    private List<TileLayerData> map;

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Vector2Data getSpawnPoint() { return spawnPoint; }
    public String getTileSet() { return tileSet; }
    public List<TileLayerData> getMap() { return map; }
}
