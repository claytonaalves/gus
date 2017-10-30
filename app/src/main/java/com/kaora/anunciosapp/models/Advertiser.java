package com.kaora.anunciosapp.models;

import java.io.Serializable;

public class Advertiser implements Serializable {
    public String guidAnunciante;
    public int idCidade;
    public int idCategoria;
    public String razaoSocial;
    public String nomeFantasia;
    public String telefone;
    public String celular;
    public String email;
    public String logradouro;
    public String numero;
    public String bairro;

    public String pictureFile;
    public Boolean published = false;
    public int position;

    @Override
    public String toString() {
        return this.nomeFantasia;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Advertiser
                && ((Advertiser)object).guidAnunciante.equals(this.guidAnunciante);
    }

}
