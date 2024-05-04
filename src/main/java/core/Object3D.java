package core;

import LinearAlgebra.MatrixUtils;

import java.util.ArrayList;

class Object3D {
    private final ArrayList<Point3D> points;

    public ArrayList<Point3D> getPoints() {
        return points;
    }

    public ArrayList<Integer[]> getEdges() {
        return edges;
    }

    private final ArrayList<Integer[]> edges;

    public Object3D() {
        points = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public Object3D(Object3D object3D) {
        points = new ArrayList<>();
        for (Point3D point3D : object3D.getPoints()) {
            points.add(new Point3D(point3D.getX(), point3D.getY(), point3D.getZ()));
        }
        edges = new ArrayList<>(object3D.edges);
    }

    public void addPoint(Point3D point) {
        points.add(point);
    }

    public void addEdge(int point1, int point2) {
        if (points.size() < point2 || points.size() < point1) {
            throw new IllegalArgumentException("Points of edge should be already added in model");
        }

        edges.add(new Integer[]{point1, point2});
    }

    // [minCord, maxCord]
    private ArrayList<Point3D> calcDimensionalBox() {
        if (points.isEmpty()) {
            throw new IllegalArgumentException("For calculation dimensional box needed at least 1 point");
        }

        ArrayList<Point3D> box = new ArrayList<>();
        Point3D maxPoint = new Point3D(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
        Point3D minPoint = new Point3D(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);

        for (int i = 1; i < points.size(); i++) {
            maxPoint = new Point3D(Math.max(maxPoint.getX(), points.get(i).getX()),
                    Math.max(maxPoint.getY(), points.get(i).getY()),
                    Math.max(maxPoint.getZ(), points.get(i).getZ())
            );
            minPoint = new Point3D(Math.min(minPoint.getX(), points.get(i).getX()),
                    Math.min(minPoint.getY(), points.get(i).getY()),
                    Math.min(minPoint.getZ(), points.get(i).getZ())
            );
        }

        box.add(minPoint);
        box.add(maxPoint);
        return box;
    }

    public Object3D scale(double scaleFactor) {
        Object3D result = new Object3D(this);
        for (Point3D point : result.points) {
            point.setLocation(new Point3D(point.getX() * scaleFactor,
                    point.getY() * scaleFactor,
                    point.getZ() * scaleFactor
            ));
        }

        return result;
    }

    public Object3D move(Point3D center) {
        Object3D result = new Object3D(this);
        for (Point3D point3D : result.points) {

            point3D.setLocation(
                    point3D.getX() - center.getX(),
                    point3D.getY() - center.getY(),
                    point3D.getZ() - center.getZ()
            );
        }
        return result;
    }


    public Object3D normalize() {
        // dimensionalBox = [minCord, maxCord]
        ArrayList<Point3D> dimensionalBox = calcDimensionalBox();
        Object3D result = new Object3D(this);

        // Находим размеры исходной фигуры вдоль каждой оси
        double sizeX = dimensionalBox.getLast().getX() - dimensionalBox.getFirst().getX();
        double sizeY = dimensionalBox.getLast().getY() - dimensionalBox.getFirst().getY();
        double sizeZ = dimensionalBox.getLast().getZ() - dimensionalBox.getFirst().getZ();

        double scaleFactor = 2 / Math.max(sizeX, Math.max(sizeY, sizeZ));

        Point3D center = new Point3D(
                0,
                0,
                (dimensionalBox.getFirst().getZ() + dimensionalBox.getLast().getZ()) / 2
        );

        result = result.move(center);
        result = result.scale(scaleFactor);
        return result;
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

    private Point3D rotatePoint(Point3D point3D, double rotateX, double rotateY) {
        double[] cords = {point3D.getX(), point3D.getY(), point3D.getZ()};
        cords = MatrixUtils.multiply(getYRotationMatrix(rotateY), cords, false);
        cords = MatrixUtils.multiply(getXRotationMatrix(rotateX), cords, false);
        return new Point3D(cords[0], cords[1], cords[2]);
    }

    public Object3D rotateObject(double rotateX, double rotateY) {
        Object3D result = new Object3D(this);
        for (Point3D point3D : result.getPoints()) {
            point3D.setLocation(rotatePoint(point3D, rotateX, rotateY));
        }
        return result;
    }


}
