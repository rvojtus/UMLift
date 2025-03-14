package cz.cuni.mff.umlift.standalone.gui;


import cz.cuni.mff.umlift.standalone.UMLiftMainStandalone;
import cz.cuni.mff.umlift.core.emf.CodeGenerationConfig;
import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * JDialog representing configuration, used for core features - transformation, artifact generation, by the main
 * program.
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
    private final JTextField packageNameTextField;
    private final JComboBox<GenJDKLevel> genJDKLevelComboBox;
    private final JTextField genModelFileNameTextField;

    // Generation
    private final DirectoryChooserPanel ecoreGenModelOutputDirFileChooser;
    private final DirectoryChooserPanel generatedFilesOutputDirFileChooser;

    public ConfigDialog(Frame owner) {
        super(owner, "Config", true);
        this.setVisible(false);
        setLayout(new GridLayout(10, 2, 5, 5));
        ((JPanel) this.getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // UI Components
        JLabel projectNameLabel = new JLabel("Project name:");
        projectNameTextField = new JTextField();
        add(projectNameLabel);
        add(projectNameTextField);

        JLabel nsURILabel = new JLabel("NS URI:");
        nsURITextField = new JTextField();
        add(nsURILabel);
        add(nsURITextField);

        JLabel nsPrefixLabel = new JLabel("NS Prefix:");
        nsPrefixTextField = new JTextField();
        add(nsPrefixLabel);
        add(nsPrefixTextField);

        JLabel ecoreFileNameLabel = new JLabel("Ecore file name:");
        ecoreFileNameTextField = new JTextField();
        add(ecoreFileNameLabel);
        add(ecoreFileNameTextField);

        JLabel packageNameLabel = new JLabel("Package name:");
        packageNameTextField = new JTextField();
        add(packageNameLabel);
        add(packageNameTextField);

        JLabel genJDKLevelLabel = new JLabel("Gen JDK level:");
        genJDKLevelComboBox = new JComboBox<>(GenJDKLevel.values());
        add(genJDKLevelLabel);
        add(genJDKLevelComboBox);

        JLabel genModelFileNameLabel = new JLabel("GenModel file name:");
        genModelFileNameTextField = new JTextField();
        add(genModelFileNameLabel);
        add(genModelFileNameTextField);

        JLabel ecoreGenModelOutputDirLabel = new JLabel("EMF models output directory:");
        ecoreGenModelOutputDirFileChooser = new DirectoryChooserPanel(null);
        add(ecoreGenModelOutputDirLabel);
        add(ecoreGenModelOutputDirFileChooser);

        JLabel generatedFilesOutputDirLabel = new JLabel("Generated files output directory:");
        generatedFilesOutputDirFileChooser = new DirectoryChooserPanel(null);
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
        loadConfig();
        revalidate();
        repaint();
    }

    private JPanel getButtonsPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(this::saveConfig);
        buttonPanel.add(saveButton);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(this::resetConfig);
        buttonPanel.add(resetButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    private void resetConfig(ActionEvent e) {
        try {
            prefs.clear();
            prefs.flush();
        } catch (BackingStoreException ignored) {
        } finally {
            loadConfig();
            revalidate();
            repaint();
        }
    }

    private void saveConfig(ActionEvent event) {
        prefs.put("projectName", projectNameTextField.getText());
        prefs.put("nsURI", nsURITextField.getText());
        prefs.put("nsPrefix", nsPrefixTextField.getText());
        prefs.put("ecoreFileName", ecoreFileNameTextField.getText());

        prefs.put("packageName", packageNameTextField.getText());
        prefs.put("genJDKLevel", Objects.requireNonNull(genJDKLevelComboBox.getSelectedItem()).toString());
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
        ecoreFileNameTextField.setText(prefs.get("ecoreFileName", "ecore"));

        packageNameTextField.setText(prefs.get("packageName", "org.example"));
        genJDKLevelComboBox.setSelectedItem(GenJDKLevel.get(prefs.get("genJDKLevel",
                GenJDKLevel.JDK210_LITERAL.toString())));
        genModelFileNameTextField.setText(prefs.get("genModelFileName", "genmodel"));

        ecoreGenModelOutputDirFileChooser.setSelectedDirectory(prefs.get("ecoreGenModelOutputDir", "src/main/java" +
                "/resources/"));
        generatedFilesOutputDirFileChooser.setSelectedDirectory(prefs.get("generatedFilesOutputDir", "src/main/java"));
    }

    public CodeGenerationConfig getCodeGenerationConfig() {

        return CodeGenerationConfig.getInstance()
                .setProjectName(projectNameTextField.getText())
                .setProjectNsURI(nsURITextField.getText())
                .setProjectNsPrefix(nsPrefixTextField.getText())
                .setEcoreFileName(ecoreFileNameTextField.getText())
                .setGenModelFileName(genModelFileNameTextField.getText())
                .setBasePackage(packageNameTextField.getText())
                .setGenJDKLevel((GenJDKLevel) genJDKLevelComboBox.getSelectedItem())
                .setEcoreGenModelDir(Path.of(ecoreGenModelOutputDirFileChooser.getSelectedDirectory()))
                .setGeneratedFilesDir(Path.of(generatedFilesOutputDirFileChooser.getSelectedDirectory()))
                .setOutputEcoreFile(Path.of(ecoreGenModelOutputDirFileChooser.getSelectedDirectory() + File.separator + ecoreFileNameTextField.getText()))
                .setOutputGenModelFile(Path.of(ecoreGenModelOutputDirFileChooser.getSelectedDirectory() + File.separator + genModelFileNameTextField.getText()))
                .build();
    }

}
