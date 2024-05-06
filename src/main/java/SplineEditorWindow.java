import core.BSpline;
import core.SplineViewport;
import core.SplineViewportSettings;

import javax.swing.*;
import java.awt.*;

public class SplineEditorWindow extends JFrame {
    SplineViewport splineViewport;
    SplineViewportSettings splineViewportSettings;
    public SplineEditorWindow(MainWindow mainWindow, BSpline spline) {
        super("Spline editor");
        try {
            setPreferredSize(new Dimension(640, 480));
            setLocation(0, 0);
            setDefaultCloseOperation(HIDE_ON_CLOSE);
            setLayout(new BorderLayout());

            splineViewport = new SplineViewport(getWidth(), getHeight());
            splineViewport.setSpline(spline);
            add(splineViewport);

            splineViewportSettings = new SplineViewportSettings(splineViewport, mainWindow.viewport3D);
            splineViewport.setSettings(splineViewportSettings);
            add(splineViewportSettings, BorderLayout.SOUTH);
            pack();
            setVisible(true);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}