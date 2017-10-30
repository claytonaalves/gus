package com.kaora.anunciosapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.kaora.anunciosapp.R;

public class AnuncioPublicado extends AppCompatActivity {

    private static final String TAG = AnuncioPublicado.class.getSimpleName();
    private TextView tvTitulo, tvDescricao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncio_publicado);

        tvTitulo = (TextView) findViewById(R.id.tvTitulo);
        tvDescricao = (TextView) findViewById(R.id.tvDescricao);

//        String guidAnuncio = "";

        Intent startingIntent = getIntent();
        if (startingIntent != null) {
//            guidAnuncio = startingIntent.getStringExtra("guid_anuncio");

            tvTitulo.setText(startingIntent.getStringExtra("title"));
            tvDescricao.setText(startingIntent.getStringExtra("description"));
        }

//        getOfferDetails(id_offer);
    }

}
