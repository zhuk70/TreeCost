 
package model;

public class ItemFormula {
    private String nome;
    private String nomePai;
    private int quantidade;
    private double precoUnitario;

    public ItemFormula(String nome, String nomePai, int quantidade, double precoUnitario) {
        this.nome = nome;
        this.nomePai = nomePai;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public String getNome() {
        return nome;
    }

    public String getNomePai() {
        return nomePai;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(double precoUnitario) {
        if (precoUnitario < 0) {
            throw new IllegalArgumentException("O preço não pode ser negativo.");
        }

        this.precoUnitario = precoUnitario;
    }
    
    public void setQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException(
                "A quantidade deve ser maior que zero."
            );
        }

        this.quantidade = quantidade;
    }

}
