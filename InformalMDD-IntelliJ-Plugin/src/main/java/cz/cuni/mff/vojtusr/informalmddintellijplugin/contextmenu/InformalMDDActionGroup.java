package cz.cuni.mff.vojtusr.informalmddintellijplugin.contextmenu;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class InformalMDDActionGroup extends DefaultActionGroup {
    private final static String[] supportedExtensions = {"uxf", "ecore", "genmodel"};
    @Override
    public void update(@NotNull AnActionEvent event) {
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);

        boolean isSupportedFile = file != null && Arrays.stream(supportedExtensions).anyMatch(x -> x.equalsIgnoreCase(file.getExtension()));
        event.getPresentation().setEnabledAndVisible(isSupportedFile);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
