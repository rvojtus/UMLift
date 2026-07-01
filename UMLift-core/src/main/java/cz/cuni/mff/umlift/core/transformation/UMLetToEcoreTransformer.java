package cz.cuni.mff.umlift.core.transformation;

import com.baselet.control.Main;
import com.baselet.control.enums.Program;
import com.baselet.control.enums.RuntimeType;
import com.baselet.control.util.Utils;
import com.baselet.diagram.DiagramHandler;
import cz.cuni.mff.umlift.core.transformation.processors.DiagramProcessor;
import org.eclipse.emf.ecore.*;

import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

public class UMLetToEcoreTransformer {
    private static final Logger LOG = Logger.getLogger(UMLetToEcoreTransformer.class.getName());

    public UMLetToEcoreTransformer() {
    }

    /**
     * Transforms a UMLet diagram file into an Ecore model file.
     *
     * <p>This method initializes the UMLet program in batch mode if it is not already initialized.
     * It reads the UMLet diagram from the specified file path, extracts the grid elements, and
     * processes them to generate the corresponding Ecore model.</p>
     *
     * @param inputUMLetFile the path to the UMLet file
     */
    public void transform(Path inputUMLetFile) {
        if (!Program.isInitialized()) {
            Utils.BuildInfo buildInfo = Utils.readBuildInfo();
            Program.init(buildInfo.version, RuntimeType.BATCH);
        }
        DiagramHandler diagram = new DiagramHandler(inputUMLetFile.toFile());

        DiagramProcessor diagramProcessor = new DiagramProcessor();
        try {
            diagramProcessor.processDiagram(diagram);
            LOG.info("UMLet to Ecore transformation finished for: " + inputUMLetFile);
        } catch (Exception e) {
            LOG.severe(e.getMessage());
        }
    }

    /**
     * Transforms a UMLet diagram file into an Ecore model file, and closes or keeps background program running.
     *
     * @param inputUMLetFile the path to the UMLet file
     * @param keepOpen       specifies whether the running UMLet backend program, used for transformation, stays open
     *                       or closes
     * @see #transform(Path)
     */
    public void transform(java.nio.file.Path inputUMLetFile, boolean keepOpen) {
        transform(inputUMLetFile);
        if (!keepOpen) {
            if (Program.isInitialized()) {
                Main.getInstance().closeProgram();
            }
        }
    }

    public boolean closeTransformer() {
        if (!Program.isInitialized()) {
            return false;
        }
        Main.getInstance().closeProgram();
        return true;
    }

}
