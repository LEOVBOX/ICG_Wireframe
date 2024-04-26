package core;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class SplineViewportSettings extends JPanel {
    SplineViewport splineViewport;
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

    void setK(int K) {
        this.K.setValue(K);
    }


    public SplineViewportSettings(SplineViewport splineViewport, BSpline spline) {
        this.splineViewport = splineViewport;
        setBackground(Color.LIGHT_GRAY);
        setLayout(new GridLayout(4, 8, 10, 10));

        // 1 ROW
        JLabel nLabel = new JLabel("N");
        nLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(nLabel);
        N = new JSpinner(new SpinnerNumberModel(spline.N, 1, 500, 1));
        N.addChangeListener(e -> {
            JSpinner source = (JSpinner) e.getSource();
            int value = (int) source.getValue();
            spline.N = value;
            splineViewport.repaint();
        });
        add(N);


        JLabel kLabel = new JLabel("K");
        kLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(kLabel);
        K = new JSpinner(new SpinnerNumberModel(spline.referencePoints.size(), 0, 300, 1));
        K.addChangeListener(e -> {
            JSpinner source = (JSpinner) e.getSource();
            int value = (int) source.getValue();
            for (int i = 0; i < Math.abs(spline.referencePoints.size() - value); i++) {
                if (value < spline.referencePoints.size()) {
                    spline.referencePoints.remove(spline.referencePoints.getLast());
                }
                else {
                    if (spline.referencePoints.isEmpty()) {
                        spline.referencePoints.add(new Point2D.Double(0, 0));
                    }
                    else{
                        spline.referencePoints.add(new Point2D.Double(spline.referencePoints.getLast().getX() + 1, 0));
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
        M1 = new JSpinner();
        add(M1);

        JLabel mLabel = new JLabel("M");
        mLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(mLabel);
        M = new JSpinner();
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
        add(okButton);

        applyButton = new JButton("Apply");
        add(applyButton);

        autoChangeButton = new JRadioButton("Auto change");
        add(autoChangeButton);

        normalizeButton = new JButton("Normalize");
        add(normalizeButton);

        zoomPlusButton = new JButton("zoom+");
        add(zoomPlusButton);

        zoomMinusButton = new JButton("zoom-");
        add(zoomMinusButton);

        clearButton = new JButton("clear");
        clearButton.addActionListener(e -> {
            spline.referencePoints.clear();
            splineViewport.repaint();
            K.setValue(0);
        });
        add(clearButton);

        JLabel gridStepLabel = new JLabel("Grid step = 1");
        add(gridStepLabel);

    }
}
