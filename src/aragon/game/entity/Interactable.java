package aragon.game.entity;

import aragon.game.entity.player.Player;

public interface Interactable {
    void interact(Player player);
    boolean canInteract(Player player);
}
