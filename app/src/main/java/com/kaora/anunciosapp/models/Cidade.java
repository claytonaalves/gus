package com.kaora.anunciosapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Cidade implements Parcelable {
    public int idCidade;
    public String nome;
    public String uf;

    public Cidade(int idCidade, String nome, String uf) {
        this.idCidade = idCidade;
        this.nome = nome;
        this.uf = uf;
    }

    private Cidade(Parcel in) {
        this.idCidade = in.readInt();
        this.nome = in.readString();
        this.uf = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idCidade);
        dest.writeString(nome);
        dest.writeString(uf);
    }

    public static final Parcelable.Creator<Cidade> CREATOR = new Parcelable.Creator<Cidade>() {

        @Override
        public Cidade createFromParcel(Parcel source) {
            return new Cidade(source);
        }

        @Override
        public Cidade[] newArray(int size) {
            return new Cidade[size];
        }
    };
}
