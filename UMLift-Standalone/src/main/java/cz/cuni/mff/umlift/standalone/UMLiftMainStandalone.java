package cz.cuni.mff.umlift.standalone;

import com.baselet.gui.CurrentGui;
import com.baselet.standalone.MainStandalone;
import cz.cuni.mff.umlift.core.pom.MavenPackaging;
import cz.cuni.mff.umlift.core.pom.PomConfiguration;
import cz.cuni.mff.umlift.core.pom.PomGenerator;
import cz.cuni.mff.umlift.standalone.gui.ConfigDialog;
import cz.cuni.mff.umlift.core.config.GenerationConfig;
import cz.cuni.mff.umlift.core.emf.ModelToCodeGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Main Class that launches UMLet's {@link MainStandalone#main(String[])}, and adds UMLift-core functionality.
 *
 * @since 1.0
 */
public class UMLiftMainStandalone {
    private static final Logger LOG = Logger.getLogger(UMLiftMainStandalone.class.getName());

    public static void main(String[] args) {
        LOG.info("Starting UMLift Standalone...");
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
        JMenu generateMenu = getUMLiftMenu(configDialog);

        menuBar.add(generateMenu, 3);
        SwingUtilities.updateComponentTreeUI(mainFrame);
    }

    private static JMenu getUMLiftMenu(ConfigDialog configDialog) {
        JMenu generateMenu = new JMenu("UMLift");
        generateMenu.setToolTipText("Generate Code from Diagram");

        JMenuItem generateCodeItem = new JMenuItem("Generate Code");
        generateCodeItem.setMnemonic('G');
        generateCodeItem.addActionListener(UMLiftMainStandalone::startCodeGeneration);
        generateMenu.add(generateCodeItem);

        JMenuItem configOptionsMenuItem = new JMenuItem("Options...");
        configOptionsMenuItem.addActionListener(e -> configDialog.setVisible(true));
        generateMenu.add(configOptionsMenuItem);
        return generateMenu;
    }

    private static void startCodeGeneration(ActionEvent event) {
        ModelToCodeGenerator generator = new ModelToCodeGenerator();
        String currUMLetFile =
                CurrentGui.getInstance().getGui().getCurrentDiagram().getHandler().getFileHandler().getFullPathName();
        final Path inputUMLetFilePath = Path.of(currUMLetFile);
        GenerationConfig.getInstance().setInputUMLetFile(inputUMLetFilePath);

        try {
            generator.generateCodeFromUMLetFile(inputUMLetFilePath);
            PomConfiguration pomConfig = new PomConfiguration("4.0.0",
                    GenerationConfig.getInstance().getBasePackage(),
                    GenerationConfig.getInstance().getProjectName(),
                    "1.0.0", MavenPackaging.POM, GenerationConfig.getInstance().getGenJDKLevel());
            PomGenerator pomGenerator = new PomGenerator(pomConfig);
            File pomFile =
                    new File(GenerationConfig.getInstance().getProjectRootDir().toString() + File.separator +
                            "pom.xml");
            pomGenerator.createAndSavePom(pomFile);

            openDesktop(GenerationConfig.getInstance().getGeneratedFilesDir().toFile());
        } catch (IOException e) {
            displayErrorDialog("Code Generation Error", "An error occurred while generating code from UMLet file.");
        }
    }

    private static void displayErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(CurrentGui.getInstance().getGui().getMainFrame(), message, title,
                JOptionPane.ERROR_MESSAGE);
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
