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

public class AdvertiserProfileAdapter extends BaseAdapter {

    private final List<Advertiser> advertisers;
    private final Activity activity;

    public AdvertiserProfileAdapter(List<Advertiser> advertisers, Activity activity) {
        this.advertisers = advertisers;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return advertisers.size();
    }

    @Override
    public Object getItem(int position) {
        return advertisers.get(position);
    }

    @Override
    public long getItemId(int position) {
        Advertiser perfil = advertisers.get(position);
//        return perfil._id;
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Advertiser advertiser = advertisers.get(position);

        View view = activity.getLayoutInflater()
                .inflate(R.layout.item_listview_anunciante, parent, false);

        TextView tvNomeAnunciante = (TextView) view.findViewById(R.id.tvNomeAnunciante);
        tvNomeAnunciante.setText(advertiser.tradingName);

        SimpleDraweeView profileLogo = (SimpleDraweeView) view.findViewById(R.id.imgLogoAnunciante);
        profileLogo.setImageURI(Uri.parse(ApiRestAdapter.ADVERTISERS_IMAGE_PATH + advertiser.imageFile));

        view.setTag(advertiser);

        return view;
    }
}
