package com.kaora.anunciosapp.rest;

import android.text.TextUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kaora.anunciosapp.models.Anunciante;
import com.kaora.anunciosapp.models.Categoria;
import com.kaora.anunciosapp.models.Cidade;
import com.kaora.anunciosapp.models.PerfilAnunciante;
import com.kaora.anunciosapp.models.Preferencia;
import com.kaora.anunciosapp.models.Publicacao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiRestAdapter {

    private static Retrofit retrofit;
    public static final String HOST = "http://177.221.202.42:8080";
    public static final String BASE_URL = HOST + "/api/v1/";
    public static final String IMAGES_PATH = HOST + "/images";
//    public static final String BASE_URL = "http://10.0.2.16:5000/";
//    public static final String BASE_URL = "http://10.1.1.43:5000/";
    private static ApiRestAdapter instance;

    private ApiRestInterface service;

    public static ApiRestAdapter getInstance() {
        if (instance == null) {
            instance = new ApiRestAdapter();
        }
        return instance;
    }

    private ApiRestAdapter() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        service = retrofit.create(ApiRestInterface.class);
    }

    public void anunciantesPorCategoria(Callback<List<Anunciante>> cb, int idCategoria) {
        Call<List<Anunciante>> request = service.anunciantesPorCategoria(idCategoria);
        request.enqueue(cb);
    }

    public void publicaAnunciante(PerfilAnunciante anunciante, Callback<PerfilAnunciante> cb) {
        Call<PerfilAnunciante> request = service.publicaAnunciante(anunciante);
        request.enqueue(cb);
    }

    public void publicaPublicacao(Publicacao publicacao, Callback<Publicacao> cb) {
        Call<Publicacao> request = service.publicaPublicacao(publicacao);
        request.enqueue(cb);
    }

//    public void obtemPublicacao(String guidAnuncio, Callback<Publicacao> cb) {
//        Call<Publicacao> request = service.obtemPublicacao(guidPublicacao);
//        request.enqueue(cb);
//    }

    public void obtemCidades(Callback<List<Cidade>> cb) {
        Call<List<Cidade>> request = service.obtemCidades();
        request.enqueue(cb);
    }

    public void obtemCategorias(int idCidade, Callback<List<Categoria>> cb) {
        Call<List<Categoria>> request = service.obtemCategorias(idCidade);
        request.enqueue(cb);
    }

    public void obtemPublicacoes(Date desde, List<Preferencia> preferencias, Callback<List<Publicacao>> cb) {
        List<Integer> idsCategorias = new ArrayList<>();
        for (Preferencia preferencia : preferencias) {
            idsCategorias.add(preferencia.idCategoria);
        }
        String listaDeIdsCategorias = TextUtils.join(",", idsCategorias);
        Call<List<Publicacao>> request = service.obtemPublicacoes(desde.getTime()/1000, listaDeIdsCategorias);
        request.enqueue(cb);
    }

    public void obtemPublicacao(String guidPublicacao, Callback<Publicacao> cb) {
        Call<Publicacao> request = service.obtemPublicacao(guidPublicacao);
        request.enqueue(cb);
    }

    public Call<ResponseBody> postaFotoPublicacao(RequestBody description, MultipartBody.Part body) {
        return service.postaFotoPublicacao(description, body);
    }

}
