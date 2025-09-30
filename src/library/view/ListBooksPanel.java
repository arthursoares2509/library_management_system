package library.view;

import library.model.Book;
import library.model.BookDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ListBooksPanel extends JPanel {
    private JTable table = new JTable();
    private DefaultTableModel model = new DefaultTableModel();
    private JButton btnBack = new JButton("Back to Insert");
    private JButton btnDelete = new JButton("Delete Book"); // New button
    private Runnable onBack;

    public ListBooksPanel() {
        setLayout(new BorderLayout());

        model.setColumnIdentifiers(new String[]{"ID", "Title", "Author", "Genre", "Publication Date"});
        table.setModel(model);
        table.setDefaultEditor(Object.class, null);

        JScrollPane scroll = new JScrollPane(table);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnBack);
        buttonPanel.add(btnDelete); // Adds the new button

        add(scroll, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Existing listener for the "Back" button
        btnBack.addActionListener(e -> {
            if (onBack != null) onBack.run();
        });

        // New listener for the "Delete" button
        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a book to delete!");
                return;
            }

            int id = (int) model.getValueAt(selectedRow, 0); // Gets the ID of the selected row
            int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this book?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                try {
                    BookDAO dao = new BookDAO();
                    if (dao.deleteBook(id)) {
                        JOptionPane.showMessageDialog(this, "Book deleted successfully!");
                        updateTable(); // Updates table after deletion
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting: " + ex.getMessage());
                }
            }
        });

        updateTable();
    }

    public void setListener(Runnable r) {
        onBack = r;
    }

    public void updateTable() {
        try {
            BookDAO dao = new BookDAO();
            List<Book> books = dao.listBooks();

            model.setRowCount(0);
            for (Book b : books) {
                model.addRow(new Object[]{
                    b.getId(),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getGenre(),
                    b.getPublicationDate()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + ex.getMessage());
        }
    }
}
