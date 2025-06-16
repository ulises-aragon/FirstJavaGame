package aragon.game.assets;

public class AssetLoadingException extends Exception {
    public AssetLoadingException(String message) {
        super(message);
    }

    public AssetLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}