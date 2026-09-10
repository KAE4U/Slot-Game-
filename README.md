# Gavião da Fiel Slots 🦅⚫⚪

Simulador de jogo de **Slot Machine ("Roleta")** desenvolvido em **Java Swing**, com
temática de futebol da torcida alvinegra (Corinthians). Trabalho da disciplina de
**Práticas de Programação Orientada a Objetos** — UNIP.

## 🎮 Sobre o jogo

- Grade **3x3** de símbolos que giram ao acionar o botão **GIRAR**.
- Cada giro desconta a **aposta** do **saldo** de créditos de demonstração.
- Ao final do giro, o jogo verifica **linhas horizontais** e **diagonais**: três
  símbolos iguais resultam em prêmio (`aposta × multiplicador do símbolo`).
- Animação com **parada progressiva** das colunas (1s, 1.5s e 2s).

## 🏆 Símbolos e multiplicadores

| Símbolo          | Multiplicador |
|------------------|:-------------:|
| Gavião da Fiel   | x100          |
| Mosqueteiro      | x50           |
| Símbolo Antigo   | x30           |
| Escudo Oficial   | x20           |
| Memphis          | x15           |

## 🧱 Organização do código (POO / MVC)

```
src/
├── Main.java                     # Ponto de entrada
├── model/                        # Regras de negócio (sem interface gráfica)
│   ├── Simbolo.java              # Enum: símbolos, imagens e multiplicadores
│   ├── Jogador.java              # Saldo, aposta, débito e crédito
│   └── MotorDoJogo.java          # Sorteio (Random) + verificação de vitória
├── view/                         # Interface gráfica (Swing)
│   ├── TelaPrincipal.java        # JFrame (BorderLayout + GridLayout)
│   └── CarregadorImagens.java    # getResource() com fallback desenhado
├── controller/
│   └── ControladorJogo.java      # Eventos + animação (javax.swing.Timer)
└── recursos/imagens/             # Imagens dos símbolos
```

### Requisitos técnicos atendidos
- ✅ **POO / modularização**: lógica separada em Model, View e Controller.
- ✅ **Apostas e saldo**: desconto por giro e alerta via `JOptionPane` quando o saldo é insuficiente.
- ✅ **Animação temporal**: `javax.swing.Timer` com parada progressiva das colunas.
- ✅ **Aleatoriedade**: `java.util.Random` sorteia a grade ao fim do giro.
- ✅ **Vitória**: varredura de linhas horizontais e diagonais.
- ✅ **Caminhos relativos**: imagens carregadas com `getClass().getResource()`.
- ✅ **Nomenclatura semântica**: `btnGirar`, `lblSaldo`, etc.
- ✅ **Layouts**: `BorderLayout` + `GridLayout` (sem `setLayout(null)`).

## ▶️ Como compilar e executar

Pela linha de comando (a partir da raiz do projeto):

```bash
# Compilar
javac -d build $(find src -name "*.java")

# Copiar os recursos (imagens) para o classpath de execução
cp -r src/recursos build/

# Executar
java -cp build Main
```

> As imagens ficam em `src/recursos/imagens/`. Caso alguma não seja encontrada,
> o jogo desenha automaticamente um ícone de reserva (fallback), garantindo que
> a aplicação continue funcionando.
