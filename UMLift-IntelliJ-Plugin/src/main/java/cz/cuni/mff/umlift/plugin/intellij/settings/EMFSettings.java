package cz.cuni.mff.umlift.plugin.intellij.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * Stores plugin configuration settings persistently.
 * <p>
 * This class is responsible for saving and loading user settings related to the UMLift plugin.
 * The state {@link State} is stored in the IntelliJ configuration directory and is managed by the IntelliJ platform.
 * </p>
 * <p>
 * Related classes: {@link EMFSettingsComponent} and {@link EMFSettingsConfigurable}
 * </p>
 *
 * @see PersistentStateComponent
 * @since 1.0
 */
@State(
        name = "cz.cuni.mff.umlift.plugin.intellij.settings.EMFSettings",
        storages = @Storage("UMLiftSettingsPlugin.xml")
)
public final class EMFSettings implements PersistentStateComponent<EMFSettings.State> {
    public static final String ECORE_FILE_SUFFIX = "ecore";
    public static final String GENMODEL_FILE_SUFFIX = "genmodel";
    public static final String UMLET_FILE_SUFFIX = "uxf";

    /**
     * Represents the persistent state of the plugin settings.
     */
    public static class State {
        @NonNls
        // Ecore
        public String projectName = "exampleProject";
        public String NsURI = "https://example.org/" + projectName;
        public String NsPrefix = projectName + "Prefix";
        public String ecoreFileName = "ecore";
        // GenModel
        public String genModelFileName = "genmodel";
        public String basePackage = "org.example";
        public GenJDKLevel genJDKLevel = GenJDKLevel.JDK210_LITERAL;
        // Generation
        public Path ecoreGenModelOutputDir = Path.of("src/main/resources/EMFModels");
        public String generatedFilesOutputDir = "src/main/java";
        // Maven POM
        public boolean generatePom = true;
    }

    private State myState = new State();

    public static EMFSettings getInstance() {
        return ApplicationManager.getApplication().getService(EMFSettings.class);
    }

    @Override
    public @Nullable EMFSettings.State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

}
