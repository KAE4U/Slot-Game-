package model;

/**
 * Níveis de dificuldade do jogo.
 *
 * Cada nível ajusta:
 *  - o saldo inicial de créditos de demonstração;
 *  - o "peso" de raridade dos símbolos (quanto maior, mais difícil formar
 *    combinações com os símbolos mais valiosos).
 *
 * Isso permite regular a dificuldade sem alterar a lógica principal do jogo.
 */
public enum Dificuldade {

    FACIL("Fácil", 200.0, 1, 0.60),
    NORMAL("Normal", 100.0, 2, 0.45),
    DIFICIL("Difícil", 60.0, 3, 0.32);

    private final String rotulo;
    private final double saldoInicial;
    private final int pesoRaridade;
    private final double fatorPagamento;

    Dificuldade(String rotulo, double saldoInicial, int pesoRaridade, double fatorPagamento) {
        this.rotulo = rotulo;
        this.saldoInicial = saldoInicial;
        this.pesoRaridade = pesoRaridade;
        this.fatorPagamento = fatorPagamento;
    }

    public String getRotulo() {
        return rotulo;
    }

    public double getSaldoInicial() {
        return saldoInicial;
    }

    public int getPesoRaridade() {
        return pesoRaridade;
    }

    /**
     * Fator aplicado aos prêmios: quanto menor, mais difícil (paga menos).
     * Usado para manter o retorno do jogo abaixo de 100% de forma controlada.
     */
    public double getFatorPagamento() {
        return fatorPagamento;
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
