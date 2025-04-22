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

public class ContextUtils {
    private static final Logger LOG = Logger.getInstance(ContextUtils.class);

    static void notifySuccess(@NotNull Project project, final String message) {
        NotificationGroupManager.getInstance().getNotificationGroup("cz.cuni.mff.umlift.plugin.intellij" +
                        ".notificationgroup")
                .createNotification(message, NotificationType.INFORMATION)
                .notify(project);
    }

    static void notifyFailure(@NotNull Project project, final String message) {
        NotificationGroupManager.getInstance().getNotificationGroup("cz.cuni.mff.umlift.plugin.intellij" +
                        ".notificationgroup")
                .createNotification(message, NotificationType.ERROR)
                .notify(project);
    }

    static void refreshFiles() {
        VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
    }

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

    static void generatePom(File outputFile, final PomConfiguration config) throws IOException {
        PomGenerator generator = new PomGenerator(config);
        Model model = generator.createPom();
        generator.savePom(model, outputFile);
    }

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
                .setOutputEcoreFile(Path.of(finalEcoreGenModelOutputDir + File.separator + state.ecoreFileName))
                .setOutputGenModelFile(Path.of(finalEcoreGenModelOutputDir + File.separator + state.genModelFileName))
                .setEcoreFileName(state.ecoreFileName)
                .setGenModelFileName(state.genModelFileName)
                .setProjectName(state.projectName)
                .setProjectNsPrefix(state.NsPrefix)
                .setProjectNsURI(state.NsURI)
                .setBasePackage(state.basePackage)
                .setGenJDKLevel(state.genJDKLevel);
    }
}
