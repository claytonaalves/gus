package com.kaora.anunciosapp.adapters;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.models.PublicationCategory;
import com.kaora.anunciosapp.rest.ApiRestAdapter;

import java.util.List;

public class CategoriasAdapter extends BaseAdapter {

    private final List<PublicationCategory> publicationCategories;
    private final Activity activity;

    public CategoriasAdapter(List<PublicationCategory> publicationCategories, Activity activity) {
        this.publicationCategories = publicationCategories;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return publicationCategories.size();
    }

    @Override
    public Object getItem(int position) {
        return publicationCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        PublicationCategory publicationCategory = publicationCategories.get(position);
        return publicationCategory.idCategoria;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = activity.getLayoutInflater()
                .inflate(R.layout.item_listview_categoria, parent, false);
        PublicationCategory publicationCategory = publicationCategories.get(position);

        Uri uri = Uri.parse(ApiRestAdapter.BASE_URL + publicationCategory.imagem);
        SimpleDraweeView draweeView = (SimpleDraweeView) view.findViewById(R.id.imagem);
        draweeView.setImageURI(uri);

        TextView nomeCategoria = (TextView) view.findViewById(R.id.tvNomeCategoria);
        nomeCategoria.setText(publicationCategory.descricao);

        TextView qtdeAnunciantes = (TextView) view.findViewById(R.id.qtdeAnunciantes);
        qtdeAnunciantes.setVisibility(publicationCategory.qtdeAnunciantes>0 ? View.VISIBLE : View.INVISIBLE);
        qtdeAnunciantes.setText(" " + Integer.toString(publicationCategory.qtdeAnunciantes) + " ");

        view.setTag(publicationCategory);

        return view;
    }
}
