import core.SplineViewport;
import core.SplineViewportSettings;
import core.Viewport3D;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    public MainWindow() {
        super("ICG_Wireframe");
        try {
            setPreferredSize(new Dimension(1000, 600));
            setLocation(0, 0);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            Viewport3D viewport3D = new Viewport3D(getWidth(), getHeight());
            add(viewport3D);
            pack();
            setVisible(true);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String args[]) {
        new MainWindow();
    }
}
