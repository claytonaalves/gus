package com.kaora.anunciosapp.models;

import java.io.Serializable;

public class Anunciante implements Serializable {

    public int _id;
    public String razaoSocial;
    public String nomeFantasia;
    public String logradouro;
    public String numero;
    public String telefone;
    public String celular;
    public String email;
    public long idCategoria;

    @Override
    public boolean equals(Object object) {
        return object instanceof Anunciante && ((Anunciante)object)._id==this._id;
    }

}
