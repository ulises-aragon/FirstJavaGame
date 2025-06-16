package aragon.game.input.data;

import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

public class InputCategoryData {
    @NotEmpty(message="Input category must have one or more actions.")
    private Map<String, InputActionData> actions;

    public InputCategoryData() {}

    public InputCategoryData(Map<String, InputActionData> actions) {
        this.actions = actions;
    }

    public Map<String, InputActionData> getActions() { return actions; }
    public void setActions(Map<String, InputActionData> actions) { this.actions = actions; }
}
