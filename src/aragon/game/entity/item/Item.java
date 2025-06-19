package aragon.game.entity.item;

import aragon.game.entity.Collectible;
import aragon.game.entity.Entity;
import aragon.game.entity.EntityManager;
import aragon.game.entity.player.Player;
import aragon.game.graphics.Sprite;
import aragon.game.util.Vector2;

import java.awt.*;

public class Item extends Entity implements Collectible {
    private final Sprite sprite;

    public Item(EntityManager entityManager, String spritePath, int x, int y) {
        super(entityManager, x, y, entityManager.getLevel().getGameState().getGame().getTileSize(), false);
        this.sprite = entityManager.getLevel().getGameState().getGame().getAssetManager().getSprite(spritePath);

        int collisionOffset = (int) (entityManager.getLevel().getGameState().getGame().getTileSize()*0.4);
        int collisionSize = (int) (entityManager.getLevel().getGameState().getGame().getTileSize()*0.5);

        collisionBounds = new Rectangle(collisionOffset, collisionOffset, collisionSize, collisionSize);
    }

    @Override
    public void onCollect(Player player) {
        if (canCollect(player)) {
            suicide();
            player.addKey();
        }
    }

    @Override
    public boolean canCollect(Player player) {
        return isAlive() && player.getCollisionBounds().intersects(getCollisionBounds());
    }

    @Override
    public void update() {
        Player player = entityManager.getLevel().getEntityManager().getPlayer();
        if (canCollect(player)) onCollect(player);
    }

    @Override
    public void render(Graphics graphics) {
        Vector2 cameraPosition = entityManager.getLevel().getGameState().getGame().getCamera().getPosition();
        Vector2 worldPosition = position.subtract(cameraPosition);
        double spriteXSize = size.x*0.5;
        double spriteYSize = size.y*0.5;

        graphics.drawImage(
                sprite.getImage(),
                (int) (worldPosition.x + spriteXSize*0.5),
                (int) (worldPosition.y + spriteYSize*0.5),
                (int) spriteXSize,
                (int) spriteYSize,
                null
        );
    }
}
