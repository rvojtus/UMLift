package cz.cuni.mff.umlift.plugin.intellij.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * {@link Configurable} implementation for integrating settings into IntelliJ.
 *
 * <p>
 * This class manages the settings and synchronizes with {@link EMFSettingsComponent}.
 * It is responsible for displaying the settings form and applying and tracking user changes.
 * </p>
 *
 * @see EMFSettings
 * @see EMFSettingsComponent
 * @see Configurable
 * @since 1.0
 */
final class EMFSettingsConfigurable implements Configurable {
    private EMFSettingsComponent emfSettingsComponent;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "UMLift Settings";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return emfSettingsComponent.getPreferredFocusedComponent();
    }

    /**
     * Creates the settings UI component.
     *
     * @return the root panel of the settings component
     */
    @Nullable
    @Override
    public JComponent createComponent() {
        emfSettingsComponent = new EMFSettingsComponent();
        return emfSettingsComponent.getPanel();
    }

    /**
     * Checks whether the settings have been modified by the user.
     *
     * @return {@code true} if settings were modified, otherwise {@code false}
     */
    @Override
    public boolean isModified() {
        EMFSettings.State state = Objects.requireNonNull(EMFSettings.getInstance().getState());
        return Stream.of(!emfSettingsComponent.getProjectName().equals(state.projectName),
                !emfSettingsComponent.getNsURI().equals(state.NsURI),
                !emfSettingsComponent.getNsPrefix().equals(state.NsPrefix),
                !emfSettingsComponent.getEcoreFileName().equals(state.ecoreFileName),
                !emfSettingsComponent.getBasePackage().equals(state.basePackage),
                !emfSettingsComponent.getGenModelFileName().equals(state.genModelFileName),
                !emfSettingsComponent.getEcoreGenModelDestination().equals(state.ecoreGenModelOutputDir.toString()),
                !emfSettingsComponent.getGeneratedFilesOutputDir().equals(state.generatedFilesOutputDir)).anyMatch(Boolean::booleanValue);
    }

    /**
     * Applies the modified settings.
     *
     * <p>
     * This method is called when the user clicks "Apply" or "OK" in the settings UI panel.
     * </p>
     */
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
        state.basePackage = emfSettingsComponent.getBasePackage();
        state.genModelFileName = emfSettingsComponent.getGenModelFileName();

        // Generation
        state.ecoreGenModelOutputDir = Path.of(emfSettingsComponent.getEcoreGenModelDestination());
        state.generatedFilesOutputDir = emfSettingsComponent.getGeneratedFilesOutputDir();
    }

    /**
     * Resets the settings UI panel to the currently saved values.
     */
    @Override
    public void reset() {
        EMFSettings.State state = Objects.requireNonNull(EMFSettings.getInstance().getState());
        // Ecore
        emfSettingsComponent.setProjectName(state.projectName);
        emfSettingsComponent.setNsURI(state.NsURI);
        emfSettingsComponent.setNsPrefix(state.NsPrefix);
        emfSettingsComponent.setEcoreFileName(state.ecoreFileName);

        // GenModel
        emfSettingsComponent.setBasePackage(state.basePackage);
        emfSettingsComponent.setGenModelFileName(state.genModelFileName);

        // Generation
        emfSettingsComponent.setEcoreGenModelDestination(String.valueOf(state.ecoreGenModelOutputDir));
        emfSettingsComponent.setGeneratedFilesOutputDir(state.generatedFilesOutputDir);
    }

    /**
     * Release UI component resource when the settings panel is closed.
     */
    @Override
    public void disposeUIResources() {
        emfSettingsComponent = null;
    }
}
