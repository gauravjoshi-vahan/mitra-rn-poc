package com.vahan.mitra_playstore.view.earn.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.google.firebase.analytics.FirebaseAnalytics
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentCashoutPurposeBinding
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.HelperMethod
import com.vahan.mitra_playstore.utils.ItemOffsetDecoration
import com.vahan.mitra_playstore.view.earn.model.Purpose
import com.vahan.mitra_playstore.view.earn.view.adapter.CashoutPurposeAdapter

class CashoutPurposeFragment : Fragment() {
    var price = ""
    var eprice = ""
    var cashoutEnabled = false
    var cashoutFeePercentage = ""
    var cashoutFixedFee = 0
    var purpose = ""
    var orderReachToNextLevel = 0
    var tripsReachToNextLevel = 0
    var orderReachToNextLevel1: Boolean = false
    var daysReachToNextLevel: Boolean = false
    var cashoutNextLevelPercentage: Int = 0
    private lateinit var fa: FirebaseAnalytics

    private lateinit var binding: FragmentCashoutPurposeBinding
    var purposeList: ArrayList<Purpose> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_cashout_purpose, container, false)
        initView()
        return binding.root
    }


    private fun initView() {
        fa = FirebaseAnalytics.getInstance(requireContext())
        getPurpose()
        clickListener()
        setInstrumentation()
    }

    private fun setInstrumentation() {
        // cashout_purpose_viewed
        val bundle = Bundle()
        val properties = Properties()
        MoEHelper.getInstance(requireContext())
            .trackEvent(Constants.CASHOUT_PURPOSE_VIEWED, properties)
        BlitzLlamaSDK.getSdkManager(requireContext()).triggerEvent(Constants.CASHOUT_PURPOSE_VIEWED)
        fa.logEvent(Constants.CASHOUT_PURPOSE_VIEWED, bundle)
        UXCam.logEvent(Constants.CASHOUT_PURPOSE_VIEWED)
    }

    private fun clickListener() {
        binding.saveButtonOne.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.PRICE, price)
            bundle.putString(Constants.EPRICE, eprice)
            bundle.putInt(Constants.BUNDLE_CASHOUT_FEE_LABEL, cashoutFixedFee)
            bundle.putString(Constants.BUNDLE_CASHOUT_FEE_PERCENTAGE, cashoutFeePercentage)
            bundle.putString(Constants.BUNDLE_CASHOUT_PURPOSE, purpose)
            bundle.putInt("orderReachToNextLevel", orderReachToNextLevel)
            bundle.putInt("tripsReachToNextLevel", tripsReachToNextLevel)
            bundle.putBoolean("orderReachToNextLevel1", orderReachToNextLevel1)
            bundle.putBoolean("daysReachToNextLevel", daysReachToNextLevel)
            bundle.putInt("cashoutNextLevelPercentage", cashoutNextLevelPercentage)
            if (cashoutEnabled != null) {
                bundle.putBoolean(Constants.BUNDLE_CASHOUT_FEE_PERCENTAGE_ENABLED, cashoutEnabled)
            }
            Navigation.findNavController(binding.rvContainer)
                .navigate(R.id.transaction_layout, bundle)

            setClickedInstrumentation()
        }
        binding.backArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setClickedInstrumentation() {
        // cashout_purpose_selected
        val bundle = Bundle()
        bundle.putString(Constants.BUNDLE_CASHOUT_PURPOSE, purpose)
        val properties = Properties()
        properties.addAttribute(Constants.BUNDLE_CASHOUT_PURPOSE, purpose)
        MoEHelper.getInstance(requireContext())
            .trackEvent(Constants.BUNDLE_cashout_purpose_selected, properties)
        BlitzLlamaSDK.getSdkManager(requireContext())
            .triggerEvent(Constants.BUNDLE_cashout_purpose_selected)
        fa.logEvent(Constants.BUNDLE_cashout_purpose_selected, bundle)
        UXCam.logEvent(Constants.BUNDLE_cashout_purpose_selected)
    }

    private fun getPurpose() {
        purposeList.clear()
        purposeList.add(
            Purpose(
                Constants.HOME_UTILITIES,
                "https://uploads-a.vahan.co/wmRtiT-homeutilities.svg",
                "Home Utilities",
                "घरेलू उपयोगिताएँ"
            )
        )

        purposeList.add(
            Purpose(
                Constants.HOUSE_RENT,
                "https://uploads-a.vahan.co/drgRPZ-houserent.svg",
                "House Rent",
                "घर का किराया"
            )
        )
        purposeList.add(
            Purpose(
                Constants.FAMILY,
                "https://uploads-a.vahan.co/NiZiTZ-family.svg",
                "Family",
                "परिवार"
            )
        )
        purposeList.add(
            Purpose(
                Constants.VEHICLE,
                "https://uploads-a.vahan.co/t3g8MR-vehicle.svg",
                "Vehicle",
                "वाहन"
            )
        )
        purposeList.add(
            Purpose(
                Constants.SHOPPING,
                "https://uploads-a.vahan.co/ylLXVX-shopping.svg",
                "Shopping",
                "खरीदारी"
            )
        )
        purposeList.add(
            Purpose(
                Constants.FESTIVAL,
                "https://uploads-a.vahan.co/TyRDrh-festival.svg",
                "Festival",
                "त्यौहार"
            )
        )
        purposeList.add(
            Purpose(
                Constants.MEDICAL,
                "https://uploads-a.vahan.co/mSLw6V-medical.svg",
                "Medical",
                "मेडिकल"
            )
        )
        purposeList.add(
            Purpose(
                Constants.OTHER,
                "https://uploads-a.vahan.co/H3Jv0T-other.svg",
                "Other",
                "अन्य"
            )
        )

        binding.rvContainer.addItemDecoration(
            ItemOffsetDecoration(
                2,
                HelperMethod.dpToPx(10f, activity),
                true
            )
        )
        binding.rvContainer.layoutManager = GridLayoutManager(activity, 2)
        val getCashoutadapter = CashoutPurposeAdapter(
            requireActivity(),
            this@CashoutPurposeFragment,
            purposeList,
        )
        binding.rvContainer.adapter = getCashoutadapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.run {
            price = this.getString(Constants.PRICE).toString()
            cashoutEnabled = this.getBoolean(Constants.BUNDLE_CASHOUT_FEE_PERCENTAGE_ENABLED)
            cashoutFeePercentage = this.getString(Constants.BUNDLE_CASHOUT_FEE_PERCENTAGE)!!
            cashoutFixedFee = this.getInt(Constants.BUNDLE_CASHOUT_FEE_LABEL)
            tripsReachToNextLevel = this.getInt("tripsReachToNextLevel")
            orderReachToNextLevel = this.getInt("orderReachToNextLevel")
            orderReachToNextLevel1 = this.getBoolean("orderReachToNextLevel1")
            daysReachToNextLevel = this.getBoolean("daysReachToNextLevel")
            cashoutNextLevelPercentage = this.getInt("cashoutNextLevelPercentage")
            eprice = this.getString(Constants.EPRICE).toString()
            eprice = this.getString(Constants.EPRICE).toString()
        }
    }

    fun getPurposeSelectedData(selectedItem: String) {
        if (selectedItem != "") {
            binding.saveButtonOne.isEnabled = true
            binding.saveButtonOne.isClickable = true
            binding.saveButtonOne.setBackgroundResource(R.drawable.curver_corner_12)
            purpose = selectedItem
        } else {
            binding.saveButtonOne.isEnabled = false
            binding.saveButtonOne.isClickable = false
            binding.saveButtonOne.setBackgroundResource(R.drawable.curver_corner_12_disable)
        }
    }


}