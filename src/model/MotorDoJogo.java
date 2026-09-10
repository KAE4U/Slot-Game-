package model;

import java.util.Random;

/**
 * Núcleo lógico do slot: sorteia a grade de símbolos e calcula o resultado.
 *
 * A grade é 3x3 (LINHAS x COLUNAS). O sorteio usa {@link java.util.Random}
 * conforme exigido na especificação.
 *
 * Verificação de vitória (em {@link #avaliarGiro(double)}):
 *  - varre as 3 linhas horizontais;
 *  - varre as 2 diagonais.
 * Uma combinação vence quando os 3 símbolos da linha/diagonal são idênticos.
 * O prêmio é a soma de (aposta x multiplicador do símbolo) de cada combinação.
 *
 * Rodadas grátis (Free Spins): quando o símbolo GAVIÃO (o mais valioso)
 * aparece 3 ou mais vezes na grade, o jogador ganha rodadas grátis.
 *
 * A dificuldade influencia a chance dos símbolos valiosos aparecerem através
 * de um sorteio ponderado (símbolos mais valiosos ficam mais raros conforme
 * a dificuldade aumenta).
 */
public class MotorDoJogo {

    public static final int LINHAS = 3;
    public static final int COLUNAS = 3;

    /** Quantidade de símbolos GAVIÃO na grade que dispara rodadas grátis. */
    private static final int GAVIOES_PARA_FREE_SPINS = 3;

    /** Rodadas grátis concedidas ao disparar o bônus. */
    public static final int RODADAS_GRATIS_CONCEDIDAS = 5;

    private final Random aleatorio = new Random();
    private final Simbolo[][] grade = new Simbolo[LINHAS][COLUNAS];

    private Dificuldade dificuldade = Dificuldade.NORMAL;

    /** Pesos de sorteio de cada símbolo (índice = ordinal do símbolo). */
    private int[] pesos;

    public MotorDoJogo() {
        recalcularPesos();
    }

    public void setDificuldade(Dificuldade dificuldade) {
        this.dificuldade = dificuldade;
        recalcularPesos();
    }

    public Dificuldade getDificuldade() {
        return dificuldade;
    }

    /**
     * Recalcula o peso de cada símbolo. Símbolos mais valiosos (multiplicador
     * maior) recebem peso menor — e esse efeito é amplificado pela dificuldade.
     */
    private void recalcularPesos() {
        Simbolo[] valores = Simbolo.values();
        pesos = new int[valores.length];
        for (int i = 0; i < valores.length; i++) {
            // Símbolos mais valiosos ficam moderadamente mais raros.
            // O ajuste fino da dificuldade é feito pelo fator de pagamento
            // (ver avaliarGiro), o que evita distorcer a taxa de vitórias.
            int multiplicador = valores[i].getMultiplicador(); // 3..25
            pesos[i] = Math.max(2, 30 - multiplicador);
        }
    }

    /** Sorteia todos os símbolos da grade. Chamado ao fim do giro. */
    public void sortearGrade() {
        for (int linha = 0; linha < LINHAS; linha++) {
            for (int coluna = 0; coluna < COLUNAS; coluna++) {
                grade[linha][coluna] = sortearSimbolo();
            }
        }
    }

    /** Sorteia um símbolo aleatório de forma ponderada (usado na animação e na grade). */
    public Simbolo sortearSimbolo() {
        int total = 0;
        for (int p : pesos) {
            total += p;
        }
        int escolha = aleatorio.nextInt(total);
        Simbolo[] valores = Simbolo.values();
        for (int i = 0; i < valores.length; i++) {
            escolha -= pesos[i];
            if (escolha < 0) {
                return valores[i];
            }
        }
        return valores[valores.length - 1];
    }

    /** Retorna o símbolo em uma posição específica da grade. */
    public Simbolo getSimbolo(int linha, int coluna) {
        return grade[linha][coluna];
    }

    /**
     * Avalia a grade sorteada, calculando prêmio, posições vencedoras e
     * rodadas grátis.
     *
     * @param aposta valor apostado no giro
     * @return o resultado completo do giro
     */
    public ResultadoGiro avaliarGiro(double aposta) {
        ResultadoGiro resultado = new ResultadoGiro();
        double fator = dificuldade.getFatorPagamento();

        // Linhas horizontais
        for (int linha = 0; linha < LINHAS; linha++) {
            if (tresIguais(grade[linha][0], grade[linha][1], grade[linha][2])) {
                resultado.adicionarPremio(aposta * grade[linha][0].getMultiplicador() * fator);
                resultado.marcarPosicao(linha, 0);
                resultado.marcarPosicao(linha, 1);
                resultado.marcarPosicao(linha, 2);
            }
        }

        // Diagonal principal
        if (tresIguais(grade[0][0], grade[1][1], grade[2][2])) {
            resultado.adicionarPremio(aposta * grade[0][0].getMultiplicador() * fator);
            resultado.marcarPosicao(0, 0);
            resultado.marcarPosicao(1, 1);
            resultado.marcarPosicao(2, 2);
        }

        // Diagonal secundária
        if (tresIguais(grade[0][2], grade[1][1], grade[2][0])) {
            resultado.adicionarPremio(aposta * grade[0][2].getMultiplicador() * fator);
            resultado.marcarPosicao(0, 2);
            resultado.marcarPosicao(1, 1);
            resultado.marcarPosicao(2, 0);
        }

        // Rodadas grátis: contar quantos GAVIÕES apareceram na grade
        if (contarSimbolo(Simbolo.GAVIAO) >= GAVIOES_PARA_FREE_SPINS) {
            resultado.adicionarRodadasGratis(RODADAS_GRATIS_CONCEDIDAS);
        }

        return resultado;
    }

    /** Conta quantas vezes um símbolo aparece na grade. */
    private int contarSimbolo(Simbolo alvo) {
        int total = 0;
        for (int linha = 0; linha < LINHAS; linha++) {
            for (int coluna = 0; coluna < COLUNAS; coluna++) {
                if (grade[linha][coluna] == alvo) {
                    total++;
                }
            }
        }
        return total;
    }

    /** Verifica se os três símbolos informados são idênticos. */
    private boolean tresIguais(Simbolo a, Simbolo b, Simbolo c) {
        return a != null && a == b && b == c;
    }
}
