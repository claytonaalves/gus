package com.kaora.anunciosapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class Publication implements Serializable {

    @SerializedName(value = "guid_publicacao")
    public String publicationGuid;

    @SerializedName(value = "guid_anunciante")
    public String advertiserGuid;

    @SerializedName(value = "id_categoria")
    public int category_id;

    @SerializedName(value = "titulo")
    public String title;

    @SerializedName(value = "descricao")
    public String description;

    @SerializedName(value = "data_publicacao")
    public Date publicationDate;

    @SerializedName(value = "data_validade")
    public Date dueDate;

    @SerializedName(value = "imagem")
    public String imageFile;

    @SerializedName(value = "anunciante")
    public Advertiser advertiser;

    public boolean published = false;
    public boolean archived = false;

    public Publication() {
        this.publicationDate = new Date();
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

}
