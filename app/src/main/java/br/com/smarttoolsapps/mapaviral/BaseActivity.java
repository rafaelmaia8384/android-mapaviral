package br.com.smarttoolsapps.mapaviral;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

public class BaseActivity extends AppCompatActivity {

    protected DialogHelper dialogHelper;
    protected AppConfig appConfig;
    protected Gson gson = new Gson();
    protected RetrofitEndpoints endpoints;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        appConfig = new AppConfig(getApplicationContext());
        endpoints = RetrofitClientUF.getRetrofitInstance(appConfig.getSistema().getBaseUrl()).create(RetrofitEndpoints.class);
        dialogHelper = new DialogHelper(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }
}
