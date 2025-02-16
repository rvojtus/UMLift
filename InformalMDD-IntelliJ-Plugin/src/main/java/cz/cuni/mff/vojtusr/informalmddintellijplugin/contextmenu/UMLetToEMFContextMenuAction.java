package cz.cuni.mff.vojtusr.informalmddintellijplugin.contextmenu;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
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
            transformUXF(project, file.getPath(), project.getBasePath());
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

    private void transformUXF(@NotNull Project project, String uxfPath, String projectDir) {
        EMFSettings.State state = Objects.requireNonNull(EMFSettings.getInstance().getState());

        String projectName = state.projectName;
        String ecoreFilePath = state.ecoreDestination + "/ecore.ecore";
        String genModelFilePath = state.ecoreDestination;

        if (!ecoreFilePath.startsWith("/")) {
            ecoreFilePath = projectDir + File.separator + ecoreFilePath;
            genModelFilePath = projectDir + File.separator + genModelFilePath;
        }

        String finalEcoreFilePath = ecoreFilePath;
        String finalGenModelFilePath = genModelFilePath;
        UMLetToEcoreTransformer transformer = new UMLetToEcoreTransformer(projectName);
        GenModelGenerator generator = new GenModelGenerator();

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Transforming " + projectName, false) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(true);
                progressIndicator.setText("Transforming UMLet" + uxfPath);

                transformer.transform(uxfPath);
                notifySuccess(project, "UMLet transformation finished successfully");
                
                try {
                    transformer.saveEcore(finalEcoreFilePath);
                    LOG.info("Successfully saved Ecore: " + finalEcoreFilePath);
                } catch (IOException e) {
                    LOG.error("Error saving Ecore: ", e);
                    notifyFailure(project, "Error saving Ecore " + finalEcoreFilePath);
                }

                GenModel genModel = generator.generateGenModelFromEcore(finalEcoreFilePath);
                try {
                    generator.saveGenModel(genModel, finalGenModelFilePath);
                    LOG.info("Successfully saved GenModel: " + finalGenModelFilePath);
                } catch (IOException e) {
                    LOG.error("Error saving GenModel: ", e);
                    notifyFailure(project, "Error saving GenModel " + finalGenModelFilePath);
                }

                progressIndicator.setIndeterminate(false);
                refreshFiles();
            }
        });
    }

    private static void notifySuccess(@NotNull Project project, final String message) {
        NotificationGroupManager.getInstance().getNotificationGroup("cz.cuni.mff.vojtusr.notificationgroup")
                .createNotification(message, NotificationType.INFORMATION)
                .notify(project);
    }

    private static void notifyFailure(@NotNull Project project, final String message) {
        NotificationGroupManager.getInstance().getNotificationGroup("cz.cuni.mff.vojtusr.notificationgroup")
                .createNotification(message, NotificationType.ERROR)
                .notify(project);
    }

    private static void refreshFiles() {
        VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
    }
}
