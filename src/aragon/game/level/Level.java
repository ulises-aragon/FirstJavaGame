package aragon.game.level;

import aragon.game.assets.AssetManager;
import aragon.game.assets.data.LevelData;
import aragon.game.assets.data.TileLayerData;
import aragon.game.entity.EntityManager;
import aragon.game.entity.interactable.Door;
import aragon.game.entity.item.Item;
import aragon.game.entity.player.Player;
import aragon.game.entity.statics.Tree;
import aragon.game.main.states.State;
import aragon.game.util.Vector2;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Level {
    private static final AssetManager ASSET_MANAGER = AssetManager.get();

    private final State gameState;
    private int width, height;
    private TileSet tileSet;
    private final List<TileLayer> layers;

    private final EntityManager entityManager;

    public Level(State gameState) {
        this.gameState = gameState;
        int tileSize = gameState.getGame().getTileSize();
        layers = new ArrayList<>();
        entityManager = new EntityManager(this);

        entityManager.addEntity(new Player(entityManager, 64));
        entityManager.addEntity(new Tree(entityManager, 11*tileSize, 5*tileSize));
        entityManager.addEntity(new Item(entityManager, "garden.key", 9*tileSize, 10*tileSize));
        entityManager.addEntity(new Item(entityManager, "garden.key", 7*tileSize, 12*tileSize));
        entityManager.addEntity(new Item(entityManager, "garden.key", 9*tileSize, 14*tileSize));
        entityManager.addEntity(new Item(entityManager, "garden.key", 7*tileSize, 16*tileSize));
        entityManager.addEntity(new Item(entityManager, "garden.key", 9*tileSize, 18*tileSize));
        entityManager.addEntity(new Door(entityManager, "garden.door_open_horizontal", "garden.door", 16*tileSize, 8*tileSize, false, true, Vector2.yAxis));
        entityManager.addEntity(new Door(entityManager, "garden.door_open_horizontal", "garden.door", 16*tileSize, 9*tileSize, false, true, Vector2.yAxis));
        entityManager.addEntity(new Door(entityManager, "garden.door_open_horizontal", "garden.door", 23*tileSize, 7*tileSize, true, true,Vector2.yAxis));
        entityManager.addEntity(new Door(entityManager, "garden.door_open_vertical", "garden.door", 19*tileSize, 4*tileSize, true, true, Vector2.xAxis));
    }

    public Level(State gameState, LevelData levelData) {
        this(gameState);
        loadLevelData(levelData);
    }

    public void loadLevelData(LevelData levelData) {
        if (levelData == null) return;
        layers.clear();

        width = levelData.getWidth();
        height = levelData.getHeight();

        int spawnX, spawnY;

        if (levelData.getSpawnPoint() != null) {
            spawnX = levelData.getSpawnPoint().getX();
            spawnY = levelData.getSpawnPoint().getY();
        } else {
            spawnX = 0;
            spawnY = 0;
        }

        tileSet = ASSET_MANAGER.getTileSet(levelData.getTileSet());

        for (TileLayerData layerData : levelData.getMap()) {
            createLayer(layerData.getName(), layerData.getResolvedType(), layerData.getData());
        }

        entityManager.getPlayer().position = new Vector2(spawnX, spawnY);
    }

    public State getGameState() {
        return gameState;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public TileSet getTileSet() {
        return tileSet;
    }

    private void registerLayer(TileLayer layer) {
        layers.add(layer);
        Collections.sort(layers);
    }

    public void addLayer(String name, TileLayerType type) {
        if (tileSet == null) return;
        TileLayer newLayer = new TileLayer(name, width, height, type, tileSet);
        registerLayer(newLayer);
    }

    public void createLayer(String name, TileLayerType type, List<List<Integer>> map) {
        if (tileSet == null) return;
        TileLayer newLayer = new TileLayer(name, width, height, type, tileSet);
        registerLayer(newLayer);

        for (int y=0; y < map.size(); y++) {
            List<Integer> row = map.get(y);
            for (int x=0; x < row.size(); x++) {
                newLayer.setTileId(x, y, row.get(x));
            }
        }
    }

    public TileLayer getLayer(String name) {
        if (tileSet == null) return null;
        return layers.stream().filter(layer -> name.equals(layer.getName())).findFirst().orElse(null);
    }

    public TileLayer getLayerByType(TileLayerType layerType) {
        if (tileSet == null) return null;
        return layers.stream().filter(layer -> layerType.equals(layer.getLayerType())).findFirst().orElse(null);
    }

    private void clearLayers() {
        layers.clear();
    }

    public Tile getTileAt(int x, int y, TileLayerType type) {
        if (tileSet == null) return null;
        TileLayer layer = getLayerByType(type);
        if (layer == null) return null;
        return tileSet.getTile(layer.getTileId(x, y));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void update() {
        entityManager.update();
    }

    public void render(Graphics graphics) {
        for (TileLayer layer : layers) {
            layer.render(graphics, this);
        }
        entityManager.render(graphics);
    }
}
