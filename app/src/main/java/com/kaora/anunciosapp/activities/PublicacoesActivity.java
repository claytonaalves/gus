package com.kaora.anunciosapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.adapters.PublicacoesAdapter;
import com.kaora.anunciosapp.models.Publicacao;
import com.kaora.components.CustomRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PublicacoesActivity extends AppCompatActivity {

    private CustomRecyclerView rvPublicacoes;
    private PublicacoesAdapter publicacoesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicacoes);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criarNovoAnuncio();
            }
        });

        List<Publicacao> publicacoes = new ArrayList<>();

        View emptyView = findViewById(R.id.view_publicacoes_vazia);

        publicacoesAdapter = new PublicacoesAdapter(this, publicacoes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvPublicacoes = (CustomRecyclerView) findViewById(R.id.rvPublicacoes);
        rvPublicacoes.setEmptyView(emptyView);
        rvPublicacoes.setAdapter(publicacoesAdapter);
        rvPublicacoes.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_categorias, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_criar_anuncio:
                criarNovoAnuncio();
                break;
            case R.id.action_meus_anuncios:
//                mostraActivityMeusAnuncios();
                break;
            case R.id.action_perfis:
//                mostraActivityPerfis();
                break;
            case R.id.action_configuracoes:
                mostraActivityCidades();
                break;
        }
        return true;
    }

    private void mostraActivityCidades() {
        startActivity(new Intent(this, CidadesActivity.class));
    }

    private void criarNovoAnuncio() {
        publicacoesAdapter.notifyItemRemoved(0);
//        int qtdePerfisCadastrados = database.todosPerfis().size();
//        if (qtdePerfisCadastrados==0) {
//            mostraActivityCriacaoPerfil();
//        } else if (qtdePerfisCadastrados==1) {
//            mostraActivityNovoAnuncio();
//        } else {
//            mostraActivitySelecaoPerfil();
//        }
    }

}
