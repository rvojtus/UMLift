package cz.cuni.mff.vojtusr.informalmddintellijplugin.GUI;

import com.baselet.control.CanCloseProgram;
import com.baselet.control.config.Config;
import com.baselet.diagram.CurrentDiagram;
import com.baselet.diagram.DiagramHandler;
import com.baselet.diagram.DrawPanel;
import com.baselet.element.interfaces.GridElement;
import com.baselet.element.old.custom.CustomElementHandler;
import com.baselet.gui.BaseGUI;
import com.baselet.gui.CurrentGui;
import com.baselet.gui.pane.OwnSyntaxPane;
import cz.cuni.mff.vojtusr.informalmddintellijplugin.UMLetFileEditor;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;

public class UMLetIntelliJGUI extends BaseGUI {

    private UMLetFileEditor editor;
    private final HashMap<DiagramHandler, UMLetFileEditor> diagrams;

    public UMLetIntelliJGUI(CanCloseProgram main) {
        super(main);
        diagrams = new HashMap<>();
    }

    @Override
    public void close(DiagramHandler diagram) {
        // intellij does the closing
    }

    @Override
    public void closeWindow() {
        main.closeProgram();
    }

    @Override
    public void diagramSelected(DiagramHandler handler) {
        DrawPanel currentDiagram = CurrentGui.getInstance().getGui().getCurrentDiagram();
        if (currentDiagram == null) {
            return;
        }
        boolean enable = handler != null &&!currentDiagram.getGridElements().isEmpty();
        //niečo
    }

    @Override
    public void enablePasteMenuEntry() {}

    @Override
    public CustomElementHandler getCurrentCustomHandler() {
        if (editor == null) {
            return null;
        }
        return editor.getCustomElementHandler();
    }

    @Override
    public DrawPanel getCurrentDiagram() {
        if (editor == null) {
            return null;
        }
        return editor.getDiagram();
    }

    @Override
    public int getMainSplitPosition() {
        return Config.getInstance().getMain_split_position();
    }

    @Override
    public int getRightSplitPosition() {
        return Config.getInstance().getRight_split_position();
    }

    @Override
    public int getMailSplitPosition() {
        return Config.getInstance().getMail_split_position();
    }

    @Override
    public String getSelectedPalette() {
        if (editor != null) {
            return editor.getSelectedPaletteName();
        }
        return null;
    }

    @Override
    protected void init() {}

    @Override
    public void open(DiagramHandler diagram) {
        if (editor != null) {
            editor.open(diagram);
        }
    }

    @Override
    public void jumpTo(DiagramHandler diagram) {}

    @Override
    public void showPalette(String palette) {
        super.showPalette(palette);
        if (editor != null) {
            editor.showPalette(palette);
        }
    }

    @Override
    public void setCustomElementChanged(CustomElementHandler handler, boolean changed) {}

    @Override
    public void setCustomElementSelected(boolean selected) {
//        if (editor != null && contributor != null) {
//            contributor.setCustomElementSelected(selected);
//        }
    }

    @Override
    public void setCustomPanelEnabled(boolean enable) {
        if (editor != null) {
            editor.setCustomPanelEnabled(enable);
//            if (contributor != null) {
//                contributor.setCustomPanelEnabled(enable);
//            }
        }
    }

    @Override
    public void setMailPanelEnabled(boolean enable) {
        if (editor != null) {
            editor.setMailPanelEnabled(enable);
        }
    }

    @Override
    public boolean isMailPanelVisible() {
        return editor.isMailPanelVisible();
    }

    @Override
    public void updateDiagramName(DiagramHandler diagram, String name) {
        UMLetFileEditor editor = diagrams.get(diagram);
        if (editor != null) {
            editor.diagramNameChanged();
        }
    }

    @Override
    public void setDiagramChanged(DiagramHandler diagram, boolean changed) {
        UMLetFileEditor editor = diagrams.get(diagram);
        if (editor != null) {
            editor.dirtyChanged();
        }
    }

    @Override
    public void setCursor(Cursor cursor) {
        if (editor != null) {
            editor.setCursor(cursor);
        }
    }

    public void registerEditorForDiagramHandler(UMLetFileEditor editor, DiagramHandler handler) {
        diagrams.put(handler, editor);
    }

    public void setCurrentDiagramHandler(DiagramHandler handler) {
        CurrentDiagram.getInstance().setCurrentDiagramHandler(handler);
    }

    public void setCurrentEditor(UMLetFileEditor editor) {
        this.editor = editor;
    }

    public void editorRemoved(UMLetFileEditor editor) {
        // Before removing the editor, we have to store the actual splitpositions and lastUsedPalette to variables so that a new editor has the same values
        Config.getInstance().setMain_split_position(editor.getMainSplitLocation());
        Config.getInstance().setRight_split_position(editor.getRightSplitLocation());
        Config.getInstance().setLastUsedPalette(getSelectedPalette());
        diagrams.remove(editor.getDiagram().getHandler());
        if (editor.equals(this.editor)) {
            this.editor = null;
        }
    }

    @Override
    public OwnSyntaxPane getPropertyPane() {
        if (editor != null) {
            return editor.getPropertyPane();
        }
        else {
            return null;
        }
    }

    @Override
    public void requestFocus() {
        if (editor != null) {
            editor.requestFocus();
        }
    }

    @Override
    public void elementsSelected(Collection<GridElement> selectedElements) {
        super.elementsSelected(selectedElements);
//        if (contributor != null) {
//            contributor.setElementsSelected(selectedElements);
//        }
    }

    @Override
    public void setValueOfZoomDisplay(int i) {
//        if (contributor != null) {
//            contributor.updateZoomMenuRadioButton(i);
//        }
    }

    @Override
    public void afterSaving() {
        super.afterSaving();
        //EclipseGUI.refreshWorkspace();
    }

    @Override
    public void focusPropertyPane() {
        editor.focusPropertyPane();
    }

    @Override
    public Frame getMainFrame() {
        return editor.getMainFrame();
    }

    @Override
    public boolean hasExtendedContextMenu() {
        return false;
    }

    @Override
    public boolean saveWindowSizeInConfig() {
        return false;
    }
}
