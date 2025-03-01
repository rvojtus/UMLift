package cz.cuni.mff.umlift.plugin.intellij.editor;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * A custom file editor provider for {@link UMLetFileEditor}. Supports ".uxf" files.
 *
 * <p>
 * This editor provides a specialized UI for editing UMLet files within IntelliJ IDEA.
 * It replaces the default text editor with a custom visual editor {@link UMLetFileEditor}.
 * </p>
 *
 * @see FileEditorProvider
 * @see DumbAware
 * @see UMLetFileEditor
 * @since 1.0
 */
public class UMLetFileEditorProvider implements FileEditorProvider, DumbAware {
    private static final Logger LOG = Logger.getInstance(UMLetFileEditorProvider.class);

    /**
     * Checks if this provider accepts the given file
     *
     * @param project     the current project context
     * @param virtualFile the file being opened
     * @return {@code true} if this provider handles the file, {@code false} otherwise
     */
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return "uxf".equalsIgnoreCase(virtualFile.getExtension());
    }

    /**
     * Creates an editor for the given file.
     *
     * @param project     the current project context
     * @param virtualFile the file being opened
     * @return a new {@link UMLetFileEditor} instance
     */
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
