package com.kaora.anunciosapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.database.MyDatabaseHelper;

public class LaunchActivity extends AppCompatActivity {

    MyDatabaseHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        database = MyDatabaseHelper.getInstance(this);

        Intent intent;

        if (database.preferenciasDefinidas()) {
            intent = new Intent(this, PublicacoesActivity.class);
        } else {
            intent = new Intent(this, CidadesActivity.class);
            intent.putExtra("configuracaoInicial", true);
        }

        startActivity(intent);
        finish();
    }

}
