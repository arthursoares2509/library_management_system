package library;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new library.view.libraryApp().setVisible(true);
        });
    }
}
