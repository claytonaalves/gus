package com.kaora.anunciosapp.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Publicacao {

    public String guidPublicacao;
    public String guidAnunciante;
    public int idCategoria;
    public String titulo;
    public String descricao;
    public long dataPublicacao;
    public long dataValidade;
    public String imagem;

    public Publicacao() {
        this.dataPublicacao = new Date().getTime()/1000;
    }

    public Publicacao(String titulo, String descricao) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataPublicacao = new Date().getTime()/1000;
    }

    public void setDataValidade(Date dataValidade) {
        this.dataValidade = dataValidade.getTime()/1000;
    }

    public String dataFormatada() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return dateFormat.format(new Date(this.dataValidade));
    }

}
