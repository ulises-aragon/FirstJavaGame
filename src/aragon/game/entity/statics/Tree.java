package aragon.game.entity.statics;

import aragon.game.entity.EntityManager;
import aragon.game.graphics.Sprite;
import aragon.game.util.Vector2;

import java.awt.*;

public class Tree extends StaticEntity {
    private final Sprite sprite;
    public Tree(EntityManager entityManager, int x, int y) {
        super(
                entityManager,
                x,
                y,
                entityManager.getLevel().getGameState().getGame().getTileSize()*3,
                entityManager.getLevel().getGameState().getGame().getTileSize()*5,
                true
        );
        int tileSize = entityManager.getLevel().getGameState().getGame().getTileSize();
        sprite = entityManager.getLevel().getGameState().getGame().getAssetManager().getSprite("garden.tree");
        collisionBounds = new Rectangle(tileSize, (int) (tileSize*4.6), tileSize, tileSize/4);
    }

    @Override
    public void update() {}

    @Override
    public void render(Graphics graphics) {
        Vector2 cameraPosition = entityManager.getLevel().getGameState().getGame().getCamera().getPosition();
        graphics.drawImage(
                sprite.getImage(),
                (int) (position.x - cameraPosition.x),
                (int) (position.y - cameraPosition.y),
                (int) size.x,
                (int) size.y,
                null
        );
    }
}
