package com.kaora.anunciosapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.models.Advertiser;
import com.kaora.anunciosapp.rest.ApiRestAdapter;

public class AdvertiserDetailActivity extends AppCompatActivity {

    TextView tvNomeAnunciante;
    TextView tvTelefone;
    TextView tvEndereco;
    TextView tvNumero;
    SimpleDraweeView image;

    private Advertiser advertiser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertiser_detail);

        setTitle("Anunciante");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        advertiser = (Advertiser) intent.getSerializableExtra("advertiser");

        tvNomeAnunciante = (TextView) findViewById(R.id.tvNomeAnunciante);
        tvTelefone = (TextView) findViewById(R.id.tvTelefone);
        tvEndereco = (TextView) findViewById(R.id.tvEndereco);
        tvNumero = (TextView) findViewById(R.id.tvNumero);
        image = (SimpleDraweeView) findViewById(R.id.main_image);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvNomeAnunciante.setText(advertiser.tradingName);
        tvTelefone.setText(advertiser.phoneNumber);
        tvEndereco.setText(advertiser.streetName);
        tvNumero.setText(advertiser.addressNumber);
        if (advertiser.imageFile != null) {
            if (!advertiser.imageFile.equals("")) {
                image.setImageURI(ApiRestAdapter.ADVERTISERS_IMAGE_PATH + advertiser.imageFile);
            } else {
                image.setImageResource(R.drawable.photo_gray);
            }
        } else {
            image.setImageResource(R.drawable.photo_gray);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}
