package aragon.game.graphics;

import aragon.game.util.Vector2D;

import java.awt.image.BufferedImage;

public class SpriteSheet extends Sprite {
    private Vector2D offset = Vector2D.zero;
    private Vector2D spriteSize = Vector2D.zero;
    private Vector2D tempOffset = Vector2D.zero;
    private Vector2D tempSpriteSize = Vector2D.zero;

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
        spriteSize = new Vector2D(x, y);
        tempSpriteSize = spriteSize;
    }

    public void setOffset(int x, int y) {
        offset = new Vector2D(x, y);
        tempOffset = offset;
    }

    public void withSpriteSize(int x, int y) {
        tempSpriteSize = new Vector2D(x, y);
    }

    public void withOffset(int x, int y) {
        tempOffset = new Vector2D(x, y);
    }

    public Vector2D getSpriteSize() {
        return new Vector2D(spriteSize);
    }

    public Sprite getSprite(int x, int y) {
        Vector2D cachedOffset = tempOffset;
        tempOffset = offset;
        Vector2D cachedSize = tempSpriteSize;
        tempSpriteSize = spriteSize;
        return getSubSprite(
                (int) (cachedOffset.x + (x * cachedSize.x)),
                (int) (cachedOffset.y + (y * cachedSize.y)),
                (int) (cachedSize.x),
                (int) (cachedSize.y)
        );
    }

    public SpriteSheet getSpriteSheet(int x0, int y0, int x1, int y1) {
        Vector2D cachedOffset = tempOffset;
        tempOffset = offset;
        Vector2D cachedSize = tempSpriteSize;
        tempSpriteSize = spriteSize;
        return new SpriteSheet(getSubSprite(
                (int) (cachedOffset.x + (x0 * cachedSize.x)),
                (int) (cachedOffset.y + (y0 * cachedSize.y)),
                (int) (cachedOffset.x + (x1 * cachedSize.x)),
                (int) (cachedOffset.y + (y1 * cachedSize.y))
        ));
    }
}
