package com.kaora.anunciosapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.database.MyDatabaseHelper;
import com.kaora.anunciosapp.models.PublicationCategory;
import com.kaora.anunciosapp.models.Cidade;
import com.kaora.anunciosapp.models.Preference;
import com.kaora.anunciosapp.rest.ApiRestAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreferenciasActivity extends AppCompatActivity {

    public static final int SELECIONAR_PREFFERENCIAS = 1;
    public static final int PREFERENCIA_SELECIONADA = 1;
    public static final int NENHUMA_PREFERENCIA_SELECIONADA = 2;

    private MyDatabaseHelper database;
    private ListView lvPreferencias;
    private TextView tvNomeCidade;
    private PreferenciasAdapter preferenciasAdapter;
    private Cidade cidade;
    private List<Preference> preferences;
    private List<Preference> preferenciasSelecionadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferencias);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        database = MyDatabaseHelper.getInstance(this);

        cidade = getIntent().getParcelableExtra("cidade");

        tvNomeCidade = (TextView) findViewById(R.id.tvNomeCidade);
        tvNomeCidade.setText(cidade.nome);
    }

    @Override
    protected void onResume() {
        super.onResume();
        preparaListaDePreferencias();
        obtemCategoriasDaAPI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (usuarioSelecionouAlgumaPreferencia()) {
            setResult(PREFERENCIA_SELECIONADA);
        } else {
            setResult(NENHUMA_PREFERENCIA_SELECIONADA);
        }
        this.finish();
        return true;
    }

    private void preparaListaDePreferencias() {
        preferenciasSelecionadas = database.getPreferencesByCity(1);
        preferences = new ArrayList<>();
        lvPreferencias = (ListView) findViewById(R.id.lvPreferencias);
        preferenciasAdapter = new PreferenciasAdapter(this, preferences);
        lvPreferencias.setAdapter(preferenciasAdapter);
    }

    private void obtemCategoriasDaAPI() {
        ApiRestAdapter webservice = ApiRestAdapter.getInstance();

        webservice.obtemCategorias(cidade.idCidade, new Callback<List<PublicationCategory>>() {
            @Override
            public void onResponse(Call<List<PublicationCategory>> call, Response<List<PublicationCategory>> response) {
                preencheListaDePreferencias(response.body());
            }

            @Override
            public void onFailure(Call<List<PublicationCategory>> call, Throwable t) {
                Toast.makeText(PreferenciasActivity.this, "Não foi possível obter a lista de publicationCategories!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void preencheListaDePreferencias(List<PublicationCategory> publicationCategories) {
        preferences.clear();
        for (PublicationCategory publicationCategory : publicationCategories) {
            Preference preference = new Preference(publicationCategory.idCategoria, publicationCategory.descricao);
            preference.selected = preferenciasSelecionadas.contains(preference);
            preferences.add(preference);
        }
        preferenciasAdapter.notifyDataSetChanged();
    }

    private boolean usuarioSelecionouAlgumaPreferencia() {
        for (Preference preference : preferences) {
            if (preference.selected)
                return true;
        }
        return false;
    }

}
