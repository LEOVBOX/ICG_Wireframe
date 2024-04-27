package core;

public class Point3D {
    private double x;
    private double y;
    private double z;


    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setLocation(Point3D p) {
        this.x = p.getX();
        this.y = p.getY();
        this.z = p.getZ();
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }


}
