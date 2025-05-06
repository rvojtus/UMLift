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
import cz.cuni.mff.umlift.core.config.GenerationConfig;
import cz.cuni.mff.umlift.core.emf.ModelToCodeGenerator;
import org.eclipse.emf.common.util.Diagnostic;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintStream;

import static cz.cuni.mff.umlift.plugin.intellij.settings.EMFSettings.UMLET_FILE_SUFFIX;
import static cz.cuni.mff.umlift.plugin.intellij.contextmenu.ContextUtils.*;

/**
 * A context menu action that provides code generation from UMLet Diagram files to Java code
 *
 * @see AnAction
 * @see ModelToCodeGenerator
 * @since 1.0
 */
public class UMLet2CodeContextMenuAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(UMLet2CodeContextMenuAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);
        Project project = event.getData(CommonDataKeys.PROJECT);

        if (file != null && project != null) {
            generateCode(project, file.getPath());
        } else {
            Messages.showMessageDialog(event.getProject(), "File not found", "Error", Messages.getInformationIcon());
        }
    }

    /**
     * Updates the visibility and enabled state of the action presentation based on the current context.
     * Determines whether the action should be visible and enabled by checking if the current file
     * is a UMLet-compatible file (with the correct file extension).
     *
     * @param event the action event containing context information such as the selected file
     */
    @Override
    public void update(@NotNull AnActionEvent event) {
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);

        boolean isUxfFile = file != null && UMLET_FILE_SUFFIX.equalsIgnoreCase(file.getExtension());
        event.getPresentation().setEnabledAndVisible(isUxfFile);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    /**
     * Initiates the code generation process for a UMLet file in a background task. The method sets up
     * the progress indicator, configures code generation settings, and delegates code generation logic
     * to a generator instance. After the generation, it handles diagnostics, logs results, and refreshes project files.
     *
     * @param project        the IntelliJ IDEA project where code generation is initiated. This is used
     *                       to configure paths and manage project-specific tasks during the code generation process.
     * @param inputUMLetFile the path to the UMLet file to be used as the input for code generation.
     *                       This file provides the basis for generating code artifacts.
     */
    private void generateCode(@NotNull Project project, String inputUMLetFile) {


        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Generating code for UXF file...", false) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(true);
                progressIndicator.setText("Generating code for UXF file:" + inputUMLetFile);


                PrintStream originalStream = System.out;
                // Redirect STDOUT to ProgressOutputStream
                System.setOut(new PrintStream(new ProgressOutputStream(progressIndicator)));

                populateCodeGenConfig(project, inputUMLetFile);
                ModelToCodeGenerator generator = new ModelToCodeGenerator();

                runCodeGeneration(progressIndicator, generator, originalStream);
                ContextUtils.createPom(project);
            }

            private void runCodeGeneration(@NotNull ProgressIndicator progressIndicator,
                                           ModelToCodeGenerator generator,
                                           PrintStream originalStream) {
                // Run code generation
                try {
                    Diagnostic diagnostic =
                            generator.generateCodeFromUMLetFile(GenerationConfig.getInstance().getInputUMLetFile());
                    if (diagnostic.getSeverity() == Diagnostic.OK) {
                        LOG.info("Generated code for UXF file: " + inputUMLetFile);
                        notifySuccess(project, "Successfully generated code for UXF file: " + inputUMLetFile);
                    } else {
                        LOG.error("Failed to generate code for UXF File, diagnostics: " + diagnostic);
                        notifyFailure(project, "Failed to generate code for UXF file:" + inputUMLetFile);
                    }
                } catch (IOException e) {
                    LOG.error("Failed to generate code for UXF file: " + inputUMLetFile, e);
                    notifyFailure(project, "Failed to generate code for UXF file: " + inputUMLetFile);
                } finally {
                    System.setOut(originalStream);
                    progressIndicator.setIndeterminate(false);
                    refreshFiles();
                }
            }
        });
    }
}
