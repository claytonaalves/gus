package com.kaora.anunciosapp.models;

public class Preferencia {

    public int idCategoria;
    public String descricao;
    public boolean selecionanda;

    public Preferencia(int idCategoria, String descricao) {
        this.idCategoria = idCategoria;
        this.descricao = descricao;
    }
}
