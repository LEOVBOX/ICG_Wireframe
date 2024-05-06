package core;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class RotationFigure {
    public BSpline spline;
    public Object3D object3D;
    private int M;

    private int M1;

    public RotationFigure() {
        this.spline = new BSpline();
        this.M = 4;
        this.M1 = 1;
    }

    public RotationFigure(int M, BSpline spline) {
        this.M = M;
        this.spline = spline;
        this.M1 = 1;
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

    public int getM() {
        return M;
    }

    public void setM1(int M1) {
        this.M1 = M1;
    }

    public int getM1() {
        return M1;
    }

    public Point3D getRotatedPoint(Point2D point, int j) {
        double angle = Math.toRadians((double) (j * 360) / M);
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);
        double rY = point.getY() * sinAngle;
        double rX = point.getY() * cosAngle;
        double rZ = point.getX();
        return new Point3D(rX, rY, rZ);
    }


    // Нахождение точек аппроксимации окружности
    private ArrayList<Point3D> getApproxPoints(Point3D point) {
        ArrayList<Point3D> result = new ArrayList<>();
        double angleBetweenGeneratix = Math.toRadians((double) 360 / M);
        double aproxPointAgnle = angleBetweenGeneratix / M1;
        for (int i = 1; i < M1; i++) {
            Point3D newPoint = new Point3D(point.getX(), point.getY(), point.getZ());
            newPoint = newPoint.rotate(0, 0, aproxPointAgnle * i);
            result.add(newPoint);
        }
        return result;
    }

    private ArrayList<Point3D> getGeneratix(int j) {
        ArrayList<Point3D> generatrix = new ArrayList<>();
        for (Point2D point : spline.approximationPoints) {
            generatrix.add(getRotatedPoint(point, j));
        }
        return generatrix;
    }

    private void connectGeneratix(Object3D object3D, ArrayList<Point3D> generatix) {
        boolean createEdge = false;
        for (Point3D point : generatix) {
            object3D.addPoint(point);
            if (createEdge) {
                object3D.addEdge(object3D.getPoints().size() - 1, object3D.getPoints().size() - 2);
            } else if (generatix.indexOf(point) == 0) {
                createEdge = true;
            }
        }
    }

    private void createCircle(Object3D object3D, int layer) {
        for (int angle = 0; angle < M; angle++) {
            int point1 = (spline.approximationPoints.size() * angle + layer) % object3D.getPoints().size();
            int lastPointIdx = point1;
            if (M1 > 1) {
                ArrayList<Point3D> additionalPoints = getApproxPoints(object3D.getPoints().get(point1));
                for (Point3D additionalPoint : additionalPoints) {
                    object3D.addPoint(additionalPoint);
                    object3D.addEdge(object3D.getPoints().size() - 1, lastPointIdx);
                    lastPointIdx = object3D.getPoints().size() - 1;
                }
            }

            int point2 = (spline.approximationPoints.size() * (angle + 1) + layer) % (M*spline.approximationPoints.size());
            object3D.addEdge(lastPointIdx, point2);
        }
    }

    public void getObject3D() {
        spline.caclApproximation();
        object3D = new Object3D();
        for (int j = 0; j < M; j++) {
            ArrayList<Point3D> generatrix = getGeneratix(j);
            connectGeneratix(object3D, generatrix);
        }
        int step = spline.approximationPoints.size()/spline.referencePoints.size();
        if (step == 0)
            step = 1;

        for (int i = 0; i < spline.approximationPoints.size() - 1; i+=step) {
            createCircle(object3D, i);
        }

        createCircle(object3D, spline.approximationPoints.size() - 1);



        object3D = object3D.normalize();
    }

}