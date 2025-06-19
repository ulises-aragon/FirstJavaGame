package aragon.game.assets.data;

import jakarta.validation.constraints.NotBlank;

public class AutoLoadDirectoryConfig {
    @NotBlank(message="Directory must contain a path.")
    private String path;
    @NotBlank(message="Directory must have a valid type.")
    private String type;

    public String getPath() { return path; }
    public String getType() {
        return type;
    }
}
