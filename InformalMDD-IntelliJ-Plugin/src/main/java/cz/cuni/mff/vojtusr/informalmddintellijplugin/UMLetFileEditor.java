package cz.cuni.mff.vojtusr.informalmddintellijplugin;

import com.baselet.control.Main;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import cz.cuni.mff.vojtusr.gui.GUI;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.diagnostic.Logger;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;

public class UMLetFileEditor extends UserDataHolderBase implements FileEditor {
    private static final Logger LOG = Logger.getInstance(UMLetFileEditor.class);

    private final JPanel myPanel;

    private final Project myProject;
    private final VirtualFile myFile;
    private final Document myDocument;

    public UMLetFileEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        myProject = project;
        myFile = virtualFile;
        myDocument = FileDocumentManager.getInstance().getDocument(myFile);

        LOG.info("Initializing UMLet for file: " + myFile.getPath());

        JFrame UMLetMainFrame = GUI.getUMLetMainFrame();
        Main.getInstance().doOpen(myFile.getPath());
        myPanel = convert(UMLetMainFrame);
        UMLetMainFrame.getContentPane().removeAll();
        UMLetMainFrame.dispose();

        LOG.info("UMLet successfully initialized!");
    }

    private JPanel convert(JFrame frame) {
        JPanel panel = new JPanel();
        panel.setLayout(frame.getContentPane().getLayout());

        for (Component component : frame.getContentPane().getComponents()) {
            frame.getContentPane().remove(component);
            panel.add(component);
        }

        return panel;
    }

    @Override
    public @NotNull JComponent getComponent() {
        return myPanel;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return myPanel;
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
        LOG.info("Disposing UMLetFileEditor for file: " + myFile.getPath());
        if (myPanel != null) {
            //Main.getInstance().closeProgram();
            myPanel.removeAll();
        }

    }

    @Override
    public @Nullable VirtualFile getFile() {
        return myFile;
    }
}
