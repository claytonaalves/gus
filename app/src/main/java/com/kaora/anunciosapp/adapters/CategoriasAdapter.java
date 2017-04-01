package com.kaora.anunciosapp.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.models.Categoria;

import java.util.List;

public class CategoriasAdapter extends BaseAdapter {

    private final List<Categoria> categorias;
    private final Activity activity;

    public CategoriasAdapter(List<Categoria> categorias, Activity activity) {
        this.categorias = categorias;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return categorias.size();
    }

    @Override
    public Object getItem(int position) {
        return categorias.get(position);
    }

    @Override
    public long getItemId(int position) {
        Categoria categoria = categorias.get(position);
        return categoria._id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = activity.getLayoutInflater()
                .inflate(R.layout.item_listview_categoria, parent, false);
        Categoria categoria = categorias.get(position);

        TextView nomeCategoria = (TextView) view.findViewById(R.id.tvNomeCategoria);
        nomeCategoria.setText(categoria.descricao);

        view.setTag(categoria);

        return view;
    }
}