package cz.cuni.mff.imdd.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * JPanel providing Directory Selecting functionality. Used in {@link ConfigDialog}.
 *
 * @since 1.0
 */
public class DirectoryChooserPanel extends JPanel {
    private final JTextField directoryTextField;

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
