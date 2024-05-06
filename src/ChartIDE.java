import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChartIDE {
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        // Change skin of Swing elements to "Windows" theme
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        // Apply default skin if not available
        } catch (Exception e) {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Default".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }

        // Create the main frame
        JFrame frame = new JFrame("Chart IDE");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400 , 750);

        // Divide the frame into two panels
        JSplitPane splitPane = new JSplitPane();
        frame.add(splitPane, BorderLayout.CENTER);

        // Initialize graphicsViwer
        JPanel graphicsViewer = new JPanel();

        // Create JMenuBar and JMenuItems
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        // Create buttons
        JButton undoButton = new JButton("Undo");
        JButton redoButton = new JButton("Redo");
        JButton origoButton = new JButton("Origo");

        // Add buttons to the menu bar
        menuBar.add(undoButton);
        menuBar.add(redoButton);
        menuBar.add(origoButton);

        JPanel finalGraphicsViewer2 = graphicsViewer;
        undoButton.addActionListener(e -> {
            GraphicalElementsManager.undoLastCommand();
            finalGraphicsViewer2.repaint();
        });

        JPanel finalGraphicsViewer3 = graphicsViewer;
        redoButton.addActionListener(e -> {
            GraphicalElementsManager.redoLastCommand();
            finalGraphicsViewer3.repaint();
        });

        AtomicBoolean showOrigo = new AtomicBoolean(true);  // Initially show origo
        JPanel finalGraphicsViewer4 = graphicsViewer;
        origoButton.addActionListener(e -> {
            showOrigo.set(!showOrigo.get());
            finalGraphicsViewer4.repaint();
        });

        // Label to display coordinates
        JLabel coordinateLabel = new JLabel("Coordinates: (0,0)");
        frame.add(coordinateLabel, BorderLayout.SOUTH);

        // Panel for graphics viewer with custom drawing
        graphicsViewer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Calculate center of the panel
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;

                // Translate the graphics context to center
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.translate(centerX, centerY);

                // Optionally, draw a small cross at the origin and label it
                if (showOrigo.get()) {
                    g2d.drawLine(-10, 0, 10, 0); // horizontal line
                    g2d.drawLine(0, -10, 0, 10); // vertical line
                    g2d.drawString("(0,0)", 5, -5); // label for origin
                }

                // Draw grid from the new origin
                drawGrid(g2d);

                // Draw elements from the new origin
                drawElements(g2d);

                g2d.dispose();
            }

            private void drawGrid(Graphics2D g) {
                int width = getWidth();
                int height = getHeight();

                // Calculate the center of the panel
                int centerX = 0;
                int centerY = 0;

                // Set the color for the grid lines
                g.setColor(Color.LIGHT_GRAY);

                // Determine the range to cover the panel from the center
                int halfWidth = width / 2;
                int halfHeight = height / 2;

                // Draw vertical lines
                for (int x = centerX; x < halfWidth; x += 50) {
                    g.drawLine(x, -halfHeight, x, halfHeight);
                    g.drawLine(-x, -halfHeight, -x, halfHeight);
                }

                // Draw horizontal lines
                for (int y = centerY; y < halfHeight; y += 50) {
                    g.drawLine(-halfWidth, y, halfWidth, y);
                    g.drawLine(-halfWidth, -y, halfWidth, -y);
                }

                // Enhance the origin lines to make them more visible
                g.setColor(Color.RED);  // Change color for origin lines
                g.drawLine(-10, 0, 10, 0); // Small horizontal line at origin
                g.drawLine(0, -10, 0, 10); // Small vertical line at origin
                g.drawString("(0,0)", 5, -5); // Label for origin
            }



            private void drawElements(Graphics2D g) {
                java.util.List<GraphicalElement> elements = GraphicalElementsManager.getJavaElements();
                for (GraphicalElement element : elements) {
                    g.setColor(Color.BLACK);
                    GraphicalElementsManager.drawWithClipping(g, element);
                    //element.draw(g, false);
                }
            }


        };
        graphicsViewer.setBackground(Color.WHITE);
        splitPane.setLeftComponent(graphicsViewer);

        // Add MouseMotionListener to graphicsViewer
        JPanel finalGraphicsViewer = graphicsViewer;
        graphicsViewer.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // Get the mouse coordinates and adjust by the center
                int centerX = finalGraphicsViewer.getWidth() / 2;
                int centerY = finalGraphicsViewer.getHeight() / 2;
                int x = e.getX() - centerX;
                int y = -(e.getY() - centerY);  // Invert y to match traditional Cartesian coordinates

                coordinateLabel.setText("Coordinates: (" + x + "," + y + ")");
            }
        });

        // TextArea for the language editor
        JTextArea languageEditor = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(languageEditor);
        splitPane.setRightComponent(scrollPane);

        JPanel finalGraphicsViewer1 = graphicsViewer;
        languageEditor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String command = languageEditor.getText().trim();
                    GraphicalElement element = GraphicalElementsManager.parseCommandToElement(command);
                    GraphicalElementsManager.addElement(element);
                    finalGraphicsViewer1.repaint();
                    languageEditor.setText(""); // Clear text editor on enter
                }
            }
        });



        // Set the divider location
        splitPane.setDividerLocation(700);

        // Display the frame
        frame.setVisible(true);
    }
}