package com.arnoldvaz27.management;

import static com.arnoldvaz27.doctors.CustomToast.showToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arnoldvaz27.doctors.Beds;
import com.arnoldvaz27.management.databinding.CanteenDataBinding;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class CanteenData extends AppCompatActivity {

    CanteenDataBinding binding;
    private RecyclerView recyclerView;
    private DatabaseReference eventsRef;
    private ProgressBar loadingBar;
    private BottomSheetDialog bottomSheetDialog;
    private AlertDialog dialogAddFood;
    String FoodName,Price,visit_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.pink));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        binding = DataBindingUtil.setContentView(this,R.layout.canteen_data);

        ImageView addFood = binding.addFood;
        recyclerView = binding.RecyclerView;
        loadingBar = binding.progressCircular;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Canteen");
        bottomSheetDialog = new BottomSheetDialog(CanteenData.this,R.style.BottomSheetTheme);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        VerifyData();
        addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialogAddFood == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CanteenData.this);
                    View view = LayoutInflater.from(CanteenData.this).inflate(
                            R.layout.layout_add_canteen, findViewById(R.id.layoutAddFoodContainer)
                    );
                    builder.setView(view);

                    dialogAddFood = builder.create();
                    if (dialogAddFood.getWindow() != null) {
                        dialogAddFood.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    }

                    final EditText foodName,price;
                    foodName = view.findViewById(R.id.name);
                    price = view.findViewById(R.id.price);

                    foodName.setSelection(foodName.getText().length());
                    foodName.requestFocus();
                    foodName.getShowSoftInputOnFocus();

                    view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(TextUtils.isEmpty(foodName.getText().toString()) ||TextUtils.isEmpty(price.getText().toString())
                            ){
                                showToast(getApplicationContext(),"Please enter details in all the fields",R.color.red);
                            }
                            else{
                                HashMap<String, Object> service = new HashMap<>();
                                service.put("name", foodName.getText().toString());
                                service.put("price", price.getText().toString());

                                String value = new SimpleDateFormat("ddMMyyyyHH:mm:ssa", Locale.getDefault()).format(new Date());
                                eventsRef.child(value).setValue(service).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            foodName.setText("");
                                            price.setText("");
                                            showToast(getApplicationContext(),"Data added",R.color.green);
                                            dialogAddFood.dismiss();
                                        }else{
                                            showToast(getApplicationContext(),"Please try again",R.color.red);
                                        }
                                    }
                                });
                            }
                        }
                    });

                    view.findViewById(R.id.textCancel).setOnClickListener(v1 -> {
                        dialogAddFood.dismiss();

                    });
                }
                dialogAddFood.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                dialogAddFood.show();
            }
        });
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
                    public FoodDataHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_container_canteen, viewGroup, false);

                        return new FoodDataHolder(view);
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    private void getDetails() {
        eventsRef.child(visit_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    FoodName = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                    Price = Objects.requireNonNull(dataSnapshot.child("price").getValue()).toString();
                    BottomSheet();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public static class FoodDataHolder extends RecyclerView.ViewHolder {
        TextView userName, userPrice;
        ImageView userImage;

        public FoodDataHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.textName);
            userPrice = itemView.findViewById(R.id.textPrice);
            userImage = itemView.findViewById(R.id.image);
        }
    }
    private void BottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(CanteenData.this,R.style.BottomSheetTheme);
        bottomSheetDialog.setCanceledOnTouchOutside(false);

        final View sheetView = LayoutInflater.from(CanteenData.this).inflate(R.layout.canteen_bottomsheet, findViewById(R.id.layoutMoreOptions));

        final CardView Close,Delete,Edit,Save,Discard;
        final GridLayout editingGrid,viewingGrid;
        final TextView name;
        final EditText price;
        name = sheetView.findViewById(R.id.name);
        price = sheetView.findViewById(R.id.price);
        Close = sheetView.findViewById(R.id.close);
        Delete = sheetView.findViewById(R.id.delete);
        Edit = sheetView.findViewById(R.id.edit);
        Save = sheetView.findViewById(R.id.save);
        Discard = sheetView.findViewById(R.id.discard);
        editingGrid = sheetView.findViewById(R.id.editingGrid);
        viewingGrid = sheetView.findViewById(R.id.viewingGrid);

        name.setText("Food Name: "+ FoodName);
        price.setText("Price: Rs. "+ Price+" /-");

        Edit.setOnClickListener(v -> {
            name.setText(FoodName);
            price.setText(Price);
            viewingGrid.setVisibility(View.GONE);
            editingGrid.setVisibility(View.VISIBLE);
            price.setEnabled(true);
            price.setSelection(price.getText().length());
            price.requestFocus();
            price.getShowSoftInputOnFocus();
        });
        Close.setOnClickListener(v -> bottomSheetDialog.cancel());
        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                VerifyData();
                eventsRef.child(visit_user_id).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showToast(getApplicationContext(),"Data deleted",R.color.green);
                        }else{
                            showToast(getApplicationContext(),"Please try again",R.color.red);
                        }
                    }
                });
            }
        });
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(price.getText().toString())) {
                    showToast(getApplicationContext(),"You need to add data in all fields in order to add the doctor",R.color.red);
                }else {
                    bottomSheetDialog.dismiss();
                    VerifyData();
                    HashMap<String, Object> service = new HashMap<>();
                    service.put("name", name.getText().toString());
                    service.put("price", price.getText().toString());

                    eventsRef.child(visit_user_id).setValue(service).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showToast(getApplicationContext(),"Data added",R.color.green);
                                bottomSheetDialog.dismiss();
                            }else{
                                showToast(getApplicationContext(),"Please try again",R.color.red);
                            }
                        }
                    });
                }


            }

        });
        Discard.setOnClickListener(v -> bottomSheetDialog.cancel());


        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }
}