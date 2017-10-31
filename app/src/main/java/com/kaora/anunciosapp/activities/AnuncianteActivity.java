package com.kaora.anunciosapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.models.Advertiser;

public class AnuncianteActivity extends AppCompatActivity {

    TextView tvNomeAnunciante;
    TextView tvTelefone;
    TextView tvEndereco;
    TextView tvNumero;

    private Advertiser advertiser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anunciante);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        advertiser = (Advertiser) intent.getSerializableExtra("advertiser");

        tvNomeAnunciante = (TextView) findViewById(R.id.tvNomeAnunciante);
        tvTelefone = (TextView) findViewById(R.id.tvTelefone);
        tvEndereco = (TextView) findViewById(R.id.tvEndereco);
        tvNumero = (TextView) findViewById(R.id.tvNumero);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvNomeAnunciante.setText(advertiser.tradingName);
        tvTelefone.setText(advertiser.phoneNumber);
        tvEndereco.setText(advertiser.streetName);
        tvNumero.setText(advertiser.addressNumber);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}
