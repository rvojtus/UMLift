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
import cz.cuni.mff.umlift.core.emf.GenModelGenerator;
import cz.cuni.mff.umlift.core.transformation.UMLetToEcoreTransformer;
import cz.cuni.mff.umlift.plugin.intellij.settings.EMFSettings;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import static cz.cuni.mff.umlift.core.emf.HelperUtil.saveGenModel;
import static cz.cuni.mff.umlift.plugin.intellij.settings.EMFSettings.UMLET_FILE_SUFFIX;
import static cz.cuni.mff.umlift.plugin.intellij.contextmenu.ContextUtils.*;

/**
 * A context menu action that provides transformation of UMLet Diagram files to Ecore Models.
 *
 * @see AnAction
 * @see UMLetToEcoreTransformer
 * @since 1.0
 */
public class UMLetToEMFContextMenuAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(UMLetToEMFContextMenuAction.class);

    /**
     * Performs the transformation action when user selects it from the context menu.
     *
     * @param event the action event containing context
     */
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

    /**
     * Updates the visibility and availability of this action.
     *
     * <p>
     * This method ensures that the action is only available when an UMLet file is selected.
     * </p>
     *
     * @param event the action event containing context
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
     * Transforms provided UMLet File to an Ecore Model.
     * Uses {@link ProgressIndicator} to show progress of the transformation
     *
     * @param project    the project context
     * @param uxfPath    the UMLet file to be transformed to Ecore Model
     * @param projectDir directory where the generated Ecore Model will be created
     * @see UMLetToEcoreTransformer#transform(Path)
     */
    private void transformUXF(@NotNull Project project, String uxfPath, String projectDir) {
        EMFSettings.State state = Objects.requireNonNull(EMFSettings.getInstance().getState());

        UMLetToEcoreTransformer transformer = new UMLetToEcoreTransformer.EcoreConfigBuilder()
                .setEPackageName(state.projectName)
                .setEPackageNsPrefix(state.NsPrefix)
                .setEPackageNsURI(state.NsURI)
                .build();

        Path ecoreGenModelOutputDir = state.ecoreGenModelOutputDir;
        if (!ecoreGenModelOutputDir.startsWith("/")) {
            ecoreGenModelOutputDir = Path.of(projectDir + File.separator + ecoreGenModelOutputDir);
        }
        final Path finalEcoreGenModelOutputDir = ecoreGenModelOutputDir;

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Transforming " + state.projectName, false) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(true);
                progressIndicator.setText("Transforming UMLet" + uxfPath);

                transformer.transform(Path.of(uxfPath));
                notifySuccess(project, "UMLet transformation finished successfully");

                Path inputEcoreFile = saveEcoreFile();

                generateGenmodel(inputEcoreFile);

                progressIndicator.setIndeterminate(false);
                refreshFiles();
            }

            private void generateGenmodel(Path inputEcoreFile) {
                CodeGenerationConfig.getInstance().setGenJDKLevel(state.genJDKLevel).setBasePackage(state.basePackage);
                GenModelGenerator generator = new GenModelGenerator();
                GenModel genModel = generator.generateGenModelFromEcore(inputEcoreFile);

                final Path genmodelPath =
                        Path.of(finalEcoreGenModelOutputDir + File.separator + state.genModelFileName);
                try {
                    saveGenModel(genModel, genmodelPath);
                    LOG.info("Successfully saved GenModel: " + state.genModelFileName);
                } catch (IOException e) {
                    LOG.error("Error saving GenModel: ", e);
                    notifyFailure(project, "Error saving GenModel " + state.genModelFileName);
                }
            }

            private @NotNull Path saveEcoreFile() {
                final Path ecorePath = Path.of(finalEcoreGenModelOutputDir + File.separator + state.ecoreFileName);
                try {
                    transformer.saveEcore(ecorePath);
                    LOG.info("Successfully saved Ecore: " + state.ecoreFileName);
                } catch (IOException e) {
                    LOG.error("Error saving Ecore: ", e);
                    notifyFailure(project, "Error saving Ecore " + state.ecoreFileName);
                }
                return ecorePath;
            }
        });
    }


}
