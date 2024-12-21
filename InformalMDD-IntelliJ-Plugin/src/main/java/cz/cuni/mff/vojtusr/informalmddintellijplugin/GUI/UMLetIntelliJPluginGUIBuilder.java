package cz.cuni.mff.vojtusr.informalmddintellijplugin.GUI;

import com.baselet.control.config.Config;
import com.baselet.gui.BaseGUIBuilder;
import com.baselet.gui.listener.GUIListener;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;

public class UMLetIntelliJPluginGUIBuilder extends BaseGUIBuilder {

    public UMLetIntelliJPluginGUIBuilder() {
    }

    public JBPanel<?> buildGUI() {
        JBPanel<?> mainPanel = new JBPanel<>();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(initBase(contentPlaceHolder, Config.getInstance().getMain_split_position()));
        mainPanel.addKeyListener(new GUIListener());

        return mainPanel;
    }

    private final JPanel contentPlaceHolder = new JPanel(new BorderLayout());

    public void setContent(JScrollPane scrollPane) {
        contentPlaceHolder.removeAll();
        contentPlaceHolder.add(scrollPane);
    }
}
