package aragon.game.input;

import aragon.game.util.Vector2D;
import aragon.game.main.Game;

import java.awt.event.*;
import java.util.*;

public final class InputManager implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
    private static InputManager instance;

    // Static key and mouse containers.
    private static final int MAX_KEYS = 512;
    private static final int MAX_MOUSE_BUTTONS = 10;
    private final boolean[] keys = new boolean[MAX_KEYS];
    private final boolean[] previousKeys = new boolean[MAX_KEYS];
    private final boolean[] buttons = new boolean[MAX_MOUSE_BUTTONS];
    private final boolean[] previousButtons = new boolean[MAX_MOUSE_BUTTONS];

    // Mouse management.
    private Vector2D lastMousePosition = Vector2D.zero;
    private Vector2D mousePosition = Vector2D.zero;
    private int scrollDelta = 0;

    // Input buffer.
    private final Queue<InputActionEvent> inputBuffer = new LinkedList<>();
    private static final int DEFAULT_BUFFER_TIME_MS = 150;
    private int bufferTimeMs = DEFAULT_BUFFER_TIME_MS;
    private boolean bufferingEnabled = true;

    private final Map<String, Boolean> previousStates = new HashMap<>();

    // Events
    private final List<InputActionEventListener> globalInputListeners = new ArrayList<>();
    private final Map<String, List<InputActionEventListener>> eventListeners = new HashMap<>();
    private final Map<String, List<InputActionEventListener>> categoryListeners = new HashMap<>();
    private final List<InputMouseEventListener> mouseEventListeners = new ArrayList<>();

    private final Map<String, Long> lastHoldEventTime = new HashMap<>();
    private static final long HOLD_EVENT_INTERVAL = 100;

    private final Queue<InputActionEvent> eventQueue = new LinkedList<>();
    private boolean defferedEventProcessing = false;

    // Action management.
    private final Map<String, Set<InputAction>> inputCategories = new HashMap<>();
    private final Map<String, InputAction> inputActions = new HashMap<>();
    private final Map<Input, Set<String>> inputToActions = new HashMap<>();

    private final Map<String, Long> actionPressTime = new HashMap<>();

    private final Game game;

    private InputManager(Game game) {
        this.game = game;
        game.getDisplay().getCanvas().addKeyListener(this);
        game.getDisplay().getCanvas().addMouseListener(this);
        game.getDisplay().getCanvas().addMouseMotionListener(this);
        game.getDisplay().getCanvas().addMouseWheelListener(this);
    }

    public static InputManager build(Game game) {
        if (instance == null) {
            instance = new InputManager(game);
        }
        return instance;
    }

    public static InputManager getManager() {
        if (instance == null) {
            throw new IllegalStateException("InputManager is not initialized. Do getInstance(game) first.");
        }
        return instance;
    }

    // InputAction management.
    public void createCategory(String categoryName) {
        inputCategories.put(categoryName, new HashSet<>());
    }

    public void removeCategory(String categoryName) {
        inputCategories.remove(categoryName);
    }

    public InputAction bind(String actionName) {
        InputAction action = new InputAction(actionName);
        inputActions.put(actionName, action);
        return action;
    }

    public InputAction bind(String actionName, Input... inputs) {
        InputAction action = bind(actionName);
        for (Input input : inputs) {
            action.addInput(input);
        }
        return action;
    }

    public InputAction bind(String actionName, int... keyCodes) {
        InputAction action = bind(actionName);
        for (int keyCode : keyCodes) {
            action.addKeyCode(keyCode);
        }
        return action;
    }

    public InputAction bind(String categoryName, String actionName) {
        Set<InputAction> category = inputCategories.computeIfAbsent(categoryName, k -> { return new HashSet<>(); });
        InputAction action = bind(actionName);
        category.add(action);
        return action;
    }

    public InputAction bind(String categoryName, String actionName, Input... inputs) {
        InputAction action = bind(categoryName, actionName);
        for (Input input : inputs) {
            action.addInput(input);
        }
        return action;
    }

    public InputAction bind(String categoryName, String actionName, int... keyCodes) {
        InputAction action = bind(categoryName, actionName);
        for (int keyCode : keyCodes) {
            action.addKeyCode(keyCode);
        }
        return action;
    }

    public boolean addInputToAction(String actionName, Input input) {
        InputAction action = inputActions.get(actionName);
        if (action == null) return false;

        return addInputToAction(action, input);
    }

    public boolean addInputToAction(InputAction action, Input input) {
        action.addInput(input);
        inputToActions.computeIfAbsent(input, k -> new HashSet<>()).add(action.getName());
        return true;
    }

    public boolean removeInputFromAction(String actionName, Input input) {
        InputAction action = inputActions.get(actionName);
        if (action == null) return false;
        return removeInputFromAction(action, input);
    }

    public boolean removeInputFromAction(InputAction action, Input input) {
        action.removeInput(input);
        Set<String> actionsForInput = inputToActions.get(input);
        if (actionsForInput != null) {
            actionsForInput.remove(action.getName());
            if (actionsForInput.isEmpty()) {
                inputToActions.remove(input);
            }
        }
        return true;
    }

    public void clearActionInputs(String actionName) {
        InputAction action = inputActions.get(actionName);
        if (action == null) return;
        clearActionInputs(action);
    }

    public void clearActionInputs(InputAction action) {
        for (Input input : action.getBoundInputs()) {
            Set<String> actionsForInput = inputToActions.get(input);
            if (actionsForInput != null) {
                actionsForInput.remove(action.getName());
                if (actionsForInput.isEmpty()) {
                    inputToActions.remove(input);
                }
            }
        }
        action.clearInputs();
    }

    public void reassignAction(String actionName, Input... inputs) {
        clearActionInputs(actionName);
        for (Input input : inputs) {
            addInputToAction(actionName, input);
        }
    }

    public void reassignAction(InputAction action, Input... inputs) {
        clearActionInputs(action);
        for (Input input : inputs) {
            addInputToAction(action, input);
        }
    }

    public Set<String> getCurrentlyHeldActions() { return new HashSet<>(actionPressTime.keySet()); }

    public InputAction getAction(String actionName) {
        return inputActions.get(actionName);
    }

    public Set<String> getActionNames() {
        return new HashSet<>(inputActions.keySet());
    }

    public Set<String> getActionsForInput(Input input) {
        return inputToActions.getOrDefault(input, new HashSet<>());
    }

    public Set<InputAction> getActionsForCategory(String categoryName) { return inputCategories.getOrDefault(categoryName, new HashSet<>()); }

    // Event management.
    public void addGlobalInputListener(InputActionEventListener listener) {
        globalInputListeners.add(listener);
    }

    public void removeGlobalInputListener(InputActionEventListener listener) {
        globalInputListeners.remove(listener);
    }

    public void addActionListener(String actionName, InputActionEventListener listener) {
        eventListeners.computeIfAbsent(actionName, k -> new ArrayList<>()).add(listener);
    }

    public void removeActionListener(String actionName, InputActionEventListener listener) {
        List<InputActionEventListener> listeners = eventListeners.get(actionName);
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                eventListeners.remove(actionName);
            }
        }
    }

    public void addCategoryListener(String categoryName, InputActionEventListener listener) {
        categoryListeners.computeIfAbsent(categoryName, k -> new ArrayList<>()).add(listener);
    }

    public void removeCategoryListener(String categoryName, InputActionEventListener listener) {
        List<InputActionEventListener> listeners = categoryListeners.get(categoryName);
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                categoryListeners.remove(categoryName);
            }
        }
    }

    public void addMouseListener(InputMouseEventListener listener) {
        mouseEventListeners.add(listener);
    }

    public void removeMouseListener(InputMouseEventListener listener) {
        mouseEventListeners.remove(listener);
    }

    public void setDefferedEventProcessing(boolean enabled) {
        defferedEventProcessing = enabled;
        if (!enabled) {
            eventQueue.clear();
        }
    }

    public void processQueuedEvents() {
        while (!eventQueue.isEmpty()) {
            InputActionEvent event = eventQueue.poll();
            fireInputEvent(event);
        }
    }

    private void fireActionPressed(String actionName, Input triggeringInput) {
        InputActionEvent event = new InputActionEvent(actionName, System.currentTimeMillis(), InputActionEventType.PRESSED, triggeringInput);

        if (defferedEventProcessing) {
            eventQueue.offer(event);
        } else {
            fireInputEvent(event);
        }
    }

    private void fireActionReleased(String actionName, Input triggeringInput) {
        InputActionEvent event = new InputActionEvent(actionName, System.currentTimeMillis(), InputActionEventType.RELEASED, triggeringInput);

        if (defferedEventProcessing) {
            eventQueue.offer(event);
        } else {
            fireInputEvent(event);
        }
    }

    private void fireActionHeld(String actionName, long holdDuration) {
        for (InputActionEventListener listener : globalInputListeners) {
            listener.onActionHeld(actionName, holdDuration);
        }

        List<InputActionEventListener> specificListeners = eventListeners.get(actionName);
        if (specificListeners != null) {
            for (InputActionEventListener listener : specificListeners) {
                listener.onActionHeld(actionName, holdDuration);
            }
        }

        for (Map.Entry<String, Set<InputAction>> entry : inputCategories.entrySet()) {
            if (entry.getValue().stream().anyMatch(action -> action.getName().equals(actionName))) {
                List<InputActionEventListener> categoryListeners = this.categoryListeners.get(entry.getKey());
                if (categoryListeners != null) {
                    for (InputActionEventListener listener : categoryListeners) {
                        listener.onActionHeld(actionName, holdDuration);
                    }
                }
            }
        }
    }

    private void fireInputEvent(InputActionEvent event) {
        String actionName = event.getActionName();

        for (InputActionEventListener listener : globalInputListeners) {
            if (event.getEventType() == InputActionEventType.PRESSED) {
                listener.onActionTriggered(actionName);
            } else {
                listener.onActionReleased(actionName);
            }
        }

        List<InputActionEventListener> specificListeners = eventListeners.get(actionName);
        if (specificListeners != null) {
            for (InputActionEventListener listener : specificListeners) {
                if (event.getEventType() == InputActionEventType.PRESSED) {
                    listener.onActionTriggered(actionName);
                } else {
                    listener.onActionReleased(actionName);
                }
            }
        }

        for (Map.Entry<String, Set<InputAction>> entry : inputCategories.entrySet()) {
            if (entry.getValue().stream().anyMatch(action -> action.getName().equals(actionName))) {
                List<InputActionEventListener> categoryListeners = this.categoryListeners.get(entry.getKey());
                if (categoryListeners != null) {
                    for (InputActionEventListener listener : categoryListeners) {
                        if (event.getEventType() == InputActionEventType.PRESSED) {
                            listener.onActionTriggered(actionName);
                        } else {
                            listener.onActionReleased(actionName);
                        }
                    }
                }
            }
        }
    }

    private void fireInputMouseEvent(InputMouseEvent mouseEvent) {
        for (InputMouseEventListener listener : mouseEventListeners) {
            if (mouseEvent.getDelta().magnitude() > 0) {
                listener.onMouseMoved(mouseEvent.getPosition(), mouseEvent.getDelta());
            }

            if (mouseEvent.getScrollDelta() != 0) {
                listener.onMouseScrolled(mouseEvent.getScrollDelta());
            }
        }
    }

    private Input findTriggeringInput(String actionName) {
        InputAction action = inputActions.get(actionName);
        if (action == null) return null;

        for (Input input : action.getBoundInputs()) {
            if (isInputTriggered(input)) {
                return input;
            }
        }

        return null;
    }

    // Buffer management.
    public void setBufferTime(int milliseconds) {
        bufferTimeMs = Math.max(0, milliseconds);
    }

    public int getBufferTime() {
        return bufferTimeMs;
    }

    public void setBufferingEnabled(boolean enabled) {
        bufferingEnabled = enabled;
        if (!bufferingEnabled) {
            clearInputBuffer();
        }
    }

    public void clearInputBuffer() {
        inputBuffer.clear();
    }

    public Queue<InputActionEvent> getBufferedInputEvents() {
        return new LinkedList<>(inputBuffer);
    }

    // Convenience methods.
    public boolean addKeyCodeToAction(String actionName, int keyCode) {
        return addInputToAction(actionName, Input.keyboard(keyCode));
    }

    public boolean addKeyCodeToAction(InputAction action, int keyCode) {
        return addInputToAction(action, Input.keyboard(keyCode));
    }

    public boolean addMouseButtonToAction(String actionName, int mouseButton) {
        return addInputToAction(actionName, Input.mouse(mouseButton));
    }

    public boolean addMouseButtonToAction(InputAction action, int mouseButton) {
        return addInputToAction(action, Input.mouse(mouseButton));
    }

    public boolean removeKeyCodeFromAction(String actionName, int keyCode) {
        return removeInputFromAction(actionName, Input.keyboard(keyCode));
    }

    public boolean removeKeyCodeFromAction(InputAction action, int keyCode) {
        return removeInputFromAction(action, Input.keyboard(keyCode));
    }

    public Set<String> getActionsForKeyCode(int keyCode) {
        return getActionsForInput(Input.keyboard(keyCode));
    }

    public Set<String> getActionsForMouseButton(int mouseButton) {
        return getActionsForInput(Input.mouse(mouseButton));
    }

    // InputAction methods.
    public boolean isActionHeld(String actionName) {
        InputAction action = inputActions.get(actionName);
        if (action == null) return false;
        return isActionHeld(action);
    }

    public boolean isActionHeld(InputAction action) {
        for (Input input : action.getBoundInputs()) {
            if (isInputHeld(input)) {
                return true;
            }
        }

        return false;
    }

    public boolean isActionTriggered(String actionName) {
        InputAction action = inputActions.get(actionName);
        if (action == null) return false;
        return isActionTriggered(action);
    }

    public boolean isActionTriggered(InputAction action) {
        for (Input input : action.getBoundInputs()) {
            if (isInputTriggered(input)) {
                return true;
            }
        }

        return false;
    }

    public boolean isActionReleased(String actionName) {
        InputAction action = inputActions.get(actionName);
        if (action == null) return false;
        return isActionReleased(action);
    }

    public boolean isActionReleased(InputAction action) {
        for (Input input : action.getBoundInputs()) {
            if (isInputReleased(input)) {
                return true;
            }
        }

        return false;
    }

    public long getActionHoldDuration(InputAction action) {
        return getActionHoldDuration(action.getName());
    }

    public long getActionHoldDuration(String actionName) {
        Long pressTime = actionPressTime.get(actionName);
        if (pressTime == null) {
            return 0;
        }
        return System.currentTimeMillis() - pressTime;
    }

    public boolean isActionHeldFor(InputAction action, long milliseconds) {
        return isActionHeldFor(action.getName(), milliseconds);
    }

    public boolean isActionHeldFor(String actionName, long milliseconds) {
        return getActionHoldDuration(actionName) >= milliseconds;
    }

    public boolean wasActionTriggeredRecently(InputAction action) {
        return wasActionTriggeredRecently(action.getName());
    }

    public boolean wasActionTriggeredRecently(InputAction action, long timeWindow) {
        return wasActionTriggeredRecently(action.getName(), timeWindow);
    }

    public boolean wasActionTriggeredRecently(String actionName) {
        return wasActionTriggeredRecently(actionName, bufferTimeMs);
    }

    public boolean wasActionTriggeredRecently(String actionName, long timeWindow) {
        if (!bufferingEnabled) return false;

        long currentTime = System.currentTimeMillis();
        return inputBuffer.stream()
                .anyMatch(inputActionEvent -> inputActionEvent.getActionName().equals(actionName) &&
                        inputActionEvent.getEventType() == InputActionEventType.PRESSED &&
                        (currentTime - inputActionEvent.getTimestamp()) <= timeWindow);
    }

    public boolean wasActionReleasedRecently(InputAction action) {
        return wasActionReleasedRecently(action.getName());
    }

    public boolean wasActionReleasedRecently(InputAction action, long timeWindow) {
        return wasActionReleasedRecently(action.getName(), timeWindow);
    }

    public boolean wasActionReleasedRecently(String actionName) {
        return wasActionReleasedRecently(actionName, bufferTimeMs);
    }

    public boolean wasActionReleasedRecently(String actionName, long timeWindow) {
        if (!bufferingEnabled) return false;

        long currentTime = System.currentTimeMillis();
        return inputBuffer.stream()
                .anyMatch(inputActionEvent -> inputActionEvent.getActionName().equals(actionName) &&
                        inputActionEvent.getEventType() == InputActionEventType.RELEASED &&
                        (currentTime - inputActionEvent.getTimestamp()) <= timeWindow);
    }

    public boolean consumeBufferedAction(InputAction action) {
        return consumeBufferedAction(action.getName());
    }

    public boolean consumeBufferedAction(InputAction action, long timeWindow) {
        return consumeBufferedAction(action.getName(), timeWindow);
    }

    public boolean consumeBufferedAction(String actionName) {
        return consumeBufferedAction(actionName, bufferTimeMs);
    }

    public boolean consumeBufferedAction(String actionName, long timeWindow) {
        if (!bufferingEnabled) return false;

        long currentTime = System.currentTimeMillis();
        Iterator<InputActionEvent> iterator = inputBuffer.iterator();

        while (iterator.hasNext()) {
            InputActionEvent inputActionEvent = iterator.next();
            if (inputActionEvent.getActionName().equals(actionName) &&
                    inputActionEvent.getEventType() == InputActionEventType.PRESSED &&
                    (currentTime - inputActionEvent.getTimestamp()) <= timeWindow) {
                iterator.remove();
                return true;
            }
        }

        return false;
    }

    // Generic input state methods.
    public boolean isInputHeld(Input input) {
        switch (input.getType()) {
            case KEYBOARD -> {
                return isKeyBeingHeld(input.getCode());
            }
            case MOUSE -> {
                return isMouseButtonHeld(input.getCode());
            }
            default -> {
                return false;
            }
        }
    }

    public boolean isInputTriggered(Input input) {
        switch (input.getType()) {
            case KEYBOARD -> {
                return isKeyTriggered(input.getCode());
            }
            case MOUSE -> {
                return isMouseButtonTriggered(input.getCode());
            }
            default -> {
                return false;
            }
        }
    }

    public boolean isInputReleased(Input input) {
        switch (input.getType()) {
            case KEYBOARD -> {
                return isKeyReleased(input.getCode());
            }
            case MOUSE -> {
                return isMouseButtonReleased(input.getCode());
            }
            default -> {
                return false;
            }
        }
    }

    // Key state methods.
    public boolean isKeyBeingHeld(int keyCode) {
        boolean valid = keyCode >= 0 && keyCode < keys.length;
        return valid && keys[keyCode];
    }

    public boolean isKeyTriggered(int keyCode) {
        boolean valid = keyCode >= 0 && keyCode < keys.length;
        return valid && keys[keyCode] && !previousKeys[keyCode];
    }

    public boolean isKeyReleased(int keyCode) {
        boolean valid = keyCode >= 0 && keyCode < keys.length;
        return valid && !keys[keyCode] && previousKeys[keyCode];
    }

    // Mouse state methods.
    public boolean isMouseButtonHeld(int button) {
        boolean valid = button >= 0 && button < buttons.length;
        return valid && buttons[button];
    }

    public boolean isMouseButtonTriggered(int button) {
        boolean valid = button >= 0 && button < buttons.length;
        return valid && buttons[button] && !previousButtons[button];
    }

    public boolean isMouseButtonReleased(int button) {
        boolean valid = button >= 0 && button < buttons.length;
        return valid && !buttons[button] && previousButtons[button];
    }

    public Vector2D getMousePosition() {
        return new Vector2D(mousePosition);
    }

    public int getScrollDelta() {
        return scrollDelta;
    }

    public Vector2D getMouseDelta() {
        return mousePosition.subtract(lastMousePosition);
    }

    public boolean isMouseInArea(int x, int y, int width, int height) {
        Vector2D pos = getMousePosition();
        return pos.x >= x && pos.x < x + width &&
                pos.y >= y && pos.y < y + height;
    }

    // Update previous states.
    public void updateBuffer() {
        if (!bufferingEnabled) return;
        long currentTime = System.currentTimeMillis();
        inputBuffer.removeIf(inputActionEvent -> (currentTime - inputActionEvent.getTimestamp()) > (long) (bufferTimeMs * 2));

        for (String actionName : inputActions.keySet()) {
            boolean currentlyPressed = isActionHeld(actionName);
            boolean previouslyPressed = previousStates.getOrDefault(actionName, false);

            if (currentlyPressed && !previouslyPressed) {
                inputBuffer.offer(new InputActionEvent(actionName, currentTime, InputActionEventType.PRESSED));
                actionPressTime.put(actionName, currentTime);

                Input triggeringInput = findTriggeringInput(actionName);
                fireActionPressed(actionName, triggeringInput);

            } else if (!currentlyPressed && previouslyPressed) {
                inputBuffer.offer(new InputActionEvent(actionName, currentTime, InputActionEventType.RELEASED));
                actionPressTime.remove(actionName);

                Input triggeringInput = findTriggeringInput(actionName);
                fireActionReleased(actionName, triggeringInput);

            } else if (currentlyPressed) {
                long holdDuration = getActionHoldDuration(actionName);
                Long lastHoldEvent = lastHoldEventTime.get(actionName);

                if (lastHoldEvent == null || (currentTime - lastHoldEvent) >= HOLD_EVENT_INTERVAL) {
                    fireActionHeld(actionName, holdDuration);
                    lastHoldEventTime.put(actionName, currentTime);
                }
            } else {
                lastHoldEventTime.remove(actionName);
            }

            previousStates.put(actionName, currentlyPressed);
        }

        Vector2D mouseDelta = getMouseDelta();
        if (mouseDelta.magnitude() > 0 || scrollDelta != 0) {
            InputMouseEvent mouseEvent = new InputMouseEvent(getMousePosition(), mouseDelta, scrollDelta);
            fireInputMouseEvent(mouseEvent);
        }
    }

    public void update() {
        updateBuffer();

        lastMousePosition = new Vector2D(mousePosition);
        scrollDelta = 0;
        System.arraycopy(keys, 0, previousKeys, 0, keys.length);
        System.arraycopy(buttons, 0, previousButtons, 0, buttons.length);
    }

    // KeyListener methods.
    @Override
    public void keyPressed(KeyEvent event) {
        int keyCode = event.getKeyCode();
        keys[keyCode] = true;
    }

    @Override
    public void keyReleased(KeyEvent event) {
        int keyCode = event.getKeyCode();
        keys[keyCode] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Ignore
    }

    // MouseListener methods.
    @Override
    public void mousePressed(MouseEvent event) {
        int button = event.getButton();
        buttons[button] = true;
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        int button = event.getButton();
        buttons[button] = false;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        // Ignore
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    // MouseMotionListener methods.
    @Override
    public void mouseMoved(MouseEvent event) {
        mousePosition = new Vector2D((int) event.getX(), (int) event.getY());
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        mousePosition = new Vector2D((int) event.getX(), (int) event.getY());
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent event) {
        scrollDelta = event.getWheelRotation();
    }

    // Utility methods.
    public Set<String> getConflictingActions(Input input) {
        Set<String> actions = getActionsForInput(input);
        return actions.size() > 1 ? actions : Collections.emptySet();
    }

    public Map<String, Map<String, Set<Input>>> getActionMappings() {
        Map<String, Map<String, Set<Input>>> mappings = new HashMap<>();
        for (Map.Entry<String, Set<InputAction>> entry : inputCategories.entrySet()) {
            Map<String, Set<Input>> categoryMappings = mappings.computeIfAbsent(entry.getKey(), k -> { return new HashMap<>(); });
            for (InputAction action : entry.getValue()) {
                categoryMappings.put(action.getName(), action.getBoundInputs());
            }
        }
        return mappings;
    }

    public void loadActionMappings(Map<String, Map<String, Set<Input>>> mappings) {
        inputActions.clear();
        inputToActions.clear();

        for (Map.Entry<String, Map<String, Set<Input>>> categoryEntry : mappings.entrySet()) {
            String categoryName = categoryEntry.getKey();
            createCategory(categoryName);
            for (Map.Entry<String, Set<Input>> actionEntry : categoryEntry.getValue().entrySet()) {
                String actionName = actionEntry.getKey();
                InputAction action = bind(categoryName, actionName);
                for (Input input : actionEntry.getValue()) {
                    addInputToAction(action, input);
                }
            }
        }
    }
}