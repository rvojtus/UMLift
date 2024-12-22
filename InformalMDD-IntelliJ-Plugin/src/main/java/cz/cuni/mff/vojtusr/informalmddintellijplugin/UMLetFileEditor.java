package cz.cuni.mff.vojtusr.informalmddintellijplugin;

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
import com.intellij.ide.ui.LafManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;
import cz.cuni.mff.vojtusr.informalmddintellijplugin.GUI.UMLetIntelliJGUI;
import cz.cuni.mff.vojtusr.informalmddintellijplugin.GUI.UMLetIntelliJPluginGUIBuilder;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.diagnostic.Logger;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;
import java.io.File;

public class UMLetFileEditor extends UserDataHolderBase implements FileEditor {
    private static final Logger LOG = Logger.getInstance(UMLetFileEditor.class);

    private JBPanel<?> embeddedPanel;

    private final Project myProject;
    private final VirtualFile virtualFile;
    private final File openedFile;
    private final Document myDocument;

    private DiagramHandler handler;

    private final UMLetIntelliJPluginGUIBuilder guiComponents = new UMLetIntelliJPluginGUIBuilder();

    public UMLetFileEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        myProject = project;
        this.virtualFile = virtualFile;
        myDocument = FileDocumentManager.getInstance().getDocument(this.virtualFile);
        openedFile = new File(virtualFile.getPath());

        LOG.info("Initializing UMLet for file: " + this.virtualFile.getPath());

        startUMLet();

        LOG.info("UMLet successfully initialized!");
    }

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

    private void createControl() {
        getGui().setCurrentEditor(this);
        handler = new DiagramHandler(openedFile);
        getGui().registerEditorForDiagramHandler(this, handler);
        getGui().setCurrentDiagramHandler(handler);
        open(handler);
    }

    private void readBuildInfoAndInitVersion() {
        Utils.BuildInfo buildInfo = Utils.readBuildInfo();
        Program.init(buildInfo.version, RuntimeType.STANDALONE);
    }

    private void initAll() {
        readBuildInfoAndInitVersion();
        initHomeProgramPath();
        ConfigHandler.loadConfig();
    }

    private void initHomeProgramPath() {
        String tempPath, realPath;
        tempPath = Path.executable();
        tempPath = tempPath.substring(0, tempPath.length() - 1);
        tempPath = tempPath.substring(0, tempPath.lastIndexOf('/') + 1);
        if (tempPath.endsWith("/lib/")) {
            tempPath = tempPath.substring(0, tempPath.length() - "lib/".length());
        }
        realPath = new File(tempPath).getAbsolutePath() + "/";
        Path.setHomeProgram(realPath);
    }

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

    private void refreshPalette() {
        if (guiComponents.getPalettePanel().getComponentCount() == 0) {
            for (PaletteHandler paletteHandler : Main.getInstance().getPalettes().values()) {
                guiComponents.getPalettePanel().add(paletteHandler.getDrawPanel().getScrollPane(), paletteHandler.getName());
                paletteHandler.getDrawPanel().getScrollPane().invalidate();
            }
        }
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
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
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

//    @Override
//    public StructureViewBuilder getStructureViewBuilder() {
//        VirtualFile file = FileDocumentManager.getInstance().getFile(myDocument);
//        if (file == null || !file.isValid()) return null;
//        return StructureViewBuilder.PROVIDER.getStructureViewBuilder(file.getFileType(), file, myProject);
//    }

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

    public void dirtyChanged(){

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

}
