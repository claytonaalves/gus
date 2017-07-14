package com.kaora.anunciosapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.models.Cidade;

import java.util.List;

public class CidadesAdapter extends ArrayAdapter<Cidade> {

    public CidadesAdapter(@NonNull Context context, List<Cidade> cidades) {
        super(context, 0, cidades);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Cidade cidade = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.li_cidade, parent, false);
        }
        TextView tvCidade = (TextView) convertView.findViewById(R.id.tvNomeCidade);
        tvCidade.setText(cidade.nome);
        return convertView;
    }

}
