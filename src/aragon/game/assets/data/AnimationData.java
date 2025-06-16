package aragon.game.assets.data;

import aragon.game.graphics.AnimationPriority;
import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class AnimationData {
    @Min(value=0, message="Animation row must be a positive number.")
    private int row = 0;

    @SerializedName("from")
    @Min(value=0, message="Animation starting column must be a positive number.")
    private int startCol = 0;

    @SerializedName("to")
    @Min(value=0, message="Animation ending column must be a positive number.")
    private int endCol = 0;

    @Min(value=1, message="Frame delay must be at least 1.")
    private int frameDelay = 2;

    @SerializedName("priority")
    @NotBlank(message="Animation must have a priority")
    private String animationPriority;

    private boolean loops = false;

    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }

    public int getStartCol() { return startCol; }
    public void setStartCol(int startCol) { this.startCol = startCol; }

    public int getEndCol() { return endCol; }
    public void setEndCol(int endCol) { this.endCol = endCol; }

    public int getFrameDelay() { return frameDelay; }
    public void setFrameDelay(int frameDelay) { this.frameDelay = frameDelay; }

    public boolean isLooping() { return loops; }
    public void setLooping(boolean loops) { this.loops = loops; }

    public AnimationPriority getResolvedPriority() {
        return AnimationPriority.get(animationPriority);
    }

    public int getFrameCount() {
        return endCol - startCol + 1;
    }

    public void validate() {
        if (endCol <= startCol) {
            throw new IllegalArgumentException(
                    String.format("Invalid animation range: from=%d, to=%d", startCol, endCol)
            );
        }
        AnimationPriority priority = getResolvedPriority();
        if (priority == null) {
            throw new IllegalArgumentException(
                    String.format("Invalid animation priority: %s", animationPriority)
            );
        }
    }
}
