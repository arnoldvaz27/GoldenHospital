package com.arnoldvaz27.doctors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arnoldvaz27.doctors.databinding.PatientDoctorDataBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class PatientDoctorData extends AppCompatActivity {

    PatientDoctorDataBinding binding;
    private RecyclerView recyclerView;
    private DatabaseReference eventsRef;
    private FirebaseAuth mAuth;
    private String currentUserID,search;
    private ProgressDialog loadingBar;
    private BottomSheetDialog bottomSheetDialog;
    String visit_user_id,userName,userProfile,userDisease,userPhoneNumber,userEmail,userAppointed,userStatus,userAge,userId;
    private AlertDialog dialogAddFood;
    String FoodName,Price;    ImageView info;
    private EditText inputSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.teal_700));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        binding = DataBindingUtil.setContentView(this, R.layout.patient_doctor_data);

        loadingBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Role").child("Patients");
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        recyclerView = binding.RecyclerView;        info = binding.info;
        inputSearch = binding.inputSearch;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
VerifyData();
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Info();
            }
        });
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("")){
                    search = s.toString();
                    findSpecific();
                }else{
                    VerifyData();
                }
            }
        });
    }

    private void findSpecific() {

        FirebaseRecyclerOptions<Patient> options = new FirebaseRecyclerOptions.Builder<Patient>()
                .setQuery(eventsRef,Patient.class).build();

        FirebaseRecyclerAdapter<Patient, PatientHolder> adapter =
                new FirebaseRecyclerAdapter<Patient, PatientHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull PatientHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull Patient model) {
                        final String userIDs = getRef(position).getKey();

                        assert userIDs != null;
                        eventsRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {

                                    String Name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                                    String Email = Objects.requireNonNull(dataSnapshot.child("doctorAppointed").getValue()).toString();
                                    String Profile = Objects.requireNonNull(dataSnapshot.child("profile").getValue()).toString();


                                    if(Name.toLowerCase().contains(search.toLowerCase())){
                                        holder.userName.setText(Name);

                                        if (Email.equals("None")) {
                                            holder.userPosition.setTextColor(getResources().getColor(com.arnoldvaz27.doctors.R.color.red));
                                            holder.userPosition.setText("Doctor Appointed: "+Email);
                                        } else {
                                            holder.userPosition.setTextColor(getResources().getColor(com.arnoldvaz27.doctors.R.color.green));
                                            holder.userPosition.setText(Email);
                                        }

                                        if(Profile.equals("Male")){
                                            holder.userImage.setImageResource(com.arnoldvaz27.doctors.R.drawable.man);
                                        }else if(Profile.equals("Female")){
                                            holder.userImage.setImageResource(com.arnoldvaz27.doctors.R.drawable.woman);
                                        }else{
                                            holder.userImage.setImageResource(com.arnoldvaz27.doctors.R.drawable.man);
                                        }
                                    }else{
                                        holder.layoutPatient.setVisibility(View.GONE);
                                        holder.userName.setVisibility(View.GONE);
                                        holder.userPosition.setVisibility(View.GONE);
                                        holder.userImage.setVisibility(View.GONE);
                                    }


                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            visit_user_id = getRef(position).getKey();
                                            getDetails();
                                        }
                                    });
                                    loadingBar.dismiss();

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public PatientHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(com.arnoldvaz27.doctors.R.layout.item_container_patient,viewGroup,false);

                        return new PatientHolder(view);
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void VerifyData() {

        loadingBar.setTitle("Displaying All The Patients");
        loadingBar.setMessage("Please wait while we are fetching the details");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.getProgress();
        loadingBar.show();
        FirebaseRecyclerOptions<Patient> options = new FirebaseRecyclerOptions.Builder<Patient>()
                .setQuery(eventsRef,Patient.class).build();

        FirebaseRecyclerAdapter<Patient, PatientHolder> adapter =
                new FirebaseRecyclerAdapter<Patient, PatientHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull PatientHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull Patient model) {
                        final String userIDs = getRef(position).getKey();

                        assert userIDs != null;
                        eventsRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {

                                    String Name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                                    String Email = Objects.requireNonNull(dataSnapshot.child("doctorAppointed").getValue()).toString();
                                    String Profile = Objects.requireNonNull(dataSnapshot.child("profile").getValue()).toString();

                                    holder.userName.setText(Name);

                                    if (Email.equals("None")) {
                                        holder.userPosition.setTextColor(getResources().getColor(com.arnoldvaz27.doctors.R.color.red));
                                    } else {
                                        holder.userPosition.setTextColor(getResources().getColor(com.arnoldvaz27.doctors.R.color.green));
                                    }
                                    holder.userPosition.setText("Doctor Appointed: "+Email);
                                    if(Profile.equals("Male")){
                                        holder.userImage.setImageResource(R.drawable.man);
                                    }else if(Profile.equals("Female")){
                                        holder.userImage.setImageResource(R.drawable.woman);
                                    }else{
                                        holder.userImage.setImageResource(R.drawable.man);
                                    }

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            visit_user_id = getRef(position).getKey();
                                            getDetails();
                                        }
                                    });
                                    loadingBar.dismiss();

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public PatientHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_container_patient,viewGroup,false);

                        return new PatientHolder(view);
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    public static class PatientHolder extends  RecyclerView.ViewHolder
    {
        TextView userName,userPosition;
        ImageView userImage;        LinearLayout layoutPatient;

        public PatientHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.textName);
            userPosition = itemView.findViewById(R.id.textDesignation);
            userImage= itemView.findViewById(R.id.imageProfile);            layoutPatient = itemView.findViewById(com.arnoldvaz27.doctors.R.id.layoutPatient);

        }
    }

    private void getDetails() {
        eventsRef.child(visit_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    userName = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                    userPhoneNumber = Objects.requireNonNull(dataSnapshot.child("phoneNumber").getValue()).toString();
                    userEmail = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
                    userDisease = Objects.requireNonNull(dataSnapshot.child("disease").getValue()).toString();
                    userProfile = Objects.requireNonNull(dataSnapshot.child("profile").getValue()).toString();
                    userAge = Objects.requireNonNull(dataSnapshot.child("age").getValue()).toString();
                    userStatus = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();
                    userAppointed = Objects.requireNonNull(dataSnapshot.child("doctorAppointed").getValue()).toString();
                    userId = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();
                    BottomSheet();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void BottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(PatientDoctorData.this,R.style.BottomSheetTheme);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        final View sheetView = LayoutInflater.from(PatientDoctorData.this).inflate(R.layout.patient_bottomsheet, findViewById(R.id.layoutMoreOptions));

        final CardView Close,Edit;
        final TextView name,gender,disease,status,appointed;
        final EditText number,designation,email,age,dateJoined;

        name = sheetView.findViewById(R.id.Name);
        number = sheetView.findViewById(R.id.number);
        disease = sheetView.findViewById(R.id.disease);
        status = sheetView.findViewById(R.id.status);
        appointed = sheetView.findViewById(R.id.doctorAppointed);
        email = sheetView.findViewById(R.id.email);
        age = sheetView.findViewById(R.id.age);
        gender = sheetView.findViewById(R.id.gender);
        dateJoined = sheetView.findViewById(R.id.dateJoin);
        Close = sheetView.findViewById(R.id.close);
        Edit = sheetView.findViewById(R.id.edit);

        switch (userStatus) {
            case "Not Available":
                status.setTextColor(getResources().getColor(R.color.red));
                break;
            case "Cured":
                status.setTextColor(getResources().getColor(R.color.green));
                break;
            case "Under Treatment":
                status.setTextColor(getResources().getColor(R.color.yellow));
                break;
        }
        if (userAppointed.equals("None")) {
            appointed.setTextColor(getResources().getColor(R.color.red));
        } else {
            appointed.setTextColor(getResources().getColor(R.color.green));
        }
        name.setText(userName);
        number.setText(userPhoneNumber);
        email.setText(userEmail);
        disease.setText(userDisease);
        age.setText(userAge);
        gender.setText(userProfile);
        status.setText(userStatus);
        appointed.setText(userAppointed);

        Edit.setVisibility(View.INVISIBLE);

        Close.setOnClickListener(v -> bottomSheetDialog.cancel());
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }

    private void Info() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(PatientDoctorData.this, R.style.AlertDialog);
        builder.setTitle("Note");
        builder.setCancelable(false);

        final TextView groupNameField = new TextView(PatientDoctorData.this);
        groupNameField.setText("1) The details mentioned are in the following order \n\n--> Name --> Disease Type \n--> Mobile Number --> Email --> Age --> Gender --> Treatment Status \n--> Doctor Appointed");
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