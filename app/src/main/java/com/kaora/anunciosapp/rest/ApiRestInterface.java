package com.kaora.anunciosapp.rest;

import com.kaora.anunciosapp.models.Anunciante;
import com.kaora.anunciosapp.models.Categoria;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiRestInterface {

    @GET("categoria")
    Call<List<Categoria>> obtemCategorias();

    @GET("categoria/{id}")
    Call<List<Anunciante>> anunciantesPorCategoria(@Path("id") int idCategoria);

}
