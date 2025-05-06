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
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Main Class that launches UMLet's {@link MainStandalone#main(String[])}, and adds UMLift-core functionality.
 *
 * @since 1.0
 */
public class UMLiftMainStandalone {
    private static final Logger LOG = Logger.getLogger(UMLiftMainStandalone.class.getName());

    /**
     * The main entry point for the UMLift standalone application. This method initializes
     * the application by starting the UMLet standalone mode and performing any necessary
     * setup processes.
     *
     * @param args Command-line arguments. These parameters are not used in this method.
     */
    public static void main(String[] args) {
        LOG.info("Starting UMLift Standalone...");
        startUMLetStandalone();
    }

    private static void startUMLetStandalone() {
        MainStandalone.main(new String[]{});
        addConfigOptionsJMenuItem();
    }

    /**
     * Adds the configuration options menu item to the application window's menu bar.
     * This method integrates a specific "UMLift" menu, which allows users to access the
     * configuration dialog and other related functionalities, into the application's menu bar.
     * <p>
     * If the menu bar is not initialized, the method logs a severe error message and halts further execution.
     * The "Options..." menu item, when selected, opens a dialog for configuration settings (managed by ConfigDialog).
     * <p>
     * The method ensures the menu bar's appearance is updated after the menu is added.
     * <p>
     * Functionality:
     * - Retrieves the main application JFrame from the CurrentGui instance.
     * - Accesses the JFrame's JMenuBar to append the UMLift menu.
     * - Creates a new ConfigDialog instance for managing user settings.
     * - Constructs the UMLift menu using the `getUMLiftMenu` method, which includes configuration and code
     * generation options.
     * - Updates the UI hierarchy after inserting the menu.
     * <p>
     * Preconditions:
     * - The application's main JFrame must be properly initialized through CurrentGui.
     * - The JMenuBar must be present or successfully initialized before adding the menu.
     * <p>
     * Postconditions:
     * - A new UMLift menu is added to the menu bar as the fourth menu item (index 3).
     * - The JMenuBar's visual representation in the application is refreshed.
     */
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

    /**
     * Creates and returns the "UMLift" menu for the application. This menu includes
     * options for generating code from diagrams and configuring application settings
     * via a dialog.
     *
     * @param configDialog The configuration dialog to be displayed when the "Options..."
     *                     menu item is selected.
     * @return A JMenu object representing the "UMLift" menu with its associated menu items.
     */
    private static JMenu getUMLiftMenu(ConfigDialog configDialog) {
        JMenu generateMenu = new JMenu("UMLift");
        generateMenu.setToolTipText("Generate Code from Diagram");

        JMenuItem generateCodeItem = new JMenuItem("Generate Code");
        generateCodeItem.setMnemonic('G');
        generateCodeItem.addActionListener(e -> showProgressDialog());
        generateMenu.add(generateCodeItem);

        JMenuItem configOptionsMenuItem = new JMenuItem("Options...");
        configOptionsMenuItem.addActionListener(e -> configDialog.setVisible(true));
        generateMenu.add(configOptionsMenuItem);
        return generateMenu;
    }

    /**
     * Displays a progress dialog to the user while a background task, namely code generation,
     * is performed. This method shows a modal dialog with real-time output from the background
     * process redirected to a text area.
     * <p>
     * The progress dialog includes:
     * - A non-editable text area to display logs or output messages.
     * - A scroll pane that encloses the text area for comfortable viewing of longer outputs.
     * <p>
     * Functionality:
     * - Initializes and configures a modal JDialog.
     * - Redirects `System.out` to display messages in the dialog's text area.
     * - Launches a `SwingWorker` to execute the `startCodeGeneration` method in the background.
     * - Updates the dialog UI in real time with logs from the background process.
     * - Disposes of the dialog once the task is completed and restores the original `System.out`.
     * <p>
     * Preconditions:
     * - The application's main frame, obtained from `CurrentGui`, must be correctly initialized.
     * <p>
     * Postconditions:
     * - A progress dialog is displayed during code generation.
     * - The dialog automatically closes once the background task is complete.
     * - The original `System.out` stream is restored.
     * <p>
     * Exceptions: None thrown directly, but errors during code generation could propagate to
     * the logging output in the dialog.
     */
    private static void showProgressDialog() {
        JDialog progressDialog = new JDialog(CurrentGui.getInstance().getGui().getMainFrame(),
                "Generating Code", true);
        JTextArea outputArea = new JTextArea(20, 50);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        progressDialog.add(scrollPane, BorderLayout.CENTER);
        progressDialog.pack();
        progressDialog.setLocationRelativeTo(CurrentGui.getInstance().getGui().getMainFrame());

        // Redirect System.out to outputArea
        PrintStream printStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                SwingUtilities.invokeLater(() -> {
                    outputArea.append(String.valueOf((char) b));
                    outputArea.setCaretPosition(outputArea.getDocument().getLength());
                });
            }
        });

        PrintStream originalOut = System.out;
        System.setOut(printStream);

        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                startCodeGeneration();
                return null;
            }

            @Override
            protected void done() {
                System.setOut(originalOut);
                progressDialog.dispose();
            }
        };

        worker.execute();
        progressDialog.setVisible(true);
    }

    /**
     * Initiates the code generation process from a UMLet file. This method performs the following actions:
     * <p>
     * - Retrieves the file path of the current UMLet diagram using the application's GUI system.
     * - Configures the input UMLet file path in the generation configuration.
     * - Invokes the `ModelToCodeGenerator` to generate code based on the specified UMLet file.
     * - Creates a Maven `pom.xml` file using the `PomGenerator` based on the current project configuration.
     * - Opens the project directory in the system's default file explorer after generation is complete.
     * <p>
     * In case of an exception during the code generation or file operations (e.g., I/O errors),
     * an error message dialog is displayed to inform the user.
     * <p>
     * Preconditions:
     * - The current diagram file path must be retrievable through the application's GUI structure.
     * - The configuration for code generation (e.g., base package, project name, and directory structure)
     * must be properly initialized beforehand.
     * <p>
     * Postconditions:
     * - Code files and a Maven `pom.xml` file are generated in the configured project directory.
     * - The project directory is opened in the system's default file explorer if the process completes successfully.
     * - An error dialog is displayed if an exception occurs during the process.
     */
    private static void startCodeGeneration() {
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

            openDesktop(GenerationConfig.getInstance().getProjectRootDir().toFile());
        } catch (IOException e) {
            displayErrorDialog("Code Generation Error", "An error occurred while generating code from UMLet file.");
        }
    }

    private static void displayErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(CurrentGui.getInstance().getGui().getMainFrame(), message, title,
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Opens the specified directory in the system's default file explorer if the desktop
     * environment supports opening files and directories. If the directory does not exist
     * or is not a valid folder, the method will exit without performing any action.
     *
     * @param projectDirFile The directory to be opened. This must exist and be a valid folder
     *                       for the operation to proceed.
     */
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
