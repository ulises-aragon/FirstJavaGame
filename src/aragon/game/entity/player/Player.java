package aragon.game.entity.player;

import aragon.game.assets.AssetLoader;
import aragon.game.entity.Entity;
import aragon.game.graphics.Animation;
import aragon.game.graphics.Sprite;
import aragon.game.input.InputActionEventListener;
import aragon.game.input.InputManager;
import aragon.game.main.GameHandler;
import aragon.game.main.states.State;
import aragon.game.util.Vector2D;

import java.awt.*;

public class Player extends Entity implements InputActionEventListener {
    private static final AssetLoader assetLoader = AssetLoader.get();

    private static final Animation idleAnimation = assetLoader.getAnimation("player.idle");
    private static final Animation walkAnimation = assetLoader.getAnimation("player.walk");
    private static final Animation runAnimation = assetLoader.getAnimation("player.run");
    private static final Animation sitAnimation = assetLoader.getAnimation("player.sit");
    private static final Animation standAnimation = assetLoader.getAnimation("player.stand");

    private static final int defaultSpeed = 3;
    private static final int walkSpeed = 1;

    private final InputManager inputManager;
    private Animation animation = idleAnimation;
    private int speed;
    private int moveXAxis = 0;
    private int moveYAxis = 0;
    private boolean flipped = false;
    private boolean isWalking = false;

    public Player(GameHandler gameHandler, int x, int y, int width, int height) {
        super(gameHandler, x, y, width, height);
        speed = defaultSpeed;
        inputManager = gameHandler.getInputManager();

        inputManager.addCategoryListener("movement", this);

        animation.restart();
    }

    public Player(GameHandler gameHandler, int x, int y, int size) {
        this(gameHandler, x, y, size, size);
    }

    @Override
    public void onActionTriggered(String actionName) {
        switch (actionName) {
            case("player_up") -> {
                moveYAxis -= 1;
            }
            case("player_down") -> {
                moveYAxis += 1;
            }
            case("player_right") -> {
                moveXAxis += 1;
            }
            case("player_left") -> {
                moveXAxis -= 1;
            }
            case("player_walk") -> {
                speed = walkSpeed;
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
                speed = defaultSpeed;
                isWalking = false;
                standAnimation.setSpeed(1);
            }
        }
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

    public Vector2D getMoveVector() {
        return new Vector2D(moveXAxis, moveYAxis).normalize();
    }

    @Override
    public void update() {
        Vector2D moveVector = getMoveVector();

        if (moveVector.x > 0) {
            flipped = false;
        } else if (moveVector.x < 0) {
            flipped = true;
        }

        Animation targetAnimation = getAnimation(moveVector.magnitude() > 0);
        if (targetAnimation != animation) {
            animation.reset();
            animation = targetAnimation;
            animation.restart();
        }

        position = position.add(moveVector.scale(speed));
        animation.update();
    }

    @Override
    public void render(Graphics graphics) {
        Vector2D offset = gameHandler.getCamera().getPosition();
        Sprite sprite = flipped ? animation.getSprite().flipHorizontal() : animation.getSprite();
        graphics.drawImage(sprite.getImage(), (int) (position.x - offset.x), (int) (position.y - offset.y), (int) size.x, (int) size.y, null);
    }
}
