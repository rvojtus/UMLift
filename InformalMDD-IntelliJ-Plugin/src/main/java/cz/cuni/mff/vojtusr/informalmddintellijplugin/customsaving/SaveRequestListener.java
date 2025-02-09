package cz.cuni.mff.vojtusr.informalmddintellijplugin.customsaving;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import cz.cuni.mff.vojtusr.informalmddintellijplugin.editor.UMLetFileEditor;
import org.jetbrains.annotations.NotNull;

@Service
public final class SaveRequestListener implements AnActionListener {
    @Override
    public void beforeActionPerformed(@NotNull AnAction action, @NotNull AnActionEvent event) {
        if ("SaveAll".equals(ActionManager.getInstance().getId(action))) {
            Project project = event.getProject();
            if (project == null) {
                return;
            }
            VirtualFile selectedFile = getActiveFile(project);
            if (selectedFile != null && "uxf".equalsIgnoreCase(selectedFile.getExtension())) {
                FileEditor editor = getFileEditor(project, selectedFile);
                if (editor instanceof UMLetFileEditor fileEditor) {
                    fileEditor.askSave();
                }
            }
        }
    }

    /**
     * Gets the currently active file in the editor.
     */
    private VirtualFile getActiveFile(@NotNull Project project) {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        VirtualFile[] openFiles = fileEditorManager.getSelectedFiles();
        return (openFiles.length > 0) ? openFiles[0] : null;
    }

    /**
     * Gets the `FileEditor` associated with a file.
     */
    private FileEditor getFileEditor(@NotNull Project project, @NotNull VirtualFile file) {
        FileEditorManager manager = FileEditorManager.getInstance(project);
        FileEditor[] editors = manager.getEditors(file);
        return (editors.length > 0) ? editors[0] : null;
    }
}
