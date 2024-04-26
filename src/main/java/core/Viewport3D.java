package core;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Viewport3D extends JPanel {
    BSpline spline;

    ArrayList<Point3D> object3d;

    int centerX, centerY;


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



    }

    

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
    }


}
