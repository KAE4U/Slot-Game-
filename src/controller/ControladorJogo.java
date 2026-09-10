package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import model.Jogador;
import model.MotorDoJogo;
import model.Simbolo;
import view.TelaPrincipal;

/**
 * Controlador do jogo (camada Controller do padrão MVC).
 *
 * Liga a interface gráfica ({@link TelaPrincipal}) às regras de negócio
 * ({@link Jogador} e {@link MotorDoJogo}). Concentra:
 *  - o tratamento dos eventos dos botões;
 *  - a animação dos rolos com parada progressiva usando {@link javax.swing.Timer};
 *  - o desconto da aposta, o alerta de saldo insuficiente e o cálculo do prêmio.
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

    private final TelaPrincipal tela;
    private final Jogador jogador;
    private final MotorDoJogo motor;

    /** Timers de animação de cada coluna (um por coluna). */
    private final Timer[] timersColuna = new Timer[MotorDoJogo.COLUNAS];

    /** Timers responsáveis por parar cada coluna no tempo certo. */
    private final Timer[] timersParada = new Timer[MotorDoJogo.COLUNAS];

    /** Quantidade de colunas que já pararam no giro atual. */
    private int colunasParadas;

    /** Indica se um giro está em andamento (bloqueia novos acionamentos). */
    private boolean girando;

    public ControladorJogo(TelaPrincipal tela, Jogador jogador, MotorDoJogo motor) {
        this.tela = tela;
        this.jogador = jogador;
        this.motor = motor;
        inicializar();
    }

    /** Configura estado inicial da tela e registra os escutadores de eventos. */
    private void inicializar() {
        atualizarPainelInformacoes();
        tela.atualizarUltimoGanho(0.0);

        tela.getBtnGirar().addActionListener(e -> aoClicarGirar());
        tela.getBtnMaxBet().addActionListener(e -> aoClicarMaxBet());
        tela.getBtnAumentarAposta().addActionListener(e -> aoAjustarAposta(PASSO_APOSTA));
        tela.getBtnDiminuirAposta().addActionListener(e -> aoAjustarAposta(-PASSO_APOSTA));
    }

    /** Atualiza os rótulos de saldo e aposta na tela. */
    private void atualizarPainelInformacoes() {
        tela.atualizarSaldo(jogador.getSaldo());
        tela.atualizarAposta(jogador.getAposta());
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
     * Trata o clique em GIRAR: valida saldo, debita a aposta e inicia a animação.
     */
    private void aoClicarGirar() {
        if (girando) {
            return;
        }
        if (!jogador.temSaldoSuficiente()) {
            JOptionPane.showMessageDialog(tela,
                    "Créditos insuficientes para esta aposta!\n"
                            + "Saldo atual: R$ " + String.format("%.2f", jogador.getSaldo()),
                    "Saldo insuficiente",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        jogador.debitarAposta();
        atualizarPainelInformacoes();
        tela.exibirMensagem("Girando...");
        motor.sortearGrade();
        iniciarAnimacao();
    }

    // ---- Animação com Timer ----

    /**
     * Inicia a animação de todas as colunas. Cada coluna troca de imagem
     * ciclicamente e para em um tempo diferente (parada progressiva).
     */
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
        // Timer de animação: troca imagens aleatórias rapidamente
        timersColuna[coluna] = new Timer(INTERVALO_TROCA, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int linha = 0; linha < MotorDoJogo.LINHAS; linha++) {
                    tela.exibirIcone(linha, coluna, motor.sortearSimbolo());
                }
            }
        });
        timersColuna[coluna].start();

        // Timer de parada: dispara uma única vez no tempo da coluna
        timersParada[coluna] = new Timer(TEMPO_PARADA_COLUNA[coluna], new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pararColuna(coluna);
            }
        });
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

    /** Ao parar todas as colunas: calcula prêmio, credita e atualiza a tela. */
    private void finalizarGiro() {
        double premio = motor.calcularPremio(jogador.getAposta());
        if (premio > 0) {
            jogador.creditarPremio(premio);
            tela.exibirMensagem(String.format("VITÓRIA! Você ganhou R$ %.2f", premio));
        } else {
            tela.exibirMensagem("Não foi dessa vez. Tente novamente!");
        }
        tela.atualizarUltimoGanho(premio);
        atualizarPainelInformacoes();

        girando = false;
        tela.getBtnGirar().setEnabled(true);

        // Aviso amigável caso o jogador fique sem saldo para a menor aposta
        if (jogador.getSaldo() < Jogador.APOSTA_MINIMA) {
            JOptionPane.showMessageDialog(tela,
                    "Seus créditos acabaram! Obrigado por jogar.",
                    "Fim dos créditos",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
