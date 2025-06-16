package aragon.game.assets;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public final class ImageLoader {
    public static BufferedImage loadImage(String path) {
        BufferedImage image = null;

        try {
            InputStream input = ImageLoader.class.getClassLoader().getResourceAsStream(path);
            assert input != null;
            image = ImageIO.read(input);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return image;
    }
}
