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

import com.arnoldvaz27.doctors.NurseDoctorsData;
import com.arnoldvaz27.doctors.Nurses;
import com.arnoldvaz27.doctors.databinding.NurseDoctorsDataBinding;
import com.arnoldvaz27.management.databinding.NurseDataBinding;
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

public class NurseData extends AppCompatActivity {

    NurseDataBinding binding;
    private RecyclerView eventsList;
    private DatabaseReference eventsRef;
    private FirebaseAuth mAuth;
    private String currentUserID, search;
    private EditText inputSearch;
    String visit_user_id, userName, userProfile, userDesignation, userPhoneNumber, userEmail, userDateJoined, userAge, userId;
    private BottomSheetDialog bottomSheetDialog;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(com.arnoldvaz27.doctors.R.color.yellow));
        getWindow().setNavigationBarColor(getResources().getColor(com.arnoldvaz27.doctors.R.color.white));
        binding = DataBindingUtil.setContentView(this, R.layout.nurse_data);
        loadingBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Role").child("Nurses");
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        eventsList = binding.RecyclerView;
        inputSearch = binding.inputSearch;
        eventsList.setLayoutManager(new LinearLayoutManager(this));
        bottomSheetDialog = new BottomSheetDialog(NurseData.this, com.arnoldvaz27.doctors.R.style.BottomSheetTheme);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
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

        FirebaseRecyclerOptions<Nurses> options = new FirebaseRecyclerOptions.Builder<Nurses>()
                .setQuery(eventsRef, Nurses.class).build();

        FirebaseRecyclerAdapter<Nurses, NurseHolder> adapter =
                new FirebaseRecyclerAdapter<Nurses, NurseHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull NurseHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull Nurses model) {
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
                                            holder.userImage.setImageResource(com.arnoldvaz27.doctors.R.drawable.male);
                                        } else if (Profile.equals("Female")) {
                                            holder.userImage.setImageResource(com.arnoldvaz27.doctors.R.drawable.female);
                                        } else {
                                            holder.userImage.setImageResource(com.arnoldvaz27.doctors.R.drawable.male);
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
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public NurseHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_container_nurses, viewGroup, false);

                        return new NurseHolder(view);
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

        loadingBar.setTitle("Displaying All The Nurses");
        loadingBar.setMessage("Please wait while we are fetching the details");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.getProgress();
        loadingBar.show();
        FirebaseRecyclerOptions<Nurses> options = new FirebaseRecyclerOptions.Builder<Nurses>()
                .setQuery(eventsRef, Nurses.class).build();

        FirebaseRecyclerAdapter<Nurses, NurseHolder> adapter =
                new FirebaseRecyclerAdapter<Nurses, NurseHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull NurseHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull Nurses model) {
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
                                        holder.userImage.setImageResource(com.arnoldvaz27.doctors.R.drawable.male);
                                    } else if (Profile.equals("Female")) {
                                        holder.userImage.setImageResource(com.arnoldvaz27.doctors.R.drawable.female);
                                    } else {
                                        holder.userImage.setImageResource(com.arnoldvaz27.doctors.R.drawable.male);
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
                    public NurseHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_container_nurses, viewGroup, false);

                        return new NurseHolder(view);
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
                    userDateJoined = Objects.requireNonNull(dataSnapshot.child("dateJoined").getValue()).toString();
                    BottomSheet();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void BottomSheet() {

        final View sheetView = LayoutInflater.from(NurseData.this).inflate(com.arnoldvaz27.doctors.R.layout.nurse_bottomsheet, findViewById(com.arnoldvaz27.doctors.R.id.layoutMoreOptions));

        final CardView Close, Edit;
        final TextView name, gender;
        final EditText number, designation, email, age, dateJoined;

        name = sheetView.findViewById(com.arnoldvaz27.doctors.R.id.nurseName);
        number = sheetView.findViewById(com.arnoldvaz27.doctors.R.id.number);
        designation = sheetView.findViewById(com.arnoldvaz27.doctors.R.id.nurseDesignation);
        email = sheetView.findViewById(com.arnoldvaz27.doctors.R.id.email);
        age = sheetView.findViewById(com.arnoldvaz27.doctors.R.id.age);
        gender = sheetView.findViewById(com.arnoldvaz27.doctors.R.id.gender);
        dateJoined = sheetView.findViewById(com.arnoldvaz27.doctors.R.id.dateJoin);
        Close = sheetView.findViewById(com.arnoldvaz27.doctors.R.id.close);
        Edit = sheetView.findViewById(com.arnoldvaz27.doctors.R.id.edit);

        name.setText(userName);
        number.setText(userPhoneNumber);
        designation.setText(userDesignation);
        email.setText(userEmail);
        age.setText(userAge);
        gender.setText(userProfile);
        dateJoined.setText(userDateJoined);


        Edit.setVisibility(View.INVISIBLE);


        Close.setOnClickListener(v -> bottomSheetDialog.cancel());
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }

    public static class NurseHolder extends RecyclerView.ViewHolder {
        TextView userName, userPosition;
        ImageView userImage;
        LinearLayout linearLayout;

        public NurseHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(com.arnoldvaz27.doctors.R.id.textName);
            userPosition = itemView.findViewById(com.arnoldvaz27.doctors.R.id.textDesignation);
            userImage = itemView.findViewById(com.arnoldvaz27.doctors.R.id.imageProfile);
            linearLayout = itemView.findViewById(com.arnoldvaz27.doctors.R.id.layoutNurses);
        }
    }
}