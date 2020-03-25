package br.com.smarttoolsapps.mapaviral;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class ModelLocal {

    @SerializedName("lat")
    private float lat;
    @SerializedName("lon")
    private float lon;
    @SerializedName("updatedAt")
    private String updatedAt;

    public ModelLocal(float lat, float lon, String updatedAt) {

        this.lat = lat;
        this.lon = lon;
        this.updatedAt = updatedAt;
    }

    public LatLng getLatLng() {

        return new LatLng(lat, lon);
    }

    public String getTime() {

        return updatedAt;
    }
}