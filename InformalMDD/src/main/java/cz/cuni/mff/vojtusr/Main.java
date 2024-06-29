package cz.cuni.mff.vojtusr;

import cz.cuni.mff.vojtusr.xslt.XSLT;

import javax.xml.transform.*;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        String inputFile = "resources/examples/input.xml";
        String outputFile = "resources/output.xml";
        String xslFile = "resources/xsl_templates/umlet_to_ecore.xsl";

        try {
            XSLT xslt = new XSLT(new File(xslFile));
            xslt.transform(new File(inputFile), new File(outputFile));
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException: " + e.getMessage());
            System.exit(1);
        } catch (TransformerException e) {
            System.err.println("TransformerException: " + e.getMessage());
            System.exit(1);
        }
    }
}