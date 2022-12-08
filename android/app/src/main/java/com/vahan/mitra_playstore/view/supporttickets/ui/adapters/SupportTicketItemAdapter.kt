package com.vahan.mitra_playstore.view.supporttickets.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.moengage.core.Properties
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.SupportTicketItemBinding
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.captureAllEvents
import com.vahan.mitra_playstore.view.supporttickets.datamodels.StatusMappingModel
import com.vahan.mitra_playstore.view.supporttickets.datamodels.SupportTicketDTO
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class SupportTicketItemAdapter(
    private val context: Context,
    private val allTicketsData: List<SupportTicketDTO.Result>
) : RecyclerView.Adapter<SupportTicketItemAdapter.MyViewHolder>() {
    val statusMap = mapOf(2 to "Open", 3 to "Pending", 4 to "Resolved", 5 to "Closed")
    var statusMappingData = ArrayList<StatusMappingModel>()

    class MyViewHolder(supportTicketItemBinding: SupportTicketItemBinding) :
        RecyclerView.ViewHolder(supportTicketItemBinding.root) {
        val binding: SupportTicketItemBinding = supportTicketItemBinding
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MyViewHolder {
        val supportTicketItemBinding: SupportTicketItemBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.support_ticket_item, parent, false
            )
        initStatusMapping()
        return MyViewHolder(supportTicketItemBinding)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        val item = allTicketsData[position]
        holder.binding.apply {
            statusMappingData.forEachIndexed{ind, element ->
                if(element.status == item.status){
                    ticketStatus.text = element.statusText
                    ticketStatus.setTextColor(Color.parseColor(element.color))
                }
                else {
                    ticketStatus.text = element.statusText
                    ticketStatus.setTextColor(Color.parseColor("#4f575e"))
                }

            }
            ticketSubject.text = item.custom_fields?.cf_payment_issues_test ?: item.subject
            val updatedAtDateTime = convertToNewFormat( item.updated_at )
            ticketUpdatedAtValue.text = "${
                SimpleDateFormat("dd", Locale.getDefault()).format(updatedAtDateTime!!)
            } ${SimpleDateFormat("MMMM", Locale.getDefault()).format(updatedAtDateTime)} ${
                SimpleDateFormat("yyyy", Locale.getDefault()).format(updatedAtDateTime)} ${ "," } ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(updatedAtDateTime)}"
            ticketIDValue.text = "#${item.id.toString()}"
            val createdOnDateTime = convertToNewFormat( item.created_at )
            createdOnValue.text = "${
                SimpleDateFormat("dd", Locale.getDefault()).format(createdOnDateTime!!)
            } ${SimpleDateFormat("MMMM", Locale.getDefault()).format(createdOnDateTime)} ${
                SimpleDateFormat("yyyy", Locale.getDefault()).format(createdOnDateTime)}"
            ticketTypeValue.text = item.type
        }
        clickListeners(holder)
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(ParseException::class)
    private fun convertToNewFormat(dateStr: String?): Date? {
        val utc = TimeZone.getTimeZone("UTC")
        val sourceFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val destFormat = SimpleDateFormat("EEE yyyy-MM-dd HH:mm a")
        sourceFormat.timeZone = utc
        return sourceFormat.parse(dateStr!!)
    }

    private fun initStatusMapping(){
        statusMappingData.clear()
        val openStatusMapping = StatusMappingModel("#F13333", 2, "Open")
        val pendingStatusMapping = StatusMappingModel("#EE8102", 3, "Pending")
        val resolvedStatusMapping = StatusMappingModel("#04A7BD", 4, "Resolved")
        val closedStatusMapping = StatusMappingModel("#27C174", 5, "Closed")

        statusMappingData.add(openStatusMapping)
        statusMappingData.add(pendingStatusMapping)
        statusMappingData.add(resolvedStatusMapping)
        statusMappingData.add(closedStatusMapping)
    }

    private fun toTitleCase(string: String?): String? {
        // Check if String is null
        if (string == null) {
            return null
        }
        var whiteSpace = true
        val builder = StringBuilder(string) // String builder to store string
        val builderLength = builder.length
        // Loop through builder
        for (i in 0 until builderLength) {
            val c = builder[i] // Get character at builders position
            if (whiteSpace) {
                // Check if character is not white space
                if (!Character.isWhitespace(c)) {

                    // Convert to title case and leave whitespace mode.
                    builder.setCharAt(i, c.titlecaseChar())
                    whiteSpace = false
                }
            } else if (Character.isWhitespace(c)) {
                whiteSpace = true // Set character is white space
            } else {
                builder.setCharAt(i, c.lowercaseChar()) // Set character to lowercase
            }
        }
        return builder.toString() // Return builders text
    }

    private fun clickListeners(holder: SupportTicketItemAdapter.MyViewHolder) {
        holder.binding.apply {
            ticketHeader.setOnClickListener {
                if (ticketBody.visibility == View.VISIBLE) {
                    ticketBody.visibility = View.GONE
                    ticketExpandBtn.rotation = 90F
                } else {
                    setInstrumentation(Constants.TICKET_EXPANDED)
                    ticketBody.visibility = View.VISIBLE
                    ticketExpandBtn.rotation = 270F
                }
            }
        }
    }

    private fun setInstrumentation(inp: String) {
        val properties = Properties()
        val attribute = HashMap<String, Any>()

        captureAllEvents(
            context,
            inp,
            attribute,
            properties
        )
    }

    override fun getItemCount(): Int {
        return allTicketsData.size ?: 0
    }
}