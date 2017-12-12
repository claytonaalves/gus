package com.kaora.anunciosapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.kaora.anunciosapp.Config;
import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.adapters.CategoryListAdapter;
import com.kaora.anunciosapp.database.MyDatabaseHelper;
import com.kaora.anunciosapp.models.PublicationCategory;
import com.kaora.anunciosapp.rest.ApiRestAdapter;
import com.kaora.anunciosapp.utils.NotificationUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriasActivity extends AppCompatActivity {

    MyDatabaseHelper database;
    List<PublicationCategory> publicationCategories;

    CategoryListAdapter categoryListAdapter;
    private BroadcastReceiver notificationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

//        database = MyDatabaseHelper.getInstance(this);
//        publicationCategories = database.categoriasPreferidas();
//
//        categoryListAdapter = new CategoryListAdapter(publicationCategories, this);
//        ListView lvCategorias = (ListView) findViewById(R.id.lvOfertas);
//        lvCategorias.setAdapter(categoryListAdapter);
//
//        lvCategorias.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                PublicationCategory categoriaSelecionada = (PublicationCategory) view.getTag();
//                Intent intent = new Intent(CategoriasActivity.this, AdvertiserListActivity.class);
//                intent.putExtra("category_id", categoriaSelecionada.category_id);
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

        restApi.obtemCategorias(1, new Callback<List<PublicationCategory>>() {
            @Override
            public void onResponse(Call<List<PublicationCategory>> call, Response<List<PublicationCategory>> response) {
                database.updateCategories(response.body());
                atualizaListagemCategorias();
            }

            @Override
            public void onFailure(Call<List<PublicationCategory>> call, Throwable t) {
//                Toast.makeText(PreferenciasActivity.this, "Não foi possível atualizar as activity_categorias!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizaListagemCategorias() {
//        publicationCategories.clear();
//        for (PublicationCategory categoria : database.categoriasPreferidas()) {
//            publicationCategories.add(categoria);
//        }
//        categoryListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_overflow, menu);

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
        int localProfileCount = database.allLocalProfiles().size();
        if (localProfileCount==0) {
            mostraActivityCriacaoPerfil();
        } else if (localProfileCount==1) {
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
        Intent intent = new Intent(this, ProfilesListActivity.class);
        startActivity(intent);
    }

    private void mostraActivityPerfis() {
        Intent intent = new Intent(this, ProfilesListActivity.class);
        intent.putExtra("modoEdicao", 1);
        startActivity(intent);
    }

    private void mostraActivityMeusAnuncios() {
        Intent intent = new Intent(this, MeusAnunciosActivity.class);
        startActivity(intent);
    }

    private void mostraActivityNovoAnuncio() {
        Intent intent = new Intent(this, NewPublicationActivity.class);
        intent.putExtra("idPerfil", 1L); // id do primeiro perfil
        startActivity(intent);
    }

    private void mostraActivityPreferencias() {
//        Advertiser perfil = database.getAdvertiserByGuid();
//        Intent intent = new Intent(this, PreferenciasActivity.class);
//        intent.putExtra("idperfil", perfil._id);
//        startActivity(intent);
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
