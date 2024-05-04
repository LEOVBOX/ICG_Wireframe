package core;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class RotationFigure {
    public BSpline spline;
    public Object3D object3D;
    public int M;

    public RotationFigure() {
        this.spline = new BSpline();
        this.object3D = new Object3D();
        this.M = 4;
    }

    public RotationFigure(int M, BSpline spline) {
        this.M = M;
        this.spline = spline;
    }

    public BSpline getSpline() {
        return spline;
    }

    public void setSpline(BSpline spline) {
        this.spline = spline;
    }

    public void setM(int M) {
        this.M = M;
    }

    public Point3D getRotatedPoint(Point2D point, int j) {
        double angle = Math.toRadians((double)(j*360)/M);
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);
        double rY = point.getY() * sinAngle;
        double rX = point.getY() * cosAngle;
        double rZ = point.getX();
        return new Point3D(rX, rY, rZ);
    }

    private ArrayList<Point3D> getGeneratix(int j) {
        ArrayList<Point3D> generatrix = new ArrayList<>();
        for (Point2D point: spline.approximationPoints) {
            generatrix.add(getRotatedPoint(point, j));
        }
        return generatrix;
    }

    public void getObject3D() {
        object3D = new Object3D();
        for (int j = 0; j < M; j++) {
            ArrayList<Point3D> generatrix = getGeneratix(j);
            for (int i = 0; i < generatrix.size(); i++) {
                object3D.addPoint(generatrix.get(i));
                if (object3D.getPoints().size() > 1 && i > 0)
                    object3D.addEdge(object3D.getPoints().size() - 2, object3D.getPoints().size() - 1);
                if (j > 0)
                    object3D.addEdge(object3D.getPoints().size() - generatrix.size() - 1, object3D.getPoints().size() - 1);
            }
        }

        if (M > 1) {
            for (int i = 0; i < spline.approximationPoints.size(); i++)
            {
                object3D.addEdge(spline.approximationPoints.size() * (M - 1) + i, i);
            }
        }
        object3D = object3D.normalize();
    }

}