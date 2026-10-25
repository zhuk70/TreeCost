

import service.ArvoreFormula;

import java.util.Arrays;
import java.util.List;

import model.ItemFormula;
import model.TreeNode;

public class TesteComputador {
    public static void main(String[] args) {
        List<ItemFormula> itensPC = Arrays.asList(
            new ItemFormula("Computador", null, 1, 2500.00),
            new ItemFormula("Gabinete", "Computador", 1, 800.00),
            new ItemFormula("Placa-Mãe", "Gabinete", 1, 500.00),
            new ItemFormula("CPU", "Placa-Mãe", 1, 1000.00),
            new ItemFormula("Memória RAM", "Placa-Mãe", 2, 200.00),
            new ItemFormula("Chipset", "Placa-Mãe", 1, 150.00),
            new ItemFormula("Fonte de Alimentação", "Gabinete", 1, 300.00),
            new ItemFormula("Armazenamento", "Gabinete", 1, 0.00),
            new ItemFormula("HDD", "Armazenamento", 1, 250.00),
            new ItemFormula("SSD", "Armazenamento", 1, 400.00),
            new ItemFormula("Monitor", "Computador", 1, 900.00),
            new ItemFormula("Tela LCD", "Monitor", 1, 600.00),
            new ItemFormula("Carcaça", "Monitor", 1, 100.00),
            new ItemFormula("Periféricos", "Computador", 1, 0.00),
            new ItemFormula("Teclado", "Periféricos", 1, 100.00),
            new ItemFormula("Mouse", "Periféricos", 1, 80.00)
        );

        ArvoreFormula arvore = new ArvoreFormula();
        TreeNode raiz = arvore.construir(itensPC);
        boolean adicionado = arvore.adicionarComponente(
        "Computador",
        new ItemFormula("Webcam", "Computador", 1, 150.00)
        );
        boolean removido = arvore.removerComponente(
            "Computador",
            "Webcam"
        );
        ItemFormula componente = arvore.consultarComponente(
            raiz,
            "Memória RAM"
        );

        if (componente != null) {
            System.out.println("\nComponente encontrado:");
            System.out.println("Nome: " + componente.getNome());
            System.out.println("Quantidade: " + componente.getQuantidade());
            System.out.printf("Preço unitário: R$ %.2f%n",
                    componente.getPrecoUnitario());
        } else {
            System.out.println("Componente não encontrado.");
        }
        System.out.println("\nLista completa de componentes:");
        arvore.listarComponentes(raiz);
                
        boolean editado = arvore.editarComponente(
            raiz,
            "Memória RAM",
            3,
            210.00
        );

        System.out.println("Componente editado: " + editado);

        System.out.println("\nDados atualizados:");
        arvore.listarComponentes(raiz);


        System.out.println("Componente removido: " + removido);

        System.out.println("\nÁrvore após a remoção:");
        arvore.imprimir(raiz, 0);

        System.out.println("Componente adicionado: " + adicionado);

        System.out.println("Árvore da fórmula do computador:");
        arvore.imprimir(raiz, 0);

        System.out.println("\nExplosão para 2 computadores:");
        arvore.explodirFormula(raiz, 2, 0);
    }
}
