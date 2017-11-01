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
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.activities.PublicacaoActivity;
import com.kaora.anunciosapp.models.Publication;
import com.kaora.anunciosapp.rest.ApiRestAdapter;
import com.kaora.anunciosapp.utils.DateUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicacoesAdapter extends RecyclerView.Adapter<PublicacoesAdapter.ViewHolder> {

    private Context context;
    private List<Publication> publicacoes;
    private DateFormat df;

    public PublicacoesAdapter(Context context, List<Publication> publicacoes) {
        this.publicacoes = publicacoes;
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
        Publication publication = publicacoes.get(position);
        holder.tvTitulo.setText(publication.title);
        holder.tvDescricao.setText(publication.description);
        holder.tvDataPublicacao.setText(DateUtils.textoDataPublicacao(publication.publicationDate));
        holder.tvDataValidade.setText(textoDataValidade(publication.dueDate));
        if ((publication.imageFile != null) && (publication.imageFile.equals(""))) {
            holder.draweeView.setImageResource(R.drawable.photo_gray);
        } else {
            holder.draweeView.setImageURI(Uri.parse(ApiRestAdapter.PUBLICATIONS_IMAGE_PATH + publication.imageFile));
        }
    }

    private String textoDataValidade(Date dataValidade) {
        Resources res = context.getResources();
        return String.format(res.getString(R.string.data_validade), df.format(dataValidade));
    }

    @Override
    public int getItemCount() {
        return publicacoes.size();
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
                Publication publication = publicacoes.get(position);
                obtemPublicacaoDoWebservice(publication.publicationGuid);
            }
        }

        private void obtemPublicacaoDoWebservice(String guidPublicacao) {
            ApiRestAdapter webservice = ApiRestAdapter.getInstance();
            webservice.obtemPublicacao(guidPublicacao, new Callback<Publication>() {
                @Override
                public void onResponse(Call<Publication> call, Response<Publication> response) {
                    Publication publication = response.body();
                    Intent intent = new Intent(context, PublicacaoActivity.class);
                    intent.putExtra("publication", publication);
                    context.startActivity(intent);
                }

                @Override
                public void onFailure(Call<Publication> call, Throwable t) {
                    Toast.makeText(context, "Falha ao baixar Publicação", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
