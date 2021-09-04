package com.arnoldvaz27.doctors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

//custom toast class to show custom coloured toast messages
public class CustomToast {
    public static void showToast(Context context, String message, int color) {
        Toast toast = new Toast(context);

        @SuppressLint("InflateParams") View view = LayoutInflater.from(context) //inflating the toast layout
                .inflate(R.layout.toast, null);

        TextView tvMessage = view.findViewById(R.id.tvMessage);
        CardView cardView = view.findViewById(R.id.cardView);
        cardView.setCardBackgroundColor(context.getResources().getColor(color));
        tvMessage.setText(message);

        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show(); //showing the toast
    }
}
