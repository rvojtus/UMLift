package cz.cuni.mff.vojtusr.informalmddintellijplugin.contextmenu;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import cz.cuni.mff.vojtusr.emf.JavaGenerator;
import cz.cuni.mff.vojtusr.informalmddintellijplugin.settings.EMFSettings;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class GenerateCodeContextMenuAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(GenerateCodeContextMenuAction.class);
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);
        Project project = event.getProject();

        if (file != null && project != null) {
            try {
                generateCode(project, file);
            } catch (IOException e) {
                LOG.error("Error when generating code from Ecore file: " + e);
            }

        }
        else {
            Messages.showMessageDialog(event.getProject(), "File not found", "Error", Messages.getInformationIcon());
        }
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);

        boolean isUxfFile = file != null && ("ecore".equalsIgnoreCase(file.getExtension()));
        event.getPresentation().setEnabledAndVisible(isUxfFile);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    private void generateCode(Project project, VirtualFile file) throws IOException {
        EMFSettings.State state = Objects.requireNonNull(EMFSettings.getInstance().getState());
        JavaGenerator generator = new JavaGenerator();
        String outputDir = state.modelDir;
        if (!outputDir.startsWith("/")) {
            outputDir = project.getBasePath() + File.separator + outputDir;
        }
        generator.generateCodeFromFiles(file.getPath(), outputDir);
    }
}
