package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import model.ResultadoGiro;

/**
 * Pop-up (diálogo modal) exibido ao vencer um giro.
 *
 * Mostra o valor total ganho, o maior multiplicador e o detalhamento de cada
 * combinação vencedora (símbolo, multiplicador e prêmio). Segue a paleta do
 * {@link Tema} para manter a identidade visual do jogo.
 */
public class DialogoVitoria extends JDialog {

    public DialogoVitoria(Component parente, ResultadoGiro resultado) {
        super(parente instanceof java.awt.Frame ? (java.awt.Frame) parente : null,
                "Vitória!", true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel conteudo = new JPanel(new BorderLayout(0, 12));
        conteudo.setBackground(Tema.FUNDO);
        conteudo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.DESTAQUE, 3),
                BorderFactory.createEmptyBorder(18, 24, 18, 24)));

        conteudo.add(criarCabecalho(resultado), BorderLayout.NORTH);
        conteudo.add(criarDetalhes(resultado), BorderLayout.CENTER);
        conteudo.add(criarRodape(), BorderLayout.SOUTH);

        setContentPane(conteudo);
        pack();
        setLocationRelativeTo(parente);
    }

    /** Cabeçalho com "VOCÊ GANHOU", o valor total e o maior multiplicador. */
    private JPanel criarCabecalho(ResultadoGiro resultado) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setOpaque(false);

        JLabel titulo = new JLabel("🎉 VOCÊ GANHOU! 🎉", SwingConstants.CENTER);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setForeground(Tema.DESTAQUE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));

        JLabel valor = new JLabel(String.format("R$ %.2f", resultado.getPremio()),
                SwingConstants.CENTER);
        valor.setAlignmentX(Component.CENTER_ALIGNMENT);
        valor.setForeground(Tema.VITORIA);
        valor.setFont(new Font("SansSerif", Font.BOLD, 34));

        JLabel multiplicador = new JLabel(
                "Maior multiplicador: x" + resultado.getMaiorMultiplicador(),
                SwingConstants.CENTER);
        multiplicador.setAlignmentX(Component.CENTER_ALIGNMENT);
        multiplicador.setForeground(Tema.TEXTO);
        multiplicador.setFont(new Font("SansSerif", Font.BOLD, 16));

        painel.add(titulo);
        painel.add(valor);
        painel.add(multiplicador);
        return painel;
    }

    /** Lista detalhada de cada combinação vencedora. */
    private JPanel criarDetalhes(ResultadoGiro resultado) {
        JPanel painel = new JPanel();
        painel.setLayout(new GridLayout(0, 1, 0, 4));
        painel.setBackground(Tema.FUNDO_PAINEL);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDA),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel cabecalho = new JLabel("Combinações vencedoras:");
        cabecalho.setForeground(Tema.TEXTO_SUAVE);
        cabecalho.setFont(new Font("SansSerif", Font.PLAIN, 13));
        painel.add(cabecalho);

        for (ResultadoGiro.Combinacao c : resultado.getCombinacoes()) {
            JLabel linha = new JLabel(String.format(
                    "%s — %s (x%d) = R$ %.2f",
                    c.descricaoLinha, c.simbolo.getNomeExibicao(), c.multiplicador, c.premio));
            linha.setForeground(Tema.TEXTO);
            linha.setFont(new Font("SansSerif", Font.BOLD, 14));
            painel.add(linha);
        }
        return painel;
    }

    /** Rodapé com o botão de fechar. */
    private JPanel criarRodape() {
        JPanel painel = new JPanel();
        painel.setOpaque(false);

        JButton btnFechar = new JButton("Continuar jogando");
        btnFechar.setBackground(Tema.DESTAQUE);
        btnFechar.setForeground(new Color(40, 30, 0));
        btnFechar.setFocusPainted(false);
        btnFechar.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnFechar.addActionListener(e -> dispose());

        painel.add(btnFechar);
        return painel;
    }
}
