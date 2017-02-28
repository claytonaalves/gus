package com.kaora.anunciosapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

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

    private List<Categoria> carregaCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        categorias.add(new Categoria(1, "Anúncios"));
        categorias.add(new Categoria(1, "Ofertas"));
        return categorias;
    }

}
