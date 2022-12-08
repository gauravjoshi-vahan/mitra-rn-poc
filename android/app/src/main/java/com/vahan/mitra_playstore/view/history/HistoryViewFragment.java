package com.vahan.mitra_playstore.view.history;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.blitzllama.androidSDK.BlitzLlamaSDK;
import com.bumptech.glide.RequestBuilder;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.moe.pushlibrary.MoEHelper;
import com.moengage.core.Properties;
import com.moengage.inapp.MoEInAppHelper;
import com.uxcam.UXCam;
import com.vahan.mitra_playstore.R;
import com.vahan.mitra_playstore.databinding.FragmentHistoryViewBinding;
import com.vahan.mitra_playstore.model.TranscInfoModel;
import com.vahan.mitra_playstore.network.SharedViewModel;
import com.vahan.mitra_playstore.utils.Constants;
import com.vahan.mitra_playstore.utils.GlideApp;
import com.vahan.mitra_playstore.utils.SvgSoftwareLayerSetter;

import java.util.Objects;


public class HistoryViewFragment extends Fragment {

    private FragmentHistoryViewBinding binding;
    private SharedViewModel viewSharedViewModel;
    private ShimmerFrameLayout shimmerFrameLayout;
    private FirebaseAnalytics fa;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String id = getArguments().getString(Constants.TRANSACTION_ID);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history_view, container, false);
        viewSharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        shimmerFrameLayout = binding.shimmerViewContainer;
        showLoading();
        initView(id);
        Double.parseDouble("12");
        return binding.getRoot();
    }

    private void initView(String id) {
        fa = FirebaseAnalytics.getInstance(getActivity());
        clickListener();
        getTranscInfoBasedOnId(id);
    }

    private void clickListener() {
        binding.ivBackButton.setOnClickListener(view -> {
            requireActivity().onBackPressed();
        });
    }

    private void getTranscInfoBasedOnId(String id) {
        viewSharedViewModel.getTransactionDetailsInfo(id).observe(getViewLifecycleOwner(), transactionDetailInfoModel -> {
            if (transactionDetailInfoModel.getStatusCode() == 500) {
                Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_error_fragment);
            } else if (transactionDetailInfoModel.getStatusCode() >= 400 && transactionDetailInfoModel.getStatusCode() <= 499) {
                Toast.makeText(requireActivity(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            } else {
                hideLoading(transactionDetailInfoModel);
            }
        });


    }

    public void initLoader(Context context) {
        Dialog dialogLoader = new Dialog(context);
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

    private void showLoading() {
        binding.shimmerViewContainer.setVisibility(View.VISIBLE);
        binding.parentRootContainer.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmerAnimation();
    }

    private void hideLoading(TranscInfoModel transactionDetailInfoModel) {
        shimmerFrameLayout.stopShimmerAnimation();
        binding.shimmerViewContainer.setVisibility(View.GONE);
        binding.parentRootContainer.setVisibility(View.VISIBLE);
        updateUI(transactionDetailInfoModel);
    }

    @SuppressLint("ResourceType")
    private void updateUI(TranscInfoModel transactionDetailInfoModel) {
        setEventBasedOnType(transactionDetailInfoModel);
        binding.backButtonLabel.setText(transactionDetailInfoModel.getLabel());
        binding.tvRs.setText(transactionDetailInfoModel.getAmountLabel());
        binding.tvDate.setText(transactionDetailInfoModel.getDateTimeStr());
        binding.tvInfoMessage.setText(transactionDetailInfoModel.getTransactionMessage());
        binding.tvExtraInfoMessage.setText(transactionDetailInfoModel.getTransactionMessage());
        binding.tvCount.setText(transactionDetailInfoModel.getInfo().getValue());
        binding.tvDateValue.setText(transactionDetailInfoModel.getInfo().getLabel());
        binding.tvBrand.setText(transactionDetailInfoModel.getTitle());
        if (!transactionDetailInfoModel.getInfo().getTitle().equalsIgnoreCase("")) {
            binding.tvOrder.setText(transactionDetailInfoModel.getInfo().getDesc());
        } else {
            binding.tvOrder.setText(transactionDetailInfoModel.getTransactionMessage());
        }

        binding.tvTypeOrder.setText(transactionDetailInfoModel.getInfo().getTitle());
        binding.middleConstraint.setBackgroundColor(Color.parseColor(transactionDetailInfoModel.getBackgroundColor()));
        binding.middleConstraint.setAlpha(50);

        Drawable unwrappedDrawable = AppCompatResources.getDrawable(getActivity(), R.drawable.ic_mitra_blue);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(transactionDetailInfoModel.getColor()));
        binding.imageViewcenterData.setImageDrawable(unwrappedDrawable);
        RequestBuilder<PictureDrawable> requestBuilder = GlideApp.with(getActivity())
                .as(PictureDrawable.class)
                .error(R.drawable.dialog_icon)
                .listener(new SvgSoftwareLayerSetter());
        Uri uri = Uri.parse(transactionDetailInfoModel.getIcon());
        requestBuilder.load(uri).into(binding.imgSalary);
        requestBuilder =
                GlideApp.with(getActivity())
                        .as(PictureDrawable.class)
                        .listener(new SvgSoftwareLayerSetter());
        Uri uri1 = Uri.parse(transactionDetailInfoModel.getInfo().getIcon());
        requestBuilder.load(uri1).into(binding.imgOrder);
        if (!Objects.equals(transactionDetailInfoModel.getUtrNumber(), null) && !Objects.equals(transactionDetailInfoModel.getUtrNumber(), "")) {
            binding.utrParent.setVisibility(View.VISIBLE);
            binding.tvUTRNo.setText(transactionDetailInfoModel.getUtrNumber());
        } else {
            binding.utrParent.setVisibility(View.GONE);
        }
        if (!(Objects.requireNonNull(transactionDetailInfoModel.getStatusLabel())).equalsIgnoreCase("") && !transactionDetailInfoModel.getStatusLabel().equalsIgnoreCase("Failed")) {
            binding.tvStatus.setVisibility(View.VISIBLE);
            binding.tvStatus.setText(transactionDetailInfoModel.getStatusLabel());
            GradientDrawable shape = new GradientDrawable();
            shape.setCornerRadius(8);
            shape.setColor(Color.parseColor(transactionDetailInfoModel.getStatusColor()));
//            shape.setColor(Color.parseColor("#04A7BD"));
            binding.tvStatusParent.setBackground(shape);
        } else {
            binding.tvStatus.setVisibility(View.GONE);
        }
        if (Objects.requireNonNull(transactionDetailInfoModel.getStatus()).equalsIgnoreCase("Failed")) {
            binding.imgOrder.setVisibility(View.GONE);
            binding.tvDateValue.setVisibility(View.GONE);
            binding.tvCount.setVisibility(View.GONE);
            binding.tvOrder.setGravity(View.TEXT_ALIGNMENT_CENTER);
        } else if (transactionDetailInfoModel.getInfo().getTitle().equalsIgnoreCase("")) {
            binding.imgOrder.setVisibility(View.GONE);
            binding.tvDateValue.setVisibility(View.GONE);
            binding.tvCount.setVisibility(View.GONE);
            binding.tvOrder.setGravity(Gravity.CENTER);
        } else {
            binding.imgOrder.setVisibility(View.VISIBLE);
            binding.tvCount.setVisibility(View.VISIBLE);
            binding.tvOrder.setGravity(Gravity.START);
            if (transactionDetailInfoModel.getInfo().getLabel().equalsIgnoreCase("")) {
                binding.tvDateValue.setVisibility(View.GONE);
            } else {
                binding.tvDateValue.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setEventBasedOnType(TranscInfoModel transactionDetailInfoModel) {
        Bundle bundle = new Bundle();
        MoEInAppHelper.getInstance().showInApp(getActivity());
        Properties properties = new Properties();
        if (Objects.requireNonNull(transactionDetailInfoModel.getType()).equalsIgnoreCase("credit")) {
            MoEHelper.getInstance(getActivity()).trackEvent(Constants.TXN_HISTORY_CREDIT_VIEWED, properties);
            fa.logEvent(Constants.TXN_HISTORY_CREDIT_VIEWED, bundle);
            UXCam.logEvent(Constants.TXN_HISTORY_CREDIT_VIEWED);
            BlitzLlamaSDK.getSdkManager(requireContext()).triggerEvent(Constants.TXN_HISTORY_CREDIT_VIEWED);
        } else if (transactionDetailInfoModel.getType().equalsIgnoreCase(Constants.FAILED)) {
            MoEHelper.getInstance(getActivity()).trackEvent(Constants.TXN_HISTORY_FAILED, properties);
            fa.logEvent(Constants.TXN_HISTORY_FAILED, bundle);
            UXCam.logEvent(Constants.TXN_HISTORY_FAILED);
            BlitzLlamaSDK.getSdkManager(requireContext()).triggerEvent(Constants.TXN_HISTORY_FAILED);
        }

    }

}