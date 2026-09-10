package controller;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import model.Dificuldade;
import model.Jogador;
import model.MotorDoJogo;
import model.ResultadoGiro;
import view.Som;
import view.TelaPrincipal;

/**
 * Controlador do jogo (camada Controller do padrão MVC).
 *
 * Liga a interface gráfica ({@link TelaPrincipal}) às regras de negócio
 * ({@link Jogador} e {@link MotorDoJogo}). Concentra:
 *  - o tratamento dos eventos dos botões e do seletor de dificuldade;
 *  - a animação dos rolos com parada progressiva via {@link javax.swing.Timer};
 *  - o desconto da aposta, o alerta de saldo insuficiente e o cálculo do prêmio;
 *  - os recursos extras: modo AUTO, efeitos sonoros, destaque da linha
 *    vencedora e rodadas grátis (Free Spins).
 *
 * A modularização segue a especificação: cada responsabilidade fica em um
 * método próprio, evitando concentrar tudo no escutador do botão.
 */
public class ControladorJogo {

    /** Intervalo (ms) entre trocas de imagem durante a animação de um rolo. */
    private static final int INTERVALO_TROCA = 80;

    /** Tempos de parada progressiva de cada coluna (ms), conforme a especificação. */
    private static final int[] TEMPO_PARADA_COLUNA = {1000, 1500, 2000};

    /** Passo de ajuste da aposta com os botões +/-. */
    private static final double PASSO_APOSTA = 1.0;

    /** Intervalo entre giros no modo automático (ms). */
    private static final int INTERVALO_AUTO = 2600;

    /** Piscadas do destaque da linha vencedora. */
    private static final int PISCADAS_VITORIA = 6;
    private static final int INTERVALO_PISCADA = 250;

    private final TelaPrincipal tela;
    private Jogador jogador;
    private final MotorDoJogo motor;

    /** Timers de animação de cada coluna (um por coluna). */
    private final Timer[] timersColuna = new Timer[MotorDoJogo.COLUNAS];

    /** Timers responsáveis por parar cada coluna no tempo certo. */
    private final Timer[] timersParada = new Timer[MotorDoJogo.COLUNAS];

    /** Timer que dispara giros automáticos no modo AUTO. */
    private Timer timerAuto;

    /** Timer que faz a linha vencedora piscar. */
    private Timer timerPiscar;

    private int colunasParadas;
    private boolean girando;
    private boolean modoAuto;

    public ControladorJogo(TelaPrincipal tela, Jogador jogador, MotorDoJogo motor) {
        this.tela = tela;
        this.jogador = jogador;
        this.motor = motor;
        inicializar();
    }

    /** Configura estado inicial da tela e registra os escutadores de eventos. */
    private void inicializar() {
        tela.getComboDificuldade().setSelectedItem(motor.getDificuldade());
        atualizarPainelInformacoes();
        tela.atualizarUltimoGanho(0.0);

        tela.getBtnGirar().addActionListener(e -> aoClicarGirar());
        tela.getBtnMaxBet().addActionListener(e -> aoClicarMaxBet());
        tela.getBtnAuto().addActionListener(e -> aoAlternarAuto());
        tela.getBtnAumentarAposta().addActionListener(e -> aoAjustarAposta(PASSO_APOSTA));
        tela.getBtnDiminuirAposta().addActionListener(e -> aoAjustarAposta(-PASSO_APOSTA));
        tela.getComboDificuldade().addActionListener(e -> aoTrocarDificuldade());
    }

    /** Atualiza os rótulos de saldo, aposta e rodadas grátis na tela. */
    private void atualizarPainelInformacoes() {
        tela.atualizarSaldo(jogador.getSaldo());
        tela.atualizarAposta(jogador.getAposta());
        tela.atualizarRodadasGratis(jogador.getRodadasGratis());
    }

    // ---- Eventos ----

    private void aoAjustarAposta(double passo) {
        if (girando) {
            return;
        }
        jogador.ajustarAposta(passo);
        atualizarPainelInformacoes();
    }

    private void aoClicarMaxBet() {
        if (girando) {
            return;
        }
        jogador.apostarMaximo();
        atualizarPainelInformacoes();
    }

    /**
     * Troca a dificuldade: reinicia a partida (novo saldo) para valer o novo
     * nível. Pergunta ao jogador para evitar reinício acidental.
     */
    private void aoTrocarDificuldade() {
        if (girando) {
            return;
        }
        Dificuldade escolhida = (Dificuldade) tela.getComboDificuldade().getSelectedItem();
        if (escolhida == motor.getDificuldade()) {
            return;
        }
        int opcao = JOptionPane.showConfirmDialog(tela,
                "Trocar a dificuldade reinicia o jogo com um novo saldo.\nDeseja continuar?",
                "Alterar dificuldade",
                JOptionPane.YES_NO_OPTION);
        if (opcao == JOptionPane.YES_OPTION) {
            pararAuto();
            motor.setDificuldade(escolhida);
            jogador = new Jogador(escolhida);
            tela.limparDestaques();
            tela.atualizarUltimoGanho(0.0);
            tela.exibirMensagem("Dificuldade: " + escolhida.getRotulo() + ". Boa sorte!");
            atualizarPainelInformacoes();
        } else {
            // desfaz a seleção visual
            tela.getComboDificuldade().setSelectedItem(motor.getDificuldade());
        }
    }

    /** Liga/desliga o modo automático de giros. */
    private void aoAlternarAuto() {
        if (modoAuto) {
            pararAuto();
            return;
        }
        modoAuto = true;
        tela.setTextoBotaoAuto("PARAR");
        // dispara o primeiro giro imediatamente e agenda os próximos
        if (!girando) {
            aoClicarGirar();
        }
        timerAuto = new Timer(INTERVALO_AUTO, e -> {
            if (!girando && jogador.podeGirar()) {
                aoClicarGirar();
            } else if (!jogador.podeGirar()) {
                pararAuto();
            }
        });
        timerAuto.start();
    }

    /** Interrompe o modo automático. */
    private void pararAuto() {
        modoAuto = false;
        tela.setTextoBotaoAuto("AUTO");
        if (timerAuto != null) {
            timerAuto.stop();
        }
    }

    /**
     * Trata o clique em GIRAR: valida saldo/rodadas, debita a aposta (quando
     * pago) e inicia a animação.
     */
    private void aoClicarGirar() {
        if (girando) {
            return;
        }
        if (!jogador.podeGirar()) {
            pararAuto();
            JOptionPane.showMessageDialog(tela,
                    "Créditos insuficientes para esta aposta!\n"
                            + "Saldo atual: R$ " + String.format("%.2f", jogador.getSaldo()),
                    "Saldo insuficiente",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        tela.limparDestaques();
        pararPiscar();

        boolean rodadaGratis = jogador.temRodadasGratis();
        if (rodadaGratis) {
            jogador.consumirRodadaGratis();
            tela.exibirMensagem("Rodada GRÁTIS! Girando...");
        } else {
            jogador.debitarAposta();
            tela.exibirMensagem("Girando...");
        }

        atualizarPainelInformacoes();
        Som.tocarGiro();
        motor.sortearGrade();
        iniciarAnimacao();
    }

    // ---- Animação com Timer ----

    private void iniciarAnimacao() {
        girando = true;
        colunasParadas = 0;
        tela.getBtnGirar().setEnabled(false);

        for (int coluna = 0; coluna < MotorDoJogo.COLUNAS; coluna++) {
            iniciarAnimacaoColuna(coluna);
        }
    }

    /** Configura e inicia os timers de uma coluna específica. */
    private void iniciarAnimacaoColuna(final int coluna) {
        timersColuna[coluna] = new Timer(INTERVALO_TROCA, e -> {
            for (int linha = 0; linha < MotorDoJogo.LINHAS; linha++) {
                tela.exibirIcone(linha, coluna, motor.sortearSimbolo());
            }
        });
        timersColuna[coluna].start();

        timersParada[coluna] = new Timer(TEMPO_PARADA_COLUNA[coluna], e -> pararColuna(coluna));
        timersParada[coluna].setRepeats(false);
        timersParada[coluna].start();
    }

    /** Para a animação de uma coluna e exibe os símbolos sorteados definitivos. */
    private void pararColuna(int coluna) {
        timersColuna[coluna].stop();
        for (int linha = 0; linha < MotorDoJogo.LINHAS; linha++) {
            tela.exibirIcone(linha, coluna, motor.getSimbolo(linha, coluna));
        }
        colunasParadas++;
        if (colunasParadas == MotorDoJogo.COLUNAS) {
            finalizarGiro();
        }
    }

    /** Ao parar todas as colunas: avalia, credita prêmio/free spins e atualiza a tela. */
    private void finalizarGiro() {
        ResultadoGiro resultado = motor.avaliarGiro(jogador.getAposta());

        if (resultado.houveVitoria()) {
            jogador.creditarPremio(resultado.getPremio());
            Som.tocarVitoria();
            piscarVencedoras(resultado);
            tela.exibirMensagem(String.format("VITÓRIA! Você ganhou R$ %.2f", resultado.getPremio()));
        } else {
            tela.exibirMensagem("Não foi dessa vez. Tente novamente!");
        }

        if (resultado.getRodadasGratis() > 0) {
            jogador.adicionarRodadasGratis(resultado.getRodadasGratis());
            Som.tocarBonus();
            tela.exibirMensagem(String.format(
                    "BÔNUS! %d rodadas grátis conquistadas!", resultado.getRodadasGratis()));
        }

        tela.atualizarUltimoGanho(resultado.getPremio());
        atualizarPainelInformacoes();

        girando = false;
        tela.getBtnGirar().setEnabled(true);

        if (!jogador.podeGirar()) {
            pararAuto();
            JOptionPane.showMessageDialog(tela,
                    "Seus créditos acabaram! Obrigado por jogar.",
                    "Fim dos créditos",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ---- Destaque da linha vencedora (piscar) ----

    /** Faz as células vencedoras piscarem algumas vezes. */
    private void piscarVencedoras(ResultadoGiro resultado) {
        pararPiscar();
        final boolean[] aceso = {true};
        final int[] contador = {0};
        timerPiscar = new Timer(INTERVALO_PISCADA, e -> {
            aceso[0] = !aceso[0];
            for (ResultadoGiro.Posicao p : resultado.getPosicoesVencedoras()) {
                tela.destacarCelula(p.linha, p.coluna, aceso[0]);
            }
            contador[0]++;
            if (contador[0] >= PISCADAS_VITORIA) {
                pararPiscar();
                // deixa aceso ao final
                for (ResultadoGiro.Posicao p : resultado.getPosicoesVencedoras()) {
                    tela.destacarCelula(p.linha, p.coluna, true);
                }
            }
        });
        timerPiscar.start();
    }

    private void pararPiscar() {
        if (timerPiscar != null) {
            timerPiscar.stop();
        }
    }
}
