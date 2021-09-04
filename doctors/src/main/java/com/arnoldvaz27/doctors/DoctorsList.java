package com.arnoldvaz27.doctors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arnoldvaz27.doctors.databinding.DoctorsListBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.Objects;

public class DoctorsList extends AppCompatActivity {

    DoctorsListBinding binding;
    private RecyclerView eventsList;
    private DatabaseReference eventsRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    String visit_user_id,userName,userProfile,userDesignation,userPhoneNumber,userEmail,userExperience,userAge,userId;
    private BottomSheetDialog bottomSheetDialog;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.dark_green));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        binding = DataBindingUtil.setContentView(this, R.layout.doctors_list);
        loadingBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Role").child("Doctors");
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        eventsList = binding.RecyclerView;
        eventsList.setLayoutManager(new LinearLayoutManager(this));

        loadingBar.setTitle("Displaying All The Doctors");
        loadingBar.setMessage("Please wait while we are fetching the details");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.getProgress();
        loadingBar.show();
        FirebaseRecyclerOptions<Doctors> options = new FirebaseRecyclerOptions.Builder<Doctors>()
                .setQuery(eventsRef,Doctors.class).build();

        FirebaseRecyclerAdapter<Doctors, DoctorsListHolder> adapter =
                new FirebaseRecyclerAdapter<Doctors, DoctorsListHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull DoctorsListHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull Doctors model) {
                        final String userIDs = getRef(position).getKey();

                        assert userIDs != null;
                        eventsRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {

                                    String Name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                                    String Email = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
                                    String Profile = Objects.requireNonNull(dataSnapshot.child("profile").getValue()).toString();

                                    holder.userName.setText(Name);
                                    holder.userPosition.setText(Email);
                                    if(Profile.equals("Male")){
                                        holder.userImage.setImageResource(R.drawable.male);
                                    }else if(Profile.equals("Female")){
                                        holder.userImage.setImageResource(R.drawable.female);
                                    }else{
                                        holder.userImage.setImageResource(R.drawable.male);
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
                    public DoctorsListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_container_doctors,viewGroup,false);

                        return new DoctorsListHolder(view);
                    }
                };

        eventsList.setAdapter(adapter);
        adapter.startListening();
    }

    private void getDetails() {
        eventsRef.child(visit_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    userName = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                    userDesignation = Objects.requireNonNull(dataSnapshot.child("designation").getValue()).toString();
                    userPhoneNumber = Objects.requireNonNull(dataSnapshot.child("phoneNumber").getValue()).toString();
                    userEmail = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
                    userProfile = Objects.requireNonNull(dataSnapshot.child("profile").getValue()).toString();
                    userAge = Objects.requireNonNull(dataSnapshot.child("age").getValue()).toString();
                    userId = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();
                    userExperience = Objects.requireNonNull(dataSnapshot.child("experience").getValue()).toString();
                    BottomSheet();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void BottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(DoctorsList.this,R.style.BottomSheetTheme);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        final View sheetView = LayoutInflater.from(DoctorsList.this).inflate(R.layout.doctor_bottomsheet, findViewById(R.id.layoutMoreOptions));

        final CardView Close,Edit;
        final TextView name,gender;
        final EditText number,designation,email,age,experience;

        name = sheetView.findViewById(R.id.name);
        number = sheetView.findViewById(R.id.number);
        designation = sheetView.findViewById(R.id.doctorDesignation);
        email = sheetView.findViewById(R.id.floor);
        age = sheetView.findViewById(R.id.age);
        gender = sheetView.findViewById(R.id.gender);
        experience = sheetView.findViewById(R.id.experience);
        Close = sheetView.findViewById(R.id.close);
        Edit = sheetView.findViewById(R.id.edit);

        name.setText(userName);
        number.setText(userPhoneNumber);
        designation.setText(userDesignation);
        email.setText(userEmail);
        age.setText(userAge);
        gender.setText(userProfile);
        experience.setText(userExperience);

        if(userId.equals(currentUserID)){
            Edit.setVisibility(View.VISIBLE);
        }else{
            Edit.setVisibility(View.INVISIBLE);
        }

        Edit.setOnClickListener(v -> {
            bottomSheetDialog.cancel();
            startActivity(new Intent(getApplicationContext(),DoctorsProfile.class));
            finish();
        });
        Close.setOnClickListener(v -> bottomSheetDialog.cancel());
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }

    public static class DoctorsListHolder extends  RecyclerView.ViewHolder
    {
        TextView userName,userPosition;
        ImageView userImage;
        public DoctorsListHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.textName);
            userPosition = itemView.findViewById(R.id.textDesignation);
            userImage= itemView.findViewById(R.id.imageProfile);
        }
    }
}