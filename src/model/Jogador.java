package model;

/**
 * Representa o jogador e seu saldo de créditos de demonstração.
 *
 * Responsabilidades:
 *  - guardar o saldo atual, o valor da aposta e as rodadas grátis;
 *  - validar se há créditos suficientes para girar;
 *  - debitar a aposta e creditar prêmios.
 *
 * A classe NÃO conhece a interface gráfica — apenas as regras de saldo.
 */
public class Jogador {

    /** Valor mínimo e máximo de aposta permitidos. */
    public static final double APOSTA_MINIMA = 1.0;
    public static final double APOSTA_MAXIMA = 50.0;

    private double saldo;
    private double aposta;
    private int rodadasGratis;

    public Jogador(Dificuldade dificuldade) {
        this.saldo = dificuldade.getSaldoInicial();
        this.aposta = APOSTA_MINIMA * 5; // aposta inicial de R$ 5,00
    }

    public double getSaldo() {
        return saldo;
    }

    public double getAposta() {
        return aposta;
    }

    public int getRodadasGratis() {
        return rodadasGratis;
    }

    /**
     * Define o valor da aposta, respeitando os limites mínimo e máximo.
     */
    public void setAposta(double novaAposta) {
        if (novaAposta < APOSTA_MINIMA) {
            novaAposta = APOSTA_MINIMA;
        } else if (novaAposta > APOSTA_MAXIMA) {
            novaAposta = APOSTA_MAXIMA;
        }
        this.aposta = novaAposta;
    }

    /** Ajusta a aposta em passos (positivo aumenta, negativo diminui). */
    public void ajustarAposta(double passo) {
        setAposta(this.aposta + passo);
    }

    /** Define a aposta como o maior valor possível dado o saldo (Max Bet). */
    public void apostarMaximo() {
        double maximo = Math.min(APOSTA_MAXIMA, saldo);
        if (maximo < APOSTA_MINIMA) {
            maximo = APOSTA_MINIMA;
        }
        setAposta(maximo);
    }

    /** Indica se o jogador possui rodadas grátis disponíveis. */
    public boolean temRodadasGratis() {
        return rodadasGratis > 0;
    }

    /** Adiciona rodadas grátis (bônus de Free Spins). */
    public void adicionarRodadasGratis(int quantidade) {
        this.rodadasGratis += quantidade;
    }

    /** Consome uma rodada grátis. */
    public void consumirRodadaGratis() {
        if (rodadasGratis > 0) {
            rodadasGratis--;
        }
    }

    /**
     * Verifica se o jogador pode girar: tem rodada grátis OU saldo suficiente
     * para cobrir a aposta atual.
     */
    public boolean podeGirar() {
        return temRodadasGratis() || saldo >= aposta;
    }

    /**
     * Debita a aposta do saldo. Deve ser chamado apenas quando o giro é pago
     * (isto é, quando não está usando rodada grátis).
     */
    public void debitarAposta() {
        this.saldo -= aposta;
    }

    /** Credita um prêmio ao saldo. */
    public void creditarPremio(double premio) {
        this.saldo += premio;
    }

    /**
     * Deposita créditos fictícios no saldo.
     *
     * @param valor quantia a adicionar (deve ser positiva)
     * @return {@code true} se o depósito foi realizado; {@code false} se o
     *         valor for inválido (menor ou igual a zero)
     */
    public boolean depositar(double valor) {
        if (valor <= 0) {
            return false;
        }
        this.saldo += valor;
        return true;
    }

    /**
     * Saca (retira) créditos fictícios do saldo.
     *
     * @param valor quantia a retirar (deve ser positiva e não maior que o saldo)
     * @return {@code true} se o saque foi realizado; {@code false} se o valor
     *         for inválido ou maior que o saldo disponível
     */
    public boolean sacar(double valor) {
        if (valor <= 0 || valor > saldo) {
            return false;
        }
        this.saldo -= valor;
        return true;
    }
}
