package com.vahan.mitra_playstore.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.freshchat.consumer.sdk.FaqOptions
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
import com.vahan.mitra_playstore.workmanager.WorkerScheduler
import java.util.*


@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        timeCheckEvent()
        intialiseFireBase()
        /** This static boolean variable is used re-initialized the sessions when app is killed (In Earn-Fragment) **/
        Constants.checkSessionSoftUpdate = true
        setFreshChat()
        initHandler()
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
            if (!PrefrenceUtils.retriveData(this, Constants.CLICKED_ACTION).equals("")) {
                handleIntents(
                    PrefrenceUtils.retriveData(
                        this@SplashActivity,
                        Constants.CLICKED_ACTION
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
                    startActivity(Intent(this@SplashActivity, AuthenticationActivity::class.java))
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
                startActivity(Intent(this@SplashActivity, LanguageSelectionActivity::class.java))
                overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
                finish()
            }, 3000)
        }

    }

    // Background flow
    private fun handleIntents(type: String) {
        when (type) {
            Constants.HOME -> {
                PrefrenceUtils.insertData(this, Constants.CLICKED_ACTION, "")
                startActivity(Intent(this, MainActivity::class.java).putExtra(Constants.TYPE, type))
                overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
                finish()
            }
            Constants.PROFILE -> {
                PrefrenceUtils.insertData(this, Constants.CLICKED_ACTION, "")
                PrefrenceUtils.insertData(this, Constants.LINK, "")
            }
            Constants.BORROW -> {
                PrefrenceUtils.insertData(this, Constants.CLICKED_ACTION, "")
                startActivity(
                    Intent(this, MainActivity::class.java)
                        .putExtra(Constants.TYPE, type)
                )
                overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
                finish()
            }

            // Adding a new Entry Trigger for Push Notification
            Constants.CHATBOT -> {
                val tags: MutableList<String> = ArrayList()
                tags.add("newFaq")
                val faqOptions = FaqOptions()
                    .showFaqCategoriesAsGrid(false)
                    .showContactUsOnAppBar(false)
                    .showContactUsOnFaqScreens(true)
                    .showContactUsOnFaqNotHelpful(true)
                    .filterContactUsByTags(tags, "Test 2") //tags, filtered screen title
                Freshchat.showFAQs(this, faqOptions)
            }
            Constants.INSURE -> {
                PrefrenceUtils.insertData(this, Constants.CLICKED_ACTION, "")
                startActivity(
                    Intent(this, MainActivity::class.java)
                        .putExtra(Constants.TYPE, type)
                )
                overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
                finish()
            }
            Constants.BANK_ACCOUNT -> {
                PrefrenceUtils.insertData(this, Constants.CLICKED_ACTION, "")
                PrefrenceUtils.insertData(this, Constants.LINK, "")
            }
            Constants.DOCUMENT -> {
                PrefrenceUtils.insertData(this, Constants.CLICKED_ACTION, "")
                PrefrenceUtils.insertData(this, Constants.LINK, "")
            }
            Constants.WEBVIEW -> {
                PrefrenceUtils.insertData(this, Constants.CLICKED_ACTION, "")
                startActivity(
                    Intent(this, NotificationViewActivity::class.java)
                        .putExtra(Constants.TYPE, type)
                        .putExtra(Constants.LINK, PrefrenceUtils.retriveData(this, Constants.LINK))
                )
                overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
            }
            Constants.REFERRAL ->{
                PrefrenceUtils.insertData(this, Constants.CLICKED_ACTION, "")
                startActivity(Intent(this, MainActivity::class.java).putExtra(Constants.TYPE, type))
                overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
            }

        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun intialiseFireBase() {
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


}