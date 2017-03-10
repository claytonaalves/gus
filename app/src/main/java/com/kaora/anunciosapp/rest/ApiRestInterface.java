package com.kaora.anunciosapp.rest;

import com.kaora.anunciosapp.models.Categoria;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiRestInterface {

    @GET("/activity_categorias")
    Call<List<Categoria>> obtemCategorias();

}
