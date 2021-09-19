package com.arnoldvaz27.patients;

import static com.arnoldvaz27.doctors.CustomToast.showToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arnoldvaz27.patients.databinding.PatientHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class PatientHome extends AppCompatActivity {

    PatientHomeBinding binding;
    TextView name,disease,gender,status,appointed;
    EditText number,email,age;
    ImageView info;
    private LinearLayout savingLayout;
    private Button save,reset,edit;
    private DatabaseReference eventsRef,rootRef;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    ProgressBar progressBar;
    String Name,Disease,Gender,Status,Appointed,Number,Email,Age;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.yellow));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        binding = DataBindingUtil.setContentView(this, R.layout.patient_home);

        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(getApplicationContext(), findViewById(R.id.settings));
                popup.getMenuInflater().inflate(R.menu.settings_file, popup.getMenu());
                popup.getMenu().findItem(R.id.profile).setVisible(false);

                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.logout) {
                        Toast.makeText(getApplicationContext(), "The application is closing", Toast.LENGTH_SHORT).show();
                        final SharedPreferences fieldsVisibility1 = getSharedPreferences("Role", MODE_PRIVATE);
                        final SharedPreferences.Editor fieldsVisibility2 = fieldsVisibility1.edit();
                        fieldsVisibility2.putString("type", null);
                        fieldsVisibility2.apply();
                        finishAffinity();
                    }
                    return true;
                });

                popup.show();
            }
        });

        name = binding.Name;
        disease = binding.disease;
        gender = binding.gender;
        status = binding.status;
        appointed = binding.doctorAppointed;
        number = binding.number;
        email = binding.email;
        save = binding.Save;
        reset = binding.Reset;
        edit = binding.Edit;
        age = binding.age;
        savingLayout = binding.savingLinear;
        info = binding.info;
        progressBar = binding.Progress;

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Role").child("Patients");
        rootRef = FirebaseDatabase.getInstance().getReference().child("Users");

        eventsRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                    Number = Objects.requireNonNull(dataSnapshot.child("phoneNumber").getValue()).toString();
                    Email = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
                    Disease = Objects.requireNonNull(dataSnapshot.child("disease").getValue()).toString();
                    Gender = Objects.requireNonNull(dataSnapshot.child("profile").getValue()).toString();
                    Age = Objects.requireNonNull(dataSnapshot.child("age").getValue()).toString();
                    Status = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();
                    Appointed = Objects.requireNonNull(dataSnapshot.child("doctorAppointed").getValue()).toString();

                    SetData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Info();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> patient = new HashMap<>();
                patient.put("name", name.getText().toString());
                patient.put("phoneNumber", number.getText().toString());
                patient.put("email", email.getText().toString());
                patient.put("disease", disease.getText().toString());
                patient.put("profile", gender.getText().toString());
                patient.put("age", age.getText().toString());
                patient.put("status", status.getText().toString());
                patient.put("doctorAppointed", appointed.getText().toString());
                patient.put("uid", currentUserId);
                eventsRef.child(currentUserId).setValue(patient).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            rootRef.child(currentUserId).setValue(patient).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        savingLayout.setVisibility(View.GONE);
                                        edit.setVisibility(View.VISIBLE);
                                        number.setEnabled(false);
                                        email.setEnabled(false);
                                        age.setEnabled(false);
                                        showToast(getApplicationContext(), "Data updated", R.color.green);
                                    } else {
                                        showToast(getApplicationContext(), "Please try again", R.color.red);
                                    }
                                }
                            });
                        } else {
                            showToast(getApplicationContext(), "Please try again", R.color.red);
                        }
                    }
                });
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savingLayout.setVisibility(View.VISIBLE);
                edit.setVisibility(View.GONE);
                number.setEnabled(true);
                email.setEnabled(true);
                age.setEnabled(true);
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savingLayout.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
                number.setEnabled(false);
                email.setEnabled(false);
                age.setEnabled(false);
            }
        });
    }

    private void SetData() {
        switch (Status) {
            case "Not Available":
                status.setTextColor(getResources().getColor(com.arnoldvaz27.doctors.R.color.red));
                break;
            case "Cured":
                status.setTextColor(getResources().getColor(com.arnoldvaz27.doctors.R.color.green));
                break;
            case "Under Treatment":
                status.setTextColor(getResources().getColor(com.arnoldvaz27.doctors.R.color.yellow));
                break;
        }
        if (Appointed.equals("None")) {
            appointed.setTextColor(getResources().getColor(com.arnoldvaz27.doctors.R.color.red));
        } else {
            appointed.setTextColor(getResources().getColor(com.arnoldvaz27.doctors.R.color.green));
        }
        name.setText(Name);
        number.setText(Number);
        email.setText(Email);
        disease.setText(Disease);
        age.setText(Age);
        gender.setText(Gender);
        status.setText(Status);
        appointed.setText(Appointed);
        progressBar.setVisibility(View.GONE);
    }
    private void Info() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(PatientHome.this, R.style.AlertDialog);
        builder.setTitle("Note");
        builder.setCancelable(false);

        final TextView groupNameField = new TextView(PatientHome.this);
        groupNameField.setText("1) The details mentioned are in the following order \n\n--> Your Name --> Disease Type \n--> Mobile Number --> Email --> Age --> Gender --> Treatment Status \n--> Doctor Appointed");
        groupNameField.setPadding(20,30,20,20);
        groupNameField.setTextColor(Color.BLACK);

        groupNameField.setBackgroundColor(Color.WHITE);
        builder.setView(groupNameField);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }
}