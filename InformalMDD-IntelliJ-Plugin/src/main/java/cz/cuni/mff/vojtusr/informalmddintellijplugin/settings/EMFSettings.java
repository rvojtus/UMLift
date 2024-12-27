package cz.cuni.mff.vojtusr.informalmddintellijplugin.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "cz.cuni.mff.vojtusr.informalmddintellijplugin.settings.EMFSettings",
        storages = @Storage("EMFSettingsPlugin.xml")
)
public final class EMFSettings implements PersistentStateComponent<EMFSettings.State> {

    public static class State {
        @NonNls
        // Ecore
        public String projectName = "exampleProject";
        public String NsURI = "http://example.org/"+projectName;
        public String NsPrefix = projectName;
        public String ecoreDestination = "resources/";
        // Generation
        public String modelDir = projectName + "src-gen/";

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
