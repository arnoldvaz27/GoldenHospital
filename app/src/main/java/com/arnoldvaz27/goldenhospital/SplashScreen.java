package com.arnoldvaz27.goldenhospital;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.arnoldvaz27.doctors.DoctorsHome;
import com.arnoldvaz27.goldenhospital.databinding.SplashScreenBinding;
import com.arnoldvaz27.management.ManagementHome;
import com.arnoldvaz27.nurses.NurseHome;
import com.arnoldvaz27.patients.PatientHome;

public class SplashScreen extends AppCompatActivity {

    SplashScreenBinding binding;
    private static final int SPLASH_SCREEN_TIME_OUT=2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.golden));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        binding = DataBindingUtil.setContentView(this, R.layout.splash_screen);  //binding xml layout to java file

        SharedPreferences settingLayoutType = getSharedPreferences("Role", MODE_PRIVATE);
        String type = settingLayoutType.getString("type", null);

        if(type == null){
            new Handler().postDelayed(() -> {
                Intent i = new Intent(SplashScreen.this,
                        Role.class);
                SplashScreen.this.startActivity(i);
                SplashScreen.this.finishAffinity();
            }, SPLASH_SCREEN_TIME_OUT);
        }else if(type.equals("Doctors")){
            new Handler().postDelayed(() -> {
                Intent i = new Intent(SplashScreen.this,
                        DoctorsHome.class);
                SplashScreen.this.startActivity(i);
                SplashScreen.this.finishAffinity();
            }, SPLASH_SCREEN_TIME_OUT);
        }else if(type.equals("Management")){
            new Handler().postDelayed(() -> {
                Intent i = new Intent(SplashScreen.this,
                        ManagementHome.class);
                SplashScreen.this.startActivity(i);
                SplashScreen.this.finishAffinity();
            }, SPLASH_SCREEN_TIME_OUT);
        }else if(type.equals("Nurse")){
            new Handler().postDelayed(() -> {
                Intent i = new Intent(SplashScreen.this,
                        NurseHome.class);
                SplashScreen.this.startActivity(i);
                SplashScreen.this.finishAffinity();
            }, SPLASH_SCREEN_TIME_OUT);
        }else if(type.equals("Patient")){
            new Handler().postDelayed(() -> {
                Intent i = new Intent(SplashScreen.this,
                        PatientHome.class);
                SplashScreen.this.startActivity(i);
                SplashScreen.this.finishAffinity();
            }, SPLASH_SCREEN_TIME_OUT);
        }
    }
}