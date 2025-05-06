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
 * The CopyAction class provides functionality for copying content within the context of
 * UMLet diagrams in an IntelliJ-based IDE plugin. It extends the AnAction class to
 * integrate custom behavior when the action is invoked or updated.
 * <p>
 * This class ensures that the copy action is only enabled and visible in the IDE
 * if the currently selected file is an UMLet-compatible file, identified by its
 * specific file extension.
 * <p>
 * Overrides the following methods:
 * <p>
 * actionPerformed - Executes the copy action triggered by the user. If the current
 * project or file is invalid, the action is not performed.
 * <p>
 * update          - Updates the presentation layer of the action (e.g., enabling
 * or disabling the action) based on the selected file's properties.
 * <p>
 * getActionUpdateThread - Specifies the thread context in which the update and
 * corresponding action logic should execute. This is set
 * to run in the background thread.
 */
public class CopyAction extends AnAction {

    /**
     * Executes the copy action when triggered by the user. This method first verifies
     * the validity of the current project and the selected file. If the file exists
     * and has the appropriate UMLet-compatible extension, the copy action is performed.
     * Otherwise, the action is disabled.
     *
     * @param event the event containing details about the action invocation, including
     *              the current project, selected file, and presentation updates
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);
        if (file != null && Objects.equals(file.getExtension(), UMLET_FILE_SUFFIX)) {
            MenuFactoryIntelliJ.getInstance().doAction(MenuConstants.COPY, null);
        } else {
            event.getPresentation().setEnabled(false);
        }
    }

    /**
     * Updates the presentation layer of the action. This method determines whether the action
     * should be enabled and visible based on the currently selected file in the IDE.
     * The action becomes enabled and visible only if the selected file is an UMLet-compatible file,
     * identified by the file extension.
     *
     * @param event the event containing context information, such as the currently selected
     *              file and the action's presentation details
     */
    @Override
    public void update(@NotNull AnActionEvent event) {
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);

        boolean isUxfFile = file != null && (UMLET_FILE_SUFFIX.equalsIgnoreCase(file.getExtension()));
        event.getPresentation().setEnabledAndVisible(isUxfFile);
    }

    /**
     * Specifies the thread used for updating the action's presentation and performing other updates.
     * Determines the preferred context for execution to ensure responsive UI updates.
     *
     * @return the thread type that should be used for action updates, which is the background thread (BGT) in this case
     */
    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
