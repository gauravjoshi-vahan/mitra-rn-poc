package com.vahan.mitra_playstore.view.notification.adapter

import android.annotation.SuppressLint
import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.ItemNotificationBinding


class NotificationAdapter() :
        RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    private lateinit var context: Context
    private lateinit var listItem: View
    private var qnt = 0

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {
        val itemBinding: ItemNotificationBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_notification, parent, false
        )
        return ViewHolder(itemBinding)
    }


    override fun getItemCount(): Int {
       return 10
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            if (position>4){
                conMain.setBackgroundResource(R.color.white)
                imgRead.visibility=View.GONE
            }
        }
    }


    class ViewHolder(itemBinding: ItemNotificationBinding) :
            RecyclerView.ViewHolder(itemBinding.root) {
        val binding: ItemNotificationBinding = itemBinding

    }
}