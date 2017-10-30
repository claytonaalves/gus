package com.kaora.anunciosapp.adapters;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.models.Advertiser;
import com.kaora.anunciosapp.rest.ApiRestAdapter;

import java.util.List;

public class PerfisAdapter extends BaseAdapter {

    private final List<Advertiser> perfis;
    private final Activity activity;

    public PerfisAdapter(List<Advertiser> perfis, Activity activity) {
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
        Advertiser perfil = perfis.get(position);
//        return perfil._id;
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Advertiser perfil = perfis.get(position);

        View view = activity.getLayoutInflater()
                .inflate(R.layout.item_listview_anunciante, parent, false);

        TextView tvNomeAnunciante = (TextView) view.findViewById(R.id.tvNomeAnunciante);
        tvNomeAnunciante.setText(perfil.nomeFantasia);

        SimpleDraweeView profileLogo = (SimpleDraweeView) view.findViewById(R.id.imgLogoAnunciante);
        profileLogo.setImageURI(Uri.parse(ApiRestAdapter.ADVERTISERS_IMAGE_PATH + perfil.pictureFile));

        view.setTag(perfil);

        return view;
    }
}
