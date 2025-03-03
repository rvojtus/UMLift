package cz.cuni.mff.imdd.gui;


import cz.cuni.mff.imdd.UMLiftMainStandalone;
import cz.cuni.mff.umlift.core.emf.CodeGenerationConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.prefs.Preferences;

/**
 * JDialog representing configuration, used for core features - transformation, artifact generation, by the main program.
 *
 * @see UMLiftMainStandalone
 * @since 1.0
 */
public class ConfigDialog extends JDialog {
    private static final int DIALOG_WIDTH = 800;
    private static final Preferences prefs = Preferences.userNodeForPackage(ConfigDialog.class);

    // Ecore
    private final JTextField projectNameTextField;
    private final JTextField nsURITextField;
    private final JTextField nsPrefixTextField;
    private final JTextField ecoreFileNameTextField;

    // GenModel
    private final JTextField genModelFileNameTextField;

    // Generation
    private final DirectoryChooserPanel ecoreGenModelOutputDirFileChooser;
    private final DirectoryChooserPanel generatedFilesOutputDirFileChooser;

    public ConfigDialog(Frame owner) {
        super(owner, "Config", true);
        this.setVisible(false);
        setLayout(new GridLayout(8, 2, 5, 5));
        ((JPanel) this.getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // UI Components
        JLabel projectNameLabel = new JLabel("Project name:");
        projectNameTextField = new JTextField(prefs.get("projectName", "exampleProject"));
        add(projectNameLabel);
        add(projectNameTextField);

        JLabel nsURILabel = new JLabel("NS URI:");
        nsURITextField = new JTextField(prefs.get("nsURI", "exampleNSURI"));
        add(nsURILabel);
        add(nsURITextField);

        JLabel nsPrefixLabel = new JLabel("NS Prefix:");
        nsPrefixTextField = new JTextField(prefs.get("nsPrefix", "exampleNSPrefix"));
        add(nsPrefixLabel);
        add(nsPrefixTextField);

        JLabel ecoreFileNameLabel = new JLabel("Ecore file name:");
        ecoreFileNameTextField = new JTextField(prefs.get("ecoreFileName", "exampleEcoreFileName"));
        add(ecoreFileNameLabel);
        add(ecoreFileNameTextField);

        JLabel genModelFileNameLabel = new JLabel("Gen model file name:");
        genModelFileNameTextField = new JTextField(prefs.get("genModelFileName", "exampleGenModelFileName"));
        add(genModelFileNameLabel);
        add(genModelFileNameTextField);

        JLabel ecoreGenModelOutputDirLabel = new JLabel("EMF models output directory:");
        ecoreGenModelOutputDirFileChooser = new DirectoryChooserPanel(prefs.get("ecoreGenModelOutputDir", "resources/"));
        add(ecoreGenModelOutputDirLabel);
        add(ecoreGenModelOutputDirFileChooser);

        JLabel generatedFilesOutputDirLabel = new JLabel("Generated files output directory:");
        generatedFilesOutputDirFileChooser = new DirectoryChooserPanel(prefs.get("generatedFilesOutputDir", "src-gen/"));
        add(generatedFilesOutputDirLabel);
        add(generatedFilesOutputDirFileChooser);

        // Empty cell
        add(new JPanel());

        // Buttons Panel
        JPanel buttonPanel = getButtonsPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setSize(DIALOG_WIDTH, getPreferredSize().height);
        setLocationRelativeTo(owner);
    }

    private JPanel getButtonsPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(this::saveConfig);
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> dispose());
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    private void saveConfig(ActionEvent event) {
        prefs.put("projectName", projectNameTextField.getText());
        prefs.put("nsURI", nsURITextField.getText());
        prefs.put("nsPrefix", nsPrefixTextField.getText());
        prefs.put("ecoreFileName", ecoreFileNameTextField.getText());

        prefs.put("genModelFileName", genModelFileNameTextField.getText());

        prefs.put("ecoreGenModelOutputDir", ecoreGenModelOutputDirFileChooser.getSelectedDirectory());
        prefs.put("generatedFilesOutputDir", generatedFilesOutputDirFileChooser.getSelectedDirectory());

        JOptionPane.showMessageDialog(this, "Configuration saved.");
        dispose();
    }

    private void loadConfig() {
        projectNameTextField.setText(prefs.get("projectName", "exampleProject"));
        nsURITextField.setText(prefs.get("nsURI", "exampleNSURI"));
        nsPrefixTextField.setText(prefs.get("nsPrefix", "exampleNSPrefix"));
        ecoreFileNameTextField.setText(prefs.get("ecoreFileName", "exampleEcoreFileName"));

        genModelFileNameTextField.setText(prefs.get("genModelFileName", "exampleGenModelFileName"));

        ecoreGenModelOutputDirFileChooser.setSelectedDirectory(prefs.get("ecoreGenModelOutputDir", "resources/"));
        generatedFilesOutputDirFileChooser.setSelectedDirectory(prefs.get("generatedFilesOutputDir", "src-gen/"));
    }

    public CodeGenerationConfig getCodeGenerationConfig() {

        return CodeGenerationConfig.getInstance()
                .setProjectName(projectNameTextField.getText())
                .setProjectNsURI(nsURITextField.getText())
                .setProjectNsPrefix(nsPrefixTextField.getText())
                .setEcoreFileName(ecoreFileNameTextField.getText())
                .setGenModelFileName(genModelFileNameTextField.getText())
                .setEcoreGenModelDir(Path.of(ecoreGenModelOutputDirFileChooser.getSelectedDirectory()))
                .setGeneratedFilesDir(Path.of(generatedFilesOutputDirFileChooser.getSelectedDirectory()))
                .setOutputEcoreFile(Path.of(ecoreGenModelOutputDirFileChooser.getSelectedDirectory() + File.separator + ecoreFileNameTextField.getText()))
                .setOutputGenModelFile(Path.of(ecoreGenModelOutputDirFileChooser.getSelectedDirectory() + File.separator + genModelFileNameTextField.getText()))
                .build();
    }

}
