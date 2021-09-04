package com.arnoldvaz27.management;

import static com.arnoldvaz27.doctors.CustomToast.showToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.arnoldvaz27.management.databinding.BedsDataBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    String BedString;
    private RecyclerView recyclerView;
    private DatabaseReference eventsRef;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.dark_red));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        binding = DataBindingUtil.setContentView(this,R.layout.beds_data);
        addBeds = binding.addBed;
        recyclerView = binding.RecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Beds");

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

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void display() {
        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Displaying All The Beds");
        loadingBar.setMessage("Please wait while we are fetching the details");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.getProgress();
        loadingBar.show();
        FirebaseRecyclerOptions<Beds> options = new FirebaseRecyclerOptions.Builder<Beds>()
                .setQuery(eventsRef, Beds.class).build();

        FirebaseRecyclerAdapter<Beds, BedDataHolder> adapter =
                new FirebaseRecyclerAdapter<Beds, BedDataHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull BedDataHolder holder, int position, @NonNull Beds model) {
                        final String userIDs = getRef(position).getKey();

                        assert userIDs != null;
                        eventsRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() || dataSnapshot.hasChildren()) {

                                    String Status = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();
                                    String Floor = Objects.requireNonNull(dataSnapshot.child("floorNumber").getValue()).toString();
                                    holder.userFloor.setText("Floor Number: "+Floor);
                                    if(Status.equals("Available")){
                                        holder.userStatus.setTextColor(getResources().getColor(R.color.green));
                                    }else{
                                        holder.userStatus.setTextColor(getResources().getColor(R.color.red));
                                    }
                                    holder.userStatus.setText("Status: "+Status);
                                    loadingBar.dismiss();

                                } else {
                                    showToast(getApplicationContext(), "No Data", R.color.red);
                                    loadingBar.hide();
                                }

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
}