package cz.cuni.mff.umlift.plugin.intellij.contextmenu;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import cz.cuni.mff.umlift.core.config.GenerationConfig;
import cz.cuni.mff.umlift.core.pom.MavenPackaging;
import cz.cuni.mff.umlift.core.pom.PomConfiguration;
import cz.cuni.mff.umlift.core.pom.PomGenerator;
import cz.cuni.mff.umlift.plugin.intellij.settings.EMFSettings;
import org.apache.maven.model.Model;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Utility class for context-related operations in the project.
 */
public class ContextUtils {
    private static final Logger LOG = Logger.getInstance(ContextUtils.class);

    /**
     * Notifies the user of a successful operation in the context of the provided project.
     *
     * @param project the current IntelliJ project in which the notification should be shown
     * @param message the success message to be displayed in the notification
     */
    static void notifySuccess(@NotNull Project project, final String message) {
        NotificationGroupManager.getInstance().getNotificationGroup("cz.cuni.mff.umlift.plugin.intellij" +
                        ".notificationgroup")
                .createNotification(message, NotificationType.INFORMATION)
                .notify(project);
    }

    /**
     * Sends a failure notification to the user within the context of the specified project.
     *
     * @param project the current IntelliJ project in which the notification should be displayed
     * @param message the error message to be displayed in the notification
     */
    static void notifyFailure(@NotNull Project project, final String message) {
        NotificationGroupManager.getInstance().getNotificationGroup("cz.cuni.mff.umlift.plugin.intellij" +
                        ".notificationgroup")
                .createNotification(message, NotificationType.ERROR)
                .notify(project);
    }

    /**
     * Refreshes all in-memory file system views across the IntelliJ platform, ensuring synchronization with
     * the current state of the filesystem on disk.
     * <p>
     * This method performs a refresh operation without using a file watcher, forcing any cached file views
     * in the platform's virtual file system to be updated immediately.
     * <p>
     * The refresh operation is performed globally and impacts all files and directories managed by
     * the IntelliJ Virtual File System.
     */
    static void refreshFiles() {
        VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
    }

    /**
     * A custom {@link OutputStream} that writes output to a {@link ProgressIndicator}.
     * <p>
     * This stream is used to redirect output to the progress indicator's text area,
     * typically to provide feedback or progress updates during tasks that involve output streams.
     */
    static class ProgressOutputStream extends OutputStream {
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

    /**
     * Generates and saves a Maven POM (Project Object Model) file based on the provided configuration.
     *
     * @param outputFile the file where the generated POM will be saved
     * @param config     the configuration used to create the POM, including details such as model version,
     *                   group ID, artifact ID, version, packaging, and Java generation level
     * @throws IOException if an error occurs while writing the POM to the file
     */
    static void generatePom(File outputFile, final PomConfiguration config) throws IOException {
        PomGenerator generator = new PomGenerator(config);
        Model model = generator.createPom();
        generator.savePom(model, outputFile);
    }

    /**
     * Creates a Maven POM (Project Object Model) file for the specified project.
     * The generated POM file will use the project's base path and the configurations
     * defined in the {@link GenerationConfig} instance for attributes such as
     * groupId, artifactId, version, and Java generation level.
     *
     * @param project the project for which the POM file is to be created. This parameter
     *                must not be null and must contain a valid base path for the project.
     */
    static void createPom(@NotNull Project project) {
        try {
            File pomFile = new File(Objects.requireNonNull(project.getBasePath()) + File.separator + "pom.xml");
            PomConfiguration pomConfig = new PomConfiguration("4.0.0",
                    GenerationConfig.getInstance().getBasePackage(),
                    GenerationConfig.getInstance().getProjectName(),
                    "1.0.0", MavenPackaging.POM, GenerationConfig.getInstance().getGenJDKLevel());
            ContextUtils.generatePom(pomFile, pomConfig);
            LOG.info("Generated POM for Project " + project.getBasePath());
            notifySuccess(project, "Successfully created POM for Project " + project.getBasePath());
        } catch (IOException e) {
            LOG.error("Error saving POM to: " + project.getBasePath(), e);
            notifyFailure(project, "Failed to create POM for Project " + project);
        }
    }

    /**
     * Configures the code generation settings for the project based on the current plugin state
     * and the provided UMLet file path. This method sets up output directories, file names,
     * namespace information, and other parameters needed for model generation.
     *
     * @param project        the current IntelliJ project for which code generation settings are being configured.
     *                       This parameter is used to resolve project-specific directory paths.
     * @param inputUMLetFile the file path to the UMLet file that serves as the input
     *                       for the code generation process.
     */
    static void populateCodeGenConfig(@NotNull Project project,
                                      String inputUMLetFile) {
        EMFSettings.State state = Objects.requireNonNull(EMFSettings.getInstance().getState());

        Path ecoreGenModelOutputDir = state.ecoreGenModelOutputDir;
        if (!ecoreGenModelOutputDir.startsWith("/")) {
            ecoreGenModelOutputDir = Path.of(project.getBasePath() + File.separator + ecoreGenModelOutputDir);
        }
        final Path finalEcoreGenModelOutputDir = ecoreGenModelOutputDir;

        String outputDir = state.generatedFilesOutputDir;
        if (!outputDir.startsWith("/")) {
            outputDir = project.getBasePath() + File.separator + outputDir;
        }

        final Path finalOutputDir = Path.of(outputDir);
        // Setup configuration for code generation
        GenerationConfig.getInstance()
                .setInputUMLetFile(Path.of(inputUMLetFile))
                .setGeneratedFilesDir(finalOutputDir)
                .setEcoreGenModelDir(finalEcoreGenModelOutputDir)
                .setProjectName(state.projectName)
                .setProjectNsPrefix(state.NsPrefix)
                .setProjectNsURI(state.NsURI)
                .setBasePackage(state.basePackage)
                .setGenJDKLevel(state.genJDKLevel);
    }
}
