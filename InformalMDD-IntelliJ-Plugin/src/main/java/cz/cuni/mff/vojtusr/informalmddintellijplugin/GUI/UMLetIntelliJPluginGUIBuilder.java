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
        final int minWidthEMFPalette = 400;
        final int mainDividerLoc = Config.getInstance().getMain_split_position() - minWidthEMFPalette;
        mainPanel.add(initBase(contentPlaceHolder, mainDividerLoc));
        mainPanel.addKeyListener(new GUIListener());

        return mainPanel;
    }

    private final JPanel contentPlaceHolder = new JPanel(new BorderLayout());

    public void setContent(JScrollPane scrollPane) {
        contentPlaceHolder.removeAll();
        contentPlaceHolder.add(scrollPane);
    }
}
