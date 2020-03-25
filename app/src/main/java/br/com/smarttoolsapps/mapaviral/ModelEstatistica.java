package br.com.smarttoolsapps.mapaviral;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class ModelEstatistica {

    @SerializedName("usuarios_totais")
    private int usuariosTotais;
    @SerializedName("usuarios_assintomaticos")
    private int usuariosAssintomaticos;
    @SerializedName("usuarios_contato_exterior")
    private int usuariosTiveramContato;
    @SerializedName("numero_cadastro_48h")
    private int usuariosCadastros48h;
    @SerializedName("deslocamentos_48h")
    private int usuariosDeslocamento48h;
    @SerializedName("deixou_usar_48h")
    private int usuariosDeletado48h;
    @SerializedName("sintomas_agressivos")
    private int usuariosSintomasAgressivos;
    @SerializedName("s0")
    private int s0;
    @SerializedName("s1")
    private int s1;
    @SerializedName("s2")
    private int s2;
    @SerializedName("s3")
    private int s3;
    @SerializedName("s4")
    private int s4;
    @SerializedName("s5")
    private int s5;
    @SerializedName("s6")
    private int s6;
    @SerializedName("s7")
    private int s7;
    @SerializedName("s8")
    private int s8;
    @SerializedName("s9")
    private int s9;
    @SerializedName("s10")
    private int s10;
    @SerializedName("s11")
    private int s11;

    public ModelEstatistica(int usuariosTotais, int usuariosAssintomaticos, int usuariosTiveramContato, int usuariosCadastros48h, int usuariosDeslocamento48h, int usuariosDeletado48h, int usuariosSintomasAgressivos, int s0, int s1, int s2, int s3, int s4, int s5, int s6, int s7, int s8, int s9, int s10, int s11) {

        this.usuariosTotais = usuariosTotais;
        this.usuariosAssintomaticos = usuariosAssintomaticos;
        this.usuariosTiveramContato = usuariosTiveramContato;
        this.usuariosCadastros48h = usuariosCadastros48h;
        this.usuariosDeslocamento48h = usuariosDeslocamento48h;
        this.usuariosDeletado48h = usuariosDeletado48h;
        this.usuariosSintomasAgressivos = usuariosSintomasAgressivos;
        this.s0 = s0;
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
        this.s4 = s4;
        this.s5 = s5;
        this.s6 = s6;
        this.s7 = s7;
        this.s8 = s8;
        this.s9 = s9;
        this.s10 = s10;
        this.s11 = s11;
    }

    public int getUsuariosTotais() {

        return usuariosTotais;
    }

    public int getUsuariosAssintomaticos() {

        return usuariosAssintomaticos;
    }

    public int getUsuariosTiveramContato() {

        return usuariosTiveramContato;
    }

    public int getUsuariosCadastros48h() {

        return usuariosCadastros48h;
    }

    public int getUsuariosDeslocamento48h() {

        return usuariosDeslocamento48h;
    }

    public int getUsuariosDeletado48h() {

        return usuariosDeletado48h;
    }

    public int getUsuariosSintomasAgressivos() {

        return usuariosSintomasAgressivos;
    }

    public int getS0() {

        return s0;
    }

    public int getS1() {

        return s1;
    }

    public int getS2() {

        return s2;
    }

    public int getS3() {

        return s3;
    }

    public int getS4() {

        return s4;
    }

    public int getS5() {

        return s5;
    }

    public int getS6() {

        return s6;
    }

    public int getS7() {

        return s7;
    }

    public int getS8() {

        return s8;
    }

    public int getS9() {

        return s9;
    }

    public int getS10() {

        return s10;
    }

    public int getS11() {

        return s11;
    }
}