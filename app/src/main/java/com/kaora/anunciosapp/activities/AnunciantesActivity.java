package com.kaora.anunciosapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.adapters.AnuncianteAdapter;
import com.kaora.anunciosapp.database.MyDatabaseHelper;
import com.kaora.anunciosapp.models.Anunciante;
import com.kaora.anunciosapp.rest.ApiRestAdapter;

import java.util.ArrayList;
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
        idCategoria = intent.getIntExtra("idCategoria", 0);

        lvAnunciantes = (ListView) findViewById(R.id.lvAnunciantes);
        lvAnunciantes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                iniciaActivityAnunciante((Anunciante) view.getTag());
            }
        });
    }

    private void iniciaActivityAnunciante(Anunciante anunciante) {
        Intent intent = new Intent(this, AnuncianteActivity.class);
        intent.putExtra("anunciante", anunciante);
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
        ApiRestAdapter api = new ApiRestAdapter();
        api.anunciantesPorCategoria(new Callback<List<Anunciante>>() {
            @Override
            public void onResponse(Call<List<Anunciante>> call, Response<List<Anunciante>> response) {
                List<Anunciante> anunciantes = response.body();
                lvAnunciantes.setAdapter(new AnuncianteAdapter(anunciantes, AnunciantesActivity.this));
            }

            @Override
            public void onFailure(Call<List<Anunciante>> call, Throwable t) {

            }
        }, idCategoria);
    }
}
