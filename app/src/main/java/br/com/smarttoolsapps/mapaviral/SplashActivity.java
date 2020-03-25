package br.com.smarttoolsapps.mapaviral;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        AppConfig config = new AppConfig(getApplicationContext());

        Intent intent;

        if (config.isHideConfigMsgIntro()) {

            intent = new Intent(SplashActivity.this, MainActivity.class);
        }
        else {

            intent = new Intent(SplashActivity.this, IntroActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
