package com.vahan.mitra_playstore.view.history.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestBuilder;
import com.vahan.mitra_playstore.R;
import com.vahan.mitra_playstore.models.TransactionDetailsModelJava;
import com.vahan.mitra_playstore.utils.GlideApp;
import com.vahan.mitra_playstore.utils.SvgSoftwareLayerSetter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CompleteHistoryDetailsAdapter extends RecyclerView.Adapter<CompleteHistoryDetailsAdapter.MyViewHolder> {

    private ArrayList<TransactionDetailsModelJava.Transaction> transactionArrayList;
    private FragmentActivity activity;

    public CompleteHistoryDetailsAdapter(FragmentActivity activity, ArrayList<TransactionDetailsModelJava.Transaction> transactionDetailsModels) {
        this.transactionArrayList = transactionDetailsModels;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_each_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvTransDetails.setText(transactionArrayList.get(position).getLabel());
        if (transactionArrayList.get(position).getType().equalsIgnoreCase("debit")) {
            holder.tvAmount.setText("- " + transactionArrayList.get(position).getAmountLabel());
            holder.tvAmount.setTextColor(activity.getColor(R.color.black_v2));
        } else {
            holder.tvAmount.setText("+ " + transactionArrayList.get(position).getAmountLabel());
            holder.tvAmount.setTextColor(activity.getColor(R.color.green));
        }
        holder.tvTransSubHeading.setText(transactionArrayList.get(position).getDateTimeStr());

        if (!transactionArrayList.get(position).getStatusLabel().equalsIgnoreCase("")) {
            holder.tvStatus.setVisibility(View.VISIBLE);
//            int color = Integer.parseInt(transactionArrayList.get(position).getStatusColor(), 16)+0xFF000000;
//            holder.tvStatus.setTextColor(color);
            holder.tvStatus.setText(transactionArrayList.get(position).getStatusLabel());
            holder.tvStatus.setTextColor(Color.parseColor(transactionArrayList.get(position).getStatusColor()));
        } else {
            holder.tvStatus.setVisibility(View.GONE);
        }

        RequestBuilder<PictureDrawable> requestBuilder = GlideApp.with(activity)
                .as(PictureDrawable.class)
                .error(R.drawable.dialog_icon)
                .listener(new SvgSoftwareLayerSetter());
        Uri uri = Uri.parse(transactionArrayList.get(position).getIcon());
        requestBuilder.load(uri).into(holder.ivSymbol);
    }


    @Override
    public int getItemCount() {
        return transactionArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTransDetails, tvTransSubHeading, tvAmount, tvStatus;
        private ImageView ivSymbol;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTransDetails = itemView.findViewById(R.id.transc_name);
            tvTransSubHeading = itemView.findViewById(R.id.transc_name_subHeading);
            tvAmount = itemView.findViewById(R.id.salary_income);
            ivSymbol = itemView.findViewById(R.id.iv_Symbol);
            tvStatus = itemView.findViewById(R.id.salary_status);
            LinearLayout llContainer = itemView.findViewById(R.id.container_salary_item);
            llContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            if (view.getId() == R.id.container_salary_item) {
                bundle.putString("transactionId", transactionArrayList.get(getAdapterPosition()).getId());
                Navigation.findNavController(view).navigate(R.id.nav_fragment_view, bundle);
            }
        }
    }

    private String convertUtCTOLocale(String transactionDate) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        Date date = null;
        try {
            date = format.parse(transactionDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String dateTime = dateFormat.format(cal.getTime());
        return dateTime;
    }
}
