import core.SplineViewport;
import core.SplineViewportSettings;

import javax.swing.*;
import java.awt.*;

public class SplineEditorWindow extends JFrame {
    MainWindow mainWindow;
    SplineViewport splineViewport;
    SplineViewportSettings splineViewportSettings;
    public SplineEditorWindow(MainWindow mainWindow) {
        super("Spline editor");
        this.mainWindow = mainWindow;
        try {
            setPreferredSize(new Dimension(640, 480));
            setLocation(0, 0);
            setDefaultCloseOperation(HIDE_ON_CLOSE);
            setLayout(new BorderLayout());

            splineViewport = new SplineViewport(getWidth(), getHeight());
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