package com.kaora.anunciosapp.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Anuncio {
    public long _id;
    public String titulo;
    public String descricao;
    public Date validoAte;
    public String imagem;
    public int idCategoria;
//    public boolean publicado;

    public String dataFormatada() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return dateFormat.format(this.validoAte);
    }

}
