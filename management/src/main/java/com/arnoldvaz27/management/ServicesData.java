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
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.arnoldvaz27.management.databinding.ServicesDataBinding;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class ServicesData extends AppCompatActivity {

    ServicesDataBinding binding;
    ImageView addService;
    private AlertDialog dialogAddService;
    EditText serviceName, startDate;
    ImageView startDateImage;
    private RecyclerView recyclerView;
    private ProgressDialog loadingBar;
    private DatabaseReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.purple_200));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        binding = DataBindingUtil.setContentView(this, R.layout.services_data);

        addService = binding.addService;
        recyclerView = binding.RecyclerView;
        EditText inputSearch = binding.inputSearch;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Services");

        VerifyData();

        addService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogAddService == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ServicesData.this);
                    View view = LayoutInflater.from(ServicesData.this).inflate(
                            R.layout.layout_add_servicedata, findViewById(R.id.layoutAddAmbulanceContainer)
                    );
                    builder.setView(view);

                    dialogAddService = builder.create();
                    if (dialogAddService.getWindow() != null) {
                        dialogAddService.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    }

                    serviceName = view.findViewById(R.id.serviceName);
                    startDate = view.findViewById(R.id.startDate);
                    startDateImage = view.findViewById(R.id.startDateImage);

                    serviceName.setSelection(serviceName.getText().length());
                    serviceName.requestFocus();
                    serviceName.getShowSoftInputOnFocus();

                    startDateImage.setOnClickListener(v12 -> Click(startDateImage));

                    view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (TextUtils.isEmpty(serviceName.getText().toString()) || TextUtils.isEmpty(startDate.getText().toString())
                            ) {
                                showToast(getApplicationContext(), "Please enter all the fields", R.color.red);
                            } else {
                                HashMap<String, Object> service = new HashMap<>();
                                service.put("date", startDate.getText().toString());
                                service.put("name", serviceName.getText().toString());

                                String value = new SimpleDateFormat("ddMMyyyyHH:mm:ssa", Locale.getDefault()).format(new Date());
                                eventsRef.child(value).setValue(service).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            serviceName.setText("");
                                            startDate.setText("");
                                            showToast(getApplicationContext(),"Data added",R.color.green);
                                            dialogAddService.dismiss();
                                        }else{
                                            showToast(getApplicationContext(),"Please try again",R.color.red);
                                        }
                                    }
                                });
                            }
                        }
                    });

                    view.findViewById(R.id.textCancel).setOnClickListener(v1 -> {
                        dialogAddService.dismiss();

                    });
                }
                dialogAddService.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                dialogAddService.show();
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
        loadingBar.setTitle("Displaying All The Services");
        loadingBar.setMessage("Please wait while we are fetching the details");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.getProgress();
        loadingBar.show();
        FirebaseRecyclerOptions<Services> options = new FirebaseRecyclerOptions.Builder<Services>()
                .setQuery(eventsRef, Services.class).build();

        FirebaseRecyclerAdapter<Services, ServiceDataHolder> adapter =
                new FirebaseRecyclerAdapter<Services, ServiceDataHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull ServiceDataHolder holder, int position, @NonNull Services model) {
                        final String userIDs = getRef(position).getKey();

                        assert userIDs != null;
                        eventsRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() || dataSnapshot.hasChildren()) {

                                    String Name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                                    String Date = Objects.requireNonNull(dataSnapshot.child("date").getValue()).toString();
                                    holder.userName.setText(Name);
                                    holder.userDate.setText(Date);
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
                    public ServiceDataHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_container_service, viewGroup, false);

                        return new ServiceDataHolder(view);
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public void Click(View v) {

        if (v == startDateImage) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);


            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) -> startDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year), mYear, mMonth, mDay);
            datePickerDialog.show();
        }

    }

    public static class ServiceDataHolder extends RecyclerView.ViewHolder {
        TextView userName, userDate;
        ImageView userImage;

        public ServiceDataHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.textName);
            userDate = itemView.findViewById(R.id.textDate);
            userImage = itemView.findViewById(R.id.image);
        }
    }
}