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

    public Object3D getObject3D(int M) {
        object3D = new Object3D();
        for (int j = 0; j < M; j++) {
            for (int i = 0; i < spline.approximationPoints.size(); i++) {
                object3D.addPoint(new Point3D(Rx(i, j), Ry(i, j), Rz(i)));
                if (i > 0) {
                    object3D.addEdge(j + (i-1)%(j+1), (j + i)%(j+1));
                }
            }
        }
        return object3D;
    }
}
