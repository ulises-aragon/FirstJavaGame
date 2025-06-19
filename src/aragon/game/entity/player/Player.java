package aragon.game.entity.player;

import aragon.game.entity.*;
import aragon.game.graphics.Animation;
import aragon.game.graphics.Sprite;
import aragon.game.input.InputActionEventListener;
import aragon.game.main.Game;
import aragon.game.util.Vector2;

import java.awt.*;
import java.util.List;

public class Player extends Creature implements InputActionEventListener {
    private static final int DEFAULT_SPEED = 3;
    private static final int WALK_SPEED = 1;

    private final Animation idleAnimation;
    private final Animation walkAnimation;
    private final Animation runAnimation;
    private final Animation sitAnimation;
    private final Animation standAnimation;

    private Animation animation;
    private boolean flipped = false;
    private boolean isWalking = false;

    private int keys = 0;
    private final Rectangle interactionBounds = new Rectangle(16, 16);

    public Player(EntityManager entityManager, int width, int height) {
        super(entityManager, 0, 0, width, height);
        collisionBounds = new Rectangle(width/3, (int)(height/1.2), width/3, (int) (height-(height/1.2)));
        moveSpeed = DEFAULT_SPEED;

        Game game = entityManager.getLevel().getGameState().getGame();
        idleAnimation = game.getAssetManager().getAnimation("player.idle");
        walkAnimation = game.getAssetManager().getAnimation("player.walk");
        runAnimation = game.getAssetManager().getAnimation("player.run");
        sitAnimation = game.getAssetManager().getAnimation("player.sit");
        standAnimation = game.getAssetManager().getAnimation("player.stand");
        animation = idleAnimation;
        animation.restart();

        game.getInputManager().addCategoryListener("player_control", this);
    }

    public Player(EntityManager entityManager, int size) {
        this(entityManager, size, size);
    }

    @Override
    public void onActionTriggered(String actionName) {
        switch (actionName) {
            case("player_up") -> moveYAxis -= 1;
            case("player_down") -> moveYAxis += 1;
            case("player_right") -> moveXAxis += 1;
            case("player_left") -> moveXAxis -= 1;
            case("player_interact") -> handleInteraction();
            case("player_walk") -> {
                moveSpeed = WALK_SPEED;
                isWalking = true;
                standAnimation.setSpeed(0.3);
            }
        }
    }

    @Override
    public void onActionReleased(String actionName) {
        switch (actionName) {
            case("player_up") -> {
                moveYAxis += 1;
            }
            case("player_down") -> {
                moveYAxis -= 1;
            }
            case("player_right") -> {
                moveXAxis -= 1;
            }
            case("player_left") -> {
                moveXAxis += 1;
            }
            case("player_walk") -> {
                moveSpeed = DEFAULT_SPEED;
                isWalking = false;
                standAnimation.setSpeed(1);
            }
        }
    }

    public Rectangle getInteractionBounds() {
        return getInteractionBounds(0, 0);
    }

    public Rectangle getInteractionBounds(int xOffset, int yOffset) {
        Rectangle collisionBox = getCollisionBounds(xOffset, yOffset);
        FacingDirection facingDirection = getFacingDirection();

        return switch(facingDirection) {
            case RIGHT -> new Rectangle(
                    (int) (collisionBox.x + collisionBox.width + interactionBounds.width*0.5),
                    (int) (collisionBox.y + collisionBox.height*0.5 - interactionBounds.height*0.5),
                    interactionBounds.width,
                    interactionBounds.height
            );
            case LEFT -> new Rectangle(
                    (int) (collisionBox.x - interactionBounds.width*1.5),
                    (int) (collisionBox.y + collisionBox.height*0.5 - interactionBounds.height*0.5),
                    interactionBounds.width,
                    interactionBounds.height
            );
            case UP -> new Rectangle(
                    (int) (collisionBox.x + collisionBox.width*0.5 - interactionBounds.width*0.5),
                    (int) (collisionBox.y - interactionBounds.height*1.5),
                    interactionBounds.width,
                    interactionBounds.height
            );
            case DOWN -> new Rectangle(
                    (int) (collisionBox.x + collisionBox.width*0.5 - interactionBounds.width*0.5),
                    (int) (collisionBox.y + collisionBox.height + interactionBounds.height*0.5),
                    interactionBounds.width,
                    interactionBounds.height
            );
        };
    }

    private Animation getAnimation(boolean isMoving) {
        Animation targetAnimation = animation;

        if (isMoving) {
            if (animation == standAnimation && !animation.isPlaying()) {
                targetAnimation = isWalking ? walkAnimation : runAnimation;
            } else if (animation != runAnimation && animation != walkAnimation) {
                targetAnimation = standAnimation;
            } else if (animation != standAnimation) {
                targetAnimation = isWalking ? walkAnimation : runAnimation;
            }
        } else {
            if (animation == sitAnimation && !animation.isPlaying()) {
                targetAnimation = idleAnimation;
            } else if (animation != idleAnimation) {
                targetAnimation = sitAnimation;
            }
        }

        return targetAnimation;
    }

    private void handleInteraction() {
        Rectangle interactionBox = getInteractionBounds();
        List<Interactable> interactablesInBounds = entityManager.getLevel()
                .getEntityManager().getInteractablesInBounds(interactionBox);

        Interactable closestInteractable = null;
        double closestDistance = Double.MAX_VALUE;

        for (Interactable interactable : interactablesInBounds) {
            if (interactable.canInteract(this)) {
                Vector2 playerCenter = position.add(size.scale(0.5));
                Vector2 interactableCenter = ((Entity) interactable).position
                        .add(((Entity) interactable).getSize().scale(0.5));

                double distance = playerCenter.subtract(interactableCenter).magnitude();

                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestInteractable = interactable;
                }
            }
        }

        if (closestInteractable != null) {
            closestInteractable.interact(this);
        }
    }

    public void addKey() {
        keys++;
    }

    public void removeKey() {
        if (keys <= 0) return;
        keys--;
    }

    public int getKeys() {
        return keys;
    }

    @Override
    public void update() {
        Vector2 moveVector = getMoveVector();

        if (facingDirection.x > 0) {
            flipped = false;
        } else if (facingDirection.x < 0) {
            flipped = true;
        }

        Animation targetAnimation = getAnimation(moveVector.magnitude() > 0);
        if (targetAnimation != animation) {
            animation.reset();
            animation = targetAnimation;
            animation.restart();
        }

        animation.update();
        move();
        entityManager.getLevel().getGameState().getGame().getCamera().setCameraSubject(this);
    }

    @Override
    public void render(Graphics graphics) {
        Vector2 cameraPosition = entityManager.getLevel().getGameState().getGame().getCamera().getPosition();
        Vector2 worldPosition = position.subtract(cameraPosition);
        Sprite sprite = flipped ? animation.getSprite().flipHorizontal() : animation.getSprite();

        graphics.drawImage(
                sprite.getImage(),
                (int) worldPosition.x,
                (int) worldPosition.y,
                (int) size.x,
                (int) size.y,
                null
        );
    }

    private void renderDebug(Graphics graphics, Vector2 cameraPosition) {
        Vector2 invertedCameraPosition = cameraPosition.scale(-1);
        Rectangle collisionBox = getCollisionBounds((int) invertedCameraPosition.x, (int) invertedCameraPosition.y);
        Rectangle interactionBox = getInteractionBounds((int) invertedCameraPosition.x, (int) invertedCameraPosition.y);

        graphics.setColor(Color.RED);
        graphics.drawRect(
                collisionBox.x,
                collisionBox.y,
                collisionBox.width,
                collisionBox.height
        );

        graphics.setColor(Color.CYAN);
        graphics.drawRect(
                interactionBox.x,
                interactionBox.y,
                interactionBox.width,
                interactionBox.height
        );
    }
}
