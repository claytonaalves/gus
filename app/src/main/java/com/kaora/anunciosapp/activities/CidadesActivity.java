package com.kaora.anunciosapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.adapters.CityListAdapter;
import com.kaora.anunciosapp.models.Cidade;
import com.kaora.anunciosapp.rest.ApiRestAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CidadesActivity extends AppCompatActivity {

    public static final int PREFERENCIAS_SELECIONADAS = 1;

    List<Cidade> cidades;
    CityListAdapter cityListAdapter;
    private boolean configuracaoInicial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cidades);

        Intent intent = getIntent();
        configuracaoInicial = intent.getBooleanExtra("configuracaoInicial", false);

        if (!configuracaoInicial) {
            setTitle("Cidades");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        cidades = new ArrayList<>();
        preparaListaDeCidades(cidades);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setResult(PREFERENCIAS_SELECIONADAS);
        this.finish();
        return true;
    }

    private void preparaListaDeCidades(List<Cidade> cidades) {
        cityListAdapter = new CityListAdapter(this, cidades);
        ListView lvCidades = (ListView) findViewById(R.id.lvCidades);
        lvCidades.setAdapter(cityListAdapter);
        lvCidades.setOnItemClickListener(new CidadeItemClickListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApiRestAdapter api = ApiRestAdapter.getInstance();
        api.obtemCidades(new Callback<List<Cidade>>() {
            @Override
            public void onResponse(Call<List<Cidade>> call, Response<List<Cidade>> response) {
                atualizaListagemDeCidades(response.body());
            }

            @Override
            public void onFailure(Call<List<Cidade>> call, Throwable t) {
                Toast.makeText(CidadesActivity.this, "Não foi possível obter a lista de cidades", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizaListagemDeCidades(List<Cidade> cidades) {
        this.cidades.clear();
        for (Cidade cidade : cidades)
            this.cidades.add(cidade);
        cityListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == PreferenciasActivity.PREFERENCIA_SELECIONADA) {
            if (configuracaoInicial) {
                Intent intent = new Intent(this, PublicationListActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private class CidadeItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cidade cidade = cidades.get(position);
            Intent intent = new Intent(CidadesActivity.this, PreferenciasActivity.class);
            intent.putExtra("cidade", cidade);
            startActivityForResult(intent, PreferenciasActivity.SELECIONAR_PREFFERENCIAS);
        }
    }
}
