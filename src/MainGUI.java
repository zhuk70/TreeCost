
import model.ItemFormula;
import service.ArvoreFormula;
import view.TelaTreeCost;

import javax.swing.SwingUtilities;
import java.util.Arrays;
import java.util.List;

public class MainGUI {
    public static void main(String[] args) {
        List<ItemFormula> itens = Arrays.asList(
            new ItemFormula("Caneta", null, 1, 2.50),
            new ItemFormula("Tinta", "Caneta", 1, 0.80),
            new ItemFormula("Pigmento", "Tinta", 1, 0.30),
            new ItemFormula("Solvente", "Tinta", 1, 0.50),
            new ItemFormula("Corpo", "Caneta", 1, 1.00),
            new ItemFormula("Plástico", "Corpo", 1, 0.70),
            new ItemFormula("Clip", "Corpo", 1, 0.30),
            new ItemFormula("Tampa", "Caneta", 1, 0.70),
            new ItemFormula("Plástico", "Tampa", 1, 0.50),
            new ItemFormula("Vedação", "Tampa", 1, 0.20)
        );

        ArvoreFormula arvore = new ArvoreFormula();
        arvore.construir(itens);

        SwingUtilities.invokeLater(() -> {
            new TelaTreeCost(arvore).setVisible(true);
        });
    }
}
