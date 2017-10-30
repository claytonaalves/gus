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
import com.kaora.anunciosapp.models.Publicacao;
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
    private List<Publicacao> publicacoes;
    private DateFormat df;

    public PublicacoesAdapter(Context context, List<Publicacao> publicacoes) {
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
        Publicacao publicacao = publicacoes.get(position);
        holder.tvTitulo.setText(publicacao.titulo);
        holder.tvDescricao.setText(publicacao.descricao);
        holder.tvDataPublicacao.setText(DateUtils.textoDataPublicacao(publicacao.dataPublicacao));
        holder.tvDataValidade.setText(textoDataValidade(publicacao.dataValidade));
        if (publicacao.imagem != "") {
            holder.draweeView.setImageURI(Uri.parse(ApiRestAdapter.PUBLICATIONS_IMAGE_PATH + publicacao.imagem));
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
                Publicacao publicacao = publicacoes.get(position);
                obtemPublicacaoDoWebservice(publicacao.guidPublicacao);
            }
        }

        private void obtemPublicacaoDoWebservice(String guidPublicacao) {
            ApiRestAdapter webservice = ApiRestAdapter.getInstance();
            webservice.obtemPublicacao(guidPublicacao, new Callback<Publicacao>() {
                @Override
                public void onResponse(Call<Publicacao> call, Response<Publicacao> response) {
                    Publicacao publicacao = response.body();
                    Intent intent = new Intent(context, PublicacaoActivity.class);
                    intent.putExtra("publicacao", publicacao);
                    context.startActivity(intent);
                }

                @Override
                public void onFailure(Call<Publicacao> call, Throwable t) {
                    Toast.makeText(context, "Falha ao baixar Publicação", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
