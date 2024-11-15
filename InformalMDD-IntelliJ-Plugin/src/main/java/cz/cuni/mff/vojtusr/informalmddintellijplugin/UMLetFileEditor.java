package cz.cuni.mff.vojtusr.informalmddintellijplugin;

import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;

public class UMLetFileEditor extends UserDataHolderBase implements FileEditor {
    private final JPanel myPanel;

    private final Project myProject;
    private final VirtualFile myFile;
    //private final Document myDocument;

    public UMLetFileEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        myPanel = new JPanel();
        myPanel.setLayout(new BorderLayout());
        myPanel.add(new JLabel("Editor for: " + virtualFile.getName()), BorderLayout.CENTER);
        //myPanel.add(new JLabel("Path: " + virtualFile.getPath()));
        myProject = project;
        myFile = virtualFile;
//        myDocument = FileDocumentManager.getInstance().getDocument(myFile);
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

    }

    @Override
    public @Nullable VirtualFile getFile() {
        return myFile;
    }
}
