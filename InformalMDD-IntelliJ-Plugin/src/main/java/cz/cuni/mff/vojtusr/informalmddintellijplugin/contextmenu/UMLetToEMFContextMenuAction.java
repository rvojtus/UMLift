package cz.cuni.mff.vojtusr.informalmddintellijplugin.contextmenu;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import cz.cuni.mff.vojtusr.emf.GenModelGenerator;
import cz.cuni.mff.vojtusr.informalmddintellijplugin.settings.EMFSettings;
import cz.cuni.mff.vojtusr.transformation.UMLetToEcoreTransformer;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class UMLetToEMFContextMenuAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(UMLetToEMFContextMenuAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);
        Project project = event.getProject();

        if (file != null && project != null) {
            transformUXF(file.getPath(), project.getBasePath());
        } else {
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

    private void transformUXF(String uxfPath, String projectDir) {
        EMFSettings.State state = Objects.requireNonNull(EMFSettings.getInstance().getState());

        String projectName = state.projectName;
        String ecoreFilePath = state.ecoreDestination + "/ecore.ecore";
        String genModelFilePath = state.ecoreDestination;

        if (!ecoreFilePath.startsWith("/")) {
            ecoreFilePath = projectDir + File.separator + ecoreFilePath;
            genModelFilePath = projectDir + File.separator + genModelFilePath;
        }

        UMLetToEcoreTransformer transformer = new UMLetToEcoreTransformer(projectName);
        transformer.transform(uxfPath);

        try {
            transformer.saveEcore(ecoreFilePath);
            LOG.info("Saved Ecore to: " + ecoreFilePath);
        } catch (IOException e) {
            LOG.error("Error saving Ecore: ", e);
        }

        GenModelGenerator generator = new GenModelGenerator();
        GenModel genModel = generator.generateGenModelFromEcore(ecoreFilePath);
        try {
            generator.saveGenModel(genModel, genModelFilePath);
            LOG.info("Saved GenModel to: " + genModelFilePath);
        } catch (IOException e) {
            LOG.error("Could not save GenModel: " + e);
        }
    }
}
