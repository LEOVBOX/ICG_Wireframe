import core.BSpline;
import core.SplineViewport;
import core.Viewport3D;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    SplineEditorWindow splineEditorWindow;

    Viewport3D viewport3D;
    private void openSplineEditor() {
            splineEditorWindow = new SplineEditorWindow(this);
    }
    public MainWindow() {
        super("ICG_Wireframe");
        try {
            setPreferredSize(new Dimension(600, 480));
            setLocation(0, 0);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            viewport3D = new Viewport3D(getWidth(), getHeight());
            JToolBar toolBar = new JToolBar();
            JButton splineEditorButton = new JButton("open spline editor");
            splineEditorButton.addActionListener(e->openSplineEditor());
            toolBar.add(splineEditorButton);
            add(toolBar, BorderLayout.NORTH);
            add(viewport3D, BorderLayout.CENTER);
            pack();
            setVisible(true);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new MainWindow();
    }
}
