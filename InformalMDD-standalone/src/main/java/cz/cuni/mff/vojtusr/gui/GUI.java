package cz.cuni.mff.vojtusr.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class GUI {
    private static final int fontSize = 14;
    private static final String xsltDir = "InformalMDD-core/src/main/resources/xsl_templates";
    private static final String UMLetFileExtension = ".uxf";

    private static final JFrame mainFrame = new JFrame("InformalMDD-CodeGenerator");
    private static final JPanel topPanel = getTopPanel();


    public static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        setFonts();
        mainFrame.setSize(500, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainFrame.setLayout(new BorderLayout());

        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        mainFrame.add(topPanel, BorderLayout.NORTH);
        mainFrame.setVisible(true);
    }

    private static void setFonts() {
        UIManager.put("Label.font", new Font("Monospaced", Font.PLAIN, fontSize));
        UIManager.put("Button.font", new Font("Monospaced", Font.PLAIN, fontSize));
        UIManager.put("TextField.font", new Font("Monospaced", Font.PLAIN, fontSize));
        UIManager.put("RadioButton.font", new Font("Monospaced", Font.PLAIN, fontSize));
    }

    private static JPanel getTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel mddLabel = getCenteredMainLabel("Informal MDD");
        topPanel.add(mddLabel);

        JLabel codeGenLabel = getCenteredMainLabel("Java Code Generator");
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

        JLabel openProjectLabel = new JLabel("No file selected");

        JRadioButton newProjectRadioButton = new JRadioButton("New UMLet Project");
        newProjectRadioButton.setSelected(true);

        configFormPanel.add(newProjectRadioButton);


        openProjectLabel.setVisible(false);
        openProjectLabel.setHorizontalAlignment(JLabel.CENTER);
        configFormPanel.add(openProjectLabel);

        JRadioButton openProjectRadioButton = new JRadioButton("Open UMLet Project");

        configFormPanel.add(openProjectRadioButton);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(newProjectRadioButton);
        buttonGroup.add(openProjectRadioButton);

        JButton openProjectFileButton = getProjectFileChooseButton(openProjectLabel, openProjectRadioButton);

        newProjectRadioButton.addActionListener(e -> {
            openProjectLabel.setVisible(false);
            openProjectFileButton.setEnabled(false);
        });
        openProjectRadioButton.addActionListener(e -> {
            openProjectLabel.setVisible(true);
            openProjectFileButton.setEnabled(true);
        });
        configFormPanel.add(openProjectFileButton);

        JButton openUMLetButton = new JButton("Start UMLet Tool");
        configFormPanel.add(openUMLetButton);

        JButton generateCodeButton = new JButton("Generate Code");
        configFormPanel.add(generateCodeButton);

        return topPanel;
    }

    private static JLabel getCenteredMainLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Monospaced", Font.PLAIN, 28));
        return label;
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

    private static JButton getProjectFileChooseButton(JLabel fileSelectedLabel, JRadioButton openProjectRadioButton) {
        JButton openProjectButton = new JButton("Choose UMLet File");
        openProjectButton.setEnabled(false);
        openProjectButton.addActionListener(e -> {
            if (!openProjectRadioButton.isSelected()) return;
            openProjectButton.setEnabled(true);
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(UMLetFileExtension);
                }

                @Override
                public String getDescription() {
                    return "UMLet Files (*"+UMLetFileExtension+")";
                }
            });
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setDialogTitle("Choose UMLet File");

            int returnVal = fileChooser.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fileSelectedLabel.setText(fileChooser.getSelectedFile().getName());
            }
        });
        return openProjectButton;
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
