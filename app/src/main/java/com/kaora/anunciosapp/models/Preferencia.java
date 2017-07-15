package com.kaora.anunciosapp.models;

public class Preferencia {

    public int idCategoria;
    public String descricao;
    public boolean selecionanda;

    public Preferencia(int idCategoria, String descricao) {
        this.idCategoria = idCategoria;
        this.descricao = descricao;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Preferencia && ((Preferencia) object).idCategoria == this.idCategoria;
    }

}
