package com.arnoldvaz27.doctors;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

public class DoctorsHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.golden));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        setContentView(R.layout.doctors_home);
        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(getApplicationContext(), findViewById(R.id.settings));
                popup.getMenuInflater().inflate(R.menu.settings_file, popup.getMenu());

                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.logout) {
                        Toast.makeText(getApplicationContext(), "The application is closing", Toast.LENGTH_SHORT).show();
                        final SharedPreferences fieldsVisibility1 = getSharedPreferences("Role", MODE_PRIVATE);
                        final SharedPreferences.Editor fieldsVisibility2 = fieldsVisibility1.edit();
                        fieldsVisibility2.putString("type", null);
                        fieldsVisibility2.apply();
                        finishAffinity();
                    }
                    if (item.getItemId() == R.id.profile) {
                        startActivity(new Intent(getApplicationContext(),DoctorsProfile.class));
                    }
                    return true;
                });

                popup.show();
            }
        });
        findViewById(R.id.doctors).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),DoctorsList.class));
            }
        });
        findViewById(R.id.beds).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),BedsDoctorsData.class));
            }
        });
        findViewById(R.id.ambulances).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AmbulanceDoctorData.class));
            }
        });
        findViewById(R.id.medicines).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MedicinesDoctorData.class));
            }
        });
        findViewById(R.id.canteen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CanteenDoctorData.class));
            }
        });
        findViewById(R.id.services).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ServiceDoctorData.class));
            }
        });
        findViewById(R.id.nurses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),NurseDoctorsData.class));
            }
        });
        findViewById(R.id.patients).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),PatientDoctorData.class));
            }
        });
    }
}