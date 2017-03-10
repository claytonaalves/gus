package com.kaora.anunciosapp.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Categoria {

    public int idCategoria;
    public String descricao;
    // imagem

    public Categoria(int idCategoria, String descricao) {
        this.idCategoria = idCategoria;
        this.descricao = descricao;
    }

    public Categoria(JSONObject json) throws JSONException {
        this.idCategoria = json.getInt("_id");
        this.descricao = json.getString("descricao");
    }

}
