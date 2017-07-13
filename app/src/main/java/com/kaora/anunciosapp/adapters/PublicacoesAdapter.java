package com.kaora.anunciosapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaora.anunciosapp.R;
import com.kaora.anunciosapp.models.Publicacao;

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
    }

    @Override
    public int getItemCount() {
        return publicacoes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitulo;
        TextView tvDescricao;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitulo = (TextView) itemView.findViewById(R.id.tvTitulo);
            tvDescricao = (TextView) itemView.findViewById(R.id.tvDescricao);
        }
    }

}
