package br.com.smarttoolsapps.mapaviral;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import br.com.smarttoolsapps.mapaviral.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InformacoesActivity extends BaseActivity {

    public static final int SINTOMAS_ACTIVITY = 123;

    private Switch s0;
    private Switch s1;
    private Switch s2;
    private Switch s3;
    private Switch s4;
    private Switch s5;
    private Switch s6;
    private Switch s7;
    private Switch s8;
    private Switch s9;
    private Switch s10;
    private Switch s11;

    private ModelUsuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacoes);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        usuario = appConfig.getUsuario();

        s0 = findViewById(R.id.switchS0);
        s1 = findViewById(R.id.switchS1);
        s2 = findViewById(R.id.switchS2);
        s3 = findViewById(R.id.switchS3);
        s4 = findViewById(R.id.switchS4);
        s5 = findViewById(R.id.switchS5);
        s6 = findViewById(R.id.switchS6);
        s7 = findViewById(R.id.switchS7);
        s8 = findViewById(R.id.switchS8);
        s9 = findViewById(R.id.switchS9);
        s10 = findViewById(R.id.switchS10);
        s11 = findViewById(R.id.switchS11);

        s0.setChecked(usuario.getS0());
        s1.setChecked(usuario.getS1());
        s2.setChecked(usuario.getS2());
        s3.setChecked(usuario.getS3());
        s4.setChecked(usuario.getS4());
        s5.setChecked(usuario.getS5());
        s6.setChecked(usuario.getS6());
        s7.setChecked(usuario.getS7());
        s8.setChecked(usuario.getS8());
        s9.setChecked(usuario.getS9());
        s10.setChecked(usuario.getS10());
        s11.setChecked(usuario.getS11());
    }

    @Override
    public boolean onSupportNavigateUp(){

        finish();

        return true;
    }

    public void buttonAtualizarSintomas(View view) {
        
        dialogHelper.showProgress();

        String id_aparelho = appConfig.getDeviceID();

        Call<ModelRequest> call = endpoints.usuariosAtualizarSintomas(id_aparelho, s0.isChecked(), s1.isChecked(), s2.isChecked(), s3.isChecked(), s4.isChecked(), s5.isChecked(), s6.isChecked(), s7.isChecked(), s8.isChecked(), s9.isChecked(), s10.isChecked());

        call.enqueue(new Callback<ModelRequest>() {

            @Override
            public void onResponse(Call<ModelRequest> call, Response<ModelRequest> response) {

                if (response.isSuccessful()) {

                    if (response.body().getError() == 0) {

                        appConfig.setUsuario(gson.toJson(response.body().getData().get(0)));

                        setResult(1);
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

    public void buttonExcluirInformacoes(View view) {

        dialogHelper.showProgressDelayed(500, new Runnable() {

            @Override
            public void run() {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        dialogHelper.confirmDialog(true, "Excluir dados", "Ao excluir seus dados do sistema você não poderá mais acessar as informações no mapa.\n\nDeseja continuar?", "Cancelar", new MaterialDialog.SingleButtonCallback() {

                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                dialogHelper.showProgress();

                                Call<ModelRequest> call = endpoints.usuariosExcluir(appConfig.getDeviceID());

                                call.enqueue(new Callback<ModelRequest>() {

                                    @Override
                                    public void onResponse(Call<ModelRequest> call, Response<ModelRequest> response) {

                                        if (response.isSuccessful()) {

                                            if (response.body().getError() == 0) {

                                                appConfig.setHideConfigMsgIntro(false);

                                                setResult(2);
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
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }
}
