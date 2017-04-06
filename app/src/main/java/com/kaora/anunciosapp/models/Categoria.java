package com.kaora.anunciosapp.models;

public class Categoria {

    public int _id;
    public String descricao;
    public String imagem;

    public Categoria(int _id, String descricao, String imagem) {
        this._id = _id;
        this.descricao = descricao;
        this.imagem = imagem;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Categoria && ((Categoria) object)._id == this._id;
    }

}
