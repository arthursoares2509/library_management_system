package biblioteca;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BibliotecaApp extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    private InserirLivroPanel inserirLivroPanel = new InserirLivroPanel();
    private ListarLivrosPanel listarLivrosPanel = new ListarLivrosPanel();

    public BibliotecaApp() {
        setTitle("Biblioteca - Etapa 3");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel.add(inserirLivroPanel, "inserir");
        mainPanel.add(listarLivrosPanel, "listar");

        add(mainPanel);

        inserirLivroPanel.setListener(() -> {
            listarLivrosPanel.atualizarTabela();
            cardLayout.show(mainPanel, "listar");
        });

        listarLivrosPanel.setListener(() -> {
            cardLayout.show(mainPanel, "inserir");
        });

        cardLayout.show(mainPanel, "inserir");
    }

    // Tela para inserir livro
    class InserirLivroPanel extends JPanel {
        private JTextField tituloField = new JTextField(30);
        private JTextField autorField = new JTextField(30);
        private JTextField generoField = new JTextField(15);
        private JTextField dataField = new JTextField(10);
        private JTextField caminhoPdfField = new JTextField(30);
        private JButton btnEscolherPdf = new JButton("Escolher PDF");
        private JButton btnInserir = new JButton("Inserir Livro");
        private JButton btnIrParaLista = new JButton("Ver Livros");

        private byte[] pdfBytes = null;

        private Runnable onSucesso;

        public InserirLivroPanel() {
            setLayout(new BorderLayout());

            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(5,5,5,5);
            c.anchor = GridBagConstraints.WEST;

            c.gridx = 0; c.gridy = 0; form.add(new JLabel("Título:"), c);
            c.gridx = 1; form.add(tituloField, c);

            c.gridx = 0; c.gridy = 1; form.add(new JLabel("Autor:"), c);
            c.gridx = 1; form.add(autorField, c);

            c.gridx = 0; c.gridy = 2; form.add(new JLabel("Gênero:"), c);
            c.gridx = 1; form.add(generoField, c);

            c.gridx = 0; c.gridy = 3; form.add(new JLabel("Data de publicação:"), c);
            c.gridx = 1; form.add(dataField, c);

            c.gridx = 0; c.gridy = 4; form.add(new JLabel("Arquivo PDF:"), c);
            c.gridx = 1; form.add(caminhoPdfField, c);
            c.gridx = 2; form.add(btnEscolherPdf, c);

            JPanel botoes = new JPanel();
            botoes.add(btnInserir);
            botoes.add(btnIrParaLista);

            add(form, BorderLayout.CENTER);
            add(botoes, BorderLayout.SOUTH);

            caminhoPdfField.setEditable(false);

            btnEscolherPdf.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                int res = chooser.showOpenDialog(this);
                if (res == JFileChooser.APPROVE_OPTION) {
                    try {
                        pdfBytes = java.nio.file.Files.readAllBytes(chooser.getSelectedFile().toPath());
                        caminhoPdfField.setText(chooser.getSelectedFile().getAbsolutePath());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Erro ao carregar PDF: " + ex.getMessage());
                    }
                }
            });

            btnInserir.addActionListener(e -> {
                try {
                    String titulo = tituloField.getText().trim();
                    String autor = autorField.getText().trim();
                    String genero = generoField.getText().trim();
                    String data = dataField.getText().trim();

                    if(titulo.isEmpty() || autor.isEmpty() || genero.isEmpty() || data.isEmpty() || pdfBytes == null) {
                        JOptionPane.showMessageDialog(this, "Preencha todos os campos e escolha um PDF.");
                        return;
                    }

                    Livro livro = new Livro(titulo, autor, genero, data, pdfBytes);
                    LivroDAO dao = new LivroDAO();

                    if(!dao.existeLivro(livro)) {
                        dao.inserirLivro(livro);
                        JOptionPane.showMessageDialog(this, "Livro inserido com sucesso.");
                        limparCampos();
                        if(onSucesso != null) onSucesso.run();
                    } else {
                        JOptionPane.showMessageDialog(this, "Livro já existe.");
                    }
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
                }
            });

            btnIrParaLista.addActionListener(e -> {
                if(onSucesso != null) onSucesso.run();
            });
        }

        public void setListener(Runnable r) {
            onSucesso = r;
        }

        private void limparCampos() {
            tituloField.setText("");
            autorField.setText("");
            generoField.setText("");
            dataField.setText("");
            caminhoPdfField.setText("");
            pdfBytes = null;
        }
    }

    // Tela para listar livros
    class ListarLivrosPanel extends JPanel {
        private JTable tabela = new JTable();
        private DefaultTableModel modelo = new DefaultTableModel();
        private JButton btnVoltar = new JButton("Voltar para Inserir");
        private Runnable onVoltar;

        public ListarLivrosPanel() {
            setLayout(new BorderLayout());

            modelo.setColumnIdentifiers(new String[]{"ID", "Título", "Autor", "Gênero", "Data Publicação"});
            tabela.setModel(modelo);
            tabela.setDefaultEditor(Object.class, null);

            JScrollPane scroll = new JScrollPane(tabela);

            JPanel painelBotoes = new JPanel();
            painelBotoes.add(btnVoltar);

            add(scroll, BorderLayout.CENTER);
            add(painelBotoes, BorderLayout.SOUTH);

            btnVoltar.addActionListener(e -> {
                if(onVoltar != null) onVoltar.run();
            });

            atualizarTabela();
        }

        public void setListener(Runnable r) {
            onVoltar = r;
        }

        public void atualizarTabela() {
            try {
                LivroDAO dao = new LivroDAO();
                List<Livro> livros = dao.listarLivros();

                modelo.setRowCount(0);
                for(Livro l : livros) {
                    modelo.addRow(new Object[]{
                        l.getId(),
                        l.getTitulo(),
                        l.getAutor(),
                        l.getGenero(),
                        l.getDataPublicacao()
                    });
                }
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar livros: " + ex.getMessage());
            }
        }
    }

    // Classe Livro
    class Livro {
        private int id;
        private String titulo;
        private String autor;
        private String genero;
        private String dataPublicacao;
        private byte[] pdf;

        public Livro(String titulo, String autor, String genero, String dataPublicacao, byte[] pdf) {
            this.titulo = titulo;
            this.autor = autor;
            this.genero = genero;
            this.dataPublicacao = dataPublicacao;
            this.pdf = pdf;
        }

        public Livro(int id, String titulo, String autor, String genero, String dataPublicacao, byte[] pdf) {
            this(titulo, autor, genero, dataPublicacao, pdf);
            this.id = id;
        }

        public int getId() { return id; }
        public String getTitulo() { return titulo; }
        public String getAutor() { return autor; }
        public String getGenero() { return genero; }
        public String getDataPublicacao() { return dataPublicacao; }
        public byte[] getPdf() { return pdf; }
    }

    // DAO para Livro
    class LivroDAO {
        public Connection getConnection() throws SQLException {
            return Conexao.getConnection();
        }

        public void inserirLivro(Livro livro) throws SQLException {
            String sql = "INSERT INTO livros (titulo, autor, genero, data_publicacao, pdf) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, livro.getTitulo());
                stmt.setString(2, livro.getAutor());
                stmt.setString(3, livro.getGenero());
                stmt.setString(4, livro.getDataPublicacao());
                stmt.setBytes(5, livro.getPdf());
                stmt.executeUpdate();
            }
        }

        public List<Livro> listarLivros() throws SQLException {
            List<Livro> livros = new ArrayList<>();
            String sql = "SELECT * FROM livros";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    livros.add(new Livro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("genero"),
                        rs.getString("data_publicacao"),
                        rs.getBytes("pdf")
                    ));
                }
            }
            return livros;
        }

        public boolean existeLivro(Livro livro) throws SQLException {
            List<Livro> livros = listarLivros();
            for(Livro l : livros) {
                if(l.getTitulo().equalsIgnoreCase(livro.getTitulo()) && l.getAutor().equalsIgnoreCase(livro.getAutor())) {
                    return true;
                }
            }
            return false;
        }
    }

    // Classe Conexao
    static class Conexao {
        private static final String URL = "jdbc:mysql://localhost:3306/biblioteca";
        private static final String USER = "root";
        private static final String PASS = "0990";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASS);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BibliotecaApp().setVisible(true);
        });
    }
}
