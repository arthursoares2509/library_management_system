package library.view;

import library.model.Book;
import library.model.BookDAO;
import javax.swing.*;
import java.awt.*;

public class InsertBookPanel extends JPanel {
    private JTextField titleField = new JTextField(30);
    private JTextField authorField = new JTextField(30);
    private JTextField genreField = new JTextField(15);
    private JTextField dateField = new JTextField(10);
    private JTextField pdfPathField = new JTextField(30);
    private JButton btnChoosePdf = new JButton("Choose PDF");
    private JButton btnInsert = new JButton("Insert Book");
    private JButton btnGoToList = new JButton("View Books");

    private byte[] pdfBytes = null;
    private Runnable onSuccess;

    public InsertBookPanel() {
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0; form.add(new JLabel("Title:"), c);
        c.gridx = 1; form.add(titleField, c);

        c.gridx = 0; c.gridy = 1; form.add(new JLabel("Author:"), c);
        c.gridx = 1; form.add(authorField, c);

        c.gridx = 0; c.gridy = 2; form.add(new JLabel("Genre:"), c);
        c.gridx = 1; form.add(genreField, c);

        c.gridx = 0; c.gridy = 3; form.add(new JLabel("Publication Date:"), c);
        c.gridx = 1; form.add(dateField, c);

        c.gridx = 0; c.gridy = 4; form.add(new JLabel("PDF File:"), c);
        c.gridx = 1; form.add(pdfPathField, c);
        c.gridx = 2; form.add(btnChoosePdf, c);

        JPanel buttons = new JPanel();
        buttons.add(btnInsert);
        buttons.add(btnGoToList);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        pdfPathField.setEditable(false);

        btnChoosePdf.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int res = chooser.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                try {
                    pdfBytes = java.nio.file.Files.readAllBytes(chooser.getSelectedFile().toPath());
                    pdfPathField.setText(chooser.getSelectedFile().getAbsolutePath());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error loading PDF: " + ex.getMessage());
                }
            }
        });

        btnInsert.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                String genre = genreField.getText().trim();
                String date = dateField.getText().trim();

                if(title.isEmpty() || author.isEmpty() || genre.isEmpty() || date.isEmpty() || pdfBytes == null) {
                    JOptionPane.showMessageDialog(this, "Fill in all fields and choose a PDF.");
                    return;
                }

                Book book = new Book(title, author, genre, date, pdfBytes);
                BookDAO dao = new BookDAO();

                if(!dao.bookExists(book)) {
                    dao.insertBook(book);
                    JOptionPane.showMessageDialog(this, "Book inserted successfully.");
                    clearFields();
                    if(onSuccess != null) onSuccess.run();
                } else {
                    JOptionPane.showMessageDialog(this, "Book already exists.");
                }
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        btnGoToList.addActionListener(e -> {
            if(onSuccess != null) onSuccess.run();
        });
    }

    public void setListener(Runnable r) {
        onSuccess = r;
    }

    private void clearFields() {
        titleField.setText("");
        authorField.setText("");
        genreField.setText("");
        dateField.setText("");
        pdfPathField.setText("");
        pdfBytes = null;
    }
}
