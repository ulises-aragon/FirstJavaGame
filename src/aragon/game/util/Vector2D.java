package aragon.game.util;

public class Vector2D {
    public static Vector2D one = new Vector2D(1, 1);
    public static Vector2D zero = new Vector2D();
    public static Vector2D xAxis = new Vector2D(1, 0);
    public static Vector2D yAxis = new Vector2D(0, 1);

    public double x, y;

    public Vector2D() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2D(Vector2D other) {
        this.x = other.x;
        this.y = other.y;
    }

    public Vector2D(double xy) {
        x = xy;
        y = xy;
    }

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D add(Vector2D other) { return new Vector2D(this.x + other.x, this.y + other.y); }

    public Vector2D subtract(Vector2D other) { return new Vector2D(this.x - other.x, this.y - other.y); }

    public Vector2D scale(double scalar) { return new Vector2D(this.x * scalar, this.y * scalar); }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public double dot(Vector2D other) {
        return this.x * other.x + this.y * other.y;
    }

    public Vector2D normalize() {
        double mag = magnitude();
        if (mag == 0) {
            return this;
        }
        return new Vector2D( this.x / mag, this.y / mag);
    }

    @Override
    public String toString() {
        return "Vector2D[ x: " + x + ", y: " + y + " ]";
    }
}
