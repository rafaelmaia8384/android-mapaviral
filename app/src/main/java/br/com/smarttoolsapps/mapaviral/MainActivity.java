package br.com.smarttoolsapps.mapaviral;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements OnMapReadyCallback {

    private static final float MIN_ZOOM_LEVEL = 12f;

    private GoogleMap map;
    private List<LatLng> heatMap = new ArrayList<>();
    private Map<String, Marker> markerList = new HashMap<>();
    private BitmapDescriptor bitmapRed;
    private BitmapDescriptor bitmapGreen;
    private BitmapDescriptor bitmapOrange;
    private boolean markerClicked;
    private boolean notification;
    private TileOverlay tileDeslocamento;
    private Snackbar snack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LocationUpdateService.stopService(this);
        LocationUpdateService.startService(this);

        bitmapGreen = BitmapDescriptorFactory.fromBitmap(createCustomMarker(this, R.layout.layout_marker_green));
        bitmapOrange = BitmapDescriptorFactory.fromBitmap(createCustomMarker(this, R.layout.layout_marker_orange));
        bitmapRed = BitmapDescriptorFactory.fromBitmap(createCustomMarker(this, R.layout.layout_marker_red));

        if (!appConfig.isHideAvisoInicial()) {

            mostrarAvisoInicial();
        }
    }

    private void mostrarAvisoInicial() {

        new MaterialDialog.Builder(MainActivity.this)
                .autoDismiss(true)
                .title("Bem vindo")
                .customView(R.layout.layout_aviso_inicial, false)
                .positiveText("OK")
                .showListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        View content = ((MaterialDialog)dialog).getCustomView();
                        ((CheckBox)content.findViewById(R.id.checkAviso)).setChecked(appConfig.isHideAvisoInicial());
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {

                        View content = dialog.getCustomView();

                        boolean checked = ((CheckBox)content.findViewById(R.id.checkAviso)).isChecked();
                        appConfig.setHideAvisoInicial(checked);
                    }
                })
                .cancelable(true)
                .show();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        ModelSistema sistema = appConfig.getSistema();

        map = googleMap;

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sistema.getLatLng(), sistema.getZoom()));
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setRotateGesturesEnabled(false);

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {

            @Override
            public void onCameraIdle() {

                if (googleMap.getCameraPosition().zoom >= MIN_ZOOM_LEVEL) {

                    if (markerClicked) {

                        markerClicked = false;

                        return;
                    }

                    VisibleRegion visibleRegion = googleMap.getProjection().getVisibleRegion();
                    final double distance = SphericalUtil.computeDistanceBetween(visibleRegion.farLeft, googleMap.getCameraPosition().target);

                    Call<ModelRequest> call = endpoints.usuariosListar(appConfig.getDeviceID(), (float)googleMap.getCameraPosition().target.latitude, (float)googleMap.getCameraPosition().target.longitude, (float)distance);
                    call.enqueue(new Callback<ModelRequest>() {

                        @Override
                        public void onResponse(Call<ModelRequest> call, Response<ModelRequest> response) {

                            if (response.isSuccessful()) {

                                if (response.body().getError() == 0) {

                                    for (int i = 0; i < response.body().getData().size(); i++) {

                                        ModelUsuario usuario = gson.fromJson(gson.toJson(response.body().getData().get(i)), ModelUsuario.class);

                                        if (!markerList.containsKey(usuario.getId())) {

                                            MarkerOptions markerOptions = new MarkerOptions();
                                            markerOptions.position(usuario.getPosition());
                                            markerOptions.title("Sintomas");
                                            markerOptions.snippet(usuario.getSnippet());
                                            markerOptions.icon(usuario.getNivel() == 2 ? bitmapRed : usuario.getNivel() == 1 ? bitmapOrange : bitmapGreen);

                                            Marker marker = googleMap.addMarker(markerOptions);
                                            marker.setTag(usuario.getId());

                                            markerList.put(usuario.getId(), marker);
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelRequest> call, Throwable t) {

                        }
                    });
                }
                else {

                    if (tileDeslocamento != null && snack != null) {

                        tileDeslocamento.remove();
                        tileDeslocamento = null;
                        snack.dismiss();
                        snack = null;
                    }

                    removeAllMarkers();
                }
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {

                markerClicked = true;

                return tileDeslocamento != null;
            }
        });

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(final Marker marker) {

                dialogHelper.showProgress();

                marker.hideInfoWindow();

                Call<ModelRequest> call = endpoints.usuariosObterPerfil(appConfig.getDeviceID(), (String)marker.getTag());
                call.enqueue(new Callback<ModelRequest>() {

                    @Override
                    public void onResponse(Call<ModelRequest> call, Response<ModelRequest> response) {

                        if (response.isSuccessful()) {

                            if (response.body().getError() == 0) {

                                dialogHelper.dismissProgress();

                                ModelUsuario u = gson.fromJson(gson.toJson(response.body().getData().get(0)), ModelUsuario.class);

                                List<LatLng> heat = new ArrayList<>();
                                List<ModelLocal> l = u.getLocais();

                                for (int i = 0; i < l.size(); i++) {

                                    heat.add(l.get(i).getLatLng());
                                }

                                int colors[] = { Color.YELLOW, Color.GREEN, Color.BLUE };
                                float points[] = { 0.25f, 0.50f, 0.75f };
                                final HeatmapTileProvider provider = new HeatmapTileProvider.Builder().data(heat).build();

                                provider.setGradient(new Gradient(colors, points));
                                provider.setOpacity(1.0);
                                provider.setRadius(100);

                                tileDeslocamento = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));

                                snack = Snackbar.make(findViewById(R.id.map), "A área azul representa os últimos locais frequentados pelo usuário (48h).", Snackbar.LENGTH_INDEFINITE);

                                snack.setAction("Remover", new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {

                                        tileDeslocamento.remove();
                                        tileDeslocamento = null;
                                    }
                                });

                                snack.show();
                            }
                            else {

                                dialogHelper.dismissProgress();
                                dialogHelper.showError(response.body().getMsg());
                            }
                        }
                        else {

                            dialogHelper.dismissProgress();
                            dialogHelper.showError("Erro de comunicação com o servidor. Tente novamente em instantes.");
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelRequest> call, Throwable t) {

                        dialogHelper.dismissProgress();
                        dialogHelper.showError("Não foi possível conectar com o servidor. Tente novamente em instantes.");
                    }
                });
            }
        });

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {

                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                if (marker.getTitle() != null) {

                    LinearLayout info = new LinearLayout(MainActivity.this);
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(MainActivity.this);
                    title.setTextColor(Color.DKGRAY);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(MainActivity.this);
                    snippet.setTextColor(Color.GRAY);
                    snippet.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
                    snippet.setText(marker.getSnippet());

                    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    View button = layoutInflater.inflate(R.layout.layout_infowindow_button, null);

                    info.addView(title);
                    info.addView(snippet);
                    info.addView(button);

                    return info;
                }

                return null;
            }
        });

        setUpMarkers(googleMap);

        if (notification) {

            notification = false;

            goToHome();
        }
    }

    private static Bitmap createCustomMarker(Context context, int res) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(res, null);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        marker.setLayoutParams(new ViewGroup.LayoutParams(42, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

    private void removeAllMarkers() {

        final Set<String> keys = markerList.keySet();

        if (keys.size() == 0) return;

        dialogHelper.showProgress();

        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {

                for (final String key : keys) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            markerList.get(key).remove();
                        }
                    });
                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {

                                markerList.clear();
                            }
                        }, 1000);
                    }
                });

                dialogHelper.dismissProgress();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu1:
                dialogHelper.showProgressDelayed(500, new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(MainActivity.this, InformacoesActivity.class);
                        startActivityForResult(i, InformacoesActivity.SINTOMAS_ACTIVITY);
                    }
                });
                return true;
            case R.id.menu2:

                dialogHelper.showProgress();

                Call<ModelRequest> call = endpoints.usuariosEstatisticas(appConfig.getDeviceID());
                call.enqueue(new Callback<ModelRequest>() {

                    @Override
                    public void onResponse(Call<ModelRequest> call, Response<ModelRequest> response) {

                        if (response.isSuccessful()) {

                            if (response.body().getError() == 0) {

                                dialogHelper.dismissProgress();

                                String json = gson.toJson(response.body().getData().get(0));

                                Intent i = new Intent(MainActivity.this, EstatisticasActivity.class);
                                i.putExtra("estatisticas", json);
                                startActivity(i);
                            }
                            else {

                                dialogHelper.dismissProgress();
                                dialogHelper.showError(response.body().getMsg());
                            }
                        }
                        else {

                            dialogHelper.dismissProgress();
                            dialogHelper.showError("Erro de comunicação com o servidor. Tente novamente em instantes.");
                        }
                    }

                    @Override
                    public void onFailure(Call<ModelRequest> call, Throwable t) {

                        dialogHelper.dismissProgress();
                        dialogHelper.showError("Não foi possível conectar com o servidor. Tente novamente em instantes.");

                        Log.d("DEBUG", t.getLocalizedMessage());
                    }
                });
                return true;
            case R.id.menu3:

                dialogHelper.showProgressDelayed(500, new Runnable() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                new MaterialDialog.Builder(MainActivity.this)
                                        .autoDismiss(true)
                                        .title("Alerta de proximidade")
                                        .customView(R.layout.layout_alertas_proximidade, false)
                                        .positiveText("Salvar")
                                        .negativeText("Cancelar")
                                        .showListener(new DialogInterface.OnShowListener() {

                                            @Override
                                            public void onShow(DialogInterface dialog) {

                                                View content = ((MaterialDialog)dialog).getCustomView();
                                                ((Switch)content.findViewById(R.id.switchAlerta)).setChecked(appConfig.isAlertaAtivado());
                                            }
                                        })
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {

                                            @Override
                                            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {

                                                View content = dialog.getCustomView();

                                                appConfig.setAlertaAtivado(((Switch)content.findViewById(R.id.switchAlerta)).isChecked());
                                            }
                                        })
                                        .cancelable(true)
                                        .show();
                            }
                        });
                    }
                });
                return true;
            case R.id.menu4:
                dialogHelper.showProgressDelayed(500, new Runnable() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                new MaterialDialog.Builder(MainActivity.this)
                                        .autoDismiss(true)
                                        .title("Termos de Uso e Privacidade")
                                        .customView(R.layout.layout_termos_de_uso, false)
                                        .positiveText("Ok")
                                        .cancelable(true)
                                        .show();
                            }
                        });
                    }
                });
                return true;
            case R.id.menu5:
                dialogHelper.showProgressDelayed(500, new Runnable() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                new MaterialDialog.Builder(MainActivity.this)
                                        .autoDismiss(true)
                                        .title("Sobre")
                                        .customView(R.layout.layout_sobre, false)
                                        .positiveText("Ok")
                                        .cancelable(true)
                                        .show();
                            }
                        });
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void buttonVerLicenca1(View view) {

        dialogHelper.showProgressDelayed(500, new Runnable() {

            @Override
            public void run() {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        new MaterialDialog.Builder(MainActivity.this)
                                .autoDismiss(true)
                                .title("Licença")
                                .showListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialog) {

                                        View content = ((MaterialDialog)dialog).getCustomView();
                                        ((TextView)content.findViewById(R.id.textLicense)).setText(getResources().getString(R.string.license_appintro));
                                    }
                                })
                                .customView(R.layout.layout_license, false)
                                .positiveText("Ok")
                                .cancelable(true)
                                .show();
                    }
                });
            }
        });
    }

    public void buttonVerLicenca2(View view) {

        dialogHelper.showProgressDelayed(500, new Runnable() {

            @Override
            public void run() {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        new MaterialDialog.Builder(MainActivity.this)
                                .autoDismiss(true)
                                .title("Licença")
                                .showListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialog) {

                                        View content = ((MaterialDialog)dialog).getCustomView();
                                        ((TextView)content.findViewById(R.id.textLicense)).setText(getResources().getString(R.string.license_androidstorage));
                                    }
                                })
                                .customView(R.layout.layout_license, false)
                                .positiveText("Ok")
                                .cancelable(true)
                                .show();
                    }
                });
            }
        });
    }

    public void buttonVerLicenca3(View view) {

        dialogHelper.showProgressDelayed(500, new Runnable() {

            @Override
            public void run() {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        new MaterialDialog.Builder(MainActivity.this)
                                .autoDismiss(true)
                                .title("Licença")
                                .showListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialog) {

                                        View content = ((MaterialDialog)dialog).getCustomView();
                                        ((TextView)content.findViewById(R.id.textLicense)).setText(getResources().getString(R.string.license_materialdialog));
                                    }
                                })
                                .customView(R.layout.layout_license, false)
                                .positiveText("Ok")
                                .cancelable(true)
                                .show();
                    }
                });
            }
        });
    }

    public void buttonVerLicenca4(View view) {

        dialogHelper.showProgressDelayed(500, new Runnable() {

            @Override
            public void run() {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        new MaterialDialog.Builder(MainActivity.this)
                                .autoDismiss(true)
                                .title("Licença")
                                .showListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialog) {

                                        View content = ((MaterialDialog)dialog).getCustomView();
                                        ((TextView)content.findViewById(R.id.textLicense)).setText(getResources().getString(R.string.license_retrofit));
                                    }
                                })
                                .customView(R.layout.layout_license, false)
                                .positiveText("Ok")
                                .cancelable(true)
                                .show();
                    }
                });
            }
        });
    }

    public void buttonVerLicenca6(View view) {

        dialogHelper.showProgressDelayed(500, new Runnable() {

            @Override
            public void run() {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        new MaterialDialog.Builder(MainActivity.this)
                                .autoDismiss(true)
                                .title("Licença")
                                .showListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialog) {

                                        View content = ((MaterialDialog)dialog).getCustomView();
                                        ((TextView)content.findViewById(R.id.textLicense)).setText(getResources().getString(R.string.license_mpandroidchart));
                                    }
                                })
                                .customView(R.layout.layout_license, false)
                                .positiveText("Ok")
                                .cancelable(true)
                                .show();
                    }
                });
            }
        });
    }

    private void goToHome() {

        try {

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(),location.getLongitude()))
                    .zoom(13f).build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        }
        catch (SecurityException e) {

            Log.d("DEBUG", e.getLocalizedMessage());
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (getIntent().getBooleanExtra("notification", false)) {

            notification = true;

            if (map != null) {

                getIntent().getExtras().remove("notification");

                goToHome();
            }
        }
    }

    private void setUpMarkers(final GoogleMap googleMap) {

        dialogHelper.showProgress();

        List<ModelHeatmap> list = appConfig.getHeatMap();

        if (list == null) {

            Call<ResponseBody> call = endpoints.usuariosHeatMap(appConfig.getDeviceID());
            call.enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (response.isSuccessful()) {

                        File file = appConfig.writeResponseBodyToDisk(response.body());

                        if (file != null) {

                            List<ModelHeatmap> heatmap = ModelHeatmap.loadFromCsv(file);

                            for (int i = 0; i < heatmap.size(); i++) {

                                ModelHeatmap heat = heatmap.get(i);
                                heatMap.add(heat.getPosition());
                            }

                            HeatmapTileProvider provider = new HeatmapTileProvider.Builder().data(heatMap).build();
                            provider.setOpacity(0.7);
                            provider.setRadius(50);

                            googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
                        }
                        else {

                            dialogHelper.showError("Erro ao atualizar as zonas de risco. Tente novamente em instantes.");
                        }

                        dialogHelper.dismissProgress();
                    }
                    else {

                        dialogHelper.dismissProgress();
                        dialogHelper.showError("Erro de comunicação com o servidor. Tente novamente em instantes.");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    dialogHelper.dismissProgress();
                    dialogHelper.showError("Não foi possível conectar com o servidor. Tente novamente em instantes.");

                    Log.d("DEBUG", t.getLocalizedMessage());
                }
            });
        }
        else {

            for (int i = 0; i < list.size(); i++) {

                ModelHeatmap heat = list.get(i);
                heatMap.add(heat.getPosition());
            }

            HeatmapTileProvider provider = new HeatmapTileProvider.Builder().data(heatMap).build();
            provider.setOpacity(0.7);
            provider.setRadius(50);

            googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));

            dialogHelper.dismissProgress();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == InformacoesActivity.SINTOMAS_ACTIVITY) {

            if (resultCode == 1) {

                dialogHelper.showSuccess("Sintomas atualizados.\n\nPode levar alguns minutos para atualizar as informações no mapa.");
            }
            else if (resultCode == 2) {

                Intent i = new Intent(MainActivity.this, IntroActivity.class);
                startActivity(i);
                finish();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
