package model;

import java.awt.Color;

/**
 * Representa cada símbolo do rolo do slot (tema Corinthians).
 *
 * Cada símbolo conhece:
 *  - o nome de exibição;
 *  - o arquivo de imagem correspondente (caminho relativo dentro dos recursos);
 *  - o multiplicador de prêmio aplicado sobre a aposta quando ocorre uma vitória;
 *  - uma cor de destaque usada no desenho de fallback (caso a imagem não seja encontrada).
 *
 * Quanto maior o multiplicador, mais "raro/valioso" é o símbolo.
 */
public enum Simbolo {

    GAVIAO("Gavião da Fiel", "gaviao.jpg", 100, new Color(30, 30, 30)),
    MOSQUETEIRO("Mosqueteiro", "mosqueteiro.jpg", 50, new Color(60, 60, 60)),
    SIMBOLO_ANTIGO("Símbolo Antigo", "simbolo_antigo.png", 30, new Color(90, 90, 90)),
    LOGO("Escudo Oficial", "logo.png", 20, new Color(120, 120, 120)),
    MEMPHIS("Memphis", "memphis.jpeg", 15, new Color(150, 150, 150));

    /** Pasta base dos recursos de imagem dentro do classpath. */
    public static final String PASTA_IMAGENS = "/recursos/imagens/";

    private final String nomeExibicao;
    private final String nomeArquivo;
    private final int multiplicador;
    private final Color corFallback;

    Simbolo(String nomeExibicao, String nomeArquivo, int multiplicador, Color corFallback) {
        this.nomeExibicao = nomeExibicao;
        this.nomeArquivo = nomeArquivo;
        this.multiplicador = multiplicador;
        this.corFallback = corFallback;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    /** Caminho relativo completo para uso com getClass().getResource(). */
    public String getCaminhoRecurso() {
        return PASTA_IMAGENS + nomeArquivo;
    }

    public int getMultiplicador() {
        return multiplicador;
    }

    public Color getCorFallback() {
        return corFallback;
    }
}
