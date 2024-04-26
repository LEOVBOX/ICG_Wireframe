package core;

public class RotationFigure {
    BSpline spline;
    int M;


    // i - number of generatix
    // j - number of rotation angle.
    public Double Rx(int i, int j) {
        return spline.approximationPoints.get(i).getY() * Math.cos(j*360/M);
    }
    public Double Ry(int i, int j) {
        return spline.approximationPoints.get(i).getY() * Math.sin(j*360/M);
    }
    public Double Rz(int i, int j) {
        return spline.approximationPoints.get(i).getX();
    }

}
