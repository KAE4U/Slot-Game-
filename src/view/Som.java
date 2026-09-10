package view;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

/**
 * Utilitário de efeitos sonoros simples, sintetizados em tempo real
 * (sem depender de arquivos de áudio externos).
 *
 * Os sons são gerados como ondas senoidais e tocados em uma thread separada
 * para não travar a interface gráfica. Se o sistema não tiver saída de áudio,
 * as falhas são ignoradas silenciosamente.
 */
public final class Som {

    private static final float TAXA_AMOSTRAGEM = 44100f;

    private Som() {
        // classe utilitária
    }

    /** Som curto de "clique/giro". */
    public static void tocarGiro() {
        tocarTom(440, 120, 0.3);
    }

    /** Sequência ascendente alegre para vitória. */
    public static void tocarVitoria() {
        new Thread(() -> {
            tocarTomBloqueante(523, 120, 0.4); // Dó
            tocarTomBloqueante(659, 120, 0.4); // Mi
            tocarTomBloqueante(784, 200, 0.4); // Sol
        }, "som-vitoria").start();
    }

    /** Fanfarra especial para rodadas grátis. */
    public static void tocarBonus() {
        new Thread(() -> {
            tocarTomBloqueante(784, 100, 0.4);
            tocarTomBloqueante(880, 100, 0.4);
            tocarTomBloqueante(988, 100, 0.4);
            tocarTomBloqueante(1047, 250, 0.4);
        }, "som-bonus").start();
    }

    /** Toca um tom em thread separada (não bloqueia a interface). */
    private static void tocarTom(int frequencia, int duracaoMs, double volume) {
        new Thread(() -> tocarTomBloqueante(frequencia, duracaoMs, volume), "som-tom").start();
    }

    /** Gera e reproduz uma onda senoidal de forma síncrona. */
    private static void tocarTomBloqueante(int frequencia, int duracaoMs, double volume) {
        try {
            int totalAmostras = (int) (TAXA_AMOSTRAGEM * duracaoMs / 1000);
            byte[] buffer = new byte[totalAmostras];
            for (int i = 0; i < totalAmostras; i++) {
                double angulo = 2.0 * Math.PI * i * frequencia / TAXA_AMOSTRAGEM;
                // envelope simples para evitar estalos no início/fim
                double envelope = Math.min(1.0, Math.min(i, totalAmostras - i) / 500.0);
                buffer[i] = (byte) (Math.sin(angulo) * 127 * volume * envelope);
            }

            AudioFormat formato = new AudioFormat(TAXA_AMOSTRAGEM, 8, 1, true, false);
            try (SourceDataLine linha = AudioSystem.getSourceDataLine(formato)) {
                linha.open(formato);
                linha.start();
                linha.write(buffer, 0, buffer.length);
                linha.drain();
            }
        } catch (Exception ignorada) {
            // Sem áudio disponível: ignora silenciosamente.
        }
    }
}
