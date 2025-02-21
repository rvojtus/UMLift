package cz.cuni.mff.vojtusr.informalmddintellijplugin.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

@State(
        name = "cz.cuni.mff.vojtusr.informalmddintellijplugin.settings.EMFSettings",
        storages = @Storage("EMFSettingsPlugin.xml")
)
public final class EMFSettings implements PersistentStateComponent<EMFSettings.State> {

    public static class State {
        @NonNls
        // Ecore
        public String projectName = "exampleProject";
        public String NsURI = "https://example.org/" + projectName;
        public String NsPrefix = projectName + "Prefix";
        public String ecoreFileName = "ecore";
        // GenModel
        public String genModelFileName = "genmodel";
        // Generation
        public Path ecoreGenModelOutputDir = Path.of("resources");
        public String generatedFilesOutputDir = projectName;
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
