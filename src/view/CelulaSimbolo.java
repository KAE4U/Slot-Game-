package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.JComponent;

import model.Simbolo;

/**
 * Componente que exibe um símbolo do slot preenchendo todo o quadrado (fill).
 *
 * A imagem é desenhada esticada para ocupar toda a área da célula, com cantos
 * arredondados e uma moldura. Quando marcada como vencedora, a célula recebe
 * um brilho/borda destacado (usado para piscar a linha ganhadora).
 */
public class CelulaSimbolo extends JComponent {

    private final CarregadorImagens carregador;
    private Simbolo simbolo;
    private boolean destacada;

    public CelulaSimbolo(CarregadorImagens carregador, Simbolo simboloInicial) {
        this.carregador = carregador;
        this.simbolo = simboloInicial;
    }

    /** Define qual símbolo esta célula exibe e redesenha. */
    public void setSimbolo(Simbolo simbolo) {
        this.simbolo = simbolo;
        repaint();
    }

    public Simbolo getSimbolo() {
        return simbolo;
    }

    /** Liga/desliga o destaque de vitória. */
    public void setDestacada(boolean destacada) {
        this.destacada = destacada;
        repaint();
    }

    public boolean isDestacada() {
        return destacada;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int largura = getWidth();
        int altura = getHeight();
        int arco = 18;

        // Fundo da célula
        g.setColor(Tema.FUNDO_CELULA);
        g.fillRoundRect(0, 0, largura, altura, arco, arco);

        // Imagem preenchendo todo o quadrado (fill), respeitando cantos arredondados
        Image imagem = carregador.getImagem(simbolo);
        if (imagem != null) {
            java.awt.Shape recorteAntigo = g.getClip();
            g.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, largura, altura, arco, arco));
            g.drawImage(imagem, 0, 0, largura, altura, this);
            g.setClip(recorteAntigo);
        }

        // Moldura: verde brilhante se vencedora, branca suave caso contrário
        if (destacada) {
            g.setColor(Tema.VITORIA);
            g.setStroke(new BasicStroke(5f));
            g.drawRoundRect(2, 2, largura - 5, altura - 5, arco, arco);
        } else {
            g.setColor(new Color(255, 255, 255, 60));
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(1, 1, largura - 3, altura - 3, arco, arco);
        }

        g.dispose();
    }
}
