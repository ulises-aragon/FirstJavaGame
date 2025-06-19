package aragon.game.entity.interactable;

import aragon.game.entity.EntityManager;
import aragon.game.entity.player.Player;
import aragon.game.graphics.Sprite;
import aragon.game.util.Vector2;

import java.awt.*;

public class Door extends InteractableEntity {
    private final Sprite openedSprite;
    private final Sprite closedSprite;
    private boolean closed;
    private final boolean requiresKey;

    public Door(EntityManager entityManager, String openedSprite, String closedSprite, int x, int y, boolean requiresKey, boolean closed, Vector2 axis) {
        super(
                entityManager,
                x,
                y,
                axis.x > axis.y ? entityManager.getLevel().getGameState().getGame().getTileSize() : entityManager.getLevel().getGameState().getGame().getTileSize()/2,
                axis.x > axis.y ? entityManager.getLevel().getGameState().getGame().getTileSize()/2 : entityManager.getLevel().getGameState().getGame().getTileSize(),
                closed
        );
        this.openedSprite = entityManager.getLevel().getGameState().getGame().getAssetManager().getSprite(openedSprite);
        this.closedSprite = entityManager.getLevel().getGameState().getGame().getAssetManager().getSprite(closedSprite);
        this.requiresKey = requiresKey;
        this.closed = closed;
    }

    @Override
    public void interact(Player player) {
        if (requiresKey) {
            if (!closed) {
                return;
            }
            if (player.getKeys() <= 0) {
                return;
            }
            player.removeKey();
            closed = false;
            setSolid(false);
        } else {
            closed = !closed;
            setSolid(closed);
        }
    }

    @Override
    public void update() {}

    @Override
    public void render(Graphics graphics) {
        Vector2 cameraPosition = entityManager.getLevel().getGameState().getGame().getCamera().getPosition();
        Vector2 screenPosition = position.subtract(cameraPosition);
        Sprite currentSprite = closed ? closedSprite : openedSprite;

        graphics.drawImage(
                currentSprite.getImage(),
                (int) screenPosition.x,
                (int) screenPosition.y,
                (int) size.x,
                (int) size.y,
                null
        );
    }
}
