package cz.cuni.mff.imdd;

import com.baselet.gui.CurrentGui;
import com.baselet.standalone.MainStandalone;
import cz.cuni.mff.imdd.gui.ConfigDialog;
import cz.cuni.mff.umlift.core.emf.CodeGenerationConfig;
import cz.cuni.mff.umlift.core.emf.ModelToCodeGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Main Class that launches UMLet's {@link MainStandalone#main(String[])}, and adds IMDD functionality.
 *
 * @since 1.0
 */
public class IMDDMainStandalone {
    private static final Logger LOG = Logger.getLogger(IMDDMainStandalone.class.getName());

    public static void main(String[] args) {
        LOG.info("Starting IMDDMainStandalone...");
        startUMLetStandalone();
    }

    private static void startUMLetStandalone() {
        MainStandalone.main(new String[]{});
        addConfigOptionsJMenuItem();
    }

    private static void addConfigOptionsJMenuItem() {
        JFrame mainFrame = (JFrame) CurrentGui.getInstance().getGui().getMainFrame();
        JMenuBar menuBar = mainFrame.getJMenuBar();
        if (menuBar == null) {
            LOG.severe("MenuBar is null");
            return;
        }

        ConfigDialog configDialog = new ConfigDialog(mainFrame);
        JMenu generateMenu = getIMDDMenu(configDialog);

        menuBar.add(generateMenu, 3);
        SwingUtilities.updateComponentTreeUI(mainFrame);
    }

    private static JMenu getIMDDMenu(ConfigDialog configDialog) {
        JMenu generateMenu = new JMenu("IMDD");
        generateMenu.setToolTipText("Generate Code from Diagram");

        JMenuItem generateCodeItem = new JMenuItem("Generate Code");
        generateCodeItem.setMnemonic('G');
        generateCodeItem.addActionListener(e -> startCodeGeneration(e, configDialog.getCodeGenerationConfig()));
        generateMenu.add(generateCodeItem);

        JMenuItem configOptionsMenuItem = new JMenuItem("EMF Options...");
        configOptionsMenuItem.addActionListener(_ -> configDialog.setVisible(true));
        generateMenu.add(configOptionsMenuItem);
        return generateMenu;
    }

    private static void startCodeGeneration(ActionEvent event, CodeGenerationConfig config) {
        ModelToCodeGenerator generator = new ModelToCodeGenerator();
        String currUMLetFile = CurrentGui.getInstance().getGui().getCurrentDiagram().getHandler().getFileHandler().getFullPathName();
        config.setInputUMLetFile(Path.of(currUMLetFile));
        // todo maybe make the output dir relative to the dir of the umlet file
        try {
            generator.generateCodeFromUMLetFile(config);
            openDesktop(config.getGeneratedFilesDir().toFile());
        } catch (IOException e) {
            displayErrorDialog("Code Generation Error", "An error occurred while generating code from UMLet file.");
        }
    }

    private static void displayErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(CurrentGui.getInstance().getGui().getMainFrame(), message, title, JOptionPane.ERROR_MESSAGE);
    }

    private static void openDesktop(File projectDirFile) {
        if (!projectDirFile.exists() || !projectDirFile.isDirectory()) {
            return;
        }
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(projectDirFile);
            } catch (IOException e) {
                displayErrorDialog("Open Desktop Error", "An error occurred while opening Desktop.");
            }
        }
    }
}
