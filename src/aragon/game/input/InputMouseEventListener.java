package aragon.game.input;

import aragon.game.util.Vector2D;

public interface InputMouseEventListener {
    default void onMouseMoved(Vector2D position, Vector2D delta) {}
    default void onMouseScrolled(int scrollDelta) {}
}
