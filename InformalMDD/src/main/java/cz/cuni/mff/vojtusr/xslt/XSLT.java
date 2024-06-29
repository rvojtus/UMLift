package cz.cuni.mff.vojtusr.xslt;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class XSLT {
    private final File xsltFile;
    public XSLT(File xsltFile) {
        this.xsltFile = xsltFile;
    }
    public void transform(File input, File output) throws TransformerException, FileNotFoundException {
        TransformerFactory factory = TransformerFactory.newInstance();

        Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));

        StreamSource source = new StreamSource(input);

        StreamResult result = new StreamResult(new FileOutputStream(output));

        transformer.transform(source, result);
    }
}
