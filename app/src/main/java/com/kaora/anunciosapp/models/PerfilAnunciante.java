package com.kaora.anunciosapp.models;

public class PerfilAnunciante {
    public long _id;
    public String nome;
    public String telefone;
    public String celular;
    public String email;
    public String endereco;
    public String numero;
    public String estado;
    public String cidade;
    public String bairro;
    public long idCategoria;

    @Override
    public String toString() {
        return this.nome;
    }
}
