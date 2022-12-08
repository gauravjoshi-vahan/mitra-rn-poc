package com.vahan.mitra_playstore.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.vahan.mitra_playstore.R



class DemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.milestone_tracker_new_adapter)
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        val charSequences: MutableList<CharSequence> = ArrayList()
        charSequences.add("0")
        charSequences.add("7000")
        val charSequenceArray = charSequences.toTypedArray()
//        sb_single5.tickMarkTextArray = charSequenceArray
//        sb_single5.setRange(0F,7000F)
//        sb_single5.setOnTouchListener { _, _ -> true }
//        val anim = ValueAnimator.ofFloat(0F, 2000F)
//        anim.duration = 3000
//        anim.addUpdateListener { animation ->
//            val animProgress = animation.animatedValue as Float
//            sb_single5?.setProgress(animProgress)
//        }
//        anim.start()
//        sb_single5.setOnRangeChangedListener (object : OnRangeChangedListener{
//            override fun onRangeChanged(
//                view: RangeSeekBar,
//                leftValue: Float,
//                rightValue: Float,
//                isFromUser: Boolean
//            ) {
//                view.leftSeekBar.setIndicatorText("$$leftValue")
//            }
//
//            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
//
//            }
//
//            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
//
//            }
//
//        })
    }
}