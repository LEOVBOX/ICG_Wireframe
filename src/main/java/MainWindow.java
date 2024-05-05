import core.*;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainWindow extends JFrame {
    SplineEditorWindow splineEditorWindow;

    Scene scene;

    Viewport3D viewport3D;
    private void openSplineEditor() {
            if (splineEditorWindow == null) {
                splineEditorWindow = new SplineEditorWindow(this);
                if (scene.isAutoChange) {
                    splineEditorWindow.splineViewport.setSpline(scene.rotationFigure.getSpline());
                }
                else {
                    splineEditorWindow.splineViewport.setSpline(new BSpline(this.scene.rotationFigure.getSpline()));
                }

                splineEditorWindow.splineViewport.repaint();
            }

            else {
                if (scene.isAutoChange) {
                    splineEditorWindow.splineViewport.setSpline(scene.rotationFigure.getSpline());
                }
                else {
                    splineEditorWindow.splineViewport.setSpline(new BSpline(this.scene.rotationFigure.getSpline()));
                }
                splineEditorWindow.setVisible(true);
                splineEditorWindow.splineViewport.repaint();
            }
    }
    public MainWindow() {
        super("ICG_Wireframe");
        try {
            setPreferredSize(new Dimension(600, 480));
            setLocation(0, 0);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            scene = new Scene();

            try (FileReader reader = new FileReader("default.json")) {
                StringBuilder jsonString = new StringBuilder();
                int character;
                while ((character = reader.read()) != -1) {
                    jsonString.append((char) character);
                }
                scene = Scene.fromJson(jsonString.toString());
            }

            catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "No default figure file");
                scene.rotationFigure = new RotationFigure();
                scene.farPlaneZ = 2000;
                scene.nearPlaneZ = 1500;
                scene.rotateX = 0.0;
                scene.rotateY = 0.0;
            }


            viewport3D = new Viewport3D(getWidth(), getHeight(), scene);
            JToolBar toolBar = new JToolBar();
            JButton splineEditorButton = new JButton("open spline editor");
            splineEditorButton.addActionListener(e->openSplineEditor());
            toolBar.add(splineEditorButton);

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                try (FileWriter writer = new FileWriter("default.json")) {
                    // Преобразуем объект в JSON и записываем его в файл
                    writer.write(scene.toJson());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            toolBar.add(saveButton);

            JButton resetAnglesButton = new JButton("Reset angles");
            resetAnglesButton.addActionListener(e -> {
                scene.rotateX = 0.0;
                scene.rotateY = 0.0;
                viewport3D.repaint();
            });
            toolBar.add(resetAnglesButton);

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
