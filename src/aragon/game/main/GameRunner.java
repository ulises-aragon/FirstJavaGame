package aragon.game.main;

public class GameRunner {
    public static void main(String[] args) {
        Game instance = new Game("This is my game.", 64, 256 * 4, 192 * 4);
        instance.launch();
    }
}
