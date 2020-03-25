package br.com.smarttoolsapps.mapaviral;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.paolorotolo.appintro.AppIntro;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

import br.com.smarttoolsapps.mapaviral.R;
import mumayank.com.airlocationlibrary.AirLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntroActivity extends AppIntro {

    public static final int CODE_PERMISSION_REQUEST = 100;

    private Fragment intro1;
    private Fragment intro2;
    private Fragment intro3;
    private Fragment intro4;
    private Fragment intro5;

    public static DialogHelper dialogHelper;
    private AppConfig appConfig;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        appConfig = new AppConfig(getApplicationContext());

        if (appConfig.isHideConfigMsgIntro()) {

            Intent i = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(i);

            finish();

            return;
        }

        dialogHelper = new DialogHelper(this);

        intro1 = new FragmentIntro1();
        intro2 = new FragmentIntro2();
        intro3 = new FragmentIntro3();
        intro4 = new FragmentIntro4();
        intro5 = new FragmentIntro5();

        addSlide(intro1);
        addSlide(intro2);
        addSlide(intro3);
        addSlide(intro4);
        addSlide(intro5);

        showSkipButton(false);

        getPager().setOffscreenPageLimit(4);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {

        super.onDonePressed(currentFragment);

        Switch switchTermos = findViewById(R.id.switchTermos);
        Switch switchGPS = findViewById(R.id.switchGPS);

        if (!switchTermos.isChecked()) {

            getPager().setCurrentItem(2);

            dialogHelper.showError("É necessário concordar com os Termos de Uso e Privacidade do aplicativo.");

            return;
        }

        if (!switchGPS.isChecked()) {

            getPager().setCurrentItem(3);

            dialogHelper.showError("É necessário permitir a obtenção da localização do seu aparelho.");

            return;
        }

        dialogHelper.showProgress();

        new AirLocation(IntroActivity.this, true, true, new AirLocation.Callbacks() {

            @Override
            public void onSuccess(@NotNull final Location location) {

                Geocoder gcd = new Geocoder(IntroActivity.this, Locale.getDefault());

                try {

                    List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    if (addresses.size() > 0) {

                        final String uf = addresses.get(0).getAdminArea();

                        RetrofitEndpoints endpoints = RetrofitClientInstance.getRetrofitInstance().create(RetrofitEndpoints.class);
                        Call<ModelRequest> call = endpoints.sistemaInfo(uf);

                        call.enqueue(new Callback<ModelRequest>() {

                            @Override
                            public void onResponse(Call<ModelRequest> call, Response<ModelRequest> response) {

                                if (response.isSuccessful()) {

                                    if (response.body().getError() == 0) {

                                        final ModelSistema sistema = gson.fromJson(gson.toJson(response.body().getData().get(0)), ModelSistema.class);

                                        Switch s0 = getPager().findViewById(R.id.switchS0);
                                        Switch s1 = getPager().findViewById(R.id.switchS1);
                                        Switch s2 = getPager().findViewById(R.id.switchS2);
                                        Switch s3 = getPager().findViewById(R.id.switchS3);
                                        Switch s4 = getPager().findViewById(R.id.switchS4);
                                        Switch s5 = getPager().findViewById(R.id.switchS5);
                                        Switch s6 = getPager().findViewById(R.id.switchS6);
                                        Switch s7 = getPager().findViewById(R.id.switchS7);
                                        Switch s8 = getPager().findViewById(R.id.switchS8);
                                        Switch s9 = getPager().findViewById(R.id.switchS9);
                                        Switch s10 = getPager().findViewById(R.id.switchS10);
                                        Switch s11 = getPager().findViewById(R.id.switchS11);

                                        float lat = (float) location.getLatitude();
                                        float lon = (float) location.getLongitude();
                                        float p = location.getAccuracy();

                                        RetrofitEndpoints endpoints2 = RetrofitClientUF.getRetrofitInstance(sistema.getBaseUrl()).create(RetrofitEndpoints.class);
                                        Call<ModelRequest> call2 = endpoints2.usuariosCadastrar(appConfig.getDeviceID(), s0.isChecked(), s1.isChecked(), s2.isChecked(), s3.isChecked(), s4.isChecked(), s5.isChecked(), s6.isChecked(), s7.isChecked(), s8.isChecked(), s9.isChecked(), s10.isChecked(), s11.isChecked(), lat, lon, p, uf);

                                        call2.enqueue(new Callback<ModelRequest>() {

                                            @Override
                                            public void onResponse(Call<ModelRequest> call, Response<ModelRequest> response) {

                                                if (response.isSuccessful()) {

                                                    if (response.body().getError() == 0) {

                                                        appConfig.setHideConfigMsgIntro(true);
                                                        appConfig.setUsuario(gson.toJson(response.body().getData().get(0)));
                                                        appConfig.setSistema(gson.toJson(sistema));

                                                        Intent i = new Intent(IntroActivity.this, MainActivity.class);
                                                        startActivity(i);

                                                        finish();
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
                    }
                    else {

                        dialogHelper.dismissProgress();
                        dialogHelper.showError("Não foi possível obter sua Unidade da Federação. Tente ativar o GPS do aparelho.");
                    }
                }
                catch (Exception e) {

                    dialogHelper.dismissProgress();
                    dialogHelper.showError("Não foi possível obter sua Unidade da Federação. Tente ativar o GPS do seu aparelho.");

                    Log.d("DEBUG", e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailed(@NotNull AirLocation.LocationFailedEnum locationFailedEnum) {

                dialogHelper.dismissProgress();
                dialogHelper.showError("Não foi possível obter a localização do seu aparelho. Ative a Localização GPS e tente novamente.");
            }
        });
    }

    public void buttonTermosDeUso(View view) {

        dialogHelper.showProgressDelayed(500, new Runnable() {

            @Override
            public void run() {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        new MaterialDialog.Builder(IntroActivity.this)
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CODE_PERMISSION_REQUEST) {

            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

                    dialogHelper.showError("É nesserário permitir que o aplicativo tenha acesso à localização do aparelho para ajudar na criação das zonas de risco.");
                }
                else {

                    dialogHelper.showError("Você desativou a solicitação de permissão para este aplicativo.\n\nPara ativar a permissão de localização, acesse as permissões deste aplicativo nas configurações do seu aparelho.");
                }

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        ((Switch)findViewById(R.id.switchGPS)).setChecked(false);
                    }
                }, 500);
            }
        }
    }
}
