package aragon.game.assets.data;

import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.NotBlank;

public class TileSetSpriteReference {
    @NotBlank(message="Sprite reference must contain a valid path.")
    private String path;

    @SerializedName("size")
    private Vector2Data tileSize;

    public String getPath() { return path; }
    public Vector2Data getTileSize() { return tileSize; }
}
