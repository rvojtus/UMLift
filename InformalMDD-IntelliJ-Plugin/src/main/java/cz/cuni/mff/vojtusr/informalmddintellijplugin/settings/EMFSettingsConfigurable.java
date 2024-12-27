package cz.cuni.mff.vojtusr.informalmddintellijplugin.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
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
                !emfSettingsComponent.getEcoreFileDestination().equals(state.ecoreDestination) ||
                !emfSettingsComponent.getModelDir().equals(state.modelDir);
    }

    @Override
    public void apply() {
        EMFSettings.State state = Objects.requireNonNull(EMFSettings.getInstance().getState());
        state.projectName = emfSettingsComponent.getProjectName();
        state.NsURI = emfSettingsComponent.getNsURI();
        state.NsPrefix = emfSettingsComponent.getNsPrefix();
        state.ecoreDestination = emfSettingsComponent.getEcoreFileDestination();
        state.modelDir = emfSettingsComponent.getModelDir();
    }

    @Override
    public void reset() {
        EMFSettings.State state = Objects.requireNonNull(EMFSettings.getInstance().getState());
        emfSettingsComponent.setProjectName(state.projectName);
        emfSettingsComponent.setNsURI(state.NsURI);
        emfSettingsComponent.setNsPrefix(state.NsPrefix);
        emfSettingsComponent.setEcoreFileDestination(state.ecoreDestination);
        emfSettingsComponent.setModelDir(state.modelDir);
    }

    @Override
    public void disposeUIResources() {
        emfSettingsComponent = null;
    }
}
