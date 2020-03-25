package br.com.smarttoolsapps.mapaviral;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

;

public class LocationUpdateService extends Service {

    private final static int TIME_INTERVAL = 1000* 60 * 1000; //60 segundos
    private Handler handler;
    private Gson gson = new Gson();

    public static void startService(Context ctx) {

        Intent i = new Intent(ctx, LocationUpdateService.class);
        ctx.startService(i);
    }

    public static void stopService(Context ctx) {

        Intent i = new Intent(ctx, LocationUpdateService.class);
        ctx.stopService(i);
    }

    @Override
    public void onCreate() {

        final AppConfig appConfig = new AppConfig(getApplicationContext());

        handler = new Handler(Looper.getMainLooper());

        final RetrofitEndpoints endpoints = RetrofitClientUF.getRetrofitInstance(appConfig.getSistema().getBaseUrl()).create(RetrofitEndpoints.class);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                try {

                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    float longitude = (float) location.getLongitude();
                    float latitude = (float) location.getLatitude();
                    float precision = location.getAccuracy();

                    Call<ModelRequest> call = endpoints.usuariosAtualizarLocal(appConfig.getDeviceID(), appConfig.isAlertaAtivado(), latitude, longitude, precision);

                    call.enqueue(new Callback<ModelRequest>() {

                        @Override
                        public void onResponse(Call<ModelRequest> call, Response<ModelRequest> response) {

                            if (response.isSuccessful()) {

                                if (response.body().getError() == 0) {

                                    int count = 0;

                                    for (int i = 0; i < response.body().getData().size(); i++) {

                                        ModelUsuario usuario = gson.fromJson(gson.toJson(response.body().getData().get(i)), ModelUsuario.class);

                                        if (!appConfig.alertaExistente(usuario.getIdAparelho())) {

                                            appConfig.registrarAlerta(usuario.getIdAparelho());

                                            count++;
                                        }
                                    }

                                    if (count > 0) {

                                        notificar();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ModelRequest> call, Throwable t) {

                            Log.d("onFailure", t.getLocalizedMessage());
                        }
                    });
                }
                catch (SecurityException e) {

                    Log.d("DEBUG", e.getLocalizedMessage());
                }

                handler.postDelayed(this, TIME_INTERVAL);
            }
        }, TIME_INTERVAL);

        super.onCreate();
    }

    private void notificar() {

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel("ID", "Name", importance);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(getApplicationContext(), notificationChannel.getId());
        }
        else {

            builder = new NotificationCompat.Builder(getApplicationContext());
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("notification", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_ONE_SHOT);

        builder = builder
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                .setContentTitle("Alerta de aproximação")
                .setTicker("Mapa Viral")
                .setContentText("Um usuário com sintomas está próximo de você.")
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(0, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("DEBUG", "LocationUpdateService started");

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
