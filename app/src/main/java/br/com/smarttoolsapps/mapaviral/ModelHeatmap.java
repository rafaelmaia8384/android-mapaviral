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

public class ModelHeatmap {

    @SerializedName("lat")
    private float lat;
    @SerializedName("lon")
    private float lon;

    public ModelHeatmap(float lat, float lon) {

        this.lat = lat;
        this.lon = lon;
    }

    public static List<ModelHeatmap> loadFromCsv(File file) {

        List<ModelHeatmap> result = new ArrayList<>();

        CsvReader csvReader = new CsvReader();
        csvReader.setContainsHeader(true);
        csvReader.setFieldSeparator('\t');

        try {

            CsvContainer csv = csvReader.read(file, StandardCharsets.UTF_8);

            for (CsvRow row : csv.getRows()) {

                result.add(new ModelHeatmap(Float.parseFloat(row.getField(0)), Float.parseFloat(row.getField(1))));
            }
        }
        catch (Exception e) {

            Log.d("DEBUG", e.getLocalizedMessage());
        }

        return result;
    }

    public LatLng getPosition() {

        return new LatLng(lat, lon);
    }
}