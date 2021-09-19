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
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import com.arnoldvaz27.doctors.Ambulances;
import com.arnoldvaz27.management.databinding.AmbulanceDataBinding;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmbulanceData extends AppCompatActivity {

    AmbulanceDataBinding binding;
    private AlertDialog dialogAddAmbulance;
    private RecyclerView recyclerView;
    private DatabaseReference eventsRef;
    private ProgressBar loadingBar;
    private EditText inputSearch;
    ImageView info;

    private BottomSheetDialog bottomSheetDialog;
    private String ambulanceStatusItem, search;
    String ambulanceStatusString, NumberPlate, Status, visit_user_id, Email, Persons, PhoneNumber;
    private final Pattern pattern1 = Pattern.compile("^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.purple));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        binding = DataBindingUtil.setContentView(this, R.layout.ambulance_data);
        ImageView addAmbulance = binding.addAmbulance;
        recyclerView = binding.RecyclerView;
        loadingBar = binding.progressCircular;
        inputSearch = binding.inputSearch;
        info = binding.info;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Ambulance");
        bottomSheetDialog = new BottomSheetDialog(AmbulanceData.this, R.style.BottomSheetTheme);
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
        addAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialogAddAmbulance == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AmbulanceData.this);
                    View view = LayoutInflater.from(AmbulanceData.this).inflate(
                            R.layout.layout_add_ambulance, findViewById(R.id.layoutAddAmbulanceContainer)
                    );
                    builder.setView(view);

                    dialogAddAmbulance = builder.create();
                    if (dialogAddAmbulance.getWindow() != null) {
                        dialogAddAmbulance.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    }

                    final EditText inputNumberPlate, persons, email, phoneNumber;
                    final TextView status;
                    String[] AmbulanceStatus;
                    Spinner AmbulanceChooseStatus;
                    inputNumberPlate = view.findViewById(R.id.inputNumberPlate);
                    persons = view.findViewById(R.id.persons);
                    email = view.findViewById(R.id.floor);
                    phoneNumber = view.findViewById(R.id.phone);
                    status = view.findViewById(R.id.status);
                    AmbulanceChooseStatus = view.findViewById(R.id.statusChoose);
                    AmbulanceStatus = getResources().getStringArray(R.array.AmbulanceStatus);

                    inputNumberPlate.setSelection(inputNumberPlate.getText().length());
                    inputNumberPlate.requestFocus();
                    inputNumberPlate.getShowSoftInputOnFocus();
                    ArrayAdapter<String> adapterStream2 = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, AmbulanceStatus);
                    adapterStream2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    AmbulanceChooseStatus.setAdapter(adapterStream2);
                    AmbulanceChooseStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            AmbulanceChooseStatus.setSelection(i);
                            ambulanceStatusItem = adapterView.getItemAtPosition(i).toString();
                            ambulanceStatusString = ambulanceStatusItem;
                            status.setText(ambulanceStatusString);

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Matcher matcher1 = pattern1.matcher(email.getText().toString());

                            if (TextUtils.isEmpty(inputNumberPlate.getText().toString()) || TextUtils.isEmpty(persons.getText().toString()) ||
                                    TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(phoneNumber.getText().toString())) {
                                showToast(getApplicationContext(), "Please enter details in all the fields", R.color.red);
                            } else if (status.getText().toString().equals("Choose Status")) {
                                showToast(getApplicationContext(), "Status Invalid, Please select the correct status", R.color.red);
                            } else if (!matcher1.matches()) {
                                showToast(getApplicationContext(), "Invalid Email, please provide valid email", R.color.red);
                            } else if (phoneNumber.getText().toString().length() > 10 || phoneNumber.getText().toString().length() < 10) {
                                showToast(getApplicationContext(), "Please provide valid 10 digit phone number", R.color.red);
                            } else {

                                HashMap<String, Object> service = new HashMap<>();
                                service.put("numberPlate", inputNumberPlate.getText().toString());
                                service.put("persons", persons.getText().toString());
                                service.put("email", email.getText().toString());
                                service.put("phoneNumber", phoneNumber.getText().toString());
                                service.put("status", status.getText().toString());

                                String value = new SimpleDateFormat("ddMMyyyyHH:mm:ssa", Locale.getDefault()).format(new Date());
                                eventsRef.child(value).setValue(service).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            inputNumberPlate.setText("");
                                            persons.setText("");
                                            email.setText("");
                                            phoneNumber.setText("");
                                            status.setText("");
                                            showToast(getApplicationContext(), "Data added", R.color.green);
                                            dialogAddAmbulance.dismiss();
                                        } else {
                                            showToast(getApplicationContext(), "Please try again", R.color.red);
                                        }
                                    }
                                });
                            }
                        }
                    });

                    view.findViewById(R.id.textCancel).setOnClickListener(v1 -> {
                        dialogAddAmbulance.dismiss();

                    });
                }
                dialogAddAmbulance.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                dialogAddAmbulance.show();
            }
        });
    }

    private void findSpecific() {
        FirebaseRecyclerOptions<Ambulances> options = new FirebaseRecyclerOptions.Builder<Ambulances>()
                .setQuery(eventsRef, Ambulances.class).build();

        FirebaseRecyclerAdapter<Ambulances, AmbulancesDataHolder> adapter =
                new FirebaseRecyclerAdapter<Ambulances, AmbulancesDataHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull AmbulancesDataHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull Ambulances model) {
                        final String userIDs = getRef(position).getKey();

                        assert userIDs != null;
                        eventsRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() || dataSnapshot.hasChildren()) {

                                    Status = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();
                                    NumberPlate = Objects.requireNonNull(dataSnapshot.child("numberPlate").getValue()).toString();

                                    if (NumberPlate.toLowerCase().contains(search.toLowerCase())) {
                                        holder.userNumberPlate.setText(NumberPlate);
                                        if (Status.equals("Available")) {
                                            holder.userStatus.setTextColor(getResources().getColor(R.color.green));
                                        } else {
                                            holder.userStatus.setTextColor(getResources().getColor(R.color.red));
                                        }
                                        holder.userStatus.setText("Status: " + Status);
                                    } else {
                                        holder.layoutAmbulance.setVisibility(View.GONE);
                                        holder.userStatus.setVisibility(View.GONE);
                                        holder.userNumberPlate.setVisibility(View.GONE);
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

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                visit_user_id = getRef(position).getKey();
                                getDetails();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AmbulancesDataHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_container_ambulance, viewGroup, false);

                        return new AmbulancesDataHolder(view);
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

        FirebaseRecyclerOptions<Ambulances> options = new FirebaseRecyclerOptions.Builder<Ambulances>()
                .setQuery(eventsRef, Ambulances.class).build();

        FirebaseRecyclerAdapter<Ambulances, AmbulancesDataHolder> adapter =
                new FirebaseRecyclerAdapter<Ambulances, AmbulancesDataHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull AmbulancesDataHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull Ambulances model) {
                        final String userIDs = getRef(position).getKey();

                        assert userIDs != null;
                        eventsRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() || dataSnapshot.hasChildren()) {

                                    Status = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();
                                    NumberPlate = Objects.requireNonNull(dataSnapshot.child("numberPlate").getValue()).toString();
                                    holder.userNumberPlate.setText(NumberPlate);
                                    if (Status.equals("Available")) {
                                        holder.userStatus.setTextColor(getResources().getColor(R.color.green));
                                    } else {
                                        holder.userStatus.setTextColor(getResources().getColor(R.color.red));
                                    }
                                    holder.userStatus.setText("Status: " + Status);
                                } else {
                                    showToast(getApplicationContext(), "No Data", R.color.red);
                                }
                                loadingBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                visit_user_id = getRef(position).getKey();
                                getDetails();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AmbulancesDataHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_container_ambulance, viewGroup, false);

                        return new AmbulancesDataHolder(view);
                    }

                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class AmbulancesDataHolder extends RecyclerView.ViewHolder {
        TextView userNumberPlate, userStatus;
        ImageView userImage;
        LinearLayout layoutAmbulance;

        public AmbulancesDataHolder(@NonNull View itemView) {
            super(itemView);

            userNumberPlate = itemView.findViewById(R.id.textNumberPlate);
            userStatus = itemView.findViewById(R.id.textStatus);
            userImage = itemView.findViewById(R.id.image);
            layoutAmbulance = itemView.findViewById(R.id.layoutAmbulance);
        }
    }

    private void getDetails() {
        eventsRef.child(visit_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Status = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();
                    NumberPlate = Objects.requireNonNull(dataSnapshot.child("numberPlate").getValue()).toString();
                    Persons = Objects.requireNonNull(dataSnapshot.child("persons").getValue()).toString();
                    Email = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
                    PhoneNumber = Objects.requireNonNull(dataSnapshot.child("phoneNumber").getValue()).toString();

                    BottomSheet();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void BottomSheet() {

        final View sheetView = LayoutInflater.from(AmbulanceData.this).inflate(R.layout.ambulance_bottomsheet, findViewById(R.id.layoutMoreOptions));

        final CardView Close, Delete, Edit, Save, Discard;
        final GridLayout editingGrid, viewingGrid;
        final TextView numberPlate, persons, email, phone, status;
        final LinearLayout statusLayout;
        String[] AmbulanceStatus;
        Spinner AmbulanceStatusChoose;

        numberPlate = sheetView.findViewById(R.id.numberPlate);
        persons = sheetView.findViewById(R.id.persons);
        email = sheetView.findViewById(R.id.floor);
        phone = sheetView.findViewById(R.id.phone);
        status = sheetView.findViewById(R.id.status);
        Close = sheetView.findViewById(R.id.close);
        Delete = sheetView.findViewById(R.id.delete);
        Edit = sheetView.findViewById(R.id.edit);
        Save = sheetView.findViewById(R.id.save);
        Discard = sheetView.findViewById(R.id.discard);
        editingGrid = sheetView.findViewById(R.id.editingGrid);
        viewingGrid = sheetView.findViewById(R.id.viewingGrid);
        statusLayout = sheetView.findViewById(R.id.statusLayout);
        AmbulanceStatusChoose = sheetView.findViewById(R.id.statusChoose);

        numberPlate.setText(NumberPlate);
        persons.setText(Persons);
        email.setText(Email);
        phone.setText(PhoneNumber);
        status.setText(Status);

        AmbulanceStatus = getResources().getStringArray(R.array.AmbulanceStatus);

        ArrayAdapter<String> adapterStream2 = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, AmbulanceStatus);
        adapterStream2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AmbulanceStatusChoose.setAdapter(adapterStream2);
        AmbulanceStatusChoose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                AmbulanceStatusChoose.setSelection(i);
                ambulanceStatusItem = adapterView.getItemAtPosition(i).toString();
                ambulanceStatusString = ambulanceStatusItem;
                status.setText(ambulanceStatusString);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Edit.setOnClickListener(v -> {
            viewingGrid.setVisibility(View.GONE);
            editingGrid.setVisibility(View.VISIBLE);
            statusLayout.setVisibility(View.VISIBLE);
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
                            showToast(getApplicationContext(), "Data deleted", R.color.green);
                        } else {
                            showToast(getApplicationContext(), "Please try again", R.color.red);
                        }
                    }
                });
            }
        });
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Matcher matcher1 = pattern1.matcher(email.getText().toString());
                if (TextUtils.isEmpty(numberPlate.getText().toString()) || TextUtils.isEmpty(persons.getText().toString()) ||
                        TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(phone.getText().toString())) {
                    showToast(getApplicationContext(), "Please enter details in all the fields", R.color.red);
                } else if (status.getText().toString().equals("Choose Status")) {
                    showToast(getApplicationContext(), "Status Invalid, Please select the correct status", R.color.red);
                } else if (!matcher1.matches()) {
                    showToast(getApplicationContext(), "Invalid Email, please provide valid email", R.color.red);
                } else if (phone.getText().toString().length() > 10 || phone.getText().toString().length() < 10) {
                    showToast(getApplicationContext(), "Please provide valid 10 digit phone number", R.color.red);
                } else {
                    bottomSheetDialog.dismiss();
                    VerifyData();
                    HashMap<String, Object> service = new HashMap<>();
                    service.put("numberPlate", numberPlate.getText().toString());
                    service.put("persons", persons.getText().toString());
                    service.put("email", email.getText().toString());
                    service.put("phoneNumber", phone.getText().toString());
                    service.put("status", status.getText().toString());

                    eventsRef.child(visit_user_id).setValue(service).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                numberPlate.setText("");
                                persons.setText("");
                                email.setText("");
                                phone.setText("");
                                status.setText("");
                                showToast(getApplicationContext(), "Data updated", R.color.green);
                            } else {
                                showToast(getApplicationContext(), "Please try again", R.color.red);
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

    private void Info() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(AmbulanceData.this, R.style.AlertDialog);
        builder.setTitle("Note");
        builder.setCancelable(false);

        final TextView groupNameField = new TextView(AmbulanceData.this);
        groupNameField.setText("1) The details mentioned are in the following order \n\n--> Ambulance number --> Number of persons --> Email --> phone number --> Status \n\n2) You can edit the status of the ambulance and also delete the ambulance data.");
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