package aragon.game.input;

import java.util.Objects;

public class Input {
    private final InputType type;
    private final int code;

    private Input(InputType type, int code) {
        this.type = type;
        this.code = code;
    }

    public InputType getType() {
        return type;
    }

    public int getCode() {
        return code;
    }

    public boolean isKeyboard() {
        return type == InputType.KEYBOARD;
    }

    public boolean isMouse() {
        return type == InputType.MOUSE;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Input input = (Input) object;
        return code == input.code && type == input.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, code);
    }

    @Override
    public String toString() {
        return "Input[" + type + ", " + InputMapping.getKeyName(code) + "]";
    }

    // Factories.
    public static Input keyboard(int keyCode) {
        return new Input(InputType.KEYBOARD, keyCode);
    }

    public static Input mouse(int mouseButton) {
        return new Input(InputType.MOUSE, mouseButton);
    }
}
