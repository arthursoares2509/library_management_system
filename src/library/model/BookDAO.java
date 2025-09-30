package library.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    public Connection getConnection() throws SQLException {
        return ConnectionFactory.getConnection();
    }

    // New method to delete a book by ID
    public boolean deleteBook(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Existing methods
    public void insertBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, author, genre, publication_date, pdf) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getGenre());
            stmt.setString(4, book.getPublicationDate());
            stmt.setBytes(5, book.getPdf());
            stmt.executeUpdate();
        }
    }

    public List<Book> listBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                books.add(new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("genre"),
                    rs.getString("publication_date"),
                    rs.getBytes("pdf")
                ));
            }
        }
        return books;
    }

    public boolean bookExists(Book book) throws SQLException {
        List<Book> books = listBooks();
        for (Book b : books) {
            if (b.getTitle().equalsIgnoreCase(book.getTitle()) &&
                b.getAuthor().equalsIgnoreCase(book.getAuthor())) {
                return true;
            }
        }
        return false;
    }
}
