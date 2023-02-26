package com.vahan.mitra_playstore.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.freshchat.consumer.sdk.Freshchat
import com.google.firebase.analytics.FirebaseAnalytics
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.ActivityLanguageSelectionBinding
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.LocaleManager
import com.vahan.mitra_playstore.utils.PrefrenceUtils


class LanguageSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLanguageSelectionBinding
    private var checkCounterForSelection: Int? = 0
    private lateinit var fa: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this fragment
        // binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_language_selection)
        initView()
    }

    private fun initView() {
        setLocaleLanguage("en")
        UXCam.setAutomaticScreenNameTagging(true);
        fa = FirebaseAnalytics.getInstance(this@LanguageSelectionActivity)
        PrefrenceUtils.insertDataLang(this@LanguageSelectionActivity, Constants.LANGUAGE, "en")
        PrefrenceUtils.insertDataLang(
            this@LanguageSelectionActivity, Constants.LANGUAGE_API_RESP, "english")
        binding.llOneEnglish.setOnClickListener {
            binding.llOneEnglish.setBackgroundColor(
                ContextCompat
                    .getColor(
                        this@LanguageSelectionActivity,
                        R.color.unread_notification_background
                    )
            )
            binding.llTwoHindi.setBackgroundColor(
                ContextCompat
                    .getColor(this@LanguageSelectionActivity, R.color.white)
            )
            binding.llThreeTelugu.setBackgroundColor(
                ContextCompat
                    .getColor(
                        this@LanguageSelectionActivity,
                        R.color.white
                    )
            )
            PrefrenceUtils.insertDataLang(this@LanguageSelectionActivity, Constants.LANGUAGE, "en")
            PrefrenceUtils.insertDataLang(
                this@LanguageSelectionActivity,
                Constants.LANGUAGE_API_RESP,
                "english"
            )
            checkCounterForSelection = 1
            setLocaleLanguage("en")
            changeLanguageEnglish()
        }

        binding.llTwoHindi.setOnClickListener {
            binding.llOneEnglish.setBackgroundColor(
                ContextCompat
                    .getColor(this@LanguageSelectionActivity, R.color.white)
            )
            binding.llTwoHindi.setBackgroundColor(
                ContextCompat
                    .getColor(
                        this@LanguageSelectionActivity,
                        R.color.unread_notification_background
                    )
            )
            binding.llThreeTelugu.setBackgroundColor(
                ContextCompat
                    .getColor(
                        this@LanguageSelectionActivity,
                        R.color.white
                    )
            )
            PrefrenceUtils.insertDataLang(this@LanguageSelectionActivity, Constants.LANGUAGE, "hi")
            PrefrenceUtils.insertDataLang(
                this@LanguageSelectionActivity,
                Constants.LANGUAGE_API_RESP,
                "hindi"
            )
            setLocaleLanguage("hi")
            changeLanguageHindi()
        }

        binding.llThreeTelugu.setOnClickListener {
            binding.llOneEnglish.setBackgroundColor(
                ContextCompat
                    .getColor(this@LanguageSelectionActivity, R.color.white)
            )
            binding.llTwoHindi.setBackgroundColor(
                ContextCompat
                    .getColor(
                        this@LanguageSelectionActivity,
                        R.color.white
                    )
            )
           binding.llThreeTelugu.setBackgroundColor(
            ContextCompat
                .getColor(
                    this@LanguageSelectionActivity,
                    R.color.unread_notification_background
                )
            )
            PrefrenceUtils.insertDataLang(this@LanguageSelectionActivity, Constants.LANGUAGE, "te")
            PrefrenceUtils.insertDataLang(
                this@LanguageSelectionActivity,
                Constants.LANGUAGE_API_RESP,
                "telugu"
            )
            setLocaleLanguage("te")
            changeLanguageTelugu()
        }

        binding.saveButton.setOnClickListener {
            setInstrumentationAtSave()
            startActivity(Intent(this, StarterScreen::class.java))
            overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
            finishAffinity()
        }

    }

    private fun setInstrumentationAtSave() {
        val bundleOne = Bundle()
        bundleOne.putString(Constants.SELECTED_LANGUAGE,
            PrefrenceUtils.retriveLangData(this@LanguageSelectionActivity, Constants.LANGUAGE))
        val propertiesOne = Properties()
        propertiesOne.addAttribute(Constants.SELECTED_LANGUAGE,
            PrefrenceUtils.retriveLangData(this@LanguageSelectionActivity, Constants.LANGUAGE))
        MoEHelper.getInstance(this).trackEvent(Constants.LANGUAGE_SELECTED, propertiesOne)
        fa.logEvent(Constants.LANGUAGE_SELECTED, bundleOne)
        val data = HashMap<String,String>()
        data[Constants.SELECTED_LANGUAGE] =
            PrefrenceUtils.retriveLangData(this@LanguageSelectionActivity, Constants.LANGUAGE)
        UXCam.logEvent(Constants.SELECTED_LANGUAGE, data)
        BlitzLlamaSDK.getSdkManager(this).triggerEvent(Constants.LANGUAGE_SELECTED)
        val attribute: HashMap<String, Any> = HashMap()
        attribute[Constants.SELECTED_LANGUAGE] =  PrefrenceUtils.retriveLangData(this@LanguageSelectionActivity, Constants.LANGUAGE)
        Freshchat.trackEvent(this, Constants.LANGUAGE_SELECTED, attribute);
    }


    private fun changeLanguageEnglish() {
        binding.langTvHeading.text = Constants.language_selection_en
        binding.langTvSubHeading.text = Constants.select_language_string_sub_string_en
        binding.saveButton.text = Constants.next_string_en
    }

    private fun changeLanguageHindi() {
        binding.langTvHeading.text = Constants.language_selection_hi
        binding.langTvSubHeading.text = Constants.select_language_string_sub_string_hi
        binding.saveButton.text = Constants.next_string_hi
    }

    private fun changeLanguageTelugu() {
        binding.langTvHeading.text = Constants.language_selection_te
        binding.langTvSubHeading.text = Constants.select_language_string_sub_string_te
        binding.saveButton.text = Constants.next_string_te
    }

    private fun setLocaleLanguage(lang: String) {
        LocaleManager.setNewLocale(applicationContext, lang)
    }

}