package com.arnoldvaz27.doctors;

import static com.arnoldvaz27.doctors.CustomToast.showToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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

import com.arnoldvaz27.doctors.databinding.CanteenDoctorDataBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class CanteenDoctorData extends AppCompatActivity {

    CanteenDoctorDataBinding binding;
    private RecyclerView recyclerView;
    private DatabaseReference eventsRef;
    private ProgressBar loadingBar;
    private BottomSheetDialog bottomSheetDialog;
    private AlertDialog dialogAddFood;
    String FoodName,Price,visit_user_id,search;    private EditText inputSearch;
    ImageView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.pink));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        binding = DataBindingUtil.setContentView(this,R.layout.canteen_doctor_data);

        recyclerView = binding.RecyclerView;
        loadingBar = binding.progressCircular;
        info = binding.info;
        inputSearch = binding.inputSearch;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Canteen");
        bottomSheetDialog = new BottomSheetDialog(CanteenDoctorData.this,R.style.BottomSheetTheme);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
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

        FirebaseRecyclerOptions<Canteens> options = new FirebaseRecyclerOptions.Builder<Canteens>()
                .setQuery(eventsRef, Canteens.class).build();

        FirebaseRecyclerAdapter<Canteens, FoodDataHolder> adapter =
                new FirebaseRecyclerAdapter<Canteens, FoodDataHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull FoodDataHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull Canteens model) {
                        final String userIDs = getRef(position).getKey();

                        assert userIDs != null;
                        eventsRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() || dataSnapshot.hasChildren()) {

                                    FoodName = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                                    Price = Objects.requireNonNull(dataSnapshot.child("price").getValue()).toString();

                                    if (FoodName.toLowerCase().contains(search.toLowerCase())) {
                                        holder.userName.setText("Name: " + FoodName);
                                        holder.userPrice.setText("Price: Rs. " + Price + " /-");

                                    } else {
                                        holder.linearLayout.setVisibility(View.GONE);
                                        holder.userPrice.setVisibility(View.GONE);
                                        holder.userName.setVisibility(View.GONE);
                                        holder.userImage.setVisibility(View.GONE);
                                    }

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
                    public FoodDataHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_container_canteen, viewGroup, false);

                        return new FoodDataHolder(view);
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
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

        FirebaseRecyclerOptions<Beds> options = new FirebaseRecyclerOptions.Builder<Beds>()
                .setQuery(eventsRef, Beds.class).build();

        FirebaseRecyclerAdapter<Beds, FoodDataHolder> adapter =
                new FirebaseRecyclerAdapter<Beds, FoodDataHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull FoodDataHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull Beds model) {
                        final String userIDs = getRef(position).getKey();

                        assert userIDs != null;
                        eventsRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() || dataSnapshot.hasChildren()) {

                                    FoodName = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                                    Price = Objects.requireNonNull(dataSnapshot.child("price").getValue()).toString();
                                    holder.userName.setText("Name: "+FoodName);
                                    holder.userPrice.setText("Price: Rs. "+Price+" /-");

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
                    public FoodDataHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_container_canteen, viewGroup, false);

                        return new FoodDataHolder(view);
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    public static class FoodDataHolder extends RecyclerView.ViewHolder {
        TextView userName, userPrice;
        ImageView userImage;
        LinearLayout linearLayout;

        public FoodDataHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.textName);
            userPrice = itemView.findViewById(R.id.textPrice);
            userImage = itemView.findViewById(R.id.image);            linearLayout = itemView.findViewById(R.id.layoutCanteen);

        }
    }
    private void Info() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(CanteenDoctorData.this, R.style.AlertDialog);
        builder.setTitle("Note");
        builder.setCancelable(false);

        final TextView groupNameField = new TextView(CanteenDoctorData.this);
        groupNameField.setText("1) The details mentioned are in the following order \n\n--> Food Name --> Price. \n\n2) You can not edit the price of the food.");
        groupNameField.setPadding(20, 30, 20, 20);
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