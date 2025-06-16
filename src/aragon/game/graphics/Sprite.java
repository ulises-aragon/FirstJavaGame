package aragon.game.graphics;

import aragon.game.assets.ImageLoader;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Sprite {
    protected final BufferedImage image;

    public Sprite(BufferedImage image) {
        this.image = image;
    }

    public Sprite(Sprite sprite) {
        int width = sprite.image.getWidth();
        int height = sprite.image.getHeight();
        image = new BufferedImage(width, height, sprite.image.getType());
        Graphics graphics = image.createGraphics();
        graphics.drawImage(image, 0, 0, width, height, null);
        graphics.dispose();
    }

    public Sprite(String path) {
        image = ImageLoader.loadImage(path);
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    public Sprite getSubSprite(int x, int y, int w, int h) {
        return new Sprite(image.getSubimage(x, y, w, h));
    }

    public Sprite flipHorizontal() {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage flipped = new BufferedImage(width, height, image.getType());
        Graphics graphics = flipped.createGraphics();
        graphics.drawImage(image, 0, 0, width, height, width, 0, 0, height, null);
        graphics.dispose();
        return new Sprite(flipped);
    }

    public Sprite flipVertical() {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage flipped = new BufferedImage(width, height, image.getType());
        Graphics graphics = flipped.createGraphics();
        graphics.drawImage(image, 0, 0, width, height, 0, height, width, 0, null);
        graphics.dispose();
        return new Sprite(flipped);
    }
}
