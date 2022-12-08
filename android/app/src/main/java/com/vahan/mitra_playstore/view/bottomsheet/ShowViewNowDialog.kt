package com.vahan.mitra_playstore.view.bottomsheet

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.vahan.mitra_playstore.R


class ShowViewNowDialog(
    context: Context,
    private val param: Array<String>
) :
    AlertDialog(context, R.style.CustomBottomSheetDialogTheme) {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_now_doc)
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val img1 = findViewById<ImageView>(R.id.panCard_img)
        val img2 = findViewById<ImageView>(R.id.adhaar_img)

        if(param.size==1){
            Glide.with(context).load(param[0]).into(img1)
            img2.visibility = View.GONE
        }else{
            Glide.with(context).load(param[0]).into(img1)
            Glide.with(context).load(param[1]).into(img2)
            img2.visibility = View.VISIBLE
        }

    }


}