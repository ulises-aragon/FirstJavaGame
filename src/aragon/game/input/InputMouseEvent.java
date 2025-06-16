package aragon.game.input;

import aragon.game.util.Vector2D;

public class InputMouseEvent {
    private final Vector2D position;
    private final Vector2D delta;
    private final int scrollDelta;
    private final long timestamp;

    public InputMouseEvent(Vector2D position, Vector2D delta, int scrollDelta) {
        this.position = position;
        this.delta = delta;
        this.scrollDelta = scrollDelta;
        this.timestamp = System.currentTimeMillis();
    }

    public Vector2D getPosition() { return position; }
    public Vector2D getDelta() { return position; }
    public int getScrollDelta() { return scrollDelta; }
    public long getTimestamp() { return timestamp; }
}
