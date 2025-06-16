package aragon.game.input;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class InputAction {
    private final String name;
    private final Set<Input> boundInputs;

    public InputAction(String name) {
        this.name = name;
        this.boundInputs = new HashSet<>();
    }

    public String getName() {
        return this.name;
    }

    public Set<Input> getBoundInputs() {
        return new HashSet<>(boundInputs);
    }

    public void addInput(Input input) {
        boundInputs.add(input);
    }

    public void addKeyCode(int keyCode) {
        boundInputs.add(Input.keyboard(keyCode));
    }

    public void addMouseButton(int mouseButton) {
        boundInputs.add(Input.mouse(mouseButton));
    }

    public void removeInput(Input input) {
        boundInputs.remove(input);
    }

    public void removeKeyCode(int keyCode) {
        boundInputs.remove(Input.keyboard(keyCode));
    }

    public void removeMouseButton(int mouseButton) {
        boundInputs.remove(Input.mouse(mouseButton));
    }

    public void clearInputs() {
        boundInputs.clear();
    }

    public boolean hasInput(Input input) {
        return boundInputs.contains(input);
    }

    public boolean hasKeyCode(int keyCode) {
        return boundInputs.contains(Input.keyboard(keyCode));
    }

    public boolean hasMouseButton(int mouseButton) {
        return boundInputs.contains(Input.mouse(mouseButton));
    }

    public boolean isEmpty() {
        return boundInputs.isEmpty();
    }

    public Set<Integer> getKeyBoardKeys() {
        return boundInputs.stream().filter(Input::isKeyboard).map(Input::getCode).collect(Collectors.toSet());
    }

    public Set<Integer> getMouseButtons() {
        return boundInputs.stream().filter(Input::isMouse).map(Input::getCode).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return name + " (InputAction) [" + boundInputs + "]";
    }
}
