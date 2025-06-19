package aragon.game.assets.data;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class AutoLoadConfig {
    @NotEmpty(message="Atleast one directory must be declared.")
    private List<AutoLoadDirectoryConfig> directories;

    public List<AutoLoadDirectoryConfig> getDirectories() { return directories; }
}
