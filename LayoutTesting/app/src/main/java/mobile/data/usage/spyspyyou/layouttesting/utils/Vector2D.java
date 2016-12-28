package mobile.data.usage.spyspyyou.layouttesting.utils;

// a general vector class useful for the hit box resolution
public class Vector2D {

    // coordinates the vector points to
    public double x, y;

    public Vector2D(Vector2D vector2D) {
        this.x = vector2D.x;
        this.y = vector2D.y;
    }

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vector2D vector2D) {
        this.x = vector2D.x;
        this.y = vector2D.y;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // make the vector length 1
    public void normalize() {
        double abs = Math.sqrt(x * x + y * y);
        x /= abs;
        y /= abs;
    }

    // multiply the vectors length with the scalar
    public Vector2D scale(double scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    // set the length to newLength
    public Vector2D scaleTo(double newLength) {
        normalize();
        scale(newLength);
        return this;
    }

    // return the length of the vector
    public double getLength() {
        return Math.sqrt(x * x + y * y);
    }

    public boolean has0Length(){
        return x == 0 && y == 0;
    }

    public void add(Vector2D vector) {
        x += vector.x;
        y += vector.y;
    }

    public void add(double x, double y) {
        this.x += x;
        this.y += y;
    }

    public float getFloatX(){
        return (float)x;
    }

    public float getFloatY(){
        return (float)y;
    }

    public int getIntX(){
        return (int)x;
    }

    public int getIntY(){
        return (int)y;
    }
}

