package aragon.game.util;

public class Vector2 {
    public static Vector2 one = new Vector2(1, 1);
    public static Vector2 zero = new Vector2();
    public static Vector2 xAxis = new Vector2(1, 0);
    public static Vector2 yAxis = new Vector2(0, 1);

    public double x, y;

    public Vector2() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2(Vector2 other) {
        this.x = other.x;
        this.y = other.y;
    }

    public Vector2(double xy) {
        x = xy;
        y = xy;
    }

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 add(Vector2 other) { return new Vector2(this.x + other.x, this.y + other.y); }

    public Vector2 subtract(Vector2 other) { return new Vector2(this.x - other.x, this.y - other.y); }

    public Vector2 multiply(Vector2 other) { return new Vector2(this.x * other.x, this.y * other.y); }

    public Vector2 scale(double scalar) { return new Vector2(this.x * scalar, this.y * scalar); }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public double dot(Vector2 other) {
        return this.x * other.x + this.y * other.y;
    }

    public Vector2 normalize() {
        double mag = magnitude();
        if (mag == 0) {
            return this;
        }
        return new Vector2( this.x / mag, this.y / mag);
    }

    @Override
    public String toString() {
        return "Vector2D[ x: " + x + ", y: " + y + " ]";
    }
}
