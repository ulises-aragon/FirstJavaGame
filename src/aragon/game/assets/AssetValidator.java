package aragon.game.assets;

import java.util.Map;

@FunctionalInterface
public interface AssetValidator<T> {
    void validate(Map.Entry<String, T> entry) throws AssetLoadingException;
}