package aragon.game.assets.data;

import com.google.gson.annotations.SerializedName;

public class SpriteData {
    private String path;

    @SerializedName("sheet")
    private SpriteReference spriteReference;

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public SpriteReference getSpriteSheetReference() { return spriteReference; }

    public boolean isPathFile() { return path.contains("/") || path.matches(".*\\.(png|jpg|jpeg|json)$"); }

    public boolean isStandaloneSprite() { return path != null && !path.trim().isEmpty() && isPathFile(); }

    public boolean isSheetSprite() { return path == null && getSpriteSheetReference() != null; }

    public boolean isReferencedSheetSprite() { return path != null && !path.trim().isEmpty() && !isPathFile() && getSpriteSheetReference() != null; }

    public boolean isValid() { return isStandaloneSprite() ^ isSheetSprite() ^ isReferencedSheetSprite(); }
}