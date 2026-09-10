package view;

import java.awt.Color;

/**
 * Paleta de cores centralizada da interface.
 *
 * A base deixou de ser preto puro (#000000) e passou a usar tons de cinza-carvão
 * mais suaves, mantendo a identidade preto e branco alvinegra, porém com um
 * visual mais moderno e agradável.
 */
public final class Tema {

    private Tema() {
        // classe utilitária: não deve ser instanciada
    }

    /** Fundo principal da janela (cinza-carvão, no lugar do preto puro). */
    public static final Color FUNDO = new Color(38, 40, 44);

    /** Fundo de painéis internos, um pouco mais claro que o fundo principal. */
    public static final Color FUNDO_PAINEL = new Color(52, 55, 61);

    /** Fundo das células dos rolos. */
    public static final Color FUNDO_CELULA = new Color(28, 30, 34);

    /** Fundo do topo/rodapé em gradiente (cor inicial). */
    public static final Color GRADIENTE_TOPO = new Color(58, 61, 68);

    /** Fundo do topo/rodapé em gradiente (cor final). */
    public static final Color GRADIENTE_BASE = new Color(30, 32, 36);

    /** Cor principal de texto. */
    public static final Color TEXTO = new Color(240, 240, 240);

    /** Cor de texto secundário / rótulos. */
    public static final Color TEXTO_SUAVE = new Color(180, 183, 190);

    /** Cor de destaque (dourado) para valores importantes e o botão girar. */
    public static final Color DESTAQUE = new Color(240, 190, 70);

    /** Cor de borda clara. */
    public static final Color BORDA = new Color(210, 212, 218);

    /** Cor usada para destacar a linha/coluna vencedora. */
    public static final Color VITORIA = new Color(90, 200, 120);

    /** Branco levemente suavizado para molduras. */
    public static final Color BRANCO_SUAVE = new Color(235, 236, 240);
}
