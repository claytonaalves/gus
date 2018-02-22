package com.kaora.anunciosapp.rest;

import android.text.TextUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kaora.anunciosapp.BuildConfig;
import com.kaora.anunciosapp.models.Advertiser;
import com.kaora.anunciosapp.models.Publication;
import com.kaora.anunciosapp.models.PublicationCategory;
import com.kaora.anunciosapp.models.Cidade;
import com.kaora.anunciosapp.models.Preference;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiRestAdapter {

    private static ApiRestAdapter instance;
    private ApiRestInterface service;

    private static final String HOST = BuildConfig.API_HOST;
    public static final String BASE_URL = BuildConfig.API_URL;
    public static final String PUBLICATIONS_IMAGE_PATH = BuildConfig.API_HOST + "/images/publicacoes/";
    public static final String ADVERTISERS_IMAGE_PATH = BuildConfig.API_HOST + "/images/anunciantes/";

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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        service = retrofit.create(ApiRestInterface.class);
    }

    public void anunciantesPorCategoria(Callback<List<Advertiser>> cb, int idCategoria) {
        Call<List<Advertiser>> request = service.anunciantesPorCategoria(idCategoria);
        request.enqueue(cb);
    }

    public void postAdvertiserProfile(Advertiser advertiser, Callback<Advertiser> cb) {
        Call<Advertiser> request = service.postAdvertiserProfile(advertiser);
        request.enqueue(cb);
    }

    public void publicaPublicacao(Publication publication, Callback<Publication> cb) {
        Call<Publication> request = service.publicaPublicacao(publication);
        request.enqueue(cb);
    }

//    public void obtemPublicacao(String guidAnuncio, Callback<Publication> cb) {
//        Call<Publication> request = service.obtemPublicacao(publicationGuid);
//        request.enqueue(cb);
//    }

    public void obtemCidades(Callback<List<Cidade>> cb) {
        Call<List<Cidade>> request = service.obtemCidades();
        request.enqueue(cb);
    }

    public void obtemCategorias(int idCidade, Callback<List<PublicationCategory>> cb) {
        Call<List<PublicationCategory>> request = service.obtemCategorias(idCidade);
        request.enqueue(cb);
    }

    public void obtemPublicacoes(String deviceId, List<Preference> preferences, Callback<List<Publication>> cb) {
        List<Integer> idsCategorias = new ArrayList<>();
        for (Preference preference : preferences) {
            idsCategorias.add(preference.categoryId);
        }
        String listaDeIdsCategorias = TextUtils.join(",", idsCategorias);
        Call<List<Publication>> request = service.obtemPublicacoes(deviceId, listaDeIdsCategorias);
        request.enqueue(cb);
    }

    public void obtemPublicacao(String guidPublicacao, Callback<Publication> cb) {
        Call<Publication> request = service.obtemPublicacao(guidPublicacao);
        request.enqueue(cb);
    }

    public void searchAdvertiser(String query, Callback<List<Advertiser>> cb) {
        Call<List<Advertiser>> request = service.searchAdvertiser(query);
        request.enqueue(cb);
    }

    public Call<ResponseBody> postaFotoPublicacao(RequestBody description, MultipartBody.Part body) {
        return service.postaFotoPublicacao(description, body);
    }

    public Call<ResponseBody> postAdvertiserImage(RequestBody description, MultipartBody.Part body) {
        return service.postAdvertiserImage(description, body);
    }

}
