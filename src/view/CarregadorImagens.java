package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

import model.Simbolo;

/**
 * Responsável por carregar as imagens dos símbolos usando caminhos relativos
 * (getClass().getResource()), garantindo portabilidade entre máquinas/SOs.
 *
 * Caso a imagem não seja encontrada no classpath, é gerado automaticamente
 * um "fallback" desenhado por código, para que o jogo continue funcionando
 * mesmo sem os arquivos de imagem presentes.
 *
 * As imagens originais são carregadas uma única vez e mantidas em cache. O
 * redimensionamento para preencher a célula (fill) é feito pela própria célula
 * ({@link CelulaSimbolo}), que conhece seu tamanho atual em tela.
 */
public class CarregadorImagens {

    /** Tamanho base usado para gerar os fallbacks desenhados. */
    private static final int TAMANHO_BASE = 256;

    private final Map<Simbolo, Image> cache = new EnumMap<>(Simbolo.class);

    public CarregadorImagens() {
        for (Simbolo simbolo : Simbolo.values()) {
            cache.put(simbolo, carregar(simbolo));
        }
    }

    /** Retorna a imagem original (não redimensionada) do símbolo. */
    public Image getImagem(Simbolo simbolo) {
        return cache.get(simbolo);
    }

    /** Tenta carregar a imagem do recurso; se falhar, desenha um fallback. */
    private Image carregar(Simbolo simbolo) {
        java.net.URL url = getClass().getResource(simbolo.getCaminhoRecurso());
        if (url != null) {
            javax.swing.ImageIcon original = new javax.swing.ImageIcon(url);
            if (original.getIconWidth() > 0) {
                return original.getImage();
            }
        }
        return criarFallback(simbolo);
    }

    /**
     * Desenha uma imagem de reserva com a cor do símbolo e suas iniciais,
     * usada quando o arquivo de imagem não é encontrado.
     */
    private Image criarFallback(Simbolo simbolo) {
        BufferedImage img = new BufferedImage(TAMANHO_BASE, TAMANHO_BASE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(simbolo.getCorFallback());
        g.fillRect(0, 0, TAMANHO_BASE, TAMANHO_BASE);

        String texto = iniciais(simbolo.getNomeExibicao());
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, TAMANHO_BASE / 3));
        java.awt.FontMetrics fm = g.getFontMetrics();
        int x = (TAMANHO_BASE - fm.stringWidth(texto)) / 2;
        int y = (TAMANHO_BASE - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(texto, x, y);

        g.dispose();
        return img;
    }

    /** Extrai até 2 iniciais das palavras do nome (ex.: "Gavião da Fiel" -> "GF"). */
    private String iniciais(String nome) {
        StringBuilder sb = new StringBuilder();
        for (String palavra : nome.split("\\s+")) {
            if (!palavra.isEmpty() && !palavra.equalsIgnoreCase("da")
                    && !palavra.equalsIgnoreCase("de")) {
                sb.append(Character.toUpperCase(palavra.charAt(0)));
            }
            if (sb.length() >= 2) {
                break;
            }
        }
        return sb.toString();
    }
}
