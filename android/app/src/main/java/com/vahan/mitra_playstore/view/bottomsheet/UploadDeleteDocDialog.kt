package com.vahan.mitra_playstore.view.bottomsheet

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.vahan.mitra_playstore.R


class UploadDeleteDocDialog(
    context: Context, val delete: () -> Unit
) :
    AlertDialog(context, R.style.CustomBottomSheetDialogTheme) {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.delete_doc)
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        findViewById<TextView>(R.id.delete_documents).setOnClickListener {
            delete()
            dismiss()
        }

        findViewById<ImageView>(R.id.cross_dialog).setOnClickListener {
            dismiss()
        }
        findViewById<TextView>(R.id.close_dialog).setOnClickListener {
            dismiss()
        }
    }


}