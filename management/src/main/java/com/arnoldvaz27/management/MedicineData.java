package com.arnoldvaz27.management;

import static com.arnoldvaz27.doctors.CustomToast.showToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arnoldvaz27.doctors.Medicines;
import com.arnoldvaz27.management.databinding.MedicineDataBinding;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class MedicineData extends AppCompatActivity {

    MedicineDataBinding binding;
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
        binding = DataBindingUtil.setContentView(this,R.layout.medicine_data);

        ImageView addMedicine = binding.addMedicines;
        recyclerView = binding.RecyclerView;
        loadingBar = binding.progressCircular;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Medicines");
        bottomSheetDialog = new BottomSheetDialog(MedicineData.this,R.style.BottomSheetTheme);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        VerifyData();
        addMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialogAddMedicines == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MedicineData.this);
                    View view = LayoutInflater.from(MedicineData.this).inflate(
                            R.layout.layout_add_medicines, findViewById(R.id.layoutAddMedicinesContainer)
                    );
                    builder.setView(view);

                    dialogAddMedicines = builder.create();
                    if (dialogAddMedicines.getWindow() != null) {
                        dialogAddMedicines.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    }


                    name = view.findViewById(R.id.inputName);
                    stock = view.findViewById(R.id.stock);
                    stockExpiry = view.findViewById(R.id.stockExpiry);
                    stockArrived = view.findViewById(R.id.stockArrive);
                    stockExpiryImage = view.findViewById(R.id.stockExpiryImage);
                    stockArriveImage = view.findViewById(R.id.stockArriveImage);

                    name.setSelection(name.getText().length());
                    name.requestFocus();

                    stockExpiryImage.setOnClickListener(v12 -> Click(stockExpiryImage));
                    stockArriveImage.setOnClickListener(v13 -> Click(stockArriveImage));


                    view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(stock.getText().toString()) ||
                                    TextUtils.isEmpty(stockExpiry.getText().toString()) || TextUtils.isEmpty(stockArrived.getText().toString())) {
                                showToast(getApplicationContext(),"Please enter details in all fields",R.color.red);
                            } else {
                                HashMap<String, Object> service = new HashMap<>();
                                service.put("name", name.getText().toString());
                                service.put("stock", stock.getText().toString());
                                service.put("arrived", stockArrived.getText().toString());
                                service.put("expiry", stockExpiry.getText().toString());

                                String value = new SimpleDateFormat("ddMMyyyyHH:mm:ssa", Locale.getDefault()).format(new Date());
                                eventsRef.child(value).setValue(service).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            name.setText("");
                                            stock.setText("");
                                            stockExpiry.setText("");
                                            stockArrived.setText("");
                                            showToast(getApplicationContext(),"Data added",R.color.green);
                                            dialogAddMedicines.dismiss();
                                        }else{
                                            showToast(getApplicationContext(),"Please try again",R.color.red);
                                        }
                                    }
                                });
                            }
                        }
                    });

                    view.findViewById(R.id.textCancel).setOnClickListener(v1 -> {
                        dialogAddMedicines.dismiss();

                    });
                }
                dialogAddMedicines.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                dialogAddMedicines.show();
            }
        });
    }
    public void Click(View v) {

        if (v == stockExpiryImage) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);


            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) -> stockExpiry.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year), mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == stockArriveImage) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);


            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) -> stockArrived.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year), mYear, mMonth, mDay);
            datePickerDialog.show();
        }

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