package com.kaora.anunciosapp.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.adapters.AdvertiserListAdapter;
import com.kaora.anunciosapp.models.Advertiser;
import com.kaora.anunciosapp.rest.ApiRestAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdvertiserListActivity extends AppCompatActivity {

    private ApiRestAdapter webservice;

    private ListView advertiserListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertiser_list);

        webservice = ApiRestAdapter.getInstance();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        handleIntent(getIntent());

        advertiserListView = (ListView) findViewById(R.id.lvAnunciantes);
        advertiserListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showAdvertiserDetailsActivity((Advertiser) view.getTag());
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            webservice.searchAdvertiser(query, new Callback<List<Advertiser>>() {
                @Override
                public void onResponse(Call<List<Advertiser>> call, Response<List<Advertiser>> response) {
                    List<Advertiser> advertisers = response.body();
                    advertiserListView.setAdapter(new AdvertiserListAdapter(advertisers, AdvertiserListActivity.this));
                }

                @Override
                public void onFailure(Call<List<Advertiser>> call, Throwable t) {

                }
            });
        }
    }

    private void showAdvertiserDetailsActivity(Advertiser anunciante) {
        Intent intent = new Intent(this, AdvertiserDetailActivity.class);
        intent.putExtra("advertiser", anunciante);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}
