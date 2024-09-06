package cz.cuni.mff.vojtusr;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    private static final int fontSize = 14;
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        UIManager.put("Label.font", new Font("Monospaced", Font.PLAIN, fontSize));
        UIManager.put("Button.font", new Font("Monospaced", Font.PLAIN, fontSize));
        UIManager.put("TextField.font", new Font("Monospaced", Font.PLAIN, fontSize));
        JFrame mainFrame = new JFrame("InformalMDD-CodeGenerator");
        mainFrame.setSize(500, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainFrame.setLayout(new BorderLayout());

        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        JPanel topPanel = getTopPanel();

        mainFrame.add(topPanel, BorderLayout.NORTH);
        mainFrame.setVisible(true);
    }

    private static JPanel getTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel mddLabel = new JLabel("Informal MDD");
        mddLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mddLabel.setFont(new Font("Monospaced", Font.PLAIN, 28));
        topPanel.add(mddLabel);

        JLabel codeGenLabel = new JLabel("Java Code Generator");
        codeGenLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        codeGenLabel.setFont(new Font("Monospaced", Font.PLAIN, 28));
        topPanel.add(codeGenLabel);

        topPanel.add(Box.createRigidArea(new Dimension(10, 0)));//pseudo empty line

        JPanel projectNamePanel = new JPanel();
        projectNamePanel.setLayout(new GridLayout(4, 2, 10, 10));
        projectNamePanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        topPanel.add(projectNamePanel);

        JLabel projectNameLabel = new JLabel("Project Name");
        projectNamePanel.add(projectNameLabel);

        JTextField projectNameTextField = new JTextField(20);
        projectNamePanel.add(projectNameTextField);

        JLabel projectDirLabel = new JLabel("Project Directory");
        projectNamePanel.add(projectDirLabel);

        JTextField projectDirTextField = new JTextField(30);
        projectDirTextField.setText("No directory selected");
        projectDirTextField.setEditable(false);
        projectNamePanel.add(projectDirTextField);

        projectNamePanel.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton projectDirChooseButton = getProjectDirChooseButton(projectDirTextField);
        projectNamePanel.add(projectDirChooseButton);

        JLabel xsltLabel = new JLabel("XSLT");

        return topPanel;
    }

    private static JButton getProjectDirChooseButton(JTextField projectDirTextField) {
        JButton projectDirChooseButton = new JButton("Choose Directory");
        projectDirChooseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setDialogTitle("Choose Directory");

            int returnVal = chooser.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                projectDirTextField.setText(chooser.getSelectedFile().getAbsolutePath());
            } else {
                projectDirTextField.setText("No directory selected");
            }
        });
        return projectDirChooseButton;
    }
}