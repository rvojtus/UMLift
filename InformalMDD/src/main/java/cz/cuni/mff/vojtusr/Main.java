package cz.cuni.mff.vojtusr;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Main {
    public static void main(String[] args) {
        String inputFile = "resources/examples/input.xml";
        String outputFile = "resources/output.xml";
        String xslFile = "resources/xsl_templates/umlet_to_ecore.xsl";

        try {
            transform(inputFile, outputFile, xslFile);
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException: " + e.getMessage());
            System.exit(1);
        } catch (TransformerException e) {
            System.err.println("TransformerException: " + e.getMessage());
            System.exit(1);
        }



    }

    private static void transform(String inputFile, String outputFile, String xslFile) throws FileNotFoundException, TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();

        Transformer transformer = factory.newTransformer(new StreamSource(xslFile));

        StreamSource source = new StreamSource(new File(inputFile));

        StreamResult result = new StreamResult(new FileOutputStream(outputFile));

        transformer.transform(source, result);
    }
}