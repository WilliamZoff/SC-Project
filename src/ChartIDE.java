import javax.swing.*;
import java.awt.*;
import java.io.File;

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
        frame.setSize(1000, 600);

        // Divide the frame into two panels
        JSplitPane splitPane = new JSplitPane();
        frame.add(splitPane, BorderLayout.CENTER);

        // Create JMenuBar and JMenuItems
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        // Add items to the File menu
        JMenuItem openFile = new JMenuItem("Open File");
        JMenuItem saveFile = new JMenuItem("Save File");
        fileMenu.add(openFile);
        fileMenu.add(saveFile);

        // Action Listeners for Menu Items
        openFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                // Code to handle file opening
            }
        });
        saveFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                // Code to handle file saving
            }
        });

        // Panel for graphics viewer with custom drawing
        JPanel graphicsViewer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGrid(g);
            }

            private void drawGrid(@org.jetbrains.annotations.NotNull Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();

                // Define the pattern for dashed lines and set color
                float[] dashPattern = { 5, 3 }; // 5 pixels line, 3 pixels space
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0));
                g2d.setColor(Color.GRAY);

                int width = getWidth();
                int height = getHeight();

                // Draw the vertical lines
                for (int i = 0; i < width; i += 50) {
                    g2d.drawLine(i, 0, i, height);
                }

                // Draw the horizontal lines
                for (int i = 0; i < height; i += 50) {
                    g2d.drawLine(0, i, width, i);
                }

                g2d.dispose();
            }

        };
        graphicsViewer.setBackground(Color.WHITE);
        splitPane.setLeftComponent(graphicsViewer);

        // TextArea for the language editor
        JTextArea languageEditor = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(languageEditor);
        splitPane.setRightComponent(scrollPane);

        // Set the divider location
        splitPane.setDividerLocation(700);

        // Display the frame
        frame.setVisible(true);
    }
}