package aragon.game.input.data;

import aragon.game.input.InputMapping;
import jakarta.validation.constraints.NotBlank;

public class InputData {
    @NotBlank(message = "Input type cannot be blank.")
    private String type;

    private String key;
    private Integer code;

    public InputData() {}

    public InputData(String type, String key) {
        this.type = type;
        this.key = key;
    }

    public InputData(String type, int code) {
        this.type = type;
        this.code = code;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }

    public int getResolvedCode() {
        if (code != null) {
            return code;
        }

        if (key != null && !key.trim().isEmpty()) {
            String normalizedType = type.toUpperCase();
            if ("KEYBOARD".equals(normalizedType)) {
                return InputMapping.getKeyCode(key);
            } else if ("MOUSE".equals(normalizedType)) {
                return InputMapping.getMouseButton(key);
            }
        }

        throw new IllegalArgumentException("No valid key or code specified for input: " + this);
    }

    public boolean isValid() {
        if (type == null || type.trim().isEmpty()) return false;

        try {
            int resolvedCode = getResolvedCode();

            String normalizedType = type.toUpperCase();
            if ("KEYBOARD".equals(normalizedType)) {
                return resolvedCode >= 0 && resolvedCode < 512;
            } else if ("MOUSE".equals(normalizedType)) {
                return resolvedCode >= 0 && resolvedCode < 10;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    @Override
    public String toString() {
        if (key != null) {
            return String.format("%s[%s]", type, key);
        } else {
            return String.format("%s[%d]", type, code);
        }
    }
}
