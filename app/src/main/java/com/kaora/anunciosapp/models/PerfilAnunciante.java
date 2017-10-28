package com.kaora.anunciosapp.models;

public class PerfilAnunciante {
    public String guidAnunciante;
    public int idCidade;
    public int idCategoria;
    public String nomeFantasia;
    public String telefone;
    public String celular;
    public String email;
    public String endereco;
    public String numero;
    public String bairro;

    public String pictureFile;
    public Boolean published = false;

    @Override
    public String toString() {
        return this.nomeFantasia;
    }
}
