package com.kaora.anunciosapp.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.models.Publicacao;
import com.kaora.anunciosapp.rest.ApiRestAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        holder.tvDataPublicacao.setText(textoDataPublicacao(publicacao.dataPublicacao));
        holder.tvDataValidade.setText(textoDataValidade(publicacao.dataValidade));
        holder.draweeView.setImageURI(Uri.parse(ApiRestAdapter.BASE_URL + "publicacoes/foto/avatar.jpg"));
    }

    private String textoDataValidade(long dataValidade) {
        Resources res = context.getResources();
        return String.format(res.getString(R.string.data_validade), df.format(new Date(dataValidade * 1000)));
    }

    private String textoDataPublicacao(long dataPublicacao) {
        Date hoje = new Date();
        Date data = new Date(dataPublicacao * 1000);
        long diferenca = (hoje.getTime()-data.getTime());
        if (diferenca>172800000) {
            return df.format(data);
        } else if (diferenca>86400000) {
            return "Ontem";
        } else {
            return "Hoje";
        }
    }

    @Override
    public int getItemCount() {
        return publicacoes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

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
        }
    }

}
