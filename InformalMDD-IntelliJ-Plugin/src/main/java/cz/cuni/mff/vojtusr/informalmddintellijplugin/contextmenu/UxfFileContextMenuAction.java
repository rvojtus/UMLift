package cz.cuni.mff.vojtusr.informalmddintellijplugin.contextmenu;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import cz.cuni.mff.vojtusr.informalmddintellijplugin.settings.EMFSettings;
import cz.cuni.mff.vojtusr.informalmddintellijplugin.settings.EMFSettingsComponent;
import cz.cuni.mff.vojtusr.transformation.UMLetToEcoreTransformer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class UxfFileContextMenuAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);

        if (file != null) {
            transformUXF(file.getPath());
        }
        else {
            Messages.showMessageDialog(event.getProject(), "File not found", "Error", Messages.getInformationIcon());
        }
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);

        boolean isUxfFile = file != null && "uxf".equalsIgnoreCase(file.getExtension());
        event.getPresentation().setEnabledAndVisible(isUxfFile);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    private void transformUXF(String uxfPath) {
        EMFSettings.State state = Objects.requireNonNull(EMFSettings.getInstance().getState());

        String projectName = state.projectName;
        String ecoreFilePath = state.ecoreDestination + "/ecore.ecore";

        UMLetToEcoreTransformer transformer = new UMLetToEcoreTransformer(projectName);
        transformer.transform(uxfPath, ecoreFilePath);
    }
}
