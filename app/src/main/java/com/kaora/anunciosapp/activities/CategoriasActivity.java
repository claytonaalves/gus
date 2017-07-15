package com.kaora.anunciosapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.kaora.anunciosapp.Config;
import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.adapters.CategoriasAdapter;
import com.kaora.anunciosapp.database.MyDatabaseHelper;
import com.kaora.anunciosapp.models.Categoria;
import com.kaora.anunciosapp.models.PerfilAnunciante;
import com.kaora.anunciosapp.rest.ApiRestAdapter;
import com.kaora.anunciosapp.utils.NotificationUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriasActivity extends AppCompatActivity {

    MyDatabaseHelper database;
    List<Categoria> categorias;

    CategoriasAdapter categoriasAdapter;
    private BroadcastReceiver notificationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

//        database = MyDatabaseHelper.getInstance(this);
//        categorias = database.categoriasPreferidas();
//
//        categoriasAdapter = new CategoriasAdapter(categorias, this);
//        ListView lvCategorias = (ListView) findViewById(R.id.lvOfertas);
//        lvCategorias.setAdapter(categoriasAdapter);
//
//        lvCategorias.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Categoria categoriaSelecionada = (Categoria) view.getTag();
//                Intent intent = new Intent(CategoriasActivity.this, AnunciantesActivity.class);
//                intent.putExtra("idCategoria", categoriaSelecionada.idCategoria);
//                startActivity(intent);
//            }
//        });
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                criarNovoAnuncio();
//            }
//        });
//
//        preparaBroadcastReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtemCategoriasDaAPI();
        registraBroadcastReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationBroadcastReceiver);
        super.onPause();
    }

    private void registraBroadcastReceiver() {
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(notificationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(notificationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    private void obtemCategoriasDaAPI() {
        ApiRestAdapter restApi = ApiRestAdapter.getInstance();

        restApi.obtemCategorias(1, new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                database.atualizaCategorias(response.body());
                atualizaListagemCategorias();
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
//                Toast.makeText(PreferenciasActivity.this, "Não foi possível atualizar as activity_categorias!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizaListagemCategorias() {
//        categorias.clear();
//        for (Categoria categoria : database.categoriasPreferidas()) {
//            categorias.add(categoria);
//        }
//        categoriasAdapter.notifyDataSetChanged();
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
                mostraActivityMeusAnuncios();
                break;
            case R.id.action_perfis:
                mostraActivityPerfis();
                break;
            case R.id.action_configuracoes:
                mostraActivityPreferencias();
                break;
        }
        return true;
    }

    private void criarNovoAnuncio() {
        int qtdePerfisCadastrados = database.todosPerfis().size();
        if (qtdePerfisCadastrados==0) {
            mostraActivityCriacaoPerfil();
        } else if (qtdePerfisCadastrados==1) {
            mostraActivityNovoAnuncio();
        } else {
            mostraActivitySelecaoPerfil();
        }
    }

    private void mostraActivityCriacaoPerfil() {
        Intent intent = new Intent(this, AvisoPerfilActivity.class);
        startActivity(intent);
    }

    private void mostraActivitySelecaoPerfil() {
        Intent intent = new Intent(this, SelecionarPerfilActivity.class);
        startActivity(intent);
    }

    private void mostraActivityPerfis() {
        Intent intent = new Intent(this, SelecionarPerfilActivity.class);
        intent.putExtra("modoEdicao", 1);
        startActivity(intent);
    }

    private void mostraActivityMeusAnuncios() {
        Intent intent = new Intent(this, MeusAnunciosActivity.class);
        startActivity(intent);
    }

    private void mostraActivityNovoAnuncio() {
        Intent intent = new Intent(this, NovoAnuncioActivity.class);
        intent.putExtra("idPerfil", 1L); // id do primeiro perfil
        startActivity(intent);
    }

    private void mostraActivityPreferencias() {
        PerfilAnunciante perfil = database.selecionaPerfil();
        Intent intent = new Intent(this, PreferenciasActivity.class);
        intent.putExtra("idperfil", perfil._id);
        startActivity(intent);
    }

    private void preparaBroadcastReceiver() {
        notificationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
//                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Novo anúncio ", Toast.LENGTH_LONG).show();
                }
            }
        };

    }

}
