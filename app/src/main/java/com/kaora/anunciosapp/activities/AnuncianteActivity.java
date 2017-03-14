package com.kaora.anunciosapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.models.Anunciante;

public class AnuncianteActivity extends AppCompatActivity {

    TextView tvNomeAnunciante;
    TextView tvTelefone;
    TextView tvEndereco;
    TextView tvNumero;

    Anunciante anunciante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anunciante);

        Intent intent = getIntent();
        anunciante = (Anunciante) intent.getSerializableExtra("anunciante");

        tvNomeAnunciante = (TextView) findViewById(R.id.tvNomeAnunciante);
        tvTelefone = (TextView) findViewById(R.id.tvTelefone);
        tvEndereco = (TextView) findViewById(R.id.tvEndereco);
        tvNumero = (TextView) findViewById(R.id.tvNumero);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvNomeAnunciante.setText(anunciante.nome);
        tvTelefone.setText(anunciante.telefone);
        tvEndereco.setText(anunciante.logradouro);
        tvNumero.setText(anunciante.numero);
    }
}
