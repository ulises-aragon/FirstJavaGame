package aragon.game.assets;

import java.util.Map;

@FunctionalInterface
public interface AssetProcessor<T> {
    void process(Map.Entry<String, T> entry) throws AssetLoadingException;
}