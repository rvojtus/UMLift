package cz.cuni.mff.umlift.plugin.intellij.GUI;

import com.baselet.diagram.CurrentDiagram;
import com.baselet.diagram.DiagramHandler;
import com.baselet.gui.CurrentGui;
import com.baselet.gui.menu.MenuFactory;
import com.intellij.openapi.diagnostic.Logger;

import javax.swing.*;

import static com.baselet.control.constants.MenuConstants.*;

public class MenuFactoryIntelliJ extends MenuFactory {
    private static final Logger LOG = Logger.getInstance(MenuFactoryIntelliJ.class);
    private static MenuFactoryIntelliJ instance = null;

    public static MenuFactoryIntelliJ getInstance() {
        if (instance == null) {
            instance = new MenuFactoryIntelliJ();
        }
        return instance;
    }

    @Override
    public void doAction(final String menuItem, final Object param) {
        DiagramHandler actualHandler = CurrentDiagram.getInstance().getDiagramHandler();
        if (menuItem.equals(SEARCH)) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    CurrentGui.getInstance().getGui().enableSearch(true);
                }
            });
        } else if (menuItem.equals(ZOOM) && actualHandler != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    CurrentDiagram.getInstance().getDiagramHandler().setGridAndZoom((Integer) param);
                }
            });
        }
        // If the action is not overwritten, it is part of the default actions
        else {
            super.doAction(menuItem, param);
        }
    }
}
