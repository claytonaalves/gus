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
    public Date dataPublicacao;
    public Date dataValidade;
    public String imagem;
    public Anunciante anunciante;

    public Publicacao() {
        this.dataPublicacao = new Date();
    }

    public void setDataValidade(Date dataValidade) {
        this.dataValidade = dataValidade;
    }

    public String dataFormatada() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return dateFormat.format(this.dataValidade);
    }

}
