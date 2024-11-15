package demo;

import gui.MainWindow;

import java.util.Locale;

/*
 * Main swing GUI class
 */
public class DemoExecution {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US); // Unify number formats
        MainWindow.createAndShowGUI();
    }
}
