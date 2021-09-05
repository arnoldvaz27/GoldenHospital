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
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.arnoldvaz27.management.databinding.BedsDataBinding;
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

public class BedsData extends AppCompatActivity {

    BedsDataBinding binding;
    ImageView addBeds;
    private AlertDialog dialogAddBed;
    private String BedItem;
    String BedString,visit_user_id,Status,Floor,Room,Bed;
    private RecyclerView recyclerView;
    private DatabaseReference eventsRef;
    private ProgressBar loadingBar;
    private BottomSheetDialog bottomSheetDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.dark_red));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        binding = DataBindingUtil.setContentView(this,R.layout.beds_data);
        addBeds = binding.addBed;
        recyclerView = binding.RecyclerView;
        loadingBar = binding.progressCircular;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Beds");

        bottomSheetDialog = new BottomSheetDialog(BedsData.this,R.style.BottomSheetTheme);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        VerifyData();

        addBeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialogAddBed == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BedsData.this);
                    View view = LayoutInflater.from(BedsData.this).inflate(
                            R.layout.layout_add_bed, findViewById(R.id.layoutAddBedContainer)
                    );
                    builder.setView(view);

                    dialogAddBed = builder.create();
                    if (dialogAddBed.getWindow() != null) {
                        dialogAddBed.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    }

                    final EditText bedNumber,roomNumber,floor;
                    final TextView status;
                    String[] BedStrings;
                    Spinner BedChoose;
                    bedNumber = view.findViewById(R.id.name);
                    roomNumber = view.findViewById(R.id.roomNumber);
                    floor = view.findViewById(R.id.floor);
                    status = view.findViewById(R.id.status);
                    BedChoose = view.findViewById(R.id.statusChoose);
                    BedStrings = getResources().getStringArray(R.array.BedStatus);
                    bedNumber.setSelection(bedNumber.getText().length());
                    bedNumber.requestFocus();
                    bedNumber.getShowSoftInputOnFocus();
                    ArrayAdapter<String> adapterStream2 = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, BedStrings);
                    adapterStream2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    BedChoose.setAdapter(adapterStream2);
                    BedChoose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            BedChoose.setSelection(i);
                            BedItem = adapterView.getItemAtPosition(i).toString();
                            BedString = BedItem;
                            status.setText(BedString);

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(TextUtils.isEmpty(bedNumber.getText().toString()) ||TextUtils.isEmpty(roomNumber.getText().toString()) ||
                                    TextUtils.isEmpty(floor.getText().toString())){
                                showToast(getApplicationContext(),"Please enter details in all the fields",R.color.red);
                            }
                            else if(status.getText().toString().equals("Choose Status")){
                                showToast(getApplicationContext(),"Status Invalid, Please select the correct status",R.color.red);
                            }
                            else{
                                HashMap<String, Object> service = new HashMap<>();
                                service.put("bedNumber", bedNumber.getText().toString());
                                service.put("roomNumber", roomNumber.getText().toString());
                                service.put("floorNumber", floor.getText().toString());
                                service.put("status", status.getText().toString());

                                String value = new SimpleDateFormat("ddMMyyyyHH:mm:ssa", Locale.getDefault()).format(new Date());
                                eventsRef.child(value).setValue(service).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            bedNumber.setText("");
                                            roomNumber.setText("");
                                            floor.setText("");
                                            status.setText("");
                                            showToast(getApplicationContext(),"Data added",R.color.green);
                                            dialogAddBed.dismiss();
                                        }else{
                                            showToast(getApplicationContext(),"Please try again",R.color.red);
                                        }
                                    }
                                });
                            }
                        }
                    });

                    view.findViewById(R.id.textCancel).setOnClickListener(v1 -> {
                        dialogAddBed.dismiss();

                    });
                }
                dialogAddBed.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                dialogAddBed.show();
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

        FirebaseRecyclerAdapter<Beds, BedDataHolder> adapter =
                new FirebaseRecyclerAdapter<Beds, BedDataHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull BedDataHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull Beds model) {
                        final String userIDs = getRef(position).getKey();

                        assert userIDs != null;
                        eventsRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() || dataSnapshot.hasChildren()) {

                                    Status = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();
                                    Floor = Objects.requireNonNull(dataSnapshot.child("floorNumber").getValue()).toString();
                                    holder.userFloor.setText("Floor Number: "+Floor);
                                    if(Status.equals("Available")){
                                        holder.userStatus.setTextColor(getResources().getColor(R.color.green));
                                    }else{
                                        holder.userStatus.setTextColor(getResources().getColor(R.color.red));
                                    }
                                    holder.userStatus.setText("Status: "+Status);

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
                    public BedDataHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_container_beds, viewGroup, false);

                        return new BedDataHolder(view);
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
                    Status = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();
                    Floor = Objects.requireNonNull(dataSnapshot.child("floorNumber").getValue()).toString();
                    Room = Objects.requireNonNull(dataSnapshot.child("bedNumber").getValue()).toString();
                    Bed = Objects.requireNonNull(dataSnapshot.child("bedNumber").getValue()).toString();
                    BottomSheet();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static class BedDataHolder extends RecyclerView.ViewHolder {
        TextView userFloor, userStatus;
        ImageView userImage;

        public BedDataHolder(@NonNull View itemView) {
            super(itemView);

            userFloor = itemView.findViewById(R.id.textFloorNumber);
            userStatus = itemView.findViewById(R.id.textStatus);
            userImage = itemView.findViewById(R.id.image);
        }
    }
    private void BottomSheet() {

        final View sheetView = LayoutInflater.from(BedsData.this).inflate(R.layout.beds_bottomsheet, findViewById(R.id.layoutMoreOptions));

        final CardView Edit,Discard,Close,Save,Delete;
        final GridLayout editingGrid,viewingGrid;
        final TextView bedNumber,roomNumber,floor,status;
        final LinearLayout statusLayout;
        String[] BedStrings;
        Spinner BedChoose;

        Edit = sheetView.findViewById(R.id.edit);
        Discard = sheetView.findViewById(R.id.discard);
        Close = sheetView.findViewById(R.id.close);
        Save = sheetView.findViewById(R.id.save);
        Delete = sheetView.findViewById(R.id.delete);
        editingGrid = sheetView.findViewById(R.id.editingGrid);
        viewingGrid = sheetView.findViewById(R.id.viewingGrid);
        bedNumber = sheetView.findViewById(R.id.name);
        roomNumber = sheetView.findViewById(R.id.roomNumber);
        floor = sheetView.findViewById(R.id.floor);
        status = sheetView.findViewById(R.id.status);
        statusLayout = sheetView.findViewById(R.id.statusLayout);
        BedChoose = sheetView.findViewById(R.id.statusChoose);

        bedNumber.setText("Bed Number: "+Bed);
        roomNumber.setText("Room Number: "+Room);
        floor.setText("Floor Number: "+Floor);
        status.setText("Status: "+Status);

        BedStrings = getResources().getStringArray(R.array.BedStatus);

        ArrayAdapter<String> adapterStream2 = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, BedStrings);
        adapterStream2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BedChoose.setAdapter(adapterStream2);
        BedChoose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                BedChoose.setSelection(i);
                BedItem = adapterView.getItemAtPosition(i).toString();
                BedString = BedItem;
                status.setText(BedString);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Edit.setOnClickListener(v -> {
            bedNumber.setText(Bed);
            roomNumber.setText(Room);
            floor.setText(Floor);
            status.setText(Status);
            viewingGrid.setVisibility(View.GONE);
            editingGrid.setVisibility(View.VISIBLE);
            statusLayout.setVisibility(View.VISIBLE);
        });
        Close.setOnClickListener(v ->
                bottomSheetDialog.dismiss());
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(bedNumber.getText().toString()) || TextUtils.isEmpty(roomNumber.getText().toString()) ||
                        TextUtils.isEmpty(floor.getText().toString()) || TextUtils.isEmpty(status.getText().toString())) {
                    showToast(getApplicationContext(),"You need to add data in all fields in order to add Bed",R.color.red);
                }else if(status.getText().toString().equals("Choose Status")){
                    showToast(getApplicationContext(),"Status Invalid, Please select the correct status",R.color.red);
                }else{
                    bottomSheetDialog.dismiss();
                    VerifyData();
                    HashMap<String, Object> service = new HashMap<>();
                    service.put("bedNumber", bedNumber.getText().toString());
                    service.put("roomNumber", roomNumber.getText().toString());
                    service.put("floorNumber", floor.getText().toString());
                    service.put("status", status.getText().toString());
                    eventsRef.child(visit_user_id).setValue(service).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showToast(getApplicationContext(),"Data updated",R.color.green);
                            }else{
                                showToast(getApplicationContext(),"Please try again",R.color.red);
                            }
                        }
                    });
                }
            }
        });
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
        Discard.setOnClickListener(v ->
                bottomSheetDialog.dismiss());

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }
}