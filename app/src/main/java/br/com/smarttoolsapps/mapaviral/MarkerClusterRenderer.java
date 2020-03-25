package br.com.smarttoolsapps.mapaviral;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class MarkerClusterRenderer extends DefaultClusterRenderer<ModelUsuario> {   // 1

    private static final int MARKER_DIMENSION = 64;  // 2
    private static final float ZOOM_MIN_LEVEL = 12f;

    private final IconGenerator iconGenerator;
    private final ImageView markerImageView;
    private float zoom;
    private LatLngBounds bounds = new LatLngBounds(new LatLng(0f, 0f), new LatLng(0f, 0f));

    public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<ModelUsuario> clusterManager) {

        super(context, map, clusterManager);

        iconGenerator = new IconGenerator(context);  // 3
        markerImageView = new ImageView(context);
        markerImageView.setLayoutParams(new ViewGroup.LayoutParams(MARKER_DIMENSION, MARKER_DIMENSION));
        iconGenerator.setContentView(markerImageView);  // 4
    }

    @Override
    protected void onBeforeClusterItemRendered(ModelUsuario item, MarkerOptions markerOptions) { // 5

        int drawable;

        if (item.getNivel() < 1) drawable = R.drawable.location_vector_icon_baixa;
        else if (item.getNivel() < 2) drawable = R.drawable.location_vector_icon_moderada;
        else drawable = R.drawable.location_vector_icon_alta;

        markerImageView.setImageResource(drawable);  // 6

        Bitmap icon = iconGenerator.makeIcon();  // 7

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));  // 8
        markerOptions.title(item.getTitle());
    }

    @Override
    protected void onClusterItemRendered(ModelUsuario item, Marker marker) {

        marker.setTag(item.getId());

        super.onClusterItemRendered(item, marker);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<ModelUsuario> cluster, MarkerOptions markerOptions) {

        markerOptions.visible(false);

        super.onBeforeClusterRendered(cluster, markerOptions);
    }

    public void setBounds(LatLngBounds b) {

        bounds = b;
    }

    public void setZoom(float z) {

        zoom = z;
    }

    @Override
    protected boolean shouldRenderAsCluster(final Cluster<ModelUsuario> cluster) {

        boolean visible = bounds.contains(cluster.getPosition());

        return (zoom < ZOOM_MIN_LEVEL || cluster.getSize() >= 5 || !visible);
    }
}