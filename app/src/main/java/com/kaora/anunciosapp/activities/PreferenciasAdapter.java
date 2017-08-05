package com.kaora.anunciosapp.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.database.MyDatabaseHelper;
import com.kaora.anunciosapp.models.Preferencia;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.List;


public class PreferenciasAdapter extends ArrayAdapter<Preferencia> {

    private MyDatabaseHelper database;

    public PreferenciasAdapter(@NonNull Context context, List<Preferencia> preferencias) {
        super(context, 0, preferencias);
        database = MyDatabaseHelper.getInstance(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final Preferencia preferencia = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.li_preferencia, parent, false);
        }

        TextView tvPreferencia = (TextView) convertView.findViewById(R.id.descricaoPreferencia);
        SwitchButton switchButton = (SwitchButton) convertView.findViewById(R.id.switchButton);
        switchButton.setOnCheckedChangeListener(null);

        assert preferencia != null;
        tvPreferencia.setText(preferencia.descricao);
        switchButton.setChecked(preferencia.selecionanda);

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferencia.selecionanda = isChecked;
                database.salvaPreferencia(preferencia);
            }
        });

        return convertView;

    }
}
