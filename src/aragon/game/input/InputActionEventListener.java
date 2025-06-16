package aragon.game.input;

public interface InputActionEventListener {
    default void onActionTriggered(String actionName) {}
    default void onActionReleased(String actionName) {}
    default void onActionHeld(String actionName, long holdDuration) {}
}
