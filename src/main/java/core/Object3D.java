package core;

import java.util.ArrayList;

class Object3D {
    private ArrayList<Point3D> points;

    public ArrayList<Point3D> getPoints() {
        return points;
    }

    public ArrayList<Integer[]> getEdges() {
        return edges;
    }

    private ArrayList<Integer[]> edges;

    public Object3D() {
        points = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public Object3D(Object3D object3D) {
        points = new ArrayList<>();
        for (Point3D point3D: object3D.getPoints()) {
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

}
