package br.com.smarttoolsapps.mapaviral;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitEndpoints {

    //Sistema

    @GET("sistema/info/{uf}")
    Call<ModelRequest> sistemaInfo(
            @Path("uf") String uf
    );

    //Usuários

    @FormUrlEncoded
    @POST("usuarios/cadastrar")
    Call<ModelRequest> usuariosCadastrar(
            @Header("device") String id_aparelho,
            @Field("s0") boolean s0,
            @Field("s1") boolean s1,
            @Field("s2") boolean s2,
            @Field("s3") boolean s3,
            @Field("s4") boolean s4,
            @Field("s5") boolean s5,
            @Field("s6") boolean s6,
            @Field("s7") boolean s7,
            @Field("s8") boolean s8,
            @Field("s9") boolean s9,
            @Field("s10") boolean s10,
            @Field("s11") boolean s11,
            @Field("lat") float lat,
            @Field("lon") float lon,
            @Field("p") float preciao,
            @Field("uf") String uf
    );

    @GET("usuarios/perfil/{id}")
    Call<ModelRequest> usuariosObterPerfil(
            @Header("device") String id_aparelho,
            @Path("id") String id
    );

    @GET("usuarios/estatisticas")
    Call<ModelRequest> usuariosEstatisticas(
            @Header("device") String id_aparelho
    );

    @FormUrlEncoded
    @POST("usuarios/atualizar")
    Call<ModelRequest> usuariosAtualizarSintomas(
            @Header("device") String id_aparelho,
            @Field("s0") boolean s0,
            @Field("s1") boolean s1,
            @Field("s2") boolean s2,
            @Field("s3") boolean s3,
            @Field("s4") boolean s4,
            @Field("s5") boolean s5,
            @Field("s6") boolean s6,
            @Field("s7") boolean s7,
            @Field("s8") boolean s8,
            @Field("s9") boolean s9,
            @Field("s10") boolean s10
    );

    @FormUrlEncoded
    @POST("usuarios/atualizarlocal")
    Call<ModelRequest> usuariosAtualizarLocal(
            @Header("device") String id_aparelho,
            @Field("alerta") boolean alerta,
            @Field("lat") float lat,
            @Field("lon") float lon,
            @Field("p") float precisao
    );

    @DELETE("usuarios/excluir")
    Call<ModelRequest> usuariosExcluir(
            @Header("device") String id_aparelho
    );

    @GET("data/heatmap.csv")
    Call<ResponseBody> usuariosHeatMap(
            @Header("device") String id_aparelho
    );

    @GET("usuarios/listar/{lat}/{lon}/{distance}")
    Call<ModelRequest> usuariosListar(
            @Header("device") String id_aparelho,
            @Path("lat") float lat,
            @Path("lon") float lon,
            @Path("distance") float distance
    );
}
