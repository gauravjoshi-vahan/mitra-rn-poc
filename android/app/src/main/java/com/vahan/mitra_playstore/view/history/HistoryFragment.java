package com.vahan.mitra_playstore.view.history;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.moe.pushlibrary.MoEHelper;
import com.moengage.core.Properties;
import com.moengage.inapp.MoEInAppHelper;
import com.vahan.mitra_playstore.R;
import com.vahan.mitra_playstore.databinding.FragmentHistoryBinding;
import com.vahan.mitra_playstore.interfaces.SortAndFilterCallback;
import com.vahan.mitra_playstore.model.CashOutModel;
import com.vahan.mitra_playstore.models.FeedbackTriggersModel;
import com.vahan.mitra_playstore.models.TransactionDetailsFilterConstraintsModel;
import com.vahan.mitra_playstore.models.TransactionDetailsModelJava;
import com.vahan.mitra_playstore.models.kotlin.EarnDataModel;
import com.vahan.mitra_playstore.network.SharedViewModel;
import com.vahan.mitra_playstore.utils.Constants;
import com.vahan.mitra_playstore.utils.PrefrenceUtils;
import com.vahan.mitra_playstore.view.bottomsheet.BottomSheetCashOutPurpose;
import com.vahan.mitra_playstore.view.bottomsheet.BottomSheetCashoutHold;
import com.vahan.mitra_playstore.view.bottomsheet.BottomSheetV2;
import com.vahan.mitra_playstore.view.bottomsheet.FeedbackBottomsheet;
import com.vahan.mitra_playstore.view.earn.view.ui.HomeFragment;
import com.vahan.mitra_playstore.view.earn.viewModel.EarnViewModel;
import com.vahan.mitra_playstore.view.history.adapter.CompleteHistoryDetailsAdapter;
import com.vahan.mitra_playstore.view.history.adapter.FilterListAdapter;
import com.vahan.mitra_playstore.view.history.adapter.SortListAdapter;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HistoryFragment extends Fragment implements SortAndFilterCallback {

    private FragmentHistoryBinding binding;
    private ArrayList<TransactionDetailsModelJava.Transaction> transactionDetailsModels;
    private SharedViewModel viewSharedViewModel;
    private EarnViewModel viewEarnViewModel;
    private EarnDataModel.CashOutDetails cashOutDetails;
    private EarnDataModel.CashoutAdditionalData cashoutAdditionalData;
    private EarnDataModel.User user;
    private ArrayList<TransactionDetailsFilterConstraintsModel.Filter> filterModel;
    private ArrayList<TransactionDetailsFilterConstraintsModel.Sort> sortedMapValue;
    private BottomSheetDialog mBottomSheetDialog;
    private Dialog dialogLoader;
    private ArrayList<String> companyName = new ArrayList<>();
    private ArrayList<String> dateName = new ArrayList<>();
    private FilterListAdapter filterListAdapter;
    private FilterListAdapter filterCompanyListAdapter;
    private BottomSheetDialog mBottomSheetDialogFilter;
    public static int position = 0;
    private Handler handler;
    public ArrayList<String> valuesSelectedList = new ArrayList<>();
    public String type = "", sortBy = "";
    private FirebaseAnalytics fa;
    public String singleSelectedFilter = "";
    private FeedbackTriggersModel trigger;

    String key = "";
    private Boolean eligiblityCheck = false;
    JSONObject jsonObject;
    JSONObject objEC;
    JSONObject objEW;
    JSONObject objNE;
    JSONObject objE;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);
        viewSharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        viewEarnViewModel = new ViewModelProvider(this).get(EarnViewModel.class);
        if (getArguments() != null) {
            cashOutDetails = getArguments().getParcelable("cashoutDetails");
            cashoutAdditionalData = getArguments().getParcelable("cashoutAdditionalData");
        }
        user = getArguments().getParcelable("userData");
        key = getArguments().getString("key");
        Log.d(
                "jsonStringObj",
                "getUpdates2: " + PrefrenceUtils.retriveData(
                        getContext(),
                        Constants.REMOTE_CONFIG_CASHOUT_CONDITION
                )
        );
        try {
             jsonObject = new JSONObject(PrefrenceUtils.retriveData(getContext(), Constants.REMOTE_CONFIG_CASHOUT_CONDITION));
            objEC = jsonObject.getJSONObject("EC");
            objEW = jsonObject.getJSONObject("EW");
            objNE = jsonObject.getJSONObject("NE");
            objE = jsonObject.getJSONObject("E");
        }catch (Throwable t){

        }


        initView();
        position = 0;
        valuesSelectedList = new ArrayList<>();
        return binding.getRoot();
    }

    private void initView() {

        handler = new Handler();
        fa = FirebaseAnalytics.getInstance(requireActivity());
        getRemoteConfigDataForUpdate();
        initLoader(getActivity());
        getDatafromRemoteConfig();
        getTransactionDetails(type);
        clickListener();
        setCashout();

    }

    private void setCashout() {
        if (Objects.equals(key, "experimentalUser")) {
            //experimental Flow
            experimentFlow();
        } else if (Objects.equals(key, "oldUser")) {
            // old User
            updateUI();
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        binding.cashoutCardRoot.setVisibility(View.VISIBLE);
        binding.cashLayoutExp.setVisibility(View.GONE);

        if (Objects.equals(user.getCashoutEligibilityStatus(), "EC")) {
//            requireContext().captureAllEvents(requireContext(),
//                    Constants.CASHOUT_DISABLED_VIEWED,
//                    HashMap(),
//                    Properties())
            binding.cashoutTxt.setVisibility(View.VISIBLE);
            binding.cashLayout.setVisibility(View.GONE);
            binding.cashoutCardHoldRoot.setVisibility(View.GONE);

                if (Objects.equals(PrefrenceUtils.retriveLangData(getContext(), Constants.LANGUAGE), "en")) {
                    try {
                        binding.cashOutConfigTxt.setText(objEC.getString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        binding.cashOutConfigTxt.setText(objEC.getString("hi_msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

        }
        else if (Objects.equals(user.getCashoutEligibilityStatus(), "E")) {
            if(Boolean.TRUE.equals(Objects.requireNonNull(cashOutDetails.getHoldDetails()).isHold())) {
                binding.setPriceExpHold.setText(cashOutDetails.getAmountEligibleLabel());
                binding.cashoutCardRoot.setVisibility(View.GONE);
                binding.cashoutCardHoldRoot.setVisibility(View.VISIBLE);
                binding.ivIconHold.setImageResource(R.drawable.ic_cashout_hold);
                binding.cashOutHoldTxt.setText(getString(R.string.cashout_on_hold));
                binding.cashOutHoldTxtDesc.setText(cashOutDetails.getHoldDetails().getCompanyName()+ " "+cashOutDetails.getHoldDetails().getHoldMessage());
            }else{
                binding.cashoutCardHoldRoot.setVisibility(View.GONE);
                if (Objects.equals(PrefrenceUtils.retriveLangData(getContext(), Constants.LANGUAGE), "en")) {
                    try {
                        binding.cashOutConfigTxt.setText(objE.getString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        binding.cashOutConfigTxt.setText(objE.getString("hi_msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                binding.cashoutTxt.setVisibility(View.GONE);
                if (Boolean.TRUE.equals(cashOutDetails.getEnabled()) && Objects.requireNonNull(cashOutDetails.getAmountEligible()) >= Objects.requireNonNull(cashOutDetails.getMinAmountEligible())) {
//                val properties = Properties()
//                val attribute:HashMap<String, Any> =HashMap()
//                properties.addAttribute(Constants.ELIGIBLE_AMOUNT,
//                        dataModel !!.cashoutDetails ?.amountEligible ?:0.0)
//                properties.addAttribute(Constants.CASHED_FEE_PERCENT,
//                        dataModel !!.cashoutDetails ?.cashoutFeePercentage ?:0.0)
//                properties.addAttribute(Constants.CASHED_FIXED_FEE,
//                        dataModel !!.cashoutDetails ?.cashoutFixedFee ?:0)
//                // CHECK VALUE IS NOT EMPTY...
//                attribute[Constants.ELIGIBLE_AMOUNT] =
//                        dataModel !!.cashoutDetails ?.amountEligible ?:0.0
//                attribute[Constants.CASHED_FEE_PERCENT] =
//                        dataModel !!.cashoutDetails ?.cashoutFeePercentage ?:0.0
//                attribute[Constants.CASHED_FIXED_FEE] =
//                        dataModel !!.cashoutDetails ?.cashoutFixedFee ?:0
//                requireContext().captureAllEvents(requireContext(),
//                        Constants.CASHOUT_ELIGIBLE_VIEWED,
//                        attribute,
//                        properties)
                    binding.cashLayout.setVisibility(View.VISIBLE);
                    binding.cashLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                        setInstrumentationOnCLick(dataModel !!, "regular_flow")
                            new BottomSheetV2(requireActivity(),
                                    Objects.requireNonNull(cashOutDetails.getAmountEligibleLabel()),
                                    null,
                                    HistoryFragment.this,
                                    cashOutDetails.getAmountEligible(),
                                    Objects.requireNonNull(cashOutDetails.getCashoutFixedFee()),
                                    cashOutDetails.isCashoutFeeEnabled(),
                                    Objects.requireNonNull(cashOutDetails.getCashoutFeePercentage()),
                                    0,
                                    0,
                                    false,
                                    false,
                                    0
                            ).show();
                        }
                    });
                } else if (Boolean.FALSE.equals(cashOutDetails.getEnabled()) && Objects.requireNonNull(cashOutDetails.getAmountEligible()) >= Objects.requireNonNull(cashOutDetails.getMinAmountEligible())) {
                    binding.imgGrey.setVisibility(View.VISIBLE);
                    binding.cashLayout.setVisibility(View.VISIBLE);
                    binding.cashLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(
                                    requireContext(),
                                    getString(R.string.you_have_already_availed_the_service),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
                } else if (Boolean.TRUE.equals(cashOutDetails.getEnabled()) && Objects.requireNonNull(cashOutDetails.getAmountEligible()) < Objects.requireNonNull(cashOutDetails.getMinAmountEligible()) && cashOutDetails.getAmountEligible() != 0.0) {
//                requireContext().captureEvents(requireContext(),
//                        Constants.CASHOUT_INELIGIBLE_VIEWED,
//                        HashMap())
                    binding.cashLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                        val properties = Properties()
//                        val attribute = HashMap < String, Any>()
//                        requireContext().captureAllEvents(requireContext(),
//                                Constants.CASHOUT_INELIGIBLE_TAPPED,
//                                attribute,
//                                properties)

                            if (Objects.equals(PrefrenceUtils.retriveLangData(getContext(), Constants.LANGUAGE), "en")) {
                                Toast.makeText(requireContext(),
                                        "Minimum amount for cashout is Rs. "
                                                + cashOutDetails.getMinAmountEligible(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(),
                                        "कैशआउट के लिए न्यूनतम राशि रु. "
                                                + cashOutDetails.getMinAmountEligible(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    binding.cashLayout.setVisibility(View.VISIBLE);
                    binding.imgGrey.setVisibility(View.VISIBLE);
                } else if (Boolean.TRUE.equals(cashOutDetails.getEnabled()) && Objects.requireNonNull(cashOutDetails.getAmountEligible()) < Objects.requireNonNull(cashOutDetails.getMinAmountEligible()) && cashOutDetails.getAmountEligible() == 0.0) {
                    binding.cashLayout.setVisibility(View.GONE);
                } else if (Boolean.FALSE.equals(cashOutDetails.getEnabled()) && Objects.requireNonNull(cashOutDetails.getAmountEligible()) < Objects.requireNonNull(cashOutDetails.getMinAmountEligible()) && cashOutDetails.getAmountEligible() == 0.0) {
                    binding.cashLayout.setVisibility(View.GONE);
                } else if (Boolean.TRUE.equals(cashOutDetails.getEnabled()) && Objects.requireNonNull(cashOutDetails.getAmountEligible()) < Objects.requireNonNull(cashOutDetails.getMinAmountEligible()) && cashOutDetails.getAmountEligible() < 0.0) {
                    binding.cashLayout.setVisibility(View.GONE);
                }
            }

        }
        else if (Objects.equals(user.getCashoutEligibilityStatus(), "EW")) {
//            val properties = Properties()
//            val attribute = HashMap < String, Any>()
//            requireContext().captureAllEvents(requireContext(),
//                    Constants.CASHOUT_DISABLED_VIEWED,
//                    attribute,
//                    properties)
            binding.cashoutTxt.setVisibility(View.VISIBLE);
            binding.cashLayout.setVisibility(View.GONE);
            binding.cashoutCardHoldRoot.setVisibility(View.GONE);
            if (Objects.equals(PrefrenceUtils.retriveLangData(getContext(), Constants.LANGUAGE), "en")) {
                try {
                    binding.cashOutConfigTxt.setText(objEW.getString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    binding.cashOutConfigTxt.setText(objEW.getString("hi_msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (Objects.equals(user.getCashoutEligibilityStatus(), "NE")) {
//            val properties = Properties()
//            val attribute = HashMap < String, Any>()
//            requireContext().captureAllEvents(requireContext(),
//                    Constants.CASHOUT_DISABLED_VIEWED,
//                    attribute,
//                    properties)
            binding.cashoutTxt.setVisibility(View.VISIBLE);
            binding.cashLayout.setVisibility(View.GONE);
            binding.cashoutCardHoldRoot.setVisibility(View.GONE);
            if (Objects.equals(PrefrenceUtils.retriveLangData(getContext(), Constants.LANGUAGE), "en")) {
                try {
                    binding.cashOutConfigTxt.setText(objNE.getString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    binding.cashOutConfigTxt.setText(objNE.getString("hi_msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        if (String.valueOf(cashOutDetails.getAmountEligible()).contains(".")) {
            try {
                binding.setPrice.setText(getResources().getString(R.string.rupee)+" "+Objects.requireNonNull(cashOutDetails.getAmountEligible()).toString().substring(0,cashOutDetails.getAmountEligible().toString().indexOf(".")));
            } catch (Exception e) {

            }
        } else {
            binding.setPrice.setText(getResources().getString(R.string.rupee)+" "+cashOutDetails.getAmountEligibleLabel());
        }
    }


    @SuppressLint("SetTextI18n")
    private void experimentFlow() {
        binding.cashoutCardRoot.setVisibility(View.VISIBLE);
        binding.cashLayout.setVisibility(ViewPager.GONE);
        if (Objects.equals(cashoutAdditionalData.getCashoutEligibilityStatus(), "EC")) {
            eligiblityCheck = false;
//                val properties = Properties()
//                val attribute:HashMap<String, Any> =HashMap()
//                requireContext().captureAllEvents(requireContext(),
//                        Constants.CASHOUT_DISABLED_VIEWED,
//                        attribute,
//                        properties)
            binding.cashoutTxt.setVisibility(ViewPager.VISIBLE);
            binding.cashLayoutExp.setVisibility(ViewPager.GONE);
            binding.cashoutCardHoldRoot.setVisibility(View.GONE);
            if (Objects.equals(PrefrenceUtils.retriveLangData(getContext(), Constants.LANGUAGE), "en")) {
                try {
                    binding.cashOutConfigTxt.setText(objEC.getString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    binding.cashOutConfigTxt.setText(objEC.getString("hi_msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (Objects.equals(cashoutAdditionalData.getCashoutEligibilityStatus(), "E")) {
            if(Boolean.TRUE.equals(Objects.requireNonNull(cashOutDetails.getHoldDetails()).isHold())) {
                binding.setPriceExpHold.setText(cashOutDetails.getAmountEligibleLabel());
                binding.cashoutCardRoot.setVisibility(View.GONE);
                binding.cashoutCardHoldRoot.setVisibility(View.VISIBLE);
                binding.ivIconHold.setImageResource(R.drawable.ic_cashout_hold);
                binding.cashOutHoldTxt.setText(getString(R.string.cashout_on_hold));
                binding.cashOutHoldTxtDesc.setText(cashOutDetails.getHoldDetails().getCompanyName() + " " + cashOutDetails.getHoldDetails().getHoldMessage());
            }else{
                eligiblityCheck = true;
                binding.cashoutTxt.setVisibility(View.GONE);
                binding.cashoutCardHoldRoot.setVisibility(View.GONE);
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        int progress = 1;
                        while (progress <= Objects.requireNonNull(cashoutAdditionalData.getCashoutPercentage())) {
                            try {
                                Thread.sleep(10); // 5 seconds
                                binding.cashoutProgressBar.setProgress(progress);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            progress += 1;
                        }
                    }
                };
                thread.start();
                binding.cashoutProgressBar.setProgressDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.item_progress));
                if (Objects.equals(PrefrenceUtils.retriveLangData(getActivity(), Constants.LANGUAGE), "en")) {
                    binding.tvCashoutDesc.setText("Your Cashout Limit is " + cashoutAdditionalData.getCashoutPercentage() + "%");
                } else {
                    binding.tvCashoutDesc.setText("आपकी कैशआउट सीमा " + cashoutAdditionalData.getCashoutPercentage() + "% है");
                }


                if (Objects.equals(PrefrenceUtils.retriveLangData(requireActivity(), Constants.LANGUAGE), "en")) {
                    if (Objects.equals(Boolean.TRUE, cashoutAdditionalData.getOrderReachToNextLevel()) && Boolean.TRUE.equals(cashoutAdditionalData.getDaysReachToNextLevel())) {
                        binding.cashOutConfigTxtDetailExp.setText("Complete " + cashoutAdditionalData.getOrderRequiredToReachToNextLevel() +
                                " trips in " + cashoutAdditionalData.getDaysRequiredTOReachToNextLevel() +
                                " days to icrease limit to " + cashoutAdditionalData.getCashoutNextLevelPercentage() + "%");
                    } else if (Boolean.TRUE.equals(cashoutAdditionalData.getOrderReachToNextLevel()) && !Boolean.TRUE.equals(cashoutAdditionalData.getDaysReachToNextLevel())) {
                        binding.cashOutConfigTxtDetailExp.setText("Complete " + cashoutAdditionalData.getOrderRequiredToReachToNextLevel() +
                                " trips with Mitra to unlock " +
                                cashoutAdditionalData.getCashoutNextLevelPercentage() + " % of Cashout");


                    } else if (!Boolean.TRUE.equals(cashoutAdditionalData.getOrderReachToNextLevel()) && Boolean.TRUE.equals(cashoutAdditionalData.getDaysReachToNextLevel())) {
                        binding.cashOutConfigTxtDetailExp.setText("Continue working " +
                                cashoutAdditionalData.getDaysRequiredTOReachToNextLevel() +
                                " more days with Mitra to unlock " +
                                cashoutAdditionalData.getCashoutNextLevelPercentage() + "% of Cashout");
                    } else if (!Boolean.TRUE.equals(cashoutAdditionalData.getOrderReachToNextLevel()) && !Boolean.TRUE.equals(cashoutAdditionalData.getDaysReachToNextLevel())) {
                        binding.cashOutConfigTxtDetailExp.setText("You are now eligible for maximum cashout!");
                    }
                } else {
                    if (Boolean.TRUE.equals(cashoutAdditionalData.getOrderReachToNextLevel()) && Boolean.TRUE.equals(cashoutAdditionalData.getDaysReachToNextLevel())) {
                        binding.cashOutConfigTxtDetailExp.setText(cashoutAdditionalData.getOrderRequiredToReachToNextLevel() + " ट्रिप " +
                                cashoutAdditionalData.getDaysRequiredTOReachToNextLevel() +
                                " दिन में पूरा करके " + cashoutAdditionalData.getCashoutNextLevelPercentage() + "% की लिमिट बढ़ा सकते हैं ");
                    } else if (Boolean.TRUE.equals(cashoutAdditionalData.getOrderReachToNextLevel()) && !Boolean.TRUE.equals(cashoutAdditionalData.getDaysReachToNextLevel())) {
                        binding.cashOutConfigTxtDetailExp.setText(cashoutAdditionalData.getCashoutNextLevelPercentage() + "% कैशआउट अनलॉक करने के लिए मित्रा में " + cashoutAdditionalData.getOrderRequiredToReachToNextLevel() + " + और यात्राएं पूरी करे");
                    } else if (!Boolean.TRUE.equals(cashoutAdditionalData.getOrderReachToNextLevel()) && !Boolean.TRUE.equals(cashoutAdditionalData.getDaysReachToNextLevel())) {
                        binding.cashOutConfigTxtDetailExp.setText("अब आप अधिकतम कैशआउट के पात्र हैं!");
                    } else if (!Boolean.TRUE.equals(cashoutAdditionalData.getOrderReachToNextLevel()) && Boolean.TRUE.equals(cashoutAdditionalData.getDaysReachToNextLevel())) {
                        binding.cashOutConfigTxtDetailExp.setText(cashoutAdditionalData.getCashoutNextLevelPercentage() + "% कैशआउट अनलॉक करने के लिए मित्रा में " + cashoutAdditionalData.getDaysRequiredTOReachToNextLevel() + " और दिन काम करना जारी रखें ");
                    }
                }

                if (Boolean.TRUE.equals(cashOutDetails.getEnabled()) && Objects.requireNonNull(cashOutDetails.getAmountEligible()) >= Objects.requireNonNull(cashOutDetails.getMinAmountEligible())) {
                    binding.cashLayoutExp.setVisibility(View.VISIBLE);

                    binding.cashLayoutExp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //setInstrumentationOnCLick(dataModel !!, "Cashout Levels Flow")
                            new BottomSheetCashOutPurpose(
                                    requireActivity(),
                                    cashoutAdditionalData.getUserLevel(),
                                    Objects.requireNonNull(cashOutDetails.getAmountEligibleLabel()),
                                    null,
                                    HistoryFragment.this,
                                    cashOutDetails.getAmountEligible(),
                                    Objects.requireNonNull(cashOutDetails.getCashoutFixedFee()),
                                    cashOutDetails.isCashoutFeeEnabled(),
                                    Objects.requireNonNull(cashOutDetails.getCashoutFeePercentage()),
                                    cashoutAdditionalData.getOrderRequiredToReachToNextLevel(),
                                    cashoutAdditionalData.getDaysRequiredTOReachToNextLevel(),
                                    cashoutAdditionalData.getCashoutEligibilityStatus(),
                                    cashoutAdditionalData.getOrderReachToNextLevel(),
                                    cashoutAdditionalData.getDaysReachToNextLevel(),
                                    cashoutAdditionalData.getCashoutNextLevelPercentage()
                            ).show();
                        }
                    });
                } else if (Boolean.FALSE.equals(cashOutDetails.getEnabled()) && Objects.requireNonNull(cashOutDetails.getAmountEligible()) >= Objects.requireNonNull(cashOutDetails.getMinAmountEligible())) {
                    binding.imgGreyExp.setVisibility(View.VISIBLE);
                    binding.cashLayoutExp.setVisibility(View.VISIBLE);
                    binding.cashLayoutExp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(
                                    requireContext(),
                                    getString(R.string.you_have_already_availed_the_service),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
                } else if (Boolean.TRUE.equals(cashOutDetails.getEnabled()) && Objects.requireNonNull(cashOutDetails.getAmountEligible()) < Objects.requireNonNull(cashOutDetails.getMinAmountEligible()) && cashOutDetails.getAmountEligible() != 0.0) {
//            val property = Properties()
//            val attribute = HashMap < String, Any>()
//            requireContext().captureEvents(requireContext(),
//                    Constants.CASHOUT_INELIGIBLE_VIEWED,
//                    HashMap())
                    binding.cashLayoutExp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                    requireContext().captureAllEvents(requireContext(),
//                            Constants.CASHOUT_INELIGIBLE_TAPPED,
//                            attribute,
//                            property)
                            if (Objects.equals(PrefrenceUtils.retriveLangData(requireActivity(), Constants.LANGUAGE), "en")) {
                                Toast.makeText(requireContext(), "Minimum amount for cashout is Rs. " +
                                                cashOutDetails.getMinAmountEligible(), Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                Toast.makeText(requireContext(),
                                        "कैशआउट के लिए न्यूनतम राशि रु. " +
                                                cashOutDetails.getMinAmountEligible(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    binding.cashLayoutExp.setVisibility(View.VISIBLE);
                    binding.imgGreyExp.setVisibility(View.VISIBLE);
                } else if (Boolean.TRUE.equals(cashOutDetails.getEnabled()) && Objects.requireNonNull(cashOutDetails.getAmountEligible()) < Objects.requireNonNull(cashOutDetails.getMinAmountEligible()) && cashOutDetails.getAmountEligible() == 0.0) {
                    binding.cashLayoutExp.setVisibility(View.GONE);
                } else if (Boolean.FALSE.equals(cashOutDetails.getEnabled()) && Objects.requireNonNull(cashOutDetails.getAmountEligible()) < Objects.requireNonNull(cashOutDetails.getMinAmountEligible()) && cashOutDetails.getAmountEligible() == 0.0) {
                    binding.cashLayoutExp.setVisibility(View.GONE);
                } else if (Boolean.TRUE.equals(cashOutDetails.getEnabled()) && Objects.requireNonNull(cashOutDetails.getAmountEligible()) < Objects.requireNonNull(cashOutDetails.getMinAmountEligible()) && cashOutDetails.getAmountEligible() < 0.0) {
                    binding.cashLayoutExp.setVisibility(View.GONE);
                }
            }


        }
        else if (Objects.equals(cashoutAdditionalData.getCashoutEligibilityStatus(), "EW")) {
            eligiblityCheck = false;
//            val properties = Properties()
//            val attribute = HashMap < String, Any>()
//            requireContext().captureAllEvents(requireContext(),
//                    Constants.CASHOUT_DISABLED_VIEWED,
//                    attribute,
//                    properties)
            binding.cashoutTxt.setVisibility(View.VISIBLE);
            binding.cashLayout.setVisibility(View.GONE);
            binding.cashoutCardHoldRoot.setVisibility(View.GONE);
            if (Objects.equals(PrefrenceUtils.retriveLangData(getActivity(), Constants.LANGUAGE), "en")) {
                try {
                    binding.cashOutConfigTxt.setText(objEW.getString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    binding.cashOutConfigTxt.setText(objEW.getString("hi_msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (Objects.equals(cashoutAdditionalData.getCashoutEligibilityStatus(), "NE")) {
            eligiblityCheck = false;
//            val properties = Properties()
//            val attribute = HashMap < String, Any>()
//            requireContext().captureAllEvents(requireContext(),
//                    Constants.CASHOUT_DISABLED_VIEWED,
//                    attribute,
//                    properties)
            binding.cashoutTxt.setVisibility(View.VISIBLE);
            binding.cashLayoutExp.setVisibility(View.GONE);
            binding.cashoutCardHoldRoot.setVisibility(View.GONE);
            if (Objects.equals(PrefrenceUtils.retriveLangData(requireActivity(), Constants.LANGUAGE), "en")) {
                try {
                    binding.cashOutConfigTxt.setText(objNE.getString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    binding.cashOutConfigTxt.setText(objNE.getString("hi_msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }




            if (cashOutDetails.getAmountEligible().toString().contains(".")) {
                try {
                    binding.setPriceExp.setText(getResources().getString(R.string.rupee)+" "+(cashOutDetails.getAmountEligible()).toString().substring(
                            0,
                            cashOutDetails.getAmountEligible().toString().indexOf(".")));
                } catch (Exception e) {
                    throw e;
                }
            } else {
                binding.setPriceExp.setText(getResources().getString(R.string.rupee)+" "+cashOutDetails.getAmountEligible().toString());
            }
    }

    private void getDatafromRemoteConfig() {
        trigger = new Gson().fromJson(PrefrenceUtils.retriveData(requireContext(), Constants.FEEDBACK_TRIGGERS_REMOTE),
                FeedbackTriggersModel.class
        );
    }

    private void clickListener() {

        binding.llHold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BottomSheetCashoutHold(
                        getActivity(),
                        cashOutDetails.getHoldDetails().getCompanyName(),
                        cashOutDetails.getHoldDetails().getCompanyIcon(),
                        cashOutDetails.getHoldDetails().getHoldMessage(),
                        cashOutDetails.getAmountEligibleLabel()
            ).show();
            }
        });
        binding.cashoutTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eligiblityCheck) {
                    // setUpInstrumentation()
                    new BottomSheetCashOutPurpose(
                            requireActivity(),
                            cashoutAdditionalData.getUserLevel(),
                            Objects.requireNonNull(cashOutDetails.getAmountEligibleLabel()),
                            null,
                            HistoryFragment.this,
                            Objects.requireNonNull(cashOutDetails.getAmountEligible()),
                            Objects.requireNonNull(cashOutDetails.getCashoutFixedFee()),
                            cashOutDetails.isCashoutFeeEnabled(),
                            Objects.requireNonNull(cashOutDetails.getCashoutFeePercentage()),
                            cashoutAdditionalData.getOrderRequiredToReachToNextLevel(),
                            cashoutAdditionalData.getDaysRequiredTOReachToNextLevel(),
                            cashoutAdditionalData.getCashoutEligibilityStatus(),
                            cashoutAdditionalData.getOrderReachToNextLevel(),
                            cashoutAdditionalData.getDaysReachToNextLevel(),
                            cashoutAdditionalData.getCashoutNextLevelPercentage()
                    ).show();
                }
            }

        });


        binding.sortParent.setOnClickListener(view -> {
            binding.sortParent.setEnabled(false);
            getBottomSheetSortData();
        });
        binding.filterParent.setOnClickListener(view -> {
            binding.filterParent.setEnabled(false);
            getBottomSheetFilterData();
        });
        binding.tvAll.setOnClickListener(view -> {
            type = "";
            getTransactionDetails(type);
            binding.viewOne.setVisibility(View.VISIBLE);
            binding.viewThree.setVisibility(View.GONE);
            binding.viewTwo.setVisibility(View.GONE);
            binding.tvMoneyCash.setTextColor(ContextCompat.getColor(requireActivity(), R.color.light_color_heading));
            binding.moneyOut.setTextColor(ContextCompat.getColor(requireActivity(), R.color.light_color_heading));
            binding.tvAll.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black_v2));
        });
        binding.tvMoneyCash.setOnClickListener(view -> {
            type = Constants.CREDIT;
            getTransactionDetails(type);
            binding.viewOne.setVisibility(View.GONE);
            binding.viewThree.setVisibility(View.GONE);
            binding.viewTwo.setVisibility(View.VISIBLE);
            binding.tvAll.setTextColor(ContextCompat.getColor(requireActivity(), R.color.light_color_heading));
            binding.moneyOut.setTextColor(ContextCompat.getColor(requireActivity(), R.color.light_color_heading));
            binding.tvMoneyCash.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black_v2));
        });
        binding.moneyOut.setOnClickListener(view -> {
            type = Constants.DEBIT;
            getTransactionDetails(type);
            binding.viewOne.setVisibility(View.GONE);
            binding.viewThree.setVisibility(View.VISIBLE);
            binding.viewTwo.setVisibility(View.GONE);
            binding.tvAll.setTextColor(ContextCompat.getColor(requireActivity(), R.color.light_color_heading));
            binding.tvMoneyCash.setTextColor(ContextCompat.getColor(requireActivity(), R.color.light_color_heading));
            binding.moneyOut.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black_v2));
        });
        binding.ivBackButton.setOnClickListener(view -> {
            requireActivity().onBackPressed();
        });
    }

    private void getBottomSheetSortData() {
        viewSharedViewModel.getTransactionDetailsFilterConstraints().observe(getViewLifecycleOwner(), transactionDetailsFilterConstraintsModel -> {
            if (transactionDetailsFilterConstraintsModel.getStatusCode() == 500) {
                Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_error_fragment);
            } else if (transactionDetailsFilterConstraintsModel.getStatusCode() >= 400 && transactionDetailsFilterConstraintsModel.getStatusCode() <= 499) {
                Toast.makeText(requireActivity(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            } else {
                sortedMapValue = new ArrayList<>();
                sortedMapValue.addAll(transactionDetailsFilterConstraintsModel.getSorts());
                if (sortedMapValue.size() > 0) {
                    showBottomSheet(sortedMapValue);
                } else {
                    Toast.makeText(requireActivity(), getString(R.string.sort_error_toast_message), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void getBottomSheetFilterData() {
        viewSharedViewModel.getTransactionDetailsFilterConstraints().observe(getViewLifecycleOwner(), transactionDetailsFilterConstraintsModel -> {
            if (transactionDetailsFilterConstraintsModel.getStatusCode() == 500) {
                Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_error_fragment);
            } else if (transactionDetailsFilterConstraintsModel.getStatusCode() >= 400 && transactionDetailsFilterConstraintsModel.getStatusCode() <= 499) {
                Toast.makeText(requireActivity(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            } else {
                filterModel = new ArrayList<>();
                filterModel.addAll(transactionDetailsFilterConstraintsModel.getFilters());
                if (filterModel.size() > 0) {
                    showFilterBottomSheet(filterModel);
                } else {
                    Toast.makeText(requireActivity(), getString(R.string.please_wait_for_sorted_items), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void showFilterBottomSheet(ArrayList<TransactionDetailsFilterConstraintsModel.Filter> filterData) {
        mBottomSheetDialogFilter = new BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialogTheme);
        View sheetView = getLayoutInflater().inflate(R.layout.filter_bottom_sheet, null);
        RecyclerView rvFilterDateList = sheetView.findViewById(R.id.rv_period_filter);
        RecyclerView rvFilterCompanyList = sheetView.findViewById(R.id.rv_selected_company_filter);
        RelativeLayout tvCompanyAction = sheetView.findViewById(R.id.apply_action_bottom_sheet);
        RelativeLayout tvCompanyCancel = sheetView.findViewById(R.id.cancell_action_bottom_sheet);
        TextView tvSelectedCompany = sheetView.findViewById(R.id.tv_selected_company);
        ArrayList<TransactionDetailsFilterConstraintsModel.Filter.Item> filterArrayList = new ArrayList<>();
        ArrayList<TransactionDetailsFilterConstraintsModel.Filter.Item> filterArrayListCompany = new ArrayList<>();
        for (int i = 0; i < filterData.size(); i++) {
            if (filterData.get(i).getType().equalsIgnoreCase(Constants.DATE)) {
                filterArrayList.addAll(filterData.get(i).getItems());
            }
            if (filterData.get(i).getName().equalsIgnoreCase(Constants.SELECT_COMPANY)) {
                filterArrayListCompany.addAll(filterData.get(i).getItems());
            }
        }
        if (filterArrayListCompany.size() > 0) {
            tvSelectedCompany.setVisibility(View.VISIBLE);
        } else {
            tvSelectedCompany.setVisibility(View.GONE);
        }
        tvCompanyAction.setOnClickListener(view -> {
            setInstrumentation();
            JsonArray jsonFilterArray = new JsonArray();
            jsonFilterArray.add(singleSelectedFilter);
            //getFilteredData(1, 20, type, getFilteredJsonArray(jsonFilterArray));
            getFilteredData(1, 20, type, jsonFilterArray);
        });
        tvCompanyCancel.setOnClickListener(view -> {
            JsonArray jsonFilterArray = new JsonArray();
            //getFilteredData(1, 20, type, getFilterdJsonArrayClear(jsonFilterArray));
            getFilteredData(1, 20, type, jsonFilterArray);
            mBottomSheetDialogFilter.dismiss();
        });
        filterListAdapter = new FilterListAdapter(requireActivity(), filterArrayList, Constants.DATE, HistoryFragment.this);
        filterCompanyListAdapter = new FilterListAdapter(requireActivity(), filterArrayListCompany, Constants.DATE, HistoryFragment.this);
        rvFilterDateList.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
        rvFilterDateList.setAdapter(filterListAdapter);
        rvFilterCompanyList.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
        rvFilterCompanyList.setAdapter(filterCompanyListAdapter);
        mBottomSheetDialogFilter.setContentView(sheetView);
        mBottomSheetDialogFilter.show();
        binding.filterParent.setEnabled(true);
    }

    private void setInstrumentation() {
        Bundle bundle = new Bundle();
        MoEInAppHelper.getInstance().showInApp(requireActivity());
        Properties properties = new Properties();
        MoEHelper.getInstance(requireActivity()).trackEvent(Constants.FILTER_APPLIED, properties);
        fa.logEvent(Constants.FILTER_APPLIED, bundle);
    }


    private JsonArray getFilteredJsonArray(JsonArray jsonFilterArry) {
//        for (int i = 0; i < filterListAdapter.getJsonArray().length(); i++) {
//            try {
//                jsonFilterArry.add(filterListAdapter.getJsonArray().get(i).toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        for (int i = 0; i < filterCompanyListAdapter.getJsonArray().length(); i++) {
//            try {
//                jsonFilterArry.add(filterCompanyListAdapter.getJsonArray().get(i).toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }


        for (int i = 0; i < valuesSelectedList.size(); i++) {
            jsonFilterArry.add(valuesSelectedList.get(i));
        }
        if (jsonFilterArry.size() > 0) {
            binding.filterAppliedCount.setText(jsonFilterArry.size() + getString(R.string.applied));
        } else {
            binding.filterAppliedCount.setText("");
        }
        return jsonFilterArry;
    }

    private JsonArray getFilterdJsonArrayClear(JsonArray jsonFilterArry) {
        valuesSelectedList.clear();
        binding.filterAppliedCount.setText("");
        return jsonFilterArry;
    }

    private void getFilteredData(int startPage, int perPage, String type, JsonArray jsonFilterArraySelected) {
        dialogLoader.show();
        JsonObject data = new JsonObject();
        JsonArray jsonSortArray = new JsonArray();
        JsonArray jsonFilterArray = new JsonArray();
        jsonFilterArray.addAll(jsonFilterArraySelected);
        data.addProperty(Constants.PAGE, startPage);
        data.addProperty(Constants.PER_PAGE, perPage);
        data.addProperty(Constants.TYPE, type);
        data.add(Constants.FILTERS, jsonFilterArray);
        data.add(Constants.SORTS, jsonSortArray);
        viewSharedViewModel.getTransactionDetails(data).observe(getViewLifecycleOwner(), earnDataModel -> {
            if (earnDataModel.getStatusCode() >= 400 && earnDataModel.getStatusCode() <= 499) {
                dialogLoader.dismiss();
                Toast.makeText(requireContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            } else if (earnDataModel.getStatusCode() == 500) {
                dialogLoader.dismiss();
                Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_error_fragment);
            } else {
                dialogLoader.dismiss();
                mBottomSheetDialogFilter.dismiss();
                binding.money.setText(earnDataModel.getWalletBalanceLabel());
                transactionDetailsModels = new ArrayList<>();
                transactionDetailsModels.addAll(earnDataModel.getTransactions());
                binding.rvHistory.setAdapter(new CompleteHistoryDetailsAdapter(requireActivity(), transactionDetailsModels));
            }
        });
    }

    private void apiEarnInfo() {
        dialogLoader.show();
        //       viewEarnViewModel.getGetEarnList().collect()
//            when (it) {
//                is ApiState.Success -> {
//        viewEarnViewModel.getGetEarnList().collect((earnDataModelApiState, continuation) -> {
//
//        }
//        }
//                })
    }

    private void getTransactionDetails(String type) {
        dialogLoader.show();
        JsonObject data = new JsonObject();
        JsonArray jsonSortArray = new JsonArray();
        if (!sortBy.equalsIgnoreCase("")) {
            jsonSortArray.add(sortBy);
        }
        JsonArray jsonFilterArray = new JsonArray();
        data.addProperty(Constants.PAGE, 1);
        data.addProperty(Constants.PER_PAGE, 40);
        data.addProperty(Constants.TYPE, type);
        data.add(Constants.FILTERS, jsonFilterArray);
        data.add(Constants.SORTS, jsonSortArray);
        viewSharedViewModel.getTransactionDetails(data).observe(getViewLifecycleOwner(), earnDataModel -> {
            if (earnDataModel.getStatusCode() >= 400 && earnDataModel.getStatusCode() <= 499) {
                dialogLoader.dismiss();
                Toast.makeText(requireContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            } else if (earnDataModel.getStatusCode() == 500) {
                dialogLoader.dismiss();
                Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_error_fragment);
            } else {
                dialogLoader.dismiss();
                binding.money.setText(earnDataModel.getWalletBalanceLabel());
                transactionDetailsModels = new ArrayList<>();
                transactionDetailsModels.addAll(earnDataModel.getTransactions());
                binding.rvHistory.setAdapter(new CompleteHistoryDetailsAdapter(requireActivity(), transactionDetailsModels));
                setInstrumentationHistroyView();
            }
        });
    }

    private void showBottomSheet(ArrayList<TransactionDetailsFilterConstraintsModel.Sort> sortedMapValue) {
        mBottomSheetDialog = new BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialogTheme);
        View sheetView = getLayoutInflater().inflate(R.layout.sort_bottom_sheet, null);
        RecyclerView rvSortList = sheetView.findViewById(R.id.rv_sortedListData);
        rvSortList.setAdapter(new SortListAdapter(requireActivity(), sortedMapValue, HistoryFragment.this));
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
        binding.sortParent.setEnabled(true);
    }

    @Override
    public void sortSelectedData(String key, String value) {
        mBottomSheetDialog.dismiss();
        binding.sortTypeHeading.setText(value);
        sortBy = key;
        Bundle bundle = new Bundle();
        MoEInAppHelper.getInstance().showInApp(requireActivity());
        Properties properties = new Properties();
        MoEHelper.getInstance(requireActivity()).trackEvent(Constants.SORT_APPLIED, properties);
        fa.logEvent(Constants.SORT_APPLIED, bundle);
        getTransactionDetailsAfterSort(type, sortBy);
    }

    @SuppressLint("SetTextI18n")
    private void getTransactionDetailsAfterSort(String type, String key) {

        dialogLoader.show();
        JsonObject data = new JsonObject();
        JsonArray jsonSortArray = new JsonArray();
        jsonSortArray.add(key);
        JsonArray jsonFilterArray = new JsonArray();
        data.addProperty(Constants.PAGE, 1);
        data.addProperty(Constants.PER_PAGE, 20);
        data.addProperty(Constants.TYPE, type);
        data.add(Constants.FILTERS, jsonFilterArray);
        data.add(Constants.SORTS, jsonSortArray);
        viewSharedViewModel.getTransactionDetails(data).observe(getViewLifecycleOwner(), earnDataModel -> {
            if (earnDataModel.getStatusCode() >= 400 && earnDataModel.getStatusCode() <= 499) {
                dialogLoader.dismiss();
                Toast.makeText(requireContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            } else if (earnDataModel.getStatusCode() == 500) {
                dialogLoader.dismiss();
                Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_error_fragment);
            } else {
                dialogLoader.dismiss();
                binding.money.setText(getString(R.string.rupee) + earnDataModel.getWalletBalance());
                transactionDetailsModels = new ArrayList<>();
                transactionDetailsModels.addAll(earnDataModel.getTransactions());
                binding.rvHistory.setAdapter(new CompleteHistoryDetailsAdapter(requireActivity(), transactionDetailsModels));
            }
            // Log.d("EarnFragment", "onChanged: " + earnDataModel.getTransactions().get(0).getAmount());
        });

    }

    public void initLoader(Context context) {
        dialogLoader = new Dialog(context);
        dialogLoader.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogLoader.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLoader.setCancelable(false);
        dialogLoader.setContentView(R.layout.layout_loader);
        ImageView imageViewAnimation = dialogLoader.findViewById(R.id.animate_icon);
        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(2000);
        rotate.setInterpolator(new LinearInterpolator());
        imageViewAnimation.startAnimation(rotate);
    }


    private void setInstrumentationHistroyView() {
        Bundle bundle = new Bundle();
        MoEInAppHelper.getInstance().showInApp(requireActivity());
        Properties properties = new Properties();
        MoEHelper.getInstance(requireActivity()).trackEvent("txn_history_viewed", properties);
        fa.logEvent("txn_history_viewed", bundle);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (trigger != null) {
                    for (int i = 0; i < trigger.getTrigger().size(); i++) {
                        if (!PrefrenceUtils.retriveDataInBoolean(getContext(), Constants.TXN_HISTORY)) {
                            if (PrefrenceUtils.retriveDataInBoolean(getContext(), Constants.ISFEEDBACKSESSION)) {
                                if (trigger.getTrigger().get(i).getTrigger_event().equalsIgnoreCase("txn_history_viewed")) {
                                    new FeedbackBottomsheet(requireContext(), "txn").show();
                                    PrefrenceUtils.insertDataInBoolean(requireContext(), Constants.ISFEEDBACKSESSION, false);
                                }
                            } else {
                                return;
                            }
                        } else {
                            return;
                        }

                    }
                }
            }
        }, 3000);

    }

    private void getRemoteConfigDataForUpdate() {
        viewEarnViewModel.getRemoteConfigDataForUpdate();
    }
}