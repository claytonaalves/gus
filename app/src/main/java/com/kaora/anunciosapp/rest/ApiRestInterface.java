package com.kaora.anunciosapp.rest;

import com.kaora.anunciosapp.models.Anunciante;
import com.kaora.anunciosapp.models.Anuncio;
import com.kaora.anunciosapp.models.Categoria;
import com.kaora.anunciosapp.models.Cidade;
import com.kaora.anunciosapp.models.PerfilAnunciante;
import com.kaora.anunciosapp.models.Publicacao;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiRestInterface {

    @GET("categoria/{id}")
    Call<List<Anunciante>> anunciantesPorCategoria(@Path("id") int idCategoria);

    @POST("anunciante")
    Call<PerfilAnunciante> publicaAnunciante(@Body PerfilAnunciante anunciante);

    @POST("anuncio")
    Call<Anuncio> publicaAnuncio(@Body Anuncio anuncio);

    @GET("anuncio/{guid}")
    Call<Anuncio> obtemAnuncio(@Path("guid") String guidAnuncio);

    @GET("cidades/")
    Call<List<Cidade>> obtemCidades();

    @GET("categorias/")
    Call<List<Categoria>> obtemCategorias(@Query("id_cidade") int idCidade);

    @GET("publicacoes/")
    Call<List<Publicacao>> obtemPublicacoes(@Query("desde") long desde, @Query("categorias") String idsCategorias);

}
