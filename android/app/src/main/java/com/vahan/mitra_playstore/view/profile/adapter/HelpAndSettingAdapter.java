package com.vahan.mitra_playstore.view.profile.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.freshchat.consumer.sdk.FaqOptions;
import com.freshchat.consumer.sdk.Freshchat;
import com.moe.pushlibrary.MoEHelper;
import com.vahan.mitra_playstore.R;
import com.vahan.mitra_playstore.models.OtherSettingModel;
import com.vahan.mitra_playstore.utils.Constants;
import com.vahan.mitra_playstore.utils.LocaleManager;
import com.vahan.mitra_playstore.utils.PrefrenceUtils;
import com.vahan.mitra_playstore.view.BaseApplication;
import com.vahan.mitra_playstore.view.MainActivity;
import com.vahan.mitra_playstore.view.SalaryViewActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HelpAndSettingAdapter extends RecyclerView.Adapter<HelpAndSettingAdapter.MyViewHolder> {
    private List<OtherSettingModel.OtherSetting> otherSettingModels;
    private FragmentActivity activity;
    private String language = "";
    private String languageHeader = "";
    AlertDialog alertDialog;

    public HelpAndSettingAdapter(FragmentActivity activity, List<OtherSettingModel.OtherSetting> data) {
        this.otherSettingModels = data;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.help_and_setting_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.icon.setImageResource(otherSettingModels.get(position).getIcon());

        if (PrefrenceUtils.retriveLangData(activity, Constants.LANGUAGE).equalsIgnoreCase("en")) {
            holder.tvKey.setText(otherSettingModels.get(position).getKey());
            Log.d("hey", "onBindViewHolder: "+otherSettingModels.get(position).getKey());
        } else if(PrefrenceUtils.retriveLangData(activity, Constants.LANGUAGE).equalsIgnoreCase("hi")) {
            holder.tvKey.setText(otherSettingModels.get(position).getHiKey());
        }else{
            holder.tvKey.setText(otherSettingModels.get(position).getTeKey());
        }
        holder.tvStatus.setText(otherSettingModels.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        return otherSettingModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvKey,tvStatus;
        private ImageView icon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvKey = itemView.findViewById(R.id.tv_key);
            tvStatus = itemView.findViewById(R.id.tv_status);
            icon = itemView.findViewById(R.id.iv_view_bank_details);
            LinearLayout llContainer = itemView.findViewById(R.id.help_and_setting_container);
            llContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.help_and_setting_container:
                    if (otherSettingModels.get(getAdapterPosition()).getKey().
                            equalsIgnoreCase("Logout")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                                .setTitle(activity.getString(R.string.alert_logout))
                                .setMessage(activity.getString(R.string.alert_logout_message))
                                .setCancelable(true)
                                .setNegativeButton(activity.getString(R.string.alert_no), (dialog, which) -> dialog.cancel())
                                .setPositiveButton(activity.getString(R.string.alert_yes), (dialog, which) -> {
                                    MoEHelper.getInstance(activity).logoutUser();
                                    PrefrenceUtils.insertData(activity, Constants.CheckUpdateCount, "");
                                    PrefrenceUtils.insertData(activity, Constants.API_TOKEN, "");
                                    PrefrenceUtils.insertDataInBoolean(activity, Constants.PROFILE_PAGE_VIEWED, false);
                                    PrefrenceUtils.insertDataInBoolean(activity, Constants.CASHOUT_AVAIL, false);
                                    PrefrenceUtils.insertDataInBoolean(activity, Constants.TXN_HISTORY, false);
                                    PrefrenceUtils.insertDataInBoolean(activity, Constants.LOAN_APPLICATION_VIEWED, false);
                                    PrefrenceUtils.insertDataInBoolean(activity, Constants.LOAN_APPLIED, false);
                                    PrefrenceUtils.insertDataInBoolean(activity, Constants.PAY_SLIP_VIEWED, false);
                                    PrefrenceUtils.insertDataInBoolean(activity, Constants.MILESTONE_DETAILS_VIEWED, false);
                                    PrefrenceUtils.insertDataInBoolean(activity, Constants.HOME_PAGE_VIEWED, false);
                                    PrefrenceUtils.logoutUser(activity);
                                    activity.finishAffinity();
                                });
                        final AlertDialog dialog = builder.create();
                        if (dialog.getWindow() != null)
                            dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                        dialog.show();
                    }
                    else if (otherSettingModels.get(getAdapterPosition()).getKey().
                            equalsIgnoreCase("Help & Support")) {
                       // Navigation.findNavController(view).navigate(R.id.nav_help_support);
                        Navigation.findNavController(view).navigate(R.id.nav_help_support_fragment);
                    }
                    else if (otherSettingModels.get(getAdapterPosition()).getKey().
                            equalsIgnoreCase("Notification Settings")) {
                        Navigation.findNavController(view).navigate(R.id.nav_fragment_notification);
                    }
                    else if (otherSettingModels.get(getAdapterPosition()).getKey().
                            equalsIgnoreCase("Edit Language")){
                        changeLanguage();
                    }
                    else if (otherSettingModels.get(getAdapterPosition()).getKey().
                            equalsIgnoreCase("Frequently Asked Questions")) {
                        List<String> tags = new ArrayList<>();
                        tags.add("newfaq");

                        FaqOptions faqOptions = new FaqOptions()
                                .showFaqCategoriesAsGrid(false)
                                .showContactUsOnAppBar(true)
                                .showContactUsOnFaqScreens(false)
                                .showContactUsOnFaqNotHelpful(false)
                                .filterByTags(tags, "Test 2", FaqOptions.FilterType.CATEGORY);//tags, filtered screen title, type

                        Freshchat.showFAQs(activity, faqOptions);
                    }
                    else if (otherSettingModels.get(getAdapterPosition()).getKey().
                            equalsIgnoreCase("Support Tickets")) {
                        Navigation.findNavController(view).navigate(R.id.action_nav_profile_fragment_to_nav_support_ticket_fragment);
                    }
                    else {
                        if (PrefrenceUtils.retriveLangData(activity, Constants.LANGUAGE).equalsIgnoreCase("en")) {
                            activity.startActivity(new Intent(activity, SalaryViewActivity.class)
                                    .putExtra(Constants._TYPE, otherSettingModels.get(getAdapterPosition()).getKey())
                                    .putExtra(Constants.LINK, otherSettingModels.get(getAdapterPosition()).getUrl()));
                        } else {
                            activity.startActivity(new Intent(activity, SalaryViewActivity.class)
                                    .putExtra(Constants._TYPE, otherSettingModels.get(getAdapterPosition()).getHiKey())
                                    .putExtra(Constants.LINK, otherSettingModels.get(getAdapterPosition()).getUrl()));
                        }

                    }
                    break;
            }
        }
    }

    private void changeLanguage()
    {
        //Freshchat.notifyAppLocaleChange(mContext!!);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity).setCancelable(true);
        View view = LayoutInflater.from(activity)
            .inflate(R.layout.language_selection_profile, null);
        RelativeLayout btnLanguageChange = view.findViewById(R.id.btn_lng_selection);
        RadioGroup rGLanguageChange = view.findViewById(R.id.lg_rg_profile);
        if (Objects.equals(PrefrenceUtils.retriveLangData(activity, Constants.LANGUAGE), "en")) {
            language = "en";
            rGLanguageChange.check(R.id.english_lg);
        } else if (Objects.equals(PrefrenceUtils.retriveLangData(activity, Constants.LANGUAGE), "hi")){
            language = "hi";
            rGLanguageChange.check(R.id.hindi_lg);
        }else {
            language = "te";
            rGLanguageChange.check(R.id.telugu_lg);
        }
       // if(!Objects.equals(language, "")) tvStatus.setText(language);


        rGLanguageChange.setOnCheckedChangeListener((group, checkedId) -> {
            int btn = group.getCheckedRadioButtonId();
            switch (btn) {
                case R.id.hindi_lg:
                    language = "hi";
                    languageHeader = "hindi";
                    Freshchat.notifyAppLocaleChange(activity);
                    break;
                case R.id.english_lg:
                    language = "en";
                    languageHeader = "english";
                    Freshchat.notifyAppLocaleChange(activity);
                    break;
                case R.id.telugu_lg:
                    language = "te";
                    languageHeader = "telugu";
                    Freshchat.notifyAppLocaleChange(activity);
            }
        });
        builder.setView(view);
        alertDialog = builder.create();

        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;

        alertDialog.show();
        btnLanguageChange.setOnClickListener(view1 -> {
            try {
                if (Objects.equals(PrefrenceUtils.retriveLangData(
                        activity, Constants.LANGUAGE), language)
            ) {
                    alertDialog.dismiss();
                } else if (!language.equals("")) {
                   // setInstrumentation();
                    setLocaleLanguage(language);
                    PrefrenceUtils.insertDataLang(activity, Constants.LANGUAGE, language);
                    PrefrenceUtils.insertDataLang(
                            activity,
                            Constants.LANGUAGE_API_RESP,
                            languageHeader);
                    activity.startActivity(new Intent(activity, MainActivity.class));
                    activity.finish();

                }
                Toast.makeText(activity,(R.string.please_select_the_language), Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(activity,(R.string.please_select_the_language), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLocaleLanguage(String language) {
        LocaleManager.setNewLocale(BaseApplication.getContext(), language);
    }
}
