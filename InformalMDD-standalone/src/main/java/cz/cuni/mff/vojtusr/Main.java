package cz.cuni.mff.vojtusr;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    private static final int fontSize = 14;
    private static final String xsltDir = "InformalMDD-core/src/main/resources/xsl_templates";
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        UIManager.put("Label.font", new Font("Monospaced", Font.PLAIN, fontSize));
        UIManager.put("Button.font", new Font("Monospaced", Font.PLAIN, fontSize));
        UIManager.put("TextField.font", new Font("Monospaced", Font.PLAIN, fontSize));
        UIManager.put("RadioButton.font", new Font("Monospaced", Font.PLAIN, fontSize));
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

        JPanel configFormPanel = new JPanel();
        configFormPanel.setLayout(new GridLayout(8, 2, 10, 10));
        configFormPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        topPanel.add(configFormPanel);

        JLabel projectNameLabel = new JLabel("Project Name");
        configFormPanel.add(projectNameLabel);

        JTextField projectNameTextField = new JTextField(20);
        configFormPanel.add(projectNameTextField);

        JLabel projectDirLabel = new JLabel("Project Directory");
        configFormPanel.add(projectDirLabel);

        JTextField projectDirTextField = new JTextField(30);
        projectDirTextField.setText("No directory selected");
        projectDirTextField.setHorizontalAlignment(JTextField.CENTER);
        projectDirTextField.setEditable(false);
        configFormPanel.add(projectDirTextField);

        configFormPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton projectDirChooseButton = getProjectDirChooseButton(projectDirTextField);
        configFormPanel.add(projectDirChooseButton);

        JLabel xsltLabel = new JLabel("XSLT Template");
        configFormPanel.add(xsltLabel);

        JComboBox<String> xsltTemplateComboBox = new JComboBox<>(getXSLTTemplates(Path.of(xsltDir)));
        configFormPanel.add(xsltTemplateComboBox);

        configFormPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton chooseXSLTTemplateButton = getXSLTChooseButton(xsltTemplateComboBox);
        configFormPanel.add(chooseXSLTTemplateButton);

        JRadioButton newProjectRadioButton = new JRadioButton("New UMLet Project");
        newProjectRadioButton.setSelected(true);
        configFormPanel.add(newProjectRadioButton);

        JLabel openProjectLabel = new JLabel("No file selected");
        openProjectLabel.setHorizontalAlignment(JLabel.CENTER);
        configFormPanel.add(openProjectLabel);

        JRadioButton openProjectRadioButton = new JRadioButton("Open UMLet Project");
        configFormPanel.add(openProjectRadioButton);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(newProjectRadioButton);
        buttonGroup.add(openProjectRadioButton);

        JButton openProjectButton = new JButton("Choose UMLet File");
        configFormPanel.add(openProjectButton);

        return topPanel;
    }

    private static String[] getXSLTTemplates(Path dir) {
        try (Stream<Path> pathStream = Files.list(dir)) {
            List<String> fileNames = new ArrayList<>();
            pathStream.forEach(path -> {
               if (Files.isRegularFile(path)) {
                   fileNames.add(path.getFileName().toString());
               }
            });
            return fileNames.toArray(new String[0]);
        } catch (IOException e) {
            System.err.println("Failed to list XSLT templates: " + e);
            return new String[]{"Error"};
        }
    }

    private static JButton getXSLTChooseButton(JComboBox<String> xsltTextField) {
        JButton projectDirChooseButton = new JButton("Choose Template");
        projectDirChooseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setDialogTitle("Choose Template");

            int returnVal = chooser.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                xsltTextField.addItem(chooser.getSelectedFile().getName());
                xsltTextField.setSelectedItem(chooser.getSelectedFile().getName());
            } else {
                xsltTextField.setSelectedIndex(0);
            }
        });
        return projectDirChooseButton;
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