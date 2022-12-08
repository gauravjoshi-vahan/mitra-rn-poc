package com.vahan.mitra_playstore.view.supporttickets.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.moengage.core.Properties
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentSupportTicketBinding
import com.vahan.mitra_playstore.network.SharedViewModel
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.utils.captureAllEvents
import com.vahan.mitra_playstore.view.supporttickets.datamodels.SupportTicketDTO
import com.vahan.mitra_playstore.view.supporttickets.ui.adapters.SupportTicketItemAdapter
import kotlinx.android.synthetic.main.fragment_support_ticket.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
class SupportTicket : Fragment() {
    private lateinit var binding: FragmentSupportTicketBinding
    private lateinit var allTicketsList: ArrayList<SupportTicketDTO.Result>
    private val ticketListOrder: HashMap<String, Int> = hashMapOf(
        "Open" to 0,
        "Pending" to 1,
        "Resolved" to 2,
        "Closed" to 3
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_support_ticket,
            container,
            false
        )
        setInstrumentation(Constants.TICKET_STATUS_PAGE_VIEWED)
        getAllTicketsFromFreshdesk()
        clickListeners()
        return binding.root
    }

    private fun getAllTicketsFromFreshdesk() {
        showLoading()
        val viewSharedModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        val queryText =
            "\"cf_client_id:" + PrefrenceUtils.retriveData(requireActivity(), Constants.PHONENUMBER)
                .takeLast(10) + "\""
        //       +  "8281288526\""

        viewSharedModel.callFreshDeskAPI(queryText).observe(viewLifecycleOwner) {
            if (it?.results !== null) {
                hideLoading()
                if (it?.results.size > 0) {
                    allTicketsList = sortTickets(it.results)
                    allTicketsList = sortTickets(allTicketsList)
                    binding.supportTicketScrollable.visibility = View.VISIBLE
                    binding.root.noTicketsView.visibility = View.GONE
                    binding.rvTicket.adapter =
                        SupportTicketItemAdapter(requireActivity(), allTicketsList)
//                Log.d("FRESHDESK Unique", allTicketsList[0].type.toString())
                } else {

                    if (it?.results?.size == 0) {
                        binding.rvTicketItemParent.visibility = View.GONE
                        binding.noTicketsViewParent.visibility = View.VISIBLE
                    }
                }
            } else {
                hideLoading()
                Navigation.findNavController(binding.root)
                    .navigate(R.id.nav_error_fragment)
            }
        }
    }

    private fun sortTickets(tickets: ArrayList<SupportTicketDTO.Result>): ArrayList<SupportTicketDTO.Result> {
        val comparator = Comparator { o1: SupportTicketDTO.Result, o2: SupportTicketDTO.Result ->
            return@Comparator o1.status!! - o2.status!!
        }
        val copy = arrayListOf<SupportTicketDTO.Result>().apply { addAll(tickets) }
        copy.sortWith(comparator)
        return copy
    }

    private fun setInstrumentation(inp: String) {
        val properties = Properties()
        val attribute = HashMap<String, Any>()

       captureAllEvents(
            requireContext(),
            inp,
            attribute,
            properties
        )
    }

    private fun showLoading() {
        binding.apply {
            showLoaderContent.visibility = View.VISIBLE
            hideLoaderContent.visibility = View.GONE
            shimmerLayout1.visibility = View.VISIBLE
            shimmerLayout1.startShimmerAnimation()
        }
    }

    private fun hideLoading() {
        binding.apply {
            showLoaderContent.visibility = View.GONE
            hideLoaderContent.visibility = View.VISIBLE
            shimmerLayout1.visibility = View.GONE
            shimmerLayout1.stopShimmerAnimation()
        }
    }

    private fun clickListeners() {
        binding.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.noTicketsView.goHomeBtn.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.nav_home_fragment)
        }
    }
}