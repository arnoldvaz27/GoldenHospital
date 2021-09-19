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

import com.arnoldvaz27.doctors.databinding.ServiceDoctorDataBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ServiceDoctorData extends AppCompatActivity {

    ServiceDoctorDataBinding binding;
    ImageView addService;
    private AlertDialog dialogAddService;
    EditText serviceName, startDate,inputSearch;
    ImageView startDateImage;
    String search;    ImageView info;

    private RecyclerView recyclerView;
    private DatabaseReference eventsRef;
    private ProgressBar loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.purple_200));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        binding = DataBindingUtil.setContentView(this, R.layout.service_doctor_data);
        recyclerView = binding.RecyclerView;
        inputSearch = binding.inputSearch;
        loadingBar = binding.progressCircular;
        info = binding.info;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Services");

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
                if(!s.toString().equals("")){
                    search = s.toString();
                    findSpecific();
                }else{
                    VerifyData();
                }
            }
        });

    }

    private void findSpecific() {
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
                                    if(Name.toLowerCase().contains(search.toLowerCase())){
                                        holder.userName.setText(Name);
                                        holder.userDate.setText(Date);
                                    }else{
                                        holder.layoutService.setVisibility(View.GONE);
                                        holder.userDate.setVisibility(View.GONE);
                                        holder.userName.setVisibility(View.GONE);
                                        holder.userImage.setVisibility(View.GONE);
                                    }
                                    loadingBar.setVisibility(View.GONE);

                                } else {
                                    showToast(getApplicationContext(), "No Data", R.color.red);
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
                    loadingBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void display() {
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
                    public ServiceDataHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_container_service, viewGroup, false);

                        return new ServiceDataHolder(view);
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    public static class ServiceDataHolder extends RecyclerView.ViewHolder {
        TextView userName, userDate;
        ImageView userImage;
        LinearLayout layoutService;

        public ServiceDataHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.textName);
            userDate = itemView.findViewById(R.id.textDate);
            userImage = itemView.findViewById(R.id.image);
            layoutService = itemView.findViewById(R.id.layoutService);
        }
    }
    private void Info() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ServiceDoctorData.this, R.style.AlertDialog);
        builder.setTitle("Note");
        builder.setCancelable(false);

        final TextView groupNameField = new TextView(ServiceDoctorData.this);
        groupNameField.setText("1) The details mentioned are in the following order \n\n--> Service Name --> Service Start Date \n\n2) This section is used to display all the services that are provided by the hospital.");
        groupNameField.setPadding(20,30,20,20);
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