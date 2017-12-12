package com.kaora.anunciosapp.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.models.Advertiser;
import com.kaora.anunciosapp.rest.ApiRestAdapter;

import java.util.List;


public class AdvertiserListAdapter extends BaseAdapter {

    private final List<Advertiser> advertisers;
    private final Activity activity;

    public AdvertiserListAdapter(List<Advertiser> advertisers, Activity activity) {
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
        Advertiser anunciante = advertisers.get(position);
        return anunciante.position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Advertiser advertiser = advertisers.get(position);

        View view = activity.getLayoutInflater()
                .inflate(R.layout.li_advertiser, parent, false);

        TextView tvNomeAnunciante = (TextView) view.findViewById(R.id.tvNomeAnunciante);
        SimpleDraweeView image = (SimpleDraweeView) view.findViewById(R.id.main_image);

        tvNomeAnunciante.setText(advertiser.tradingName);

        view.setTag(advertiser);

        if (advertiser.imageFile != null) {
            if (!advertiser.imageFile.equals("")) {
                image.setImageURI(ApiRestAdapter.ADVERTISERS_IMAGE_PATH + advertiser.imageFile);
            }
        }

        return view;
    }
}
