package com.vahan.mitra_playstore.view

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.ActivityGenericWelcomeBinding
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.activities.enternumberactivity.view.ui.EnterNumberActivity


class GenericWelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGenericWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_generic_welcome)
        binding = DataBindingUtil.setContentView(this, com.vahan.mitra_playstore.R.layout.activity_generic_welcome)
        initView()
    }

    private fun initView(){
        PrefrenceUtils.insertDataLang(this@GenericWelcomeActivity, Constants.LANGUAGE, "en")
        PrefrenceUtils.insertDataLang(
            this@GenericWelcomeActivity, Constants.LANGUAGE_API_RESP, "english")

        val textView = binding.welcomeScreenTxt
//        val word: Spannable = SpannableString("Top Delivery Jobs")
//
//        word.setSpan(
//            ForegroundColorSpan(ContextCompat.getColor(this, R.color.black_v2)),
//            0,
//            word.length,
//            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//        )

        textView.text = makeColouredText("Top Delivery Jobs \n", R.color.black_v2)
        textView.append(makeColouredText("near you ", R.color.default_200))
        textView.append(makeColouredText("with \n", R.color.black_v2))
        textView.append(makeColouredText("salary up to ", R.color.black_v2))
        textView.append(makeColouredText("₹30,000 \n", R.color.default_200))
        textView.append(makeColouredText("Start Earning in ", R.color.black_v2))
        textView.append(makeColouredText("24 hours!", R.color.default_200))

        binding.getStartedBtn.setOnClickListener {
            startActivity(Intent(this, EnterNumberActivity::class.java))
//            startActivity(Intent(this, ReactActivity::class.java))
        }
    }

    private fun makeColouredText(textStr: String, color: Int): Spannable{
        val word: Spannable = SpannableString(textStr)

        word.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, color)),
            0,
            word.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return word;
    }
}