package com.arnoldvaz27.goldenhospital;

import static com.arnoldvaz27.doctors.CustomToast.showToast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import com.arnoldvaz27.doctors.DoctorsHome;
import com.arnoldvaz27.goldenhospital.databinding.PatientBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Patient extends AppCompatActivity {

    PatientBinding binding;
    private EditText patientName, patientAge, patientEmail, patientPhoneNumber,patientDisease;
    private TextView patientProfile;
    private CardView patientMale, patientFemale;
    private Button patientReset, patientSave;
    private ProgressBar patientProgress;
    private DatabaseReference RootRef;
    private String currentUserId;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.golden));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        binding = DataBindingUtil.setContentView(this,R.layout.patient);

        RootRef = FirebaseDatabase.getInstance().getReference();

        initViews();

        currentUserId = getIntent().getStringExtra("userId");


        patientMale.setOnClickListener(v -> patientProfile.setText("Male"));

        patientFemale.setOnClickListener(v -> patientProfile.setText("Female"));
        
        patientReset.setOnClickListener(v -> {
            patientName.setText("");
            patientAge.setText("");
            patientEmail.setText("");
            patientPhoneNumber.setText("");
            patientProfile.setText("");
        });

        patientSave.setOnClickListener(v -> {
            String name = patientName.getText().toString();
            String age = patientAge.getText().toString();
            String email = patientEmail.getText().toString();
            String disease = patientDisease.getText().toString();
            String phoneNumber = patientPhoneNumber.getText().toString();
            String profile = patientProfile.getText().toString();
            if (name.equals("") ||  age.equals("") ||  email.equals("") || phoneNumber.equals("") || profile.equals("") || disease.equals("")) {
                showToast(getApplicationContext(), "Please enter all the details to Save", R.color.red);
            } else {
                patientSavingDetails();
            }
        });
    }

    private void patientSavingDetails() {

        patientProgress.setVisibility(View.VISIBLE);
        patientSave.setVisibility(View.GONE);
        HashMap<String, Object> doctor = new HashMap<>();
        doctor.put("uid", currentUserId);
        doctor.put("name", patientName.getText().toString());
        doctor.put("age", patientAge.getText().toString());
        doctor.put("status", "Not Available");
        doctor.put("doctorAppointed", "None");
        doctor.put("disease", patientDisease.getText().toString());
        doctor.put("email", patientEmail.getText().toString());
        doctor.put("phoneNumber", patientPhoneNumber.getText().toString());
        doctor.put("profile", patientProfile.getText().toString());
        RootRef.child("Users").child(currentUserId).setValue(doctor).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap<String, Object> doctor1 = new HashMap<>();
                doctor1.put("uid", currentUserId);
                doctor1.put("name", patientName.getText().toString());
                doctor1.put("age", patientAge.getText().toString());
                doctor1.put("disease", patientDisease.getText().toString());
                doctor1.put("status", "Not Available");
                doctor1.put("doctorAppointed", "None");
                doctor1.put("email", patientEmail.getText().toString());
                doctor1.put("phoneNumber", patientPhoneNumber.getText().toString());
                doctor1.put("profile", patientProfile.getText().toString());
                RootRef.child("Role").child("Patients").child(currentUserId).setValue(doctor1).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        final SharedPreferences fieldsVisibility1 = getSharedPreferences("Role", MODE_PRIVATE);
                        final SharedPreferences.Editor fieldsVisibility2 = fieldsVisibility1.edit();
                        fieldsVisibility2.putString("type", "Patient");
                        fieldsVisibility2.apply();
                        startActivity(new Intent(getApplicationContext(), DoctorsHome.class));
                        patientProgress.setVisibility(View.GONE);
                        patientSave.setVisibility(View.VISIBLE);
                        finishAffinity();
                    } else {
                        showToast(getApplicationContext(), "Error", R.color.red);
                        patientProgress.setVisibility(View.GONE);
                        patientSave.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                showToast(getApplicationContext(), "Data not saved", R.color.red);
                patientProgress.setVisibility(View.GONE);
                patientSave.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initViews() {
        patientName = binding.name;
        patientAge = binding.age;
        patientEmail = binding.email;
        patientDisease = binding.diseaseType;
        patientProfile = binding.profile;
        patientMale = binding.male;
        patientPhoneNumber = binding.phoneNumber;
        patientFemale = binding.female;
        patientReset = binding.Reset;
        patientSave = binding.Save;
        patientProgress = binding.Progress;
    }
}