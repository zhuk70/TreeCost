package service;

import model.ItemFormula;
import model.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArvoreFormula {
    private TreeNode raiz;

    public TreeNode construir(List<ItemFormula> itens) {
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("A lista de itens não pode estar vazia.");
        }

        Map<String, TreeNode> nos = new HashMap<>();
        Map<String, Integer> ocorrencias = new HashMap<>();
        Set<ItemFormula> itensConhecidos = new HashSet<>();

        for (ItemFormula item : itens) {
            if (item == null || item.getNome() == null || item.getNome().isBlank()) {
                throw new IllegalArgumentException("Existe um item sem nome válido.");
            }

            if (item.getQuantidade() <= 0 || item.getPrecoUnitario() < 0) {
                throw new IllegalArgumentException("Quantidade ou preço inválido.");
            }

            if (!itensConhecidos.add(item)) {
                throw new IllegalArgumentException("O mesmo objeto foi informado mais de uma vez.");
            }

            String chave = item.getNome();
            int numero = ocorrencias.getOrDefault(chave, 0) + 1;
            ocorrencias.put(chave, numero);

            if (numero > 1) {
                chave = item.getNome() + "#" + numero;
            }

            nos.put(chave, new TreeNode(item));
        }

        Map<ItemFormula, TreeNode> nosPorItem = new HashMap<>();
        for (TreeNode no : nos.values()) {
            nosPorItem.put(no.getItem(), no);
        }

        List<TreeNode> raizes = new ArrayList<>();

        for (TreeNode no : nos.values()) {
            ItemFormula item = no.getItem();

            if (item.getNomePai() == null) {
                raizes.add(no);
                continue;
            }

            List<TreeNode> paisPossiveis = new ArrayList<>();

            for (TreeNode candidato : nos.values()) {
                if (candidato.getItem().getNome().equals(item.getNomePai())
                        && candidato != no) {
                    paisPossiveis.add(candidato);
                }
            }

            if (paisPossiveis.isEmpty()) {
                throw new IllegalArgumentException(
                    "Pai não encontrado para o item: " + item.getNome()
                );
            }

            if (paisPossiveis.size() > 1) {
                throw new IllegalArgumentException(
                    "Pai ambíguo para " + item.getNome()
                    + ". É necessário identificar unicamente o pai."
                );
            }

            paisPossiveis.get(0).adicionarFilho(no);
        }

        if (raizes.size() != 1) {
            throw new IllegalArgumentException(
                "A fórmula deve possuir exatamente uma raiz."
            );
        }

        raiz = raizes.get(0);

        Set<TreeNode> visitados = new HashSet<>();
        validarArvore(raiz, visitados);

        if (visitados.size() != itens.size()) {
            throw new IllegalArgumentException(
                "Existem itens desconectados ou ciclos na fórmula."
            );
        }

        return raiz;
    }

    private void validarArvore(TreeNode no, Set<TreeNode> visitados) {
        if (!visitados.add(no)) {
            throw new IllegalArgumentException("Foi detectado um ciclo na fórmula.");
        }

        for (TreeNode filho : no.getFilhos()) {
            validarArvore(filho, visitados);
        }
    }

    public TreeNode getRaiz() {
        return raiz;
    }

    public void imprimir(TreeNode no, int nivel) {
        if (no == null) {
            return;
        }

        System.out.println(
            "  ".repeat(nivel)
            + no.getItem().getNome()
            + " - R$ "
            + String.format("%.2f", no.getItem().getPrecoUnitario())
        );

        for (TreeNode filho : no.getFilhos()) {
            imprimir(filho, nivel + 1);
        }
    }
    

    public double calcularCustoComponentes(TreeNode no) {
        if (no == null) {
            return 0.0;
        }

        if (no.ehFolha()) {
            return no.getItem().getPrecoUnitario();
        }

        double total = 0.0;

        for (TreeNode filho : no.getFilhos()) {
            total += filho.getItem().getQuantidade()
                    * calcularCustoComponentes(filho);
        }

        return total;
    }

    
    public void explodirFormula(TreeNode no, int quantidadeProduzir, int nivel) {
        if (no == null) {
            return;
        }

        if (quantidadeProduzir <= 0) {
            throw new IllegalArgumentException(
                "A quantidade a produzir deve ser maior que zero."
            );
        }

        for (TreeNode filho : no.getFilhos()) {
            int quantidadeNecessaria =
                    quantidadeProduzir * filho.getItem().getQuantidade();

            System.out.println(
                "  ".repeat(nivel)
                + filho.getItem().getNome()
                + " - Quantidade necessária: "
                + quantidadeNecessaria
            );

            explodirFormula(filho, quantidadeNecessaria, nivel + 1);
        }
    }
    
    public boolean atualizarPreco(TreeNode no, String nomeComponente, double novoPreco) {
        if (no == null) {
            return false;
        }

        if (novoPreco < 0) {
            throw new IllegalArgumentException(
                "O preço não pode ser negativo."
            );
        }

        if (no.getItem().getNome().equalsIgnoreCase(nomeComponente)) {
            no.getItem().setPrecoUnitario(novoPreco);
            return true;
        }

        for (TreeNode filho : no.getFilhos()) {
            if (atualizarPreco(filho, nomeComponente, novoPreco)) {
                return true;
            }
        }

        return false;
    }

    
    public boolean adicionarComponente(String nomePai, ItemFormula novoItem) {
        if (raiz == null || novoItem == null) {
            return false;
        }

        if (novoItem.getNome() == null || novoItem.getNome().isBlank()) {
            return false;
        }

        if (novoItem.getQuantidade() <= 0
                || novoItem.getPrecoUnitario() < 0) {
            return false;
        }

        TreeNode pai = buscarNo(raiz, nomePai);

        if (pai == null) {
            return false;
        }

        pai.adicionarFilho(new TreeNode(novoItem));
        return true;
    }

    private TreeNode buscarNo(TreeNode atual, String nome) {
        if (atual == null || nome == null) {
            return null;
        }

        if (atual.getItem().getNome().equalsIgnoreCase(nome)) {
            return atual;
        }

        for (TreeNode filho : atual.getFilhos()) {
            TreeNode encontrado = buscarNo(filho, nome);

            if (encontrado != null) {
                return encontrado;
            }
        }
        return null;
    }
    
    public boolean removerComponente(String nomePai, String nomeComponente) {
        if (raiz == null || nomePai == null || nomeComponente == null) {
            return false;
        }

        if (raiz.getItem().getNome().equalsIgnoreCase(nomeComponente)) {
            return false;
        }

        TreeNode pai = buscarNo(raiz, nomePai);

        if (pai == null) {
            return false;
        }

        for (TreeNode filho : pai.getFilhos()) {
            if (filho.getItem().getNome().equalsIgnoreCase(nomeComponente)) {
                pai.getFilhos().remove(filho);
                return true;
            }
        }

        return false;
    }
    
    public ItemFormula consultarComponente(TreeNode no, String nome) {
        if (no == null || nome == null) {
            return null;
        }

        if (no.getItem().getNome().equalsIgnoreCase(nome)) {
            return no.getItem();
        }

        for (TreeNode filho : no.getFilhos()) {
            ItemFormula encontrado = consultarComponente(filho, nome);

            if (encontrado != null) {
                return encontrado;
            }
        }

        return null;
    }
    
    public void listarComponentes(TreeNode no) {
        if (no == null) {
            return;
        }

        ItemFormula item = no.getItem();

        System.out.printf(
            "%s | Pai: %s | Quantidade: %d | Preço: R$ %.2f%n",
            item.getNome(),
            item.getNomePai() == null ? "Nenhum" : item.getNomePai(),
            item.getQuantidade(),
            item.getPrecoUnitario()
        );

        for (TreeNode filho : no.getFilhos()) {
            listarComponentes(filho);
        }
    }
    
    public boolean editarComponente(
            TreeNode no,
            String nome,
            int novaQuantidade,
            double novoPreco) {

        if (no == null || nome == null || nome.isBlank()) {
            return false;
        }

        if (novaQuantidade <= 0 || novoPreco < 0) {
            return false;
        }

        ItemFormula item = no.getItem();

        if (item.getNome().equalsIgnoreCase(nome)) {
            item.setQuantidade(novaQuantidade);
            item.setPrecoUnitario(novoPreco);
            return true;
        }

        for (TreeNode filho : no.getFilhos()) {
            if (editarComponente(
                    filho, nome, novaQuantidade, novoPreco)) {
                return true;
            }
        }

        return false;
    }

}
