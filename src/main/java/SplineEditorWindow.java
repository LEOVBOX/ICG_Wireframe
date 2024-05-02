import core.SplineViewport;
import core.SplineViewportSettings;

import javax.swing.*;
import java.awt.*;

public class SplineEditorWindow extends JFrame {
    MainWindow mainWindow;
    public SplineEditorWindow(MainWindow mainWindow) {
        super("Spline editor");
        this.mainWindow = mainWindow;
        try {
            setPreferredSize(new Dimension(640, 480));
            setLocation(0, 0);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLayout(new BorderLayout());

            SplineViewport splineEditor = new SplineViewport(getWidth(), getHeight());
            add(splineEditor);
            SplineViewportSettings settings = new SplineViewportSettings(splineEditor, splineEditor.spline, mainWindow.viewport3D);
            splineEditor.setSettings(settings);
            add(settings, BorderLayout.SOUTH);
            pack();
            setVisible(true);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}