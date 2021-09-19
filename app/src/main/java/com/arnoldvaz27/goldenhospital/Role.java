package com.arnoldvaz27.goldenhospital;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import com.arnoldvaz27.doctors.CustomToast;
import com.arnoldvaz27.doctors.DoctorsHome;
import com.arnoldvaz27.goldenhospital.databinding.RoleBinding;
import com.arnoldvaz27.management.ManagementHome;
import com.arnoldvaz27.nurses.NurseHome;
import com.arnoldvaz27.patients.PatientHome;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class Role extends AppCompatActivity {

    RoleBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    //main layout
    private CardView staff, patient;
    private LinearLayout selectRole, savingLinear;
    private Button LoginPatient, Register;
    private ProgressBar mainProgress;
    //user details
    private LinearLayout userLinear;
    private EditText Username, Password;
    private TextView PasswordVisible;
    private Button Login, Reset;
    private ProgressBar progressBar;
    private ImageView hidden, visible;
    //doctor details
    private LinearLayout doctorLinear;
    private EditText doctorName, doctorAge, doctorEmail, doctorPhoneNumber, doctorExperience;
    private TextView doctorDesignation, doctorProfile;
    private CardView doctorMale, doctorFemale;
    private Button doctorSave;
    private ProgressBar doctorProgress;
    //management details
    private LinearLayout managementLinear;
    private EditText managementName, managementAge, managementEmail, managementPhoneNumber;
    private TextView managementDesignation, managementDateJoin, managementProfile;
    private ImageView managementSelectDate;
    private CardView managementMale, managementFemale;
    private Button managementSave;
    private ProgressBar managementProgress;
    //nurse details
    private LinearLayout nurseLinear;
    private EditText nurseName, nurseAge, nurseEmail, nursePhoneNumber;
    private ImageView nurseSelectDate;
    private TextView nurseDesignation, nurseProfile, nurseDateJoin;
    private CardView nurseMale, nurseFemale;
    private Button nurseSave;
    private ProgressBar nurseProgress;

    private String currentUserId, role, userType;

    private ValueEventListener group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.golden));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        binding = DataBindingUtil.setContentView(this, R.layout.role);  //binding xml layout to java file

        RootRef = FirebaseDatabase.getInstance().getReference();

        initViews();

        mAuth = FirebaseAuth.getInstance();

        clickListeners();


        Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                PasswordVisible.setText(Password.getText().toString());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void clickListeners() {
        staff.setOnClickListener(v -> {
            userType = "Staff";
            savingLinear.setVisibility(View.GONE);
            selectRole.setVisibility(View.GONE);
            userLinear.setVisibility(View.VISIBLE);
            Username.setSelection(Username.getText().length());
            Username.requestFocus();
            Username.setShowSoftInputOnFocus(true);

        });
        patient.setOnClickListener(v -> savingLinear.setVisibility(View.VISIBLE));

        LoginPatient.setOnClickListener(v -> {
            userType = "PatientLogin";
            selectRole.setVisibility(View.GONE);
            savingLinear.setVisibility(View.GONE);
            userLinear.setVisibility(View.VISIBLE);
            Username.setSelection(Username.getText().length());
            Username.requestFocus();
            Username.setShowSoftInputOnFocus(true);
        });

        Register.setOnClickListener(v -> {
            userType = "PatientRegister";
            selectRole.setVisibility(View.GONE);
            savingLinear.setVisibility(View.GONE);
            userLinear.setVisibility(View.VISIBLE);
            Username.setSelection(Username.getText().length());
            Username.requestFocus();
            Username.setShowSoftInputOnFocus(true);
        });


        hidden.setOnClickListener(v -> {
            PasswordVisible.setVisibility(View.VISIBLE);
            hidden.setVisibility(View.GONE);
            visible.setVisibility(View.VISIBLE);
            Password.setSelection(Password.getText().length());
            Password.requestFocus();
        });

        visible.setOnClickListener(v -> {
            PasswordVisible.setVisibility(View.GONE);
            hidden.setVisibility(View.VISIBLE);
            visible.setVisibility(View.GONE);
            Password.setSelection(Password.getText().length());
            Password.requestFocus();
        });

        Reset.setOnClickListener(v -> {
            Username.setText("");
            Password.setText("");
            PasswordVisible.setText("");
        });
        Login.setOnClickListener(v -> {
            Login.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            mainProgress.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(Username.getText().toString()) || TextUtils.isEmpty(Password.getText().toString())) {
                CustomToast.showToast(getApplicationContext(), "Please enter value in both the fields", R.color.red);
            } else {
                String username = Username.getText().toString();
                String password = Password.getText().toString();
                if (userType.equals("PatientRegister")) {
                    mAuth.createUserWithEmailAndPassword(username, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {

                                    currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                    RootRef.child("Users").child(currentUserId).setValue("");
                                    Toast.makeText(Role.this,
                                            "Account Created Successfully !!",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), Patient.class);
                                    intent.putExtra("userId", currentUserId);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    String message;
                                    message = Objects.requireNonNull(task.getException()).toString();
                                    Toast.makeText(Role.this,
                                            "Error : " + message,
                                            Toast.LENGTH_SHORT).show();
                                }
                                Login.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            });
                } else if (userType.equals("PatientLogin")) {
                    mAuth.signInWithEmailAndPassword(username, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    final SharedPreferences fieldsVisibility1 = getSharedPreferences("Role", MODE_PRIVATE);
                                    final SharedPreferences.Editor fieldsVisibility2 = fieldsVisibility1.edit();
                                    fieldsVisibility2.putString("type", "Patient");
                                    fieldsVisibility2.apply();
                                    currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                    startActivity(new Intent(getApplicationContext(), PatientHome.class));
                                    finishAffinity();
                                } else {
                                    CustomToast.showToast(getApplicationContext(), "Error", R.color.red);
                                }
                                Login.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                mainProgress.setVisibility(View.GONE);
                            });
                } else {
                    mAuth.signInWithEmailAndPassword(username, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                    retrieveDetails();
                                } else {
                                    CustomToast.showToast(getApplicationContext(), "Error", R.color.red);
                                }
                                Login.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                mainProgress.setVisibility(View.GONE);
                            });
                }
            }
        });

        //Selecting Date
        managementSelectDate.setOnClickListener(v -> Click(managementSelectDate));
        nurseSelectDate.setOnClickListener(v -> Click(nurseSelectDate));
        //Selecting Profile Type
        nurseMale.setOnClickListener(v -> nurseProfile.setText("Male"));

        nurseFemale.setOnClickListener(v -> nurseProfile.setText("Female"));

        managementMale.setOnClickListener(v -> managementProfile.setText("Male"));
        managementFemale.setOnClickListener(v -> managementProfile.setText("Female"));

        doctorMale.setOnClickListener(v -> doctorProfile.setText("Male"));
        doctorFemale.setOnClickListener(v -> doctorProfile.setText("Female"));

        //Click Save
        nurseSave.setOnClickListener(v -> {
            String name = nurseName.getText().toString();
            String designation = nurseDesignation.getText().toString();
            String age = nurseAge.getText().toString();
            String email = nurseEmail.getText().toString();
            String phoneNumber = nursePhoneNumber.getText().toString();
            String dateJoining = nurseDateJoin.getText().toString();
            String profile = nurseProfile.getText().toString();
            if (name.equals("") || designation.equals("") || age.equals("") || email.equals("") || phoneNumber.equals("") ||
                    dateJoining.equals("") || profile.equals("")) {
                CustomToast.showToast(getApplicationContext(), "Please enter all the details to Save", R.color.red);
            } else {
                nurseSavingDetails();
            }
        });
        managementSave.setOnClickListener(v -> {
            String name = managementName.getText().toString();
            String designation = managementDesignation.getText().toString();
            String age = managementAge.getText().toString();
            String email = managementEmail.getText().toString();
            String phoneNumber = managementPhoneNumber.getText().toString();
            String dateJoining = managementDateJoin.getText().toString();
            String profile = managementProfile.getText().toString();
            if (name.equals("") || designation.equals("") || age.equals("") || email.equals("") || phoneNumber.equals("") ||
                    dateJoining.equals("") || profile.equals("")) {
                CustomToast.showToast(getApplicationContext(), "Please enter all the details to Save", R.color.red);
            } else {
                managementSavingDetails();
            }
        });
        doctorSave.setOnClickListener(v -> {
            String name = doctorName.getText().toString();
            String designation = doctorDesignation.getText().toString();
            String age = doctorAge.getText().toString();
            String email = doctorEmail.getText().toString();
            String experience = doctorExperience.getText().toString();
            String phoneNumber = doctorPhoneNumber.getText().toString();
            String profile = doctorProfile.getText().toString();
            if (name.equals("") || designation.equals("") || age.equals("") || experience.equals("") || email.equals("") || phoneNumber.equals("") || profile.equals("")) {
                CustomToast.showToast(getApplicationContext(), "Please enter all the details to Save", R.color.red);
            } else {
                doctorSavingDetails();
            }
        });
    }

    private void doctorSavingDetails() {
        mainProgress.setVisibility(View.VISIBLE);
        doctorProgress.setVisibility(View.VISIBLE);
        doctorSave.setVisibility(View.GONE);
        HashMap<String, Object> doctor = new HashMap<>();
        doctor.put("uid", currentUserId);
        doctor.put("name", doctorName.getText().toString());
        doctor.put("designation", doctorDesignation.getText().toString());
        doctor.put("age", doctorAge.getText().toString());
        doctor.put("email", doctorEmail.getText().toString());
        doctor.put("phoneNumber", doctorPhoneNumber.getText().toString());
        doctor.put("experience", doctorExperience.getText().toString());
        doctor.put("profile", doctorProfile.getText().toString());
        RootRef.child("Users").child(currentUserId).setValue(doctor).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap<String, Object> doctor1 = new HashMap<>();
                doctor1.put("uid", currentUserId);
                doctor1.put("name", doctorName.getText().toString());
                doctor1.put("designation", doctorDesignation.getText().toString());
                doctor1.put("age", doctorAge.getText().toString());
                doctor1.put("email", doctorEmail.getText().toString());
                doctor1.put("phoneNumber", doctorPhoneNumber.getText().toString());
                doctor1.put("experience", doctorExperience.getText().toString());
                doctor1.put("profile", doctorProfile.getText().toString());
                RootRef.child("Role").child("Doctors").child(currentUserId).setValue(doctor1).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        mainProgress.setVisibility(View.VISIBLE);
                        doctorProgress.setVisibility(View.VISIBLE);
                        doctorSave.setVisibility(View.GONE);
                        final SharedPreferences fieldsVisibility1 = getSharedPreferences("Role", MODE_PRIVATE);
                        final SharedPreferences.Editor fieldsVisibility2 = fieldsVisibility1.edit();
                        fieldsVisibility2.putString("type", "Doctors");
                        fieldsVisibility2.apply();
                        startActivity(new Intent(getApplicationContext(), DoctorsHome.class));
                        finishAffinity();
                    } else {
                        CustomToast.showToast(getApplicationContext(), "Error", R.color.red);
                        mainProgress.setVisibility(View.GONE);
                        doctorProgress.setVisibility(View.GONE);
                        doctorSave.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                CustomToast.showToast(getApplicationContext(), "Data not saved", R.color.red);
                mainProgress.setVisibility(View.GONE);
                doctorProgress.setVisibility(View.GONE);
                doctorSave.setVisibility(View.VISIBLE);
            }
        });
    }

    private void managementSavingDetails() {
        mainProgress.setVisibility(View.VISIBLE);
        managementProgress.setVisibility(View.VISIBLE);
        managementSave.setVisibility(View.GONE);
        HashMap<String, Object> management = new HashMap<>();
        management.put("uid", currentUserId);
        management.put("name", managementName.getText().toString());
        management.put("designation", managementDesignation.getText().toString());
        management.put("age", managementAge.getText().toString());
        management.put("email", managementEmail.getText().toString());
        management.put("phoneNumber", managementPhoneNumber.getText().toString());
        management.put("dateJoined", managementDateJoin.getText().toString());
        management.put("profile", managementProfile.getText().toString());
        RootRef.child("Users").child(currentUserId).setValue(management).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap<String, Object> management1 = new HashMap<>();
                management1.put("uid", currentUserId);
                management1.put("name", managementName.getText().toString());
                management1.put("designation", managementDesignation.getText().toString());
                management1.put("age", managementAge.getText().toString());
                management1.put("email", managementEmail.getText().toString());
                management1.put("phoneNumber", managementPhoneNumber.getText().toString());
                management1.put("dateJoined", managementDateJoin.getText().toString());
                management1.put("profile", managementProfile.getText().toString());
                RootRef.child("Role").child("Management").child(currentUserId).setValue(management1).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        mainProgress.setVisibility(View.VISIBLE);
                        managementProgress.setVisibility(View.VISIBLE);
                        managementSave.setVisibility(View.GONE);
                        final SharedPreferences fieldsVisibility1 = getSharedPreferences("Role", MODE_PRIVATE);
                        final SharedPreferences.Editor fieldsVisibility2 = fieldsVisibility1.edit();
                        fieldsVisibility2.putString("type", "Management");
                        fieldsVisibility2.apply();
                        startActivity(new Intent(getApplicationContext(), ManagementHome.class));
                        finishAffinity();
                    } else {
                        CustomToast.showToast(getApplicationContext(), "Error", R.color.red);
                        mainProgress.setVisibility(View.GONE);
                        managementProgress.setVisibility(View.GONE);
                        managementSave.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                CustomToast.showToast(getApplicationContext(), "Data not saved", R.color.red);
                mainProgress.setVisibility(View.GONE);
                managementProgress.setVisibility(View.GONE);
                managementSave.setVisibility(View.VISIBLE);
            }
        });
    }

    private void nurseSavingDetails() {
        mainProgress.setVisibility(View.VISIBLE);
        nurseProgress.setVisibility(View.VISIBLE);
        nurseSave.setVisibility(View.GONE);
        HashMap<String, Object> nurse = new HashMap<>();
        nurse.put("uid", currentUserId);
        nurse.put("name", nurseName.getText().toString());
        nurse.put("designation", nurseDesignation.getText().toString());
        nurse.put("age", nurseAge.getText().toString());
        nurse.put("email", nurseEmail.getText().toString());
        nurse.put("phoneNumber", nursePhoneNumber.getText().toString());
        nurse.put("dateJoined", nurseDateJoin.getText().toString());
        nurse.put("profile", nurseProfile.getText().toString());
        RootRef.child("Users").child(currentUserId).setValue(nurse).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap<String, Object> nurse1 = new HashMap<>();
                nurse1.put("uid", currentUserId);
                nurse1.put("name", nurseName.getText().toString());
                nurse1.put("designation", nurseDesignation.getText().toString());
                nurse1.put("age", nurseAge.getText().toString());
                nurse1.put("email", nurseEmail.getText().toString());
                nurse1.put("phoneNumber", nursePhoneNumber.getText().toString());
                nurse1.put("dateJoined", nurseDateJoin.getText().toString());
                nurse1.put("profile", nurseProfile.getText().toString());
                RootRef.child("Role").child("Nurses").child(currentUserId).setValue(nurse1).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        mainProgress.setVisibility(View.VISIBLE);
                        nurseProgress.setVisibility(View.VISIBLE);
                        nurseSave.setVisibility(View.GONE);
                        final SharedPreferences fieldsVisibility1 = getSharedPreferences("Role", MODE_PRIVATE);
                        final SharedPreferences.Editor fieldsVisibility2 = fieldsVisibility1.edit();
                        fieldsVisibility2.putString("type", "Nurse");
                        fieldsVisibility2.apply();
                        startActivity(new Intent(getApplicationContext(), NurseHome.class));
                        finishAffinity();
                    } else {
                        CustomToast.showToast(getApplicationContext(), "Error", R.color.red);
                        mainProgress.setVisibility(View.GONE);
                        nurseProgress.setVisibility(View.GONE);
                        nurseSave.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                CustomToast.showToast(getApplicationContext(), "Data not saved", R.color.red);
                mainProgress.setVisibility(View.GONE);
                nurseProgress.setVisibility(View.GONE);
                nurseSave.setVisibility(View.VISIBLE);
            }
        });
    }

    private void Click(View v) {
        if (v == nurseSelectDate) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);


            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) -> nurseDateJoin.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year), mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == managementSelectDate) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);


            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) -> managementDateJoin.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year), mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    }

    private void retrieveDetails() {
        RootRef.child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("designation").exists()) {
                            role = Objects.requireNonNull(dataSnapshot.child("designation").getValue()).toString();
                            switch (role) {
                                case "Doctor":
                                    group = new ValueEventListener() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.child("name").exists() && dataSnapshot.child("designation").exists()
                                                    && dataSnapshot.child("phoneNumber").exists() && dataSnapshot.child("profile").exists() &&
                                                    dataSnapshot.child("experience").exists() && dataSnapshot.child("email").exists() && dataSnapshot.child("age").exists()) {

                                                doctorDesignation.setText(role);
                                                userLinear.setVisibility(View.GONE);
                                                doctorLinear.setVisibility(View.VISIBLE);
                                                String name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                                                String designation = Objects.requireNonNull(dataSnapshot.child("designation").getValue()).toString();
                                                String phoneNumber = Objects.requireNonNull(dataSnapshot.child("phoneNumber").getValue()).toString();
                                                String profile = Objects.requireNonNull(dataSnapshot.child("profile").getValue()).toString();
                                                String email = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
                                                String experience = Objects.requireNonNull(dataSnapshot.child("experience").getValue()).toString();
                                                String age = Objects.requireNonNull(dataSnapshot.child("age").getValue()).toString();
                                                doctorName.setText(name);
                                                doctorDesignation.setText(designation);
                                                doctorPhoneNumber.setText(phoneNumber);
                                                doctorProfile.setText(profile);
                                                doctorEmail.setText(email);
                                                doctorAge.setText(age);
                                                doctorExperience.setText(experience);
                                            } else {
                                                doctorDesignation.setText(role);
                                                userLinear.setVisibility(View.GONE);
                                                doctorLinear.setVisibility(View.VISIBLE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            CustomToast.showToast(getApplicationContext(), "Error", R.color.red);
                                        }};
                                    RootRef.child("Users").child(currentUserId)
                                            .addValueEventListener(group);
                                    break;
                                case "Nurse":
                                    group = new ValueEventListener() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.child("name").exists() && dataSnapshot.child("designation").exists()
                                                    && dataSnapshot.child("phoneNumber").exists() && dataSnapshot.child("profile").exists() &&
                                                    dataSnapshot.child("dateJoined").exists() && dataSnapshot.child("email").exists() && dataSnapshot.child("age").exists()) {

                                                nurseDesignation.setText(role);
                                                userLinear.setVisibility(View.GONE);
                                                nurseLinear.setVisibility(View.VISIBLE);
                                                RootRef.removeEventListener(group);
                                                String name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                                                String designation = Objects.requireNonNull(dataSnapshot.child("designation").getValue()).toString();
                                                String phoneNumber = Objects.requireNonNull(dataSnapshot.child("phoneNumber").getValue()).toString();
                                                String profile = Objects.requireNonNull(dataSnapshot.child("profile").getValue()).toString();
                                                String email = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
                                                String dateJoined = Objects.requireNonNull(dataSnapshot.child("dateJoined").getValue()).toString();
                                                String age = Objects.requireNonNull(dataSnapshot.child("age").getValue()).toString();
                                                nurseName.setText(name);
                                                nurseDesignation.setText(designation);
                                                nursePhoneNumber.setText(phoneNumber);
                                                nurseProfile.setText(profile);
                                                nurseEmail.setText(email);
                                                nurseAge.setText(age);
                                                nurseDateJoin.setText(dateJoined);
                                            } else {
                                                nurseDesignation.setText(role);
                                                userLinear.setVisibility(View.GONE);
                                                nurseLinear.setVisibility(View.VISIBLE);
                                                RootRef.removeEventListener(group);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            CustomToast.showToast(getApplicationContext(), "Error", R.color.red);
                                        }
                                    };
                                    RootRef.child("Users").child(currentUserId)
                                            .addValueEventListener(group);
                                    break;
                                case "Management":
                                    group = new ValueEventListener() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.child("name").exists() && dataSnapshot.child("designation").exists()
                                                    && dataSnapshot.child("phoneNumber").exists() && dataSnapshot.child("profile").exists() &&
                                                    dataSnapshot.child("dateJoined").exists() && dataSnapshot.child("email").exists() && dataSnapshot.child("age").exists()) {
                                                managementDesignation.setText(role);
                                                userLinear.setVisibility(View.GONE);
                                                managementLinear.setVisibility(View.VISIBLE);
                                                String name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                                                String designation = Objects.requireNonNull(dataSnapshot.child("designation").getValue()).toString();
                                                String phoneNumber = Objects.requireNonNull(dataSnapshot.child("phoneNumber").getValue()).toString();
                                                String profile = Objects.requireNonNull(dataSnapshot.child("profile").getValue()).toString();
                                                String email = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
                                                String dateJoined = Objects.requireNonNull(dataSnapshot.child("dateJoined").getValue()).toString();
                                                String age = Objects.requireNonNull(dataSnapshot.child("age").getValue()).toString();
                                                managementName.setText(name);
                                                managementDesignation.setText(designation);
                                                managementPhoneNumber.setText(phoneNumber);
                                                managementProfile.setText(profile);
                                                managementEmail.setText(email);
                                                managementAge.setText(age);
                                                managementDateJoin.setText(dateJoined);
                                            } else {
                                                managementDesignation.setText(role);
                                                userLinear.setVisibility(View.GONE);
                                                managementLinear.setVisibility(View.VISIBLE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            CustomToast.showToast(getApplicationContext(), "Error", R.color.red);
                                        }
                                    };
                                    RootRef.child("Users").child(currentUserId)
                                            .addValueEventListener(group);
                                    break;
                            }
                        } else {
                            CustomToast.showToast(getApplicationContext(), "Error", R.color.red);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void initViews() {
        staff = binding.staff;
        patient = binding.patient;
        mainProgress = binding.progressBar;
        selectRole = binding.selectRole;
        savingLinear = binding.savingLinear;
        LoginPatient = binding.Login;
        Register = binding.Register;
        userLinear = binding.userDetail.userLinear;
        Username = binding.userDetail.username;
        Password = binding.userDetail.password;
        PasswordVisible = binding.userDetail.Visible;
        Login = binding.userDetail.logInButton;
        Reset = binding.userDetail.resetButton;
        progressBar = binding.userDetail.progressCircular;
        hidden = binding.userDetail.hidden;
        visible = binding.userDetail.visible;

        //nurse
        nurseLinear = binding.nurseDetail.nurseLinear;
        nurseName = binding.nurseDetail.name;
        nurseAge = binding.nurseDetail.age;
        nurseEmail = binding.nurseDetail.email;
        nursePhoneNumber = binding.nurseDetail.phoneNumber;
        nurseSelectDate = binding.nurseDetail.dateOfJoiningImage;
        nurseDesignation = binding.nurseDetail.designation;
        nurseProfile = binding.nurseDetail.gender;
        nurseDateJoin = binding.nurseDetail.dateJoining;
        nurseMale = binding.nurseDetail.male;
        nurseFemale = binding.nurseDetail.female;
        nurseSave = binding.nurseDetail.Save;
        nurseProgress = binding.nurseDetail.Progress;

        //management
        managementLinear = binding.managementDetail.managementLinear;
        managementName = binding.managementDetail.name;
        managementAge = binding.managementDetail.age;
        managementEmail = binding.managementDetail.email;
        managementPhoneNumber = binding.managementDetail.phoneNumber;
        managementSelectDate = binding.managementDetail.dateOfJoiningImage;
        managementDesignation = binding.managementDetail.designation;
        managementProfile = binding.managementDetail.profile;
        managementDateJoin = binding.managementDetail.dateJoining;
        managementMale = binding.managementDetail.male;
        managementFemale = binding.managementDetail.female;
        managementSave = binding.managementDetail.Save;
        managementProgress = binding.managementDetail.Progress;

        //doctor
        doctorLinear = binding.doctorDetail.doctorLinear;
        doctorDesignation = binding.doctorDetail.designation;
        doctorName = binding.doctorDetail.name;
        doctorAge = binding.doctorDetail.age;
        doctorEmail = binding.doctorDetail.email;
        doctorPhoneNumber = binding.doctorDetail.phoneNumber;
        doctorExperience = binding.doctorDetail.experience;
        doctorProfile = binding.doctorDetail.profile;
        doctorMale = binding.doctorDetail.male;
        doctorFemale = binding.doctorDetail.female;
        doctorSave = binding.doctorDetail.Save;
        doctorProgress = binding.doctorDetail.Progress;
    }

}