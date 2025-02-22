package cz.cuni.mff.vojtusr.informalmddintellijplugin.contextmenu;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
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
import cz.cuni.mff.vojtusr.emf.JavaGenerator;
import cz.cuni.mff.vojtusr.informalmddintellijplugin.settings.EMFSettings;
import org.eclipse.emf.common.util.Diagnostic;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Objects;

import static cz.cuni.mff.vojtusr.informalmddintellijplugin.settings.EMFSettings.ECORE_FILE_SUFFIX;

/**
 * A context menu action that provides code generation for Ecore Model files.
 *
 * <p>
 * This action appears in the context menu when an Ecore File is selected in the Project View.
 * </p>
 *
 * @see AnAction
 * @see JavaGenerator
 * @since 1.0
 */
public class GenerateCodeContextMenuAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(GenerateCodeContextMenuAction.class);

    /**
     * Performs the code generation action when user selects it from the context menu.
     *
     * @param event the action event containing context
     */
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

        } else {
            Messages.showMessageDialog(event.getProject(), "File not found", "Error", Messages.getInformationIcon());
        }
    }

    /**
     * Updates the visibility and availability of this action.
     *
     * <p>
     * This method ensures that the action is only available when an Ecore file is selected.
     * </p>
     *
     * @param event the action event containing context
     */
    @Override
    public void update(@NotNull AnActionEvent event) {
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);

        boolean isUxfFile = file != null && (ECORE_FILE_SUFFIX.equalsIgnoreCase(file.getExtension()));
        event.getPresentation().setEnabledAndVisible(isUxfFile);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    /**
     * Generates code using {@link JavaGenerator}. Uses {@link ProgressIndicator} to show progress of the generation.
     *
     * @param project the current project context
     * @param file    the file to generate code from
     * @throws IOException if there is any file I/O related problem
     * @see JavaGenerator#generateCodeFromEcore(Path, Path) for details about the generation
     */
    private void generateCode(Project project, VirtualFile file) throws IOException {
        EMFSettings.State state = Objects.requireNonNull(EMFSettings.getInstance().getState());
        JavaGenerator generator = new JavaGenerator();
        String outputDir = state.generatedFilesOutputDir;
        if (!outputDir.startsWith("/")) {
            outputDir = project.getBasePath() + File.separator + outputDir;
        }

        final String finalOutputDir = outputDir;
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Generating code from Ecore file...", true) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                PrintStream originalStream = System.out;
                progressIndicator.setIndeterminate(true);
                progressIndicator.setText("Generating code from Ecore file...");
                try {
                    PrintStream printStream = new PrintStream(new ProgressOutputStream(progressIndicator));
                    System.setOut(printStream);
                    Diagnostic diagnostic = generator.generateCodeFromEcore(Path.of(file.getPath()), Path.of(finalOutputDir));

                    if (diagnostic.getSeverity() == Diagnostic.ERROR) {
                        notifyFailure(diagnostic);
                    } else {
                        progressIndicator.setText("Code generation finished successfully.");
                        notifySuccess(project);
                        refreshFiles();
                    }

                } catch (IOException e) {
                    LOG.error("Error when generating code from Ecore file: " + e);
                } finally {
                    System.setOut(originalStream);
                }
                progressIndicator.setIndeterminate(false);
            }
        });
    }

    private static class ProgressOutputStream extends OutputStream {
        private final ProgressIndicator progressIndicator;

        public ProgressOutputStream(ProgressIndicator progressIndicator) {
            this.progressIndicator = progressIndicator;
        }

        @Override
        public void write(int b) {
            String output = String.valueOf((char) b);
            if (progressIndicator != null) {
                progressIndicator.setText(progressIndicator.getText() + output);
            }
        }
    }

    private static void notifySuccess(@NotNull Project project) {
        NotificationGroupManager.getInstance().getNotificationGroup("cz.cuni.mff.vojtusr.notificationgroup")
                .createNotification("Code generation finished successfully", NotificationType.INFORMATION)
                .notify(project);
    }

    private static void notifyFailure(Diagnostic diagnostic) {
        final String message = "Code Generation Failed: " + diagnostic.getMessage();

        // Log error
        LOG.error(message);

        // Show error notification
        Notification notification = new Notification(
                "cz.cuni.mff.vojtusr.notificationgroup", "Error", message, NotificationType.ERROR
        );
        Notifications.Bus.notify(notification);

        // Show error dialog
        Messages.showErrorDialog(message, "Code Generation Error");
    }

    private static void refreshFiles() {
        VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
    }
}
