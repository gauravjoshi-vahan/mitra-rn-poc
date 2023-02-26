package com.vahan.mitra_playstore.view

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.util.Log
import android.widget.Toast
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.datatheorem.android.trustkit.TrustKit
import com.facebook.react.ReactApplication
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.microsoft.codepush.react.CodePush
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.MoEngage
import com.moengage.core.config.NotificationConfig
import com.moengage.core.model.AppStatus
import com.moengage.firebase.MoEFireBaseHelper
import com.moengage.inapp.MoEInAppHelper
import com.moengage.pushbase.MoEPushHelper
import com.scottyab.rootbeer.RootBeer
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.services.CustomPushMessageListener
import com.vahan.mitra_playstore.services.FcmEventListener
import com.vahan.mitra_playstore.services.InAppCallback
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.LocaleManager
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import dagger.hilt.android.HiltAndroidApp
import java.util.*


@HiltAndroidApp
class BaseApplication : Application() {

    private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
    override fun onCreate() {
        instance = this
        super.onCreate()
        StrictMode.setVmPolicy(
            VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build()
        )
        initFirebaseRemoteConfig()
        setupUXCAM()
        setupMoengage()
        setupBlitzLlamaSDK()
        trustKitInitialized()
    }


    private fun trustKitInitialized() {
//        TrustKit.initializeWithNetworkSecurityConfiguration(this)  //comment for stg
    }

    private fun setupBlitzLlamaSDK() {
        BlitzLlamaSDK.init(this, "key_Bm7b3o2IMpAuxIh", true)
    }

    private fun setupUXCAM() {
       val rootBeer = RootBeer(applicationContext)
//        check(!rootBeer.isRooted)
        UXCam.startWithKey("g5afbnf7766lao2")
    }

    private fun setupMoengage() {
        val moEngage = MoEngage.Builder(this, "A7BUJ1RRESMUMVUOLEOLJ1XD")
            .configureNotificationMetaData(
                NotificationConfig(
                    R.drawable.dialog_icon,
                    R.drawable.dialog_icon,
                    R.color.white,
                    "sound",
                    isMultipleNotificationInDrawerEnabled = true,
                    isBuildingBackStackEnabled = true,
                    isLargeIconDisplayEnabled = true
                )
            )
            .build()
        MoEngage.initialise(moEngage)
        trackInstallOrUpdate()

        // FCM event listener
        MoEFireBaseHelper.getInstance().addEventListener(FcmEventListener())

        // Setting CustomPushMessageListener for notification customisation
        MoEPushHelper.getInstance().messageListener = CustomPushMessageListener()
        MoEInAppHelper.getInstance().registerListener(InAppCallback())
    }

    private fun trackInstallOrUpdate() {
        //keys are just sample keys, use suitable keys for the apps
        val preferences = getSharedPreferences(Constants.DEMO_APP, 0)
        var appStatus = AppStatus.INSTALL
        if (preferences.getBoolean(Constants.HAS_SENT_INSTALL, false)) {
            if (preferences.getBoolean(Constants.EXISTING, false)) {
                appStatus = AppStatus.UPDATE
            }
            MoEHelper.getInstance(applicationContext).setAppStatus(appStatus)
            preferences.edit().putBoolean(Constants.HAS_SENT_INSTALL, true).apply()
            preferences.edit().putBoolean(Constants.EXISTING, true).apply()
        }
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.setLocale(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.setLocale(this)
    }

    companion object {
        var instance: BaseApplication? = null
            private set

        @JvmStatic
        val context: Context?
            get() = instance
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
        mFirebaseRemoteConfig?.fetchAndActivate()?.addOnCompleteListener {
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
                    Constants.FRESHCHAT_ENABLE_CONDITION_REMOTE_CONFIG,
                    mFirebaseRemoteConfig?.getString(Constants.FRESHCHAT_ENABLE_CONDITION)
                        .toString()
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

                Log.d(
                    "UPDATES_VALUE",
                    "NUDGE: " + PrefrenceUtils.retriveData(
                        this,
                        Constants.NUDGE_ICON_TEXT_REMOTE_CONFIG
                    )
                )

            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }?.addOnFailureListener { e: Exception? ->
            Toast.makeText(this, e?.message, Toast.LENGTH_SHORT).show()
        }
    }
}
