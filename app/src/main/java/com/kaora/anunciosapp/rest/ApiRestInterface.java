package com.kaora.anunciosapp.rest;

import com.kaora.anunciosapp.models.Advertiser;
import com.kaora.anunciosapp.models.Categoria;
import com.kaora.anunciosapp.models.Cidade;
import com.kaora.anunciosapp.models.Publicacao;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiRestInterface {

    @GET("cidades/")
    Call<List<Cidade>> obtemCidades();

    @GET("categorias/")
    Call<List<Categoria>> obtemCategorias(@Query("id_cidade") int idCidade);

    @GET("publicacoes/")
    Call<List<Publicacao>> obtemPublicacoes(@Query("device_id") String deviceId,
                                            @Query("categorias") String idsCategorias);

    @POST("publicacoes/")
    Call<Publicacao> publicaPublicacao(@Body Publicacao publicacao);

    @GET("publicacoes/{guid_publicacao}")
    Call<Publicacao> obtemPublicacao(@Path("guid_publicacao") String guidAnunciante);

    @Multipart
    @POST("publicacoes/fotos")
    Call<ResponseBody> postaFotoPublicacao(@Part("description") RequestBody description,
                                           @Part MultipartBody.Part file);

    @Multipart
    @POST("anunciantes/fotos")
    Call<ResponseBody> postAdvertiserImage(@Part("description") RequestBody description,
                                           @Part MultipartBody.Part file);

    @POST("anunciantes/")
    Call<Advertiser> publicaAnunciante(@Body Advertiser anunciante);

    @GET("categoria/{id}")
    Call<List<Advertiser>> anunciantesPorCategoria(@Path("id") int idCategoria);

//    @GET("publicacao/{guid}")
//    Call<Publicacao> obtemPublicacao(@Path("guid") String guidPublicacao);
}
