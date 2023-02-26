package com.vahan.mitra_playstore.view.activities.enternumberactivity.view.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.moengage.core.Properties
import com.moengage.firebase.MoEFireBaseHelper
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.ActivityRegistrationBinding
import com.vahan.mitra_playstore.models.kotlin.SendOtp
import com.vahan.mitra_playstore.models.kotlin.ValidateReferralRequestModel
import com.vahan.mitra_playstore.utils.*
import com.vahan.mitra_playstore.view.BaseActivity
import com.vahan.mitra_playstore.view.EnterOtpActivity
import com.vahan.mitra_playstore.view.SalaryViewActivity
import com.vahan.mitra_playstore.view.activities.enternumberactivity.viewmodel.EnterNumberViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext


@AndroidEntryPoint
class EnterNumberActivity : BaseActivity() {

    private var hashKey: String? = null
    lateinit var binding: ActivityRegistrationBinding
    private lateinit var enterNumberViewModel: EnterNumberViewModel
    private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
    val RESOLVE_HINT: Int = 24
    lateinit var number: String
    private var ctDialog: Dialog? = null
    private var fa: FirebaseAnalytics? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        initView()
        getAppHashKey()
        clickListener()
    }

    private fun getAppHashKey() {
        val appSignatureHashHelper = AppSignatureHashHelper(this)
        hashKey = appSignatureHashHelper.appSignatures[0]
    }

    private fun initView() {
        setupCrashAnalytics()
        enterNumberViewModel = ViewModelProvider(this)[EnterNumberViewModel::class.java]
        UXCam.setAutomaticScreenNameTagging(true)
        fa = FirebaseAnalytics.getInstance(this)
        initLoader(this)
        initDynamicLink()
        requestHint()
    }

    private fun initDynamicLink() {
        GlobalScope.launch {
            val dispatcher = getDispatcherFromCurrentThread(this)
            CoroutineScope(dispatcher).launch {
                getDynamicLink()
            }
        }
    }

    private fun setupCrashAnalytics() {
        try {
            if (intent.getStringExtra(Constants.DEVICE_ID_CHECK) != null) {
                initialiseFireBase()
            }
        } catch (e: Exception) {

        }

        if (intent.getBooleanExtra(Constants.CRASH, false)) {
        }
    }

    private fun getDispatcherFromCurrentThread(scope: CoroutineScope): CoroutineContext {
        return scope.coroutineContext
    }

    private fun getDynamicLink() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener {
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (it != null) {
                    deepLink = it.link
                    if (deepLink.toString().contains("#")) {
                        Log.d("EnterNumberActivity", "getDynamicLink: $deepLink")
                        val separator = "%3D"
                        val sepPos: Int = deepLink.toString().indexOf(separator)
                        if (sepPos == -1) {
                            println("")
                        }
                        binding.edtReferralCode.setText(
                            deepLink.toString().substring(sepPos + separator.length)
                        )
                    } else {
                        binding.edtReferralCode.setText("")
                    }

                } else {
                    binding.edtReferralCode.setText("")
                }
            }
            .addOnFailureListener {
                    Log.e("ERROR", "getDynamicLink:onFailure", it)
                }

    }

    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val intent = Credentials.getClient(this).getHintPickerIntent(hintRequest)
        startIntentSenderForResult(
            intent.intentSender,
            RESOLVE_HINT, null, 0, 0, 0
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun clickListener() {
        binding.sendOtp.setOnClickListener {
            /* Here we are checking
                1. If Number is entered by the user and not entering a Referral code then we directly call the send OTP
                2. If Referral Code is present then first we validate the referral code and then move to send OTP
             */
            if (binding.edtVerifyNumber.text!!.isNotEmpty() && binding.edtVerifyNumber.text?.length == 10)
                if(binding.edtReferralCode.text.toString().isNotEmpty()){
                   validateReferralCode(binding.edtReferralCode.text.toString())
                }else {
                    sendOtp()
                }
            else {
                binding.tvHeadingSecond.setTextColor(getColor(R.color.error_color))
                binding.tvHeadingSecond.text = getString(R.string.validation_message)
            }
        }
        binding.tvTcChange.setOnClickListener {
            val jsonObject = JSONObject(
                PrefrenceUtils.retriveData(
                    this,
                    Constants.TERM_AND_CONDITION_REMOTE_CONFIG
                )
            )
            jsonObject.getString(Constants.TC_URL)

            startActivity(
                Intent(this, SalaryViewActivity::class.java)
                    .putExtra(Constants._TYPE, getString(R.string.term_and_conditions))
                    .putExtra(
                        Constants.LINK, jsonObject.getString(Constants.TC_URL)
                    )
            )
            overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
        }
        binding.tCPrivacyPolFirst.setOnClickListener {
            val jsonObject = JSONObject(
                PrefrenceUtils.retriveData(
                    this,
                    Constants.TERM_AND_CONDITION_REMOTE_CONFIG
                )
            )
            startActivity(
                Intent(this, SalaryViewActivity::class.java)
                    .putExtra(Constants._TYPE, getString(R.string.term_and_conditions))
                    .putExtra(
                        Constants.LINK,
                        jsonObject.getString(Constants.TC_URL)
                    )
            )
            overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
        }
        binding.tCPrivacyPolSecond.setOnClickListener {
            val jsonObject = JSONObject(
                PrefrenceUtils.retriveData(
                    this,
                    Constants.TERM_AND_CONDITION_REMOTE_CONFIG
                )
            )
            startActivity(
                Intent(this, SalaryViewActivity::class.java)
                    .putExtra(Constants._TYPE, "Privacy Policy")
                    .putExtra(
                        Constants.LINK, jsonObject.getString(Constants.PRIVACY_URL)
                    )
            )
            overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
        }
        binding.parentContainer.setOnTouchListener { _, _ ->
            binding.edtVerifyNumber.clearFocus()
            hideKeyboard(this, binding.edtVerifyNumber)
            true
        }
    }

    // this method is validate the referral code
    private fun validateReferralCode(number: String) {
        dialogLoader?.show()
        val validateReferralRequestModel = ValidateReferralRequestModel(number)
        lifecycleScope.launchWhenStarted {
            enterNumberViewModel.validateOtp(validateReferralRequestModel).collect {
                when (it) {
                    is ApiState.Success -> {
                        dialogLoader?.dismiss()
                        if(it.data.success==true) {
                            sendOtp()
                        }else{
                            Toast.makeText(context, "Please enter a valid code", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is ApiState.Failure -> {
                        dialogLoader?.dismiss()
                        Toast.makeText(context, "Something went wrong :(", Toast.LENGTH_SHORT).show()
                    }
                    ApiState.Loading -> {}
                }
            }
        }
    }

    private fun sendOtp() {
        dialogLoader?.show()
        val number = SendOtp("", "")
        number.phoneNumber = binding.edtVerifyNumber.text.toString()
        PrefrenceUtils.insertData(
            this,
            Constants.PHONENUMBER,
            number.phoneNumber
        )

        Log.d("HA PN", "initView ENA: ${PrefrenceUtils.retriveData(this, Constants.PHONENUMBER)}")
        val attribute = HashMap<String, Any>()
        val properties = Properties()
        captureAllJSEEvents(
            this,
            "jse_phone_number_submitted",
            attribute,
            properties
        )
        PrefrenceUtils.insertData(this, Constants.HASHKEY, hashKey)
        lifecycleScope.launchWhenStarted {
            enterNumberViewModel.sendOTP(number).collect {
                when (it) {
                    is ApiState.Success -> {
                        dialogLoader?.dismiss()
                        val intent = Intent(context, EnterOtpActivity::class.java)
                            .putExtra(
                                Constants.MOBILE_NUMBER,
                                binding.edtVerifyNumber.text.toString()
                            )
                            .putExtra(Constants.LINK, intent.getStringExtra(Constants.LINK))
                            .putExtra(Constants.REFERRAL_CODE, binding.edtReferralCode.text.toString())
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
                        finish()
                    }
                    is ApiState.Failure -> {
                        Toast.makeText(context, "Something went wrong :(", Toast.LENGTH_SHORT).show()
                    }
                    ApiState.Loading -> {}
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                val credential: Credential = data?.getParcelableExtra(Credential.EXTRA_KEY)!!
                number = credential.id.replace("+91", "")
                binding.edtVerifyNumber.setText(number)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        initFirebaseRemoteConfig()

    }

    private fun initFirebaseRemoteConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configBuilder = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
        mFirebaseRemoteConfig?.setConfigSettingsAsync(configBuilder.build())
        mFirebaseRemoteConfig?.fetchAndActivate()
        val values = HashMap<String, Any>()
        values[Constants.ONLY_TEST_USERS] = ""
        values[Constants.APK_URL] = ""
        values[Constants.FORCE_UPDATE] = ""
        values[Constants.UPDATE_AVAILABLE] = ""
        values[Constants.VERSIONS] = 0.0
        values[Constants.CONTACT_SMS_PERMISSION] = ""
        values[Constants.NOTIFICATION_PERMISSION] = ""
        values[Constants.PERMISSION] = ""
        values[Constants.INSTALLED_APP] = ""
        values[Constants.SUPPORT_NUMBER] = ""
        values[Constants.PRIVACY_URL] = ""
        values[Constants.TERM_AND_CONDITION] = ""
        values[Constants.INSURE_CONDITION] = ""
        values[Constants.OTHER_SETTINGS] = ""
        values[Constants.UPDATE_CONDITIONS] = ""
        values[Constants.LOAN_TERM_AND_CONDITION] = ""
        values[Constants.LOAN_TERM] = ""
        values[Constants.WEBVIEW_HANDLER] = ""
        values[Constants.CHROME_URL] = ""
        values[Constants.FEEDBACK_TRIGGERS] = ""
        values[Constants.FRESHCHAT_ENABLE_CONDITION] = ""
        values[Constants.SHARE_REFERRAL_CODE_TEXT] = ""
        mFirebaseRemoteConfig?.setDefaultsAsync(values)
        getUpdates()
    }

    private fun getUpdates() {
        mFirebaseRemoteConfig?.fetchAndActivate()?.addOnCompleteListener() {
            try {
                PrefrenceUtils.insertData(
                    this,
                    Constants.SHARE_REFERRAL_CODE_TEXT_REMOTE_CONFIG,
                    mFirebaseRemoteConfig?.getString(Constants.SHARE_REFERRAL_CODE_TEXT)
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.UPDATE_CONDITIONS_REMOTE_CONFIG,
                    mFirebaseRemoteConfig?.getString(Constants.UPDATE_CONDITIONS).toString()
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.NUDGE_ICON_TEXT_REMOTE_CONFIG,
                    mFirebaseRemoteConfig?.getString(Constants.NUDGE_ICON_TEXT).toString()
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.PERMISSION_REMOTE_CONFIG,
                    mFirebaseRemoteConfig?.getString(Constants.PERMISSION)
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.CHROME_URL_REMOTE_CONFIG,
                    mFirebaseRemoteConfig?.getString(Constants.CHROME_URL)
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.UPDATE_AVAILABLE_REMOTE_CONFIG,
                    mFirebaseRemoteConfig?.getString(Constants.UPDATE_AVAILABLE)
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.ONLY_TEST_USERS_REMOTE_CONFIG,
                    mFirebaseRemoteConfig?.getString(Constants.ONLY_TEST_USERS)
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.FRESHCHAT_ENABLE_CONDITION_REMOTE_CONFIG,
                    mFirebaseRemoteConfig?.getString(Constants.FRESHCHAT_ENABLE_CONDITION)
                        .toString()
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.LOAN_TERM_REMOTE_CONFIG,
                    mFirebaseRemoteConfig?.getString(Constants.LOAN_TERM)
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.APK_URL_REMOTE_CONFIG,
                    mFirebaseRemoteConfig?.getString(Constants.APK_URL)
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.REMOTE_CONFIG_CASHOUT_CONDITION,
                    mFirebaseRemoteConfig?.getString(Constants.REMOTE_CONFIG_CASHOUT_CONDITION)
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.FORCE_UPDATE_REMOTE_CONFIG,
                    mFirebaseRemoteConfig?.getString(Constants.FORCE_UPDATE)
                )

                PrefrenceUtils.insertData(
                    this,
                    Constants.UPDATE_AVAILABLE_REMOTE_CONFIG_VERSION,
                    mFirebaseRemoteConfig?.getDouble(Constants.VERSIONS).toString()
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.CONTACT_SMS_PERMISSION_REMOTE_CONFIG,
                    mFirebaseRemoteConfig?.getString(Constants.CONTACT_SMS_PERMISSION).toString()
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.NOTIFICATION_PERMISSION_REMOTE_CONFIG,
                    mFirebaseRemoteConfig?.getString(Constants.NOTIFICATION_PERMISSION).toString()
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.INSTALLED_APP_REMOTE_CONFIG,
                    mFirebaseRemoteConfig?.getString(Constants.INSTALLED_APP).toString()
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.TERM_AND_CONDITION_REMOTE_CONFIG,
                    mFirebaseRemoteConfig?.getString(Constants.TERM_AND_CONDITION).toString()
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.SUPPORT_NUMBER_REMOTE_CONFIG,
                    mFirebaseRemoteConfig?.getString(Constants.SUPPORT_NUMBER).toString()
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.PRIVACY_URL_REMOTE_CONFIG,
                    mFirebaseRemoteConfig?.getString(Constants.PRIVACY_URL).toString()
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.INSURANCE_REMOTE_CONFIG,
                    mFirebaseRemoteConfig?.getString(Constants.INSURE_CONDITION).toString()
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.OTHER_SETTINGS_REMOTE_CONFIG,
                    mFirebaseRemoteConfig?.getString(Constants.OTHER_SETTINGS).toString()
                )


                PrefrenceUtils.insertData(
                    this,
                    Constants.LOAN_TERM_AND_CONDITION_REMOTE_CONFIG,
                    mFirebaseRemoteConfig?.getString(Constants.LOAN_TERM_AND_CONDITION).toString()
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.WEBVIEW_HANDLER_REMOTE_CONFIG_HOME_PAGE,
                    mFirebaseRemoteConfig?.getString(Constants.WEBVIEW_HANDLER)
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.WEBVIEW_HANDLER_REMOTE_CONFIG_MODIFY,
                    mFirebaseRemoteConfig?.getString(Constants.WEBVIEW_HANDLER_MODIFY)
                )
                PrefrenceUtils.insertData(
                    this,
                    Constants.FEEDBACK_TRIGGERS_REMOTE,
                    mFirebaseRemoteConfig?.getString(Constants.FEEDBACK_TRIGGERS)
                )

                Log.d(
                    "UPDATES_VALUE",
                    "getUpdates_Condition: " + PrefrenceUtils.retriveData(
                        this,
                        Constants.UPDATE_CONDITIONS_REMOTE_CONFIG
                    )
                )

                Log.d(
                    "UPDATES_VALUE",
                    "getUpdates: " + PrefrenceUtils.retriveData(
                        this,
                        Constants.ONLY_TEST_USERS_REMOTE_CONFIG
                    )
                )
                Log.d(
                    "UPDATES_VALUE",
                    "getUpdates: " + PrefrenceUtils.retriveData(
                        this,
                        Constants.APK_URL_REMOTE_CONFIG
                    )
                )
                Log.d(
                    "UPDATES_VALUE",
                    "getUpdates: " + PrefrenceUtils.retriveData(
                        this,
                        Constants.FORCE_UPDATE_REMOTE_CONFIG
                    )
                )
                Log.d(
                    "UPDATES_VALUE",
                    "getUpdates: " + PrefrenceUtils.retriveData(
                        this,
                        Constants.UPDATE_AVAILABLE_REMOTE_CONFIG
                    )
                )
                Log.d(
                    "UPDATES_VALUE",
                    "getUpdates: " + PrefrenceUtils.retriveData(
                        this,
                        Constants.UPDATE_AVAILABLE_REMOTE_CONFIG_VERSION
                    )
                )
                Log.d(
                    "UPDATES_VALUE",
                    "getUpdates: " + PrefrenceUtils.retriveData(
                        this,
                        Constants.CONTACT_SMS_PERMISSION_REMOTE_CONFIG
                    )
                )
                Log.d(
                    "UPDATES_VALUE",
                    "getUpdates1: " + PrefrenceUtils.retriveData(
                        this,
                        Constants.NOTIFICATION_PERMISSION_REMOTE_CONFIG
                    )
                )
                Log.d(
                    "UPDATES_VALUE",
                    "getUpdates2: " + PrefrenceUtils.retriveData(
                        this,
                        Constants.INSTALLED_APP_REMOTE_CONFIG
                    )
                )
                Log.d(
                    "UPDATES_VALUE",
                    "getUpdates2: " + PrefrenceUtils.retriveData(
                        this,
                        Constants.TERM_AND_CONDITION_REMOTE_CONFIG
                    )
                )
                Log.d(
                    "UPDATES_VALUE",
                    "getUpdates2: " + PrefrenceUtils.retriveData(
                        this,
                        Constants.SUPPORT_NUMBER_REMOTE_CONFIG
                    )
                )
                Log.d(
                    "UPDATES_VALUE",
                    "getUpdates2: " + PrefrenceUtils.retriveData(
                        this,
                        Constants.PRIVACY_URL_REMOTE_CONFIG
                    )
                )
                Log.d(
                    "UPDATES_VALUE",
                    "getUpdates2: " + PrefrenceUtils.retriveData(
                        this,
                        Constants.REMOTE_CONFIG_CASHOUT_CONDITION
                    )
                )
                Log.d(
                    "UPDATES_VALUE",
                    "getUpdates2: " + PrefrenceUtils.retriveData(
                        this,
                        Constants.OTHER_SETTINGS_REMOTE_CONFIG
                    )
                )
                Log.d(
                    "UPDATES_VALUE",
                    "getUpdates2: " + PrefrenceUtils.retriveData(
                        this,
                        Constants.WEBVIEW_HANDLER_REMOTE_CONFIG_HOME_PAGE
                    )
                )
                Log.d(
                    "UPDATES_VALUE",
                    "Permission: " + PrefrenceUtils.retriveData(
                        this,
                        Constants.PERMISSION_REMOTE_CONFIG
                    )
                )

            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()

            }
        }?.addOnFailureListener() { e: Exception? ->
            Toast.makeText(this, e?.message, Toast.LENGTH_SHORT).show()

        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun initialiseFireBase() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            Log.d("FCMTOKEN", token)
            MoEFireBaseHelper.getInstance().passPushToken(applicationContext, token)
            PrefrenceUtils.insertData(this, Constants.DEVICE_ID, token)
        })
    }

    private fun hideKeyboard(activity: Activity?, view: EditText) {
        val imm: InputMethodManager = activity?.applicationContext
            ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}