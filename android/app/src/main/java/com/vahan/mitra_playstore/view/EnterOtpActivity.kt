package com.vahan.mitra_playstore.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.freshchat.consumer.sdk.Freshchat
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.JsonObject
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.ActivityEnterOtpBinding
import com.vahan.mitra_playstore.models.LoginModel
import com.vahan.mitra_playstore.network.SharedViewModel
import com.vahan.mitra_playstore.services.SmsReceiver
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.utils.captureAllEvents
import com.vahan.mitra_playstore.view.activities.enternumberactivity.view.ui.EnterNumberActivity
import java.util.concurrent.TimeUnit

class EnterOtpActivity : BaseActivity(), SmsReceiver.OTPReceiveListener {
    lateinit var binding: ActivityEnterOtpBinding
    private lateinit var smsReceiver: SmsReceiver
    private var otpManually: String? = null
    private var numberForAPiCall: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    var otp1Count = 0
    var otp2Count = 0
    var otp3Count = 0
    var otp4Count = 0
    var count =0

    private var editTexts: Array<EditText>? = null
    private val format = "%02d:%02d"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enter_otp)
        initView()
        clickListener()
    }


    private fun initView() {
        auth = FirebaseAuth.getInstance()
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        startSMSListener()
        startEnableResendCounter()
        initLoader(this)
        getDataFromIntent()
        //   dynamicIconLoad()
        //pinTextWatcher()
        UXCam.setAutomaticScreenNameTagging(true)

        binding.edtVerifyOtp0.onTextChanged { s, i ->

            if(i==1){
                binding.edtVerifyOtp1.requestFocus()
            }
        }
        binding.edtVerifyOtp1.onTextChanged { s, i ->
            if(i==1){
                binding.edtVerifyOtp2.requestFocus()
            }
        }
        binding.edtVerifyOtp2.onTextChanged { s, i ->
            if(i==1){
                binding.edtVerifyOtp3.requestFocus()
            }
            if(binding.edtVerifyOtp3.text.isEmpty() && i==2){
                binding.edtVerifyOtp3.requestFocus()
            }else{

            }
        }


        binding.edtVerifyOtp3.setOnKeyListener(View.OnKeyListener { v, keyCode, event -> // You can identify which key pressed buy checking keyCode value
            // with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                count += 1
                if (count > 2) {
                    binding.edtVerifyOtp2.requestFocus()
                    count=0
                }
            }
            false
        })

        binding.edtVerifyOtp2.setOnKeyListener(View.OnKeyListener { v, keyCode, event -> // You can identify which key pressed buy checking keyCode value
            // with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                otp3Count += 1
                if (otp3Count > 2) {
                    binding.edtVerifyOtp1.requestFocus()
                    otp3Count=0
                }
            }
            false
        })

        binding.edtVerifyOtp1.setOnKeyListener(View.OnKeyListener { v, keyCode, event -> // You can identify which key pressed buy checking keyCode value
            // with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                otp2Count += 1
                if (otp2Count > 2) {
                    binding.edtVerifyOtp0.requestFocus()
                    otp2Count=0
                }
            }
            false
        })

    }

    fun EditText.onTextChanged(onTextChanged: (String,Int) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("beforeText", "beforeTextChanged: "+start+" "+after)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onTextChanged.invoke(s.toString(),count)
            }

            override fun afterTextChanged(editable: Editable?) {

            }
        })
    }



    private fun pinTextWatcher() {
        editTexts = arrayOf(
            binding.edtVerifyOtp0,
            binding.edtVerifyOtp1,
            binding.edtVerifyOtp2,
            binding.edtVerifyOtp3
        )
        binding.edtVerifyOtp0.addTextChangedListener(
            PinTextWatcher(
                0, editTexts!!
            )
        )
        binding.edtVerifyOtp1.addTextChangedListener(
            PinTextWatcher(
                1, editTexts!!
            )
        )
        binding.edtVerifyOtp2.addTextChangedListener(
            PinTextWatcher(
                2, editTexts!!
            )
        )
//        binding.edtVerifyOtp3.addTextChangedListener(
//            PinTextWatcher
//                (3, editTexts!!)
//        )
        binding.edtVerifyOtp0.setOnKeyListener(
            PinOnKeyListener
                (0, editTexts!!)
        )
        binding.edtVerifyOtp1.setOnKeyListener(
            PinOnKeyListener
                (1, editTexts!!)
        )
        binding.edtVerifyOtp2.setOnKeyListener(
            PinOnKeyListener
                (2, editTexts!!)
        )
//        binding.edtVerifyOtp3.setOnKeyListener(
//            PinOnKeyListener
//                (3, editTexts!!)
//        )
    }

    @SuppressLint("SetTextI18n")
    private fun getDataFromIntent() {
        if (intent != null) {
            numberForAPiCall = intent.getStringExtra(Constants.MOBILE_NUMBER)
            if (PrefrenceUtils.retriveLangData(this, Constants.LANGUAGE)
                    .equals("en", ignoreCase = false)
            ) {
                binding.tvHeading.text =
                    getString(R.string.number_heading_tv) + intent.getStringExtra(Constants.MOBILE_NUMBER)
            } else {
                binding.tvHeading.text =
                    "कृपया +91 ${intent.getStringExtra(Constants.MOBILE_NUMBER)} पर भेजे गए 4 अंकों का OTP कोड दर्ज करे"

            }
        }
    }


    private fun startEnableResendCounter() {
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.resendOtp.isEnabled = false
                binding.resendOtp.visibility = View.GONE
                binding.counter.visibility = View.VISIBLE
                binding.counter.text = String.format(
                    format,
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                    ),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                    )
                )
            }

            override fun onFinish() {
                binding.resendOtp.isEnabled = true
                binding.counter.visibility = View.GONE
                binding.resendOtp.visibility = View.VISIBLE
                binding.resendOtp.setTextColor(getColor(R.color.default_200))
                binding.counter.text = ""
            }
        }.start()
    }

    private fun startSMSListener() {
        try {
            smsReceiver = SmsReceiver()
            smsReceiver.setOTPListener(this)
            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            this.registerReceiver(smsReceiver, intentFilter)
            val client = SmsRetriever.getClient(this)
            val task = client.startSmsRetriever()
            task.addOnSuccessListener {
                // API successfully started
                // Toast.makeText(this, "SuccessFully", Toast.LENGTH_LONG).show()
            }
            task.addOnFailureListener {
                // Fail to start API
                Toast.makeText(this, getString(R.string.failure), Toast.LENGTH_LONG).show()

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clickListener() {
        binding.loginButton.setOnClickListener {
            otpManually =
                binding.edtVerifyOtp0.text.toString() + binding.edtVerifyOtp1.text.toString() + binding.edtVerifyOtp2.text.toString() + binding.edtVerifyOtp3.text.toString()
            if (otpManually!!.length < 0 || otpManually!!.length < 4) {
                Toast.makeText(this, getString(R.string.please_enter_4_digit_otp), Toast.LENGTH_SHORT).show()

            } else {
                verifyUserwithOTP(otpManually!!, numberForAPiCall!!)
                // getUserData("8178253375")
            }


        }
        if (binding.resendOtp.isEnabled) {
            binding.resendOtp.setOnClickListener {
                startSMSListener()
                sendOtp(numberForAPiCall!!)
            }
        }

        binding.reEnterNumber.setOnClickListener {
            startActivity(Intent(this, EnterNumberActivity::class.java))
            finish()
        }
    }

    private fun sendOtp(numberForAPiCall: String) {
        dialogLoader?.show()
        val viewSharedViewModel: SharedViewModel =
            ViewModelProvider(this)[SharedViewModel::class.java]
        val jsonObject = JsonObject()
        jsonObject.addProperty(
            "phoneNumber", numberForAPiCall
        )
        jsonObject.addProperty(
            "appHashKey", PrefrenceUtils.retriveData(this, Constants.HASHKEY)
        )
        viewSharedViewModel.sendOtp(
            jsonObject
        ).observe(
            this
        ) {
            if (it != null) {
                if (it.success == true) {
                    dialogLoader?.dismiss()
                    startEnableResendCounter()
                }
            }
        }
    }


    override fun onOTPTimeOut() {
        //   Toast.makeText(this, "TimeOut", Toast.LENGTH_LONG).show()
    }

    override fun onOTPReceivedError(error: String?) {
        // Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
    }

    override fun onOTPReceived(otp: String?) {
        runOnUiThread {
            if (otp != null) {

                try {
                    //  val otpValue = otp.split(" ")[1].substring(otp.split(" ")[1].length - 4, otp.split(" ")[1].length)
                    val otpValue = otp.substring(3, 7)
                    binding.edtVerifyOtp0.setText(otpValue.substring(0, 1))
                    binding.edtVerifyOtp1.setText(otpValue.substring(1, 2))
                    binding.edtVerifyOtp2.setText(otpValue.substring(2, 3))
                    binding.edtVerifyOtp3.setText(otpValue.substring(3))
                    Handler(mainLooper).postDelayed({
                        verifyUserwithOTP(otpValue, numberForAPiCall)
                    }, 2000)
                } catch (e: Exception) {
                    Toast.makeText(
                        this@EnterOtpActivity,
                        getString(R.string.something_went_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // Auto otp fill.

            }
            LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver)
        }

    }


    private fun verifyUserwithOTP(otpValue: String?, numberForAPiCall: String?) {
        val viewSharedViewModel: SharedViewModel =
            ViewModelProvider(this)[SharedViewModel::class.java]
        val jsonObject = JsonObject()
        jsonObject.addProperty(Constants.PHONE_NUMBER, numberForAPiCall)
        jsonObject.addProperty(Constants.OTP, otpValue)
        jsonObject.addProperty(Constants.REFERRAL_CODE, intent.getStringExtra(Constants.REFERRAL_CODE))
        viewSharedViewModel.login(jsonObject).observe(this) {
            if (it.success!!) {
                authenticateWithFireBase(
                    it.cookie?.firebaseToken!!,
                    it.cookie?.accessToken!!,
                    it.userType,
                    it.redirectURL,
                    it
                )
            } else {
                //bringing focus on on edit text
                binding.edtVerifyOtp3.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edtVerifyOtp3, InputMethodManager.RESULT_UNCHANGED_SHOWN)

                Toast.makeText(
                    this, getString(R.string.please_check_your_otp_and_try_again),
                    Toast.LENGTH_SHORT
                ).show()


//                binding.edtVerifyOtp0.setText("")
//                binding.edtVerifyOtp1.setText("")
//                binding.edtVerifyOtp2.setText("")
//                binding.edtVerifyOtp3.setText("")
            }
        }
    }

    private fun setUser(loginModel: LoginModel) {
        PrefrenceUtils.insertData(
            this,
            Constants.USERID,
            loginModel.id
        )
        if (!loginModel.userType.equals("", ignoreCase = true)) {
            MoEHelper.getInstance(this).setUserAttribute(Constants.USERT_TYPE, loginModel.userType!!)
            UXCam.setUserProperty(Constants.USERT_TYPE, loginModel.userType!!)
            BlitzLlamaSDK.getSdkManager(this)
                .setUserAttribute(Constants.USERT_TYPE, loginModel.userType!!, Constants.STRING)
        }
    }

    private fun authenticateWithFireBase(
        firebaseToken: String,
        accessToken: String,
        userType: String?,
        redirectURL: String?,
        loginModel: LoginModel
    ) {
        auth.signInWithCustomToken(firebaseToken)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    PrefrenceUtils.insertData(context, Constants.CHECKSMSONCES, "ONCES")
                    PrefrenceUtils.insertData(context, Constants.CHECKDEVICEDETAILSONCES, "ONCES")
                    PrefrenceUtils.insertData(context, Constants.USERTYPE, userType)
                    PrefrenceUtils.insertData(
                        this, Constants.API_TOKEN,
                        accessToken)
                    PrefrenceUtils.insertDataInBoolean(
                        this, Constants.CHECKFORFIRSTTIME, true
                    )
                    PrefrenceUtils.insertDataInBoolean(
                        this, Constants.ACTIVITY_COACHMARKS, false
                    )
                    PrefrenceUtils.insertDataInBoolean(
                        this, Constants.CHECKFORFIRSTTIMESLOTSCREEN, true
                    )
                    val bundle = Bundle()
                    bundle.putString(
                        FirebaseAnalytics.Param.METHOD,
                        Constants.LOGIN
                    )
                    mFirebaseAnalytics.logEvent(
                        FirebaseAnalytics.Event.LOGIN,
                        bundle
                    )
                    setUser(loginModel)
                    //FirebaseCrashlytics.getInstance().setUserId(loginModel.id!!)
                    if (numberForAPiCall != null) {
                        //FirebaseCrashlytics.getInstance().setCustomKey(Constants.PHONE_NUMBER, numberForAPiCall!!)
                    }
                    //authenticateWithFireBase2(it.cookie.accessToken)
                    setInstrumentation(loginModel)

                    if (dialogLoader!!.isShowing) {
                        dialogLoader!!.dismiss()
                    }
                    when (userType) {
                        Constants.PAYROLL -> {
                            PrefrenceUtils.insertDataInBoolean(this, Constants.CHECKFORPAYROLL, true)
                            if (intent.getStringExtra(Constants.LINK) != null) {
                                startActivity(
                                    Intent
                                        (
                                        this, HomeActivity::class.java
                                    ).putExtra(Constants.LINK, intent.getStringExtra(Constants.LINK))
                                )
                                overridePendingTransition(
                                    R.anim.slide_out_bottom,
                                    R.anim.slide_in_bottom
                                )
                            } else {
                                startActivity(
                                    Intent
                                        (
                                        this, MainActivity::class.java
                                    )
                                )
                                overridePendingTransition(
                                    R.anim.slide_out_bottom,
                                    R.anim.slide_in_bottom
                                )
                            }

                            finishAffinity()
                        }
                        Constants.NON_PAYROLL -> {
                            PrefrenceUtils.insertDataInBoolean(this, Constants.CHECKFORPAYROLL, false)
                            PrefrenceUtils.insertData(this, Constants.REDIRECTION_URL, redirectURL)
                            startActivity(
                                Intent(
                                    this@EnterOtpActivity,
                                    HomeActivity::class.java
                                ).putExtra(Constants.LINK, intent.getStringExtra(Constants.LINK))
                            )
                            overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
                            finishAffinity()
                        }
                        Constants.NEW_LEAD -> {
                            PrefrenceUtils.insertDataInBoolean(this, Constants.CHECKFORPAYROLL, false)
                            PrefrenceUtils.insertData(this, Constants.REDIRECTION_URL, redirectURL)
                            startActivity(
                                Intent(
                                    this@EnterOtpActivity,
                                    HomeActivity::class.java
                                ).putExtra("link", intent.getStringExtra(Constants.LINK))
                            )
                            overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
                            finishAffinity()
                        }

                        // ADDING A NEW REFERRAL TYPE FOR NEW LEAD REFERRAL
                        Constants.NEW_LEAD_REFERRAL_TYPE -> {
                            setInstrumentationNewLeadInit()
                            PrefrenceUtils.insertDataInBoolean(this, Constants.CHECKFORPAYROLL, false)
                            PrefrenceUtils.insertData(this, Constants.REDIRECTION_URL, redirectURL)
                            startActivity(
                                Intent(
                                    this@EnterOtpActivity,
                                    HomeActivity::class.java
                                ).putExtra("link", intent.getStringExtra(Constants.LINK))
                            )
                            overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
                            finishAffinity()
                        }
                    }
                    binding.loginButton.isEnabled = false
                    binding.loginButton.isClickable = false
                } else {
                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun setInstrumentation(it: LoginModel) {
        val bundle = Bundle()
        val property = Properties()
        val attribute = HashMap<String, Any>()
        bundle.putString(FirebaseAnalytics.Param.METHOD, Constants.LOGIN)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
        MoEHelper.getInstance(this).setUniqueId(it.id!!)
        UXCam.setUserIdentity(it.id)
        BlitzLlamaSDK.getSdkManager(this).createUser(it.id!!)
        bundle.putString(Constants.USERTYPE, it.userType)
        attribute[Constants.USERTEST] = it.userType!!
        property.addAttribute(Constants.USERTYPE, Constants.PAYROLL)
        MoEHelper.getInstance(this@EnterOtpActivity).trackEvent(Constants.USERLOGIN, property)
        UXCam.logEvent(Constants.USERLOGIN)
        BlitzLlamaSDK.getSdkManager(this).triggerEvent(Constants.USERLOGIN)
        Freshchat.trackEvent(this, Constants.USERLOGIN, attribute)
    }

    private fun setInstrumentationNewLeadInit() {
        val properties = Properties()
        val attribute = HashMap<String, Any>()
        captureAllEvents(this@EnterOtpActivity,"application_initiated",attribute,properties)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver)

    }

    fun isAllEditTextsFilled(editTextsValue: Array<EditText>): Boolean {
        for (editText in editTextsValue) if (editText.text.toString().trim { it <= ' ' }
                .isEmpty()) return false
        return true
    }

    class PinTextWatcher
    internal constructor(
        private val currentIndex: Int,
        private val editTexts: Array<EditText>,
    ) : TextWatcher {
        private var isFirst = false
        private var isLast = false
        private var newTypedString = ""
        private var enterOtpActivity = EnterOtpActivity()

        init {
            if (currentIndex == 0) isFirst =
                true else if (currentIndex == editTexts.size - 1) isLast = true
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            newTypedString = s.subSequence(start, start + count).toString().trim { it <= ' ' }
        }

        override fun afterTextChanged(s: Editable) {
            var text = newTypedString
            if (text.length > 1) text =
                text[0].toString() /* TODO: We can fill out other EditTexts */
            editTexts[currentIndex].removeTextChangedListener(this)
            editTexts[currentIndex].setText(text)
            editTexts[currentIndex].setSelection(text.length)
            editTexts[currentIndex].addTextChangedListener(this)
            if (text.length == 1) moveToNext() else if (text.isEmpty()) moveToPrevious()
        }

        private fun moveToNext() {
            if (!isLast) editTexts[currentIndex + 1].requestFocus()
            if (enterOtpActivity.isAllEditTextsFilled(editTexts) && isLast) { // isLast is optional
                editTexts[currentIndex].clearFocus()
                hideKeyboard()
            }
        }

        private fun moveToPrevious() {
            if (!isFirst) editTexts[currentIndex - 1].requestFocus()
        }

        private fun hideKeyboard() {
            try {
                val inputMethodManager =
                    enterOtpActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    enterOtpActivity.currentFocus?.windowToken,
                    0
                )
            } catch (e: java.lang.Exception) {
            }
        }
    }

    class PinOnKeyListener internal constructor(
        private val currentIndex: Int,
        private val editTexts: Array<EditText>,
    ) : View.OnKeyListener {
        //        private var enterOtpActivity = EnterOtpActivity()
        override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_UP) {
                if (editTexts[currentIndex].text.toString()
                        .isEmpty() && currentIndex != 0
                ) editTexts[currentIndex - 1].requestFocus()
            }
            return false
        }

    }
}


