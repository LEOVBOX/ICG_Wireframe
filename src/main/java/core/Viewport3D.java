package core;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Viewport3D extends JPanel {
    BSpline spline;

    ArrayList<Point3D> object3d;

    int centerX, centerY;

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


    public Viewport3D(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        centerX = width/2;
        centerY = height/2;
        setBackground(Color.DARK_GRAY);
        object3d = new ArrayList<>();

        // Axis
        object3d.add(new Point3D(0, 0, 0));
        object3d.add(new Point3D(0, 1, 0));
        object3d.add(new Point3D(0, 0, 1));
        object3d.add(new Point3D(1, 0, 0));
        object3d.add(new Point3D(12, 0, 124));
        object3d.add(new Point3D(-12, 12, 9));

        ArrayList<Point3D> dimBox = calcDimensionalBox(object3d);
        System.out.println(dimBox);



    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
    }


}
