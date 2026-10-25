
package view;

import model.ItemFormula;
import model.TreeNode;
import service.ArvoreFormula;

import javax.swing.*;
import java.awt.*;

public class TelaTreeCost extends JFrame {
    private final ArvoreFormula arvore;

    private final JTextField campoNome = new JTextField();
    private final JTextField campoPai = new JTextField();
    private final JTextField campoQuantidade = new JTextField("1");
    private final JTextField campoPreco = new JTextField();

    private final JTextArea areaArvore = new JTextArea();

    public TelaTreeCost(ArvoreFormula arvore) {
        this.arvore = arvore;

        setTitle("TreeCost - Fórmula de Produtos");
        setSize(750, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painelCampos = new JPanel(new GridLayout(5, 2, 8, 8));
        painelCampos.setPreferredSize(new Dimension(750, 300));
        painelCampos.setBorder(
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        );

        painelCampos.add(new JLabel("Nome do componente:"));
        painelCampos.add(campoNome);

        painelCampos.add(new JLabel("Nome do pai:"));
        painelCampos.add(campoPai);

        painelCampos.add(new JLabel("Quantidade:"));
        painelCampos.add(campoQuantidade);

        painelCampos.add(new JLabel("Preço unitário:"));
        painelCampos.add(campoPreco);

        JButton botaoCadastrar = new JButton("Cadastrar componente");
        JButton botaoAtualizar = new JButton("Atualizar árvore");
        JButton botaoConsultar = new JButton("Consultar componente");

        JPanel painelBotoes = new JPanel(new GridLayout(1, 3, 5, 0));
        painelBotoes.add(botaoCadastrar);
        painelBotoes.add(botaoAtualizar);
        painelBotoes.add(botaoConsultar);

        painelCampos.add(new JLabel("Ações:"));
        painelCampos.add(painelBotoes);

        areaArvore.setEditable(false);
        areaArvore.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        add(painelCampos, BorderLayout.NORTH);
        add(new JScrollPane(areaArvore), BorderLayout.CENTER);

        botaoCadastrar.addActionListener(e -> cadastrarComponente());
        botaoAtualizar.addActionListener(e -> atualizarArvore());
        botaoConsultar.addActionListener(e -> consultarComponente());

        atualizarArvore();
    }

    private void cadastrarComponente() {
        try {
            String nome = campoNome.getText().trim();
            String pai = campoPai.getText().trim();
            int quantidade = Integer.parseInt(
                campoQuantidade.getText().trim()
            );

            double preco = Double.parseDouble(
                campoPreco.getText().trim().replace(",", ".")
            );

            if (nome.isBlank() || pai.isBlank()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Informe o nome do componente e o nome do pai."
                );
                return;
            }

            ItemFormula item = new ItemFormula(
                nome, pai, quantidade, preco
            );

            boolean sucesso = arvore.adicionarComponente(pai, item);

            if (sucesso) {
                JOptionPane.showMessageDialog(
                    this, "Componente cadastrado com sucesso!"
                );

                campoNome.setText("");
                campoPai.setText("");
                campoQuantidade.setText("1");
                campoPreco.setText("");

                atualizarArvore();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível cadastrar. Verifique os dados e o pai informado."
                );
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Quantidade deve ser um número inteiro e preço deve ser numérico."
            );
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void atualizarArvore() {
        StringBuilder texto = new StringBuilder();
        montarTexto(arvore.getRaiz(), texto, 0);
        areaArvore.setText(texto.toString());
    }

    private void montarTexto(
            TreeNode no, StringBuilder texto, int nivel) {

        if (no == null) {
            return;
        }

        ItemFormula item = no.getItem();

        texto.append("  ".repeat(nivel))
             .append(item.getNome())
             .append(" | Qtd: ")
             .append(item.getQuantidade())
             .append(" | R$ ")
             .append(String.format(
                 java.util.Locale.forLanguageTag("pt-BR"),
                 "%.2f",
                 item.getPrecoUnitario()
             ))
             .append("\n");

        for (TreeNode filho : no.getFilhos()) {
            montarTexto(filho, texto, nivel + 1);
        }
    }
    
    private void consultarComponente() {
        String nome = campoNome.getText().trim();

        if (nome.isBlank()) {
            JOptionPane.showMessageDialog(
                this,
                "Digite o nome do componente no campo Nome."
            );
            return;
        }

        ItemFormula item = arvore.consultarComponente(
            arvore.getRaiz(),
            nome
        );

        if (item == null) {
            JOptionPane.showMessageDialog(
                this,
                "Componente não encontrado."
            );
            return;
        }

        String mensagem = "Nome: " + item.getNome()
                + "\nPai: "
                + (item.getNomePai() == null
                    ? "Nenhum"
                    : item.getNomePai())
                + "\nQuantidade: " + item.getQuantidade()
                + String.format(
                    "\nPreço unitário: R$ %.2f",
                    item.getPrecoUnitario()
                );

        JOptionPane.showMessageDialog(
            this,
            mensagem,
            "Dados do componente",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

}
