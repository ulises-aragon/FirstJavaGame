package aragon.game.assets.data;

import jakarta.validation.Valid;

import java.util.Map;

public class AssetManifest {
    @Valid
    private AutoLoadConfig autoLoad;

    @Valid
    private Map<String, SpriteSheetData> spriteSheets;

    @Valid
    private Map<String, SpriteData> sprites;

    @Valid
    private Map<String, AssetRegistry> registries;

    public AutoLoadConfig getAutoLoadConfig() { return autoLoad; }

    public Map<String, SpriteSheetData> getSpriteSheets() { return spriteSheets; }
    public void setSpriteSheets(Map<String, SpriteSheetData> spriteSheets) { this.spriteSheets = spriteSheets; }

    public Map<String, SpriteData> getSprites() { return sprites; }
    public void setSprites(Map<String, SpriteData> sprites) { this.sprites = sprites; }

    public Map<String, AssetRegistry> getRegistries() { return registries; }
    public void setRegistries(Map<String, AssetRegistry> registries) { this.registries = registries; }
}
