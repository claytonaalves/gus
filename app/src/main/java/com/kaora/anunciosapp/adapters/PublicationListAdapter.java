package com.kaora.anunciosapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kaora.anunciosapp.BuildConfig;
import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.activities.PublicationDetailActivity;
import com.kaora.anunciosapp.models.Publication;
import com.kaora.anunciosapp.rest.ApiRestAdapter;
import com.kaora.anunciosapp.utils.DateUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class PublicationListAdapter extends RecyclerView.Adapter<PublicationListAdapter.ViewHolder> {

    private Context context;
    private List<Publication> publications;
    private DateFormat df;

    public PublicationListAdapter(Context context, List<Publication> publications) {
        this.publications = publications;
        this.context = context;
        this.df = DateFormat.getDateInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View publicacaoView = inflater.inflate(R.layout.li_publicacao, parent, false);

        return new ViewHolder(publicacaoView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Publication publication = publications.get(position);
        holder.tvTitulo.setText(publication.title);
        holder.tvDescricao.setText(publication.description);
        holder.tvDataPublicacao.setText(DateUtils.textoDataPublicacao(publication.publicationDate));
        holder.tvDataValidade.setText(textoDataValidade(publication.dueDate));
        if (publication.hasImages()) {
            String firstImage = publication.images.get(0);
            if (!firstImage.equals("")) {
                String imageUri = ApiRestAdapter.PUBLICATIONS_IMAGE_PATH + firstImage;
                String proxiedImageUri = BuildConfig.IMG_PROXY + "/200x,q90/" + imageUri;
                holder.draweeView.setImageURI(Uri.parse(proxiedImageUri));
            } else {
                holder.draweeView.setImageResource(R.drawable.photo_gray);
            }
        } else {
            holder.draweeView.setImageResource(R.drawable.photo_gray);
        }
    }

    private String textoDataValidade(Date dataValidade) {
        Resources res = context.getResources();
        return String.format(res.getString(R.string.data_validade), df.format(dataValidade));
    }

    @Override
    public int getItemCount() {
        return publications.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTitulo;
        TextView tvDescricao;
        TextView tvDataPublicacao;
        TextView tvDataValidade;
        SimpleDraweeView draweeView;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitulo = (TextView) itemView.findViewById(R.id.tvTitulo);
            tvDescricao = (TextView) itemView.findViewById(R.id.tvDescricao);
            tvDataPublicacao = (TextView) itemView.findViewById(R.id.tvDataPublicacao);
            tvDataValidade = (TextView) itemView.findViewById(R.id.tvDataValidade);
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.imagem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Publication publication = publications.get(position);
                Intent intent = new Intent(context, PublicationDetailActivity.class);
                intent.putExtra("publication", publication);
                context.startActivity(intent);
            }
        }

    }
}
