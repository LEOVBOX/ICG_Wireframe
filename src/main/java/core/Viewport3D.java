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

    RotationFigure rotationFigure;

    int M = 4;

    int width, height;

    int nearPlaneHeight, nearPlaneWidth;

    // Coordinate Z of near clip plane in camera space
    double nearPlaneZ = 500;

    // Coordinate Z of far clip plane in camera space
    double farPlaneZ = 600;

    double cameraZ = 10;

    double FOV = Math.toRadians(10);

    double rotateY, rotateX;

    int lastX, lastY;
    private final double ANGLE_SCALE = 0.01;


    Object3D cube;

    Object3D axis;

    int centerX, centerY;

    double[][] projectionMatrix;

    public void getRotationFigure() {
        rotationFigure = new RotationFigure(M, spline);
        rotationFigure.getObject3D(M);
    }

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

    double[][] getXRotationMatrix(double theta) {
        return new double[][] {
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

    private Object3D rotateObject(Object3D object3D, double rotateX, double rotateY) {
        Object3D result = new Object3D(object3D);
        for (Point3D point3D: result.getPoints()) {
            point3D.setLocation(rotatePoint(point3D, rotateX, rotateY));
        }
        return result;
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

    private void drawAxis(Graphics2D g2d) {
        Object3D rotatedAxis = new Object3D(axis);
        rotatedAxis = rotateObject(rotatedAxis, rotateX, rotateY);

        Point windowOrigin = getWindowPoint(rotatedAxis.getPoints().get(0));
        Point windowX = getWindowPoint(rotatedAxis.getPoints().get(1));
        Point windowY = getWindowPoint(rotatedAxis.getPoints().get(2));
        Point windowZ = getWindowPoint(rotatedAxis.getPoints().get(3));

        g2d.setColor(Color.RED);
        g2d.drawLine(windowOrigin.x, windowOrigin.y, windowX.x, windowX.y);

        g2d.setColor(Color.GREEN);
        g2d.drawLine(windowOrigin.x, windowOrigin.y, windowY.x, windowY.y);

        g2d.setColor(Color.BLUE);
        g2d.drawLine(windowOrigin.x, windowOrigin.y, windowZ.x, windowZ.y);
    }

    Object3D createCube() {
        cube = new Object3D();
        cube.addPoint(new Point3D(1, 1, -1));
        cube.addPoint(new Point3D(1, -1, -1));
        cube.addPoint(new Point3D(-1, 1, -1));
        cube.addPoint(new Point3D(-1, -1, -1));

        cube.addPoint(new Point3D(1, 1, 1));
        cube.addPoint(new Point3D(1, -1, 1));
        cube.addPoint(new Point3D(-1, 1, 1));
        cube.addPoint(new Point3D(-1, -1, 1));


        // Front side
        cube.addEdge(0, 2);
        cube.addEdge(0, 1);
        cube.addEdge(1, 3);
        cube.addEdge(2 ,3);

        // Back side
        cube.addEdge(0 + 4, 2 + 4);
        cube.addEdge(0 + 4, 1 + 4);
        cube.addEdge(1 + 4, 3 + 4);
        cube.addEdge(2 + 4, 3 + 4);

        cube.addEdge(0, 0 + 4);
        cube.addEdge(1, 1 + 4);
        cube.addEdge(2, 2 + 4);
        cube.addEdge(3, 3 + 4);
        return cube;
    }

    private void render(Graphics2D g2d, Object3D object3D, int pointRadius) {
        object3D =  rotateObject(object3D, rotateX, rotateY);
        g2d.setColor(Color.WHITE);
        for (Integer[] edge: object3D.getEdges()) {
            Point a = getWindowPoint(object3D.getPoints().get(edge[0]));
            g2d.drawOval(a.x - pointRadius, a.y - pointRadius, 2 * pointRadius, 2 * pointRadius);
            Point b = getWindowPoint(object3D.getPoints().get(edge[1]));
            g2d.drawOval(b.x - pointRadius, b.y - pointRadius, 2 * pointRadius, 2 * pointRadius);
            g2d.drawLine(a.x, a.y, b.x, b.y);
        }
    }


    public Viewport3D(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        centerX = width / 2;
        centerY = height / 2;
        rotateX = 0;
        rotateY = 0;
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
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                lastX = e.getX();
                lastY = e.getY();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    super.mousePressed(e);
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        lastX = e.getX();
                        lastY = e.getY();
                        repaint();
                    }
                }

            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                    double dx = (e.getX() - lastX) * ANGLE_SCALE;
                    double dy = (e.getY() - lastY) * ANGLE_SCALE;
                    rotateX += dy; // Изменение угла вдоль оси X
                    rotateY += dx; // Изменение угла вдоль оси Y
                    lastX = e.getX();
                    lastX = e.getX();
                    lastY = e.getY();
                    repaint();
            }
        });

        axis = new Object3D();
        axis.addPoint(new Point3D(0, 0, 0));
        axis.addPoint(new Point3D(1, 0, 0));
        axis.addPoint(new Point3D(0, 1, 0));
        axis.addPoint(new Point3D(0, 0, 1));

        axis.addEdge(0, 1);
        axis.addEdge(0, 2);
        axis.addEdge(0, 3);


        cube = createCube();
        projectionMatrix = getProjectionMatrix();

        spline = new BSpline();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        projectionMatrix = getProjectionMatrix();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.white);
        drawAxis(g2d);
        g2d.setColor(Color.GREEN);
        g.drawString("rotateX: " + rotateX, 100, 100);
        g.drawString("rotateY: " + rotateY, 100, 200);
        render(g2d, cube, 0);
        if (rotationFigure != null) {
            render(g2d, rotationFigure.object3D, 2);
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
            nearPlaneHeight = (int) (2 * nearPlaneZ);
            nearPlaneWidth = nearPlaneHeight;
        }

        repaint();
    }


}
