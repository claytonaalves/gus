package com.kaora.anunciosapp.rest;

import com.kaora.anunciosapp.models.Advertiser;
import com.kaora.anunciosapp.models.Publication;
import com.kaora.anunciosapp.models.PublicationCategory;
import com.kaora.anunciosapp.models.Cidade;

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
    Call<List<PublicationCategory>> obtemCategorias(@Query("id_cidade") int idCidade);

    @GET("publicacoes/")
    Call<List<Publication>> obtemPublicacoes(@Query("device_id") String deviceId,
                                             @Query("categorias") String idsCategorias);

    @POST("publicacoes/")
    Call<Publication> publicaPublicacao(@Body Publication publication);

    @GET("publicacoes/{guid_publicacao}")
    Call<Publication> obtemPublicacao(@Path("guid_publicacao") String guidAnunciante);

    @Multipart
    @POST("publicacoes/fotos")
    Call<ResponseBody> postaFotoPublicacao(@Part("description") RequestBody description,
                                           @Part MultipartBody.Part file);

    @Multipart
    @POST("anunciantes/fotos")
    Call<ResponseBody> postAdvertiserImage(@Part("description") RequestBody description,
                                           @Part MultipartBody.Part file);

    @POST("anunciantes/")
    Call<Advertiser> postAdvertiserProfile(@Body Advertiser advertiser);

    @GET("anunciantes/search")
    Call<List<Advertiser>> searchAdvertiser(@Query("q") String query);

    @GET("categoria/{id}")
    Call<List<Advertiser>> anunciantesPorCategoria(@Path("id") int idCategoria);

//    @GET("publicacao/{guid}")
//    Call<Publication> obtemPublicacao(@Path("guid") String publicationGuid);
}
