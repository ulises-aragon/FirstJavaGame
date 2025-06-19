package aragon.game.entity;

import aragon.game.entity.player.Player;

public interface Collectible {
    void onCollect(Player player);
    boolean canCollect(Player player);
}
