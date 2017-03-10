package com.kaora.anunciosapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.database.MyDatabaseHelper;
import com.kaora.anunciosapp.models.Categoria;
import com.kaora.anunciosapp.rest.ApiRestAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreferenciasActivity extends AppCompatActivity {

    private MyDatabaseHelper database;

    private LinearLayout preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferencias);

        database = new MyDatabaseHelper(this);

        if (exitePreferenciaConfigurada()) {
            iniciaActivityCategorias();
            finish();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        preferencias = (LinearLayout) findViewById(R.id.preferencias_list);
    }

    private boolean exitePreferenciaConfigurada() {
        return database.categoriasPreferidas().size()>0;
    }

    private void iniciaActivityCategorias() {
        Intent intent = new Intent(this, CategoriasActivity.class);
        this.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ApiRestAdapter restApi = new ApiRestAdapter();

        restApi.obtemCategorias(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                database.atualizaCategorias(response.body());
                preencheListaDePreferencias(response.body());
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                Toast.makeText(PreferenciasActivity.this, "Não foi possível atualizar as activity_categorias!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem intem) {
        database.salvaPreferencias(preferenciasSelecionadas());
        this.finish();
        return true;
    }

    private void preencheListaDePreferencias(List<Categoria> categorias) {
        preferencias.removeAllViews();
        for (Categoria categoria : categorias) {
            CheckBox checkbox = new CheckBox(this);
            checkbox.setText(categoria.descricao);
            checkbox.setTag(categoria);
            preferencias.addView(checkbox);
        }
    }

    private List<Categoria> preferenciasSelecionadas() {
        List<Categoria> result = new ArrayList<>();
        for (int i=0; i<preferencias.getChildCount(); i++) {
            CheckBox checkbox = (CheckBox) preferencias.getChildAt(i);
            if (checkbox.isChecked()) {
                result.add((Categoria) checkbox.getTag());
            }
        }
        return result;
    }
}
