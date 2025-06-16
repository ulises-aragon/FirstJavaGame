package aragon.game.input;

public class InputActionEvent {
    private final String actionName;
    private final InputActionEventType eventType;
    private Input triggeringInput;
    private final long timestamp;

    public InputActionEvent(String actionName, long timestamp, InputActionEventType eventType, Input triggeringInput) {
        this(actionName, timestamp, eventType);
        this.triggeringInput = triggeringInput;
    }

    public InputActionEvent(String actionName, long timestamp, InputActionEventType eventType) {
        this.actionName = actionName;
        this.timestamp = timestamp;
        this.eventType = eventType;
    }

    public String getActionName() { return actionName; }
    public long getTimestamp() { return timestamp; }
    public InputActionEventType getEventType() { return eventType; }
    public Input getTriggeringInput() { return triggeringInput; }
}
