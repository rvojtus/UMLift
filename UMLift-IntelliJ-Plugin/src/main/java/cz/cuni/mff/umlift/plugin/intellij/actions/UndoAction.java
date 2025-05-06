package cz.cuni.mff.umlift.plugin.intellij.actions;

import com.baselet.control.constants.MenuConstants;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import cz.cuni.mff.umlift.plugin.intellij.GUI.MenuFactoryIntelliJ;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static cz.cuni.mff.umlift.plugin.intellij.settings.EMFSettings.UMLET_FILE_SUFFIX;

/**
 * The UndoAction class represents an action for performing the undo operation
 * in the context of UMLet diagrams within an IntelliJ-based IDE plugin. It extends
 * the AnAction class to integrate custom behavior when the action is triggered or updated.
 * <p>
 * This class ensures that the undo action is only enabled and visible when the current
 * selection in the IDE is an UMLet-compatible file identified by its specific file extension.
 * <p>
 * Overrides the following methods:
 * <p>
 * actionPerformed - Executes the undo operation when the action is triggered by the user.
 * If the current project or selected file is invalid, the action is not performed.
 * <p>
 * update - Updates the visibility and enablement state of the action
 * based on the properties of the currently selected file.
 * <p>
 * getActionUpdateThread - Specifies the thread context in which the action
 * logic and updates should execute. This is set to run in the background thread.
 */
public class UndoAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);
        if (file != null && Objects.equals(file.getExtension(), UMLET_FILE_SUFFIX)) {
            MenuFactoryIntelliJ.getInstance().doAction(MenuConstants.UNDO, null);
        } else {
            event.getPresentation().setEnabled(false);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);

        boolean isUxfFile = file != null && (UMLET_FILE_SUFFIX.equalsIgnoreCase(file.getExtension()));
        event.getPresentation().setEnabledAndVisible(isUxfFile);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
