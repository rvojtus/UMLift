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
import cz.cuni.mff.umlift.core.emf.CodeGenerationConfig;
import cz.cuni.mff.umlift.core.emf.ModelToCodeGenerator;
import cz.cuni.mff.umlift.plugin.intellij.settings.EMFSettings;
import org.eclipse.emf.common.util.Diagnostic;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Objects;

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
            generateCode(project, file.getPath(), project.getBasePath());
        } else {
            Messages.showMessageDialog(event.getProject(), "File not found", "Error", Messages.getInformationIcon());
        }
    }

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

    private void generateCode(@NotNull Project project, String inputUMLetFile, String projectDir) {
        EMFSettings.State state = Objects.requireNonNull(EMFSettings.getInstance().getState());

        Path ecoreGenModelOutputDir = state.ecoreGenModelOutputDir;
        if (!ecoreGenModelOutputDir.startsWith("/")) {
            ecoreGenModelOutputDir = Path.of(projectDir + File.separator + ecoreGenModelOutputDir);
        }
        final Path finalEcoreGenModelOutputDir = ecoreGenModelOutputDir;

        String outputDir = state.generatedFilesOutputDir;
        if (!outputDir.startsWith("/")) {
            outputDir = project.getBasePath() + File.separator + outputDir;
        }

        final Path finalOutputDir = Path.of(outputDir);

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Generating code for UXF file...", false) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(true);
                progressIndicator.setText("Generating code for UXF file:" + inputUMLetFile);


                PrintStream originalStream = System.out;
                // Redirect STDOUT to ProgressOutputStream
                System.setOut(new PrintStream(new ProgressOutputStream(progressIndicator)));

                final CodeGenerationConfig codeGenerationConfig = getCodeGenerationConfig();
                ModelToCodeGenerator generator = new ModelToCodeGenerator(codeGenerationConfig);

                runCodeGeneration(progressIndicator, generator, codeGenerationConfig, originalStream);
                ContextUtils.createPom(project, state);
            }

            private void runCodeGeneration(@NotNull ProgressIndicator progressIndicator,
                                           ModelToCodeGenerator generator, CodeGenerationConfig codeGenerationConfig,
                                           PrintStream originalStream) {
                // Run code generation
                try {
                    Diagnostic diagnostic =
                            generator.generateCodeFromUMLetFile(codeGenerationConfig.getInputUMLetFile());
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

            private CodeGenerationConfig getCodeGenerationConfig() {
                // Setup configuration for code generation
                return CodeGenerationConfig.getInstance()
                        .setInputUMLetFile(Path.of(inputUMLetFile))
                        .setGeneratedFilesDir(finalOutputDir)
                        .setOutputEcoreFile(Path.of(finalEcoreGenModelOutputDir + File.separator + state.ecoreFileName))
                        .setOutputGenModelFile(Path.of(finalEcoreGenModelOutputDir + File.separator + state.genModelFileName))
                        .setProjectName(state.projectName)
                        .setProjectNsPrefix(state.NsPrefix)
                        .setProjectNsURI(state.NsURI)
                        .setBasePackage(state.basePackage)
                        .setGenJDKLevel(state.genJDKLevel)
                        .build();
            }
        });
    }
}
