package aragon.game.entity;

import aragon.game.entity.player.Player;
import aragon.game.level.Level;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EntityManager {
    private final Level level;
    private Player player;
    private final List<Entity> entities= new ArrayList<>();
    private final Comparator<Entity> renderSorter = Comparator.comparingDouble(a -> a.position.y + a.size.y);

    public EntityManager(Level level) {
        this.level = level;
    }

    public void update() {
        for (int i=0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            entity.update();

            if (!entity.isAlive()) entities.remove(entity);
        }
        entities.sort(renderSorter);
    }

    public void render(Graphics graphics) {
        for (Entity entity : entities) {
            entity.render(graphics);
        }
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        if (entity instanceof Player) {
            this.player = (Player) entity;
        }
    }

    public List<Entity> getEntities() {
        return new ArrayList<>(entities);
    }

    public List<Interactable> getInteractablesInBounds(Rectangle interactionBox) {
        return entities.stream()
                .filter(entity -> entity instanceof Interactable)
                .filter(entity -> entity.getCollisionBounds().intersects(interactionBox))
                .map(entity -> (Interactable) entity)
                .collect(Collectors.toList());
    }

    public Level getLevel() {
        return level;
    }

    public Player getPlayer() {
        return player;
    }
}
