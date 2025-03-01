package cz.cuni.mff.umlift.plugin.intellij.contextmenu;

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
import cz.cuni.mff.umlift.core.emf.ModelToCodeGenerator;
import cz.cuni.mff.umlift.plugin.intellij.settings.EMFSettings;
import org.eclipse.emf.common.util.Diagnostic;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Objects;

import static cz.cuni.mff.umlift.plugin.intellij.contextmenu.ContextUtils.*;
import static cz.cuni.mff.umlift.plugin.intellij.settings.EMFSettings.ECORE_FILE_SUFFIX;

/**
 * A context menu action that provides code generation for Ecore Model files.
 *
 * <p>
 * This action appears in the context menu when an Ecore File is selected in the Project View.
 * </p>
 *
 * @see AnAction
 * @see ModelToCodeGenerator
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
     * Generates code using {@link ModelToCodeGenerator}. Uses {@link ProgressIndicator} to show progress of the generation.
     *
     * @param project the current project context
     * @param file    the file to generate code from
     * @throws IOException if there is any file I/O related problem
     * @see ModelToCodeGenerator#generateCodeFromEcore(Path, Path) for details about the generation
     */
    private void generateCode(Project project, VirtualFile file) throws IOException {
        EMFSettings.State state = Objects.requireNonNull(EMFSettings.getInstance().getState());
        ModelToCodeGenerator generator = new ModelToCodeGenerator();
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
                    PrintStream printStream = new PrintStream(new ContextUtils.ProgressOutputStream(progressIndicator));
                    System.setOut(printStream);
                    Diagnostic diagnostic = generator.generateCodeFromEcore(Path.of(file.getPath()), Path.of(finalOutputDir));

                    if (diagnostic.getSeverity() == Diagnostic.ERROR) {
                        LOG.error("Error when generating code from Ecore file: " + diagnostic);
                        notifyFailure(project, "Error generating code from Ecore file.");
                    } else {
                        progressIndicator.setText("Code generation finished successfully.");
                        notifySuccess(project, "Code generation finished successfully.");
                    }
                } catch (IOException e) {
                    LOG.error("Error when generating code from Ecore file: " + e);
                } finally {
                    refreshFiles();
                    System.setOut(originalStream);
                    progressIndicator.setIndeterminate(false);
                }
            }
        });
    }
}
