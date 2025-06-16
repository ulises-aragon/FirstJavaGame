package aragon.game.graphics;

public class AnimationFrame {
    private Sprite sprite;
    private int duration;

    public AnimationFrame(Sprite sprite, int duration) {
        this.sprite = sprite;
        this.duration = duration;
    }

    public Sprite getSprite() {
         return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
