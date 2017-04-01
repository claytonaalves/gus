package com.kaora.anunciosapp.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Categoria {

    public int _id;
    public String descricao;
    // imagem

    public Categoria(int _id, String descricao) {
        this._id = _id;
        this.descricao = descricao;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Categoria && ((Categoria) object)._id == this._id;
    }

}
