package com.arnoldvaz27.nurses;

import static com.arnoldvaz27.nurses.PatientNurseData.appointed;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arnoldvaz27.doctors.Doctors;
import com.arnoldvaz27.doctors.DoctorsProfile;
import com.arnoldvaz27.doctors.databinding.DoctorsListBinding;
import com.arnoldvaz27.nurses.databinding.DoctorsNurseListBinding;
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

public class DoctorsNurseList extends AppCompatActivity {

    DoctorsNurseListBinding binding;
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
        getWindow().setStatusBarColor(getResources().getColor(com.arnoldvaz27.doctors.R.color.dark_green));
        getWindow().setNavigationBarColor(getResources().getColor(com.arnoldvaz27.doctors.R.color.white));
        binding = DataBindingUtil.setContentView(this, R.layout.doctors_nurse_list);
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
                                        holder.userImage.setImageResource(com.arnoldvaz27.doctors.R.drawable.male);
                                    }else if(Profile.equals("Female")){
                                        holder.userImage.setImageResource(com.arnoldvaz27.doctors.R.drawable.female);
                                    }else{
                                        holder.userImage.setImageResource(com.arnoldvaz27.doctors.R.drawable.male);
                                    }

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            appointed.setText(Name);
                                            finish();

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
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(com.arnoldvaz27.doctors.R.layout.item_container_doctors,viewGroup,false);

                        return new DoctorsListHolder(view);
                    }
                };

        eventsList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class DoctorsListHolder extends  RecyclerView.ViewHolder
    {
        TextView userName,userPosition;
        ImageView userImage;
        public DoctorsListHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(com.arnoldvaz27.doctors.R.id.textName);
            userPosition = itemView.findViewById(com.arnoldvaz27.doctors.R.id.textDesignation);
            userImage= itemView.findViewById(com.arnoldvaz27.doctors.R.id.imageProfile);
        }
    }
}