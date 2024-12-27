package cz.cuni.mff.vojtusr.informalmddintellijplugin.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class EMFSettingsComponent {
    private final JPanel mainPanel;
    private final JBTextField projectNameText = new JBTextField();
    private final JBTextField NsURIText = new JBTextField();
    private final JBTextField NsPrefixText = new JBTextField();
    private final TextFieldWithBrowseButton modelDirBrowseButton = new TextFieldWithBrowseButton();

    public EMFSettingsComponent() {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        descriptor.setTitle("Select Model Directory");
        descriptor.setDescription("Choose a directory where the files will be generated.");
        modelDirBrowseButton.addBrowseFolderListener(new TextBrowseFolderListener(descriptor));

        mainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Project name:"), projectNameText, 1, false)
                .addLabeledComponent("NsURI:", NsURIText, 1, false)
                .addLabeledComponent("NsPrefix:", NsPrefixText, 1, false)
                .addLabeledComponent("ModelDir:", modelDirBrowseButton, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
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
    public String getModelDir() {
        return modelDirBrowseButton.getText();
    }

    public void setModelDir(@NotNull String modelDir) {
        modelDirBrowseButton.setText(modelDir);
    }
}
