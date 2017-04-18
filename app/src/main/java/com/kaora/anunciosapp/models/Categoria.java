package com.kaora.anunciosapp.models;

public class Categoria {

    public int _id;
    public String descricao;
    public int qtdeAnunciantes;
    public String imagem;

    public Categoria(int _id, String descricao, int qtdeAnunciantes, String imagem) {
        this._id = _id;
        this.descricao = descricao;
        this.qtdeAnunciantes = qtdeAnunciantes;
        this.imagem = imagem;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Categoria && ((Categoria) object)._id == this._id;
    }

    @Override
    public String toString() {
        return this.descricao;
    }

}
