package cz.cuni.mff.vojtusr.informalmddintellijplugin.editor;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class UMLetFileEditorProvider implements FileEditorProvider, DumbAware {
    private static final Logger LOG = Logger.getInstance(UMLetToolWindow.class);

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return "uxf".equalsIgnoreCase(virtualFile.getExtension());
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return new UMLetFileEditor(project, virtualFile);
    }

    @Override
    public @NotNull @NonNls String getEditorTypeId() {
        return "uxf-editor";
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
    }
}
