package com.arnoldvaz27.goldenhospital;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.arnoldvaz27.goldenhospital.databinding.SplashScreenBinding;

public class SplashScreen extends AppCompatActivity {

    SplashScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.golden));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        binding = DataBindingUtil.setContentView(this, R.layout.splash_screen);  //binding xml layout to java file

        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashScreen.this,
                    Role.class);
            SplashScreen.this.startActivity(i);
            SplashScreen.this.finishAffinity();
        }, 3000);
    }
}