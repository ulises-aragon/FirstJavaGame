package aragon.game.main.states;

import aragon.game.level.Level;
import aragon.game.main.Game;

import java.awt.Graphics;

public abstract class State {
    private static State currentState = null;
    public static void setState(State state) {
        currentState = state;
    }
    public static State getState() {
        return currentState;
    }

    protected Game game;
    protected Level level;

    public State(Game game) { this.game = game; }

    public Level getLevel() { return level; }
    public Game getGame() { return game; }
    public abstract void update();
    public abstract void render(Graphics graphics);
}
