package com.arnoldvaz27.doctors;

import static com.arnoldvaz27.doctors.CustomToast.showToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arnoldvaz27.doctors.databinding.DoctorsProfileBinding;
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

public class DoctorsProfile extends AppCompatActivity {

    DoctorsProfileBinding binding;
    TextView name,gender;
    EditText number,email,age,designation,experience;
    ImageView info;
    private LinearLayout savingLayout;
    private Button save,reset,edit;
    private DatabaseReference eventsRef,rootsRef;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    ProgressBar progressBar;
    String Name,Designation,Gender,Experience,Number,Email,Age;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.yellow));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        binding = DataBindingUtil.setContentView(this, R.layout.doctors_profile);

        name = binding.Name;
        designation = binding.designation;
        gender = binding.gender;
        experience = binding.experience;
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
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Role").child("Doctors");
        rootsRef = FirebaseDatabase.getInstance().getReference().child("Users");

        eventsRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                    Number = Objects.requireNonNull(dataSnapshot.child("phoneNumber").getValue()).toString();
                    Email = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
                    Designation = Objects.requireNonNull(dataSnapshot.child("designation").getValue()).toString();
                    Gender = Objects.requireNonNull(dataSnapshot.child("profile").getValue()).toString();
                    Age = Objects.requireNonNull(dataSnapshot.child("age").getValue()).toString();
                    Experience = Objects.requireNonNull(dataSnapshot.child("experience").getValue()).toString();

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
                HashMap<String, Object> nurse = new HashMap<>();
                nurse.put("name", name.getText().toString());
                nurse.put("phoneNumber", number.getText().toString());
                nurse.put("email", email.getText().toString());
                nurse.put("designation", designation.getText().toString());
                nurse.put("profile", gender.getText().toString());
                nurse.put("age", age.getText().toString());
                nurse.put("experience", experience.getText().toString());
                nurse.put("uid", currentUserId);
                eventsRef.child(currentUserId).setValue(nurse).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            rootsRef.child(currentUserId).setValue(nurse).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        savingLayout.setVisibility(View.GONE);
                                        edit.setVisibility(View.VISIBLE);
                                        number.setEnabled(false);
                                        designation.setEnabled(false);
                                        email.setEnabled(false);
                                        age.setEnabled(false);
                                        experience.setEnabled(false);
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
                designation.setEnabled(true);
                email.setEnabled(true);
                age.setEnabled(true);
                experience.setEnabled(true);
                experience.setText(Experience);
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savingLayout.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
                number.setEnabled(false);
                email.setEnabled(false);
                designation.setEnabled(false);
                age.setEnabled(false);
                experience.setEnabled(false);
            }
        });
    }
    private void SetData() {
        name.setText(Name);
        number.setText(Number);
        email.setText(Email);
        designation.setText(Designation);
        age.setText(Age);
        gender.setText(Gender);
        experience.setText(Experience + " year");
        progressBar.setVisibility(View.GONE);
    }
    private void Info() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(DoctorsProfile.this, R.style.AlertDialog);
        builder.setTitle("Note");
        builder.setCancelable(false);

        final TextView groupNameField = new TextView(DoctorsProfile.this);
        groupNameField.setText("1) The details mentioned are in the following order \n\n--> Your Name --> Designation \n--> Mobile Number --> Email --> Age --> Gender --> Experience \n\n2) You can edit the details (only age, email, phone, experience and designation can be changed)");
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