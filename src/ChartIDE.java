import javax.swing.*;
import java.awt.*;

public class ChartIDE {
    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("Chart IDE");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);

        // Divide the frame into two panels
        JSplitPane splitPane = new JSplitPane();
        frame.add(splitPane, BorderLayout.CENTER);

        // Panel for graphics viewer
        JPanel graphicsViewer = new JPanel();
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