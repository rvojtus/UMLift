package cz.cuni.mff.vojtusr.informalmddintellijplugin.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.nio.file.Path;
import java.util.Objects;

final class EMFSettingsConfigurable implements Configurable {
    private EMFSettingsComponent emfSettingsComponent;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "EMF Settings";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return emfSettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        emfSettingsComponent = new EMFSettingsComponent();
        return emfSettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        EMFSettings.State state = Objects.requireNonNull(EMFSettings.getInstance().getState());
        return !emfSettingsComponent.getProjectName().equals(state.projectName) ||
                !emfSettingsComponent.getNsURI().equals(state.NsURI) ||
                !emfSettingsComponent.getNsPrefix().equals(state.NsPrefix) ||
                !emfSettingsComponent.getEcoreFileName().equals(state.ecoreFileName) ||
                !emfSettingsComponent.getGenModelFileName().equals(state.genModelFileName) ||
                !emfSettingsComponent.getEcoreGenModelDestination().equals(state.ecoreGenModelOutputDir) ||
                !emfSettingsComponent.getGeneratedFilesOutputDir().equals(state.generatedFilesOutputDir);
    }

    @Override
    public void apply() {
        EMFSettings.State state = Objects.requireNonNull(EMFSettings.getInstance().getState());
        // Ecore
        state.projectName = emfSettingsComponent.getProjectName();
        state.NsURI = emfSettingsComponent.getNsURI();
        state.NsPrefix = emfSettingsComponent.getNsPrefix();
        state.ecoreFileName = emfSettingsComponent.getEcoreFileName();
        state.ecoreGenModelOutputDir = Path.of(emfSettingsComponent.getEcoreGenModelDestination());

        // GenModel
        state.genModelFileName = emfSettingsComponent.getGenModelFileName();

        // Generation
        state.ecoreGenModelOutputDir = Path.of(emfSettingsComponent.getEcoreGenModelDestination());
        state.generatedFilesOutputDir = emfSettingsComponent.getGeneratedFilesOutputDir();
    }

    @Override
    public void reset() {
        EMFSettings.State state = Objects.requireNonNull(EMFSettings.getInstance().getState());
        // Ecore
        emfSettingsComponent.setProjectName(state.projectName);
        emfSettingsComponent.setNsURI(state.NsURI);
        emfSettingsComponent.setNsPrefix(state.NsPrefix);
        emfSettingsComponent.setEcoreFileName(state.ecoreFileName);

        // GenModel
        emfSettingsComponent.setGenModelFileName(state.genModelFileName);

        // Generation
        emfSettingsComponent.setEcoreGenModelDestination(String.valueOf(state.ecoreGenModelOutputDir));
        emfSettingsComponent.setGeneratedFilesOutputDir(state.generatedFilesOutputDir);
    }

    @Override
    public void disposeUIResources() {
        emfSettingsComponent = null;
    }
}
