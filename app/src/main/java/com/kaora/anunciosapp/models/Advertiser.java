package com.kaora.anunciosapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.UUID;

public class Advertiser implements Serializable {

    @SerializedName(value = "guid_anunciante")
    public String advertiserGuid;

    @SerializedName(value = "id_cidade")
    public int cityId;

    @SerializedName(value = "id_categoria")
    public int categoryId;

    @SerializedName(value = "razao_social")
    public String companyName;

    @SerializedName(value = "nome_fantasia")
    public String tradingName;

    @SerializedName(value = "telefone")
    public String phoneNumber;

    @SerializedName(value = "celular")
    public String cellphone;

    public String email;

    @SerializedName(value = "logradouro")
    public String streetName;

    @SerializedName(value = "numero")
    public String addressNumber;

    @SerializedName(value = "bairro")
    public String neighbourhood;

    @SerializedName(value = "picture_file")
    public String imageFile;

    public transient boolean published = false;
    public transient int position;

    public Advertiser() {
        this.advertiserGuid = UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return this.tradingName;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Advertiser
                && ((Advertiser)object).advertiserGuid.equals(this.advertiserGuid);
    }

}
