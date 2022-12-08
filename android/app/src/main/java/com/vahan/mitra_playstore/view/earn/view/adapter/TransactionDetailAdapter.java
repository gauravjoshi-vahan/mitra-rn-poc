package com.vahan.mitra_playstore.view.earn.view.adapter;

import static com.vahan.mitra_playstore.view.earn.view.ui.HomeFragment.recylerViewItemCheck;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.vahan.mitra_playstore.models.kotlin.TransactionDetailModel;
import com.vahan.mitra_playstore.utils.Constants;
import com.vahan.mitra_playstore.utils.GlideApp;
import com.vahan.mitra_playstore.utils.SvgSoftwareLayerSetter;
import com.vahan.mitra_playstore.view.earn.view.ui.EarnFragmentV2;
import com.vahan.mitra_playstore.view.earn.view.ui.HomeFragment;

import java.util.ArrayList;
import java.util.Objects;


public class TransactionDetailAdapter extends RecyclerView.Adapter<TransactionDetailAdapter.MyViewHolder> {
    private ArrayList<TransactionDetailModel.Transaction> transactionArrayList;
    private Context activity;


    public TransactionDetailAdapter(FragmentActivity activity, ArrayList<TransactionDetailModel.Transaction> transactionDetailsModels) {
        this.transactionArrayList = transactionDetailsModels;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_details_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvTransDetails.setText(transactionArrayList.get(position).getLabel());
        if (Objects.requireNonNull(transactionArrayList.get(position).getType()).equalsIgnoreCase("debit")) {
            holder.tvAmount.setText("- " + transactionArrayList.get(position).getAmountLabel());
            holder.tvAmount.setTextColor(activity.getColor(R.color.black_v2));
        } else {
            holder.tvAmount.setText("+ " + transactionArrayList.get(position).getAmountLabel());
            holder.tvAmount.setTextColor(activity.getColor(R.color.green));
        }
        holder.tvTransSubHeading.setText(transactionArrayList.get(position).getDateTimeStr());

        if (!Objects.requireNonNull(transactionArrayList.get(position).getStatusLabel()).equalsIgnoreCase("")) {
            holder.tvStatus.setTextColor(Color.parseColor(transactionArrayList.get(position).getStatusColor()));
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(transactionArrayList.get(position).getStatusLabel());
        } else {
            holder.tvStatus.setVisibility(View.GONE);
        }

        if (recylerViewItemCheck) {
            HomeFragment.recylerViewItemCheck = true;
            holder.ivIcon.setVisibility(View.VISIBLE);
            holder.llContainer.setEnabled(true);
            holder.llContainer.setClickable(true);
        } else {
            holder.ivIcon.setVisibility(View.GONE);
            holder.llContainer.setEnabled(false);
            holder.llContainer.setClickable(false);
        }


//
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
        private ImageView ivSymbol, ivIcon;
        private LinearLayout container,llContainer;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTransDetails = itemView.findViewById(R.id.transc_name);
            tvTransSubHeading = itemView.findViewById(R.id.transc_name_subHeading);
            tvAmount = itemView.findViewById(R.id.salary_income);
            ivSymbol = itemView.findViewById(R.id.iv_Symbol);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvStatus = itemView.findViewById(R.id.salary_status);
            container = itemView.findViewById(R.id.container);
            llContainer = itemView.findViewById(R.id.container_salary_item);
            llContainer.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            if (view.getId() == R.id.container_salary_item) {
                bundle.putString(Constants.TRANSACTION_ID, transactionArrayList.get(getAdapterPosition()).getId());
                Navigation.findNavController(view).navigate(R.id.nav_fragment_view, bundle);
            }
        }
    }
}