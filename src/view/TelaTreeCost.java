package view;

import model.ItemFormula;
import model.TreeNode;
import service.ArvoreFormula;

import javax.swing.*;
import java.awt.*;

public class TelaTreeCost extends JFrame {
    private final ArvoreFormula arvore;

    private final JTextField campoNome = new JTextField(20);
    private final JTextField campoPai = new JTextField(20);
    private final JTextField campoQuantidade = new JTextField("1", 20);
    private final JTextField campoPreco = new JTextField(20);

    private final JTextArea areaArvore = new JTextArea();

    public TelaTreeCost(ArvoreFormula arvore) {
        this.arvore = arvore;

        setTitle("TreeCost - Fórmula de Produtos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(15, 15, 5, 15),
            BorderFactory.createTitledBorder(" Gestão de Componentes ")
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        painelFormulario.add(new JLabel("Nome do componente:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        painelFormulario.add(campoNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        painelFormulario.add(new JLabel("Nome do pai:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        painelFormulario.add(campoPai, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
        painelFormulario.add(new JLabel("Quantidade:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        painelFormulario.add(campoQuantidade, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0;
        painelFormulario.add(new JLabel("Preço unitário (R$):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        painelFormulario.add(campoPreco, gbc);

        JButton botaoCadastrar = new JButton("Cadastrar Componente");
        JButton botaoAtualizar = new JButton("Atualizar Árvore");
        JButton botaoConsultar = new JButton("Consultar Componente");

        botaoCadastrar.setBackground(new Color(230, 240, 255));
        botaoAtualizar.setBackground(new Color(240, 240, 240));
        botaoConsultar.setBackground(new Color(240, 240, 240));

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        painelBotoes.add(botaoCadastrar);
        painelBotoes.add(botaoAtualizar);
        painelBotoes.add(botaoConsultar);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        painelFormulario.add(painelBotoes, gbc);

        areaArvore.setEditable(false);
        areaArvore.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        areaArvore.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPane = new JScrollPane(areaArvore);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 15, 15, 15),
            BorderFactory.createTitledBorder(" Visualização da Árvore (Explosão e Custos) ")
        ));

        add(painelFormulario, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        botaoCadastrar.addActionListener(e -> cadastrarComponente());
        botaoAtualizar.addActionListener(e -> atualizarArvore());
        botaoConsultar.addActionListener(e -> consultarComponente());

        atualizarArvore();
    }

    private void cadastrarComponente() {
        try {
            String nome = campoNome.getText().trim();
            String pai = campoPai.getText().trim();
            int quantidade = Integer.parseInt(campoQuantidade.getText().trim());
            double preco = Double.parseDouble(campoPreco.getText().trim().replace(",", "."));

            if (nome.isBlank() || pai.isBlank()) {
                JOptionPane.showMessageDialog(this, "Informe o nome do componente e o nome do pai válido.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ItemFormula item = new ItemFormula(nome, pai, quantidade, preco);
            boolean sucesso = arvore.adicionarComponente(pai, item);

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Componente cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                campoNome.setText("");
                campoPai.setText("");
                campoQuantidade.setText("1");
                campoPreco.setText("");
                atualizarArvore();
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível cadastrar. Certifique-se de que o 'Nome do pai' existe na árvore.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "A quantidade deve ser inteira e o preço deve ser um número válido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarArvore() {
        StringBuilder texto = new StringBuilder();
        montarTexto(arvore.getRaiz(), texto, 0);
        areaArvore.setText(texto.toString());
    }

    private void montarTexto(TreeNode no, StringBuilder texto, int nivel) {
        if (no == null) {
            return;
        }

        ItemFormula item = no.getItem();
        String indentacao = "    ".repeat(nivel);
        String simbolo = nivel == 0 ? "📁 " : " └── ";

        texto.append(indentacao)
             .append(simbolo)
             .append(item.getNome())
             .append(" | Qtd: ")
             .append(item.getQuantidade())
             .append(" | R$ ")
             .append(String.format(java.util.Locale.forLanguageTag("pt-BR"), "%.2f", item.getPrecoUnitario()))
             .append("\n");

        for (TreeNode filho : no.getFilhos()) {
            montarTexto(filho, texto, nivel + 1);
        }
    }

    private void consultarComponente() {
        String nome = campoNome.getText().trim();

        if (nome.isBlank()) {
            JOptionPane.showMessageDialog(this, "Digite o nome do componente no campo 'Nome do componente' para consultar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ItemFormula item = arvore.consultarComponente(arvore.getRaiz(), nome);

        if (item == null) {
            JOptionPane.showMessageDialog(this, "Componente não encontrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String mensagem = "Nome: " + item.getNome()
                + "\nPai: " + (item.getNomePai() == null ? "Nenhum (Raiz)" : item.getNomePai())
                + "\nQuantidade: " + item.getQuantidade()
                + String.format("\nPreço unitário: R$ %.2f", item.getPrecoUnitario());

        JOptionPane.showMessageDialog(this, mensagem, "Detalhes do Componente", JOptionPane.INFORMATION_MESSAGE);
    }
}