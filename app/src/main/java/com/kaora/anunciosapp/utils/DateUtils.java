package com.kaora.anunciosapp.utils;

import java.text.DateFormat;
import java.util.Date;

public class DateUtils {

    public static String textoDataPublicacao(long dataPublicacao) {
        DateFormat df = DateFormat.getDateInstance();
        Date hoje = new Date();
        Date data = new Date(dataPublicacao * 1000);
        long diferenca = (hoje.getTime()-data.getTime());
        if (diferenca>172800000) {
            return df.format(data);
        } else if (diferenca>86400000) {
            return "Ontem";
        } else {
            return "Hoje";
        }
    }

}
