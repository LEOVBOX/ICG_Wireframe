package core;

import LinearAlgebra.MatrixUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

public class Viewport3D extends JPanel {
    BSpline spline;

    int width, height;

    int nearPlaneHeight, nearPlaneWidth;

    // Coordinate Z of near clip plane in camera space
    double nearPlaneZ = 10;

    // Coordinate Z of far clip plane in camera space
    double farPlaneZ = 20;

    double cameraZ = 10;

    double FOV = 45;

    double phi;

    private final double ANGLE_SCALE = 0.01;


    boolean pritPhi = false;

    Object3D axis;

    class Object3D {
        ArrayList<Point3D> points;

        ArrayList<Integer[]> edges;

        public Object3D() {
            points = new ArrayList<>();
            edges = new ArrayList<>();
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

    Object3D object3D;

    int centerX, centerY;

    double[][] projectionMatrix;

    double[][] getProjectionMatrix() {
        return new double[][] {
                {nearPlaneZ / nearPlaneWidth , 0, 0, 0},
                {0, nearPlaneZ / nearPlaneHeight, 0, 0},
                {0, 0, -(farPlaneZ + nearPlaneZ) / (farPlaneZ - nearPlaneZ), -(2 * farPlaneZ * nearPlaneZ) / (farPlaneZ - nearPlaneZ)},
                {0, 0, -1 ,0}
        };
    }

    double[][] getYRotationMatrix(double phi) {
        return new double[][]{
            {Math.cos(phi), 0, Math.sin(phi)},
            {0, 1, 0},
            {-Math.sin(phi), 0, Math.cos(phi)}
        };
    }

    private Point3D rotatePoint(Point3D point3D, double phi) {
        double[] cords = {point3D.getX(), point3D.getY(), point3D.getZ()};
        cords = MatrixUtils.multiply(getYRotationMatrix(phi), cords, false);
        return new Point3D(cords[0], cords[1], cords[2]);
    }

    private void rotateObject(Object3D object3D, double phi) {
        for (Point3D point3D: object3D.points) {
            double[] rCords = {point3D.getX(), point3D.getY(), point3D.getZ()};
            rCords = MatrixUtils.multiply(getYRotationMatrix(phi), rCords, false);
            object3D.points.set(object3D.points.indexOf(point3D), new Point3D(rCords[0], rCords[1], rCords[2]));
        }
    }



    Point3D getNDCPoint(Point3D point) {
        double[] eyePoint = {point.getX(), point.getY(), point.getZ() - cameraZ, 1};
        double[] clipSpacePoint = MatrixUtils.multiply(projectionMatrix, eyePoint, false);
        return new Point3D(clipSpacePoint[0]/clipSpacePoint[3], clipSpacePoint[1]/clipSpacePoint[3], clipSpacePoint[2]/clipSpacePoint[3]);
    }

    Point getWindowPoint(Point3D point3D) {
        Point3D ndcPoint = getNDCPoint(point3D);
        int x = (int)(centerX + nearPlaneWidth * ndcPoint.getX());
        int y = (int)( centerY - nearPlaneHeight * ndcPoint.getY());

        return new Point(x, y);
    }


    private ArrayList<Point3D> calcDimensionalBox(ArrayList<Point3D> object3d) {
        if (object3d.isEmpty()) {
            throw new IllegalArgumentException("for calculation dimensional box needed at least 1 point");
        }

        ArrayList<Point3D> box = new ArrayList<>();
        Point3D maxPoint = new Point3D(object3d.getFirst().getX(), object3d.getFirst().getY(), object3d.getFirst().getZ());
        Point3D minPoint = new Point3D(object3d.getFirst().getX(), object3d.getFirst().getY(), object3d.getFirst().getZ());
        for (int i = 1; i < object3d.size(); i++) {
            maxPoint = new Point3D(Math.max(maxPoint.getX(), object3d.get(i).getX()),
                    Math.max(maxPoint.getY(), object3d.get(i).getY()),
                    Math.max(maxPoint.getZ(), object3d.get(i).getZ())
            );
            minPoint = new Point3D(Math.min(minPoint.getX(), object3d.get(i).getX()),
                    Math.min(minPoint.getY(), object3d.get(i).getY()),
                    Math.min(minPoint.getZ(), object3d.get(i).getZ())
            );
        }
        box.add(maxPoint);
        box.add(minPoint);

        return box;
    }

    private void drawAxis(Graphics2D g2d, double phi) {
        g2d.setColor(Color.WHITE);
        Point windowOrigin = getWindowPoint(rotatePoint(new Point3D(0, 0, 0), phi));
        //g2d.drawOval(windowOrigin.x - pointRadius, windowOrigin.y - pointRadius,2 *pointRadius, 2 *pointRadius);

        g2d.setColor(Color.RED);
        Point windowOx = getWindowPoint(rotatePoint(new Point3D(1, 0, 0), phi));
        //g2d.drawOval(windowOx.x - pointRadius, windowOx.y - pointRadius, 2* pointRadius, 2 * pointRadius);
        g2d.drawLine(windowOrigin.x, windowOrigin.y, windowOx.x, windowOx.y);

        g2d.setColor(Color.GREEN);
        Point windowOy = getWindowPoint(rotatePoint(new Point3D(0, 1, 0), phi));
        //g2d.drawOval(windowOy.x - pointRadius, windowOy.y - pointRadius, 2 * pointRadius, 2 * pointRadius);
        g2d.drawLine(windowOrigin.x, windowOrigin.y, windowOy.x, windowOy.y);

        g2d.setColor(Color.BLUE);
        Point windowOZ = getWindowPoint(rotatePoint(new Point3D(0, 0, 1), phi));
        //g2d.drawOval(windowOZ.x - pointRadius, windowOZ.y - pointRadius, 2 * pointRadius, 2 * pointRadius);
        g2d.drawLine(windowOrigin.x, windowOrigin.y, windowOZ.x, windowOZ.y);

    }

    private void render(Graphics2D g2d, Object3D object3D) {
        g2d.setColor(Color.MAGENTA);
        for (Integer[] edge: object3D.edges) {
            Point a = getWindowPoint(object3D.points.get(edge[0]));
            Point b = getWindowPoint(object3D.points.get(edge[1]));
            g2d.drawLine(a.x, a.y, b.x, b.y);
        }
    }


    public Viewport3D(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        centerX = width / 2;
        centerY = height / 2;
        phi = 0;
        setBackground(Color.DARK_GRAY);
        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                super.mouseWheelMoved(e);
                nearPlaneZ += e.getWheelRotation();
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    final int[] prevX = {e.getX()};
                    addMouseMotionListener(new MouseAdapter() {
                        @Override
                        public void mouseDragged(MouseEvent e) {
                            int currentX = e.getX();
                            int dx = currentX - prevX[0];
                            phi += dx * ANGLE_SCALE;
                            prevX[0] = currentX;
                            rotateObject(object3D, phi);
                            repaint();
                        }
                    });
                }

            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                pritPhi = false;
                repaint();
            }
        });


        axis = new Object3D();
        object3D = new Object3D();
        projectionMatrix = getProjectionMatrix();

        // Axis
        axis.addPoint(new Point3D(0, 0, 0));
        axis.addPoint(new Point3D(1, 0, 0));
        axis.addPoint(new Point3D(0, 1, 0));
        axis.addPoint(new Point3D(0, 0, 1));

        axis.addEdge(0, 1);
        axis.addEdge(0, 2);
        axis.addEdge(0, 3);

        object3D.addPoint(new Point3D(2, 2, -2));
        object3D.addPoint(new Point3D(2, -2, -2));
        object3D.addPoint(new Point3D(-2, 2, -2));
        object3D.addPoint(new Point3D(-2, -2, -2));

        object3D.addPoint(new Point3D(2, 2, 2));
        object3D.addPoint(new Point3D(2, -2, 2));
        object3D.addPoint(new Point3D(-2, 2, 2));
        object3D.addPoint(new Point3D(-2, -2, 2));


        // Front side
        object3D.addEdge(0, 2);
        object3D.addEdge(0, 1);
        object3D.addEdge(1, 3);
        object3D.addEdge(2 ,3);

        // Back side
        object3D.addEdge(0 + 4, 2 + 4);
        object3D.addEdge(0 + 4, 1 + 4);
        object3D.addEdge(1 + 4, 3 + 4);
        object3D.addEdge(2 + 4, 3 + 4);

        object3D.addEdge(0, 0 + 4);
        object3D.addEdge(1, 1 + 4);
        object3D.addEdge(2, 2 + 4);
        object3D.addEdge(3, 3 + 4);

        rotateObject(object3D, 10);




    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        projectionMatrix = getProjectionMatrix();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.white);
        rotateObject(axis, phi);
        drawAxis(g2d, phi);
        render(g2d, object3D);
        if (pritPhi) {
            g2d.drawString(String.valueOf(phi), 100, 100);
        }


    }

    @Override
    public void invalidate() {
        super.invalidate();
        width = getWidth();
        height = getHeight();

        centerX = width / 2;
        centerY = height / 2;

        if (centerY != 0) {
            nearPlaneHeight = (int) (2 * nearPlaneZ * Math.tan(FOV / 2));
            nearPlaneWidth = nearPlaneHeight;
        }

        repaint();
    }


}
