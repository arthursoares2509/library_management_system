package library.model;

public class Book {
    private int id;
    private String title;
    private String author;
    private String genre;
    private String publicationDate;
    private byte[] pdf;

    // Constructors
    public Book(String title, String author, String genre, String publicationDate, byte[] pdf) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publicationDate = publicationDate;
        this.pdf = pdf;
    }

    public Book(int id, String title, String author, String genre, String publicationDate, byte[] pdf) {
        this(title, author, genre, publicationDate, pdf);
        this.id = id;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public String getPublicationDate() { return publicationDate; }
    public byte[] getPdf() { return pdf; }
}
