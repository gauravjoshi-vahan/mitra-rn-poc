package com.vahan.mitra_playstore.view.insurance.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.freshchat.consumer.sdk.Freshchat
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.moengage.inapp.MoEInAppHelper
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentInsuranceBinding
import com.vahan.mitra_playstore.models.InsuranceModel
import com.vahan.mitra_playstore.models.InsureModelClass
import com.vahan.mitra_playstore.models.StoreInfoModel
import com.vahan.mitra_playstore.network.SharedViewModel
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.adapter.InsuranceAdapter
import java.text.SimpleDateFormat
import java.util.*

class InsuranceFragment : Fragment() {
    private var fa: FirebaseAnalytics? = null
    private var ff: FirebaseFirestore? = null
    private var _binding: FragmentInsuranceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentInsuranceBinding.inflate(inflater, container, false)
        Constants.checkEarnFragmentState = false
        initView()
        return binding.root
    }

    private fun initView() {
        val model = Gson().fromJson(PrefrenceUtils.retriveData(
            activity, Constants.INSURANCE_REMOTE_CONFIG), InsureModelClass::class.java)
        UXCam.setAutomaticScreenNameTagging(false)
        UXCam.tagScreenName(Constants.INSURANCE_FRAGMENT2)
        ff = FirebaseFirestore.getInstance()
        fa = FirebaseAnalytics.getInstance(requireActivity())
        clickListener()
        checkUserReference()
        setViewLogic(model)
    }

    private fun setViewLogic(model: InsureModelClass) {
        if (PrefrenceUtils.retriveData(activity, Constants.INSURANCE_ELIGIBILITY)
                .equals("E", ignoreCase = true)
        ) {
            Constants.hideAndShowTopBar = true
            setInstrumentationForEligibleStatus()
            binding.eligibleContainer.visibility = View.VISIBLE
            binding.notEligibleContainer.visibility = View.GONE
            checkForInsuranceEligible()
        } else if (PrefrenceUtils.retriveData(activity, Constants.INSURANCE_ELIGIBILITY)
                .equals("EC", ignoreCase = true)
        ) {
            Constants.hideAndShowTopBar = false
            binding.eligibleContainer.visibility = View.GONE
            binding.notEligibleContainer.visibility = View.VISIBLE
            if (PrefrenceUtils.retriveLangData(activity, Constants.LANGUAGE)
                    .equals("en", ignoreCase = true)
            ) {
                binding.tvCommingSoon.text = model.ec.title
                binding.tvDescComingSon.text = model.ec.desc
            } else {
                binding.tvCommingSoon.text = model.ec.titleHi
                binding.tvDescComingSon.text = model.ec.hiDesc
            }
            if (model.ec.button == 1) {
                binding.userStoreInsure.visibility = View.VISIBLE
                if (PrefrenceUtils.retriveLangData(activity, Constants.LANGUAGE)
                        .equals("en", ignoreCase = true)
                ) {
                    binding.btnInterested.text = model.ec.buttonText
                } else {
                    binding.btnInterested.text = model.ec.buttonTextHi
                }
            } else {
                binding.userStoreInsure.visibility = View.GONE
            }
        } else if (PrefrenceUtils.retriveData(activity, Constants.INSURANCE_ELIGIBILITY)
                .equals("EW", ignoreCase = true)
        ) {
            Constants.hideAndShowTopBar = false
            binding.eligibleContainer.visibility = View.GONE
            binding.notEligibleContainer.visibility = View.VISIBLE
            if (PrefrenceUtils.retriveLangData(activity, Constants.LANGUAGE)
                    .equals("en", ignoreCase = true)
            ) {
                binding.tvCommingSoon.text = model.ew.title
                binding.tvDescComingSon.text = model.ew.desc
            } else {
                binding.tvCommingSoon.text = model.ew.titleHi
                binding.tvDescComingSon.text = model.ew.descHi
            }
            if (model.ew.button == 1) {
                binding.userStoreInsure.visibility = View.VISIBLE
                if (PrefrenceUtils.retriveLangData(activity, Constants.LANGUAGE)
                        .equals("en", ignoreCase = true)
                ) {
                    binding.btnInterested.text = model.ew.buttonText
                } else {
                    binding.btnInterested.text = model.ew.buttonTextHi
                }
            } else {
                binding.userStoreInsure.visibility = View.GONE
            }
        } else if (PrefrenceUtils.retriveData(activity, Constants.INSURANCE_ELIGIBILITY)
                .equals("NE", ignoreCase = true)
        ) {
            Constants.hideAndShowTopBar = false
            binding.eligibleContainer.visibility = View.GONE
            binding.notEligibleContainer.visibility = View.VISIBLE
            binding.tvCommingSoon.text = model.ne.title
            binding.tvDescComingSon.text = model.ne.desc
            if (model.ne.button == 1) {
                binding.userStoreInsure.visibility = View.VISIBLE
                if (PrefrenceUtils.retriveLangData(activity, Constants.LANGUAGE)
                        .equals("en", ignoreCase = true)
                ) {
                    binding.btnInterested.text = model.ne.buttonText
                } else {
                    binding.btnInterested.text = model.ne.buttonTextHi
                }
            } else {
                binding.userStoreInsure.visibility = View.GONE
            }
        }
    }

    private fun setInstrumentationForEligibleStatus() {
        val bundle = Bundle()
        MoEInAppHelper.getInstance().showInApp(requireContext())
        val properties = Properties()
        MoEHelper.getInstance(requireContext()).trackEvent(Constants.INSURE_LIST_VIEWED, properties)
        fa!!.logEvent(Constants.INSURE_LIST_VIEWED, bundle)
        UXCam.logEvent(Constants.INSURE_LIST_VIEWED)
        BlitzLlamaSDK.getSdkManager(requireContext()).triggerEvent(Constants.INSURE_LIST_VIEWED)
        val attribute = HashMap<String,Any>()
        attribute[Constants.INSURE_LIST_VIEWED] = ""
        Freshchat.trackEvent(requireContext(),Constants.INSURE_LIST_VIEWED,attribute)
    }

    private fun clickListener() {
        binding.userStoreInsure.setOnClickListener { uploadDetailInFirestore() }
        binding.dynamicLogo.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun checkForInsuranceEligible() {
        val viewSharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        viewSharedViewModel.checkForInsuranceEligible()
            .observe(requireActivity()) { insuranceModel: InsuranceModel ->
                if (insuranceModel.statusCode == 500) {
                    Navigation.findNavController(binding.root).navigate(R.id.nav_error_fragment)
                } else if (insuranceModel.statusCode in 400..499) {
                    Toast.makeText(
                        requireActivity(),
                        R.string.something_went_wrong,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (insuranceModel.premiumInfo.size > 0) {
                        setPremiumDetailsInInsurance(
                            insuranceModel.premiumInfo,
                            insuranceModel.userName
                        )
                    }
                }
            }
    }

    private fun setPremiumDetailsInInsurance(premiumInfo: List<InsuranceModel.PremiumInfo>, name: String) {
        binding.premiumDetails.adapter = InsuranceAdapter(activity, premiumInfo, name)
    }

    private fun checkUserReference() {
        val docRef = ff!!.collection(Constants.INSURE_USER_INFO)
            .document(PrefrenceUtils.retriveData(activity,
                Constants.PHONENUMBER))
        docRef.get().addOnCompleteListener { task: Task<DocumentSnapshot?> ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null) {
                    if (document.exists()) {
                        binding.userStoreInsure.visibility = View.GONE
                    } else {
                        binding.userStoreInsure.visibility = View.VISIBLE
                        Log.d("Not_Available", "No such document")
                    }
                } else {
                    Toast.makeText(requireContext(),
                        R.string.something_went_wrong,
                        Toast.LENGTH_LONG).show()
                }
            } else {
                Log.d("Not_Available", "get failed with ", task.exception)
            }
        }
    }

    private fun uploadDetailInFirestore() {
        setInstrumentationUploadDataInFireStore()
        try {
            ff!!.collection(Constants.INSURE_USER_INFO)
                .document(PrefrenceUtils.retriveData(activity, Constants.PHONENUMBER))
                .set(StoreInfoModel(
                    PrefrenceUtils.retriveData(activity, Constants.USERNAME),
                    PrefrenceUtils.retriveData(activity, Constants.PHONENUMBER),
                    "insure", createAt(System.currentTimeMillis()),
                    "userRef/" + PrefrenceUtils.retriveData(activity, Constants.PHONENUMBER)))
            showAlertDialog()
            binding.userStoreInsure.visibility = View.GONE
        } catch (e: Exception) {
            Log.d("All_Contacts", "Error adding document3" + e.message.toString())
        }
    }

    private fun setInstrumentationUploadDataInFireStore() {
        val bundle = Bundle()
        fa!!.logEvent(Constants.INSURE_INTERESTED, bundle)
        val properties = Properties()
        MoEHelper.getInstance(requireActivity()).trackEvent(Constants.INSURE_INTERESTED, properties)
        UXCam.logEvent(Constants.INSURE_INTERESTED)
        val attribute = HashMap<String,Any>()
        attribute[Constants.INSURE_INTERESTED] = ""
        Freshchat.trackEvent(requireContext(),Constants.INSURE_INTERESTED,attribute)
    }

    private fun showAlertDialog() {
        val ctDialog = Dialog(requireContext())
        ctDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        ctDialog.setContentView(R.layout.response_saved_dialogue)
        ctDialog.setCanceledOnTouchOutside(true)
        ctDialog.setCancelable(true)
        ctDialog.show()
    }

    private fun createAt(currentTimeMillis: Long): String {
        val simpleDateFormat =
            SimpleDateFormat(getString(R.string.date_format2), Locale.US)
        simpleDateFormat.timeZone = TimeZone.getTimeZone(Constants.UTC)
        return simpleDateFormat.format(Date(currentTimeMillis))
    }

    override fun onResume() {
        super.onResume()
        val bundle = Bundle()
        fa!!.logEvent(Constants.INSURE_PAGE_VIEWED, bundle)
        val properties = Properties()
        MoEHelper.getInstance(requireContext()).trackEvent(Constants.INSURE_PAGE_VIEWED, properties)
        val attribute = HashMap<String,Any>()
        attribute[Constants.INSURE_PAGE_VIEWED] = ""
        Freshchat.trackEvent(requireContext(),Constants.INSURE_PAGE_VIEWED,attribute)
    }
}