package com.kaora.anunciosapp.adapters;

import android.content.Context;
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

import org.w3c.dom.Text;

import java.util.List;

public class PublicacoesAdapter extends RecyclerView.Adapter<PublicacoesAdapter.ViewHolder> {

    private Context context;
    private List<Publicacao> publicacoes;

    public PublicacoesAdapter(Context context, List<Publicacao> publicacoes) {
        this.publicacoes = publicacoes;
        this.context = context;
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
        TextView tvTitulo = holder.tvTitulo;
        TextView tvDescricao = holder.tvDescricao;
        tvTitulo.setText(publicacao.titulo);
        tvDescricao.setText(publicacao.descricao);
        holder.draweeView.setImageURI(Uri.parse(ApiRestAdapter.BASE_URL + "publicacoes/foto/avatar.jpg"));
    }

    @Override
    public int getItemCount() {
        return publicacoes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitulo;
        TextView tvDescricao;
        SimpleDraweeView draweeView;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitulo = (TextView) itemView.findViewById(R.id.tvTitulo);
            tvDescricao = (TextView) itemView.findViewById(R.id.tvDescricao);
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.imagem);
        }
    }

}
