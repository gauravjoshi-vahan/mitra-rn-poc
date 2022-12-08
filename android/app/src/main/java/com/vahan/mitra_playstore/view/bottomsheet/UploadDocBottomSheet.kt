package com.vahan.mitra_playstore.view.bottomsheet

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vahan.mitra_playstore.R


class UploadDocBottomSheet(
    context: Context, val uploadTypeIsCamera: (Boolean) -> Unit
) :
    BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme) {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doc_upload)
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        findViewById<LinearLayout>(R.id.click_cam)?.setOnClickListener {
            uploadTypeIsCamera(true)
            dismiss()
        }
        findViewById<LinearLayout>(R.id.click_gal)?.setOnClickListener {
            uploadTypeIsCamera(false)
            dismiss()
        }
    }


}