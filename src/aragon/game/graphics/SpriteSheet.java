package aragon.game.graphics;

import aragon.game.util.Vector2;

import java.awt.image.BufferedImage;

public class SpriteSheet extends Sprite {
    private Vector2 offset = Vector2.zero;
    private Vector2 spriteSize = Vector2.zero;
    private Vector2 tempOffset = Vector2.zero;
    private Vector2 tempSpriteSize = Vector2.zero;

    public SpriteSheet(BufferedImage image) {
        super(image);
    }

    public SpriteSheet(Sprite sprite) {
        super(sprite);
    }

    public SpriteSheet(String path) {
        super(path);
    }

    public void setSpriteSize(int x, int y) {
        spriteSize = new Vector2(x, y);
        tempSpriteSize = spriteSize;
    }

    public void setOffset(int x, int y) {
        offset = new Vector2(x, y);
        tempOffset = offset;
    }

    public void withSpriteSize(int x, int y) {
        tempSpriteSize = new Vector2(x, y);
    }

    public void withOffset(int x, int y) {
        tempOffset = new Vector2(x, y);
    }

    public Vector2 getSpriteSize() {
        return new Vector2(spriteSize);
    }

    public Sprite getSprite(int x, int y) {
        Vector2 cachedOffset = tempOffset;
        tempOffset = offset;
        Vector2 cachedSize = tempSpriteSize;
        tempSpriteSize = spriteSize;
        return getSubSprite(
                (int) (cachedOffset.x + (x * cachedSize.x)),
                (int) (cachedOffset.y + (y * cachedSize.y)),
                (int) (cachedSize.x),
                (int) (cachedSize.y)
        );
    }

    public SpriteSheet getSpriteSheet(int x0, int y0, int x1, int y1) {
        Vector2 cachedOffset = tempOffset;
        tempOffset = offset;
        Vector2 cachedSize = tempSpriteSize;
        tempSpriteSize = spriteSize;
        return new SpriteSheet(getSubSprite(
                (int) (cachedOffset.x + (x0 * cachedSize.x)),
                (int) (cachedOffset.y + (y0 * cachedSize.y)),
                (int) (cachedOffset.x + (x1 * cachedSize.x)),
                (int) (cachedOffset.y + (y1 * cachedSize.y))
        ));
    }
}
