package com.vahan.mitra_playstore.view.loan.view.ui.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestBuilder
import com.vahan.mitra_playstore.databinding.CardLoansBinding
import com.vahan.mitra_playstore.interfaces.Listener
import com.vahan.mitra_playstore.models.LoanList
import com.vahan.mitra_playstore.utils.GlideApp
import com.vahan.mitra_playstore.utils.SvgSoftwareLayerSetter


class LoanResultAdapter(private val listener: Listener, private val requireActivity: FragmentActivity) :
    ListAdapter<LoanList.LoanApplication, LoanResultAdapter.LoanResultViewHolder>(Diff) {

    inner class LoanResultViewHolder(private val binding: CardLoansBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(response: LoanList.LoanApplication) {
            binding.apply {
                amount.text = response.emi + "/" + response.monthLang
                status.text = response.loanStatus
                loadType.text = response.purpose

                val requestBuilder: RequestBuilder<PictureDrawable> = GlideApp.with(requireActivity
                ).`as`(PictureDrawable::class.java)
                    .listener(SvgSoftwareLayerSetter())
                val uri = Uri.parse(response.loanProviderLogo)
                requestBuilder.load(uri).into(image)
                root.setOnClickListener {
                    listener.onClick(adapterPosition)
                }
            }
        }
    }

    object Diff : DiffUtil.ItemCallback<LoanList.LoanApplication>() {
        override fun areItemsTheSame(
            oldItem: LoanList.LoanApplication,
            newItem: LoanList.LoanApplication
        ): Boolean {
            return oldItem.purpose == newItem.purpose
        }

        override fun areContentsTheSame(
            oldItem: LoanList.LoanApplication,
            newItem: LoanList.LoanApplication
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanResultViewHolder {
        return LoanResultViewHolder(
            CardLoansBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LoanResultViewHolder, position: Int) {
        val data = currentList[position]
        if (data != null) {
            holder.bind(data)
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }
}