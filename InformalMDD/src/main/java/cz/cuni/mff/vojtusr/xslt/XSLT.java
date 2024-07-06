package cz.cuni.mff.vojtusr.xslt;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.FileNotFoundException;

public class XSLT {
    private final String xsltFile;
    public XSLT(String xsltFile) {
        this.xsltFile = xsltFile;
    }
    public void transform(String input, String output) throws TransformerException, FileNotFoundException {
        Source xsltSource = new StreamSource(xsltFile);

        TransformerFactory factory = TransformerFactory.newInstance();

        Transformer transformer = factory.newTransformer(xsltSource);

        StreamSource source = new StreamSource(input);

        StreamResult result = new StreamResult(output);

        transformer.transform(source, result);
    }
}
