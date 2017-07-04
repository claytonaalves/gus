package com.kaora.anunciosapp.models;

public class Cidade {
    public int idCidade;
    public String nome;
    public String uf;

    public Cidade(int idCidade, String nome, String uf) {
        this.idCidade = idCidade;
        this.nome = nome;
        this.uf = uf;
    }
}
