package aragon.game.input;

import aragon.game.util.Vector2;

public interface InputMouseEventListener {
    default void onMouseMoved(Vector2 position, Vector2 delta) {}
    default void onMouseScrolled(int scrollDelta) {}
}
