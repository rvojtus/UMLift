package cz.cuni.mff.umlift.plugin.intellij.contextmenu;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * A dynamic action group that provides code and artifact generation actions in a context menu.
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

    /**
     * Updates the state of the action's presentation based on the currently selected file
     * in the IntelliJ editor or project view. The action is enabled and visible only if
     * the selected file is not null and has a supported file extension.
     *
     * @param event the action event triggered in the IntelliJ environment, containing the
     *              context of the invocation including information about the selected file.
     */
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
