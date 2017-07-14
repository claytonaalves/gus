package com.kaora.anunciosapp.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.adapters.PublicacoesAdapter;
import com.kaora.anunciosapp.models.Publicacao;

import java.util.ArrayList;
import java.util.List;

public class PublicacoesActivity extends AppCompatActivity {

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
        publicacoes.add(new Publicacao("Vaga para Pedreiro", "A empresa xxx disponibiliza 2 vagas para auxiliar de pedreiro na construção xxx. Interessados entrar em contato..."));
        publicacoes.add(new Publicacao("Grande oferta de verduras e legumes", "Aproveite nossa grande oferta de legumes da quinta verde"));
        publicacoes.add(new Publicacao("Promoção de inauguração da boutique Brasil", "Para comemorar nossa inauguração, disponibilizamos para você os melhores preços da praça. Venha nos visitar e conferir."));
        publicacoes.add(new Publicacao("Comunicado Importante", "Este é o texto do comunicado de teste para definir a largura do TextView."));
        publicacoes.add(new Publicacao("Promoção Premiada", "Participe de nossa promoção premiada."));

        PublicacoesAdapter publicacoesAdapter = new PublicacoesAdapter(this, publicacoes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView rvPublicacoes = (RecyclerView) findViewById(R.id.rvPublicacoes);
        rvPublicacoes.setAdapter(publicacoesAdapter);
        rvPublicacoes.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_categorias, menu);

        return true;
    }

    private void criarNovoAnuncio() {
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
