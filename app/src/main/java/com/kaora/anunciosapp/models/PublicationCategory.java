package com.kaora.anunciosapp.models;

public class PublicationCategory {

    public int idCategoria;
    public String descricao;
    public int qtdeAnunciantes;
    public String imagem;

    public PublicationCategory(int idCategoria, String descricao, int qtdeAnunciantes, String imagem) {
        this.idCategoria = idCategoria;
        this.descricao = descricao;
        this.qtdeAnunciantes = qtdeAnunciantes;
        this.imagem = imagem;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof PublicationCategory && ((PublicationCategory) object).idCategoria == this.idCategoria;
    }

    @Override
    public String toString() {
        return this.descricao;
    }

}
