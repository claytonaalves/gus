package com.kaora.anunciosapp.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.models.PerfilAnunciante;

import java.util.List;

public class PerfisAdapter extends BaseAdapter {

    private final List<PerfilAnunciante> perfis;
    private final Activity activity;

    public PerfisAdapter(List<PerfilAnunciante> perfis, Activity activity) {
        this.perfis = perfis;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return perfis.size();
    }

    @Override
    public Object getItem(int position) {
        return perfis.get(position);
    }

    @Override
    public long getItemId(int position) {
        PerfilAnunciante perfil = perfis.get(position);
        return perfil._id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PerfilAnunciante perfil = perfis.get(position);

        View view = activity.getLayoutInflater()
                .inflate(R.layout.item_listview_anunciante, parent, false);

        TextView tvNomeAnunciante = (TextView) view.findViewById(R.id.tvNomeAnunciante);
        tvNomeAnunciante.setText(perfil.nome);

        view.setTag(perfil);

        return view;
    }
}
