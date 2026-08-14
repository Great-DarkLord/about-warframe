import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.event.*;

/**
 * GraphPlot - plots y = 200*a * x*(1+0.25*x) for x in [1,4] and y = 50.
 * A JSlider below the plot controls 'a' in real time and there's also
 * a text input box where you can type a value for 'a' (press Enter or click Set).
 *
 * Slider mapping: integer range [-5000,5000] mapped to a = value/1000.0
 * -> one integer step = 0.001 change in 'a'.
 */
public class GraphPlot {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Function Plotter");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // graph panel
            GraphPanel graph = new GraphPanel(1.0); // initial a = 1.0
            frame.add(graph, BorderLayout.CENTER);

            // slider and controls panel
            JPanel control = new JPanel(new BorderLayout(8, 8));
            control.setBorder(BorderFactory.createEmptyBorder(6, 10, 10, 10));

            // Slider configuration (reduced sensitivity)
            final int sliderMin = -5000; // -> -5.000
            final int sliderMax = 5000;  // ->  5.000
            final int sliderInit = 1000; // ->  1.000
            JSlider slider = new JSlider(JSlider.HORIZONTAL, sliderMin, sliderMax, sliderInit);

            // Ticks and labels
            slider.setMajorTickSpacing(2500); // -5.00, 0.00, 5.00
            slider.setMinorTickSpacing(500);  // every 0.5
            slider.setPaintTicks(true);
            Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
            labelTable.put(sliderMin, new JLabel(String.format("%.3f", sliderMin / 1000.0)));
            labelTable.put(-2500, new JLabel(String.format("%.3f", -2500 / 1000.0)));
            labelTable.put(0, new JLabel("0.000"));
            labelTable.put(2500, new JLabel(String.format("%.3f", 2500 / 1000.0)));
            labelTable.put(sliderMax, new JLabel(String.format("%.3f", sliderMax / 1000.0)));
            slider.setLabelTable(labelTable);
            slider.setPaintLabels(true);

            control.add(slider, BorderLayout.CENTER);

            // Top label showing current a
            JLabel currentLabel = new JLabel("a = 1.000");
            currentLabel.setHorizontalAlignment(SwingConstants.CENTER);
            control.add(currentLabel, BorderLayout.NORTH);

            // Reset button
            JButton resetBtn = new JButton("Reset a = 1.000");
            control.add(resetBtn, BorderLayout.EAST);

            // Bottom area: text input + Set button
            JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
            JLabel enterLabel = new JLabel("Enter a:");
            JTextField aField = new JTextField(String.format("%.3f", sliderInit / 1000.0), 8);
            JButton setBtn = new JButton("Set");
            inputPanel.add(enterLabel);
            inputPanel.add(aField);
            inputPanel.add(setBtn);
            control.add(inputPanel, BorderLayout.SOUTH);

            // Format for display
            DecimalFormat fmt = new DecimalFormat("0.000");

            // Helper: set value of 'a' from a double (synchronizes slider, text field, label, and graph)
            Runnable setAFromDouble = new Runnable() {
                // We'll use an array to make 'a' accessible inside this Runnable when invoked with a captured value.
                public void run() {}
            };

            // Change listener for slider -> update graph and text field
            slider.addChangeListener(new ChangeListener() {
                private boolean updating = false;
                @Override
                public void stateChanged(ChangeEvent e) {
                    if (updating) return;
                    try {
                        updating = true;
                        double a = slider.getValue() / 1000.0;
                        graph.setA(a);
                        currentLabel.setText("a = " + fmt.format(a));
                        // Update text field without moving caret unexpectedly
                        aField.setText(fmt.format(a));
                    } finally {
                        updating = false;
                    }
                }
            });

            // Action to apply typed value into slider/graph
            ActionListener applyTypedA = ev -> {
                String text = aField.getText().trim();
                if (text.isEmpty()) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
                try {
                    // Allow locale independent decimal point; use Double.parseDouble
                    double a = Double.parseDouble(text);
                    if (a < sliderMin / 1000.0 || a > sliderMax / 1000.0) {
                        JOptionPane.showMessageDialog(frame,
                                String.format("Value out of range. Enter a value between %.3f and %.3f.",
                                        sliderMin / 1000.0, sliderMax / 1000.0),
                                "Out of range", JOptionPane.WARNING_MESSAGE);
                        // restore displayed value to current slider value
                        aField.setText(fmt.format(slider.getValue() / 1000.0));
                        return;
                    }
                    // round to nearest thousandth to align with slider steps
                    double rounded = Math.round(a * 1000.0) / 1000.0;
                    int sliderVal = (int) Math.round(rounded * 1000.0);
                    slider.setValue(sliderVal); // this will trigger ChangeListener and update everything
                    // ensure display shows the rounded value
                    aField.setText(fmt.format(rounded));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Invalid number format. Please enter a decimal number (e.g. 1.234).",
                            "Invalid input", JOptionPane.ERROR_MESSAGE);
                    // restore to the last known good value
                    aField.setText(fmt.format(slider.getValue() / 1000.0));
                }
            };

            // Bind Enter in text field to applyTypedA
            aField.addActionListener(applyTypedA);
            // Also apply when Set button clicked
            setBtn.addActionListener(applyTypedA);

            // Focus lost: apply as well (useful if user types then clicks elsewhere)
            aField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    // apply typed value when focus leaves field
                    applyTypedA.actionPerformed(null);
                }
            });

            // Reset button action
            resetBtn.addActionListener(ev -> {
                slider.setValue(sliderInit); // triggers the slider listener which updates the text field
            });

            frame.add(control, BorderLayout.SOUTH);

            frame.setSize(900, 620);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    /**
     * GraphPanel draws the two functions. It automatically rescales y-range
     * based on current a and the data values sampled on x in [1,4].
     */
    static class GraphPanel extends JPanel {
        private double a; // parameter a
        private final double xMin = 1.0;
        private final double xMax = 4.0;
        private final int samples = 400; // number of samples along x

        public GraphPanel(double initialA) {
            this.a = initialA;
            setBackground(Color.WHITE);
        }

        public void setA(double a) {
            this.a = a;
            repaint();
        }

        private double f1(double x) {
            return 200.0 * a * x * (1.0 + 0.25 * x);
        }

        private double f2(double x) {
            return 50.0;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int marginLeft = 70;
                int marginRight = 20;
                int marginTop = 20;
                int marginBottom = 70;

                // Sample functions to find y-range
                double yMin = Double.POSITIVE_INFINITY;
                double yMax = Double.NEGATIVE_INFINITY;
                double[] ys1 = new double[samples + 1];
                double[] xs = new double[samples + 1];
                for (int i = 0; i <= samples; i++) {
                    double t = (double) i / samples;
                    double x = xMin + t * (xMax - xMin);
                    double y1 = f1(x);
                    double y2 = f2(x);
                    xs[i] = x;
                    ys1[i] = y1;
                    if (y1 < yMin) yMin = y1;
                    if (y1 > yMax) yMax = y1;
                    if (y2 < yMin) yMin = y2;
                    if (y2 > yMax) yMax = y2;
                }

                // Add some padding
                double yRange = yMax - yMin;
                if (yRange == 0) {
                    yRange = Math.abs(yMax);
                    if (yRange == 0) yRange = 1.0;
                    yMin -= yRange * 0.5;
                    yMax += yRange * 0.5;
                } else {
                    yMin -= yRange * 0.12;
                    yMax += yRange * 0.12;
                }

                // Draw background grid
                g.setColor(new Color(240, 240, 240));
                for (int i = 0; i <= 10; i++) {
                    int yy = marginTop + (int) ((h - marginTop - marginBottom) * i / 10.0);
                    g.fillRect(marginLeft, yy, w - marginLeft - marginRight, 1);
                }

                // Axes
                g.setColor(Color.BLACK);
                int plotLeft = marginLeft;
                int plotRight = w - marginRight;
                int plotTop = marginTop;
                int plotBottom = h - marginBottom;
                g.drawRect(plotLeft, plotTop, plotRight - plotLeft, plotBottom - plotTop);

                // Ticks and labels for x
                g.setFont(g.getFont().deriveFont(12f));
                int xTicks = 6;
                for (int i = 0; i <= xTicks; i++) {
                    double t = (double) i / xTicks;
                    double x = xMin + t * (xMax - xMin);
                    int px = toPixelX(x, plotLeft, plotRight);
                    int py = plotBottom;
                    g.drawLine(px, py, px, py + 6);
                    String label = String.format("%.2f", x);
                    int sw = g.getFontMetrics().stringWidth(label);
                    g.drawString(label, px - sw / 2, py + 22);
                }

                // Ticks and labels for y
                int yTicks = 8;
                for (int i = 0; i <= yTicks; i++) {
                    double t = (double) i / yTicks;
                    double y = yMax - t * (yMax - yMin);
                    int py = toPixelY(y, plotTop, plotBottom);
                    int px = plotLeft;
                    g.drawLine(px - 6, py, px, py);
                    String label = String.format("%.2f", y);
                    int sw = g.getFontMetrics().stringWidth(label);
                    g.drawString(label, px - sw - 10, py + 5);
                }

                // Axis labels
                g.drawString("x", plotRight - 10, plotBottom + 32);
                g.drawString("y", plotLeft - 40, plotTop + 12);

                // Draw the second function y = 50 (constant) in blue
                g.setColor(new Color(30, 120, 200));
                Stroke oldStroke = g.getStroke();
                g.setStroke(new BasicStroke(2f));
                int prevX = toPixelX(xs[0], plotLeft, plotRight);
                int prevY = toPixelY(f2(xs[0]), plotTop, plotBottom);
                for (int i = 1; i <= samples; i++) {
                    int px = toPixelX(xs[i], plotLeft, plotRight);
                    int py = toPixelY(f2(xs[i]), plotTop, plotBottom);
                    g.drawLine(prevX, prevY, px, py);
                    prevX = px; prevY = py;
                }

                // Draw the parameterized function y = 200*a * x*(1+0.25*x) in red
                g.setColor(new Color(200, 50, 50));
                g.setStroke(new BasicStroke(2f));
                prevX = toPixelX(xs[0], plotLeft, plotRight);
                prevY = toPixelY(ys1[0], plotTop, plotBottom);
                for (int i = 1; i <= samples; i++) {
                    int px = toPixelX(xs[i], plotLeft, plotRight);
                    int py = toPixelY(ys1[i], plotTop, plotBottom);
                    g.drawLine(prevX, prevY, px, py);
                    prevX = px; prevY = py;
                }
                g.setStroke(oldStroke);

                // Legend
                int lx = plotLeft + 12;
                int ly = plotTop + 12;
                int legW = 140;
                int legH = 48;
                g.setColor(new Color(255,255,255,230));
                g.fillRect(lx-6, ly-14, legW, legH);
                g.setColor(Color.BLACK);
                g.drawRect(lx-6, ly-14, legW, legH);

                g.setStroke(new BasicStroke(3f));
                g.setColor(new Color(200, 50, 50));
                g.drawLine(lx, ly, lx + 30, ly);
                g.setColor(Color.BLACK);
                g.drawString("y = 200*a*x*(1+0.25*x)", lx + 36, ly + 5);
                g.setColor(new Color(30,120,200));
                g.drawLine(lx, ly + 20, lx + 30, ly + 20);
                g.setColor(Color.BLACK);
                g.drawString("y = 50", lx + 36, ly + 25);

                // Title with current 'a'
                g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
                String title = String.format("y = 200*a*x*(1+0.25*x) (a = %.3f)   and   y = 50", a);
                int tw = g.getFontMetrics().stringWidth(title);
                g.drawString(title, Math.max(plotLeft, (w - tw) / 2), 16);

            } finally {
                g.dispose();
            }
        }

        private int toPixelX(double x, int plotLeft, int plotRight) {
            double t = (x - xMin) / (xMax - xMin);
            return plotLeft + (int) Math.round(t * (plotRight - plotLeft));
        }

        private int toPixelY(double y, int plotTop, int plotBottom) {
            // Recompute yMin,yMax in the same way as paintComponent to keep mapping consistent
            double yMin = Double.POSITIVE_INFINITY, yMax = Double.NEGATIVE_INFINITY;
            int s = 60;
            for (int i = 0; i <= s; i++) {
                double t = (double) i / s;
                double xx = xMin + t * (xMax - xMin);
                double yy1 = f1(xx);
                double yy2 = f2(xx);
                if (yy1 < yMin) yMin = yy1;
                if (yy1 > yMax) yMax = yy1;
                if (yy2 < yMin) yMin = yy2;
                if (yy2 > yMax) yMax = yy2;
            }
            double range = yMax - yMin;
            if (range == 0) {
                range = Math.abs(yMax);
                if (range == 0) range = 1.0;
                yMin -= range * 0.5;
                yMax += range * 0.5;
            } else {
                yMin -= range * 0.12;
                yMax += range * 0.12;
            }
            double t = (y - yMin) / (yMax - yMin);
            return plotBottom - (int) Math.round(t * (plotBottom - plotTop));
        }
    }
}