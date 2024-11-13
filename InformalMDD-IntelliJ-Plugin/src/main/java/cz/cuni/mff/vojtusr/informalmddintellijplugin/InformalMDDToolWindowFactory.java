package cz.cuni.mff.vojtusr.informalmddintellijplugin;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import cz.cuni.mff.vojtusr.gui.GUI;
import org.jetbrains.annotations.NotNull;



import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InformalMDDToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
//        JPanel panel = GUI.getTopPanel();
//
//        ContentFactory contentFactory = ContentFactory.getInstance();
//        Content content = contentFactory.createContent(panel, "", false);
//        toolWindow.getContentManager().addContent(content);
        JPanel panel = new JPanel();
        JButton openFrameButton = new JButton("Open JFrame");

        // Add action listener to open the JFrame
        openFrameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create and display the JFrame
                JFrame frame = new JFrame();
                frame.setTitle("Informal MDD");
                frame.setSize(400,300);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                JLabel label = new JLabel("Test This is a JFrame Window.");
                frame.getContentPane().add(label);
                frame.setVisible(true);
            }
        });

        panel.add(openFrameButton);

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
