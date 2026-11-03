import model.DatabaseManager;
import model.HtmlReporter;
import model.ItemFormula;
import service.ArvoreFormula;
import view.TelaTreeCost;

import javax.swing.SwingUtilities;
import java.util.Arrays;
import java.util.List;

public class MainGUI {
    public static void main(String[] args) {
        
        DatabaseManager.inicializarBanco();

        List<ItemFormula> itens = DatabaseManager.carregarItens();

        if (itens == null || itens.isEmpty()) {
            System.out.println("-> Base de dados vazia. A inserir dados padrão...");
            itens = Arrays.asList(
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
        }

        ArvoreFormula arvore = new ArvoreFormula();
        arvore.construir(itens);
        
        DatabaseManager.salvarArvore(arvore.getRaiz());

        System.out.println("--- TESTE DE EXPLOSÃO (Produzir 5 unidades) ---");
        arvore.explodirFormula(arvore.getRaiz(), 5, 0);

        double novoCustoTotal = arvore.calcularCustoComponentes(arvore.getRaiz());
        System.out.println("Novo Custo Total (Implosão): R$ " + novoCustoTotal);

        HtmlReporter.gerarRelatorio(novoCustoTotal);

        SwingUtilities.invokeLater(() -> {
            new TelaTreeCost(arvore).setVisible(true);
        });
    }
}