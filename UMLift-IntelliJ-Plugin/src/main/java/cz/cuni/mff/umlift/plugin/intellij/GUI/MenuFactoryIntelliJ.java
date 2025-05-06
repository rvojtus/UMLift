package cz.cuni.mff.umlift.plugin.intellij.GUI;

import com.baselet.diagram.CurrentDiagram;
import com.baselet.diagram.DiagramHandler;
import com.baselet.gui.CurrentGui;
import com.baselet.gui.menu.MenuFactory;
import com.intellij.openapi.diagnostic.Logger;

import javax.swing.*;

import static com.baselet.control.constants.MenuConstants.*;

/**
 * A factory class for managing menu-related actions specific to IntelliJ integration.
 * <p>
 * This class extends the MenuFactory base class and provides custom implementations
 * of certain actions, such as enabling search functionality and adjusting zoom levels
 * in diagrams. It integrates with the IntelliJ platform to perform these actions
 * interactively.
 * <p>
 * The singleton pattern is used to ensure that only one instance of the class exists
 * during runtime.
 */
public class MenuFactoryIntelliJ extends MenuFactory {
    private static final Logger LOG = Logger.getInstance(MenuFactoryIntelliJ.class);
    private static MenuFactoryIntelliJ instance = null;

    public static MenuFactoryIntelliJ getInstance() {
        if (instance == null) {
            instance = new MenuFactoryIntelliJ();
        }
        return instance;
    }

    /**
     * Executes actions based on the specified menu item and additional parameter.
     * This method overrides the base class implementation to handle specific
     * actions such as enabling search functionality and adjusting zoom levels
     * in diagrams. If the action is not explicitly handled, it delegates the
     * operation to the superclass implementation.
     *
     * @param menuItem the identifier for the menu action to be performed. Supported values
     *                 include specific action constants such as SEARCH and ZOOM.
     * @param param    an additional parameter required for performing the action. For example,
     *                 when the menuItem is ZOOM, this parameter represents the zoom level
     *                 as an Integer.
     */
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
