package cz.cuni.mff.umlift.plugin.intellij.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBComboBoxLabel;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Objects;

/**
 * Supports creating and managing a {@link JPanel} for the UI Settings Dialog.
 *
 * <p>
 * This class provides UI to specify various settings needed for code generation and transformation.
 * Uses {@link FormBuilder} to build the GUI.
 * Contains default values for all fields. Supports {@link TextFieldWithBrowseButton} for selecting files and
 * directories.
 * </p>
 *
 * @see EMFSettings
 * @see FormBuilder
 * @since 1.0
 */
public class EMFSettingsComponent {
    private final JPanel mainPanel;
    // Ecore settings
    private final JBTextField projectNameText = new JBTextField();
    private final JBTextField NsURIText = new JBTextField();
    private final JBTextField NsPrefixText = new JBTextField();
    private final JBTextField ecoreFileNameTextField = new JBTextField();

    // GenModel settings
    private final JBTextField genModelFileNameTextField = new JBTextField();
    private final JBTextField basePackageTextField = new JBTextField();
    private final ComboBox<GenJDKLevel> genJDKLevelComboBox = new ComboBox<>(GenJDKLevel.values());

    // Generation settings
    private final TextFieldWithBrowseButton ecoreGenModelOutputDirBrowse = new TextFieldWithBrowseButton();
    private final TextFieldWithBrowseButton generatedFilesOutputDirBrowseButton = new TextFieldWithBrowseButton();

    /**
     * Creates a new settings panel using {@link FormBuilder}
     */
    public EMFSettingsComponent() {
        setUpGeneratedFilesOutputDirBrowserButton();
        setUpEcoreGenModelOutputBrowseButton();

        mainPanel = FormBuilder.createFormBuilder()
                .addComponent(new JLabel("Ecore settings"))
                .addSeparator()
                .addLabeledComponent(new JBLabel("Project name:"), projectNameText, 1, false)
                .addLabeledComponent("NsURI:", NsURIText, 1, false)
                .addLabeledComponent("NsPrefix:", NsPrefixText, 1, false)
                .addLabeledComponent("Ecore file name:", ecoreFileNameTextField, 1, false)
                .addComponent(new JBLabel())
                .addComponent(new JBLabel("GenModel settings"))
                .addSeparator()
                .addLabeledComponent(new JBLabel("Package name:"), basePackageTextField, 1, false)
                .addLabeledComponent(new JBLabel("GenJDK level:"), genJDKLevelComboBox, 1, false)
                .addLabeledComponent("GenModel file name:", genModelFileNameTextField, 1, false)
                .addComponent(new JBLabel())
                .addComponent(new JBLabel("Generation settings"))
                .addSeparator()
                .addLabeledComponent("Ecore + GenModel destination:", ecoreGenModelOutputDirBrowse, 1, false)
                .addLabeledComponent("Generated files output directory:", generatedFilesOutputDirBrowseButton, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    private void setUpGeneratedFilesOutputDirBrowserButton() {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        descriptor.setTitle("Select Model Directory");
        descriptor.setDescription("Choose a directory where the files will be generated.");
        generatedFilesOutputDirBrowseButton.addBrowseFolderListener(new TextBrowseFolderListener(descriptor));
    }

    private void setUpEcoreGenModelOutputBrowseButton() {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        descriptor.setTitle("Select Directory for Ecore");
        descriptor.setDescription("Choose a directory where the Ecore + GenModel files will be generated.");
        ecoreGenModelOutputDirBrowse.addBrowseFolderListener(new TextBrowseFolderListener(descriptor));
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return projectNameText;
    }

    @NotNull
    public String getProjectName() {
        return projectNameText.getText();
    }

    public void setProjectName(@NotNull String projectName) {
        projectNameText.setText(projectName);
    }

    @NotNull
    public String getNsURI() {
        return NsURIText.getText();
    }

    public void setNsURI(@NotNull String nsURI) {
        NsURIText.setText(nsURI);
    }

    @NotNull
    public String getNsPrefix() {
        return NsPrefixText.getText();
    }

    public void setNsPrefix(@NotNull String nsPrefix) {
        NsPrefixText.setText(nsPrefix);
    }

    @NotNull
    public String getEcoreFileName() {
        return ecoreFileNameTextField.getText();
    }

    public void setEcoreFileName(@NotNull String ecoreFileName) {
        ecoreFileNameTextField.setText(ecoreFileName);
    }

    @NotNull
    public String getGenModelFileName() {
        return genModelFileNameTextField.getText();
    }

    public void setGenModelFileName(@NotNull String genModelFileName) {
        genModelFileNameTextField.setText(genModelFileName);
    }

    @NotNull
    public String getBasePackage() {
        return basePackageTextField.getText();
    }

    public void setBasePackage(@NotNull String basePackage) {
        basePackageTextField.setText(basePackage);
    }

    @NotNull
    public String getEcoreGenModelDestination() {
        return ecoreGenModelOutputDirBrowse.getText();
    }

    public void setEcoreGenModelDestination(@NotNull String ecoreFileDestination) {
        ecoreGenModelOutputDirBrowse.setText(ecoreFileDestination);
    }

    @NotNull
    public String getGeneratedFilesOutputDir() {
        return generatedFilesOutputDirBrowseButton.getText();
    }

    public void setGeneratedFilesOutputDir(@NotNull String modelDir) {
        generatedFilesOutputDirBrowseButton.setText(modelDir);
    }

    @NotNull
    public GenJDKLevel getGenJDKLevel() {
        return (GenJDKLevel) Objects.requireNonNull(genJDKLevelComboBox.getSelectedItem());
    }

    public void setGenJDKLevel(@NotNull GenJDKLevel genJDKLevel) {
        genJDKLevelComboBox.setSelectedItem(genJDKLevel);
    }
}
