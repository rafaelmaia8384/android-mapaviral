package br.com.smarttoolsapps.mapaviral;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;

public class ModelUsuario implements ClusterItem {

    @SerializedName("id")
    private String id;
    @SerializedName("id_aparelho")
    private String id_aparelho;
    @SerializedName("n")
    private int n;
    @SerializedName("s0")
    private boolean s0;      // Contato com pessoa do exterior
    @SerializedName("s1")
    private boolean s1;      // Febre
    @SerializedName("s2")
    private boolean s2;      // Cansaço
    @SerializedName("s3")
    private boolean s3;      // Tosse
    @SerializedName("s4")
    private boolean s4;      // Espirros
    @SerializedName("s5")
    private boolean s5;      // Dores no corpo e mal-estar
    @SerializedName("s6")
    private boolean s6;      // Coriza ou nariz entupido
    @SerializedName("s7")
    private boolean s7;      // Dor de garganta
    @SerializedName("s8")
    private boolean s8;      // Diarreia
    @SerializedName("s9")
    private boolean s9;      // Dor de cabeça
    @SerializedName("s10")
    private boolean s10;     // Falta de ar
    @SerializedName("s11")
    private boolean s11;     // Perda de paladar ou olfato
    @SerializedName("lat")
    private float latitude;
    @SerializedName("lon")
    private float longitude;
    @SerializedName("p")
    private float precisao;
    @SerializedName("updatedAt")
    private String updatedAt;
    @SerializedName("locais")
    private List<ModelLocal> locais;

    public ModelUsuario(String id, String id_aparelho, int nivel, boolean s0, boolean s1, boolean s2, boolean s3, boolean s4, boolean s5, boolean s6, boolean s7, boolean s8, boolean s9, boolean s10, boolean s11, float latitude, float longitude, float precisao, String updatedAt, List<ModelLocal> locais) {

        this.id = id;
        this.id_aparelho = id_aparelho;
        this.n = nivel;
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
        this.latitude = latitude;
        this.longitude = longitude;
        this.precisao = precisao;
        this.updatedAt = updatedAt;
        this.locais = locais;
    }

    public String getId() {

        return id;
    }

    public String getIdAparelho() {

        return id_aparelho;
    }

    public int getNivel() {

        return n;
    }

    public boolean getS0() {

        return s0;
    }

    public boolean getS1() {

        return s1;
    }

    public boolean getS2() {

        return s2;
    }

    public boolean getS3() {

        return s3;
    }

    public boolean getS4() {

        return s4;
    }

    public boolean getS5() {

        return s5;
    }

    public boolean getS6() {

        return s6;
    }

    public boolean getS7() {

        return s7;
    }

    public boolean getS8() {

        return s8;
    }

    public boolean getS9() {

        return s9;
    }

    public boolean getS10() {

        return s10;
    }

    public boolean getS11() {

        return s11;
    }

    public List<ModelLocal> getLocais() {

        return locais;
    }

    @Override
    public LatLng getPosition() {

        return new LatLng(latitude, longitude);
    }

    @Override
    public String getTitle() {

        return "Sintomas";
    }

    @Override
    public String getSnippet() {

        String result = "";

        if (s0) result += "\nContato com pessoa do exterior\n";
        if (s1) result += "\nFebre";
        if (s2) result += "\nCansaço";
        if (s3) result += "\nTosse";
        if (s4) result += "\nEspirros";
        if (s5) result += "\nDores no corpo ou mal-estar";
        if (s6) result += "\nCoriza ou nariz entupido";
        if (s7) result += "\nDor de garganta";
        if (s8) result += "\nDiarreia";
        if (s9) result += "\nDor de cabeça";
        if (s10) result += "\nFalta de ar";
        if (s11) result += "\nPerda de paladar ou olfato";

        if (result.length() == 0) result += "\nUsuário sem sintomas";

        result += String.format(Locale.ENGLISH, "\n\nPrecisão: %.2f metros", precisao);

        return result;
    }
}