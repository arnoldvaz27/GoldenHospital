package com.arnoldvaz27.nurses;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arnoldvaz27.doctors.Beds;
import com.arnoldvaz27.nurses.databinding.CanteenNurseDataBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class CanteenNurseData extends AppCompatActivity {

    CanteenNurseDataBinding binding;
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
        binding = DataBindingUtil.setContentView(this, R.layout.canteen_nurse_data);

        recyclerView = binding.RecyclerView;
        loadingBar = binding.progressCircular;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Canteen");
        bottomSheetDialog = new BottomSheetDialog(CanteenNurseData.this, R.style.BottomSheetTheme);
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
                    showToast(getApplicationContext(),"No Data", R.color.red);
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

        public FoodDataHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.textName);
            userPrice = itemView.findViewById(R.id.textPrice);
            userImage = itemView.findViewById(R.id.image);
        }
    }
}