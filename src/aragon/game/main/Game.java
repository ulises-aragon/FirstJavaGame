package aragon.game.main;

import aragon.game.assets.AssetLoader;
import aragon.game.assets.AssetManager;
import aragon.game.input.InputLoader;
import aragon.game.input.InputLoadingException;
import aragon.game.input.InputManager;
import aragon.game.main.states.GameState;
import aragon.game.main.states.State;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferStrategy;

public class Game implements Runnable {
    private final Logger LOGGER = LogManager.getLogger(Game.class);

    private GameDisplay display;
    private InputManager inputManager;
    private AssetManager assetManager;

    private GameCamera camera;

    private InputLoader inputLoader;
    private AssetLoader assetLoader;

    private BufferStrategy bufferStrategy;
    private Graphics graphics;

    private State gameState;
    private Thread gameThread;

    public String title;

    private final int tileSize;
    private int width, height;

    private final int updatesPerSecond = 60;
    private final int framesPerSecond = 30;

    public Game(String title, int tileSize, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTileSize() { return tileSize; }

    public int getScreenWidth() {
        return width;
    }

    public int getScreenHeight() {
        return height;
    }

    public GameDisplay getDisplay() {
        return display;
    }
    public GameCamera getCamera() { return camera; }

    public AssetManager getAssetManager() { return assetManager; }
    public InputManager getInputManager() { return inputManager; }

    private void init() throws InputLoadingException {
        LOGGER.info("Initializing display.");
        display = new GameDisplay(this, title, width, height);
        LOGGER.info("Building managers.");
        inputManager = InputManager.build(this);
        assetManager = AssetManager.build();

        camera = new GameCamera(this);

        // Build loaders.
        LOGGER.info("Building loaders.");
        inputLoader = InputLoader.build(this);
        assetLoader = AssetLoader.build(this);

        // Load assets.
        LOGGER.info("Initializing loaders.");
        inputLoader.initialize();
        assetLoader.initializeAsync()
                .thenRun(() -> {
                    // Start.
                    gameState = new GameState(this);
                    State.setState(gameState);
                })
                .exceptionally(exception -> {
                    LOGGER.error("Asset loading failed.", exception);
                    return null;
                });
    }

    private void update() {
        if (State.getState() != null) State.getState().update();
        inputManager.update();
    }

    private void render() {
        bufferStrategy = display.getCanvas().getBufferStrategy();
        if (bufferStrategy == null) {
            display.getCanvas().createBufferStrategy(3);
            return;
        }
        graphics = bufferStrategy.getDrawGraphics();
        // Render.
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, width, height);
        if (State.getState() != null) State.getState().render(graphics);
        // Dispose.
        bufferStrategy.show();
        graphics.dispose();
    }

    @Override
    public void run() {
        double updateInterval = 1_000_000_000 / (double)updatesPerSecond;
        double renderInterval = 1_000_000_000 / (double)framesPerSecond;

        double renderDelta = 0, updateDelta = 0;
        int frames = 0, ticks = 0;
        long lastTime = System.nanoTime();

        long timer = System.currentTimeMillis();

        try {
            init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            updateDelta += (currentTime - lastTime) / updateInterval;
            renderDelta += (currentTime - lastTime) / renderInterval;
            lastTime = currentTime;

            if (updateDelta >= 1) {
                update();
                ticks++;
                updateDelta--;
            }

            if (renderDelta >= 1) {
                render();
                frames++;
                renderDelta--;
            }

            if (System.currentTimeMillis() - timer > 1_000) {
                frames = 0;
                ticks = 0;
                timer += 1_000;
            }
        }

        abort();
    }

    public synchronized void launch() {
        if (gameThread != null) return;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public synchronized void abort() {
        if (gameThread == null) return;
        assetLoader.cleanup();
        try {
            gameThread.join();
            gameThread = null;
        } catch (InterruptedException e) {
            LOGGER.fatal(e);
        }
    }
}
