package aragon.game.assets.data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public class SpriteSheetData {
    @NotBlank(message="SpriteSheet path cannot be empty.")
    private String path;

    @Valid
    private Vector2Data spriteSize = new Vector2Data(16, 16);

    @Valid
    private Vector2Data offset = new Vector2Data(0, 0);

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Vector2Data getSpriteSize() { return spriteSize; }
    public void setSpriteSize(Vector2Data spriteSize) {
        this.spriteSize = spriteSize != null ? spriteSize : new Vector2Data(16, 16);
    }

    public Vector2Data getOffset() { return offset; }
    public void setOffset(Vector2Data offset) {
        this.offset = offset != null ? offset : new Vector2Data(0, 0);
    }
}
