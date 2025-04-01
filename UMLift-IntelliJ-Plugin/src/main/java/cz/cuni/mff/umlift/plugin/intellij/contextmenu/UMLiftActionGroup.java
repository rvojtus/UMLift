package cz.cuni.mff.umlift.plugin.intellij.contextmenu;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * A dynamic action group that provides code generation related actions in the context menu.
 *
 * <p>
 * This action group appears in the right-click menu of the Project View when a file is selected.
 * Supported file type extensions: ".uxf", ".ecore" and ".genmodel".
 * </p>
 *
 * @see DefaultActionGroup
 * @see EMF2CodeContextMenuAction
 * @see UMLetToEMFContextMenuAction
 * @see UMLet2CodeContextMenuAction
 */
public class UMLiftActionGroup extends DefaultActionGroup {
    private final static String[] supportedExtensions = {"uxf", "ecore", "genmodel"};

    @Override
    public void update(@NotNull AnActionEvent event) {
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);

        boolean isSupportedFile =
                file != null && Arrays.stream(supportedExtensions).anyMatch(x -> x.equalsIgnoreCase(file.getExtension()));
        event.getPresentation().setEnabledAndVisible(isSupportedFile);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
