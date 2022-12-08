package com.vahan.mitra_playstore.view.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentHelpAndSettingsBinding
import com.vahan.mitra_playstore.view.profile.adapter.HelpAndSupportAdapter
import com.vahan.mitra_playstore.view.profile.viewmodel.ItemsViewModel


class HelpAndSupportFragment : Fragment() {
    private lateinit var binding: FragmentHelpAndSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_help_and_settings,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        onClick()
    }

    private fun onClick() {
        binding.ivBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun initView() {
        setUpAdapter()
    }

    private fun setUpAdapter() {
        binding.rvList.layoutManager = LinearLayoutManager(requireContext())
        val data = ArrayList<ItemsViewModel>()

//        context?.let { ItemsViewModel(R.drawable.ic_faq, it.getString(R.string.faqs)) }
//            ?.let { data.add(it) }
        context?.let { ItemsViewModel(R.drawable.ic_chat, it.getString(R.string.chat_with_us)) }
            ?.let { data.add(it) }
        context?.let { ItemsViewModel(R.drawable.ic_talk, it.getString(R.string.talk_to_us)) }
            ?.let { data.add(it) }
        context?.let { ItemsViewModel(R.drawable.ic_complaint, it.getString(R.string.complaint_status)) }
            ?.let { data.add(it) }

        val adapter = context?.let { HelpAndSupportAdapter(it,data) }
        binding.rvList.adapter = adapter
    }
}