package com.vahan.mitra_playstore.view.refer.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.ItemInviteContactsBinding
import com.vahan.mitra_playstore.models.ContactModel
import com.vahan.mitra_playstore.view.refer.view.ui.ReferralInviteFriendsFragment
import java.util.*

class InviteAdapter(
    private val activity: ReferralInviteFriendsFragment,
    private val listContacts: List<ContactModel>
) : RecyclerView.Adapter<InviteAdapter.ViewHolder>(), Filterable {
    var listContactsFilterable = ArrayList<ContactModel>()

    init {
        listContactsFilterable = listContacts as ArrayList<ContactModel>
    }

    private var isSelectedAll = true


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding: ItemInviteContactsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_invite_contacts, parent, false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listContactsFilterable[position]
        holder.binding.apply {
            tvName.text = item.name
            tvPhNo.text = item.phNo
            checkbox.isChecked = item.isChecked
            checkbox.setOnClickListener {
                if (activity.listInvitedSelected.size <= 9 && !listContactsFilterable[position].isChecked) {
                    listContactsFilterable[position].isChecked = true
                    activity.listInvitedSelected.add(
                        ContactModel(
                            item.name,
                            item.phNo,
                            true
                        )
                    )
                    Log.d("tag", "Enable")

                } else {
                    listContactsFilterable[position].isChecked = false
                    activity.listInvitedSelected.remove(
                        ContactModel(
                            item.name,
                            item.phNo,
                            true
                        )
                    )
                    checkbox.isChecked = false
                    Log.d("tag", "Disable")

                }
                Log.d("data", "onBindViewHolder: " + activity.listInvitedSelected.size)
            }

        }

    }


    override fun getItemCount(): Int {
        return listContactsFilterable.size
    }

    class ViewHolder(itemBinding: ItemInviteContactsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: ItemInviteContactsBinding = itemBinding
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                listContactsFilterable = if (charSearch.isEmpty()) {
                    listContacts as ArrayList<ContactModel>
                } else {
                    val resultList = ArrayList<ContactModel>()
                    for (row in listContacts) {
                        if (row.name.lowercase(Locale.getDefault()).contains(
                                constraint.toString()
                                    .lowercase(Locale.getDefault())
                            )
                        ) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = listContactsFilterable
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listContactsFilterable = results?.values as ArrayList<ContactModel>
                notifyDataSetChanged()
            }
        }
    }

    fun unselectAll() {
        activity.listInvitedSelected.clear()
        for (i in 0 until listContactsFilterable.size) {
            listContactsFilterable[i].isChecked = false
        }
        Log.d("data", "onBindViewHolder: " + activity.listInvitedSelected.size)
        notifyDataSetChanged()
    }

    fun disableCheckBox() {
        for (i in 0 until listContactsFilterable.size) {
            listContactsFilterable[i].isChecked = false
        }
    }

}