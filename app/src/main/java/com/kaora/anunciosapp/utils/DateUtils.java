package com.kaora.anunciosapp.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static String textoDataPublicacao(Date dataPublicacao) {
        DateFormat df = DateFormat.getDateInstance();
        Date hoje = new Date();
        Date data = dataPublicacao;
        long diferenca = (hoje.getTime()-data.getTime());
        if (diferenca>172800000) {
            return df.format(data);
        } else if (diferenca>86400000) {
            return "Ontem";
        } else {
            return "Hoje";
        }
    }

    public static String dateToString(Date data) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return dateFormat.format(data);
    }

    public static Date stringToDate(String data) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            return dateFormat.parse(data);
        } catch (ParseException e) {
            return new Date();
        }
    }


}
