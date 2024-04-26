package core;

import LinearAlgebra.MatrixUtils;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import static java.lang.Math.pow;

public class BSpline {
    ArrayList<Point2D> referencePoints;

    ArrayList<Point2D> approximationPoints;

    int N;
    static final double[][] Ms = {
            {-1/6.0, 0.5, -0.5, 1/6.0},
            {0.5, -1, 0.5, 0},
            {-0.5, 0, 0.5, 0},
            {1/6.0, 4/6.0, 1/6.0, 0}
    };


    public BSpline() {
        referencePoints = new ArrayList<>();
        approximationPoints = new ArrayList<>();
        this.N = 1;
    }

    Point2D.Double r(double t, int i) {
        if (i < 1) {
            throw new IllegalArgumentException("i should be more then 0");
        }

        double[] T = {pow(t, 3), pow(t, 2), t, 1};
        double[] Gx = {referencePoints.get(i-1).getX(), referencePoints.get(i).getX(), referencePoints.get(i+1).getX(), referencePoints.get(i+2).getX()};
        double[] Gy = {referencePoints.get(i-1).getY(), referencePoints.get(i).getY(), referencePoints.get(i+1).getY(), referencePoints.get(i+2).getY()};

        T = MatrixUtils.multiply(Ms, T, true);
        double u = MatrixUtils.multiply(T, Gx);
        double v = MatrixUtils.multiply(T, Gy);

        return new Point2D.Double(u, v);
    }
}
