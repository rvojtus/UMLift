package cz.cuni.mff.umlift.plugin.intellij.editor;

import com.baselet.control.Main;
import com.baselet.control.config.Config;
import com.baselet.control.config.handler.ConfigHandler;
import com.baselet.control.enums.Program;
import com.baselet.control.enums.RuntimeType;
import com.baselet.control.util.Path;
import com.baselet.control.util.Utils;
import com.baselet.diagram.DiagramHandler;
import com.baselet.diagram.DrawPanel;
import com.baselet.diagram.PaletteHandler;
import com.baselet.element.old.custom.CustomElementHandler;
import com.baselet.gui.CurrentGui;
import com.baselet.gui.pane.OwnSyntaxPane;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.ide.ui.LafManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.PathUtil;
import com.intellij.openapi.diagnostic.Logger;
import cz.cuni.mff.umlift.plugin.intellij.GUI.UMLetIntelliJGUI;
import cz.cuni.mff.umlift.plugin.intellij.GUI.UMLetIntelliJPluginGUIBuilder;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.jar.JarFile;

/**
 * A custom editor for UMLet files in the IntelliJ IDEA environment.
 *
 * <p>The {@code UMLetFileEditor} class implements the {@link FileEditor} interface to provide
 * a specialized editor for UMLet `.uxf` files within IntelliJ IDEA. It leverages the IntelliJ
 * Platform's APIs for file and project management and integrates UMLet's diagram handling
 * capabilities.</p>
 *
 * <p>Key Features:
 * <ul>
 *   <li>Embeds a custom panel for rendering and editing UML diagrams.</li>
 *   <li>Handles IntelliJ project and file interactions via {@link Project} and {@link VirtualFile}.</li>
 *   <li>Provides integration with UMLet's {@link DiagramHandler} for processing UML diagrams.</li>
 *   <li>Utilizes the {@link UMLetIntelliJPluginGUIBuilder} for constructing the editor's GUI components.</li>
 * </ul>
 * </p>
 *
 * @see FileEditor
 * @see UMLetIntelliJPluginGUIBuilder
 * @see DiagramHandler
 * @since 1.0
 */
public class UMLetFileEditor extends UserDataHolderBase implements FileEditor {
    private static final String PLUGIN_ID = "cz.cuni.mff.umlift.plugin.intellij";
    private static final Logger LOG = Logger.getInstance(UMLetFileEditor.class);

    private JBPanel<?> embeddedPanel;

    private final Project myProject;
    private final VirtualFile virtualFile;
    private final File openedFile;
    private final Document myDocument;

    private DiagramHandler handler;

    private final UMLetIntelliJPluginGUIBuilder guiComponents = new UMLetIntelliJPluginGUIBuilder();

    /**
     * Constructs a new instance of {@code UMLetFileEditor}.
     *
     * <p>This constructor initializes the editor for a specific UMLet file in the IntelliJ IDEA environment.
     * It sets up the necessary project, file, and document references, and initializes UMLet for
     * the specified file.</p>
     *
     * <p>Key Actions:
     * <ul>
     *   <li>Associates the editor with the provided IntelliJ {@link Project} and {@link VirtualFile}.</li>
     *   <li>Retrieves the corresponding {@link Document} for the given file via {@link FileDocumentManager}.</li>
     *   <li>Initializes UMLet and its diagram handling functionality.</li>
     *   <li>Logs the initialization process for debugging and tracking purposes.</li>
     * </ul>
     * </p>
     *
     * @param project     the IntelliJ {@link Project}
     * @param virtualFile the {@link VirtualFile} representing the UMLet file to be edited
     */
    public UMLetFileEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        myProject = project;
        this.virtualFile = virtualFile;
        myDocument = FileDocumentManager.getInstance().getDocument(this.virtualFile);
        openedFile = new File(virtualFile.getPath());

        LOG.info("Initializing UMLet for file: " + this.virtualFile.getPath());

        startUMLet();

        LOG.info("UMLet successfully initialized!");
    }

    /**
     * Initializes the UMLet environment and sets up the editor's graphical interface.
     *
     * <p>This method performs the following key actions:
     * <ul>
     *   <li>Initializes all necessary UMLet components via {@code initAll()}.</li>
     *   <li>Configures the look-and-feel (LAF) settings for UMLet using the current IntelliJ UI theme.</li>
     *   <li>Initializes UMLet's main instance with a custom GUI tailored for IntelliJ IDEA using
     *       {@link UMLetIntelliJGUI}.</li>
     *   <li>Constructs the embedded panel for the editor's graphical interface using
     *       {@link UMLetIntelliJPluginGUIBuilder}.</li>
     *   <li>Attaches focus listeners to the embedded panel to handle focus gain and loss events.</li>
     * </ul>
     * </p>
     *
     * <p>The {@code embeddedPanel} serves as the primary container for the UMLet editor within IntelliJ IDEA,
     * allowing users to interact with UML diagrams.</p>
     */
    private void startUMLet() {
        initAll();
        Config.getInstance().setUiManager(LafManager.getInstance().getCurrentUIThemeLookAndFeel().toString());
        Main.getInstance().init(new UMLetIntelliJGUI(Main.getInstance()));
        embeddedPanel = guiComponents.buildGUI();
        createControl();

        embeddedPanel.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                refocus();
            }

            @Override
            public void focusLost(FocusEvent e) {
                // Handle focus lost logic here
            }
        });
    }

    /**
     * Sets up the control mechanisms for managing the UMLet diagram and editor state.
     *
     * <p>This method links the editor to the UMLet backend by performing the following steps:
     * <ul>
     *   <li>Associates this editor instance with the UMLet GUI using {@code setCurrentEditor()}.</li>
     *   <li>Initializes a {@link DiagramHandler} for the currently opened UMLet file.</li>
     *   <li>Registers this editor with the UMLet GUI to manage interactions with the {@link DiagramHandler}.</li>
     *   <li>Sets the initialized {@link DiagramHandler} as the current diagram handler in the UMLet GUI.</li>
     *   <li>Opens the UMLet diagram using the initialized handler.</li>
     * </ul>
     * </p>
     *
     * <p>This method ensures that the editor and UMLet GUI are synchronized, enabling seamless
     * interaction with the UML diagram being edited.</p>
     */
    private void createControl() {
        getGui().setCurrentEditor(this);
        try {
            if (Files.size(openedFile.toPath()) == 0) {
                initEmptyUXF();
            }
        } catch (IOException e) {
            LOG.error("Could not open file: " + openedFile.getPath(), e);
        }

        handler = new DiagramHandler(openedFile);
        getGui().registerEditorForDiagramHandler(this, handler);
        getGui().setCurrentDiagramHandler(handler);
        open(handler);
    }

    /**
     * Reads the build information and initializes the program version.
     *
     * <p>This method reads the build information using {@link Utils#readBuildInfo()} and initializes
     * the program with the specified version from the build information. The initialization sets the
     * runtime type to {@link RuntimeType#ECLIPSE_PLUGIN}. It is done due to the inability set the config file. The
     * {@link RuntimeType#STANDALONE} uses the same config file, so there would be a conflict between these
     * configurations. If user were to use the standalone version and this plugin, without removing the config file,
     * the same configuration parameters would be used, which can result in weird glitches, inconsistencies etc.
     * </p>
     *
     * <p>This process ensures that the program is properly configured with the correct version
     * information at startup.</p>
     */
    private void readBuildInfoAndInitVersion() {
        Utils.BuildInfo buildInfo = Utils.readBuildInfo();
        Program.init(buildInfo.version, RuntimeType.ECLIPSE_PLUGIN); // to have separate config files
    }

    /**
     * Initializes all necessary components and configurations for the program.
     *
     * <p>This method performs a series of initialization tasks to set up the environment:
     * <ul>
     *   <li>Reads the build information and initializes the program version via
     *   {@link #readBuildInfoAndInitVersion()}.</li>
     *   <li>Initializes the program's home directory path through {@link #initHomeProgramPath()}.</li>
     *   <li>Loads the configuration settings using {@link ConfigHandler#loadConfig()}.</li>
     * </ul>
     * </p>
     *
     * <p>The method ensures that all essential program components are properly initialized before the program starts
     * .</p>
     */
    private void initAll() {
        readBuildInfoAndInitVersion();
        initHomeProgramPath();
        ConfigHandler.loadConfig();
    }

    /**
     * Initializes the home program path and extracts palette files.
     *
     * <p>This method determines the home program path by calculating the path to the parent directory
     * of the JAR file. It then sets the program's home directory using the calculated path and extracts
     * the palette files into the home program directory.</p>
     *
     * <p>By calling this method, the program's home directory is initialized, and palette files are extracted
     * if they have not been extracted already.</p>
     */
    private void initHomeProgramPath() {
        String jarPath = PathUtil.getJarPathForClass(Path.class);
        String libPath = PathUtil.getParentPath(jarPath);
        String homeProgramPath = PathUtil.getParentPath(libPath) + "/";

        Path.setHomeProgram(homeProgramPath);
        extractPalettes();
    }

    /**
     * Extracts palette files from the UMLift-core's dependency JAR file
     * and saves them to this plugin's top directory.
     *
     * <p>This method checks if the palette files have already been extracted to the home program directory.
     * If they haven't been extracted, the method extracts them from the JAR file where they are stored in
     * a directory named {@code "palettes/"} and saves them to a corresponding directory in the home program
     * directory.</p>
     *
     * <p>If any I/O errors occur during extraction, they are logged. If the palette directory already exists
     * or if the extraction fails, the method gracefully handles these scenarios without interrupting the program.</p>
     */
    private void extractPalettes() {
        String jarPath = PathUtil.getJarPathForClass(Path.class);
        String palettesPath = "palettes/";
        String homeProgramPalettesPath = Path.homeProgram() + "palettes/";
        if (Files.isDirectory(Paths.get(homeProgramPalettesPath))) {
            return;// palettes already extracted
        }

        try (JarFile jarFile = new JarFile(jarPath)) {
            File palettesDir = new File(homeProgramPalettesPath);
            boolean dirCreated = palettesDir.mkdir();
            if (!dirCreated) {
                return;
            }
            jarFile.stream()
                    .filter(entry -> entry.getName().startsWith(palettesPath))
                    .forEach(entry -> {
                        String relativePath = entry.getName().substring(palettesPath.length());
                        File outputFile = new File(homeProgramPalettesPath, relativePath);

                        try (InputStream inputStream = jarFile.getInputStream(entry);
                             OutputStream outputStream = new FileOutputStream(outputFile)) {
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                        } catch (IOException ignored) {
                        }
                    });
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }

    public static URL getURL() {
        try {
            IdeaPluginDescriptor pluginDescriptor = PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID));
            if (pluginDescriptor != null) {
                File pluginPath = pluginDescriptor.getPluginPath().toFile();
                return pluginPath.toURI().toURL();
            } else {
                LOG.warn("Plugin ID: " + PLUGIN_ID + " not found!");
                return null;
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Refocuses the UMLet editor, updating the current editor and diagram handler,
     * refreshing the palette, and updating the UI components.
     *
     * <p>This method ensures that the editor's UI is synchronized with the current state.</p>
     */
    private void refocus() {
        getGui().setCurrentEditor(this);
        getGui().setCurrentDiagramHandler(handler);
        if (handler != null) {
            handler.getDrawPanel().getSelector().updateSelectorInformation();
        }
        refreshPalette();
        showPalette(getSelectedPaletteName());
        getGui().setValueOfZoomDisplay(handler.getGridSize());
        guiComponents.getPropertyTextPane().invalidate();
    }

    @Override
    public @NotNull JComponent getComponent() {
        return embeddedPanel;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return embeddedPanel;
    }

    /**
     * Refreshes the UMLet palette by adding missing palette components.
     *
     * <p>This method checks if the palette panel is empty. If so, it iterates over the available
     * {@link PaletteHandler}s and adds their corresponding draw panels to the palette. Each draw panel
     * is wrapped in a scroll pane and associated with the palette's name. The scroll pane is then invalidated
     * to ensure proper layout rendering.</p>
     *
     * <p>The method ensures that the palette is updated with all available palette components,
     * making them available for interaction within the UMLet editor.</p>
     */
    private void refreshPalette() {
        if (guiComponents.getPalettePanel().getComponentCount() == 0) {
            for (PaletteHandler paletteHandler : Main.getInstance().getPalettes().values()) {
                guiComponents.getPalettePanel().add(paletteHandler.getDrawPanel().getScrollPane(),
                        paletteHandler.getName());
                paletteHandler.getDrawPanel().getScrollPane().invalidate();
            }
        }
    }

    public void askSave() {
        if (handler == null || openedFile == null) {
            LOG.error("Can't save the file because there is no opened file!");
            return;
        }

        handler.doSave();
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) @NotNull String getName() {
        return "UMLet Editor";
    }

    @Override
    public void setState(@NotNull FileEditorState fileEditorState) {

    }

    @Override
    public boolean isModified() {
        return handler != null && handler.isChanged();
    }

    @Override
    public boolean isValid() {
        return handler != null;
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public @Nullable FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    public void dispose() {
        LOG.info("Disposing UMLetFileEditor for file: " + virtualFile.getPath());
        getGui().editorRemoved(this);
    }

    @Override
    public @Nullable VirtualFile getFile() {
        return virtualFile;
    }

    public CustomElementHandler getCustomElementHandler() {
        return guiComponents.getCustomHandler();
    }

    public DrawPanel getDiagram() {
        if (handler == null) {
            return null;
        }
        return handler.getDrawPanel();
    }

    public void setCursor(Cursor cursor) {
        embeddedPanel.setCursor(cursor);
    }

    public OwnSyntaxPane getPropertyPane() {
        return guiComponents.getPropertyTextPane();
    }

    public JTextComponent getCustomPane() {
        return guiComponents.getCustomPanel().getTextPane();
    }

    public void requestFocus() {
        embeddedPanel.requestFocus();
    }

    public String getSelectedPaletteName() {
        return guiComponents.getPaletteList().getSelectedItem().toString();
    }

    public void open(final DiagramHandler handler) {
        SwingUtilities.invokeLater(() -> guiComponents.setContent(handler.getDrawPanel().getScrollPane()));
    }

    public void showPalette(final String paletteName) {
        guiComponents.setPaletteActive(paletteName);
    }

    public void setCustomPanelEnabled(boolean enable) {
        guiComponents.setCustomPanelEnabled(enable);
        setDrawPanelEnabled(!enable);
    }

    private void setDrawPanelEnabled(boolean enable) {
        handler.getDrawPanel().getScrollPane().setEnabled(enable);
    }

    public void setMailPanelEnabled(boolean enable) {
        guiComponents.setMailPanelEnabled(enable);
    }

    public boolean isMailPanelVisible() {
        return guiComponents.getMailPanel().isVisible();
    }

    public void diagramNameChanged() {

    }

    public void dirtyChanged() {

    }

    public int getMainSplitLocation() {
        return guiComponents.getMainSplit().getDividerLocation();
    }

    public int getRightSplitLocation() {
        return guiComponents.getRightSplit().getDividerLocation();
    }

    public void focusPropertyPane() {
        guiComponents.getPropertyTextPane().getTextComponent().requestFocus();
    }

    public Frame getMainFrame() {
        return null;
    }

    private UMLetIntelliJGUI getGui() {
        return (UMLetIntelliJGUI) CurrentGui.getInstance().getGui();
    }

    private void initEmptyUXF() throws IOException {
        final String content =
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                        "<diagram program=\"umlet\" version=\"15.1\">\n" +
                        "    <zoom_level>10</zoom_level>\n" +
                        "</diagram>\n";

        byte[] bytes = content.getBytes();

        Files.write(java.nio.file.Path.of(openedFile.getPath()), bytes);
    }

}
