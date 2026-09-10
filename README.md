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
- As imagens **preenchem toda a célula** (fill) e a **linha vencedora pisca**.

## ✨ Recursos extras

- 🔁 **Modo AUTO**: gira automaticamente até você parar ou acabar o saldo.
- 🔊 **Efeitos sonoros** sintetizados (giro, vitória e bônus) — sem arquivos externos.
- 💚 **Destaque da linha vencedora**: as células que formaram a combinação piscam.
- 🎚️ **Dificuldade** (Fácil / Normal / Difícil): muda o saldo inicial e o retorno do jogo.
- 🎁 **Rodadas Grátis (Free Spins)**: 3+ Gaviões na grade concedem 5 giros grátis.

## 🏆 Símbolos e multiplicadores

| Símbolo          | Multiplicador |
|------------------|:-------------:|
| Gavião da Fiel   | x25           |
| Mosqueteiro      | x12           |
| Símbolo Antigo   | x8            |
| Escudo Oficial   | x5            |
| Memphis          | x3            |

> O prêmio final também é ajustado por um **fator de dificuldade** (o modo
> Difícil paga menos), mantendo o jogo equilibrado.

## 🧱 Organização do código (POO / MVC)

```
src/
├── Main.java                     # Ponto de entrada
├── model/                        # Regras de negócio (sem interface gráfica)
│   ├── Simbolo.java              # Enum: símbolos, imagens e multiplicadores
│   ├── Jogador.java              # Saldo, aposta, rodadas grátis, débito/crédito
│   ├── Dificuldade.java          # Níveis (saldo inicial + fator de pagamento)
│   ├── ResultadoGiro.java        # Prêmio, posições vencedoras e free spins
│   └── MotorDoJogo.java          # Sorteio (Random) + verificação de vitória
├── view/                         # Interface gráfica (Swing)
│   ├── TelaPrincipal.java        # JFrame (BorderLayout + GridLayout)
│   ├── CelulaSimbolo.java        # Célula que desenha a imagem em fill + destaque
│   ├── CarregadorImagens.java    # getResource() com fallback desenhado
│   ├── Tema.java                 # Paleta de cores (cinza-carvão + branco)
│   └── Som.java                  # Efeitos sonoros sintetizados
├── controller/
│   └── ControladorJogo.java      # Eventos + animação + AUTO + free spins
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
