package com.kaora.anunciosapp.models;

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

    }

    public Publicacao(String titulo, String descricao) {
        this.titulo = titulo;
        this.descricao = descricao;
    }

}
