package cz.cuni.mff.vojtusr;

import cz.cuni.mff.vojtusr.emf.JavaGenerator;
import cz.cuni.mff.vojtusr.emf.GenModelGenerate;
import cz.cuni.mff.vojtusr.xslt.XSLT;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;

import com.baselet.standalone.MainStandalone;

import javax.swing.*;
import javax.xml.transform.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

            //MainStandalone.main(args);

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
}