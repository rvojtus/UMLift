package cz.cuni.mff.vojtusr.informalmddintellijplugin;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import cz.cuni.mff.vojtusr.gui.GUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class UMLetToolWindow implements ToolWindowFactory, DumbAware {
    private static final Logger LOG = Logger.getInstance(UMLetToolWindow.class);
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // Create the main content for the tool window
        JPanel panel = new JPanel();
        panel.add(new JLabel("UMLet Tool Window"));

        JButton button = new JButton("Generate Code");
        LOG.info("Project base path:" + project.getBasePath());
        LOG.info("Project Name: " + project.getName());
        button.addActionListener(e -> {
            GUI.startPluginCodeGeneration(project.getBasePath(), project.getName());
        });
        panel.add(button);

        // Add the panel to the tool window content
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
