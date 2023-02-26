package com.vahan.mitra_playstore.view.profile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.freshchat.consumer.sdk.FaqOptions
import com.freshchat.consumer.sdk.Freshchat
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.view.profile.models.ItemsViewModel

class HelpAndSupportAdapter(private val context: Context, private val mList: List<ItemsViewModel>) :
    RecyclerView.Adapter<HelpAndSupportAdapter.ViewHolder>() {

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val icon: ImageView = itemView.findViewById(R.id.iv_icon)
        val title: TextView = itemView.findViewById(R.id.tv_title)
        val rlContainer: RelativeLayout = itemView.findViewById(R.id.rlContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.help_support_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemsViewModel = mList[position]
        holder.icon.setImageResource(itemsViewModel.image)
        holder.title.text = itemsViewModel.text


        holder.rlContainer.setOnClickListener {
           /* if(position==0){
                val tags: MutableList<String> = ArrayList()
                tags.add("newfaq")

                val faqOptions = FaqOptions()
                    .showFaqCategoriesAsGrid(false)
                    .showContactUsOnAppBar(true)
                    .showContactUsOnFaqScreens(false)
                    .showContactUsOnFaqNotHelpful(false)
                    .filterByTags(
                        tags,
                        "Test 2",
                        FaqOptions.FilterType.CATEGORY
                    ) //tags, filtered screen title, type


                Freshchat.showFAQs(context, faqOptions)
            }*/
            if(position==0){
                val tags: MutableList<String> = java.util.ArrayList()
                tags.add("newFaq")
                val faqOptions = FaqOptions()
                    .showFaqCategoriesAsGrid(false)
                    .showContactUsOnAppBar(false)
                    .showContactUsOnFaqScreens(true)
                    .showContactUsOnFaqNotHelpful(true)
                    .filterContactUsByTags(tags, "Test 2") //tags, filtered screen title
                Freshchat.showFAQs(context, faqOptions)
            }
            else if(position==1){
                Navigation.findNavController(holder.rlContainer).navigate(R.id.nav_help_support)
            }
            else if(position==2){
                Navigation.findNavController(holder.rlContainer).navigate(R.id.nav_support_ticket_fragment)
            }

        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

}