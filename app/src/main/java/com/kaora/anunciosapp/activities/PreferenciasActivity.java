package com.kaora.anunciosapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.models.Categoria;
import com.kaora.anunciosapp.models.Cidade;
import com.kaora.anunciosapp.models.Preferencia;
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

    //    private MyDatabaseHelper database;
    private ListView lvPreferencias;
    private TextView tvNomeCidade;
    PreferenciasAdapter preferenciasAdapter;
    private List<Preferencia> preferencias;
    private Cidade cidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferencias);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        cidade = getIntent().getParcelableExtra("cidade");

        tvNomeCidade = (TextView) findViewById(R.id.tvNomeCidade);
        tvNomeCidade.setText(cidade.nome);

        preparaListaDePreferencias();
    }

    private void preparaListaDePreferencias() {
        preferencias = new ArrayList<>();
//        database = MyDatabaseHelper.getInstance(this);
        lvPreferencias = (ListView) findViewById(R.id.lvPreferencias);
        preferenciasAdapter = new PreferenciasAdapter(this, preferencias);
        lvPreferencias.setAdapter(preferenciasAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        preencheListaDePreferencias(database.todasCategorias());
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

    private void obtemCategoriasDaAPI() {
        ApiRestAdapter webservice = ApiRestAdapter.getInstance();

        webservice.obtemCategorias(cidade.idCidade, new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                preencheListaDePreferencias(response.body());
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                Toast.makeText(PreferenciasActivity.this, "Não foi possível obter a lista de categorias!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean usuarioSelecionouAlgumaPreferencia() {
        for (Preferencia preferencia : preferencias) {
            if (preferencia.selecionanda)
                return true;
        }
        return false;
    }

    private void preencheListaDePreferencias(List<Categoria> categorias) {
//                database.atualizaCategorias(response.body());
        preferencias.clear();
        for (Categoria categoria: categorias) {
            preferencias.add(new Preferencia(categoria.idCategoria, categoria.descricao));
        }
        preferenciasAdapter.notifyDataSetChanged();
    }

    private List<Categoria> preferenciasSelecionadas() {
//        CheckBox checkbox;
//        List<Categoria> result = new ArrayList<>();
//        for (int i=0; i<preferencias.getChildCount(); i++) {
//            checkbox = (CheckBox) preferencias.getChildAt(i);
//            if (checkbox.isChecked()) {
//                result.add((Categoria) checkbox.getTag());
//            }
//        }
//        return result;
        return null;
    }
}
