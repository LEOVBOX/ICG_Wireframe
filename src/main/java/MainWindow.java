import core.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainWindow extends JFrame {
    SplineEditorWindow splineEditorWindow;

    Scene scene;

    String outputFile = "default.json";

    Viewport3D viewport3D;

    private void openSplineEditor() {
        if (splineEditorWindow == null) {
            splineEditorWindow = new SplineEditorWindow(this, scene.rotationFigure.getSpline());

        //if (scene.isAutoChange) {
            splineEditorWindow.splineViewport.setSpline(scene.rotationFigure.getSpline());
        //} else {
           // splineEditorWindow.splineViewport.setSpline(new BSpline(this.scene.rotationFigure.getSpline()));
        //}
        splineEditorWindow.setVisible(true);
        splineEditorWindow.splineViewport.repaint();
        }
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("json", "json"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (FileReader reader = new FileReader(selectedFile)) {
                outputFile = selectedFile.getName();
                StringBuilder jsonString = new StringBuilder();
                int character;
                while ((character = reader.read()) != -1) {
                    jsonString.append((char) character);
                }
                scene = Scene.fromJson(jsonString.toString());
                viewport3D.setScene(scene);
                viewport3D.update();
                System.out.println("File " + selectedFile.getName() + " opened");

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Invalid file");
                scene.rotationFigure = new RotationFigure();
                scene.farPlaneZ = 2000;
                scene.nearPlaneZ = 1500;
                scene.rotateX = 0.0;
                scene.rotateY = 0.0;
                viewport3D.update();
            }
        }
    }

    public void saveFileAs() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Выберите файл для сохранения");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON файлы", "json");
        fileChooser.setFileFilter(filter);

        // Показываем диалоговое окно выбора файла
        int userSelection = fileChooser.showSaveDialog(null);

        // Если пользователь выбрал файл и нажал "Сохранить"
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            // Получаем выбранный файл
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();

            // Добавляем расширение .json, если оно не указано
            if (!filePath.toLowerCase().endsWith(".json")) {
                filePath += ".json";
            }

            // Сохраняем JSON в выбранный файл
            try {
                FileWriter writer = new FileWriter(filePath);
                writer.write(scene.toJson());
                writer.close();
                System.out.println("Данные успешно сохранены в файл " + filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

            try (FileReader reader = new FileReader(outputFile)) {
                StringBuilder jsonString = new StringBuilder();
                int character;
                while ((character = reader.read()) != -1) {
                    jsonString.append((char) character);
                }
                scene = Scene.fromJson(jsonString.toString());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "No default figure file");
                scene.rotationFigure = new RotationFigure();
                scene.farPlaneZ = 2000;
                scene.nearPlaneZ = 1500;
                scene.rotateX = 0.0;
                scene.rotateY = 0.0;
            }


            viewport3D = new Viewport3D(getWidth(), getHeight(), scene);
            JMenuBar menuBar = new JMenuBar();

            setJMenuBar(menuBar);
            JMenu helpMenu = new JMenu("Help");
            JMenuItem aboutMenuItem = new JMenuItem("About program");
            String aboutMessage = "ICGWireframe is program for simple 3D graphics.\n Shaikhutdinov Leonid©";
            aboutMenuItem.addActionListener(e -> JOptionPane.showMessageDialog(this, aboutMessage));
            helpMenu.add(aboutMenuItem);
            menuBar.add(helpMenu);

            JMenu editMenu = new JMenu("Edit");
            JMenuItem resetAngles = new JMenuItem("Reset angles");
            resetAngles.addActionListener(e -> {
                scene.rotateX = 0.0;
                scene.rotateY = 0.0;
                viewport3D.repaint();
            });
            editMenu.add(resetAngles);
            menuBar.add(editMenu);


            JMenu fileMenu = new JMenu("File");
            JMenuItem save = new JMenuItem("Save");
            save.addActionListener(e -> {
                try (FileWriter writer = new FileWriter(outputFile)) {
                    // Преобразуем объект в JSON и записываем его в файл
                    writer.write(scene.toJson());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            fileMenu.add(save);
            menuBar.add(fileMenu);

            JToolBar toolBar = new JToolBar();
            JButton helpButton = new JButton("Help");

            helpButton.addActionListener(e -> JOptionPane.showMessageDialog(this, aboutMessage));
            toolBar.add(helpButton);

            JButton splineEditorButton = new JButton("open spline editor");
            splineEditorButton.addActionListener(e -> openSplineEditor());
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

            JButton saveAsButton = new JButton("Save as");
            saveAsButton.addActionListener(e->saveFileAs());
            toolBar.add(saveAsButton);

            JButton openButton = new JButton("Open file");
            openButton.addActionListener(e -> openFile());
            toolBar.add(openButton);

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
