package com.arnoldvaz27.management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.arnoldvaz27.doctors.DoctorsList;

public class ManagementHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.golden));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        setContentView(R.layout.management_home);

        findViewById(R.id.doctors).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DoctorsList.class));
            }
        });
        findViewById(R.id.services).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ServicesData.class));
            }
        });
        findViewById(R.id.beds).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BedsData.class));
            }
        });
    }
}