package com.arnoldvaz27.doctors;

import static com.arnoldvaz27.doctors.CustomToast.showToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arnoldvaz27.doctors.databinding.MedicinesDoctorDataBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MedicinesDoctorData extends AppCompatActivity {

    MedicinesDoctorDataBinding binding;
    private RecyclerView recyclerView;
    private DatabaseReference eventsRef;
    private ProgressBar loadingBar;
    private BottomSheetDialog bottomSheetDialog;
    private AlertDialog dialogAddMedicines;
    EditText name, stock, stockExpiry, stockArrived;
    ImageView stockExpiryImage,stockArriveImage;
    String Stock,Name,visit_user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.dark_blue));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        binding = DataBindingUtil.setContentView(this,R.layout.medicines_doctor_data);

        recyclerView = binding.RecyclerView;
        loadingBar = binding.progressCircular;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Medicines");
        bottomSheetDialog = new BottomSheetDialog(MedicinesDoctorData.this,R.style.BottomSheetTheme);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        VerifyData();
    }

    private void VerifyData() {
        bottomSheetDialog.dismiss();
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()  || snapshot.hasChildren())
                {
                    display();
                }
                else
                {
                    showToast(getApplicationContext(),"No Data",R.color.red);
                    loadingBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void display() {

        FirebaseRecyclerOptions<Medicines> options = new FirebaseRecyclerOptions.Builder<Medicines>()
                .setQuery(eventsRef, Medicines.class).build();

        FirebaseRecyclerAdapter<Medicines, MedicinesDataHolder> adapter =
                new FirebaseRecyclerAdapter<Medicines, MedicinesDataHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull MedicinesDataHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull Medicines model) {
                        final String userIDs = getRef(position).getKey();

                        assert userIDs != null;
                        eventsRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() || dataSnapshot.hasChildren()) {

                                    Name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                                    Stock = Objects.requireNonNull(dataSnapshot.child("stock").getValue()).toString();
                                    holder.userName.setText("Name: "+Name);
                                    holder.userStock.setText("Stock: "+Stock);

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            visit_user_id = getRef(position).getKey();
                                        }
                                    });

                                } else {
                                    showToast(getApplicationContext(), "No Data", R.color.red);
                                }
                                loadingBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public MedicinesDataHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_container_medicines, viewGroup, false);

                        return new MedicinesDataHolder(view);
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    public static class MedicinesDataHolder extends RecyclerView.ViewHolder {
        TextView userName, userStock;
        ImageView userImage;

        public MedicinesDataHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.textName);
            userStock = itemView.findViewById(R.id.textStock);
            userImage = itemView.findViewById(R.id.image);
        }
    }
}