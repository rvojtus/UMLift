package cz.cuni.mff.vojtusr;

import cz.cuni.mff.vojtusr.emf.JavaGenerator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import javax.xml.transform.TransformerException;
import java.io.IOException;

public class SwingViewPart extends ViewPart {
    public static final String ID = "cz.cuni.mff.vojtusr.swingview";

    private static String projectTitle = "Project title";
    private static String projectDir;

    public SwingViewPart() {
    }

    @Override
    public void createPartControl(org.eclipse.swt.widgets.Composite parent) {

        GridLayout layout = new GridLayout(2, false);
        parent.setLayout(layout);

        Label projectTitleLabel = new Label(parent, SWT.NONE);
        projectTitleLabel.setText("Project title: ");

        Text projectTitleText = new Text(parent, SWT.BORDER);
        projectTitleText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));


        Label xsltFileLabel = new Label(parent, SWT.NONE);
        xsltFileLabel.setText("XSLT file: ");

        Text xsltFilePathText = getXsltFilePathText(parent);


        Button generateButton = new Button(parent, SWT.PUSH);
        generateButton.setText("Generate Code");
        GridData buttonGridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        buttonGridData.horizontalSpan = 2;
        generateButton.setLayoutData(buttonGridData);

        generateButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                projectTitle = projectTitleText.getText();
                projectDir = getCurrentDirectory();

                String xsltFilePath = xsltFilePathText.getText();
                String UMLFile = getUMLInputFilePath();

                String testingMessage = "XSLT: " + xsltFilePath +
                        "\nUML: " + UMLFile +
                        "\nProject title: " + projectTitle +
                        "\nProject dir: " + projectDir;

                MessageDialog.openInformation(parent.getShell(), "Code Generation", testingMessage);

                if (UMLFile != null) {
                    startCodeGeneration(xsltFilePath, UMLFile);
                }
            }
        });
    }

    private static String getUMLInputFilePath() {
        IWorkbenchPage iWorkbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorPart editor = iWorkbenchPage.getActiveEditor();

        IFile file = editor.getEditorInput().getAdapter(IFile.class);
        if (file != null && file.getFileExtension().equalsIgnoreCase("uxf")) {
            return file.getLocation().toOSString();
        }
        return null;
    }

    private static Text getXsltFilePathText(Composite parent) {
        Text xsltFilePathText = new Text(parent, SWT.BORDER);
        xsltFilePathText.setEditable(false);
        xsltFilePathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Button selectXSLTFileButton = new Button(parent, SWT.PUSH);
        selectXSLTFileButton.setText("Select File...");
        selectXSLTFileButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog fileDialog = new FileDialog(parent.getShell(), SWT.OPEN);
                fileDialog.setText("Select a file");
                fileDialog.setFilterExtensions(new String[]{"*.xslt", "*.xsl", "*.xml"});
                fileDialog.setFilterNames(new String[]{"XSLT File", "XSL File", "XML File"});
                String filePath = fileDialog.open();
                if (filePath != null) {
                    xsltFilePathText.setText(filePath);
                }
            }
        });
        return xsltFilePathText;
    }

    private static String getCurrentDirectory() {
        IWorkbenchPage iWorkbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        ISelection selection = iWorkbenchPage.getSelection();
        if (selection instanceof IStructuredSelection) {
            Object element = ((IStructuredSelection) selection).getFirstElement();
            if (element instanceof IResource) {
                IResource resource = (IResource) element;
                return resource.getLocation().toOSString();
            }
        }
        return null;
    }

    private static void startCodeGeneration(String xsltFile, String inputUMLFile) {
        JavaGenerator javaGenerator = new JavaGenerator(xsltFile);
        try {
            javaGenerator.generate(inputUMLFile, projectDir, projectTitle);
        } catch (IOException e) { // todo better error display
            System.err.println("IOException occurred: " + e.getMessage());
        } catch (TransformerException e) {
            System.err.println("TransformerException occurred: " + e.getMessage());
        }
    }

    @Override
    public void setFocus() {

    }
}
