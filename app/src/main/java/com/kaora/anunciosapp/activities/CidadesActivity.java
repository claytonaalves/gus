package com.kaora.anunciosapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.adapters.CidadesAdapter;
import com.kaora.anunciosapp.models.Cidade;
import com.kaora.anunciosapp.rest.ApiRestAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CidadesActivity extends AppCompatActivity {

    List<Cidade> cidades;
    CidadesAdapter cidadesAdapter;
    private boolean configuracaoInicial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cidades);

        Intent intent = getIntent();
        configuracaoInicial = intent.getBooleanExtra("configuracaoInicial", false);

        cidades = obtemCidades();
        preparaListaDeCidades(cidades);
    }

    private void preparaListaDeCidades(List<Cidade> cidades) {
        cidadesAdapter = new CidadesAdapter(this, cidades);
        ListView lvCidades = (ListView) findViewById(R.id.lvCidades);
        lvCidades.setAdapter(cidadesAdapter);
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
        cidadesAdapter.notifyDataSetChanged();
    }

    @NonNull
    private List<Cidade> obtemCidades() {
        List<Cidade> cidades = new ArrayList<>();
//        cidades.add(new Cidade("Alta Floresta", "MT"));
//        cidades.add(new Cidade("Apiacás", "MT"));
//        cidades.add(new Cidade("Monte Verde", "MT"));
//        cidades.add(new Cidade("Bandeirantes", "MT"));
        return cidades;
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == PreferenciasActivity.PREFERENCIA_SELECIONADA) {
            if (configuracaoInicial) {
                Intent intent = new Intent(this, PublicacoesActivity.class);
                startActivity(intent);
            }
            finish();
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
