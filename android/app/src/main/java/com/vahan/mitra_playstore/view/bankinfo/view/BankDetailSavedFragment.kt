package com.vahan.mitra_playstore.view.bankinfo.view

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentBankAccountAddedBinding
import java.util.*
import kotlin.concurrent.schedule

class BankDetailSavedFragment:Fragment() {
    lateinit var binding: FragmentBankAccountAddedBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_bank_account_added, container, false)
        @Suppress("DEPRECATION")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {

            requireActivity().window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        initView();
        return binding.root
    }
    private fun initView() {
        UXCam.setAutomaticScreenNameTagging(false)
        UXCam.tagScreenName(resources.getString(R.string.bank_account_added))
        Timer().schedule(40000) {
            lifecycleScope.launchWhenResumed {
                findNavController().navigate(R.id.nav_fragment_addBank_view)
            }
        }

    }
}