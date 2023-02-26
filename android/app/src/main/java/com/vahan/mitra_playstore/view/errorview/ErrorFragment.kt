package com.vahan.mitra_playstore.view.errorview

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentErrorBinding
import com.vahan.mitra_playstore.models.kotlin.Transaction
import com.vahan.mitra_playstore.network.SharedViewModel
import com.vahan.mitra_playstore.utils.ApiState
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.HomeActivity
import com.vahan.mitra_playstore.view.earn.viewModel.EarnViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ErrorFragment : Fragment() {

    private lateinit var binding: FragmentErrorBinding
    private lateinit var viewSharedViewModel: SharedViewModel
    private lateinit var dialogLoader: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_error, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        viewSharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        UXCam.setAutomaticScreenNameTagging(false)
        UXCam.tagScreenName("Error Fragment")
        initLoader()
        binding.tvBackToHome.setOnClickListener {
            dialogLoader.show()
            if (PrefrenceUtils.retriveDataInBoolean(requireContext(), Constants.CHECKFORPAYROLL)) {
                getTransactionDetails(1, 20, "")
            } else {
                requireContext().startActivity(
                    Intent(
                        requireContext(), HomeActivity::class.java
                    ).putExtra("link", Constants.REDIRECTION_URL)
                )
            }
        }
    }

    private fun getTransactionDetails(startPage: Int, perPage: Int, type: String) {
        val viewEarnViewModel = ViewModelProvider(this)[EarnViewModel::class.java]
        val transaction = Transaction(startPage, perPage, type, listOf(), listOf())
        lifecycleScope.launchWhenStarted {
            viewEarnViewModel.getTransactionDetails(transaction).collect {
                when (it) {
                    ApiState.Loading -> {
                        dialogLoader.show()
                    }
                    is ApiState.Success -> {
                        if (dialogLoader.isShowing) {
                            dialogLoader.dismiss()
                        }
                        Navigation.findNavController(binding.root).navigate(R.id.nav_home_fragment)
                    }
                    is ApiState.Failure -> {
                        if (dialogLoader.isShowing) {
                            dialogLoader.dismiss()
                        }
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.please_try_after_sometime),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        }
    }

    fun initLoader() {
        dialogLoader = Dialog(requireContext())
        dialogLoader.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogLoader.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogLoader.setCancelable(false)
        dialogLoader.setContentView(R.layout.layout_loader)
        val imageViewAnimation: ImageView = dialogLoader.findViewById<ImageView>(R.id.animate_icon)
        val rotate = RotateAnimation(
            0F, 180F, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 2000
        rotate.interpolator = LinearInterpolator()
        imageViewAnimation.startAnimation(rotate)
    }
}



