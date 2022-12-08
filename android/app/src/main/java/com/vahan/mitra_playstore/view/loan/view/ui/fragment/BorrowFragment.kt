package com.vahan.mitra_playstore.view.loan.view.ui.fragment

import android.app.Dialog
import android.graphics.Color
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.firebase.firestore.DocumentSnapshot
import com.moe.pushlibrary.MoEHelper
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.freshchat.consumer.sdk.Freshchat
import com.google.android.gms.tasks.Task
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R

import com.vahan.mitra_playstore.databinding.FragmentBorrowBinding
import com.vahan.mitra_playstore.models.LoanConditionModel
import com.vahan.mitra_playstore.models.StoreInfoModel
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.MainActivity
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class BorrowFragment : Fragment() {
    private var fa: FirebaseAnalytics? = null
    private lateinit var binding: FragmentBorrowBinding
    private var ff: FirebaseFirestore? = null
    private var loanConditionModel: LoanConditionModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_borrow,
            container,
            false
        )
        Constants.checkEarnFragmentState = false
        initView()
        return binding.root
    }

    private fun initView() {
        fa = FirebaseAnalytics.getInstance(requireContext())
        ff = FirebaseFirestore.getInstance()
        UXCam.setAutomaticScreenNameTagging(false)
        UXCam.tagScreenName(Constants.BORROW_FRAGMENT)
        binding.userStoreBorrow.setOnClickListener { view: View? -> uploadDetailInFirestore() }
        checkUserReference()
        dataFromRemoteConfig
    }

    private val dataFromRemoteConfig: Unit
        get() {
            loanConditionModel = Gson().fromJson(
                PrefrenceUtils.retriveData(
                    activity,
                    Constants.LOAN_TERM_REMOTE_CONFIG
                ), LoanConditionModel::class.java
            )
            updateUi()
        }

    private fun updateUi() {
        if ((requireActivity() as MainActivity).loanStatusCheck.equals("NE", ignoreCase = true)) {
            if (PrefrenceUtils.retriveLangData(requireActivity(), Constants.LANGUAGE)
                    .equals("en", ignoreCase = true)
            ) {
                binding.tvCommingSoon.text = loanConditionModel!!.ne.title
                binding.tvDescComingSon.text = loanConditionModel!!.ne.desc
                binding.tvButtonApply.text = loanConditionModel!!.ne.buttonText
            } else {
                binding.tvCommingSoon.text = loanConditionModel!!.ne.titleHi
                binding.tvDescComingSon.text = loanConditionModel!!.ne.descHi
                binding.tvButtonApply.text = loanConditionModel!!.ne.buttonTextHi
            }
        } else if ((requireActivity() as MainActivity).loanStatusCheck.equals(
                "EC",
                ignoreCase = true
            )
        ) {
            if (PrefrenceUtils.retriveLangData(requireActivity(), Constants.LANGUAGE)
                    .equals("en", ignoreCase = true)
            ) {
                binding.tvCommingSoon.text = loanConditionModel!!.ec.title
                binding.tvDescComingSon.text = loanConditionModel!!.ec.desc
                binding.tvButtonApply.text = loanConditionModel!!.ec.buttonText
            } else {
                binding.tvCommingSoon.text = loanConditionModel!!.ec.titleHi
                binding.tvDescComingSon.text = loanConditionModel!!.ec.hiDesc
                binding.tvButtonApply.text = loanConditionModel!!.ec.buttonTextHi
            }
        } else if ((requireActivity() as MainActivity).loanStatusCheck.equals(
                "EW",
                ignoreCase = true
            )
        ) {
            if (PrefrenceUtils.retriveLangData(requireActivity(), Constants.LANGUAGE)
                    .equals("en", ignoreCase = true)
            ) {
                binding.tvCommingSoon.text = loanConditionModel!!.ew.title
                binding.tvDescComingSon.text = loanConditionModel!!.ew.desc
                binding.tvButtonApply.text = loanConditionModel!!.ew.buttonText
            } else {
                binding.tvCommingSoon.text = loanConditionModel!!.ew.titleHi
                binding.tvDescComingSon.text = loanConditionModel!!.ew.descHi
                binding.tvButtonApply.text = loanConditionModel!!.ew.buttonTextHi
            }
        }
    }

    private fun checkUserReference() {
        val docRef = ff!!.collection(Constants.BORROW_USER_INFO).document(
            PrefrenceUtils.retriveData(
                activity, Constants.PHONENUMBER
            )
        )
        docRef.get().addOnCompleteListener { task: Task<DocumentSnapshot?> ->
            if (task.isSuccessful) {
                val document = task.result
                if (document?.exists() == true) {
                    binding.userStoreBorrow.visibility = View.INVISIBLE
                    //Log.d("Not_Available", document.getString("phoneNumber")); //Print the numbet
                } else {
                    binding.userStoreBorrow.visibility = View.VISIBLE
                    Log.d("Not_Available", "No such document")
                }
            } else {
                Log.d("Not_Available", "get failed with ", task.exception)
            }
        }
    }

    private fun uploadDetailInFirestore() {
        setInstrumentationBorrowUploadStatus()
        try {
            ff!!.collection("borrow_user_info") // Step - 1 (Collection Create
                .document(PrefrenceUtils.retriveData(activity, Constants.PHONENUMBER)) // Step -2 Custom Document Id Number
                .set(
                    StoreInfoModel(
                        PrefrenceUtils.retriveData(activity, Constants.USERNAME),
                        PrefrenceUtils.retriveData(activity, Constants.PHONENUMBER),
                        "borrow", createAt(System.currentTimeMillis()),
                        "userRef/" + PrefrenceUtils.retriveData(activity, Constants.PHONENUMBER)
                    )
                )
            showAlertDialog()
            binding.userStoreBorrow.visibility = View.GONE
        } catch (e: Exception) {
            Log.d("All_Contacts", "Error adding document3" + e.message.toString())
        }
    }

    private fun setInstrumentationBorrowUploadStatus() {
        val bundle = Bundle()
        fa!!.logEvent("borrow_interested", bundle)
        val properties = Properties()
        MoEHelper.getInstance(requireActivity()).trackEvent("borrow_interested", properties)
        UXCam.logEvent("borrow_interested")
    }

    private fun createAt(currentTimeMillis: Long): String {
        val simpleDateFormat =
            SimpleDateFormat(getString(R.string.date_format2), Locale.US)
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return simpleDateFormat.format(Date(currentTimeMillis))
    }

    private fun showAlertDialog() {
        val ctDialog = Dialog(requireContext())
        ctDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        ctDialog.setContentView(R.layout.response_saved_dialogue)
        ctDialog.setCanceledOnTouchOutside(true)
        ctDialog.setCancelable(true)
        ctDialog.show()
    }

    override fun onResume() {
        super.onResume()
        Constants.hideAndShowTopBar = false
        val bundle = Bundle()
        fa!!.logEvent(Constants.BORROW_PAGE_VIEWED, bundle)
        val properties = Properties()
        MoEHelper.getInstance(requireContext()).trackEvent(Constants.BORROW_PAGE_VIEWED, properties)
        BlitzLlamaSDK.getSdkManager(requireContext()).triggerEvent(Constants.BORROW_PAGE_VIEWED)
        val attribute = HashMap<String,Any>()
        attribute[Constants.BORROW_PAGE_VIEWED] = ""
        Freshchat.trackEvent(requireContext(),Constants.BORROW_PAGE_VIEWED,attribute)

    }
}