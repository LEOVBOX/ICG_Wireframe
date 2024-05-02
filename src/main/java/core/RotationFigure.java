package core;

public class RotationFigure {
    BSpline spline;

    Object3D object3D;

    int M;

    public RotationFigure(int M, BSpline spline) {
        this.M = M;
        this.spline = spline;
    }

    // i - number of generatix
    // j - number of rotation angle.
    public Double Rx(int i, int j) {
        return spline.approximationPoints.get(i).getY() * Math.toRadians(Math.cos((double)(j*360)/M));
    }
    public Double Ry(int i, int j) {
        return spline.approximationPoints.get(i).getY() * Math.toRadians(Math.sin((double)(j*360)/M));
    }
    public Double Rz(int i) {
        return spline.approximationPoints.get(i).getX();
    }

    public Point3D getRotationFigurePoint(int i, int j) {
        double angle = Math.toRadians(Math.cos((double)(j*360)/M));
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);
        double rY = spline.approximationPoints.get(i).getY() * sinAngle;
        double rX = spline.approximationPoints.get(i).getY() * cosAngle;
        double rZ = spline.approximationPoints.get(i).getX();
        return new Point3D(rX, rY, rZ);
    }

    public void getObject3D(int M) {
        object3D = new Object3D();
        for (int i = 0; i < spline.approximationPoints.size(); i++) {
            for (int j = 0; j < M; j++) {
                object3D.addPoint(getRotationFigurePoint(i, j));
                if (i > 0)
                    object3D.addEdge(i-1, i);
            }
        }
    }
}
