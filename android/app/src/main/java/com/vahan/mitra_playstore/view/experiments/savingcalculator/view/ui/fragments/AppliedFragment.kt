package com.vahan.mitra_playstore.view.experiments.savingcalculator.view.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentAppliedBinding
import java.util.*
import kotlin.concurrent.schedule

class AppliedFragment : Fragment() {

    lateinit var binding : FragmentAppliedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_applied, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        Timer().schedule(4000) {
            lifecycleScope.launchWhenResumed {
                findNavController().navigate(R.id.nav_selected_plan_fragment)
            }
        }
    }


}