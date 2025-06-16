package aragon.game.main;

import aragon.game.assets.AssetLoader;
import aragon.game.input.InputManager;
import aragon.game.level.Level;

public class GameHandler {
    private Game game;
    private Level level;

    public GameHandler(Game game) {
        this.game = game;
    }

    public Game getGame() { return game; }
    public Level getLevel() { return level; }
    public void setLevel(Level level) { this.level = level; }
    public int getTileSize() { return game.getTileSize(); }
    public int getScreenWidth() { return game.getScreenWidth(); }
    public int getScreenHeight() { return game.getScreenHeight(); }
    public GameCamera getCamera() { return game.getCamera(); }
    public InputManager getInputManager() { return game.getInputManager(); }
    public AssetLoader getAssetLoader() { return game.getAssetLoader(); }
}
