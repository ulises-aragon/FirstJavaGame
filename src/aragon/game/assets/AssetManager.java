package aragon.game.assets;

import aragon.game.assets.data.LevelData;
import aragon.game.graphics.Animation;
import aragon.game.graphics.Sprite;
import aragon.game.graphics.SpriteSheet;
import aragon.game.level.TileSet;
import aragon.game.main.Game;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class AssetManager {
    private static volatile AssetManager instance;
    private final Logger LOGGER = LogManager.getLogger(AssetManager.class);

    private final Map<String, Sprite> sprites = new ConcurrentHashMap<>();
    private final Map<String, SpriteSheet> spriteSheets = new ConcurrentHashMap<>();
    private final Map<String, Animation> animations = new ConcurrentHashMap<>();
    private final Map<String, TileSet> tileSets = new ConcurrentHashMap<>();
    private final Map<String, LevelData> levelDataMap = new ConcurrentHashMap<>();

    private AssetManager() {
        LOGGER.info("Instantiated new singleton.");
    }

    public static AssetManager build() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }

    public static AssetManager get() {
        if (instance == null) {
            throw new IllegalStateException("AssetManager is not initialized. Do build() first.");
        }
        return instance;
    }

    public void registerSpriteSheet(String key, SpriteSheet sheet) {
        spriteSheets.put(key, sheet);
        LOGGER.info("Registered sprite sheet: {}", key);
    }

    public void registerSprite(String key, Sprite sprite) {
        sprites.put(key, sprite);
        LOGGER.info("Registered sprite: {}", key);
    }

    public void registerAnimation(String key, Animation animation) {
        animations.put(key, animation);
        LOGGER.info("Registered animation: {} ({} frames)", key, animation.getFrameCount());
    }

    public void registerTileSet(String key, TileSet tileSet) {
        tileSets.put(key, tileSet);
        LOGGER.info("Registered tile set: {}", key);
    }

    public void registerLevelData(String key, LevelData levelData) {
        levelDataMap.put(key, levelData);
        LOGGER.info("Registered level data: {}", key);
    }

    public TileSet getTileSet(String path) {
        TileSet tileSet = tileSets.get(path);
        if (tileSet == null) {
            LOGGER.warn("Tile set not found: {}", path);
        }
        return tileSet;
    }

    public LevelData getLevelData(String path) {
        LevelData levelData = levelDataMap.get(path);
        if (levelData == null) {
            LOGGER.warn("Level data not found: {}", path);
        }
        return levelData;
    }

    public Animation getAnimation(String path) {
        Animation animation = animations.get(path);
        if (animation == null) {
            LOGGER.warn("Animation not found: {}", path);
        }
        return animation;
    }

    public Sprite getSprite(String path) {
        Sprite sprite = sprites.get(path);
        if (sprite == null) {
            LOGGER.warn("Sprite not found: {}", path);
            return createPlaceholderSprite();
        }
        return sprite;
    }

    public Sprite getSprite(String path, int x, int y) {
        return getSpriteSheetOptional(path)
                .map(sheet -> sheet.getSprite(x, y))
                .orElseGet(() -> {
                    LOGGER.warn("Sprite sheet not found: {}", path);
                    return createPlaceholderSprite();
                });
    }

    public Optional<SpriteSheet> getSpriteSheetOptional(String path) {
        return Optional.ofNullable(spriteSheets.get(path));
    }

    public SpriteSheet getSpriteSheet(String path) {
        return getSpriteSheetOptional(path)
                .orElseThrow(() -> new IllegalArgumentException("Sprite sheet not found: " + path));
    }

    public Optional<Animation> getAnimationOptional(String path) {
        return Optional.ofNullable(animations.get(path));
    }

    private Sprite createPlaceholderSprite() {
        BufferedImage placeholder = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                placeholder.setRGB(x, y, (x == y) ? 0xFF00FF : 0x000000);
            }
        }
        return new Sprite(placeholder);
    }

    public void clear() {
        sprites.clear();
        spriteSheets.clear();
        animations.clear();
        tileSets.clear();
        levelDataMap.clear();
    }
}
