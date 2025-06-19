package aragon.game.entity.interactable;

import aragon.game.entity.Entity;
import aragon.game.entity.EntityManager;
import aragon.game.entity.Interactable;
import aragon.game.entity.player.Player;
import aragon.game.util.Vector2;

public abstract class InteractableEntity extends Entity implements Interactable {
    private int interactionRange;

    public InteractableEntity(EntityManager entityManager, int x, int y, int w, int h, boolean solid) {
        super(entityManager, x, y, w, h, solid);
        this.interactionRange = entityManager.getLevel().getGameState().getGame().getTileSize();
    }

    public InteractableEntity(EntityManager entityManager, int x, int y, int size, boolean solid) {
        this(entityManager, x, y, size, size, solid);
    }

    public void setInteractionRange(int interactionRange) {
        this.interactionRange = interactionRange;
    }

    @Override
    public boolean canInteract(Player player) {
        Vector2 playerCenter = player.position.add(player.getSize().scale(0.5));
        Vector2 interactableCenter = position.add(size.scale(0.5));
        return playerCenter.subtract(interactableCenter).magnitude() <= interactionRange;
    }
}
