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

    private double premio;
    private int rodadasGratis;
    private final List<Posicao> posicoesVencedoras = new ArrayList<>();

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

    public boolean houveVitoria() {
        return premio > 0;
    }
}
