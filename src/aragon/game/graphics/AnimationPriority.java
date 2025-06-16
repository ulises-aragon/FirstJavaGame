package aragon.game.graphics;

import java.util.HashMap;
import java.util.Map;

public enum AnimationPriority {
    CORE(0),
    IDLE(100),
    MOVEMENT(200),
    ACTION(300),
    ACTION2(400),
    ACTION3(500);

    private static final Map<String, AnimationPriority> mapping = new HashMap<>();
    private final int priority;

    static {
        for (AnimationPriority priority : AnimationPriority.values()) {
            mapping.put(priority.name(), priority);
        }
    }

    AnimationPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() { return priority; }

    public static AnimationPriority get(String stringPriority) throws IllegalArgumentException {
        String normalized = stringPriority.toUpperCase().trim();
        AnimationPriority priority = mapping.get(normalized);
        if (priority == null) {
            throw new IllegalArgumentException("Unknown animation priority: " + stringPriority);
        }
        return priority;
    }
}
