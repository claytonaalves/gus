package com.kaora.anunciosapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kaora.anunciosapp.R;

public class NovoAnuncioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_anuncio);

        setTitle("Novo anúncio");
    }
}
