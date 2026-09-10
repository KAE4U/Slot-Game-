package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import model.Dificuldade;
import model.MotorDoJogo;
import model.Simbolo;

/**
 * Janela principal do jogo (camada de View).
 *
 * Monta toda a interface gráfica usando BorderLayout (composição) associado a
 * subpainéis em GridLayout/BoxLayout — conforme a especificação, sem uso de
 * setLayout(null). A paleta é definida em {@link Tema} (cinza-carvão + branco,
 * mantendo a identidade alvinegra, porém mais suave).
 *
 * Esta classe apenas EXIBE e disponibiliza os componentes; a lógica de
 * eventos/animação fica no ControladorJogo.
 */
public class TelaPrincipal extends JFrame {

    private static final int TAMANHO_CELULA = 130;

    // Componentes com nomenclatura semântica (acessados pelo controlador)
    private final CelulaSimbolo[][] celulas = new CelulaSimbolo[MotorDoJogo.LINHAS][MotorDoJogo.COLUNAS];
    private final JButton btnGirar = new JButton("GIRAR");
    private final JButton btnMaxBet = new JButton("MAX BET");
    private final JButton btnAuto = new JButton("AUTO");
    private final JButton btnAumentarAposta = new JButton("+");
    private final JButton btnDiminuirAposta = new JButton("−");
    private final JComboBox<Dificuldade> comboDificuldade = new JComboBox<>(Dificuldade.values());
    private final JLabel lblSaldo = new JLabel();
    private final JLabel lblAposta = new JLabel();
    private final JLabel lblUltimoGanho = new JLabel();
    private final JLabel lblRodadasGratis = new JLabel();
    private final JLabel lblMensagem = new JLabel("Boa sorte!", SwingConstants.CENTER);

    private final CarregadorImagens carregadorImagens;

    public TelaPrincipal() {
        super("Gavião da Fiel Slots - Ciência da Computação");
        this.carregadorImagens = new CarregadorImagens();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));
        getContentPane().setBackground(Tema.FUNDO);

        add(criarPainelTitulo(), BorderLayout.NORTH);
        add(criarPainelRolos(), BorderLayout.CENTER);
        add(criarPainelInferior(), BorderLayout.SOUTH);
        add(criarPainelPagamentos(), BorderLayout.EAST);

        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null); // centraliza na tela
    }

    /** Painel com fundo em gradiente (usado no topo e no rodapé). */
    private JPanel criarPainelGradiente() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                Graphics2D g = (Graphics2D) graphics;
                g.setPaint(new GradientPaint(0, 0, Tema.GRADIENTE_TOPO,
                        0, getHeight(), Tema.GRADIENTE_BASE));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }

    /** Painel superior com o título do jogo e o saldo. */
    private JPanel criarPainelTitulo() {
        JPanel painel = criarPainelGradiente();
        painel.setLayout(new BorderLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel titulo = new JLabel("GAVIÃO DA FIEL SLOTS", SwingConstants.CENTER);
        titulo.setFont(new Font("Serif", Font.BOLD, 36));
        titulo.setForeground(Tema.TEXTO);
        painel.add(titulo, BorderLayout.CENTER);

        // Saldo em destaque no topo direito
        JPanel painelSaldo = new JPanel(new BorderLayout());
        painelSaldo.setOpaque(false);
        JLabel rotuloSaldo = new JLabel("SALDO", SwingConstants.CENTER);
        rotuloSaldo.setForeground(Tema.TEXTO_SUAVE);
        rotuloSaldo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSaldo.setHorizontalAlignment(SwingConstants.CENTER);
        lblSaldo.setForeground(Tema.DESTAQUE);
        lblSaldo.setFont(new Font("SansSerif", Font.BOLD, 24));
        painelSaldo.add(rotuloSaldo, BorderLayout.NORTH);
        painelSaldo.add(lblSaldo, BorderLayout.CENTER);
        painel.add(painelSaldo, BorderLayout.EAST);

        return painel;
    }

    /** Painel central com a grade 3x3 de símbolos (GridLayout). */
    private JPanel criarPainelRolos() {
        JPanel painelExterno = new JPanel(new BorderLayout());
        painelExterno.setBackground(Tema.FUNDO);
        painelExterno.setBorder(BorderFactory.createEmptyBorder(5, 22, 5, 22));

        JPanel grade = new JPanel(new GridLayout(
                MotorDoJogo.LINHAS, MotorDoJogo.COLUNAS, 10, 10));
        grade.setBackground(Tema.BRANCO_SUAVE);
        grade.setBorder(BorderFactory.createLineBorder(Tema.BRANCO_SUAVE, 8));

        Simbolo[] valores = Simbolo.values();
        for (int linha = 0; linha < MotorDoJogo.LINHAS; linha++) {
            for (int coluna = 0; coluna < MotorDoJogo.COLUNAS; coluna++) {
                Simbolo inicial = valores[(linha + coluna) % valores.length];
                CelulaSimbolo celula = new CelulaSimbolo(carregadorImagens, inicial);
                celula.setPreferredSize(new Dimension(TAMANHO_CELULA, TAMANHO_CELULA));
                celulas[linha][coluna] = celula;
                grade.add(celula);
            }
        }

        painelExterno.add(grade, BorderLayout.CENTER);

        // Mensagem de status abaixo da grade
        lblMensagem.setForeground(Tema.TEXTO);
        lblMensagem.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblMensagem.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        painelExterno.add(lblMensagem, BorderLayout.SOUTH);

        return painelExterno;
    }

    /** Painel inferior com informações de aposta/saldo e botões de ação. */
    private JPanel criarPainelInferior() {
        JPanel painel = criarPainelGradiente();
        painel.setLayout(new GridLayout(1, 5, 10, 0));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 16, 16, 16));

        // Bloco de aposta atual (+/-)
        JPanel painelAposta = criarBlocoInfo("Aposta Atual", lblAposta);
        JPanel painelBotoesAposta = new JPanel(new GridLayout(1, 2, 6, 0));
        painelBotoesAposta.setOpaque(false);
        estilizarBotao(btnDiminuirAposta);
        estilizarBotao(btnAumentarAposta);
        painelBotoesAposta.add(btnDiminuirAposta);
        painelBotoesAposta.add(btnAumentarAposta);
        painelAposta.add(painelBotoesAposta);

        // Botão girar (destaque)
        estilizarBotao(btnGirar);
        btnGirar.setFont(new Font("SansSerif", Font.BOLD, 26));
        btnGirar.setBackground(Tema.DESTAQUE);
        btnGirar.setForeground(new Color(40, 30, 0));

        // Botão AUTO e MAX BET empilhados
        JPanel painelAcoes = new JPanel(new GridLayout(2, 1, 0, 6));
        painelAcoes.setOpaque(false);
        estilizarBotao(btnAuto);
        estilizarBotao(btnMaxBet);
        btnAuto.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnMaxBet.setFont(new Font("SansSerif", Font.BOLD, 16));
        painelAcoes.add(btnAuto);
        painelAcoes.add(btnMaxBet);

        // Bloco último ganho
        JPanel painelGanho = criarBlocoInfo("Último Ganho", lblUltimoGanho);

        // Bloco de rodadas grátis
        JPanel painelFree = criarBlocoInfo("Rodadas Grátis", lblRodadasGratis);
        lblRodadasGratis.setForeground(Tema.VITORIA);

        painel.add(painelAposta);
        painel.add(btnGirar);
        painel.add(painelAcoes);
        painel.add(painelGanho);
        painel.add(painelFree);

        return painel;
    }

    /** Painel lateral direito com dificuldade + tabela de pagamentos. */
    private JPanel criarPainelPagamentos() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Tema.FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 16));

        // Seletor de dificuldade
        JPanel painelDif = new JPanel(new BorderLayout());
        painelDif.setBackground(Tema.FUNDO);
        painelDif.setBorder(tituloBorda("Dificuldade"));
        comboDificuldade.setBackground(Tema.FUNDO_PAINEL);
        comboDificuldade.setForeground(Tema.TEXTO);
        painelDif.add(comboDificuldade, BorderLayout.CENTER);
        painelDif.setAlignmentX(LEFT_ALIGNMENT);
        painel.add(painelDif);
        painel.add(Box.createVerticalStrut(12));

        // Tabela de pagamentos
        JPanel painelPag = new JPanel();
        painelPag.setLayout(new BoxLayout(painelPag, BoxLayout.Y_AXIS));
        painelPag.setBackground(Tema.FUNDO);
        painelPag.setBorder(tituloBorda("Tabela de Pagamentos"));
        for (Simbolo simbolo : Simbolo.values()) {
            JLabel linha = new JLabel(String.format(
                    "3x %s = x%d", simbolo.getNomeExibicao(), simbolo.getMultiplicador()));
            linha.setForeground(Tema.TEXTO);
            linha.setFont(new Font("SansSerif", Font.PLAIN, 13));
            linha.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            painelPag.add(linha);
        }
        JLabel bonus = new JLabel("3x Gavião = 5 Rodadas Grátis");
        bonus.setForeground(Tema.VITORIA);
        bonus.setFont(new Font("SansSerif", Font.BOLD, 12));
        bonus.setBorder(BorderFactory.createEmptyBorder(6, 4, 4, 4));
        painelPag.add(bonus);
        painelPag.setAlignmentX(LEFT_ALIGNMENT);
        painel.add(painelPag);

        painel.add(Box.createVerticalGlue());
        return painel;
    }

    /** Cria uma borda com título estilizada no tema. */
    private TitledBorder tituloBorda(String titulo) {
        TitledBorder borda = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Tema.BORDA), titulo);
        borda.setTitleColor(Tema.TEXTO);
        return borda;
    }

    /** Cria um bloco vertical com um rótulo fixo e um valor dinâmico. */
    private JPanel criarBlocoInfo(String rotulo, JLabel valor) {
        JPanel bloco = new JPanel();
        bloco.setLayout(new BoxLayout(bloco, BoxLayout.Y_AXIS));
        bloco.setBackground(Tema.FUNDO_PAINEL);
        bloco.setBorder(BorderFactory.createLineBorder(Tema.BORDA));

        JLabel titulo = new JLabel(rotulo, SwingConstants.CENTER);
        titulo.setAlignmentX(CENTER_ALIGNMENT);
        titulo.setForeground(Tema.TEXTO_SUAVE);
        titulo.setFont(new Font("SansSerif", Font.PLAIN, 13));

        valor.setAlignmentX(CENTER_ALIGNMENT);
        valor.setForeground(Tema.TEXTO);
        valor.setFont(new Font("SansSerif", Font.BOLD, 18));

        bloco.add(Box.createVerticalStrut(6));
        bloco.add(titulo);
        bloco.add(valor);
        bloco.add(Box.createVerticalStrut(6));
        return bloco;
    }

    /** Aplica o visual padrão a um botão. */
    private void estilizarBotao(JButton botao) {
        botao.setBackground(Tema.BRANCO_SUAVE);
        botao.setForeground(new Color(30, 30, 30));
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        botao.setFont(new Font("SansSerif", Font.BOLD, 16));
    }

    // ---- Métodos usados pelo controlador para atualizar a tela ----

    public void exibirIcone(int linha, int coluna, Simbolo simbolo) {
        celulas[linha][coluna].setSimbolo(simbolo);
    }

    public void destacarCelula(int linha, int coluna, boolean destacada) {
        celulas[linha][coluna].setDestacada(destacada);
    }

    public void limparDestaques() {
        for (int linha = 0; linha < MotorDoJogo.LINHAS; linha++) {
            for (int coluna = 0; coluna < MotorDoJogo.COLUNAS; coluna++) {
                celulas[linha][coluna].setDestacada(false);
            }
        }
    }

    public JButton getBtnGirar() {
        return btnGirar;
    }

    public JButton getBtnMaxBet() {
        return btnMaxBet;
    }

    public JButton getBtnAuto() {
        return btnAuto;
    }

    public JButton getBtnAumentarAposta() {
        return btnAumentarAposta;
    }

    public JButton getBtnDiminuirAposta() {
        return btnDiminuirAposta;
    }

    public JComboBox<Dificuldade> getComboDificuldade() {
        return comboDificuldade;
    }

    public void setTextoBotaoAuto(String texto) {
        btnAuto.setText(texto);
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

    public void atualizarRodadasGratis(int rodadas) {
        lblRodadasGratis.setText(String.valueOf(rodadas));
    }

    public void exibirMensagem(String mensagem) {
        lblMensagem.setText(mensagem);
    }
}
