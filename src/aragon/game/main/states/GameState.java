package aragon.game.main.states;

import aragon.game.level.Level;
import aragon.game.main.Game;

import java.awt.Graphics;

public class GameState extends State {
    public GameState(Game game) {
        super(game);
        level = new Level(this, game.getAssetManager().getLevelData("overworld.test"));
    }

    @Override
    public void update() {
        level.update();
    }

    @Override
    public void render(Graphics graphics) {
        level.render(graphics);
    }
}
