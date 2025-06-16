package aragon.game.main.states;

import java.awt.Graphics;

public abstract class State {
    private static State currentState = null;
    public static void setState(State state) {
        currentState = state;
    }
    public static State getState() {
        return currentState;
    }

    protected aragon.game.main.GameHandler handler;

    public State(aragon.game.main.GameHandler handler) { this.handler = handler; }

    public abstract void update();
    public abstract void render(Graphics graphics);
}
