package core;

import LinearAlgebra.MatrixUtils;

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

    public void setLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    double[][] getYRotationMatrix(double phi) {
        return new double[][]{
                {Math.cos(phi), 0, Math.sin(phi)},
                {0, 1, 0},
                {-Math.sin(phi), 0, Math.cos(phi)}
        };
    }

    double[][] getXRotationMatrix(double theta) {
        return new double[][]{
                {1, 0, 0},
                {0, Math.cos(theta), -Math.sin(theta)},
                {0, Math.sin(theta), Math.cos(theta)}
        };
    }

    double[][] getZRotationMatrix(double theta) {
        return new double[][]{
                {Math.cos(theta), -Math.sin(theta), 0},
                {Math.sin(theta), Math.cos(theta), 0},
                {0, 0, 1}
        };
    }

    public Point3D rotateX(double rotateX) {
        double[] cords = {getX(), getY(), getZ()};
        cords = MatrixUtils.multiply(getXRotationMatrix(rotateX), cords, false);
        return new Point3D(cords[0], cords[1], cords[2]);
    }

    public Point3D rotateY(double rotateY) {
        double[] cords = {getX(), getY(), getZ()};
        cords = MatrixUtils.multiply(getYRotationMatrix(rotateY), cords, false);
        return new Point3D(cords[0], cords[1], cords[2]);
    }


    public Point3D rotate(double rotateX, double rotateY, double rotateZ) {
        double[] cords = {getX(), getY(), getZ()};
        cords = MatrixUtils.multiply(getXRotationMatrix(rotateX), cords, false);
        cords = MatrixUtils.multiply(getYRotationMatrix(rotateY), cords, false);
        cords = MatrixUtils.multiply(getZRotationMatrix(rotateZ), cords, false);
        return new Point3D(cords[0], cords[1], cords[2]);
    }


}
