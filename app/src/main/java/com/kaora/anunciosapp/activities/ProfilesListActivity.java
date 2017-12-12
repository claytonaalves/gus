package com.kaora.anunciosapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.adapters.AdvertiserListAdapter;
import com.kaora.anunciosapp.database.MyDatabaseHelper;
import com.kaora.anunciosapp.models.Advertiser;

public class ProfilesListActivity extends AppCompatActivity {

    private ListView profilesListView;
    private boolean manageProfilesMode;
    private MyDatabaseHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_list);

        database = MyDatabaseHelper.getInstance(this);

        Intent intent = getIntent();

        // This variable defines if this activity was opened with the intention to create
        // new publication or add/edit profiles.
        manageProfilesMode = intent.getBooleanExtra("manage_profiles_mode", false);

        profilesListView = (ListView) findViewById(R.id.lvPerfis);
    }

    @Override
    protected void onResume() {
        super.onResume();
        profilesListView.setAdapter(new AdvertiserListAdapter(database.allLocalProfiles(), this));
        if (manageProfilesMode) {
            setTitle("Perfis de Anunciante");
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startNewProfileActivity();
                }
            });
            profilesListView.setOnItemClickListener(new EditProfileClickHandler());
        } else {
            profilesListView.setOnItemClickListener(new SelectProfileClickHandler());
        }
    }

    private void startNewProfileActivity() {
        Intent intent = new Intent(this, AdvertiserProfileActivity.class);
        startActivity(intent);
    }

    private void startEditProfileActivity(Advertiser profile) {
        Intent intent = new Intent(this, AdvertiserProfileActivity.class);
        intent.putExtra("advertiser", profile);
        startActivity(intent);
    }

    private void startNewPublicationActivity(String guidAnunciante) {
        Intent intent = new Intent(this, NewPublicationActivity.class);
        intent.putExtra("guid_anunciante", guidAnunciante);
        startActivity(intent);
        finish();
    }

    private class SelectProfileClickHandler implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Advertiser profile = (Advertiser) view.getTag();
            startNewPublicationActivity(profile.advertiserGuid);
        }
    }

    private class EditProfileClickHandler implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Advertiser profile = (Advertiser) view.getTag();
            startEditProfileActivity(profile);
        }
    }

}
