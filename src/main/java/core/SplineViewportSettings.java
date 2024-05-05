package core;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class SplineViewportSettings extends JPanel {
    SplineViewport splineViewport;
    Viewport3D viewport3D;
    JSpinner N;
    JSpinner K;
    JSpinner SplineRed;
    JSpinner GeneratixRed;
    JSpinner M1;
    JSpinner M;
    JSpinner splineGreen;
    JSpinner generatixGreen;
    JSpinner X;
    JSpinner Y;
    JSpinner SplineBlue;
    JSpinner GeneratixBlue;
    JButton okButton;
    JButton applyButton;
    JRadioButton autoChangeButton;
    JButton normalizeButton;
    JButton zoomPlusButton;
    JButton zoomMinusButton;
    JButton clearButton;

    BSpline spline;

    void setK(int K) {
        this.K.setValue(K);
    }


    public SplineViewportSettings(SplineViewport splineViewport, Viewport3D viewport3D) {
        this.viewport3D = viewport3D;
        this.splineViewport = splineViewport;
        this.spline = splineViewport.getSpline();
        setBackground(Color.LIGHT_GRAY);
        setLayout(new GridLayout(4, 8, 10, 10));

        // 1 ROW
        JLabel nLabel = new JLabel("N");
        nLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(nLabel);
        N = new JSpinner(new SpinnerNumberModel(splineViewport.getSpline().getN(), 1, 500, 1));
        N.addChangeListener(e -> {
            JSpinner source = (JSpinner) e.getSource();
            int value = (int) source.getValue();
            splineViewport.getSpline().setN(value);
            splineViewport.repaint();
            if (viewport3D.scene.isAutoChange) {
                viewport3D.update();
            }
        });
        add(N);


        JLabel kLabel = new JLabel("K");
        kLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(kLabel);
        K = new JSpinner(new SpinnerNumberModel(viewport3D.scene.rotationFigure.getSpline().referencePoints.size(), 0, 300, 1));
        K.addChangeListener(e -> {
            JSpinner source = (JSpinner) e.getSource();
            int value = (int) source.getValue();

            // Добавление и удаление точек сплайна при изменении значения K
            for (int i = 0; i < Math.abs(splineViewport.getSpline().referencePoints.size() - value); i++) {
                if (value < splineViewport.getSpline().referencePoints.size()) {
                    splineViewport.getSpline().referencePoints.remove(splineViewport.getSpline().referencePoints.getLast());
                }
                else {
                    if (splineViewport.getSpline().referencePoints.isEmpty()) {
                        splineViewport.getSpline().referencePoints.add(new Point2D.Double(0, 0));
                    }
                    else{
                        splineViewport.getSpline().referencePoints.add(new Point2D.Double(splineViewport.getSpline().referencePoints.getLast().getX() + 1, 0));
                    }
                }
            }

            splineViewport.repaint();
        });
        add(K);

        JLabel splineRedLabel = new JLabel("Spline red");
        splineRedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(splineRedLabel);
        SplineRed = new JSpinner();
        add(SplineRed);

        JLabel genRedLabel = new JLabel("Generatix red");
        genRedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(genRedLabel);
        GeneratixRed = new JSpinner();
        add(GeneratixRed);

        // 2 ROW
        JLabel m1Label = new JLabel("M1");
        m1Label.setHorizontalAlignment(SwingConstants.RIGHT);
        add(m1Label);
        M1 = new JSpinner(new SpinnerNumberModel(viewport3D.scene.rotationFigure.getM1(), 1, 360, 1));
        M1.addChangeListener(e -> {
            JSpinner source = (JSpinner) e.getSource();
            viewport3D.scene.rotationFigure.setM1((int)source.getValue());
            if (viewport3D.scene.isAutoChange) {
                viewport3D.update();
            }
        });
        add(M1);

        JLabel mLabel = new JLabel("M");
        mLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(mLabel);
        M = new JSpinner(new SpinnerNumberModel(viewport3D.scene.rotationFigure.getM(), 2, 360, 1));
        M.addChangeListener(e -> {
            JSpinner source = (JSpinner) e.getSource();
            viewport3D.scene.rotationFigure.setM((int) source.getValue());
            if (viewport3D.scene.isAutoChange) {
                viewport3D.update();
            }
        });

        add(M);

        JLabel splineGreenLabel = new JLabel("Spline green");
        splineGreenLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(splineGreenLabel);
        splineGreen = new JSpinner();
        add(splineGreen);

        JLabel genGreenLabel = new JLabel("Generatix green");
        genGreenLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(genGreenLabel);
        generatixGreen = new JSpinner();
        add(generatixGreen);

        // 3 ROW
        JLabel xLabel = new JLabel("X");
        xLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(xLabel);
        X = new JSpinner();
        add(X);

        JLabel yLabel = new JLabel("Y");
        yLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(yLabel);
        Y = new JSpinner();
        add(Y);

        JLabel splineBlueLabel = new JLabel("Spline blue");
        splineBlueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(splineBlueLabel);
        SplineBlue = new JSpinner();
        add(SplineBlue);

        JLabel genBlueLabel = new JLabel("Generatix blue");
        genBlueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(genBlueLabel);
        GeneratixBlue = new JSpinner();
        add(GeneratixBlue);

        // 4 ROW
        okButton = new JButton("OK");
        okButton.addActionListener(e -> this.getParent());
        add(okButton);

        applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> {
            if ((int)K.getValue() < 4) {
                JOptionPane.showMessageDialog(this, "K should be more then 4");
            }
            else if (!viewport3D.scene.isAutoChange){
                viewport3D.scene.rotationFigure.setSpline(new BSpline(splineViewport.getSpline()));
                viewport3D.scene.rotationFigure.setM((int)M.getValue());
                viewport3D.scene.rotationFigure.getObject3D();
                viewport3D.repaint();
            }

        });
        add(applyButton);

        autoChangeButton = new JRadioButton("Auto change");
        if (viewport3D.scene.isAutoChange) {
            autoChangeButton.setSelected(true);
        }
        autoChangeButton.addActionListener(e -> {
            viewport3D.scene.isAutoChange = autoChangeButton.isSelected();
            if (autoChangeButton.isSelected()) {
                splineViewport.spline = viewport3D.scene.rotationFigure.getSpline();
                splineViewport.repaint();
                viewport3D.scene.rotationFigure.getObject3D();
                viewport3D.repaint();
            }
            else {
                splineViewport.setSpline(new BSpline(viewport3D.scene.rotationFigure.getSpline()));
                splineViewport.repaint();
                viewport3D.scene.rotationFigure.getObject3D();
                viewport3D.repaint();
            }
        });
        add(autoChangeButton);

        normalizeButton = new JButton("Normalize");
        add(normalizeButton);

        zoomPlusButton = new JButton("zoom+");
        add(zoomPlusButton);

        zoomMinusButton = new JButton("zoom-");
        add(zoomMinusButton);

        clearButton = new JButton("clear");
        clearButton.addActionListener(e -> {
            splineViewport.getSpline().referencePoints.clear();
            splineViewport.repaint();
            K.setValue(0);
        });
        add(clearButton);

        JLabel gridStepLabel = new JLabel("Grid step = 1");
        add(gridStepLabel);

    }
}
