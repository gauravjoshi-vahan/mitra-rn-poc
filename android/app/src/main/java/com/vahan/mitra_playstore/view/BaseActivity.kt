package com.vahan.mitra_playstore.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.utils.LocaleManager


open class BaseActivity : AppCompatActivity() {
    protected var language = ""
    protected var dialogLoader: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocaleManager.setLocale(base))
    }

    override fun onResume() {
        super.onResume()
        LocaleManager.setLocale(baseContext)
    }

    override fun onPause() {
        super.onPause()
        LocaleManager.setLocale(baseContext)
    }

    open fun initLoader(context: Context?) {
        dialogLoader = Dialog(context!!)
        dialogLoader!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialogLoader!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogLoader!!.setCancelable(false)
        dialogLoader!!.setContentView(R.layout.layout_loader)
        val imageViewAnimation = dialogLoader!!.findViewById<ImageView>(R.id.animate_icon)
        val rotate = RotateAnimation(
            0F, 180F, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 2000
        rotate.interpolator = LinearInterpolator()
        imageViewAnimation.startAnimation(rotate)
    }
    companion object {
        var instance: BaseActivity? = null
            private set

        @JvmStatic
        val context: Context?
            get() = instance
    }


}