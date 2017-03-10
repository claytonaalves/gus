package com.kaora.anunciosapp.rest;

import com.kaora.anunciosapp.models.Categoria;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiRestAdapter {

    private static Retrofit retrofit;
    public static final String BASE_URL = "http://10.1.1.105:5000";

    ApiRestInterface service;

    public ApiRestAdapter() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(ApiRestInterface.class);
    }

    public void obtemCategorias(Callback<List<Categoria>> cb) {
        Call<List<Categoria>> request = service.obtemCategorias();
        request.enqueue(cb);
    }


}
