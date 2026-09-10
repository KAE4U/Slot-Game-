package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.ImageIcon;

import model.Simbolo;

/**
 * Responsável por carregar as imagens dos símbolos usando caminhos relativos
 * (getClass().getResource()), garantindo portabilidade entre máquinas/SOs.
 *
 * Caso a imagem não seja encontrada no classpath, é gerado automaticamente
 * um ícone de "fallback" desenhado por código, para que o jogo continue
 * funcionando mesmo sem os arquivos de imagem presentes.
 *
 * As imagens são carregadas uma única vez e mantidas em cache.
 */
public class CarregadorImagens {

    private final int tamanho;
    private final Map<Simbolo, ImageIcon> cache = new EnumMap<>(Simbolo.class);

    /**
     * @param tamanho largura/altura (em pixels) em que os ícones serão exibidos
     */
    public CarregadorImagens(int tamanho) {
        this.tamanho = tamanho;
        for (Simbolo simbolo : Simbolo.values()) {
            cache.put(simbolo, carregar(simbolo));
        }
    }

    /** Retorna o ícone (já redimensionado) para o símbolo informado. */
    public ImageIcon getIcone(Simbolo simbolo) {
        return cache.get(simbolo);
    }

    /** Tenta carregar a imagem do recurso; se falhar, desenha um fallback. */
    private ImageIcon carregar(Simbolo simbolo) {
        java.net.URL url = getClass().getResource(simbolo.getCaminhoRecurso());
        if (url != null) {
            ImageIcon original = new ImageIcon(url);
            if (original.getIconWidth() > 0) {
                java.awt.Image redimensionada = original.getImage()
                        .getScaledInstance(tamanho, tamanho, java.awt.Image.SCALE_SMOOTH);
                return new ImageIcon(redimensionada);
            }
        }
        return criarFallback(simbolo);
    }

    /**
     * Desenha um ícone de reserva com a cor do símbolo e suas iniciais,
     * usado quando o arquivo de imagem não é encontrado.
     */
    private ImageIcon criarFallback(Simbolo simbolo) {
        BufferedImage img = new BufferedImage(tamanho, tamanho, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fundo circular na cor do símbolo
        g.setColor(simbolo.getCorFallback());
        g.fillRoundRect(4, 4, tamanho - 8, tamanho - 8, 20, 20);

        // Borda branca
        g.setColor(Color.WHITE);
        g.drawRoundRect(4, 4, tamanho - 8, tamanho - 8, 20, 20);

        // Iniciais do nome do símbolo
        String texto = iniciais(simbolo.getNomeExibicao());
        g.setFont(new Font("SansSerif", Font.BOLD, tamanho / 3));
        java.awt.FontMetrics fm = g.getFontMetrics();
        int x = (tamanho - fm.stringWidth(texto)) / 2;
        int y = (tamanho - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(texto, x, y);

        g.dispose();
        return new ImageIcon(img);
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
