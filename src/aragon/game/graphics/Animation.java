package aragon.game.graphics;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Animation {
    private final int frameDelay;
    private final int totalFrames;
    private AnimationPriority priority;
    private int currentFrame;
    private int frameCount;

    private boolean stopped;
    private boolean loops;
    private double speed;

    private final List<AnimationFrame> frames = new ArrayList<>();

    public Animation(Sprite[] frames, AnimationPriority priority, int frameDelay, boolean loops) {
        this.frameDelay = frameDelay;
        this.stopped = true;
        this.loops = loops;

        for (Sprite sprite : frames) {
            addFrame(sprite, frameDelay);
        }

        this.speed = 1;
        this.frameCount = 0;
        this.currentFrame = 0;
        this.totalFrames = this.frames.size();
    }

    public void setLooping(boolean loops) { this.loops = loops; }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean isPlaying() {
        return !stopped;
    }

    public void play() {
        if (!stopped) return;
        if (frames.isEmpty()) return;
        stopped = false;
    }

    public void stop() {
        if (frames.isEmpty()) return;
        stopped = true;
    }

    public void restart() {
        if (frames.isEmpty()) return;
        currentFrame = 0;
        stopped = false;
    }

    public void reset() {
        this.stopped = true;
        this.frameCount = 0;
        this.currentFrame = 0;
    }

    private void addFrame(Sprite sprite, int duration) {
        frames.add(new AnimationFrame(sprite, duration));
        currentFrame = 0;
    }

    public Sprite getSprite() {
        return frames.get(currentFrame).getSprite();
    }

    public void update() {
        if (stopped) return;
        frameCount++;

        int delay = Math.max((int)Math.floor(frameDelay / speed), 1);

        if (frameCount < delay) return;
        frameCount = 0;
        currentFrame++;

        if (currentFrame > totalFrames - 1) {
            if (!loops) {
                currentFrame = totalFrames - 1;
                stop();
            } else {
                currentFrame = 0;
            }
        }
    }
}
