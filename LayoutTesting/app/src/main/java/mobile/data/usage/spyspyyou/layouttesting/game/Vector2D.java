package mobile.data.usage.spyspyyou.layouttesting.game;

// a general vector class useful for the hit box resolution
public class Vector2D {

    // coordinates the vector points to
    public double x, y;

    public Vector2D(double x, double y) {
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
    public void scale(double scalar) {
        x *= scalar;
        y *= scalar;
    }

    // set the length to newLength
    public void scaleTo(double newLength) {
        normalize();
        scale(newLength);
    }

    // return the length of the vector
    public double getLength() {
        return Math.sqrt(x * x + y * y);
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

