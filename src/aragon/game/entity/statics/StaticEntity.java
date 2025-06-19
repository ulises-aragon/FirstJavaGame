package aragon.game.entity.statics;

import aragon.game.entity.Entity;
import aragon.game.entity.EntityManager;

public abstract class StaticEntity extends Entity {
    public StaticEntity(EntityManager entityManager, int x, int y, int w, int h, boolean solid) {
        super(entityManager, x, y, w, h, solid);
    }
}
