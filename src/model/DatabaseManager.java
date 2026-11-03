package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:treecost.db";

    public static void inicializarBanco() {
        String sql = "CREATE TABLE IF NOT EXISTS componentes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT NOT NULL, " +
                    "nome_pai TEXT, " +
                    "quantidade INTEGER NOT NULL, " +
                    "preco_unitario REAL NOT NULL)";
        try (Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("-> Base de dados SQLite inicializada com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao criar base de dados: " + e.getMessage());
        }
    }

    public static void salvarArvore(TreeNode no) {
        limparBanco();
        salvarRecursivo(no);
        System.out.println("-> Árvore guardada na base de dados com sucesso!");
    }

    private static void salvarRecursivo(TreeNode no) {
        if (no == null) return;
        
        String sql = "INSERT INTO componentes(nome, nome_pai, quantidade, preco_unitario) VALUES(?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, no.getItem().getNome());
            pstmt.setString(2, no.getItem().getNomePai());
            pstmt.setInt(3, no.getItem().getQuantidade());
            pstmt.setDouble(4, no.getItem().getPrecoUnitario());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (TreeNode filho : no.getFilhos()) {
            salvarRecursivo(filho);
        }
    }

    public static List<ItemFormula> carregarItens() {
        List<ItemFormula> itens = new ArrayList<>();
        String sql = "SELECT nome, nome_pai, quantidade, preco_unitario FROM componentes";
        
        try (Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String nome = rs.getString("nome");
                String nomePai = rs.getString("nome_pai");
                int qtd = rs.getInt("quantidade");
                double preco = rs.getDouble("preco_unitario");
                itens.add(new ItemFormula(nome, nomePai, qtd, preco));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itens;
    }

    private static void limparBanco() {
        try (Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM componentes");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}