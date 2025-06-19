package aragon.game.input;

import aragon.game.util.Vector2;

public class InputMouseEvent {
    private final Vector2 position;
    private final Vector2 delta;
    private final int scrollDelta;
    private final long timestamp;

    public InputMouseEvent(Vector2 position, Vector2 delta, int scrollDelta) {
        this.position = position;
        this.delta = delta;
        this.scrollDelta = scrollDelta;
        this.timestamp = System.currentTimeMillis();
    }

    public Vector2 getPosition() { return position; }
    public Vector2 getDelta() { return delta; }
    public int getScrollDelta() { return scrollDelta; }
    public long getTimestamp() { return timestamp; }
}
