package model;

import java.util.Random;

/**
 * Núcleo lógico do slot: sorteia a grade de símbolos e calcula o prêmio.
 *
 * A grade é 3x3 (LINHAS x COLUNAS). O sorteio usa {@link java.util.Random}
 * conforme exigido na especificação.
 *
 * Verificação de vitória:
 *  - varre as 3 linhas horizontais;
 *  - varre as 2 diagonais.
 * Uma combinação vence quando os 3 símbolos da linha/diagonal são idênticos.
 * O prêmio é a soma de (aposta x multiplicador do símbolo) de cada
 * combinação vencedora encontrada.
 */
public class MotorDoJogo {

    public static final int LINHAS = 3;
    public static final int COLUNAS = 3;

    private final Random aleatorio = new Random();
    private final Simbolo[][] grade = new Simbolo[LINHAS][COLUNAS];

    /** Sorteia todos os símbolos da grade. Chamado ao fim do giro. */
    public void sortearGrade() {
        Simbolo[] valores = Simbolo.values();
        for (int linha = 0; linha < LINHAS; linha++) {
            for (int coluna = 0; coluna < COLUNAS; coluna++) {
                grade[linha][coluna] = valores[aleatorio.nextInt(valores.length)];
            }
        }
    }

    /** Sorteia um único símbolo aleatório (usado na animação dos rolos). */
    public Simbolo sortearSimbolo() {
        Simbolo[] valores = Simbolo.values();
        return valores[aleatorio.nextInt(valores.length)];
    }

    /** Retorna o símbolo em uma posição específica da grade. */
    public Simbolo getSimbolo(int linha, int coluna) {
        return grade[linha][coluna];
    }

    /**
     * Calcula o prêmio total varrendo linhas horizontais e diagonais.
     *
     * @param aposta valor apostado no giro
     * @return prêmio total (0 se não houver combinação vencedora)
     */
    public double calcularPremio(double aposta) {
        double premioTotal = 0.0;

        // Linhas horizontais
        for (int linha = 0; linha < LINHAS; linha++) {
            if (tresIguais(grade[linha][0], grade[linha][1], grade[linha][2])) {
                premioTotal += aposta * grade[linha][0].getMultiplicador();
            }
        }

        // Diagonal principal (canto superior esquerdo -> inferior direito)
        if (tresIguais(grade[0][0], grade[1][1], grade[2][2])) {
            premioTotal += aposta * grade[0][0].getMultiplicador();
        }

        // Diagonal secundária (canto superior direito -> inferior esquerdo)
        if (tresIguais(grade[0][2], grade[1][1], grade[2][0])) {
            premioTotal += aposta * grade[0][2].getMultiplicador();
        }

        return premioTotal;
    }

    /** Verifica se os três símbolos informados são idênticos. */
    private boolean tresIguais(Simbolo a, Simbolo b, Simbolo c) {
        return a != null && a == b && b == c;
    }
}
