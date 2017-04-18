package com.kaora.anunciosapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.adapters.PerfisAdapter;
import com.kaora.anunciosapp.database.MyDatabaseHelper;

public class SelecionarPerfilActivity extends AppCompatActivity {

    ListView lvPerfis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecionar_perfil);

        MyDatabaseHelper database = MyDatabaseHelper.getInstance(this);

        lvPerfis = (ListView) findViewById(R.id.lvPerfis);
        lvPerfis.setAdapter(new PerfisAdapter(database.todosPerfis(), this));
    }

}
