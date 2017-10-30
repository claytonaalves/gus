package com.kaora.anunciosapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.adapters.AnuncianteAdapter;
import com.kaora.anunciosapp.models.Advertiser;
import com.kaora.anunciosapp.rest.ApiRestAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnunciantesActivity extends AppCompatActivity {

    private ListView lvAnunciantes;

    private int idCategoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anunciantes);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        idCategoria = intent.getIntExtra("category_id", 0);

        lvAnunciantes = (ListView) findViewById(R.id.lvAnunciantes);
        lvAnunciantes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                iniciaActivityAnunciante((Advertiser) view.getTag());
            }
        });
    }

    private void iniciaActivityAnunciante(Advertiser anunciante) {
        Intent intent = new Intent(this, AnuncianteActivity.class);
        intent.putExtra("advertiser", anunciante);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApiRestAdapter api = ApiRestAdapter.getInstance();
        api.anunciantesPorCategoria(new Callback<List<Advertiser>>() {
            @Override
            public void onResponse(Call<List<Advertiser>> call, Response<List<Advertiser>> response) {
                List<Advertiser> anunciantes = response.body();
                lvAnunciantes.setAdapter(new AnuncianteAdapter(anunciantes, AnunciantesActivity.this));
            }

            @Override
            public void onFailure(Call<List<Advertiser>> call, Throwable t) {

            }
        }, idCategoria);
    }
}
