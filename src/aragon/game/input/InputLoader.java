package aragon.game.input;

import aragon.game.input.data.InputActionData;
import aragon.game.input.data.InputCategoryData;
import aragon.game.input.data.InputData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class InputLoader {
    private static final Logger LOGGER = LogManager.getLogger(InputLoader.class);
    private static InputLoader inputLoader;

    private Map<String, InputCategoryData> inputCategories;
    private final InputManager inputManager;
    private final Gson gson;
    private final Validator validator;

    private InputLoader(InputManager inputManager) {
        this.inputManager = inputManager;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public static InputLoader build(InputManager inputManager) {
        if (inputLoader == null) {
            inputLoader = new InputLoader(inputManager);
        }
        return inputLoader;
    }

    public void initialize() throws InputLoadingException {
        try {
            LOGGER.info("Input loading started...");

            loadConfiguration();
            validateConfiguration();
            applyInputBindings();

            LOGGER.info("Input loading finished successfully");
        } catch (Exception exception) {
            throw new InputLoadingException("Input loading failed. " + exception);
        }
    }

    public void loadConfiguration() throws IOException {
        String path = "/data/config/inputs.json";
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            throw new IOException("Input configuration file not found. " + path);
        }

        try (InputStreamReader reader = new InputStreamReader(stream)) {
            Type mapType = new TypeToken<Map<String, InputCategoryData>>(){}.getType();
            inputCategories = gson.fromJson(reader, mapType);
            LOGGER.info("Loaded input configuration. " + path);
        } catch (JsonSyntaxException exception) {
            throw new IOException("Invalid JSON formation in input configuration. " + path, exception);
        }
    }

    public void validateConfiguration() throws InputLoadingException {
        Set<ConstraintViolation<Map<String, InputCategoryData>>> violations = validator.validate(inputCategories);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder("Configuration validation errors:\n");
            for (ConstraintViolation<Map<String, InputCategoryData>> violation : violations) {
                sb.append("- ").append(violation.getPropertyPath())
                        .append(": ").append(violation.getMessage()).append("\n");
            }
            throw new InputLoadingException(sb.toString());
        }

        for (Map.Entry<String, InputCategoryData> categoryEntry : inputCategories.entrySet()) {
            String categoryName = categoryEntry.getKey();
            InputCategoryData inputCategoryData = categoryEntry.getValue();

            if (inputCategoryData.getActions() == null || inputCategoryData.getActions().isEmpty()) {
                LOGGER.warn("Input category has no actions assigned: " + categoryName);
            }

            for (Map.Entry<String, InputActionData> actionEntry : inputCategoryData.getActions().entrySet()) {
                String actionName = actionEntry.getKey();
                InputActionData inputActionData = actionEntry.getValue();

                if (inputActionData.getInputs() == null || inputActionData.getInputs().isEmpty()) {
                    LOGGER.warn(String.format("Input action has no inputs assigned: %s. In category: %s", actionName, categoryName));
                }

                for (InputData inputData : inputActionData.getInputs()) {
                    if (!inputData.isValid()) {
                        throw new InputLoadingException(
                                String.format("Invalid input configuration for action %s : %s. In category: %s",
                                        actionName, inputData, categoryName));
                    }
                }
            }
        }
    }

    public void applyInputBindings() {
        for (Map.Entry<String, InputCategoryData> categoryEntry : inputCategories.entrySet()) {
            String categoryName = categoryEntry.getKey();
            InputCategoryData inputCategoryData = categoryEntry.getValue();

            inputManager.createCategory(categoryName);
            LOGGER.info("Added input category " + categoryName);

            for (Map.Entry<String, InputActionData> actionEntry : inputCategoryData.getActions().entrySet()) {
                String actionName = actionEntry.getKey();
                InputActionData actionConfig = actionEntry.getValue();

                InputAction action = inputManager.bind(categoryName, actionName);

                for (InputData inputData : actionConfig.getInputs()) {
                    Input input = createInputFromConfig(inputData);
                    inputManager.addInputToAction(action, input);

                    LOGGER.debug(String.format("Bound %s to action %s", inputData, actionName));
                }

                LOGGER.info(String.format("Configured action %s with %d inputs.",
                        actionName, actionConfig.getInputs().size()));
            }
        }
    }

    private Input createInputFromConfig(InputData inputData) {
        int resolvedCode = inputData.getResolvedCode();

        switch (inputData.getType().toUpperCase()) {
            case "KEYBOARD" -> {
                return Input.keyboard(resolvedCode);
            }
            case "MOUSE" -> {
                return Input.mouse(resolvedCode);
            }
            default -> {
                throw new IllegalArgumentException("Unknown input type: " + inputData.getType());
            }
        }
    }

    private InputData createConfigFromInput(Input input) {
        String type = input.getType().name().toLowerCase();

        if (input.getType() == InputType.KEYBOARD) {
            String keyName = InputMapping.getKeyName(input.getCode());
            return new InputData(type, keyName);
        } else if (input.getType() == InputType.MOUSE) {
            String buttonName = InputMapping.getMouseButtonName(input.getCode());
            return new InputData(type, buttonName);
        }

        return new InputData(type, input.getCode());
    }

    private InputCategoryData createConfigFromCategory(Map<String, Set<Input>> actions) {
        Map<String, InputActionData> actionsConfig = new HashMap<>();

        for (Map.Entry<String, Set<Input>> actionEntry : actions.entrySet()) {
            String actionName = actionEntry.getKey();
            Set<Input> inputs = actionEntry.getValue();

            List<InputData> inputData = inputs.stream()
                    .map(this::createConfigFromInput)
                    .collect(Collectors.toList());

            actionsConfig.put(actionName, new InputActionData(inputData));
        }

        return new InputCategoryData(actionsConfig);
    }

    public void reloadConfiguration() throws InputLoadingException {
        LOGGER.info("Reloading input configuration...");

        for (String actionName : inputManager.getActionNames()) {
            inputManager.clearActionInputs(actionName);
        }

        initialize();

        LOGGER.info("Input configuration reloaded");
    }

    public Map<String, InputCategoryData> getInputCategories() {
        return new HashMap<>(inputCategories);
    }

    public void saveConfiguration() throws InputLoadingException {
        try {
            Map<String, Map<String, Set<Input>>> currentMappings = inputManager.getActionMappings();

            // Convert to config format
            Map<String, InputCategoryData> configToSave = new HashMap<>();

            for (Map.Entry<String, Map<String, Set<Input>>> categoryEntry : currentMappings.entrySet()) {
                String categoryName = categoryEntry.getKey();
                Map<String, Set<Input>> actions = categoryEntry.getValue();
                configToSave.put(categoryName, createConfigFromCategory(actions));
            }

            String json = gson.toJson(configToSave);
            LOGGER.info("Configuration saved: {}", json);

        } catch (Exception e) {
            throw new InputLoadingException("Failed to save input configuration", e);
        }
    }
}
