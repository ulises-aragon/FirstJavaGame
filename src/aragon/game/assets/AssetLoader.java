package aragon.game.assets;

import aragon.game.assets.data.*;
import aragon.game.graphics.Animation;
import aragon.game.graphics.Sprite;
import aragon.game.graphics.SpriteSheet;

import aragon.game.level.TileSet;
import aragon.game.main.Game;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.*;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public final class AssetLoader implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger(AssetLoader.class);
    private static volatile AssetLoader instance;

    private static final Map<String, Sprite> sprites = new ConcurrentHashMap<>();
    private static final Map<String, SpriteSheet> spriteSheets = new ConcurrentHashMap<>();
    private static final Map<String, Animation> animations = new ConcurrentHashMap<>();
    private static final Map<String, TileSet> tileSets = new ConcurrentHashMap<>();
    private static final Map<String, LevelData> levelDataRegistry = new ConcurrentHashMap<>();

    private final Game game;
    private AssetManifest assets;
    private final Gson gson;
    private final Validator validator;
    private final ExecutorService executor;

    private final AssetLoadingStats stats = new AssetLoadingStats();

    private AssetLoader(Game game) {
        this.game = game;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        this.executor = Executors.newFixedThreadPool(4);
    }

    public static AssetLoader build(Game game) {
        if (instance == null) {
            synchronized (AssetLoader.class) {
                if (instance == null) {
                    instance = new AssetLoader(game);
                }
            }
        }
        return instance;
    }

    public static AssetLoader get() {
        if (instance == null) {
            throw new IllegalStateException("AssetLoader is not initialized. Do build(game) first.");
        }
        return instance;
    }

    public CompletableFuture<Void> initializeAsync() throws RuntimeException {
        return CompletableFuture.runAsync(() -> {
            try {
                initialize();
            } catch (AssetLoadingException exception) {
                throw new RuntimeException(exception);
            }
        }, executor);
    }

    public void initialize() throws AssetLoadingException {
        long startTime = System.currentTimeMillis();
        stats.reset();

        try {
            LOGGER.info("Asset loading started...");

            loadAssetManifest();
            validateAssetManifest();
            preloadAssetsFromAssetManifest();
            loadTileSets();
            loadLevelData();

            long duration = System.currentTimeMillis() - startTime;
            LOGGER.info("Asset loading finished in {}ms. {}", duration, stats.getSummary());

        } catch (Exception exception) {
            stats.recordFailure();
            throw new AssetLoadingException("Asset loading failed.\n", exception);
        }
    }

    private <T> void loadDataFromDirectory(String directoryPath, String prefix, Class<T> dataType,
                                           AssetValidator<T> validation, AssetProcessor<T> processor) throws AssetLoadingException {
        try {
            InputStream testStream = getClass().getResourceAsStream(directoryPath);
            if (testStream == null) {
                LOGGER.warn("Resource directory not found: {}", directoryPath);
                return;
            }
            testStream.close();

            String[] expectedDataFiles = getExpectedFilesForPath(directoryPath);
            for (String fileName : expectedDataFiles) {
                String fullResourcePath = directoryPath + "/" + fileName;
                String key = generateKeyFromFileName(prefix, fileName);

                try (InputStream stream = getClass().getResourceAsStream(fullResourcePath);
                     InputStreamReader reader = new InputStreamReader(stream)) {

                    if (stream == null) {
                        LOGGER.warn("Expected resource not found: {}", fullResourcePath);
                        continue;
                    }

                    T data = gson.fromJson(reader, dataType);

                    Set<ConstraintViolation<T>> violations = validator.validate(data);
                    if (!violations.isEmpty()) {
                        StringBuilder sb = new StringBuilder("\n\t");
                        for (ConstraintViolation<T> violation : violations) {
                            sb.append("- ").append(violation.getPropertyPath())
                                    .append(": ").append(violation.getMessage()).append("\n\t");
                        }
                        throw new AssetLoadingException(
                                String.format("Invalid %s configuration: %s %s",
                                        dataType.getSimpleName(), key, sb)
                        );
                    }
                    Map.Entry<String, T> entry = Map.entry(key, data);
                    validation.validate(entry);
                    processor.process(entry);
                    LOGGER.info("Loaded {}: {}", dataType.getSimpleName(), key);
                } catch (Exception exception) {
                    stats.recordFailure();
                    throw new AssetLoadingException("Failed to load " + dataType.getSimpleName() + ": " + key, exception);
                }
            }
        } catch (Exception exception) {
            throw new AssetLoadingException("Failed to load " + dataType.getSimpleName() + "s from directory: " + directoryPath, exception);
        }
    }

    private String[] getExpectedFilesForPath(String resourcePath) {
        return switch (resourcePath) {
            case "/data/tileset" -> new String[]{"overworld/default.json"}; // Add more as needed
            case "/data/level" -> new String[]{"overworld/first.json", "overworld/second.json"}; // Add more as needed
            default -> new String[0];
        };
    }

    private <T> void loadDataFromDirectoryAuto(String directoryPath, String prefix, Class<T> dataType,
                                               AssetValidator<T> validation, AssetProcessor<T> processor) throws AssetLoadingException {
        try {
            URI uri = getClass().getResource(directoryPath).toURI();
            Path path;

            if (uri.getScheme().equals("jar")) {
                try {
                    FileSystem fileSystem = FileSystems.getFileSystem(uri);
                    path = fileSystem.getPath(directoryPath);
                } catch(Exception exception) {
                    FileSystem fileSystem = FileSystems.newFileSystem(uri, Map.of());
                    path = fileSystem.getPath(directoryPath);
                }
            } else {
                path = Paths.get(uri);
            }

            try (Stream<Path> paths = Files.walk(path)) {
                Path finalPath = path;
                paths.filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".json"))
                        .forEach(jsonPath -> {
                            try {
                                String relativePath = getRelativePathString(finalPath, jsonPath);
                                String key = generateKeyFromPath(prefix, relativePath);

                                try (InputStream stream = Files.newInputStream(jsonPath);
                                     InputStreamReader reader = new InputStreamReader(stream)) {

                                    T data = gson.fromJson(reader, dataType);

                                    Map.Entry<String, T> entry = Map.entry(key, data);
                                    validation.validate(entry);
                                    processor.process(entry);
                                } catch (Exception exception) {
                                    throw new AssetLoadingException(exception.toString());
                                }
                            } catch(Exception exception) {
                                LOGGER.error("Failed to load {} file: {}", dataType.getSimpleName(), jsonPath, exception);
                                stats.recordFailure();
                            }
                        });
            }
        } catch (Exception exception) {
            throw new AssetLoadingException("Failed to auto-discover " + dataType.getSimpleName() + "s from directory: " + directoryPath, exception);
        }
    }

    private void loadTileSets() throws AssetLoadingException {
        loadDataFromDirectoryAuto("/data/tileset", "tileset", TileSetData.class, this::validateTileSet, this::processTileSet);
    }

    private void loadLevelData() throws AssetLoadingException {
        loadDataFromDirectoryAuto("/data/level", "level", LevelData.class, this::validateLevelData, this::processLevelData);
    }

    private void loadAssetManifest() throws IOException {
        String path = "/data/config/assets.json";
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            throw new IOException("Asset configuration file not found. " + path);
        }

        try (InputStreamReader reader = new InputStreamReader(stream)) {
            assets = gson.fromJson(reader, AssetManifest.class);
            LOGGER.info("Loaded asset configuration. {}", path);
        } catch (JsonSyntaxException exception) {
            throw new IOException("Invalid JSON formation in asset configuration. " + path, exception);
        }
    }

    private void validateAssetManifest() throws AssetLoadingException {
        if (assets.getSpriteSheets() != null) {
            for (Map.Entry<String, SpriteSheetData> entry : assets.getSpriteSheets().entrySet()) {
                Set<ConstraintViolation<SpriteSheetData>> violations = validator.validate(entry.getValue());
                if (!violations.isEmpty()) {
                    StringBuilder sb = new StringBuilder("\n\t");
                    for (ConstraintViolation<SpriteSheetData> violation : violations) {
                        sb.append("- ").append(violation.getPropertyPath())
                                .append(": ").append(violation.getMessage()).append("\n");
                    }
                    throw new AssetLoadingException(
                            String.format("Invalid sprite sheet configuration: %s - %s %s",
                                    entry.getKey(), entry.getValue(), sb
                            )
                    );
                }
            }
        }

        if (assets.getSprites() != null) {
            for (Map.Entry<String, SpriteData> entry : assets.getSprites().entrySet()) {
                String spriteName = entry.getKey();
                SpriteData sprite = entry.getValue();

                Set<ConstraintViolation<SpriteData>> spriteViolations = validator.validate(sprite);
                if (!spriteViolations.isEmpty()) {
                    StringBuilder sb = new StringBuilder("\n\t");
                    for (ConstraintViolation<SpriteData> violation : spriteViolations) {
                        sb.append("- ").append(violation.getPropertyPath())
                                .append(": ").append(violation.getMessage()).append("\n");
                    }
                    throw new AssetLoadingException(
                            String.format("Invalid sprite configuration: %s - %s %s",
                                    spriteName, sprite, sb
                            )
                    );
                }

                if (sprite.getSpriteSheetReference() != null) {
                    Set<ConstraintViolation<SpriteReference>> spriteSheetViolations = validator.validate(sprite.getSpriteSheetReference());
                    if (!spriteSheetViolations.isEmpty()) {
                        StringBuilder sb = new StringBuilder("\n\t");
                        for (ConstraintViolation<SpriteReference> violation : spriteSheetViolations) {
                            sb.append("- ").append(violation.getPropertyPath())
                                    .append(": ").append(violation.getMessage()).append("\n\t");
                        }
                        throw new AssetLoadingException(
                                String.format("Invalid sprite sheet reference configuration: %s - %s %s",
                                        spriteName, sprite.getSpriteSheetReference(), sb
                                )
                        );
                    }
                }

                if (!sprite.isValid()) {
                    throw new AssetLoadingException(
                            String.format("Invalid sprite configuration: %s - %s",
                                    entry.getKey(), sprite));
                }
            }
        }

        if (assets.getRegistries() != null) {
            for (Map.Entry<String, AssetRegistry> entry : assets.getRegistries().entrySet()) {
                String registryName = entry.getKey();
                AssetRegistry registry = entry.getValue();

                if (registry.isEmpty()) {
                    LOGGER.warn("Empty registry: {}", registryName);
                    continue;
                }

                Set<ConstraintViolation<AssetRegistry>> registryViolations = validator.validate(registry);
                if (!registryViolations.isEmpty()) {
                    StringBuilder sb = new StringBuilder("\n\t");
                    for (ConstraintViolation<AssetRegistry> violation : registryViolations) {
                        sb.append("- ").append(violation.getPropertyPath())
                                .append(": ").append(violation.getMessage()).append("\n\t");
                    }
                    throw new AssetLoadingException(
                            String.format("Invalid registry configuration: %s - %s %s",
                                    registryName, registry, sb
                            )
                    );
                }

                if (registry.getSprites() != null) {
                    for (Map.Entry<String, SpriteData> spriteEntry : registry.getSprites().entrySet()) {
                        SpriteData sprite = spriteEntry.getValue();

                        Set<ConstraintViolation<SpriteData>> spriteViolations = validator.validate(sprite);
                        if (!spriteViolations.isEmpty()) {
                            StringBuilder sb = new StringBuilder("\n\t");
                            for (ConstraintViolation<SpriteData> violation : spriteViolations) {
                                sb.append("- ").append(violation.getPropertyPath())
                                        .append(": ").append(violation.getMessage()).append("\n\t");
                            }
                            throw new AssetLoadingException(String.format("Invalid sprite configuration: %s.%s - %s %s",
                                    registryName, spriteEntry.getKey(), sprite, sb
                            ));
                        }

                        if (sprite.getSpriteSheetReference() != null) {
                            Set<ConstraintViolation<SpriteReference>> spriteSheetReferenceViolations = validator.validate(sprite.getSpriteSheetReference());
                            if (!spriteSheetReferenceViolations.isEmpty()) {
                                StringBuilder sb = new StringBuilder("\n\t");
                                for (ConstraintViolation<SpriteReference> violation : spriteSheetReferenceViolations) {
                                    sb.append("- ").append(violation.getPropertyPath())
                                            .append(": ").append(violation.getMessage()).append("\n\t");
                                }
                                throw new AssetLoadingException(
                                        String.format("Invalid sprite sheet reference configuration: %s.%s - %s %s",
                                                registryName, spriteEntry.getKey(), sprite, sb
                                        )
                                );
                            }
                        }

                        if (!sprite.isValid()) {
                            throw new AssetLoadingException(
                                    String.format("Invalid sprite configuration: %s.%s - %s",
                                            registryName, spriteEntry.getKey(), sprite));
                        }
                    }
                }

                if (registry.getAnimations() != null) {
                    for (Map.Entry<String, AnimationData> animationEntry : registry.getAnimations().entrySet()) {
                        try {
                            animationEntry.getValue().validate();
                        } catch(IllegalArgumentException exception) {
                            throw new AssetLoadingException(
                                    String.format("Invalid animation configuration: %s.%s - %s",
                                            registryName, animationEntry.getKey(), exception.getMessage()
                                    )
                            );
                        }
                    }
                }
            }
        }
    }

    private void preloadAssetsFromAssetManifest() throws AssetLoadingException {
        if (assets.getSpriteSheets() != null) {
            for (Map.Entry<String, SpriteSheetData> entry : assets.getSpriteSheets().entrySet()) {
                String spriteSheetName = entry.getKey();
                SpriteSheetData spriteSheetData = entry.getValue();

                try {
                    loadSpriteSheet(spriteSheetName, spriteSheetData);
                } catch (Exception exception) {
                    stats.recordFailure();
                    throw new AssetLoadingException("Failed to load sprite sheet: " + spriteSheetName, exception);
                }
            }
        }

        if (assets.getSprites() != null) {
            for (Map.Entry<String, SpriteData> entry : assets.getSprites().entrySet()) {
                String spriteName = entry.getKey();
                SpriteData spriteData = entry.getValue();

                try {
                    loadSprite(spriteName, spriteData);
                } catch (Exception exception) {
                    stats.recordFailure();
                    throw new AssetLoadingException("Failed to load sprite: " + spriteName, exception);
                }
            }
        }

        if (assets.getRegistries() != null) {
            for (Map.Entry<String, AssetRegistry> entry : assets.getRegistries().entrySet()) {
                String registryName = entry.getKey();
                AssetRegistry assetRegistry = entry.getValue();

                try {
                    loadRegistry(registryName, assetRegistry);
                } catch(Exception exception) {
                    stats.recordFailure();
                    throw new AssetLoadingException("Failed to load registry: " + registryName, exception);
                }
            }
        }
    }

    private void loadRegistry(String registryName, AssetRegistry assetRegistry) throws AssetLoadingException {
        if (assetRegistry.getSpriteSheet() != null) {
            loadSpriteSheet(registryName, assetRegistry.getSpriteSheet());
        }

        if (assetRegistry.getSprites() != null && !assetRegistry.getSprites().isEmpty()) {
            loadSprites(registryName, assetRegistry.getSprites());
        }

        if (assetRegistry.getAnimations() != null && !assetRegistry.getAnimations().isEmpty()) {
            loadAnimations(registryName, assetRegistry.getAnimations());
        }
    }

    private void loadSpriteSheet(String registryName, SpriteSheetData config) throws AssetLoadingException {
        try {
            SpriteSheet spriteSheet = new SpriteSheet(config.getPath());

            Vector2Data spriteSize = config.getSpriteSize();
            spriteSheet.setSpriteSize(spriteSize.getX(), spriteSize.getY());

            Vector2Data offset = config.getOffset();
            spriteSheet.setOffset(offset.getX(), offset.getY());

            spriteSheets.put(registryName, spriteSheet);
            stats.recordSpriteSheetLoaded();

            LOGGER.info("Loaded sprite sheet: {}", registryName);

        } catch (Exception exception) {
            throw new AssetLoadingException("Failed to load SpriteSheet: " + registryName, exception);
        }
    }

    private void loadSprites(String registryName, Map<String, SpriteData> spritesConfig) throws AssetLoadingException {
        for (Map.Entry<String, SpriteData> entry : spritesConfig.entrySet()) {
            String spriteName = entry.getKey();
            SpriteData spriteData = entry.getValue();

            try {
                String key = registryName + "." + spriteName;
                loadSprite(key, spriteData);
            } catch(Exception exception) {
                throw new AssetLoadingException("Failed to load sprite in registry: " + registryName, exception);
            }
        }
    }

    private void loadSprite(String key, SpriteData spriteData) throws AssetLoadingException {
        try {
            Sprite sprite;

            if (spriteData.isStandaloneSprite()) {
                sprite = new Sprite(spriteData.getPath());
            } else if (spriteData.isSheetSprite()) {
                String registryName = key.split("\\.")[0];
                SpriteSheet sheet = spriteSheets.get(registryName);
                if (sheet == null) {
                    throw new AssetLoadingException("No valid sprite sheet found for registry: " + registryName);
                }

                SpriteReference spriteReference = spriteData.getSpriteSheetReference();
                Vector2Data spriteOffset = spriteReference.getOffset();
                if (spriteOffset != null) {
                    sheet.withOffset(spriteOffset.getX(), spriteOffset.getY());
                }

                Vector2Data spritePosition = spriteReference.getPos();
                sprite = sheet.getSprite(spritePosition.getX(), spritePosition.getY());
            } else if (spriteData.isReferencedSheetSprite()) {
                SpriteSheet sheet = spriteSheets.get(spriteData.getPath());
                if (sheet == null) {
                    throw new AssetLoadingException("No valid sprite sheet found for key: " + spriteData.getPath());
                }

                SpriteReference spriteReference = spriteData.getSpriteSheetReference();
                Vector2Data spriteOffset = spriteReference.getOffset();
                if (spriteOffset != null) {
                    sheet.withOffset(spriteOffset.getX(), spriteOffset.getY());
                }

                Vector2Data spritePosition = spriteReference.getPos();
                sprite = sheet.getSprite(spritePosition.getX(), spritePosition.getY());
            } else {
                throw new AssetLoadingException("Invalid sprite configuration: " + key);
            }

            sprites.put(key, sprite);
            stats.recordSpriteLoaded();

            LOGGER.info("Loaded sprite: {}", key);
        } catch(Exception exception) {
            throw new AssetLoadingException("Failed to load sprite: " + key, exception);
        }
    }

    private void loadAnimations(String registryName, Map<String, AnimationData> animationsConfig) throws AssetLoadingException {
        SpriteSheet sheet = spriteSheets.get(registryName);
        if (sheet == null) {
            throw new AssetLoadingException("No sprite sheet found for '" + registryName + "'");
        }

        for (Map.Entry<String, AnimationData> entry : animationsConfig.entrySet()) {
            String animationName = entry.getKey();
            AnimationData animationData = entry.getValue();

            try {
                String key = registryName + "." + animationName;

                Sprite[] frames = new Sprite[animationData.getFrameCount()];
                for (int i=0; i < frames.length; i++) {
                    frames[i] = sheet.getSprite(animationData.getStartCol() + i, animationData.getRow());
                }

                Animation animation = new Animation(frames, animationData.getResolvedPriority(), animationData.getFrameDelay(), animationData.isLooping());
                animations.put(key, animation);
                stats.recordAnimationLoaded();

                LOGGER.info("Loaded animation: {} ({} frames)", key, frames.length);

            } catch (Exception e) {
                throw new AssetLoadingException("Failed to load animation: " + registryName + "." + animationName, e);
            }
        }
    }

    private void validateTileSet(Map.Entry<String, TileSetData> entry) throws AssetLoadingException {
        String key = entry.getKey();
        TileSetData tilesetData = entry.getValue();

        Set<ConstraintViolation<TileSetData>> violations = validator.validate(tilesetData);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder("\n\t");
            for (ConstraintViolation<TileSetData> violation : violations) {
                sb.append("- ").append(violation.getPropertyPath())
                        .append(": ").append(violation.getMessage()).append("\n\t");
            }
            throw new AssetLoadingException(
                    String.format("Invalid tile set configuration: %s %s", key, sb)
            );
        }

        TileSetSpriteReference spriteReference = tilesetData.getSheet();
        Set<ConstraintViolation<TileSetSpriteReference>> spriteViolations = validator.validate(spriteReference);
        if (!spriteViolations.isEmpty()) {
            StringBuilder sb = new StringBuilder("\n\t");
            for (ConstraintViolation<TileSetSpriteReference> violation : spriteViolations) {
                sb.append("- ").append(violation.getPropertyPath())
                        .append(": ").append(violation.getMessage()).append("\n\t");
            }
            throw new AssetLoadingException(
                    String.format("Invalid tile set sprite configuration: %s %s", key, sb)
            );
        }

        if (spriteSheets.get(spriteReference.getPath()) == null) {
            throw new AssetLoadingException(
                    String.format("Invalid sprite sheet path for tile set: %s", key)
            );
        }

        for (int id=0; id<tilesetData.getTiles().size(); id++) {
            TileData tileData = tilesetData.getTiles().get(id);

            Set<ConstraintViolation<TileData>> tileViolations = validator.validate(tileData);
            if (!tileViolations.isEmpty()) {
                StringBuilder sb = new StringBuilder("\n\t");
                for (ConstraintViolation<TileData> violation : tileViolations) {
                    sb.append("- ").append(violation.getPropertyPath())
                            .append(": ").append(violation.getMessage()).append("\n\t");
                }
                throw new AssetLoadingException(
                        String.format("Invalid tile configuration for tile set: %s [%d] %s", key, id, sb)
                );
            }

            if (!tileData.isValid()) {
                throw new AssetLoadingException(
                        String.format("Invalid tile configuration for tile set: %s [%d]", key, id)
                );
            }
        }
    }

    private void processTileSet(Map.Entry<String, TileSetData> entry) {
        String key = entry.getKey();
        TileSetData tilesetData = entry.getValue();

        TileSetSpriteReference spriteReference = tilesetData.getSheet();
        SpriteSheet spriteSheet = spriteSheets.get(spriteReference.getPath());
        Vector2Data tileSize = spriteReference.getTileSize();

        int tileWidth = tileSize != null ? tileSize.getX() : (int) spriteSheet.getSpriteSize().x;
        int tileHeight = tileSize != null ? tileSize.getY() : (int) spriteSheet.getSpriteSize().y;

        TileSet tileset = new TileSet(tilesetData.getName(), spriteSheet, tileWidth, tileHeight);

        for (int id=0; id<tilesetData.getTiles().size(); id++) {
            TileData tileData = tilesetData.getTiles().get(id);
            Vector2Data spriteCoordinates = tileData.getSpriteCoordinates();
            tileset.addNewTile(id, tileData.getResolvedType(), spriteCoordinates.getX(), spriteCoordinates.getY());
        }

        tileSets.put(key, tileset);
        stats.recordTileSetLoaded();

        LOGGER.info("Loaded tile set: {}", key);
    }

    private void validateLevelData(Map.Entry<String, LevelData> entry) throws AssetLoadingException {
        String key = entry.getKey();
        LevelData levelData = entry.getValue();

        Set<ConstraintViolation<LevelData>> levelViolations = validator.validate(levelData);
        if (!levelViolations.isEmpty()) {
            StringBuilder sb = new StringBuilder("\n\t");
            for (ConstraintViolation<LevelData> violation : levelViolations) {
                sb.append("- ").append(violation.getPropertyPath())
                        .append(": ").append(violation.getMessage()).append("\n\t");
            }
            throw new AssetLoadingException(
                    String.format("Invalid level data configuration: %s %s", key, sb)
            );
        }

        if (tileSets.get(levelData.getTileSet()) == null) {
            throw new AssetLoadingException(
                    String.format("Invalid tile set path for level: %s", key)
            );
        }

        for (int index=0; index < levelData.getMap().size(); index++) {
            TileLayerData layer = levelData.getMap().get(index);
            Set<ConstraintViolation<TileLayerData>> layerViolations = validator.validate(layer);
            if (!levelViolations.isEmpty()) {
                StringBuilder sb = new StringBuilder("\n\t");
                for (ConstraintViolation<TileLayerData> violation : layerViolations) {
                    sb.append("- ").append(violation.getPropertyPath())
                            .append(": ").append(violation.getMessage()).append("\n\t");
                }
                throw new AssetLoadingException(
                        String.format("Invalid layer data configuration in level: %s [#%d] %s", key, index, sb)
                );
            }

            if (!layer.isValid()) {
                throw new AssetLoadingException(
                        String.format("Invalid layer type for layer in level: %s [#%d]", key, index)
                );
            }
        }
    }

    private void processLevelData(Map.Entry<String, LevelData> entry) {
        String key = entry.getKey();
        LevelData levelData = entry.getValue();

        levelDataRegistry.put(key, levelData);
        stats.recordLevelDataLoaded();
        LOGGER.info("Loaded level data: {}", key);
    }

    private String generateKeyFromFileName(String prefix, String fileName) {
        return prefix + "." + fileName.replaceAll("\\.json$", "").replace('/', '.');
    }

    private String generateKeyFromPath(String prefix, String relativePath) {
        return prefix + "." + relativePath.replace('/', '.');
    }

    private String getRelativePathString(Path basePath, Path fullPath) {
        return basePath.relativize(fullPath).toString()
                .replace('\\', '/') // Normalize separators
                .replaceAll("\\.json$", ""); // Remove extension
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

    public TileSet getTileSet(String path) {
        TileSet tileSet = tileSets.get(path);
        if (tileSet == null) {
            LOGGER.warn("Tile set not found: {}", path);
        }
        return tileSet;
    }

    public LevelData getLevelData(String path) {
        LevelData levelData = levelDataRegistry.get(path);
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

    public AssetLoadingStats getStats() { return stats; }

    public CompletableFuture<Void> reloadAsync() {
        return CompletableFuture.runAsync(() -> {
            try {
                LOGGER.info("Starting hot reload...");
                cleanup();
                initialize();
                LOGGER.info("Hot reload completed");
            } catch (AssetLoadingException e) {
                LOGGER.fatal("Hot reload failed", e);
                throw new RuntimeException(e);
            }
        }, executor);
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

    public void cleanup() {
        sprites.clear();
        spriteSheets.clear();
        animations.clear();
        tileSets.clear();
        levelDataRegistry.clear();
        assets = null;
        instance = null;
    }

    @Override
    public void close() throws Exception {
        cleanup();
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        instance = null;
        LOGGER.info("AssetManager closed");
    }

    public static class AssetLoadingStats {
        private int spriteSheetsLoaded = 0;
        private int spritesLoaded = 0;
        private int animationsLoaded = 0;
        private int tileSetsLoaded = 0;
        private int levelDataLoaded = 0;
        private int failures = 0;

        void recordSpriteSheetLoaded() { spriteSheetsLoaded++; }
        void recordSpriteLoaded() { spritesLoaded++; }
        void recordAnimationLoaded() { animationsLoaded++; }
        void recordTileSetLoaded() { tileSetsLoaded++; }
        void recordLevelDataLoaded() { levelDataLoaded++; }
        void recordFailure() { failures++; }
        void reset() { spriteSheetsLoaded = spritesLoaded = animationsLoaded = tileSetsLoaded = levelDataLoaded = failures = 0; }

        public String getSummary() {
            return String.format("Loaded: %d sprite sheets, %d sprites, %d animations, %d tile sets, %d level data. Failures: %d",
                    spriteSheetsLoaded, spritesLoaded, animationsLoaded, tileSetsLoaded, levelDataLoaded, failures);
        }
    }
}