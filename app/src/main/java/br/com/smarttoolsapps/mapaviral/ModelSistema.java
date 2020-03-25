package br.com.smarttoolsapps.mapaviral;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;

import java.util.Locale;

public class ModelSistema {

    @SerializedName("autorizado")
    private boolean autorizado;
    @SerializedName("base_url")
    private String base_url;
    @SerializedName("lat")
    private float lat;
    @SerializedName("lon")
    private float lon;
    @SerializedName("zoom")
    private float zoom;

    public ModelSistema(boolean autorizado, String base_url, float lat, float lon, float zoom) {

        this.autorizado = autorizado;
        this.base_url = base_url;
        this.lat = lat;
        this.lon = lon;
        this.zoom = zoom;
    }

    public String getBaseUrl() {

        return base_url;
    }

    public LatLng getLatLng() {

        return new LatLng(lat, lon);
    }

    public float getZoom() {

        return zoom;
    }
}