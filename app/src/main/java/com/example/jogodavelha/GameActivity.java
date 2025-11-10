package com.example.jogodavelha;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private TextView tvPlacarX, tvPlacarO, tvEmpates, tvVezJogador;
    private Button btnReiniciar;
    private ImageButton btnVoltar;
    private Button[][] botoes = new Button[3][3];

    private char[] tabuleiro = new char[9];
    private char jogadorAtual = 'X';
    private boolean jogoAtivo = true;
    private String modoJogo;
    private String dificuldade;

    private int pontosX = 0;
    private int pontosO = 0;
    private int empates = 0;

    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        modoJogo = getIntent().getStringExtra("MODO_JOGO");
        dificuldade = getIntent().getStringExtra("DIFICULDADE");

        inicializarViews();
        inicializarTabuleiro();
        configurarBotoes();
    }

    private void inicializarViews() {
        tvPlacarX = findViewById(R.id.tvPlacarX);
        tvPlacarO = findViewById(R.id.tvPlacarO);
        tvEmpates = findViewById(R.id.tvEmpates);
        tvVezJogador = findViewById(R.id.tvVezJogador);
        btnReiniciar = findViewById(R.id.btnReiniciar);
        btnVoltar = findViewById(R.id.btnVoltar);

        botoes[0][0] = findViewById(R.id.btn00);
        botoes[0][1] = findViewById(R.id.btn01);
        botoes[0][2] = findViewById(R.id.btn02);
        botoes[1][0] = findViewById(R.id.btn10);
        botoes[1][1] = findViewById(R.id.btn11);
        botoes[1][2] = findViewById(R.id.btn12);
        botoes[2][0] = findViewById(R.id.btn20);
        botoes[2][1] = findViewById(R.id.btn21);
        botoes[2][2] = findViewById(R.id.btn22);

        btnVoltar.setOnClickListener(v -> finish());
        btnReiniciar.setOnClickListener(v -> reiniciarJogo());

        atualizarPlacar();
    }

    private void inicializarTabuleiro() {
        for (int i = 0; i < 9; i++) {
            tabuleiro[i] = ' ';
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                botoes[i][j].setText("");
                botoes[i][j].setForeground(null);
                botoes[i][j].setEnabled(true);
            }
        }

        jogadorAtual = 'X';
        jogoAtivo = true;
        atualizarVezJogador();
    }

    private void configurarBotoes() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final int posicao = i * 3 + j;

                botoes[i][j].setOnClickListener(v -> {
                    if (jogoAtivo && tabuleiro[posicao] == ' ') {
                        fazerJogada(posicao);
                    }
                });
            }
        }
    }

    private void fazerJogada(int posicao) {
        tabuleiro[posicao] = jogadorAtual;
        atualizarBotao(posicao);

        if (verificarVitoria(jogadorAtual)) {
            jogoAtivo = false;
            if (jogadorAtual == 'X') {
                pontosX++;
            } else {
                pontosO++;
            }
            atualizarPlacar();
            mostrarResultado("Jogador " + jogadorAtual + " venceu!");
            return;
        }

        if (verificarEmpate()) {
            jogoAtivo = false;
            empates++;
            atualizarPlacar();
            mostrarResultado("Empate!");
            return;
        }

        jogadorAtual = (jogadorAtual == 'X') ? 'O' : 'X';
        atualizarVezJogador();

        if (modoJogo.equals("vsBot") && jogadorAtual == 'O' && jogoAtivo) {
            desabilitarTabuleiro();
            new Handler().postDelayed(() -> {
                jogadaBot();
                habilitarTabuleiro();
            }, 600);
        }
    }

    private void atualizarBotao(int posicao) {
        int linha = posicao / 3;
        int coluna = posicao % 3;

        if (tabuleiro[posicao] == 'X') {

            botoes[linha][coluna].setForeground(getResources().getDrawable(R.drawable.ic_x, null));
            botoes[linha][coluna].setForegroundGravity(android.view.Gravity.CENTER);
        } else if (tabuleiro[posicao] == 'O') {
            botoes[linha][coluna].setForeground(getResources().getDrawable(R.drawable.ic_o, null));
            botoes[linha][coluna].setForegroundGravity(android.view.Gravity.CENTER);
        }

        botoes[linha][coluna].setEnabled(false);
    }

    private void jogadaBot() {
        if (!jogoAtivo) return;

        int posicao = -1;

        if (dificuldade.equals("FACIL")) {
            posicao = jogadaFacil();
        } else if (dificuldade.equals("MEDIO")) {
            posicao = jogadaMedio();
        } else if (dificuldade.equals("DIFICIL")) {
            posicao = jogadaDificil();
        }

        if (posicao != -1) {
            fazerJogada(posicao);
        }
    }


    private int jogadaFacil() {
        List<Integer> posicoesLivres = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (tabuleiro[i] == ' ') {
                posicoesLivres.add(i);
            }
        }

        if (!posicoesLivres.isEmpty()) {
            return posicoesLivres.get(random.nextInt(posicoesLivres.size()));
        }

        return -1;
    }


    private int jogadaMedio() {

        int posicao = encontrarJogadaVencedora('O');
        if (posicao != -1) return posicao;


        posicao = encontrarJogadaVencedora('X');
        if (posicao != -1) return posicao;


        if (tabuleiro[4] == ' ') return 4;

        // Joga nos cantos
        int[] cantos = {0, 2, 6, 8};
        List<Integer> cantosLivres = new ArrayList<>();
        for (int canto : cantos) {
            if (tabuleiro[canto] == ' ') {
                cantosLivres.add(canto);
            }
        }

        if (!cantosLivres.isEmpty()) {
            return cantosLivres.get(random.nextInt(cantosLivres.size()));
        }


        return jogadaFacil();
    }

    private int encontrarJogadaVencedora(char jogador) {
        int[][] combinacoes = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };

        for (int[] combo : combinacoes) {
            int count = 0;
            int posicaoVazia = -1;

            for (int pos : combo) {
                if (tabuleiro[pos] == jogador) {
                    count++;
                } else if (tabuleiro[pos] == ' ') {
                    posicaoVazia = pos;
                }
            }

            if (count == 2 && posicaoVazia != -1) {
                return posicaoVazia;
            }
        }

        return -1;
    }


    private int jogadaDificil() {
        int melhorPosicao = -1;
        int melhorValor = Integer.MIN_VALUE;

        for (int i = 0; i < 9; i++) {
            if (tabuleiro[i] == ' ') {
                tabuleiro[i] = 'O';
                int valor = minimax(0, false);
                tabuleiro[i] = ' ';

                if (valor > melhorValor) {
                    melhorValor = valor;
                    melhorPosicao = i;
                }
            }
        }

        return melhorPosicao;
    }

    private int minimax(int profundidade, boolean isMaximizador) {

        if (verificarVitoriaInterna('O')) {
            return 10 - profundidade;
        }
        if (verificarVitoriaInterna('X')) {
            return profundidade - 10;
        }

        if (verificarEmpateInterno()) {
            return 0;
        }

        if (isMaximizador) {

            int melhorValor = Integer.MIN_VALUE;

            for (int i = 0; i < 9; i++) {
                if (tabuleiro[i] == ' ') {
                    tabuleiro[i] = 'O';
                    int valor = minimax(profundidade + 1, false);
                    tabuleiro[i] = ' ';
                    melhorValor = Math.max(melhorValor, valor);
                }
            }
            return melhorValor;

        } else {

            int melhorValor = Integer.MAX_VALUE;

            for (int i = 0; i < 9; i++) {
                if (tabuleiro[i] == ' ') {
                    tabuleiro[i] = 'X';
                    int valor = minimax(profundidade + 1, true);
                    tabuleiro[i] = ' ';
                    melhorValor = Math.min(melhorValor, valor);
                }
            }
            return melhorValor;
        }
    }

    private boolean verificarVitoriaInterna(char jogador) {
        int[][] combinacoes = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };

        for (int[] combo : combinacoes) {
            if (tabuleiro[combo[0]] == jogador &&
                    tabuleiro[combo[1]] == jogador &&
                    tabuleiro[combo[2]] == jogador) {
                return true;
            }
        }
        return false;
    }

    private boolean verificarEmpateInterno() {
        for (int i = 0; i < 9; i++) {
            if (tabuleiro[i] == ' ') {
                return false;
            }
        }
        return true;
    }


    private boolean verificarVitoria(char jogador) {
        int[][] combinacoes = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };

        for (int[] combo : combinacoes) {
            if (tabuleiro[combo[0]] == jogador &&
                    tabuleiro[combo[1]] == jogador &&
                    tabuleiro[combo[2]] == jogador) {
                return true;
            }
        }

        return false;
    }

    private boolean verificarEmpate() {
        for (int i = 0; i < 9; i++) {
            if (tabuleiro[i] == ' ') {
                return false;
            }
        }
        return true;
    }

    private void mostrarResultado(String mensagem) {
        new AlertDialog.Builder(this)
                .setTitle("Fim do Jogo")
                .setMessage(mensagem)
                .setPositiveButton("Nova Partida", (dialog, which) -> reiniciarJogo())
                .setNegativeButton("Voltar ao Menu", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void reiniciarJogo() {
        inicializarTabuleiro();
    }

    private void atualizarVezJogador() {
        tvVezJogador.setText("Vez de: " + jogadorAtual);
    }

    private void atualizarPlacar() {
        tvPlacarX.setText("Jogador X: " + pontosX);
        tvPlacarO.setText("Jogador O: " + pontosO);
        tvEmpates.setText("Empates: " + empates);
    }

    private void desabilitarTabuleiro() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                botoes[i][j].setEnabled(false);
            }
        }
    }

    private void habilitarTabuleiro() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int posicao = i * 3 + j;
                if (tabuleiro[posicao] == ' ') {
                    botoes[i][j].setEnabled(true);
                }
            }
        }
    }
}