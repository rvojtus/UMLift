package cz.cuni.mff.vojtusr;

import cz.cuni.mff.vojtusr.gui.GUI;

public class MainCore {
    public static void main(String[] args) {
        System.out.println("Starting program...");
        javax.swing.SwingUtilities.invokeLater(GUI::createAndShowGUI);
    }
}
