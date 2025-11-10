package com.example.jogodavelha;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnJogar1v1;
    private Button btnJogarVsBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnJogar1v1 = findViewById(R.id.btnJogar1v1);
        btnJogarVsBot = findViewById(R.id.btnJogarVsBot);

        btnJogar1v1.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("MODO_JOGO", "1v1");
            startActivity(intent);
        });

        btnJogarVsBot.setOnClickListener(v -> mostrarDialogDificuldade());
    }

    private void mostrarDialogDificuldade() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_dificuldade, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button btnFacil = dialogView.findViewById(R.id.btnFacil);
        Button btnMedio = dialogView.findViewById(R.id.btnMedio);
        Button btnDificil = dialogView.findViewById(R.id.btnDificil);

        btnFacil.setOnClickListener(v -> {
            dialog.dismiss();
            iniciarJogoBot("FACIL");
        });

        btnMedio.setOnClickListener(v -> {
            dialog.dismiss();
            iniciarJogoBot("MEDIO");
        });

        btnDificil.setOnClickListener(v -> {
            dialog.dismiss();
            iniciarJogoBot("DIFICIL");
        });

        dialog.show();
    }

    private void iniciarJogoBot(String dificuldade) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("MODO_JOGO", "vsBot");
        intent.putExtra("DIFICULDADE", dificuldade);
        startActivity(intent);
    }
}