package cz.cuni.mff.vojtusr;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.part.ViewPart;

public class SwingViewPart extends ViewPart {
    public static final String ID = "cz.cuni.mff.vojtusr.swingview";

    public SwingViewPart() {

    }

    @Override
    public void createPartControl(org.eclipse.swt.widgets.Composite parent) {
        Button button = new Button(parent, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        button.setText("Hello World");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                MessageDialog.openInformation(parent.getShell(), "Title", "Hello World");
            }
        });

    }

    @Override
    public void setFocus() {

    }
}
