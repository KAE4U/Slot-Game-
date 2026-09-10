import javax.swing.SwingUtilities;

import controller.ControladorJogo;
import model.Dificuldade;
import model.Jogador;
import model.MotorDoJogo;
import view.TelaPrincipal;

/**
 * Ponto de entrada do jogo de Slot "Gavião da Fiel Slots".
 *
 * Cria as camadas do padrão MVC e exibe a janela na Event Dispatch Thread
 * (boa prática de Swing para operações de interface gráfica).
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Dificuldade dificuldadeInicial = Dificuldade.NORMAL;

            Jogador jogador = new Jogador(dificuldadeInicial);
            MotorDoJogo motor = new MotorDoJogo();
            motor.setDificuldade(dificuldadeInicial);
            TelaPrincipal tela = new TelaPrincipal();

            // O controlador conecta a interface às regras de negócio.
            new ControladorJogo(tela, jogador, motor);

            tela.setVisible(true);
        });
    }
}
