package com.vahan.mitra_playstore.view.documentupload.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide

import com.vahan.mitra_playstore.view.bottomsheet.ShowViewNowDialog
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentViewUploadBinding
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils


class ViewUploadFragment : Fragment() {

    private lateinit var binding : FragmentViewUploadBinding
    private var circularProgressDrawable: CircularProgressDrawable? = null
    private var param: Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_upload, container, false)
        initView()
        return binding.root
    }

    private fun initView() {

        circularProgressDrawable = CircularProgressDrawable(requireContext())
        circularProgressDrawable?.strokeWidth = 5f
        circularProgressDrawable?.centerRadius = 30f
        circularProgressDrawable?.start()
        Log.d("urlValuesCheck", "initView: "+ PrefrenceUtils.retriveData(requireContext(), Constants.AADHARCARDFRONT)+
                " "+PrefrenceUtils.retriveData(requireContext(),Constants.AADHARCARDBACK))
        Glide.with(requireActivity())
            .load(PrefrenceUtils.retriveData(requireContext(),Constants.AADHARCARDFRONT))
            .centerCrop()
            .placeholder(circularProgressDrawable)
            .into(binding.adharImg)
        Glide.with(requireActivity())
            .load(PrefrenceUtils.retriveData(requireContext(),Constants.AADHARCARDBACK))
            .centerCrop()
            .placeholder(circularProgressDrawable)
            .into(binding.adharImgB)
        Glide.with(requireActivity())
            .load(PrefrenceUtils.retriveData(requireContext(),Constants.PANCARDIMAGE))
            .centerCrop()
            .placeholder(circularProgressDrawable)
            .into(binding.panImg)

        if(PrefrenceUtils.retriveData(requireContext(),Constants.DL_FRONT_IMG).equals("")
            && PrefrenceUtils.retriveData(requireContext(),Constants.DL_BACK_IMG).equals("")){
            binding.dlCardRoot.visibility = View.GONE
        }else{
            binding.dlCardRoot.visibility = View.VISIBLE
            Glide.with(requireActivity())
                .load(PrefrenceUtils.retriveData(requireContext(),Constants.DL_FRONT_IMG))
                .centerCrop()
                .placeholder(circularProgressDrawable)
                .into(binding.dlImgA)
            Glide.with(requireActivity())
                .load(PrefrenceUtils.retriveData(requireContext(),Constants.DL_BACK_IMG))
                .centerCrop()
                .placeholder(circularProgressDrawable)
                .into(binding.dlImgB)
        }


        binding.ivBackButton.setOnClickListener { requireActivity().onBackPressed() }

        binding.viewNowTv.setOnClickListener{
            ShowViewNowDialog(
                requireContext(),
                arrayOf(PrefrenceUtils.retriveData(requireContext(),Constants.AADHARCARDFRONT),PrefrenceUtils.retriveData(requireContext(),Constants.AADHARCARDBACK))
            ).show()
        }
        binding.viewNowTvPanCard.setOnClickListener{
            ShowViewNowDialog(
                requireContext(),
                arrayOf(PrefrenceUtils.retriveData(requireContext(),Constants.PANCARDIMAGE))
            ).show()
        }
        binding.llcontainerViewOneDl.setOnClickListener {
            ShowViewNowDialog(
                requireContext(),
                arrayOf(PrefrenceUtils.retriveData(requireContext(),Constants.DL_FRONT_IMG),PrefrenceUtils.retriveData(requireContext(),Constants.DL_BACK_IMG))
            ).show()
        }


    }


}