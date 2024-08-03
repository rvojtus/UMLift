package cz.cuni.mff.vojtusr;

import cz.cuni.mff.vojtusr.emf.JavaGenerator;
import cz.cuni.mff.vojtusr.emf.GenModelGenerate;
import cz.cuni.mff.vojtusr.xslt.XSLT;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;

import javax.xml.transform.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        String inputFile = "InformalMDD/src/main/resources/examples/simpleClass.uxf";
        String outputFile = "InformalMDD/src/main/resources/output.xml";
        String xsltFile = "InformalMDD/src/main/resources/xsl_templates/umlet_to_ecore.xslt";

        String genmodelFile = "InformalMDD/src/main/resources/testProject.genmodel";

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
}