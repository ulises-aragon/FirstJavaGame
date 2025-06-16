package aragon.game.input;

import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class InputMapping {
    public static final Map<String, Integer> KEY_MAP = new HashMap<>();
    public static final Map<String, Integer> BUTTON_MAP = new HashMap<>();

    static {
        // KEY CODES
        // Letters
        KEY_MAP.put("A", KeyEvent.VK_A);
        KEY_MAP.put("B", KeyEvent.VK_B);
        KEY_MAP.put("C", KeyEvent.VK_C);
        KEY_MAP.put("D", KeyEvent.VK_D);
        KEY_MAP.put("E", KeyEvent.VK_E);
        KEY_MAP.put("F", KeyEvent.VK_F);
        KEY_MAP.put("G", KeyEvent.VK_G);
        KEY_MAP.put("H", KeyEvent.VK_H);
        KEY_MAP.put("I", KeyEvent.VK_I);
        KEY_MAP.put("J", KeyEvent.VK_J);
        KEY_MAP.put("K", KeyEvent.VK_K);
        KEY_MAP.put("L", KeyEvent.VK_L);
        KEY_MAP.put("M", KeyEvent.VK_M);
        KEY_MAP.put("N", KeyEvent.VK_N);
        KEY_MAP.put("O", KeyEvent.VK_O);
        KEY_MAP.put("P", KeyEvent.VK_P);
        KEY_MAP.put("Q", KeyEvent.VK_Q);
        KEY_MAP.put("R", KeyEvent.VK_R);
        KEY_MAP.put("S", KeyEvent.VK_S);
        KEY_MAP.put("T", KeyEvent.VK_T);
        KEY_MAP.put("U", KeyEvent.VK_U);
        KEY_MAP.put("V", KeyEvent.VK_V);
        KEY_MAP.put("W", KeyEvent.VK_W);
        KEY_MAP.put("X", KeyEvent.VK_X);
        KEY_MAP.put("Y", KeyEvent.VK_Y);
        KEY_MAP.put("Z", KeyEvent.VK_Z);

        // Numbers
        KEY_MAP.put("0", KeyEvent.VK_0);
        KEY_MAP.put("1", KeyEvent.VK_1);
        KEY_MAP.put("2", KeyEvent.VK_2);
        KEY_MAP.put("3", KeyEvent.VK_3);
        KEY_MAP.put("4", KeyEvent.VK_4);
        KEY_MAP.put("5", KeyEvent.VK_5);
        KEY_MAP.put("6", KeyEvent.VK_6);
        KEY_MAP.put("7", KeyEvent.VK_7);
        KEY_MAP.put("8", KeyEvent.VK_8);
        KEY_MAP.put("9", KeyEvent.VK_9);
        KEY_MAP.put("ZERO", KeyEvent.VK_0);
        KEY_MAP.put("ONE", KeyEvent.VK_1);
        KEY_MAP.put("TWO", KeyEvent.VK_2);
        KEY_MAP.put("THREE", KeyEvent.VK_3);
        KEY_MAP.put("FOUR", KeyEvent.VK_4);
        KEY_MAP.put("FIVE", KeyEvent.VK_5);
        KEY_MAP.put("SIX", KeyEvent.VK_6);
        KEY_MAP.put("SEVEN", KeyEvent.VK_7);
        KEY_MAP.put("EIGHT", KeyEvent.VK_8);
        KEY_MAP.put("NINE", KeyEvent.VK_9);

        // Arrow keys
        KEY_MAP.put("UP", KeyEvent.VK_UP);
        KEY_MAP.put("DOWN", KeyEvent.VK_DOWN);
        KEY_MAP.put("LEFT", KeyEvent.VK_LEFT);
        KEY_MAP.put("RIGHT", KeyEvent.VK_RIGHT);
        KEY_MAP.put("ARROW_UP", KeyEvent.VK_UP);
        KEY_MAP.put("ARROW_DOWN", KeyEvent.VK_DOWN);
        KEY_MAP.put("ARROW_LEFT", KeyEvent.VK_LEFT);
        KEY_MAP.put("ARROW_RIGHT", KeyEvent.VK_RIGHT);

        // Function keys
        KEY_MAP.put("F1", KeyEvent.VK_F1);
        KEY_MAP.put("F2", KeyEvent.VK_F2);
        KEY_MAP.put("F3", KeyEvent.VK_F3);
        KEY_MAP.put("F4", KeyEvent.VK_F4);
        KEY_MAP.put("F5", KeyEvent.VK_F5);
        KEY_MAP.put("F6", KeyEvent.VK_F6);
        KEY_MAP.put("F7", KeyEvent.VK_F7);
        KEY_MAP.put("F8", KeyEvent.VK_F8);
        KEY_MAP.put("F9", KeyEvent.VK_F9);
        KEY_MAP.put("F10", KeyEvent.VK_F10);
        KEY_MAP.put("F11", KeyEvent.VK_F11);
        KEY_MAP.put("F12", KeyEvent.VK_F12);

        // Special keys
        KEY_MAP.put("SPACE", KeyEvent.VK_SPACE);
        KEY_MAP.put("SPACEBAR", KeyEvent.VK_SPACE);
        KEY_MAP.put("ENTER", KeyEvent.VK_ENTER);
        KEY_MAP.put("RETURN", KeyEvent.VK_ENTER);
        KEY_MAP.put("ESCAPE", KeyEvent.VK_ESCAPE);
        KEY_MAP.put("ESC", KeyEvent.VK_ESCAPE);
        KEY_MAP.put("TAB", KeyEvent.VK_TAB);
        KEY_MAP.put("BACKSPACE", KeyEvent.VK_BACK_SPACE);
        KEY_MAP.put("DELETE", KeyEvent.VK_DELETE);
        KEY_MAP.put("HOME", KeyEvent.VK_HOME);
        KEY_MAP.put("END", KeyEvent.VK_END);
        KEY_MAP.put("PAGE_UP", KeyEvent.VK_PAGE_UP);
        KEY_MAP.put("PAGE_DOWN", KeyEvent.VK_PAGE_DOWN);
        KEY_MAP.put("INSERT", KeyEvent.VK_INSERT);

        // Modifiers
        KEY_MAP.put("SHIFT", KeyEvent.VK_SHIFT);
        KEY_MAP.put("LEFT_SHIFT", KeyEvent.VK_SHIFT);
        KEY_MAP.put("RIGHT_SHIFT", KeyEvent.VK_SHIFT);
        KEY_MAP.put("LSHIFT", KeyEvent.VK_SHIFT);
        KEY_MAP.put("RSHIFT", KeyEvent.VK_SHIFT);
        KEY_MAP.put("CTRL", KeyEvent.VK_CONTROL);
        KEY_MAP.put("CONTROL", KeyEvent.VK_CONTROL);
        KEY_MAP.put("LEFT_CTRL", KeyEvent.VK_CONTROL);
        KEY_MAP.put("RIGHT_CTRL", KeyEvent.VK_CONTROL);
        KEY_MAP.put("LCTRL", KeyEvent.VK_CONTROL);
        KEY_MAP.put("RCTRL", KeyEvent.VK_CONTROL);
        KEY_MAP.put("ALT", KeyEvent.VK_ALT);
        KEY_MAP.put("LEFT_ALT", KeyEvent.VK_ALT);
        KEY_MAP.put("RIGHT_ALT", KeyEvent.VK_ALT_GRAPH);
        KEY_MAP.put("LALT", KeyEvent.VK_ALT);
        KEY_MAP.put("RALT", KeyEvent.VK_ALT_GRAPH);

        // Punctuation
        KEY_MAP.put("COMMA", KeyEvent.VK_COMMA);
        KEY_MAP.put("PERIOD", KeyEvent.VK_PERIOD);
        KEY_MAP.put("SLASH", KeyEvent.VK_SLASH);
        KEY_MAP.put("SEMICOLON", KeyEvent.VK_SEMICOLON);
        KEY_MAP.put("QUOTE", KeyEvent.VK_QUOTE);
        KEY_MAP.put("BRACE_LEFT", KeyEvent.VK_BRACELEFT);
        KEY_MAP.put("BRACE_RIGHT", KeyEvent.VK_BRACERIGHT);
        KEY_MAP.put("BRACKET_LEFT", KeyEvent.VK_OPEN_BRACKET);
        KEY_MAP.put("BRACKET_RIGHT", KeyEvent.VK_CLOSE_BRACKET);
        KEY_MAP.put("BACKSLASH", KeyEvent.VK_BACK_SLASH);
        KEY_MAP.put("MINUS", KeyEvent.VK_MINUS);
        KEY_MAP.put("EQUALS", KeyEvent.VK_EQUALS);
        KEY_MAP.put("BACKTICK", KeyEvent.VK_BACK_QUOTE);
        KEY_MAP.put("BACK_QUOTE", KeyEvent.VK_BACK_QUOTE);

        // Numpad
        KEY_MAP.put("NUMPAD_0", KeyEvent.VK_NUMPAD0);
        KEY_MAP.put("NUMPAD_1", KeyEvent.VK_NUMPAD1);
        KEY_MAP.put("NUMPAD_2", KeyEvent.VK_NUMPAD2);
        KEY_MAP.put("NUMPAD_3", KeyEvent.VK_NUMPAD3);
        KEY_MAP.put("NUMPAD_4", KeyEvent.VK_NUMPAD4);
        KEY_MAP.put("NUMPAD_5", KeyEvent.VK_NUMPAD5);
        KEY_MAP.put("NUMPAD_6", KeyEvent.VK_NUMPAD6);
        KEY_MAP.put("NUMPAD_7", KeyEvent.VK_NUMPAD7);
        KEY_MAP.put("NUMPAD_8", KeyEvent.VK_NUMPAD8);
        KEY_MAP.put("NUMPAD_9", KeyEvent.VK_NUMPAD9);
        KEY_MAP.put("NUMPAD_PLUS", KeyEvent.VK_PLUS);
        KEY_MAP.put("NUMPAD_MINUS", KeyEvent.VK_MINUS);
        KEY_MAP.put("NUMPAD_MULTIPLY", KeyEvent.VK_MULTIPLY);
        KEY_MAP.put("NUMPAD_DIVIDE", KeyEvent.VK_DIVIDE);
        KEY_MAP.put("NUMPAD_ENTER", KeyEvent.VK_ENTER);
        KEY_MAP.put("NUMPAD_DECIMAL", KeyEvent.VK_DECIMAL);

        // MOUSE CODES
        BUTTON_MAP.put("LEFT", MouseEvent.BUTTON1);
        BUTTON_MAP.put("LEFT_CLICK", MouseEvent.BUTTON1);
        BUTTON_MAP.put("LEFT_MOUSE", MouseEvent.BUTTON1);
        BUTTON_MAP.put("LMB", MouseEvent.BUTTON1);

        BUTTON_MAP.put("RIGHT", MouseEvent.BUTTON3);
        BUTTON_MAP.put("RIGHT_CLICK", MouseEvent.BUTTON3);
        BUTTON_MAP.put("RIGHT_MOUSE", MouseEvent.BUTTON3);
        BUTTON_MAP.put("RMB", MouseEvent.BUTTON3);

        BUTTON_MAP.put("MIDDLE", MouseEvent.BUTTON2);
        BUTTON_MAP.put("MIDDLE_CLICK", MouseEvent.BUTTON2);
        BUTTON_MAP.put("MIDDLE_MOUSE", MouseEvent.BUTTON2);
        BUTTON_MAP.put("MMB", MouseEvent.BUTTON2);
        BUTTON_MAP.put("WHEEL", MouseEvent.BUTTON2);
        BUTTON_MAP.put("WHEEL_CLICK", MouseEvent.BUTTON2);

        // Additional mouse buttons (if supported)
        BUTTON_MAP.put("BUTTON4", 4);
        BUTTON_MAP.put("BUTTON5", 5);
        BUTTON_MAP.put("MOUSE4", 4);
        BUTTON_MAP.put("MOUSE5", 5);
        BUTTON_MAP.put("BACK", 4);
        BUTTON_MAP.put("FORWARD", 5);
    }

    public static int getKeyCode(String keyName) throws IllegalArgumentException {
        String normalized = keyName.toUpperCase().trim();
        Integer keyCode = KEY_MAP.get(normalized);
        if (keyCode == null) {
            throw new IllegalArgumentException("Unknown key name: " + keyName);
        }
        return keyCode;
    }

    public static String getKeyName(int keyCode) {
        return KEY_MAP.entrySet().stream()
                .filter(entry -> entry.getValue() == keyCode)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("UNKNOWN_" + keyCode);
    }

    public static Set<String> getKeyNames() {
        return new HashSet<>(KEY_MAP.keySet());
    }

    public static int getMouseButton(String buttonName) throws IllegalArgumentException {
        String normalized = buttonName.toUpperCase().trim();
        Integer mouseButton = BUTTON_MAP.get(normalized);
        if (mouseButton == null) {
            throw new IllegalArgumentException("Unknown mouse button name: " + buttonName);
        }
        return mouseButton;
    }

    public static String getMouseButtonName(int mouseButton) {
        return BUTTON_MAP.entrySet().stream()
                .filter(entry -> entry.getValue() == mouseButton)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("UNKNOWN_" + mouseButton);
    }

    public static Set<String> getMouseButtonNames() {
        return new HashSet<>(BUTTON_MAP.keySet());
    }
}
