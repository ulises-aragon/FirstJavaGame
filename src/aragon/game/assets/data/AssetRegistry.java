package aragon.game.assets.data;

import jakarta.validation.Valid;

import java.util.Map;

public class AssetRegistry {
    @Valid
    private SpriteSheetData spriteSheet;

    @Valid
    private Map<String, SpriteData> sprites;

    @Valid
    private Map<String, AnimationData> animations;

    public SpriteSheetData getSpriteSheet() { return spriteSheet; }
    public void setSpriteSheet(SpriteSheetData spriteSheet) { this.spriteSheet = spriteSheet; }

    public Map<String, SpriteData> getSprites() { return sprites; }
    public void setSprites(Map<String, SpriteData> sprites) { this.sprites = sprites; }

    public Map<String, AnimationData> getAnimations() { return animations; }
    public void setAnimations(Map<String, AnimationData> animations) { this.animations = animations; }

    public boolean isEmpty() {
        return spriteSheet == null &&
                (sprites == null || sprites.isEmpty()) &&
                (animations == null || animations.isEmpty());
    }
}
