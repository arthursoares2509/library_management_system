package library.view;

import javax.swing.*;
import java.awt.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class libraryApp extends JFrame {
private CardLayout cardLayout = new CardLayout();
private JPanel mainPanel = new JPanel(cardLayout);

private InsertBookPanel insertBookPanel = new InsertBookPanel();
private ListBooksPanel listBooksPanel = new ListBooksPanel();

public libraryApp() {
    setTitle("Library - Step 3");
    setSize(700, 500);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    mainPanel.add(insertBookPanel, "insert");
    mainPanel.add(listBooksPanel, "list");

    add(mainPanel);

    insertBookPanel.setListener(() -> {
        listBooksPanel.updateTable();
        cardLayout.show(mainPanel, "list");
    });

    listBooksPanel.setListener(() -> {
        cardLayout.show(mainPanel, "insert");
    });

    cardLayout.show(mainPanel, "insert");
}

}
