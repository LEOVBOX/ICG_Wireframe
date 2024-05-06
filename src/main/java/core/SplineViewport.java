package core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SplineViewport extends JPanel implements MouseListener {
    SplineViewportSettings settings;
    public BSpline spline;

    boolean isDrawing;

    private int width, height;
    private int centerX, centerY;

    private int curX, curY;

    //private int lastX, lastY;

    private int curRefPointIdx;

    //private boolean isMoving = false;

    // length in pix
    private final int divisionLength = 50;

    private final int pointRadius = 10;

    public BSpline getSpline() {
        return spline;
    }

    public void setSpline(BSpline spline) {
        this.spline = spline;
    }

    public void setSettings(SplineViewportSettings settings) {
        this.settings = settings;
    }

    public SplineViewport(int width, int height) {
        setBackground(Color.BLACK);
        this.width = width;
        this.height = height;
        centerX = width / 2;
        centerY = height / 2;
        isDrawing = false;
        spline = new BSpline();
        curRefPointIdx = -1;
        addMouseListener(this);

        // Motion listener for dragging points
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    curX = e.getX();
                    curY = e.getY();
                    if (curRefPointIdx != -1) {
                        Point2D refPoint = getRelativePoint(new Point(curX, curY));
                        spline.referencePoints.get(curRefPointIdx).setLocation(refPoint.getX(), refPoint.getY());
                    }
                    repaint();
                }
                /*if (SwingUtilities.isRightMouseButton(e)) {
                    double dx = (lastX - e.getX()) * 0.01;
                    double dy = (lastY - e.getY()) * 0.01;
                    centerX += dx; // Изменение угла вдоль оси X
                    centerY += dy; // Изменение угла вдоль оси Y

                    lastX = e.getX();
                    lastY = e.getY();
                    repaint();
                }*/
            }
        });
    }

    private void drawAxis(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        // Отрисовка осей x и y
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawLine(0, centerY, width, centerY); // Ось x
        g2d.drawLine(centerX, 0, centerX, height); // Ось y

        int xDivisions = width / divisionLength;
        int yDivisions = height / divisionLength;

        // Нанесение делений на оси x и y
        int xSpacing = width / xDivisions;
        int ySpacing = height / yDivisions;

        // Расчет смещения сетки
        int offsetX = centerX % xSpacing;
        int offsetY = centerY % ySpacing;

        // Рисование линий сетки по вертикали и делений на оси x
        for (int i = 1; i < xDivisions; i++) {
            int x = i * xSpacing - offsetX;
            if ((i - xDivisions / 2 != 0) && (i - xDivisions / 2 < width)) {
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawLine(x, 0, x, height);
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawLine(x, centerY - 4, x, centerY + 4);
                g2d.setColor(Color.DARK_GRAY);
            }

            // Отображение координаты под делением
            g2d.drawString(String.valueOf(i - xDivisions / 2), x, centerY + 15);
        }

        // Рисование линий сетки по горизонтали и делений на оси y
        for (int i = 1; i < yDivisions; i++) {
            // Начинаем рисовать из середины экрана
            int y = i * ySpacing - offsetY;
            if ((i - yDivisions / 2 != 0) && (i - yDivisions / 2 < height)) {
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawLine(0, y, width, y);
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawLine(centerX - 4, y, centerX + 4, y);
                g2d.setColor(Color.DARK_GRAY);
            }

            // Отображение координаты под делением
            g2d.drawString(String.valueOf(yDivisions / 2 - i), centerX - 20, y);
        }
    }

    private void drawBrokenLine(Graphics2D g2d, ArrayList<Point2D.Double> points, int pointRadius, Color color) {
        if (points != null) {
            Point prevPoint = null;
            for (Point2D.Double point : points) {
                Point winodwPoint = getWindowPoint(point);
                if (winodwPoint.x < width && winodwPoint.y < width) {
                    g2d.setColor(color);
                    if (pointRadius != 0) {
                        g2d.drawOval(winodwPoint.x - pointRadius, winodwPoint.y - pointRadius, 2 * pointRadius, 2 * pointRadius);
                    }

                    if (prevPoint != null) {
                        g2d.drawLine(prevPoint.x, prevPoint.y, winodwPoint.x, winodwPoint.y);
                    }
                }
                prevPoint = winodwPoint;

            }
        }
    }

    private void drawReferencePoints(Graphics2D g2d) {
        drawBrokenLine(g2d, spline.referencePoints, pointRadius, Color.GREEN);
    }

    private void drawSpline(Graphics2D g2d) throws ExecutionException, InterruptedException {
        spline.caclApproximation();
        drawBrokenLine(g2d, spline.approximationPoints, 2, Color.RED);
    }

    private Point2D.Double getRelativePoint(Point windowPoint) {
        double windowX = windowPoint.getX();
        double windowY = windowPoint.getY();

        double relativeX, relativeY;
        relativeX = Math.abs(centerX - windowX) / divisionLength;
        if (windowX < centerX)
            relativeX *= -1;
        relativeY = Math.abs(centerY - windowY) / divisionLength;
        if (windowY > centerY)
            relativeY *= -1;

        return new Point2D.Double(relativeX, relativeY);
    }

    private Point getWindowPoint(Point2D relativePoint) {
        double relativeX = relativePoint.getX();
        double relativeY = relativePoint.getY();

        double windowX, windowY;
        windowX = centerX + relativeX * divisionLength;
        windowY = centerY - relativeY * divisionLength;

        return new Point((int) windowX, (int) windowY);
    }

    private boolean isReferencePoint(Point2D referencePoint, Point curPoint) {
        Point windowRefPoint = getWindowPoint(referencePoint);
        return (curPoint.x < windowRefPoint.getX() + pointRadius) && (curPoint.x > windowRefPoint.getX() - pointRadius)
                && (curPoint.y < windowRefPoint.getY() + pointRadius) && (curPoint.y > windowRefPoint.getY() - pointRadius);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawAxis(g2d);
        drawReferencePoints(g2d);

        if (isDrawing) {
            g.setColor(Color.WHITE);
            g.drawOval(curX - pointRadius, curY - pointRadius, 2 * pointRadius, 2 * pointRadius);
        }

        if(spline.referencePoints.size() >= 4) {
            try {
                drawSpline(g2d);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


    }

    // Метод для установки размера окна
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    // Обработчик изменения размера окна
    @Override
    public void invalidate() {
        super.invalidate();
        width = getWidth();
        height = getHeight();

        centerX = getWidth() / 2;
        centerY = getHeight() / 2;

        repaint(); // Перерисовываем оси при изменении размера окна
    }


    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            Point curPoint = e.getPoint();
            // Проверяем, являются ли координаты точки нажатия опорной точкой
            for (Point2D referencePoint: spline.referencePoints) {
                if (isReferencePoint(referencePoint, curPoint)) {
                    curRefPointIdx = spline.referencePoints.indexOf(referencePoint);
                    isDrawing = true;
                    break;
                }
            }

            curX = e.getX();
            curY = e.getY();

            repaint();
        }
        else if (e.getButton() == MouseEvent.BUTTON3) {
            //isMoving = true;
            /*lastX = e.getX();
            lastY = e.getY();*/
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {

            Point windowPoint = e.getPoint();
            Point2D.Double relativePoint = getRelativePoint(windowPoint);

            // Если новая опорная точка
            if (curRefPointIdx == -1) {
                spline.referencePoints.add(relativePoint);
                if (settings != null) {
                    settings.viewport3D.update();
                }
            }

            if (settings != null) {
                settings.setK(spline.referencePoints.size());
                if (spline.referencePoints.size() >= 4) {
                    settings.viewport3D.update();
                }
            }


            repaint();
            curRefPointIdx = -1;
            isDrawing = false;
            //isMoving = false;
        }

        if (e.getButton() == MouseEvent.BUTTON3) {
            Point curPoint = e.getPoint();
            for (Point2D referencePoint: spline.referencePoints) {
                if (isReferencePoint(referencePoint, curPoint)) {
                    spline.referencePoints.remove(referencePoint);
                    break;
                }
            }

            if (settings != null) {
                settings.setK(spline.referencePoints.size());
                settings.viewport3D.update();
            }

            //isMoving = false;
            /*lastX = e.getX();
            lastY = e.getY();*/

            repaint();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}
