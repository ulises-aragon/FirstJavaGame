package aragon.game.main.states;

import aragon.game.entity.player.Player;
import aragon.game.level.Level;
import aragon.game.main.GameHandler;

import java.awt.Graphics;

public class GameState extends State {
    Level level;
    Player player;

    public GameState(GameHandler handler) {
        super(handler);
        level = new Level(handler, handler.getAssetLoader().getLevelData("level.overworld.second"));
        handler.setLevel(level);
        player = new Player(handler,50, 50, handler.getTileSize());
    }

    @Override
    public void update() {
        level.update();
        player.update();
        handler.getCamera().setCameraSubject(player);
    }

    @Override
    public void render(Graphics graphics) {
        level.render(graphics);
        player.render(graphics);
    }
}
