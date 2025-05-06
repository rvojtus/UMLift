package cz.cuni.mff.umlift.standalone.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * A JPanel implementation that provides a GUI component for directory selection.
 * This panel includes a non-editable text field displaying the selected directory
 * and a button for opening a directory chooser dialog.
 */
public class DirectoryChooserPanel extends JPanel {
    private final JTextField directoryTextField;

    /**
     * Constructs a DirectoryChooserPanel with the specified initial directory text.
     * This panel contains a non-editable text field to display the directory path
     * and a button to open a directory chooser dialog for directory selection.
     *
     * @param directoryText the initial text to display in the directory text field
     */
    public DirectoryChooserPanel(String directoryText) {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder());

        directoryTextField = new JTextField(directoryText);
        directoryTextField.setEditable(false);

        JButton browseButton = new JButton(UIManager.getIcon("FileView.directoryIcon"));
        browseButton.setPreferredSize(new Dimension(30, 30));
        browseButton.setToolTipText("Select Directory");
        browseButton.addActionListener(this::chooseDirectory);

        add(directoryTextField, BorderLayout.CENTER);
        add(browseButton, BorderLayout.EAST);
    }

    /**
     * Opens a directory chooser dialog that allows the user to select a directory.
     * The selected directory's path is displayed in the directory text field.
     *
     * @param event the ActionEvent triggered by the user clicking the directory selection button
     */
    private void chooseDirectory(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Select a directory");

        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            directoryTextField.setText(selectedFile.getAbsolutePath());
        }
    }

    public String getSelectedDirectory() {
        return directoryTextField.getText();
    }

    public void setSelectedDirectory(String directory) {
        directoryTextField.setText(directory);
    }
}
