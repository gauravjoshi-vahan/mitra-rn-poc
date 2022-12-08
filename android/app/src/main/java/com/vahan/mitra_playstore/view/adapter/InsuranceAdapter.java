package com.vahan.mitra_playstore.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vahan.mitra_playstore.R;
import com.vahan.mitra_playstore.models.InsuranceModel;
import com.vahan.mitra_playstore.utils.Constants;

import java.util.List;

public class InsuranceAdapter extends RecyclerView.Adapter<InsuranceAdapter.MyViewHolder> {
    private List<InsuranceModel.PremiumInfo> premiumInfos;
    private String name;
    Context context;

    public InsuranceAdapter(FragmentActivity activity, List<InsuranceModel.PremiumInfo> premiumInfo, String name) {
        this.premiumInfos = premiumInfo;
        this.name = name;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.premium_insurance_details, parent, false);
        context = itemView.getContext();
        return new MyViewHolder(itemView);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvPremiumTitle.setText(premiumInfos.get(position).getTitle());
        holder.tvPremiumExpiry.setText(context.getString(R.string.expires) + premiumInfos.get(position).getExpiresOn());
        holder.tvPremiumAmount.setText(premiumInfos.get(position).getSumAssured());
        Glide.with(context)
                .asBitmap()
                .load(premiumInfos.get(position).getInsuranceProviderLogo())
                .centerInside()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_app_icon)
                .into(holder.img_comp_logo);
        holder.rlContainer.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.BUNDLE_NAME,name);
            bundle.putString(Constants.BUNDLE_INSURANCE_NAME,premiumInfos.get(position).getTitle());
            bundle.putString(Constants.BUNDLE_INSURANCE_NAME_SUBHEADING,premiumInfos.get(position).getInsuranceName());
            bundle.putString(Constants.BUNDLE_PREMIUM_RATE,premiumInfos.get(position).getPremiumRate());
            bundle.putString(Constants.BUNDLE_INSURANCE_LOGO,premiumInfos.get(position).getInsuranceProviderLogo());
            bundle.putString(Constants.BUNDLE_SUM_ASSURED,premiumInfos.get(position).getSumAssured());
            bundle.putString(Constants.BUNDLE_DUE_FROM,premiumInfos.get(position).getDueFrom());
            bundle.putString(Constants.BUNDLE_DUE_FROM,premiumInfos.get(position).getExpiresOn());
            bundle.putString(Constants.BUNDLE_POLICY_STATUS,premiumInfos.get(position).getPolicyStatus());
            Navigation.findNavController(holder.rlContainer)
                    .navigate(R.id.nav_fragment_insurance_info, bundle);
       /*     activity.startActivity(new Intent(activity, InsuranceInfoActivity.class)
                    .putExtra("name",name)
                    .putExtra("insurance_name",premiumInfos.get(position).getTitle())
                    .putExtra("insurance_name_subHeading",premiumInfos.get(position).getInsuranceName())
                    .putExtra("premium",premiumInfos.get(position).getPremiumRate())
                    .putExtra("insurance_logo",premiumInfos.get(position).getInsuranceProviderLogo())
                    .putExtra("sum_assured",premiumInfos.get(position).getSumAssured())
                    .putExtra("due_from",premiumInfos.get(position).getDueFrom())
                    .putExtra("expire_from",premiumInfos.get(position).getExpiresOn())
                    .putExtra("policy_status",premiumInfos.get(position).getPolicyStatus()));*/

        });

    }

    @Override
    public int getItemCount() {
        return premiumInfos.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPremiumTitle, tvPremiumExpiry, tvPremiumAmount;
        private RelativeLayout rlContainer;
        private ImageView img_comp_logo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPremiumTitle = itemView.findViewById(R.id.tv_premium_title);
            tvPremiumExpiry = itemView.findViewById(R.id.tv_premium_expire);
            tvPremiumAmount = itemView.findViewById(R.id.tv_premium_amount);
            rlContainer = itemView.findViewById(R.id.container);
            img_comp_logo = itemView.findViewById(R.id.img_comp_logo);
        }
    }
}