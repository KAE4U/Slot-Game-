package model;

/**
 * Representa o jogador e seu saldo de créditos de demonstração.
 *
 * Responsabilidades:
 *  - guardar o saldo atual e o valor da aposta;
 *  - validar se há créditos suficientes para girar;
 *  - debitar a aposta e creditar prêmios.
 *
 * A classe NÃO conhece a interface gráfica — apenas as regras de saldo.
 */
public class Jogador {

    /** Saldo inicial padrão de créditos de demonstração. */
    public static final double SALDO_INICIAL = 100.0;

    /** Valor mínimo e máximo de aposta permitidos. */
    public static final double APOSTA_MINIMA = 1.0;
    public static final double APOSTA_MAXIMA = 50.0;

    private double saldo;
    private double aposta;

    public Jogador() {
        this.saldo = SALDO_INICIAL;
        this.aposta = APOSTA_MINIMA * 5; // aposta inicial de R$ 5,00
    }

    public double getSaldo() {
        return saldo;
    }

    public double getAposta() {
        return aposta;
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

    /** Verifica se o jogador tem saldo suficiente para cobrir a aposta atual. */
    public boolean temSaldoSuficiente() {
        return saldo >= aposta;
    }

    /**
     * Debita a aposta do saldo. Deve ser chamado apenas quando
     * {@link #temSaldoSuficiente()} for verdadeiro.
     */
    public void debitarAposta() {
        this.saldo -= aposta;
    }

    /** Credita um prêmio ao saldo. */
    public void creditarPremio(double premio) {
        this.saldo += premio;
    }
}
