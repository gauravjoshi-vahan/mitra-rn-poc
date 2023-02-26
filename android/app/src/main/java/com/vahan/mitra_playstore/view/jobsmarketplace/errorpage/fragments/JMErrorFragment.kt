package com.vahan.mitra_playstore.view.jobsmarketplace.errorpage.fragments

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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentJmErrorBinding
import com.vahan.mitra_playstore.network.SharedViewModel
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.view.HomeActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [JMErrorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class JMErrorFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentJmErrorBinding
    private lateinit var viewSharedViewModel: SharedViewModel
    private lateinit var dialogLoader: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_jm_error, container, false)
        initView()
        return binding.root
    }

    private fun initView(){
        viewSharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        UXCam.setAutomaticScreenNameTagging(false)
        UXCam.tagScreenName("Error Fragment")
//        initLoader()
        binding.tvBackToHome.setOnClickListener {
//            dialogLoader.show()
            requireContext().startActivity(
                Intent(
                    requireContext(), HomeActivity::class.java
                ).putExtra("link", Constants.REDIRECTION_URL)
//                ).putExtra("link", Constants.REDIRECTION_URL_STRING)
            )
        }
    }

    fun initLoader() {
        dialogLoader = Dialog(requireContext())
        dialogLoader.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogLoader.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogLoader.setCancelable(false)
        dialogLoader.setContentView(R.layout.layout_loader)
        val imageViewAnimation: ImageView = dialogLoader.findViewById<ImageView>(R.id.animate_icon)
        val rotate = RotateAnimation(0F, 180F, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 2000
        rotate.interpolator = LinearInterpolator()
        imageViewAnimation.startAnimation(rotate)
    }
}