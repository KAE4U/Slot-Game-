package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.MotorDoJogo;
import model.Simbolo;

/**
 * Janela principal do jogo (camada de View).
 *
 * Monta toda a interface gráfica em preto e branco usando BorderLayout
 * (composição) associado a subpainéis em GridLayout — conforme a
 * especificação, sem uso de setLayout(null).
 *
 * Esta classe apenas EXIBE e disponibiliza os componentes; a lógica de
 * eventos/animação fica no ControladorJogo.
 */
public class TelaPrincipal extends JFrame {

    private static final Color COR_FUNDO = Color.BLACK;
    private static final Color COR_TEXTO = Color.WHITE;
    private static final int TAMANHO_SIMBOLO = 110;

    // Componentes com nomenclatura semântica (acessados pelo controlador)
    private final JLabel[][] celulas = new JLabel[MotorDoJogo.LINHAS][MotorDoJogo.COLUNAS];
    private final JButton btnGirar = new JButton("GIRAR");
    private final JButton btnMaxBet = new JButton("MAX BET");
    private final JButton btnAumentarAposta = new JButton("+");
    private final JButton btnDiminuirAposta = new JButton("-");
    private final JLabel lblSaldo = new JLabel();
    private final JLabel lblAposta = new JLabel();
    private final JLabel lblUltimoGanho = new JLabel();
    private final JLabel lblMensagem = new JLabel("Boa sorte!", SwingConstants.CENTER);

    private final CarregadorImagens carregadorImagens;

    public TelaPrincipal() {
        super("Gavião da Fiel Slots - Ciência da Computação");
        this.carregadorImagens = new CarregadorImagens(TAMANHO_SIMBOLO);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(COR_FUNDO);

        add(criarPainelTitulo(), BorderLayout.NORTH);
        add(criarPainelRolos(), BorderLayout.CENTER);
        add(criarPainelInferior(), BorderLayout.SOUTH);
        add(criarPainelPagamentos(), BorderLayout.EAST);

        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null); // centraliza na tela
    }

    /** Painel superior com o título do jogo. */
    private JPanel criarPainelTitulo() {
        JPanel painel = new JPanel();
        painel.setBackground(COR_FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        painel.setLayout(new BorderLayout());

        JLabel titulo = new JLabel("GAVIÃO DA FIEL SLOTS", SwingConstants.CENTER);
        titulo.setFont(new Font("Serif", Font.BOLD, 34));
        titulo.setForeground(COR_TEXTO);
        painel.add(titulo, BorderLayout.CENTER);

        // Saldo em destaque no topo direito
        JPanel painelSaldo = new JPanel(new BorderLayout());
        painelSaldo.setBackground(COR_FUNDO);
        JLabel rotuloSaldo = new JLabel("SALDO", SwingConstants.CENTER);
        rotuloSaldo.setForeground(COR_TEXTO);
        rotuloSaldo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSaldo.setHorizontalAlignment(SwingConstants.CENTER);
        lblSaldo.setForeground(new Color(255, 200, 0));
        lblSaldo.setFont(new Font("SansSerif", Font.BOLD, 22));
        painelSaldo.add(rotuloSaldo, BorderLayout.NORTH);
        painelSaldo.add(lblSaldo, BorderLayout.CENTER);
        painel.add(painelSaldo, BorderLayout.EAST);

        return painel;
    }

    /** Painel central com a grade 3x3 de símbolos (GridLayout). */
    private JPanel criarPainelRolos() {
        JPanel painelExterno = new JPanel(new BorderLayout());
        painelExterno.setBackground(COR_FUNDO);
        painelExterno.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        JPanel grade = new JPanel(new GridLayout(
                MotorDoJogo.LINHAS, MotorDoJogo.COLUNAS, 8, 8));
        grade.setBackground(Color.WHITE);
        grade.setBorder(BorderFactory.createLineBorder(Color.WHITE, 6));

        Simbolo[] valores = Simbolo.values();
        for (int linha = 0; linha < MotorDoJogo.LINHAS; linha++) {
            for (int coluna = 0; coluna < MotorDoJogo.COLUNAS; coluna++) {
                JLabel celula = new JLabel();
                celula.setHorizontalAlignment(SwingConstants.CENTER);
                celula.setOpaque(true);
                celula.setBackground(Color.BLACK);
                celula.setPreferredSize(new Dimension(
                        TAMANHO_SIMBOLO + 20, TAMANHO_SIMBOLO + 20));
                // símbolo inicial só para exibição
                celula.setIcon(carregadorImagens.getIcone(valores[(linha + coluna) % valores.length]));
                celulas[linha][coluna] = celula;
                grade.add(celula);
            }
        }

        painelExterno.add(grade, BorderLayout.CENTER);

        // Mensagem de status abaixo da grade
        lblMensagem.setForeground(COR_TEXTO);
        lblMensagem.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblMensagem.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        painelExterno.add(lblMensagem, BorderLayout.SOUTH);

        return painelExterno;
    }

    /** Painel inferior com informações de aposta/saldo e botões de ação. */
    private JPanel criarPainelInferior() {
        JPanel painel = new JPanel(new GridLayout(1, 4, 10, 0));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        // Bloco de aposta atual (+/-)
        JPanel painelAposta = criarBlocoInfo("Aposta Atual", lblAposta);
        JPanel painelBotoesAposta = new JPanel(new GridLayout(1, 2, 5, 0));
        painelBotoesAposta.setBackground(COR_FUNDO);
        estilizarBotao(btnDiminuirAposta);
        estilizarBotao(btnAumentarAposta);
        painelBotoesAposta.add(btnDiminuirAposta);
        painelBotoesAposta.add(btnAumentarAposta);
        painelAposta.add(painelBotoesAposta);

        // Botão girar (destaque)
        estilizarBotao(btnGirar);
        btnGirar.setFont(new Font("SansSerif", Font.BOLD, 28));
        btnGirar.setForeground(new Color(255, 200, 0));

        // Botão max bet
        estilizarBotao(btnMaxBet);
        btnMaxBet.setFont(new Font("SansSerif", Font.BOLD, 18));

        // Bloco último ganho
        JPanel painelGanho = criarBlocoInfo("Último Ganho", lblUltimoGanho);

        painel.add(painelAposta);
        painel.add(btnGirar);
        painel.add(btnMaxBet);
        painel.add(painelGanho);

        return painel;
    }

    /** Painel lateral direito com a tabela de pagamentos (multiplicadores). */
    private JPanel criarPainelPagamentos() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 5, 10, 15),
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(COR_TEXTO),
                        "Tabela de Pagamentos")));

        // Cada linha: nome do símbolo + multiplicador
        for (Simbolo simbolo : Simbolo.values()) {
            JLabel linha = new JLabel(String.format(
                    "3x %s = x%d", simbolo.getNomeExibicao(), simbolo.getMultiplicador()));
            linha.setForeground(COR_TEXTO);
            linha.setFont(new Font("SansSerif", Font.PLAIN, 13));
            linha.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            painel.add(linha);
        }
        painel.add(Box.createVerticalGlue());
        return painel;
    }

    /** Cria um bloco vertical com um rótulo fixo e um valor dinâmico. */
    private JPanel criarBlocoInfo(String rotulo, JLabel valor) {
        JPanel bloco = new JPanel();
        bloco.setLayout(new BoxLayout(bloco, BoxLayout.Y_AXIS));
        bloco.setBackground(COR_FUNDO);
        bloco.setBorder(BorderFactory.createLineBorder(COR_TEXTO));

        JLabel titulo = new JLabel(rotulo, SwingConstants.CENTER);
        titulo.setAlignmentX(CENTER_ALIGNMENT);
        titulo.setForeground(COR_TEXTO);
        titulo.setFont(new Font("SansSerif", Font.PLAIN, 13));

        valor.setAlignmentX(CENTER_ALIGNMENT);
        valor.setForeground(COR_TEXTO);
        valor.setFont(new Font("SansSerif", Font.BOLD, 18));

        bloco.add(Box.createVerticalStrut(4));
        bloco.add(titulo);
        bloco.add(valor);
        bloco.add(Box.createVerticalStrut(4));
        return bloco;
    }

    /** Aplica o visual preto e branco padrão a um botão. */
    private void estilizarBotao(JButton botao) {
        botao.setBackground(Color.WHITE);
        botao.setForeground(Color.BLACK);
        botao.setFocusPainted(false);
        botao.setFont(new Font("SansSerif", Font.BOLD, 16));
    }

    // ---- Métodos usados pelo controlador para atualizar a tela ----

    public JLabel getCelula(int linha, int coluna) {
        return celulas[linha][coluna];
    }

    public void exibirIcone(int linha, int coluna, Simbolo simbolo) {
        celulas[linha][coluna].setIcon(carregadorImagens.getIcone(simbolo));
    }

    public JButton getBtnGirar() {
        return btnGirar;
    }

    public JButton getBtnMaxBet() {
        return btnMaxBet;
    }

    public JButton getBtnAumentarAposta() {
        return btnAumentarAposta;
    }

    public JButton getBtnDiminuirAposta() {
        return btnDiminuirAposta;
    }

    public void atualizarSaldo(double saldo) {
        lblSaldo.setText(String.format("R$ %.2f", saldo));
    }

    public void atualizarAposta(double aposta) {
        lblAposta.setText(String.format("R$ %.2f", aposta));
    }

    public void atualizarUltimoGanho(double ganho) {
        lblUltimoGanho.setText(String.format("R$ %.2f", ganho));
    }

    public void exibirMensagem(String mensagem) {
        lblMensagem.setText(mensagem);
    }

    public JLabel getLblSaldo() {
        return lblSaldo;
    }
}
