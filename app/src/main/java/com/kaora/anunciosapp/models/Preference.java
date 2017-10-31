package com.kaora.anunciosapp.models;

import com.google.gson.annotations.SerializedName;

public class Preference {

    @SerializedName(value = "id_categoria")
    public int categoryId;

    @SerializedName(value = "descricao")
    public String descricao;

    @SerializedName(value = "selecionada")
    public boolean selected;

    public Preference(int categoryId, String description) {
        this.categoryId = categoryId;
        this.descricao = description;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Preference && ((Preference) object).categoryId == this.categoryId;
    }

}
