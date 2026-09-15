package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa o resultado de um giro: prêmio, posições vencedoras (para
 * destacar na tela) e quantidade de rodadas grátis conquistadas.
 */
public class ResultadoGiro {

    /** Uma posição (linha, coluna) da grade. */
    public static class Posicao {
        public final int linha;
        public final int coluna;

        public Posicao(int linha, int coluna) {
            this.linha = linha;
            this.coluna = coluna;
        }
    }

    /** Uma combinação vencedora: o símbolo, seu multiplicador e o prêmio. */
    public static class Combinacao {
        public final Simbolo simbolo;
        public final int multiplicador;
        public final double premio;
        public final String descricaoLinha;

        public Combinacao(Simbolo simbolo, int multiplicador, double premio,
                String descricaoLinha) {
            this.simbolo = simbolo;
            this.multiplicador = multiplicador;
            this.premio = premio;
            this.descricaoLinha = descricaoLinha;
        }
    }

    private double premio;
    private int rodadasGratis;
    private final List<Posicao> posicoesVencedoras = new ArrayList<>();
    private final List<Combinacao> combinacoes = new ArrayList<>();

    public double getPremio() {
        return premio;
    }

    public void adicionarPremio(double valor) {
        this.premio += valor;
    }

    public int getRodadasGratis() {
        return rodadasGratis;
    }

    public void adicionarRodadasGratis(int quantidade) {
        this.rodadasGratis += quantidade;
    }

    public List<Posicao> getPosicoesVencedoras() {
        return posicoesVencedoras;
    }

    public void marcarPosicao(int linha, int coluna) {
        posicoesVencedoras.add(new Posicao(linha, coluna));
    }

    public List<Combinacao> getCombinacoes() {
        return combinacoes;
    }

    public void adicionarCombinacao(Simbolo simbolo, int multiplicador, double premio,
            String descricaoLinha) {
        combinacoes.add(new Combinacao(simbolo, multiplicador, premio, descricaoLinha));
    }

    /** Maior multiplicador entre as combinações vencedoras (0 se não houve). */
    public int getMaiorMultiplicador() {
        int maior = 0;
        for (Combinacao c : combinacoes) {
            if (c.multiplicador > maior) {
                maior = c.multiplicador;
            }
        }
        return maior;
    }

    public boolean houveVitoria() {
        return premio > 0;
    }
}
