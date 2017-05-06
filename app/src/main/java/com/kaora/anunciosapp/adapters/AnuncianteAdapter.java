package com.kaora.anunciosapp.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.models.Anunciante;

import java.util.List;


public class AnuncianteAdapter extends BaseAdapter {

    private final List<Anunciante> anunciantes;
    private final Activity activity;

    public AnuncianteAdapter(List<Anunciante> anunciantes, Activity activity) {
        this.anunciantes = anunciantes;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return anunciantes.size();
    }

    @Override
    public Object getItem(int position) {
        return anunciantes.get(position);
    }

    @Override
    public long getItemId(int position) {
        Anunciante anunciante = anunciantes.get(position);
        return anunciante._id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Anunciante anunciante = anunciantes.get(position);

        View view = activity.getLayoutInflater()
                .inflate(R.layout.item_listview_anunciante, parent, false);

        TextView tvNomeAnunciante = (TextView) view.findViewById(R.id.tvNomeAnunciante);
        tvNomeAnunciante.setText(anunciante.nomeFantasia);

        view.setTag(anunciante);

        return view;
    }
}
