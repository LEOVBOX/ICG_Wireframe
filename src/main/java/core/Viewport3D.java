package core;

import LinearAlgebra.MatrixUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

public class Viewport3D extends JPanel {
    public Scene scene;

    int width, height;

    int nearPlaneHeight, nearPlaneWidth;

    // Coordinate Z of near clip plane in camera space
    //double nearPlaneZ = 1400;

    // Coordinate Z of far clip plane in camera space
    //double farPlaneZ = 2000;

    double cameraZ = 10;


    int lastX, lastY;

    private final double ANGLE_SCALE = 0.01;

    Object3D cube;

    Object3D axis;

    int centerX, centerY;

    double[][] projectionMatrix;

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    double[][] getProjectionMatrix() {
        return new double[][]{
                {(double) scene.nearPlaneZ / nearPlaneWidth, 0, 0, 0},
                {0, (double) scene.nearPlaneZ / nearPlaneHeight, 0, 0},
                {0, 0, (double) -(scene.farPlaneZ + scene.nearPlaneZ) / (scene.farPlaneZ - scene.nearPlaneZ),
                        (double) -(2 * scene.farPlaneZ * scene.nearPlaneZ) / (scene.farPlaneZ - scene.nearPlaneZ)},
                {0, 0, -1, 0}
        };
    }

    Point3D getNDCPoint(Point3D point) {
        double[] eyePoint = {point.getX(), point.getY(), point.getZ() - cameraZ, 1};
        double[] clipSpacePoint = MatrixUtils.multiply(projectionMatrix, eyePoint, false);
        return new Point3D(clipSpacePoint[0]/clipSpacePoint[3], clipSpacePoint[1]/clipSpacePoint[3], clipSpacePoint[2]/clipSpacePoint[3]);
    }

    Point getWindowPoint(Point3D point3D) {
        Point3D ndcPoint = getNDCPoint(point3D);
        int x = (int)(centerX + nearPlaneWidth * ndcPoint.getX());
        int y = (int)(centerY - nearPlaneHeight * ndcPoint.getY());
        return new Point(x, y);
    }

    //private Object3D normalize() {}

    private void drawAxis(Graphics2D g2d) {
        Object3D rotatedAxis = axis.rotateObject(scene.rotateX, scene.rotateY);

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
        cube.addEdge(4, 2 + 4);
        cube.addEdge(4, 1 + 4);
        cube.addEdge(1 + 4, 3 + 4);
        cube.addEdge(2 + 4, 3 + 4);

        cube.addEdge(0, 4);
        cube.addEdge(1, 1 + 4);
        cube.addEdge(2, 2 + 4);
        cube.addEdge(3, 3 + 4);
        return cube;
    }

    private Object3D createAxis() {
        Object3D axis = new Object3D();
        axis.addPoint(new Point3D(0, 0, 0));
        axis.addPoint(new Point3D(1, 0, 0));
        axis.addPoint(new Point3D(0, 1, 0));
        axis.addPoint(new Point3D(0, 0, 1));

        axis.addEdge(0, 1);
        axis.addEdge(0, 2);
        axis.addEdge(0, 3);

        return axis;
    }


    private void render(Graphics2D g2d, Object3D object3D, int pointRadius, Color color) {
        Object3D renderObject = object3D.rotateObject(scene.rotateX, scene.rotateY);
        ArrayList<Point3D> dimensionalBox = renderObject.calcDimensionalBox();

        double minZ = dimensionalBox.getFirst().getZ();
        double maxZ = dimensionalBox.getLast().getZ();

        g2d.setColor(color);
        for (Integer[] edge : renderObject.getEdges()) {
            Point3D beginPoint = renderObject.getPoints().get(edge[0]);
            Point a = getWindowPoint(beginPoint);
            double beginZ = beginPoint.getZ(); // не нужно вычитать minZ

            g2d.drawOval(a.x - pointRadius, a.y - pointRadius, 2 * pointRadius, 2 * pointRadius);

            Point3D endPoint = renderObject.getPoints().get(edge[1]);
            Point b = getWindowPoint(endPoint);
            double endZ = endPoint.getZ(); // не нужно вычитать minZ

            g2d.drawOval(b.x - pointRadius, b.y - pointRadius, 2 * pointRadius, 2 * pointRadius);

            // Пересчитываем координаты относительно начала координат
            double beginC = beginZ - minZ;
            double endC = endZ - minZ;

            // Обновляем значения minZ и maxZ
            minZ = Math.min(minZ, beginZ);
            maxZ = Math.max(maxZ, beginZ);
            minZ = Math.min(minZ, endZ);
            maxZ = Math.max(maxZ, endZ);

            // Нормализуем значения оттенков серого
            double normalizedBeginC = (beginC - minZ) / (maxZ - minZ);
            double normalizedEndC = (endC - minZ) / (maxZ - minZ);

            // Преобразуем оттенки серого в диапазон от 0 до 255
            int beginGradient = (int) (normalizedBeginC * 255);
            int endGradient = (int) (normalizedEndC * 255);

            beginGradient = Math.max(0, Math.min(255, beginGradient));
            endGradient = Math.max(0, Math.min(255, endGradient));

            // Создаем градиентный цвет
            GradientPaint gradientPaint = new GradientPaint(b.x, b.y, new Color(endGradient, endGradient, endGradient), a.x, a.y, new Color(beginGradient, beginGradient, beginGradient));

            // Устанавливаем градиентный цвет как кисть для рисования линии
            g2d.setPaint(gradientPaint);

            // Рисуем линию с градиентным цветом
            g2d.drawLine(a.x, a.y, b.x, b.y);
        }
    }

    public Viewport3D(int width, int height, Scene scene) {
        this.scene = scene;
        setPreferredSize(new Dimension(width, height));
        centerX = width / 2;
        centerY = height / 2;

        setBackground(Color.DARK_GRAY);

        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                super.mouseWheelMoved(e);
                scene.nearPlaneZ += e.getWheelRotation();
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {  // Middle button
                    lastX = e.getX();
                    lastY = e.getY();
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                    double dx = (e.getX() - lastX) * ANGLE_SCALE;
                    double dy = (e.getY() - lastY) * ANGLE_SCALE;
                    scene.rotateX += dy; // Изменение угла вдоль оси X
                    scene.rotateY += dx; // Изменение угла вдоль оси Y
                    lastX = e.getX();
                    lastY = e.getY();
                    repaint();
            }
        });

        axis = createAxis();
        cube = createCube();

        projectionMatrix = getProjectionMatrix();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        projectionMatrix = getProjectionMatrix();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.white);
        drawAxis(g2d);
        g2d.setColor(Color.GREEN);

        if (scene.rotationFigure.object3D != null) {
            render(g2d, scene.rotationFigure.object3D, 0, Color.WHITE);
        }
        else {
            render(g2d, cube, 0, Color.LIGHT_GRAY);
        }
    }

    public void update() {
        if (scene.rotationFigure != null){
            scene.rotationFigure.getObject3D();
        }
        repaint();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        width = getWidth();
        height = getHeight();

        centerX = width / 2;
        centerY = height / 2;

        if (centerY != 0) {
            nearPlaneHeight = 2 * scene.nearPlaneZ;
            nearPlaneWidth = nearPlaneHeight;
        }

        repaint();
    }


}
