package com.vahan.mitra_playstore.view.jobsmarketplace.docupload.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.JmDocUploadStatusBinding
import com.vahan.mitra_playstore.view.jobsmarketplace.docupload.datamodels.JMDocUploadStatusDTO


class JMDocUploadStatusAdapter(
    private val context: Context,
    private val item: List<JMDocUploadStatusDTO>,
    private val imageType: String,
) : RecyclerView.Adapter<JMDocUploadStatusAdapter.MyViewHolder>() {

    class MyViewHolder(itemBinding: JmDocUploadStatusBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: JmDocUploadStatusBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding: JmDocUploadStatusBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.jm_doc_upload_status, parent, false
        )
        return MyViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.binding.apply {
            docStatusTxt.text = item[position].text
            when (item[position].status) {
                "success" -> {
                    setImageFromStaticSource(R.drawable.ic_green_tick2, docStatusCircleImg)
                }
                "failed" -> {
                    setImageFromStaticSource(R.drawable.ic_failed, docStatusCircleImg)
                }
                else -> {
                    setImageFromStaticSource(R.drawable.ic_ellipse_grey, docStatusCircleImg)
                }
            }
        }

        if (position == item.size - 1) {
            holder.binding.docStatusLine.visibility = View.GONE
        } else {
            holder.binding.docStatusLine.visibility = View.VISIBLE
        }

    }

    private fun setImageFromStaticSource(imageSrc: Int, imageView: ImageView) {
//        val resID: Int = context.resources.getIdentifier(imageSrc, "drawable", context.packageName)
        imageView.setImageResource(imageSrc)
    }

    override fun getItemCount(): Int {
        return item.size ?: 0
    }
}