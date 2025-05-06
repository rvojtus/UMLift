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
 * The CutAction class provides functionality for cutting content within the context of
 * UMLet diagrams in an IntelliJ-based IDE plugin. It extends the AnAction class to
 * integrate custom behavior when the action is invoked or updated.
 * <p>
 * This class ensures that the cut action is only enabled and visible in the IDE
 * if the currently selected file is an UMLet-compatible file, identified by its
 * specific file extension.
 * <p>
 * Overrides the following methods:
 * <p>
 * actionPerformed - Executes the cut action triggered by the user. If the current
 * project or file is invalid, the action is not performed.
 * <p>
 * update          - Updates the presentation layer of the action (e.g., enabling
 * or disabling the action) based on the selected file's properties.
 * <p>
 * getActionUpdateThread - Specifies the thread context in which the update and
 * corresponding action logic should execute. This is set
 * to run in the background thread.
 */
public class CutAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);
        if (file != null && Objects.equals(file.getExtension(), UMLET_FILE_SUFFIX)) {
            MenuFactoryIntelliJ.getInstance().doAction(MenuConstants.CUT, null);
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
