package com.kaora.anunciosapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ExpandedMenuView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.adapters.CategoriasAdapter;
import com.kaora.anunciosapp.database.MyDatabaseHelper;
import com.kaora.anunciosapp.models.Categoria;

import java.util.List;

public class CategoriasActivity extends AppCompatActivity {

    MyDatabaseHelper database;
    List<Categoria> categorias;

    CategoriasAdapter categoriasAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        database = new MyDatabaseHelper(this);
        categorias = database.categoriasPreferidas();

        categoriasAdapter = new CategoriasAdapter(categorias, this);
        ListView lvCategorias = (ListView) findViewById(R.id.lvOfertas);
        lvCategorias.setAdapter(categoriasAdapter);

        lvCategorias.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Categoria categoriaSelecionada = (Categoria) view.getTag();
                Intent intent = new Intent(CategoriasActivity.this, AnunciantesActivity.class);
                intent.putExtra("idCategoria", categoriaSelecionada._id);
                startActivity(intent);
            }
        });
    }

//    private void atualizaListView() {
//        categoriasAdapter.notifyDataSetChanged();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_categorias, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_tela_inicial:
                Toast.makeText(this, "menu tela inicial clicado", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.action_configuracoes:
                // abrir activity de configurações
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }

}
