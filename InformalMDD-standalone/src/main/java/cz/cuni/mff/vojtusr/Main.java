package cz.cuni.mff.vojtusr;


import cz.cuni.mff.vojtusr.gui.GUI;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(GUI::createAndShowGUI);
    }
}