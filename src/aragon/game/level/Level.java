package aragon.game.level;

import aragon.game.assets.AssetLoader;
import aragon.game.assets.data.LevelData;
import aragon.game.assets.data.TileLayerData;
import aragon.game.main.GameHandler;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Level {
    private static final AssetLoader ASSET_LOADER = AssetLoader.get();

    private final GameHandler gameHandler;
    private int width, height;
    private TileSet tileSet;
    private final List<TileLayer> layers;

    public Level(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
        this.layers = new ArrayList<>();
    }

    public Level(GameHandler gameHandler, LevelData levelData) {
        this(gameHandler);
        loadLevelData(levelData);
    }

    public void loadLevelData(LevelData levelData) {
        if (levelData == null) return;
        layers.clear();

        width = levelData.getWidth();
        height = levelData.getHeight();

        tileSet = ASSET_LOADER.getTileSet(levelData.getTileSet());

        for (TileLayerData layerData : levelData.getMap()) {
            createLayer(layerData.getName(), layerData.getResolvedType(), layerData.getData());
        }
    }

    public TileSet getTileSet() {
        return tileSet;
    }

    private void setTileSet(TileSet tileSet) {
        this.tileSet = tileSet;
        layers.clear();
    }

    private void registerLayer(TileLayer layer) {
        layers.add(layer);
        Collections.sort(layers);
    }

    public void addLayer(String name, TileLayerType type) {
        TileLayer newLayer = new TileLayer(name, width, height, type, tileSet);
        registerLayer(newLayer);
    }

    public void createLayer(String name, TileLayerType type, List<List<Integer>> map) {
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
        return layers.stream().filter(layer -> name.equals(layer.getName())).findFirst().orElse(null);
    }

    private void clearLayers() {
        layers.clear();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void update() {

    }

    public void render(Graphics graphics) {
        for (TileLayer layer : layers) {
            layer.render(graphics, gameHandler);
        }
    }
}
