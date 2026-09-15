package view;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.SourceDataLine;

/**
 * Utilitário de efeitos sonoros do jogo.
 *
 * A música de vitória é o Hino do Corinthians (instrumental), carregado de um
 * arquivo WAV nos recursos (via caminho relativo com getResourceAsStream, para
 * manter a portabilidade). O arquivo original enviado é um .mp4; como o Java SE
 * puro não reproduz MP4/MP3 nativamente, ele foi convertido para WAV (PCM), que
 * é tocado por {@link javax.sound.sampled.Clip} sem bibliotecas externas.
 *
 * Os demais efeitos (giro e bônus) continuam sintetizados em tempo real.
 * Se o sistema não tiver saída de áudio, as falhas são ignoradas silenciosamente.
 */
public final class Som {

    private static final float TAXA_AMOSTRAGEM = 44100f;

    /** Caminho relativo do hino (WAV) dentro do classpath. */
    private static final String CAMINHO_HINO = "/recursos/audio/hino_vitoria.wav";

    /** Clip da música de vitória, mantido para permitir parar/reiniciar. */
    private static Clip clipVitoria;

    private Som() {
        // classe utilitária
    }

    /** Som curto de "clique/giro". */
    public static void tocarGiro() {
        tocarTom(440, 120, 0.3);
    }

    /**
     * Toca o Hino do Corinthians (WAV) como música de vitória.
     *
     * Se já estiver tocando, reinicia do começo. A reprodução é assíncrona
     * (o Clip toca em sua própria thread interna), não travando a interface.
     */
    public static synchronized void tocarVitoria() {
        try {
            pararVitoria();
            InputStream recurso = Som.class.getResourceAsStream(CAMINHO_HINO);
            if (recurso == null) {
                // Recurso ausente: usa a fanfarra sintetizada como reserva.
                tocarFanfarraVitoria();
                return;
            }
            AudioInputStream audio = AudioSystem.getAudioInputStream(
                    new BufferedInputStream(recurso));
            clipVitoria = AudioSystem.getClip();
            clipVitoria.open(audio);
            clipVitoria.start();
        } catch (Exception ignorada) {
            // Sem áudio disponível ou formato não suportado: ignora.
        }
    }

    /** Interrompe a música de vitória, se estiver tocando. */
    public static synchronized void pararVitoria() {
        if (clipVitoria != null) {
            if (clipVitoria.isRunning()) {
                clipVitoria.stop();
            }
            clipVitoria.close();
            clipVitoria = null;
        }
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

    /** Fanfarra sintetizada usada como reserva caso o hino não seja encontrado. */
    private static void tocarFanfarraVitoria() {
        new Thread(() -> {
            tocarTomBloqueante(523, 120, 0.4);
            tocarTomBloqueante(659, 120, 0.4);
            tocarTomBloqueante(784, 200, 0.4);
        }, "som-vitoria-reserva").start();
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
