package cz.cuni.mff.umlift.plugin.intellij.GUI;

import com.baselet.control.config.Config;
import com.baselet.gui.BaseGUIBuilder;
import com.baselet.gui.listener.GUIListener;
import com.intellij.ui.components.JBPanel;
import com.intellij.openapi.diagnostic.Logger;
import cz.cuni.mff.umlift.plugin.intellij.editor.UMLetFileEditor;

import javax.swing.*;
import java.awt.*;

/**
 * IntelliJ-specific implementation of UMLet's GUI Builder.
 *
 * @see BaseGUIBuilder
 * @since 1.0
 */
public class UMLetIntelliJPluginGUIBuilder extends BaseGUIBuilder {
    private static final Logger LOG = Logger.getInstance(UMLetIntelliJPluginGUIBuilder.class);

    private final JPanel contentPlaceHolder = new JPanel(new BorderLayout());

    public UMLetIntelliJPluginGUIBuilder() {
    }

    /**
     * Prepares a {@link JBPanel} panel
     *
     * @return configured {@link JBPanel} that will be used by the {@link UMLetFileEditor}
     */
    public JBPanel<?> buildGUI() {
        JBPanel<?> mainPanel = new JBPanel<>();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(initBase(contentPlaceHolder, Config.getInstance().getMain_split_position()));
        mainPanel.addKeyListener(new GUIListener());
        Config.getInstance().setShow_grid(true);

        return mainPanel;
    }

    /**
     * Clears the {@link #contentPlaceHolder} and adds a {@link JScrollPane}
     *
     * @param scrollPane to be added to the {@link #contentPlaceHolder}
     */
    public void setContent(JScrollPane scrollPane) {
        contentPlaceHolder.removeAll();
        contentPlaceHolder.add(scrollPane);
    }
}
