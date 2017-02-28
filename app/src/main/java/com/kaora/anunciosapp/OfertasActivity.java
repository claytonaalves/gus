package com.kaora.anunciosapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.kaora.anunciosapp.adapters.OfertaAdapter;
import com.kaora.anunciosapp.models.Categoria;

import java.util.ArrayList;
import java.util.List;

public class OfertasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ofertas);

        List<Categoria> categorias = carregaCategorias();

        OfertaAdapter adapter = new OfertaAdapter(categorias, this);
        ListView lvOfertas = (ListView) findViewById(R.id.lvOfertas);
        lvOfertas.setAdapter(adapter);

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


    private List<Categoria> carregaCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        categorias.add(new Categoria(1, "Anúncios"));
        categorias.add(new Categoria(1, "Ofertas"));
        return categorias;
    }

}
