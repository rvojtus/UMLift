package cz.cuni.mff.vojtusr;

import cz.cuni.mff.vojtusr.xslt.XSLT;

import javax.xml.transform.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        String inputFile = "InformalMDD/src/main/resources/examples/testHello.uxf";
        String outputFile = "InformalMDD/src/main/resources/output.xml";
        String xsltFile = "InformalMDD/src/main/resources/xsl_templates/umlet_to_ecore.xslt";

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