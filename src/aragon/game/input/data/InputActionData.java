package aragon.game.input.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class InputActionData {
    @NotNull
    @Size(min=1, message="InputAction must have at least one input.")
    private List<InputData> inputs;

    public InputActionData() {}

    public InputActionData(List<InputData> inputs) {
        this.inputs = inputs;
    }

    public List<InputData> getInputs() { return inputs; }
    public void setInputs(List<InputData> inputs) { this.inputs = inputs; }
}
