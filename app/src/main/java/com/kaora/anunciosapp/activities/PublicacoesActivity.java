package com.kaora.anunciosapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.adapters.PublicacoesAdapter;
import com.kaora.anunciosapp.database.MyDatabaseHelper;
import com.kaora.anunciosapp.models.PerfilAnunciante;
import com.kaora.anunciosapp.models.Preferencia;
import com.kaora.anunciosapp.models.Publicacao;
import com.kaora.anunciosapp.rest.ApiRestAdapter;
import com.kaora.components.CustomRecyclerView;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kaora.anunciosapp.R.id.view_publicacoes_vazia;

public class PublicacoesActivity extends AppCompatActivity {

    private PublicacoesAdapter publicacoesAdapter;
    private MyDatabaseHelper database;
    private List<Publicacao> publicacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicacoes);

        database = MyDatabaseHelper.getInstance(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criaNovaPublicacao();
            }
        });

        preparaListaDePublicacoes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtemPublicacoesDoServidor();
    }

    private void preparaListaDePublicacoes() {
        publicacoes = database.publicacoesSalvas();
        publicacoesAdapter = new PublicacoesAdapter(this, publicacoes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        CustomRecyclerView rvPublicacoes = (CustomRecyclerView) findViewById(R.id.rvPublicacoes);
        rvPublicacoes.setEmptyView(findViewById(view_publicacoes_vazia));
        rvPublicacoes.setAdapter(publicacoesAdapter);
        rvPublicacoes.setLayoutManager(layoutManager);
        adicionaFuncionalidadeSwipe(rvPublicacoes);
    }

    private void adicionaFuncionalidadeSwipe(CustomRecyclerView rvPublicacoes) {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(CustomRecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                marcaPublicacaoComoVista(position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvPublicacoes);
    }

    private void marcaPublicacaoComoVista(int position) {
        database.arquivaPublicacao(publicacoes.get(position));
        publicacoes.remove(position);
        publicacoesAdapter.notifyItemRemoved(position);
    }

    private void obtemPublicacoesDoServidor() {
        Date ultimaAtualizacao = obtemDataDaUltimaAtualizacao();
        List<Preferencia> preferencias = database.peferenciasSelecionadas();
        ApiRestAdapter webservice = ApiRestAdapter.getInstance();
        webservice.obtemPublicacoes(ultimaAtualizacao, preferencias, new Callback<List<Publicacao>>() {
            @Override
            public void onResponse(Call<List<Publicacao>> call, Response<List<Publicacao>> response) {
                database.salvaPublicacoes(response.body());
                atualizaListaDePublicacoes(response.body());
            }

            @Override
            public void onFailure(Call<List<Publicacao>> call, Throwable t) {
                Toast.makeText(PublicacoesActivity.this, "Falha ao obter publicações!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizaListaDePublicacoes(List<Publicacao> publicacoes) {
        for (Publicacao publicacao : publicacoes) {
            this.publicacoes.add(publicacao);
        }
        if (publicacoes.size()==1) {
            publicacoesAdapter.notifyItemInserted(0);
        } else {
            publicacoesAdapter.notifyDataSetChanged();
        }
        salvaDataDaUltimaAtualizacao();
    }

    private Date obtemDataDaUltimaAtualizacao() {
        SharedPreferences preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        long ultimaAtualizacao = preferences.getLong("ultima_atualizacao", 0);
        if (ultimaAtualizacao==0) {
            return new Date(0);
        } else {
            return new Date(ultimaAtualizacao);
        }
    }

    private void salvaDataDaUltimaAtualizacao() {
        SharedPreferences preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("ultima_atualizacao", new Date().getTime());
        editor.apply();
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
                criaNovaPublicacao();
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

    private void criaNovaPublicacao() {
        int qtdePerfisCadastrados = database.todosPerfis().size();
        if (qtdePerfisCadastrados==0) {
            mostraActivityCriacaoPerfil();
        } else if (qtdePerfisCadastrados==1) {
            mostraActivityNovoAnuncio();
        } else {
//            mostraActivitySelecaoPerfil();
        }
    }

    private void mostraActivityCriacaoPerfil() {
        Intent intent = new Intent(this, AvisoPerfilActivity.class);
        startActivity(intent);
    }

    private void mostraActivityNovoAnuncio() {
        PerfilAnunciante perfil = database.todosPerfis().get(0); // Pega o primeiro perfil
        Intent intent = new Intent(this, NovaPublicacaoActivity.class);
        intent.putExtra("guid_anunciante", perfil.guidAnunciante);
        startActivity(intent);
    }

}