package org.example;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            SortApp app = new SortApp();
            app.setVisible(true);
        });
    }
}
