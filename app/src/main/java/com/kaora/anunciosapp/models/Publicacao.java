package com.kaora.anunciosapp.models;

import java.io.Serializable;
import java.util.Date;

public class Publicacao implements Serializable {

    public String guidPublicacao;
    public String guidAnunciante;
    public int idCategoria;
    public String titulo;
    public String descricao;
    public Date dataPublicacao;
    public Date dataValidade;
    public String imagem;
    public Anunciante anunciante;

    public Boolean published = false;

    public Publicacao() {
        this.dataPublicacao = new Date();
    }

    public void setDataValidade(Date dataValidade) {
        this.dataValidade = dataValidade;
    }

}
