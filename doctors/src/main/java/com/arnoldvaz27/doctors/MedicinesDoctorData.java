package com.arnoldvaz27.doctors;

import static com.arnoldvaz27.doctors.CustomToast.showToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arnoldvaz27.doctors.databinding.MedicinesDoctorDataBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class MedicinesDoctorData extends AppCompatActivity {

    MedicinesDoctorDataBinding binding;
    private RecyclerView recyclerView;
    private DatabaseReference eventsRef;
    private ProgressBar loadingBar;
    private BottomSheetDialog bottomSheetDialog;
    private AlertDialog dialogAddMedicines;
    EditText name, stock, stockExpiry, stockArrived;
    ImageView stockExpiryImage, stockArriveImage;
    String Stock, Name, visit_user_id, search;
    ImageView info;
    private EditText inputSearch;
    String StockArrived, StockExpired, inStock, DoseName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.dark_blue));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        binding = DataBindingUtil.setContentView(this, R.layout.medicines_doctor_data);

        recyclerView = binding.RecyclerView;
        loadingBar = binding.progressCircular;
        info = binding.info;
        inputSearch = binding.inputSearch;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Medicines");
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

    private void Info() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MedicinesDoctorData.this, R.style.AlertDialog);
        builder.setTitle("Note");
        builder.setCancelable(false);

        final TextView groupNameField = new TextView(MedicinesDoctorData.this);
        groupNameField.setText("1) The details shown are the Medicine Name, Stock of Medicine, Inventory arrived, Inventory Expiry \n\n2) If the stock of medicine is 0 the stock will be shown in red, if it is between 1 to 26 then it will be shown in yellow and if the stock is above 26 then it will be shown in green");
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

    private void findSpecific() {
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


                                    if (Name.toLowerCase().contains(search.toLowerCase())) {

                                        holder.userName.setText("Name: " + Name);

                                        if (Integer.parseInt(Stock) == 0) {
                                            holder.userStock.setTextColor(getResources().getColor(R.color.red));
                                        } else if (Integer.parseInt(Stock) <= 25 && Integer.parseInt(Stock) > 0) {
                                            holder.userStock.setTextColor(getResources().getColor(R.color.yellow));
                                        } else if (Integer.parseInt(Stock) > 25) {
                                            holder.userStock.setTextColor(getResources().getColor(R.color.green));
                                        }
                                        holder.userStock.setText("Stock: " + Stock);
                                    } else {
                                        holder.layoutMedicine.setVisibility(View.GONE);
                                        holder.userName.setVisibility(View.GONE);
                                        holder.userStock.setVisibility(View.GONE);
                                        holder.userImage.setVisibility(View.GONE);
                                    }

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            visit_user_id = getRef(position).getKey();
                                            getDetails();
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

    private void getDetails() {
        eventsRef.child(visit_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    inStock = Objects.requireNonNull(dataSnapshot.child("stock").getValue()).toString();
                    DoseName = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                    StockArrived = Objects.requireNonNull(dataSnapshot.child("arrived").getValue()).toString();
                    StockExpired = Objects.requireNonNull(dataSnapshot.child("expiry").getValue()).toString();
                    BottomSheet();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void BottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(MedicinesDoctorData.this, R.style.BottomSheetTheme);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        final View sheetView = LayoutInflater.from(MedicinesDoctorData.this).inflate(R.layout.medicine_bottomsheet, findViewById(R.id.layoutMoreOptions));

        final CardView Close, Edit, Discard, Save;
        GridLayout editingGrid, viewingGrid;
        final TextView name, expiry, arrived;
        final EditText stock;

        name = sheetView.findViewById(R.id.name);
        expiry = sheetView.findViewById(R.id.expiry);
        arrived = sheetView.findViewById(R.id.arrived);
        stock = sheetView.findViewById(R.id.stock);
        Close = sheetView.findViewById(R.id.close);
        Edit = sheetView.findViewById(R.id.edit);
        Discard = sheetView.findViewById(R.id.discard);
        Save = sheetView.findViewById(R.id.save);
        viewingGrid = sheetView.findViewById(R.id.viewingGrid);
        editingGrid = sheetView.findViewById(R.id.editingGrid);

        if (Integer.parseInt(inStock) == 0) {
            stock.setTextColor(getResources().getColor(R.color.red));
        } else if (Integer.parseInt(inStock) <= 25 && Integer.parseInt(inStock) > 0) {
            stock.setTextColor(getResources().getColor(R.color.yellow));
        } else if (Integer.parseInt(inStock) > 25) {
            stock.setTextColor(getResources().getColor(R.color.green));
        }

        arrived.setTextColor(getResources().getColor(R.color.green));
        expiry.setTextColor(getResources().getColor(R.color.red));

        name.setText(DoseName);
        stock.setText("In Stock: " + inStock);
        arrived.setText("Inventory Arrived: " + StockArrived);
        expiry.setText("Inventory Expiry: " + StockExpired);

        Edit.setVisibility(View.INVISIBLE);


        Close.setOnClickListener(v -> bottomSheetDialog.cancel());
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }

    private void VerifyData() {
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() || snapshot.hasChildren()) {
                    display();
                } else {
                    showToast(getApplicationContext(), "No Data", R.color.red);
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
                                    holder.userName.setText("Name: " + Name);
                                    holder.userStock.setText("Stock: " + Stock);

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            visit_user_id = getRef(position).getKey();
                                            getDetails();
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
        LinearLayout layoutMedicine;

        public MedicinesDataHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.textName);
            userStock = itemView.findViewById(R.id.textStock);
            userImage = itemView.findViewById(R.id.image);
            layoutMedicine = itemView.findViewById(R.id.layoutMedicine);

        }
    }
}