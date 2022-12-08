package com.vahan.mitra_playstore.view.bottomsheet

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FeedbackBottomsheetLayoutBinding
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.BaseActivity
import com.vahan.mitra_playstore.view.BaseApplication
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


class FeedbackBottomsheet(context: Context, private val page :String) :
    BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme) {
    lateinit var binding: FeedbackBottomsheetLayoutBinding
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val handler = Handler(Looper.getMainLooper())
    val hashMap = HashMap<String, String>()
    private lateinit var fa: FirebaseAnalytics

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.feedback_bottomsheet_layout,
            null,
            false
        );
        setContentView(binding.root);
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        fa = FirebaseAnalytics.getInstance(context)
        setInstrumentation()

        binding.submitLayout.setOnClickListener {
            initview()
        }
        binding.close.setOnClickListener { v ->
            dismiss()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initview() {
        //Toast.makeText(context, binding.feedbackRating.rating, Toast.LENGTH_SHORT).show()

        if (binding.feedbackRating.rating != 0.0f) {
            if (binding.feedbackRating.rating > 4.0) {
                hashMap[context.getString(R.string.mobileNo)] = PrefrenceUtils.retriveData(context,Constants.PHONENUMBER)
                hashMap[context.getString(R.string.rating)] = binding.feedbackRating.rating.toString()
                hashMap[context.getString(R.string.feedbackMessage)] = binding.editText.text.toString()
                hashMap[context.getString(R.string.created_on)] = DateTimeFormatter
                    .ofPattern(context.getString(R.string.date_format))
                    .withZone(ZoneOffset.UTC)
                    .format(Instant.now())
                handler.postDelayed({ // Run your task here
                    Log.d("Message",
                        "initView: ${PrefrenceUtils.retriveData(BaseApplication.context, Constants.FIREBASE_TOKEN)}")
                    try {
                        db.collection(Constants.USER_FEEDBACK)
                            .document(
                                PrefrenceUtils.retriveData(BaseApplication.context, Constants.PHONENUMBER)
                            ).set(hashMap)
                            .addOnSuccessListener {
                                //Log.d("location", "Location added: $lat")
                                dismiss()
                            }
                            .addOnFailureListener { e ->
                                Log.d("location", "Location Erroradded: $e")
                            }
                        inAppReview()
                    } catch (e: Exception) {

                    }
                }, 1000)
            } else {
                hashMap[context.getString(R.string.mobileNo)] = PrefrenceUtils.retriveData(context,Constants.PHONENUMBER)
                hashMap[context.getString(R.string.ratings)] = binding.feedbackRating.rating.toString()
                hashMap[context.getString(R.string.feedbackMessage)] = binding.editText.text.toString()
                handler.postDelayed({ // Run your task here
                    Log.d("Message",
                        "initView: ${PrefrenceUtils.retriveData(BaseApplication.context, Constants.FIREBASE_TOKEN)}")
                    try {
                        db.collection(Constants.USER_FEEDBACK)
                            .document(
                                PrefrenceUtils.retriveData(BaseApplication.context, Constants.PHONENUMBER)
                            ).set(hashMap)
                            .addOnSuccessListener {
                                Toast.makeText(context,R.string.sharing_feedback,Toast.LENGTH_LONG).show()
                                 dismiss()
                            }
                            .addOnFailureListener { e ->
                                Log.d("location", "Location Erroradded: $e")
                            }
                    } catch (e: Exception) {

                    }
                }, 3000)
            }
            submitfeedbackCheck()
        } else {
            Toast.makeText(context, R.string.rating_mandatory, Toast.LENGTH_LONG).show()

        }
        setInstrumentationFeedbackUpdate()

    }

    private fun submitfeedbackCheck() {
        when (page) {
            Constants.PROFILE -> PrefrenceUtils.insertDataInBoolean(context,Constants.PROFILE_PAGE_VIEWED,true)
            Constants.HOME_PAGE_VIEWED ->  PrefrenceUtils.insertDataInBoolean(context,Constants.HOME_PAGE_VIEWED,true)
            Constants.CASHOUT ->  PrefrenceUtils.insertDataInBoolean(context,Constants.CASHOUT_AVAIL,true)
            Constants.MILESTONE_DETAILS_VIEWED -> PrefrenceUtils.insertDataInBoolean(context,Constants.MILESTONE_DETAILS_VIEWED,true)
            Constants.PAYSLIP ->  PrefrenceUtils.insertDataInBoolean(context,Constants.PAY_SLIP_VIEWED,true)
            Constants.LOAN_APPLIED ->  PrefrenceUtils.insertDataInBoolean(context,Constants.LOAN_APPLIED,true)
            Constants.TXT ->  PrefrenceUtils.insertDataInBoolean(context,Constants.TXN_HISTORY,true)
            Constants.LOAN_APPLICATION_VIEWED ->  PrefrenceUtils.insertDataInBoolean(context,Constants.LOAN_APPLICATION_VIEWED,true)
        }
    }

    private fun setInstrumentationFeedbackUpdate() {
        val bundle = Bundle()

        val properties = Properties()
        val data = HashMap<String, String>()
        bundle.putString(context.getString(R.string.ratings), binding.feedbackRating.rating.toInt().toString())
        if (binding.editText.text.isNotEmpty()){
            bundle.putString(context.getString(R.string.details), "true")
        }else{
            bundle.putString(context.getString(R.string.details), "false")
        }
        properties.addAttribute(context.getString(R.string.ratings), binding.feedbackRating.rating.toInt().toString())
        if (binding.editText.text.isNotEmpty()){
            properties.addAttribute(context.getString(R.string.details), "true")
        }else{
            properties.addAttribute(context.getString(R.string.details), "false")
        }
        data[context.getString(R.string.ratings)] = binding.feedbackRating.rating.toString()
        if (binding.editText.text.isNotEmpty()){
            data[context.getString(R.string.details)] = "true"
        }else{
            data[context.getString(R.string.details)] = "false"
        }
        fa.logEvent(context.getString(R.string.feedback_posted), bundle)
        MoEHelper.getInstance(context).trackEvent(context.getString(R.string.feedback_posted), properties)
        UXCam.logEvent(context.getString(R.string.feedback_posted), data)
    }

    private fun setInstrumentation() {
        val bundle = Bundle()
        val properties = Properties()
        fa.logEvent(context.getString(R.string.feedback_viewed), bundle)
        MoEHelper.getInstance(context).trackEvent(context.getString(R.string.feedback_viewed), properties)
        UXCam.logEvent(context.getString(R.string.feedback_viewed))
    }

    fun inAppReview() {
        val reviewManager = ReviewManagerFactory.create(context)
        val requestReviewFlow = reviewManager.requestReviewFlow()
        requestReviewFlow.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = request.result
                val flow = reviewManager.launchReviewFlow(BaseActivity.context as Activity, reviewInfo)
                flow.addOnCompleteListener {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                }
            } else {
                Log.d("Error: ", request.exception.toString())

            }
        }
    }


}