package com.vahan.mitra_playstore.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.freshchat.consumer.sdk.Freshchat
import com.freshchat.consumer.sdk.FreshchatConfig
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.moengage.firebase.MoEFireBaseHelper
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.ActivitySplashBinding
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import java.util.*


@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        timeCheckEvent()
        initialiseFireBase()
        /** This static boolean variable is used re-initialized the sessions when app is killed (In Earn-Fragment) **/
        Constants.checkSessionSoftUpdate = true
        setFreshChat()
        initHandler()
        Log.d("DATA1234", "onCreate: ${System.getProperty("http.agent")}")
    }

    private fun setFreshChat() {
        val config = FreshchatConfig(
            getString(R.string.app_id),
            getString(R.string.app_key)
        )
        config.domain = getString(R.string.domain)
        config.isCameraCaptureEnabled = true
        config.isGallerySelectionEnabled = true
        config.isResponseExpectationEnabled = true
        Freshchat.getInstance(this).init(config)
    }

    private fun timeCheckEvent() {
        val fa = FirebaseAnalytics.getInstance(this)
        val calendar = Calendar.getInstance()
        val hour24hrs = calendar[Calendar.HOUR_OF_DAY]
        if (hour24hrs in 2..11) {
            val bundle1 = Bundle()
            val properties = Properties()
            MoEHelper.getInstance(this).trackEvent(Constants.MORNING_TRIGGERED, properties)
            fa.logEvent(Constants.MORNING_TRIGGERED, bundle1)
            BlitzLlamaSDK.getSdkManager(this).triggerEvent(Constants.MORNING_TRIGGERED)
            val attribute: HashMap<String, Any> = HashMap()
            Freshchat.trackEvent(this, "", attribute)
        }
        PrefrenceUtils.insertDataInBoolean(
            this,
            Constants.ISFEEDBACKSESSION,
            true
        )
    }

    private fun initHandler() {
        // check conditions for Language counter
        if (!PrefrenceUtils.retriveData(this, Constants.API_TOKEN).equals("", ignoreCase = false)) {
            /**
             *  Check user come from Notification
             *  with given click Action
             */
            Log.d("CLICK", "initHandler: "+PrefrenceUtils.retriveData(this, Constants.CLICKED_ACTION))
            if (!PrefrenceUtils.retriveData(this, Constants.CLICKED_ACTION).equals("")) {
                handleIntents(
                    PrefrenceUtils.retriveData(
                        this@SplashActivity,
                        Constants.CLICKED_ACTION
                    ),
                    PrefrenceUtils.retriveData(
                        this@SplashActivity,
                        Constants.REFERRAL_NUMBERS
                    )
                )
            }
            /**
             *  If API_TOKEN available and user directly come from app then move to HOME PAGE
             *  with given click Action
             */
            else {
                if (PrefrenceUtils.retriveLangData(this, Constants.LANGUAGE).equals("")) {
                    if (PrefrenceUtils.retriveLangData(this, Constants.LANGUAGE_API_RESP)
                            .equals("")
                    ) {
                        PrefrenceUtils.insertDataLang(this, Constants.LANGUAGE_API_RESP, "english")
                        PrefrenceUtils.insertDataLang(this, Constants.LANGUAGE, "en")
                    }
                }
                Handler(mainLooper).postDelayed({
                    Log.d("FUNNEL_ONE ", "handleIntents: ENTERS2")
                    startActivity(Intent(this@SplashActivity, AuthenticationActivity::class.java).putExtra(Constants.TYPE,""))
                    overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
                    finish()
                }, 3000)

            }
        }
        /**
         *  If API_TOKEN not available and user directly come from app then move to EnterNumberActivity
         *  with given click Action
         */
        else {
            Handler(mainLooper).postDelayed({
//                startActivity(Intent(this@SplashActivity, LanguageSelectionActivity::class.java))
                startActivity(Intent(this@SplashActivity, GenericWelcomeActivity::class.java))
                overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
                finish()
            }, 3000)
        }

    }

    // Background flow
    private fun handleIntents(type: String, clickAction: String) {
        Log.d("CLICKONE", "initHandler: "+PrefrenceUtils.retriveData(this, type))
        PrefrenceUtils.insertData(this, Constants.CLICKED_ACTION, type)
        startActivity(Intent(this, AuthenticationActivity::class.java).putExtra(Constants.TYPE, type))
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
        finish()
   // check user is payroll or not if yes move forward and check the screen

    }

    @SuppressLint("StringFormatInvalid")
    private fun initialiseFireBase() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            MoEFireBaseHelper.getInstance().passPushToken(applicationContext, token)
            PrefrenceUtils.insertData(this, Constants.DEVICE_ID, token)
        })

    }


}