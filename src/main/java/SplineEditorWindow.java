import core.SplineViewport;
import core.SplineViewportSettings;

import javax.swing.*;
import java.awt.*;

public class SplineEditorWindow extends JFrame {
    public SplineEditorWindow() {
        super("Spline editor");
        try {
            setPreferredSize(new Dimension(1000, 600));
            setLocation(0, 0);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            SplineViewport splineEditor = new SplineViewport(getWidth(), getHeight());
            add(splineEditor);
            SplineViewportSettings settings = new SplineViewportSettings(splineEditor, splineEditor.spline);
            splineEditor.setSettings(settings);
            add(settings, BorderLayout.SOUTH);
            pack();
            setVisible(true);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
/*
    public static void main(String[] args) {
        new SplineEditorWindow();
    }
*/
}