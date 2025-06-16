package aragon.game.assets.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SpriteReference {
    @NotNull(message="Sprite must contain sheet coordinate.")
    private Vector2Data pos;

    private Vector2Data offset;

    public Vector2Data getPos() { return pos; }
    public Vector2Data getOffset() { return offset; }
}
