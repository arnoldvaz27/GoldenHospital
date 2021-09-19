package com.arnoldvaz27.management;

import static com.arnoldvaz27.doctors.CustomToast.showToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arnoldvaz27.management.databinding.DoctorsDataBinding;
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

public class DoctorsData extends AppCompatActivity {

    DoctorsDataBinding binding;
    private RecyclerView eventsList;
    private DatabaseReference eventsRef;
    private FirebaseAuth mAuth;
    private String currentUserID, search;
    private EditText inputSearch;

    String visit_user_id, userName, userProfile, userDesignation, userPhoneNumber, userEmail, userExperience, userAge, userId;
    private BottomSheetDialog bottomSheetDialog;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.dark_green));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        binding = DataBindingUtil.setContentView(this, R.layout.doctors_data);
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Role").child("Doctors");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        eventsList = binding.RecyclerView;
        inputSearch = binding.inputSearch;

        eventsList.setLayoutManager(new LinearLayoutManager(this));
        bottomSheetDialog = new BottomSheetDialog(DoctorsData.this, R.style.BottomSheetTheme);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        VerifyData();
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    search = s.toString();
                    findSpecific();
                } else {
                    VerifyData();
                }
            }
        });

    }

    private void findSpecific() {
        FirebaseRecyclerOptions<Doctors> options = new FirebaseRecyclerOptions.Builder<Doctors>()
                .setQuery(eventsRef, Doctors.class).build();

        FirebaseRecyclerAdapter<Doctors, DoctorsDataHolder> adapter =
                new FirebaseRecyclerAdapter<Doctors, DoctorsDataHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull DoctorsDataHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull Doctors model) {
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

                                    if (Name.toLowerCase().contains(search.toLowerCase())) {
                                        holder.userName.setText(Name);
                                        holder.userPosition.setText(Email);
                                        if (Profile.equals("Male")) {
                                            holder.userImage.setImageResource(R.drawable.male);
                                        } else if (Profile.equals("Female")) {
                                            holder.userImage.setImageResource(R.drawable.female);
                                        } else {
                                            holder.userImage.setImageResource(R.drawable.male);
                                        }
                                    } else {
                                        holder.linearLayout.setVisibility(View.GONE);
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

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public DoctorsDataHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_container_doctors, viewGroup, false);

                        return new DoctorsDataHolder(view);
                    }
                };

        eventsList.setAdapter(adapter);
        adapter.startListening();
    }

    private void VerifyData() {
        bottomSheetDialog.dismiss();
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() || snapshot.hasChildren()) {
                    display();
                } else {
                    showToast(getApplicationContext(), "No Data", R.color.red);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void display() {
        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Displaying All The Doctors");
        loadingBar.setMessage("Please wait while we are fetching the details");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.getProgress();
        loadingBar.show();
        FirebaseRecyclerOptions<Doctors> options = new FirebaseRecyclerOptions.Builder<Doctors>()
                .setQuery(eventsRef, Doctors.class).build();

        FirebaseRecyclerAdapter<Doctors, DoctorsDataHolder> adapter =
                new FirebaseRecyclerAdapter<Doctors, DoctorsDataHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull DoctorsDataHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull Doctors model) {
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
                                    if (Profile.equals("Male")) {
                                        holder.userImage.setImageResource(R.drawable.male);
                                    } else if (Profile.equals("Female")) {
                                        holder.userImage.setImageResource(R.drawable.female);
                                    } else {
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
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public DoctorsDataHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_container_doctors, viewGroup, false);

                        return new DoctorsDataHolder(view);
                    }
                };

        eventsList.setAdapter(adapter);
        adapter.startListening();
    }

    private void getDetails() {
        eventsRef.child(visit_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
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

        final View sheetView = LayoutInflater.from(DoctorsData.this).inflate(R.layout.doctors_bottomsheet, findViewById(R.id.layoutMoreOptions));

        final TextView name, gender;
        final EditText number, designation, email, age, experience;

        name = sheetView.findViewById(R.id.name);
        number = sheetView.findViewById(R.id.number);
        designation = sheetView.findViewById(R.id.doctorDesignation);
        email = sheetView.findViewById(R.id.floor);
        age = sheetView.findViewById(R.id.age);
        gender = sheetView.findViewById(R.id.gender);
        experience = sheetView.findViewById(R.id.experience);

        name.setText(userName);
        number.setText(userPhoneNumber);
        designation.setText(userDesignation);
        email.setText(userEmail);
        age.setText(userAge);
        gender.setText(userProfile);
        experience.setText(userExperience);

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }

    public static class DoctorsDataHolder extends RecyclerView.ViewHolder {
        TextView userName, userPosition;
        ImageView userImage;
        LinearLayout linearLayout;

        public DoctorsDataHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.textName);
            userPosition = itemView.findViewById(R.id.textDesignation);
            userImage = itemView.findViewById(R.id.imageProfile);
            linearLayout = itemView.findViewById(R.id.layoutDoctors);
        }
    }
}