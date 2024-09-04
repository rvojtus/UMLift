package cz.cuni.mff.vojtusr;

import com.baselet.gui.CurrentGui;
import com.baselet.standalone.gui.StandaloneGUI;
import cz.cuni.mff.vojtusr.emf.JavaGenerator;
import cz.cuni.mff.vojtusr.emf.GenModelGenerate;
import cz.cuni.mff.vojtusr.xslt.XSLT;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;

import com.baselet.standalone.MainStandalone;

import javax.swing.*;
import javax.xml.transform.*;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.baselet.standalone.MainStandalone.tmpFile;

public class Main {
    public static void main(String[] args) {
        String inputFile = "InformalMDD-core/src/main/resources/examples/simpleClass.uxf";
        String outputFile = "InformalMDD-core/src/main/resources/output.xml";
        String xsltFile = "InformalMDD-core/src/main/resources/xsl_templates/umlet_to_ecore.xslt";

        String genmodelFile = "InformalMDD-core/src/main/resources/testProject.genmodel";

        try {
            XSLT xslt = new XSLT(xsltFile);

            Path outputPath = Path.of(outputFile);
            if (!Files.exists(outputPath)) {
                Files.createFile(outputPath);
            }
            else {
                new FileOutputStream(outputFile).close();
            }

            xslt.transform(inputFile, outputFile);

            GenModelGenerate genModelGenerate = new GenModelGenerate();
            GenModel genModel = genModelGenerate.generate();

            JavaGenerator javaGenerator = new JavaGenerator();
            javaGenerator.generate(genModel);

            //javax.swing.SwingUtilities.invokeLater(Main::createAndShowGUI);

            String[] umlet_args = new String[0];

            //MainStandalone.main(umlet_args);
            com.baselet.control.Main umlet_main = MainStandalone.initInstance();
            //umlet_main.init(new StandaloneGUI(com.baselet.control.Main.getInstance(), tmpFile()));
            CurrentGui.getInstance().setGui(new StandaloneGUI(com.baselet.control.Main.getInstance(), tmpFile()));
            ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE); // Tooltips should not hide after some time
            CurrentGui.getInstance().getGui().initGUI(); // show gui
            umlet_main.doNew();

            CurrentGui currentGui = CurrentGui.getInstance();

            JFrame umlet_frame = (JFrame) currentGui.getGui().getMainFrame();
            JMenuBar menu = umlet_frame.getJMenuBar();

            addCodeGenerationButton(menu, umlet_frame);

            //javax.swing.SwingUtilities.invokeLater(() -> {MainStandalone.main(umlet_args);});

        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException: " + e.getMessage());
            System.exit(1);
        } catch (TransformerException e) {
            System.err.println("TransformerException: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("HelloWorldSwing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel label = new JLabel("Hello World");
        frame.getContentPane().add(label);
        frame.pack();
        frame.setVisible(true);
    }

    private static void addCodeGenerationButton(JMenuBar menu, JFrame mainUMLetFrame) {
        JButton codeGenerationButton = new JButton(new AbstractAction("Code Generation") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog generationOptionsFrame = new JDialog(mainUMLetFrame, "Code Generation Options", true);
                generationOptionsFrame.setSize(300, 200);
                generationOptionsFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

                // Center the new frame relative to the main frame
                generationOptionsFrame.setLocationRelativeTo(mainUMLetFrame);

                // Disable resizing, which prevents full-screen mode
                generationOptionsFrame.setResizable(false);

                generationOptionsFrame.setVisible(true);
            }
        });

        menu.add(codeGenerationButton);

        SwingUtilities.updateComponentTreeUI(mainUMLetFrame);
    }
}