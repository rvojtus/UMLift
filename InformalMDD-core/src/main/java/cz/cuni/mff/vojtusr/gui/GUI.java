package cz.cuni.mff.vojtusr.gui;

import com.baselet.gui.CurrentGui;
import com.baselet.standalone.MainStandalone;
import cz.cuni.mff.vojtusr.emf.JavaGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GUI {
    private static final int fontSize = 14;
    private static final String UMLetFileExtension = ".uxf";

    private static final JFrame mainFrame = new JFrame("InformalMDD-CodeGenerator");
    private static final JPanel topPanel = getTopPanel();

    private static JTextField projectNameTextField;
    private static JTextField projectDirTextField;
    private static JTextField openUMLetTextField;

    private static boolean isNewProject = true;

    private static String umletFilePath = "";

    public static void createAndShowGUI() {
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

    public static JPanel getTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel mddLabel = getCenteredMainLabel("Informal MDD");
        topPanel.add(mddLabel);

        JLabel codeGenLabel = getCenteredMainLabel("Java Code Generator");
        topPanel.add(codeGenLabel);

        topPanel.add(Box.createRigidArea(new Dimension(10, 0)));//pseudo empty line

        JPanel configFormPanel = getConfigFormPanel();
        topPanel.add(configFormPanel);

        JPanel startProgramButtonsPanel = getStartProgramButtonPanel(configFormPanel);
        topPanel.add(startProgramButtonsPanel);

        return topPanel;
    }

    private static JLabel getCenteredMainLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Monospaced", Font.PLAIN, 28));
        return label;
    }

    private static JPanel getConfigFormPanel() {
        JPanel configFormPanel = new JPanel();
        configFormPanel.setLayout(new GridLayout(6, 2, 10, 10));
        configFormPanel.setBorder(new EmptyBorder(20, 20, 20, 20));


        JLabel projectNameLabel = new JLabel("Project Name");
        configFormPanel.add(projectNameLabel);

        projectNameTextField = new JTextField(20);
        projectNameTextField.setText("projectTest");// temp
        configFormPanel.add(projectNameTextField);

        JLabel projectDirLabel = new JLabel("Project Directory");
        configFormPanel.add(projectDirLabel);

        projectDirTextField = new JTextField(30);
        //projectDirTextField.setText("No directory selected");
        projectDirTextField.setText("/Users/rastislav.vojtus/Documents/Bakalarka/testing");//temp
        projectDirTextField.setHorizontalAlignment(JTextField.CENTER);
        projectDirTextField.setEditable(false);
        configFormPanel.add(projectDirTextField);

        configFormPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton projectDirChooseButton = getProjectDirChooseButton(projectDirTextField);
        configFormPanel.add(projectDirChooseButton);

        //configFormPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        openUMLetTextField = new JTextField("No file selected");
        openUMLetTextField.setText("/Users/rastislav.vojtus/Documents/Bakalarka/Umlet/testHello.uxf");//temp
        openUMLetTextField.setEditable(false);

        JRadioButton newProjectRadioButton = new JRadioButton("New UMLet Project");
        newProjectRadioButton.setSelected(true);

        configFormPanel.add(newProjectRadioButton);


        openUMLetTextField.setVisible(false);
        openUMLetTextField.setHorizontalAlignment(JLabel.CENTER);
        configFormPanel.add(openUMLetTextField);

        JRadioButton openProjectRadioButton = new JRadioButton("Open UMLet Project");

        configFormPanel.add(openProjectRadioButton);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(newProjectRadioButton);
        buttonGroup.add(openProjectRadioButton);

        JButton openProjectFileButton = getProjectFileChooseButton(openUMLetTextField, openProjectRadioButton);

        newProjectRadioButton.addActionListener(e -> {
            openUMLetTextField.setVisible(false);
            openProjectFileButton.setEnabled(false);
            isNewProject = true;
        });
        openProjectRadioButton.addActionListener(e -> {
            openUMLetTextField.setVisible(true);
            openProjectFileButton.setEnabled(true);
            isNewProject = false;
        });
        configFormPanel.add(openProjectFileButton);

        return configFormPanel;
    }

    private static JPanel getStartProgramButtonPanel(JPanel configPanel) {
        JPanel programButtonPanel = new JPanel();
        programButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 0));

        JButton openUMLetButton = new JButton("Start UMLet Tool");
        openUMLetButton.addActionListener(e -> {
            if (!validateStartUMLet()) {
                JOptionPane.showMessageDialog(mainFrame, "Error occurred during validation!");
                return;
            }
            if (isNewProject) {
                startUMLet();
            } else {
                startUMLet(umletFilePath);
            }
            mainFrame.setVisible(false);
        });
        programButtonPanel.add(openUMLetButton);

        JButton generateCodeButton = new JButton("Generate Code");
        generateCodeButton.addActionListener(e -> {
            if (!validateStartCodeGeneration()) {
                JOptionPane.showMessageDialog(mainFrame, "Error occurred during validation!");
                return;
            } else {
                startCodeGeneration();
            }
        });
        programButtonPanel.add(generateCodeButton);

        Dimension buttonSize = new Dimension(200, 50);
        openUMLetButton.setPreferredSize(buttonSize);
        generateCodeButton.setPreferredSize(buttonSize);

        return programButtonPanel;
    }

    private static void startUMLet() {
        MainStandalone.main(new String[]{});
        CurrentGui.getInstance().getGui().getMainFrame().setVisible(true);
        addCodeGenerationButton();
    }

    private static void startUMLet(String filePath) {
        System.out.println("Starting UMLet: " + filePath);
        MainStandalone.main(new String[]{filePath});
        addCodeGenerationButton();
    }

    private static void startCodeGeneration() {
        try {
            JavaGenerator javaGenerator = new JavaGenerator();
            javaGenerator.generateCodeFromUMLetProject(projectDirTextField.getText(), projectNameTextField.getText());
            File projectDir = new File(projectDirTextField.getText());
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.open(projectDir);
                } catch (IOException e) {
                    System.out.println("Error opening project dir: " + projectDir.getAbsolutePath());
                }
            } else {
                System.out.println("Desktop is not supported.");
            }
        } catch (IOException e) {// todo better error display
            System.err.println("IOException occurred: " + e);
        } catch (TransformerException e) {
            System.err.println("TransformerException occurred: " + e);
        }
    }

    private static String getUMLInputFilePath() {
        String openFileUMLet = CurrentGui.getInstance().getGui().getCurrentDiagram().getHandler().getFileHandler().getFullPathName();
        System.out.println("Open File: " + openFileUMLet);
        return openFileUMLet;
    }

    private static boolean validateStartUMLet() {
        boolean validProjectName = validateProjectName();
        boolean validProjectDir = validateProjectDir();
        boolean validUMLetFile = validateUMLetFile();
        //System.err.println(validProjectName + " " + validProjectDir + " " + validUMLetFile);
        return validProjectName && validProjectDir && validUMLetFile;
    }

    private static boolean validateStartCodeGeneration() {
        return validateProjectName() && validateProjectDir() && validateUMLetFile() && !isNewProject;
    }

    private static boolean validateProjectName() {
        if (projectNameTextField.getText().trim().isEmpty()) {
            projectNameTextField.setBorder(new LineBorder(Color.RED, 2));
            JOptionPane.showMessageDialog(mainFrame, "Please enter a project name.");
            projectNameTextField.requestFocus();
            return false;
        }
        projectNameTextField.setBorder(UIManager.getBorder("TextField.border"));
        return true;
    }

    private static boolean validateProjectDir() {
        // initial check if the user selected any directory
        if (projectDirTextField.getText().trim().isEmpty() || projectDirTextField.getText().trim().equals("No directory selected")) {
            projectDirTextField.setBorder(new LineBorder(Color.RED, 2));
            JOptionPane.showMessageDialog(mainFrame, "Please enter a project directory.");
            projectDirTextField.requestFocus();
            return false;
        }
        Path projectDir = Path.of(projectDirTextField.getText().trim());
        // checks if the selected directory is valid
        if (!Files.isDirectory(projectDir)) {
            projectDirTextField.setBorder(new LineBorder(Color.RED, 2));
            JOptionPane.showMessageDialog(mainFrame, "Invalid project directory.");
            projectDirTextField.requestFocus();
            return false;
        }
        projectDirTextField.setBorder(UIManager.getBorder("TextField.border"));
        return true;
    }

    private static boolean validateUMLetFile() {
        if (!isNewProject) {
            if (openUMLetTextField.getText().trim().isEmpty() || openUMLetTextField.getText().trim().equals("No file selected")) {
                openUMLetTextField.setBorder(new LineBorder(Color.RED, 2));
                JOptionPane.showMessageDialog(mainFrame, "Please enter a valid UMLet file.");
                openUMLetTextField.requestFocus();
                return false;
            }
            openUMLetTextField.setBorder(UIManager.getBorder("TextField.border"));
            Path UMLetFilePath = Path.of(openUMLetTextField.getText().trim());
            //return Files.isRegularFile(UMLetFilePath);
        }
        return true;
    }

    private static JButton getProjectFileChooseButton(JTextField fileSelectedLabel, JRadioButton openProjectRadioButton) {
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
                    return "UMLet Files (*" + UMLetFileExtension + ")";
                }
            });
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setDialogTitle("Choose UMLet File");

            int returnVal = fileChooser.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fileSelectedLabel.setText(fileChooser.getSelectedFile().getName());
                umletFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            }
        });
        return openProjectButton;
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

    public static JButton getCodeGenerationButton() {
        return new JButton(new AbstractAction("Code Generation") {
            @Override
            public void actionPerformed(ActionEvent e) {
                startCodeGeneration();
            }
        });
    }

    private static void addCodeGenerationButton() {
        CurrentGui currentGui = CurrentGui.getInstance();
        JFrame mainUMLetFrame = (JFrame) currentGui.getGui().getMainFrame();
        JMenuBar menu = mainUMLetFrame.getJMenuBar();
        // todo Add JMenuItem - Code Generation Options Panel
        JButton codeGenerationButton = new JButton(new AbstractAction("Code Generation") {
            @Override
            public void actionPerformed(ActionEvent e) {
                startCodeGeneration();
            }
        });

        menu.add(codeGenerationButton);

        SwingUtilities.updateComponentTreeUI(mainUMLetFrame);
    }
}
