package com.kaora.anunciosapp.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.kaora.anunciosapp.Config;
import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.adapters.PublicationsAdapter;
import com.kaora.anunciosapp.database.MyDatabaseHelper;
import com.kaora.anunciosapp.models.Advertiser;
import com.kaora.anunciosapp.models.Preference;
import com.kaora.anunciosapp.models.Publication;
import com.kaora.anunciosapp.receivers.MyAlarmReceiver;
import com.kaora.anunciosapp.rest.ApiRestAdapter;
import com.kaora.components.CustomRecyclerView;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kaora.anunciosapp.R.id.view_publicacoes_vazia;

public class PublicationListActivity extends AppCompatActivity {

    private PublicationsAdapter publicationsAdapter;
    private MyDatabaseHelper database;
    private List<Publication> publications;
    private CustomRecyclerView rvPublicacoes;

    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver broadcastReceiver;

    private SwipeRefreshLayout swipeRefreshLayoutLayout;

    private String deviceId;
    private List<Preference> preferences;

//    private Menu overflowMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication_list);

        deviceId = getDeviceId();
        database = MyDatabaseHelper.getInstance(this);
        database.removeOverduePublications();
        preferences = database.getSelectedPreferences();

        initializeInterface();
        initializeBroadcastManager();
        setupPublicationList();
        initializeRemoveOverduePublicationsSchedule();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchPublicationsFromWebService(deviceId, preferences);
        broadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        broadcastManager.unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (ViewConfiguration.get(this).hasPermanentMenuKey()) {
            // Device with hardware menu button
            inflater.inflate(R.menu.main_menu_overflow_hardware_button, menu);
        } else {
            // Device without hardware menu button
            inflater.inflate(R.menu.main_menu_overflow, menu);
        }
//        overflowMenu = menu;
        return true;
    }

//    @Override
//    public boolean onKeyUp(int keycode, KeyEvent e) {
//        switch(keycode) {
//            case KeyEvent.KEYCODE_MENU:
//                if (overflowMenu !=null) {
//                    overflowMenu.performIdentifierAction(R.id.menu_overflow, 0);
//                }
//        }
//        return super.onKeyUp(keycode, e);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_criar_anuncio:
                createNewPublication();
                break;
            case R.id.action_meus_anuncios:
//                mostraActivityMeusAnuncios();
                break;
            case R.id.action_perfis:
                showProfilesActivity();
                break;
            case R.id.action_configuracoes:
                showCitiesActivity();
                break;
        }
        return true;
    }

    private void initializeBroadcastManager() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    fetchPublicationsFromWebService(deviceId, preferences);
                }
            }
        };
    }

    private void initializeInterface() {
        swipeRefreshLayoutLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayoutLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPublicationsFromWebService(deviceId, preferences);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewPublication();
            }
        });
    }

    private void setupPublicationList() {
        publications = database.getSavedPublications();
        publicationsAdapter = new PublicationsAdapter(this, publications);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvPublicacoes = (CustomRecyclerView) findViewById(R.id.rvPublicacoes);
        rvPublicacoes.setEmptyView(findViewById(view_publicacoes_vazia));
        rvPublicacoes.setAdapter(publicationsAdapter);
        rvPublicacoes.setLayoutManager(layoutManager);
        addSwipeFeature(rvPublicacoes);
    }

    // Swipe to left/right to delete ad
    private void addSwipeFeature(CustomRecyclerView rvPublicacoes) {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(CustomRecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                archivePublication(position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvPublicacoes);
    }

    private void archivePublication(int position) {
        Publication publication = publications.get(position);
        publication.archived = true;
        database.savePublication(publication);
        publications.remove(position);
        publicationsAdapter.notifyItemRemoved(position);
    }

    private void updatePublicationList(List<Publication> publications) {
        for (Publication publication : publications) {
            this.publications.add(publication);
        }
        int lastItemPos = this.publications.size() - 1;
        if (publications.size() == 1) {
            publicationsAdapter.notifyItemInserted(lastItemPos);
        } else {
            publicationsAdapter.notifyDataSetChanged();
        }
        if (lastItemPos >= 0)
            rvPublicacoes.smoothScrollToPosition(lastItemPos);
    }

    private String getDeviceId() {
        SharedPreferences preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String deviceId = preferences.getString("device_id", "");
        if (deviceId.equals("")) {
            deviceId = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("device_id", deviceId);
            editor.apply();
        }
        return deviceId;
    }

    private void showCitiesActivity() {
        startActivityForResult(new Intent(this, CidadesActivity.class), CidadesActivity.PREFERENCIAS_SELECIONADAS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == CidadesActivity.PREFERENCIAS_SELECIONADAS) {
            // get updated preferences
            preferences = database.getSelectedPreferences();
            fetchPublicationsFromWebService(deviceId, preferences);
        }
    }

    private void fetchPublicationsFromWebService(String deviceId, List<Preference> preferences) {
        ApiRestAdapter webservice = ApiRestAdapter.getInstance();
        webservice.obtemPublicacoes(deviceId, preferences, new Callback<List<Publication>>() {
            @Override
            public void onResponse(Call<List<Publication>> call, Response<List<Publication>> response) {
                database.savePublications(response.body());
                updatePublicationList(response.body());
                swipeRefreshLayoutLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Publication>> call, Throwable t) {
                Log.d("erro", t.toString());
                swipeRefreshLayoutLayout.setRefreshing(false);
                Toast.makeText(PublicationListActivity.this, "Falha ao obter publicações!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewPublication() {
        int qtdePerfisCadastrados = database.allProfiles().size();
        if (qtdePerfisCadastrados == 0) {
            mostraActivityCriacaoPerfil();
        } else if (qtdePerfisCadastrados == 1) {
            mostraActivityNovoAnuncio();
        } else {
            mostraActivitySelecaoPerfil();
        }
    }

    private void mostraActivityCriacaoPerfil() {
        Intent intent = new Intent(this, AvisoPerfilActivity.class);
        startActivity(intent);
    }

    private void mostraActivityNovoAnuncio() {
        Advertiser perfil = database.allProfiles().get(0); // Pega o primeiro perfil
        Intent intent = new Intent(this, NewPublicationActivity.class);
        intent.putExtra("guid_anunciante", perfil.advertiserGuid);
        startActivity(intent);
    }

    private void showProfilesActivity() {
        Intent intent = new Intent(this, SelectAdvertiserProfileActivity.class);
        intent.putExtra("modoEdicao", 1);
        startActivity(intent);
    }

    private void mostraActivitySelecaoPerfil() {
        Intent intent = new Intent(this, SelectAdvertiserProfileActivity.class);
        intent.putExtra("modoEdicao", 0);
        startActivity(intent);
    }

    /*
     * Schedules the removal of overdue publications
     */
    private void initializeRemoveOverduePublicationsSchedule() {
        Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                AlarmManager.INTERVAL_DAY, pIntent);
    }

}
