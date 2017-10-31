package com.kaora.anunciosapp.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.adapters.PerfisAdapter;
import com.kaora.anunciosapp.database.MyDatabaseHelper;
import com.kaora.anunciosapp.models.Advertiser;

public class SelecionarPerfilActivity extends AppCompatActivity {

    ListView lvPerfis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecionar_perfil);

        MyDatabaseHelper database = MyDatabaseHelper.getInstance(this);

        lvPerfis = (ListView) findViewById(R.id.lvPerfis);
        lvPerfis.setAdapter(new PerfisAdapter(database.allProfiles(), this));

        Intent intent = getIntent();
        int modoEdicao = intent.getIntExtra("modoEdicao", 0);

        if (modoEdicao==1) {
            setTitle("Perfis de Anunciante");
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostraActivityCriacaoPerfil();
                }
            });
            lvPerfis.setOnItemClickListener(new EdicaoPerfilClickHandler());
        } else {
            lvPerfis.setOnItemClickListener(new SelecaoPerfilClickHandler());
        }
    }

    private void mostraActivityCriacaoPerfil() {
        Intent intent = new Intent(this, NovoPerfilActivity.class);
        startActivity(intent);
    }

    private void mostraActivityNovaPublicacao(String guidAnunciante) {
        Intent intent = new Intent(this, NovaPublicacaoActivity.class);
        intent.putExtra("guid_anunciante", guidAnunciante);
        startActivity(intent);
        finish();
    }

    private class SelecaoPerfilClickHandler implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Advertiser perfil = (Advertiser) view.getTag();
            mostraActivityNovaPublicacao(perfil.advertiserGuid);
        }
    }

    private class EdicaoPerfilClickHandler implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO: abrir perfil para edição
        }
    }

}
