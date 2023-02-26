package com.vahan.mitra_playstore.view.documentupload.ui

import android.app.Dialog
import android.content.Context
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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentUploadDocumentBinding
import com.vahan.mitra_playstore.models.EarnDataModelJava
import com.vahan.mitra_playstore.network.SharedViewModel
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.HomeActivity
import com.vahan.mitra_playstore.view.MainActivity
import com.vahan.mitra_playstore.view.bottomsheet.ShowViewNowDialog
import com.vahan.mitra_playstore.view.documentupload.viewmodel.UploadViewModels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UploadDocumentFragment : Fragment() {
    private var binding: FragmentUploadDocumentBinding? = null
    lateinit var viewModel: UploadViewModels
    lateinit var viewSharedModel: SharedViewModel
    lateinit var dialogLoader: Dialog
    private var global_check: String? = null
    private var cacheAadhaarFront: String? = null
    private var cacheAadhaarBack: String? = null
    private val cachePan: String? = null
    private var isAadhaarDocumentUpload = false
    private var isPanCardDocumentUpload = false
    private var isProfilePicDocumentUpload = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_upload_document, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.apply {
            global_check = getString("nav")
            PrefrenceUtils.insertData(requireContext(), "nav", global_check)
        }
        viewModel = ViewModelProvider(requireActivity())[UploadViewModels::class.java]
        viewSharedModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        initView()
    }

    private fun initView() {
        checkForProfilePic()
        checkForAadhar()
        checkForPan()
        checkForDL()
//        if (global_check == "Navigation") {
        initLoader(requireContext())
        profileApi()
//        }
        if (global_check == "is_job_seeker") {
            binding?.profilePicCard?.visibility = View.VISIBLE
        }
        UXCam.setAutomaticScreenNameTagging(false)
        UXCam.tagScreenName("DocumentUpload Fragment")
        clickListener()
    }

    private fun checkForDL() {
        if (viewModel.frontDLCard.isNotEmpty() && viewModel.backDLCard.isNotEmpty()) {
            // Dat Fetch
            binding?.uploadDlCard?.isEnabled = false
            binding?.uploadDlCard?.isFocusable = false
            binding?.uploadDlCard?.isClickable = false
            binding?.uploadDlCard?.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.light_color_heading
                )
            )
            CoroutineScope(Dispatchers.IO).launch {
                callApi("DRIVING_LICENSE")
            }

        }
    }

    private fun checkForProfilePic() {
        if (viewModel.profilePicImagePath.isNotEmpty()) {
            // Dat Fetch
            binding?.uploadProfilePic?.isEnabled = false
            binding?.uploadProfilePic?.isFocusable = false
            binding?.uploadProfilePic?.isClickable = false
            binding?.uploadProfilePic?.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.light_color_heading
                )
            )
            CoroutineScope(Dispatchers.IO).launch {
                callApi("PROFILE_PIC")
            }

        }
    }

    private fun checkForPan() {
        if (viewModel.imagePath.isNotEmpty()) {
            // Dat Fetch
            binding?.uploadPanCard?.isEnabled = false
            binding?.uploadPanCard?.isFocusable = false
            binding?.uploadPanCard?.isClickable = false
            binding?.uploadPanCard?.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.light_color_heading
                )
            )
            CoroutineScope(Dispatchers.IO).launch {
                callApi("PAN_CARD")
            }

        }
    }

    private fun checkForAadhar() {
        if (viewModel.frontAdhaarCard.isNotEmpty() && viewModel.backAdhaarCard.isNotEmpty()) {
            // Dat Fetch
            binding?.uploadAdhaar?.isEnabled = false
            binding?.uploadAdhaar?.isFocusable = false
            binding?.uploadAdhaar?.isClickable = false
            binding?.uploadAdhaar?.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.light_color_heading
                )
            )
            CoroutineScope(Dispatchers.IO).launch {
                callApi("AADHAAR_CARD")
            }

        }
    }

    private fun profileApi() {
        dialogLoader.show()
        viewSharedModel.nonPayrollDocs.observe(viewLifecycleOwner) { earnDataModel: EarnDataModelJava.Documents ->
            when {
                earnDataModel.statusCode in 400..499 -> {
                    dialogLoader.dismiss()
                    Toast.makeText(
                        requireActivity(),
                        R.string.something_went_wrong,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                earnDataModel.statusCode >= 500 -> {
                    dialogLoader.dismiss()
//                    Navigation.findNavController(binding!!.root).navigate(R.id.nav_error_fragment)
                    //show toast here for non_payroll
                    if (PrefrenceUtils.retriveData(requireContext(), "nav") == "Navigation") {
                        Toast.makeText(
                            requireContext(),
                            R.string.something_went_wrong,
                            Toast.LENGTH_SHORT
                        ).show()
                        requireContext().startActivity(
                            Intent(
                                requireContext(),
                                HomeActivity::class.java
                            ).putExtra("link", Constants.REDIRECTION_URL)
                        )
                    } else {
                        findNavController().navigate(R.id.nav_error_fragment)
                    }
                }
                else -> {
                    dialogLoader.dismiss()
                    var imageUrlProfilePic: String?
                    var imageUrlPan: String?
                    var imageUrlBackAadhaar: String?
                    var imageUrlFrontAadhaar: String?
                    if (earnDataModel.documents != null) {
                        if (earnDataModel.documents.size > 0) {
                            for (i in earnDataModel.documents.indices) {
                                if (earnDataModel.documents[i].type.equals(
                                        "Aadhaar Image",
                                        ignoreCase = true
                                    )
                                ) {
                                    if (earnDataModel.documents[i].otherSideImageUrl != null) {
                                        imageUrlBackAadhaar =
                                            earnDataModel.documents[i].otherSideImageUrl
                                        PrefrenceUtils.insertData(
                                            requireActivity(),
                                            Constants.AADHARCARDBACK,
                                            imageUrlBackAadhaar
                                        )
                                    }
                                    imageUrlFrontAadhaar =
                                        earnDataModel.documents[i].imageUrl
                                    PrefrenceUtils.insertData(
                                        requireActivity(),
                                        Constants.AADHARCARDFRONT,
                                        imageUrlFrontAadhaar
                                    )
                                    isAadhaarDocumentUpload = true
                                }
                                if (earnDataModel.documents[i].type.equals(
                                        "PROFILE PIC",
                                        ignoreCase = true
                                    )
                                ) {
                                    imageUrlProfilePic = earnDataModel.documents[i].imageUrl
                                    PrefrenceUtils.insertData(
                                        requireActivity(),
                                        Constants.PROFILEPICIMAGE,
                                        imageUrlProfilePic
                                    )
                                    isProfilePicDocumentUpload = true
                                }
                                if (earnDataModel.documents[i].type.equals(
                                        "PAN Image",
                                        ignoreCase = true
                                    )
                                ) {
                                    imageUrlPan = earnDataModel.documents[i].imageUrl
                                    PrefrenceUtils.insertData(
                                        requireActivity(),
                                        Constants.PANCARDIMAGE,
                                        imageUrlPan
                                    )
                                    isPanCardDocumentUpload = true
                                }
                            }
                        } else {
                            PrefrenceUtils.insertData(
                                requireActivity(),
                                Constants.AADHARCARDBACK,
                                ""
                            )
                            PrefrenceUtils.insertData(
                                requireActivity(),
                                Constants.AADHARCARDFRONT,
                                ""
                            )
                            PrefrenceUtils.insertData(requireActivity(), Constants.PANCARDIMAGE, "")
                            PrefrenceUtils.insertData(
                                requireActivity(),
                                Constants.PROFILEPICIMAGE,
                                ""
                            )
                        }
                    } else {
                        PrefrenceUtils.insertData(requireActivity(), Constants.AADHARCARDBACK, "")
                        PrefrenceUtils.insertData(requireActivity(), Constants.AADHARCARDFRONT, "")
                        PrefrenceUtils.insertData(requireActivity(), Constants.PANCARDIMAGE, "")
                        PrefrenceUtils.insertData(requireActivity(), Constants.PROFILEPICIMAGE, "")
                    }
                    updateUi()
                }
            }
        }
    }

    private fun clickListener() {
        binding?.ivBackButton?.setOnClickListener {
            if (global_check == "Navigation" || global_check == "is_job_seeker") {
                requireContext().startActivity(
                    Intent(
                        requireContext(),
                        HomeActivity::class.java
                    ).putExtra("link", Constants.REDIRECTION_URL)
                )
            } else {
                checkFinalStatus()
            }
            requireActivity().onBackPressed()
        }
        binding?.uploadProfilePic?.setOnClickListener {
            clickActionForUploadProfilePic()
        }
        binding?.uploadPanCard?.setOnClickListener {
            clickActionForUploadPan()
        }
        binding?.uploadAdhaar?.setOnClickListener {
            clickActionForUploadAadhaar()
        }
        /* binding?.adharImg?.setOnClickListener {
             if (!PrefrenceUtils.retriveDataInBoolean(
                     activity,
                     Constants.IS_VERIFICATION_STATUS_AADHAR
                 )
             ) {
                 findNavController(this@UploadDocumentFragment).navigate(R.id.action_nav_upload_fragment_to_nav_view_fragment)
             } else {
                 fullScreenView()
             }
         }*/
        /*  binding?.adharImgB?.setOnClickListener {
              if (!PrefrenceUtils.retriveDataInBoolean(
                      activity,
                      Constants.IS_VERIFICATION_STATUS_AADHAR
                  )
              ) {
                  findNavController(
                      this@UploadDocumentFragment
                  ).navigate(R.id.action_nav_upload_fragment_to_nav_view_fragment)
              } else {
                  fullScreenView()
              }
          }*/
        /*   binding?.panImg?.setOnClickListener {
               if (!PrefrenceUtils.retriveDataInBoolean(
                       activity,
                       Constants.IS_VERIFICATION_STATUS_PAN
                   )
               ) {
                   findNavController(
                       this@UploadDocumentFragment
                   ).navigate(R.id.action_nav_upload_fragment_to_nav_view_fragment)
               } else {
                   fullScreenViewPan()
               }
           }*/
        binding?.llViewContainer?.setOnClickListener {
            fullScreenView()
        }
        binding?.llcontainerViewOne?.setOnClickListener {
            fullScreenViewPan()
        }
        binding?.uploadDlCard?.setOnClickListener {
            clickActionForUploadDL()
        }
        binding?.llcontainerViewOneDl?.setOnClickListener {
            fullScreenViewDL()
        }
    }

    private fun fullScreenViewDL() {
        val cacheDLFront =
            PrefrenceUtils.retriveData(requireContext(), Constants.DL_FRONT_IMG)
        val cacheDLBack =
            PrefrenceUtils.retriveData(requireContext(), Constants.DL_BACK_IMG)
        if (cacheDLFront.isNotEmpty() && cacheDLBack.isNotEmpty()) {
            ShowViewNowDialog(
                requireContext(),
                arrayOf(cacheDLFront, cacheDLBack)
            ).show()
        } else if (viewModel.frontDLCard.isNotEmpty() && viewModel.backDLCard.isNotEmpty()) {
            // Dat Fetch
            ShowViewNowDialog(
                requireContext(),
                arrayOf(viewModel.frontDLCard, viewModel.backDLCard)
            ).show()
        }
    }

    private fun clickActionForUploadDL() {
        if (viewModel.frontDLCard.isNotEmpty() && viewModel.backDLCard.isNotEmpty()) {
            // Dat Fetch
            binding?.uploadDlCard?.isEnabled = false
            binding?.uploadDlCard?.isFocusable = false
            binding?.uploadDlCard?.isClickable = false
            binding?.uploadDlCard?.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.light_color_heading
                )
            )
            CoroutineScope(Dispatchers.IO).launch {
                callApi("DRIVING_LICENSE")
            }

        } else {
            val bundle = Bundle()
            bundle.putString(Constants.ID_TYPE, "DRIVING_LICENSE")
            findNavController(
                this@UploadDocumentFragment
            ).navigate(R.id.action_nav_upload_fragment_to_nav_view_fragment, bundle)
        }
    }

    private fun fullScreenViewPan() {
        val cachePan =
            PrefrenceUtils.retriveData(requireContext(), Constants.PANCARDIMAGE)
        if (cachePan.isNotEmpty()) {
            ShowViewNowDialog(
                requireContext(),
                arrayOf(cachePan, "")
            ).show()
        } else if (viewModel.imagePath.isNotEmpty()) {
            // Dat Fetch
            ShowViewNowDialog(
                requireContext(),
                arrayOf(viewModel.imagePath, "")
            ).show()
        }
    }

    private fun fullScreenView() {
        val cacheAadharCardFront =
            PrefrenceUtils.retriveData(requireContext(), Constants.AADHARCARDFRONT)
        val cacheAadharCardBack =
            PrefrenceUtils.retriveData(requireContext(), Constants.AADHARCARDBACK)
        if (cacheAadharCardFront.isNotEmpty() && cacheAadharCardBack.isNotEmpty()) {
            ShowViewNowDialog(
                requireContext(),
                arrayOf(cacheAadharCardFront, cacheAadharCardBack)
            ).show()
        } else if (viewModel.frontAdhaarCard.isNotEmpty() && viewModel.backAdhaarCard.isNotEmpty()) {
            // Dat Fetch
            ShowViewNowDialog(
                requireContext(),
                arrayOf(viewModel.backAdhaarCard, viewModel.frontAdhaarCard)
            ).show()
        }
    }

    private fun clickActionForUploadAadhaar() {
        if (viewModel.frontAdhaarCard.isNotEmpty() && viewModel.backAdhaarCard.isNotEmpty()) {
            // Dat Fetch
            binding?.uploadAdhaar?.isEnabled = false
            binding?.uploadAdhaar?.isFocusable = false
            binding?.uploadAdhaar?.isClickable = false
            binding?.uploadAdhaar?.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.light_color_heading
                )
            )
            CoroutineScope(Dispatchers.IO).launch {
                callApi("AADHAAR_CARD")
            }

        } else {
            val bundle = Bundle()
            bundle.putString(Constants.ID_TYPE, "AADHAAR_CARD")
            findNavController(
                this@UploadDocumentFragment
            ).navigate(R.id.action_nav_upload_fragment_to_nav_view_fragment, bundle)
        }


    }

    private fun clickActionForUploadProfilePic() {
        if (viewModel.profilePicImagePath.isNotEmpty()) {
            // Dat Fetch
            binding?.uploadProfilePic?.isEnabled = false
            binding?.uploadProfilePic?.isFocusable = false
            binding?.uploadProfilePic?.isClickable = false
            binding?.uploadProfilePic?.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.light_color_heading
                )
            )
            CoroutineScope(Dispatchers.IO).launch {
                callApi("PROFILE_PIC")
            }

        } else {
            val bundle = Bundle()
            bundle.putString(Constants.ID_TYPE, "PROFILE_PIC")
            findNavController(
                this@UploadDocumentFragment
            ).navigate(R.id.action_nav_upload_fragment_to_nav_view_fragment, bundle)
        }
    }

    private fun clickActionForUploadPan() {
        if (viewModel.imagePath.isNotEmpty()) {
            // Dat Fetch
            binding?.uploadPanCard?.isEnabled = false
            binding?.uploadPanCard?.isFocusable = false
            binding?.uploadPanCard?.isClickable = false
            binding?.uploadPanCard?.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.light_color_heading
                )
            )
            CoroutineScope(Dispatchers.IO).launch {
                callApi("PAN_CARD")
            }

        } else {
            val bundle = Bundle()
            bundle.putString(Constants.ID_TYPE, "PAN_CARD")
            findNavController(
                this@UploadDocumentFragment
            ).navigate(R.id.action_nav_upload_fragment_to_nav_view_fragment, bundle)
        }
    }


    private fun checkFinalStatus() {
        (requireActivity() as MainActivity).isBackFromUpload =
            !((PrefrenceUtils.retriveData(requireContext(), Constants.AADHARCARDFRONT)
                .isNotEmpty() &&
                    PrefrenceUtils.retriveData(requireContext(), Constants.AADHARCARDBACK)
                        .isNotEmpty()) &&
                    PrefrenceUtils.retriveData(requireContext(), Constants.PANCARDIMAGE)
                        .isNotEmpty())

    }


    private fun callApi(type: String) {
        var frontUrl: String
        var backUrl: String
        val viewSharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        if (type == "AADHAAR_CARD") {
            lifecycleScope.launchWhenStarted {
                viewSharedViewModel.newUploadImageFile(
                    requireContext(), Constants.TOKENCONSTANT
                            + PrefrenceUtils.retriveData(requireContext(), Constants.API_TOKEN),
                    viewModel.bitmapDataAADHARFRONT!!, "FrontAadhar"
                )
                    ?.observe(requireActivity()) { data ->
                        if (data.fileURL?.isNotEmpty() == true) {
                            frontUrl = data.fileURL!!
                            if (viewModel.backAdhaarCard.isNotEmpty())
                                viewSharedViewModel.newUploadImageFile(
                                    requireContext(),
                                    Constants.TOKENCONSTANT + PrefrenceUtils.retriveData(
                                        requireContext(),
                                        Constants.API_TOKEN
                                    ),
                                    viewModel.bitmapDataAADHARBACK, "backAadhaarCard"
                                )
                                    .observe(requireActivity()) { data ->
                                        if (data.fileURL?.isNotEmpty() == true) {
                                            backUrl = data.fileURL!!
                                            if (frontUrl.isNotEmpty() && backUrl.isNotEmpty()) {
                                                uploadFile(
                                                    "AADHAAR_CARD",
                                                    viewSharedViewModel,
                                                    frontUrl,
                                                    backUrl
                                                )
                                            }
                                            binding?.icUploadCaseFront?.let { it1 ->
                                                Glide.with(requireContext())
                                                    .load(R.drawable.for_verify)
                                                    .into(
                                                        it1
                                                    )
                                            }
                                            binding?.uploadAdhaar?.isEnabled = false
                                            binding?.uploadAdhaar?.setBackgroundColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.light_color_heading
                                                )
                                            )

                                        }

                                    }
                        }
                    }
            }
        } else if (type == "PROFILE_PIC") {
            lifecycleScope.launchWhenStarted {
                val imageFile = viewModel.profilePicImagePath
                if (imageFile.isNotEmpty()) {
                    viewSharedViewModel.newUploadImageFile(
                        requireContext(),
                        Constants.TOKENCONSTANT + PrefrenceUtils.retriveData(
                            requireContext(),
                            Constants.API_TOKEN
                        ),
                        viewModel.bitmapDataProfilePic!!,
                        "ProfilePic"
                    )?.observe(requireActivity()) { data ->
                        if(data.fileURL?.isNotEmpty() == true){
                            PrefrenceUtils.insertData(requireContext(), Constants.PROFILEPICIMAGE, data.fileURL)
                            if (binding?.root != null)
                                binding?.profileUploadImg?.let { it1 ->
                                    Glide.with(requireContext()).load(R.drawable.for_verify).into(
                                        it1
                                    )
                                }
                            binding?.uploadProfilePic?.isEnabled = false
                            binding?.uploadProfilePic?.setBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.light_color_heading
                                )
                            )
                            uploadFile("PROFILE_PIC", viewSharedViewModel, data.fileURL!!, "")
                        }
                    }
                }
            }
        } else if (type == "PAN_CARD") {
            lifecycleScope.launchWhenStarted {
                val imageFile = viewModel.imagePath
                if (imageFile.isNotEmpty()) {
                    viewSharedViewModel.newUploadImageFile(
                        requireContext(),
                        Constants.TOKENCONSTANT + PrefrenceUtils.retriveData(
                            requireContext(),
                            Constants.API_TOKEN
                        ),
                        viewModel.bitmapDataPAN!!,
                        "PanCard"
                    )?.observe(requireActivity()) { data ->
                        PrefrenceUtils.insertData(requireContext(), Constants.PANCARDIMAGE, data.fileURL)
                        if (binding?.root != null)
                            binding?.panUploadImg?.let { it1 ->
                                Glide.with(requireContext()).load(R.drawable.for_verify).into(
                                    it1
                                )
                            }
                        binding?.uploadPanCard?.isEnabled = false
                        binding?.uploadPanCard?.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.light_color_heading
                            )
                        )
                        uploadFile("PAN_CARD", viewSharedViewModel, data.fileURL.toString(), "")
                    }
                }
            }
        } else {
            lifecycleScope.launchWhenStarted {
                if (viewModel.frontDLCard.isNotEmpty() && viewModel.frontDLCard.isNotEmpty()) {
                    viewSharedViewModel.newUploadImageFile(
                        requireContext(), Constants.TOKENCONSTANT
                                + PrefrenceUtils.retriveData(requireContext(), Constants.API_TOKEN),
                        viewModel.bitmapDataDLFront!!,
                        "DLFront"
                    )?.observe(requireActivity()) { data ->
                        if (data.fileURL?.isNotEmpty() == true) {
                            frontUrl = data.fileURL.toString()
                            if (viewModel.backDLCard.isNotEmpty())
                                viewSharedViewModel.newUploadImageFile(
                                    requireContext(),
                                    Constants.TOKENCONSTANT + PrefrenceUtils.retriveData(
                                        requireContext(),
                                        Constants.API_TOKEN
                                    ),
                                    viewModel.bitmapDataDLBack, "DLBack"
                                )
                                    .observe(requireActivity()) { data ->
                                        if (data.fileURL?.isNotEmpty() == true) {
                                            backUrl = data.fileURL.toString()
                                            if (frontUrl.isNotEmpty() && backUrl.isNotEmpty()) {
                                                uploadFile(
                                                    "DRIVING_LICENSE",
                                                    viewSharedViewModel,
                                                    frontUrl,
                                                    backUrl
                                                )
                                            }
                                            binding?.dlUploadImg?.let { it1 ->
                                                Glide.with(requireContext())
                                                    .load(R.drawable.for_verify)
                                                    .into(
                                                        it1
                                                    )
                                            }
                                            binding?.uploadDlCard?.isEnabled = false
                                            binding?.uploadDlCard?.setBackgroundColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.light_color_heading
                                                )
                                            )
                                        }

                                    }
                        }

                    }

                }
            }
        }
    }

    private fun uploadFile(
        idType: String,
        viewSharedViewModel: SharedViewModel,
        data: String,
        backUrl: String,
    ) {
        val verifyModel = JsonObject()
        when (idType) {
            "PROFILE_PIC" -> {
                verifyModel.addProperty(Constants.IMAGE_URL, data)
                verifyModel.addProperty(Constants.TYPE, "passportPhoto")
            }
            "AADHAAR_CARD" -> {
                verifyModel.addProperty(Constants.IMAGE_URL, data)
                verifyModel.addProperty(Constants.OTHER_SIDE_IMAGE_URL, backUrl)
                verifyModel.addProperty(Constants.EXPECTED_TYPE, "Aadhaar Image")
            }
            "PAN_CARD" -> {
                verifyModel.addProperty(Constants.IMAGE_URL, data)
                verifyModel.addProperty(Constants.EXPECTED_TYPE, "PAN Image")
            }
            else -> {
                verifyModel.addProperty(Constants.IMAGE_URL, data)
                verifyModel.addProperty(Constants.OTHER_SIDE_IMAGE_URL, backUrl)
                verifyModel.addProperty(Constants.EXPECTED_TYPE, "DL Image")
            }
        }
        verifyModel.addProperty(Constants.SOURCE, "mitra")
        viewSharedViewModel.verifyAadharCard(requireContext(), verifyModel)
            .observe(requireActivity()) {
                if (idType == "AADHAAR_CARD") {
                    binding?.uploadAdhaar?.isEnabled = true
                    binding?.uploadAdhaar?.isFocusable = true
                    binding?.uploadAdhaar?.isClickable = true
                    binding?.uploadAdhaar?.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.default_200
                        )
                    )
                    if (it.isValid != null && it.isValid) {
                        PrefrenceUtils.insertDataInBoolean(
                            activity,
                            Constants.IS_VERIFICATION_STATUS_AADHAR,
                            true
                        )
                        binding?.icUploadCaseFront?.let { it1 ->
                            Glide.with(requireContext()).load(R.drawable.upload_doc).into(it1)
                        }
//                        binding?.uploadAdhaar?.isEnabled = true
//                        binding?.uploadAdhaar?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.default_200))
                        binding?.uploadAdhaar?.visibility = View.GONE
                        binding?.llViewContainer?.visibility = View.VISIBLE
                        binding?.tvMissingAadharFront?.text = "Success"
                        binding?.tvMissingAadharFront?.setBackgroundResource(R.drawable.green_rect)
                        binding?.tvMissingAadharFront?.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            0,
                            0
                        )
                        binding?.tvMissingAadharFront?.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green
                            )
                        )
                        PrefrenceUtils.insertData(requireContext(), Constants.AADHARCARDFRONT, data)
                        PrefrenceUtils.insertData(
                            requireContext(),
                            Constants.AADHARCARDBACK,
                            backUrl
                        )
                    } else {
                        viewModel.frontAdhaarCard = "";
                        viewModel.backAdhaarCard = "";
                        PrefrenceUtils.insertData(requireContext(), Constants.AADHARCARDFRONT, "")
                        PrefrenceUtils.insertData(requireContext(), Constants.AADHARCARDBACK, "")
                        Toast.makeText(
                            requireContext(),
                            context?.getString(R.string.validation_failed_please_try_again_later),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        binding?.icUploadCaseFront?.let { it1 ->
                            Glide.with(requireContext()).load(R.drawable.ic_upload).into(
                                it1
                            )
                        }

                        PrefrenceUtils.insertDataInBoolean(
                            activity,
                            Constants.IS_VERIFICATION_STATUS_AADHAR,
                            false
                        )
                        binding?.uploadAdhaar?.visibility = View.VISIBLE
//                        binding?.uploadAdhaar?.isEnabled = false
                        binding?.uploadAdhaar?.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.default_200
                            )
                        )

                        binding?.llViewContainer?.visibility = View.GONE
//                        binding?.uploadAdhaar?.setBackgroundColor(
//                            ContextCompat.getColor(
//                                requireContext(),
//                                R.color.tv_missing_bg
//                            )
//                        )
                        binding?.tvMissingAadharFront?.text = "Failed"
                        binding?.tvMissingAadharFront?.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_alert_icon,
                            0,
                            0,
                            0
                        )
                        binding?.tvMissingAadharFront?.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.tv_missing_bg
                            )
                        )
                        binding?.tvMissingAadharFront?.setBackgroundResource(R.drawable.ic_missing)
                    }
                } else if (idType == "PROFILE_PIC") {
                    binding?.uploadProfilePic?.isEnabled = true
                    binding?.uploadProfilePic?.isFocusable = true
                    binding?.uploadProfilePic?.isClickable = true
                    binding?.uploadProfilePic?.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.default_200
                        )
                    )
                    if (it.isValid !== null && it.isValid) {
                        binding?.profileUploadImg?.let { it1 ->
                            Glide.with(requireContext()).load(R.drawable.upload_doc).into(
                                it1
                            )
                        }
//                        binding?.uploadPanCard?.isEnabled = true
//                        binding?.uploadPanCard?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.default_200))
                        PrefrenceUtils.insertDataInBoolean(
                            activity,
                            Constants.IS_VERIFICATION_STATUS_PROFILE_PIC,
                            true
                        )
                        binding?.uploadProfilePic?.visibility = View.GONE
                        binding?.profileBtnView?.visibility = View.VISIBLE
                        binding?.missingProfilePic?.text = "Success"
                        binding?.missingProfilePic?.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            0,
                            0
                        )

                        binding?.missingProfilePic?.setBackgroundResource(R.drawable.green_rect)
                        binding?.missingProfilePic?.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green
                            )
                        )
                    } else {
                        viewModel.profilePicImagePath = ""
                        Toast.makeText(
                            requireContext(),
                            context?.getString(R.string.validation_failed_please_try_again_later),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        binding?.profileUploadImg?.let { it1 ->
                            Glide.with(requireContext()).load(R.drawable.ic_upload).into(
                                it1
                            )
                        }
//                        binding?.uploadPanCard?.isEnabled = false
                        binding?.uploadProfilePic?.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.light_color_heading
                            )
                        )
                        PrefrenceUtils.insertDataInBoolean(
                            activity,
                            Constants.IS_VERIFICATION_STATUS_PROFILE_PIC,
                            false
                        )

                        PrefrenceUtils.insertData(
                            activity,
                            Constants.PROFILEPICIMAGE,
                            ""
                        )

                        binding?.uploadProfilePic?.visibility = View.VISIBLE
                        binding?.profileBtnView?.visibility = View.GONE
                        binding?.uploadProfilePic?.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.tv_missing_bg
                            )
                        )
//                        binding?.tvMissingAadharFront?.setCompoundDrawablesWithIntrinsicBounds(
//                            R.drawable.ic_alert_icon,
//                            0,
//                            0,
//                            0
//                        )
                        binding?.missingProfilePic?.text = "Failed"
                        binding?.missingProfilePic?.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.default_200
                            )
                        )
                        binding?.missingProfilePic?.setBackgroundResource(R.drawable.ic_missing)
                    }

                } else if (idType == "PAN_CARD") {
                    binding?.uploadPanCard?.isEnabled = true
                    binding?.uploadPanCard?.isFocusable = true
                    binding?.uploadPanCard?.isClickable = true
                    binding?.uploadPanCard?.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.default_200
                        )
                    )
                    if (it.isValid !== null && it.isValid) {
                        binding?.panUploadImg?.let { it1 ->
                            Glide.with(requireContext()).load(R.drawable.upload_doc).into(
                                it1
                            )
                        }
//                        binding?.uploadPanCard?.isEnabled = true
//                        binding?.uploadPanCard?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.default_200))
                        PrefrenceUtils.insertDataInBoolean(
                            activity,
                            Constants.IS_VERIFICATION_STATUS_PAN,
                            true
                        )
                        binding?.uploadPanCard?.visibility = View.GONE
                        binding?.llcontainerViewOne?.visibility = View.VISIBLE
                        binding?.tvMissingPancard?.text = "Success"
                        binding?.tvMissingPancard?.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            0,
                            0
                        )

                        binding?.tvMissingPancard?.setBackgroundResource(R.drawable.green_rect)
                        binding?.tvMissingPancard?.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green
                            )
                        )
                    } else {
                        viewModel.imagePath = ""
                        Toast.makeText(
                            requireContext(),
                            context?.getString(R.string.validation_failed_please_try_again_later),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        binding?.panUploadImg?.let { it1 ->
                            Glide.with(requireContext()).load(R.drawable.ic_upload).into(
                                it1
                            )
                        }
//                        binding?.uploadPanCard?.isEnabled = false
                        binding?.uploadPanCard?.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.light_color_heading
                            )
                        )
                        PrefrenceUtils.insertDataInBoolean(
                            activity,
                            Constants.IS_VERIFICATION_STATUS_PAN,
                            false
                        )

                        PrefrenceUtils.insertData(
                            activity,
                            Constants.PANCARDIMAGE,
                            ""
                        )

                        binding?.uploadPanCard?.visibility = View.VISIBLE
                        binding?.llcontainerViewOne?.visibility = View.GONE
                        binding?.uploadPanCard?.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.tv_missing_bg
                            )
                        )
                        binding?.tvMissingAadharFront?.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_alert_icon,
                            0,
                            0,
                            0
                        )
                        binding?.tvMissingPancard?.text = "Failed"
                        binding?.tvMissingPancard?.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.default_200
                            )
                        )
                        binding?.tvMissingPancard?.setBackgroundResource(R.drawable.ic_missing)
                    }

                } else {
                    // write logic
                    binding?.uploadDlCard?.isEnabled = true
                    binding?.uploadDlCard?.isFocusable = true
                    binding?.uploadDlCard?.isClickable = true
                    binding?.uploadDlCard?.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.default_200
                        )
                    )
                    if (it.isValid !== null && it.isValid) {
                        PrefrenceUtils.insertDataInBoolean(
                            activity,
                            Constants.ISPANCARD,
                            true
                        )
                        binding?.dlUploadImg?.let { it1 ->
                            Glide.with(requireContext()).load(R.drawable.upload_doc).into(it1)
                        }
                        binding?.uploadDlCard?.visibility = View.GONE
                        binding?.llcontainerViewOneDl?.visibility = View.VISIBLE
                        binding?.dlMissingTv?.text = "Success"
                        binding?.dlMissingTv?.setBackgroundResource(R.drawable.green_rect)
                        binding?.dlMissingTv?.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            0,
                            0
                        )
                        binding?.dlMissingTv?.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green
                            )
                        )
                        PrefrenceUtils.insertData(requireContext(), Constants.DL_FRONT_IMG, data)
                        PrefrenceUtils.insertData(
                            requireContext(),
                            Constants.DL_BACK_IMG,
                            backUrl
                        )
                    } else {
                        viewModel.frontDLCard = ""
                        viewModel.backDLCard = ""
                        PrefrenceUtils.insertData(requireContext(), Constants.DL_FRONT_IMG, "")
                        PrefrenceUtils.insertData(requireContext(), Constants.DL_BACK_IMG, "")
                        Toast.makeText(
                            requireContext(),
                            context?.getString(R.string.validation_failed_please_try_again_later),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        binding?.dlUploadImg?.let { it1 ->
                            Glide.with(requireContext()).load(R.drawable.ic_upload).into(
                                it1
                            )
                        }
//                        binding?.uploadDlCard?.isEnabled = false
                        binding?.uploadDlCard?.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.default_200
                            )
                        )
                        PrefrenceUtils.insertDataInBoolean(
                            activity,
                            Constants.ISPANCARD,
                            false
                        )
                        binding?.uploadDlCard?.visibility = View.VISIBLE
                        binding?.llcontainerViewOneDl?.visibility = View.GONE
                        binding?.uploadDlCard?.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.tv_missing_bg
                            )
                        )
                        binding?.dlMissingTv?.text = "Failed"
                        binding?.dlMissingTv?.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_alert_icon,
                            0,
                            0,
                            0
                        )
                        binding?.dlMissingTv?.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.tv_missing_bg
                            )
                        )
                        binding?.dlMissingTv?.setBackgroundResource(R.drawable.ic_missing)
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.frontAdhaarCard = ""
        viewModel.imagePath = ""
        viewModel.backAdhaarCard = ""
        viewModel.frontDLCard = ""
        viewModel.backDLCard = ""
    }

    override fun onResume() {
        super.onResume()
        updateUi()
    }

    private fun updateUi() {
        val cacheAadharCardFront =
            PrefrenceUtils.retriveData(requireContext(), Constants.AADHARCARDFRONT)
        val cacheAadharCardBack =
            PrefrenceUtils.retriveData(requireContext(), Constants.AADHARCARDBACK)
        val cacheProfilePic =
            PrefrenceUtils.retriveData(requireContext(), Constants.PROFILEPICIMAGE)
        val cachePanCard = PrefrenceUtils.retriveData(requireContext(), Constants.PANCARDIMAGE)
        val cacheDLFront = PrefrenceUtils.retriveData(requireContext(), Constants.DL_FRONT_IMG)
        val cacheDLBack = PrefrenceUtils.retriveData(requireContext(), Constants.DL_BACK_IMG)
        if ((cacheAadharCardBack.isNotEmpty() && cacheAadharCardFront.isNotEmpty()) || viewModel.frontAdhaarCard.isNotEmpty() && viewModel.backAdhaarCard.isNotEmpty()) {
            // Positive Flow...
            if (viewModel.frontAdhaarCard.isNotEmpty() && viewModel.backAdhaarCard.isNotEmpty()) {
                // setUI..
                binding?.adharImgB?.let {
                    Glide.with(requireContext()).load(viewModel.backAdhaarCard).into(it)
                }
                binding?.adharImg?.let {
                    Glide.with(requireContext()).load(viewModel.frontAdhaarCard).into(it)
                }
            } else if (cacheAadharCardBack.isNotEmpty() && cacheAadharCardFront.isNotEmpty()) {
                binding?.adharImgB?.let {
                    Glide.with(requireContext()).load(cacheAadharCardBack).into(it)
                }
                binding?.adharImg?.let {
                    Glide.with(requireContext()).load(cacheAadharCardFront).into(it)
                }
                binding?.uploadAdhaar?.visibility = View.GONE
                binding?.llViewContainer?.visibility = View.VISIBLE
                binding?.tvMissingAadharFront?.text = context?.getString(R.string._success)
                binding?.tvMissingAadharFront?.setBackgroundResource(R.drawable.green_rect)
                binding?.tvMissingAadharFront?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                binding?.tvMissingAadharFront?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green
                    )
                )
                binding?.icUploadCaseFront?.let { it1 ->
                    Glide.with(requireContext()).load(R.drawable.upload_doc).into(it1)
                }
//                binding?.uploadAdhaar?.isEnabled = true
//                binding?.uploadAdhaar?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.default_200))

            }
        }
        if (cacheProfilePic.isNotEmpty() || viewModel.profilePicImagePath.isNotEmpty()) {
            if (viewModel.profilePicImagePath.isNotEmpty()) {
                // setUI..
                binding?.profilePic?.let {
                    Glide.with(requireContext()).load(viewModel.profilePicImagePath).into(it)
                }
            } else if (cacheProfilePic.isNotEmpty()) {
                binding?.profilePic?.let {
                    Glide.with(requireContext()).load(cacheProfilePic).into(it)
                }
                binding?.profileUploadImg?.let { it1 ->
                    Glide.with(requireContext()).load(R.drawable.upload_doc).into(it1)
                }
//                binding?.uploadPanCard?.isEnabled = true
//                binding?.uploadPanCard?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.default_200))
                binding?.uploadProfilePic?.visibility = View.GONE
                binding?.profileBtnView?.visibility = View.VISIBLE
                binding?.uploadProfilePic?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_200_fade
                    )
                )
                binding?.missingProfilePic?.text = context?.getString(R.string._success)
                binding?.missingProfilePic?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                binding?.missingProfilePic?.setBackgroundResource(R.drawable.green_rect)
                binding?.missingProfilePic?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green
                    )
                )

            }
        }
        if (cachePanCard.isNotEmpty() || viewModel.imagePath.isNotEmpty()) {
            if (viewModel.imagePath.isNotEmpty()) {
                // setUI..
                binding?.panImg?.let {
                    Glide.with(requireContext()).load(viewModel.imagePath).into(it)
                }
            } else if (cachePanCard.isNotEmpty()) {
                binding?.panImg?.let { Glide.with(requireContext()).load(cachePanCard).into(it) }
                binding?.panUploadImg?.let { it1 ->
                    Glide.with(requireContext()).load(R.drawable.upload_doc).into(it1)
                }
//                binding?.uploadPanCard?.isEnabled = true
//                binding?.uploadPanCard?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.default_200))
                binding?.uploadPanCard?.visibility = View.GONE
                binding?.llcontainerViewOne?.visibility = View.VISIBLE
                binding?.uploadPanCard?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_200_fade
                    )
                )
                binding?.tvMissingPancard?.text = context?.getString(R.string._success)
                binding?.tvMissingPancard?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                binding?.tvMissingPancard?.setBackgroundResource(R.drawable.green_rect)
                binding?.tvMissingPancard?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green
                    )
                )

            }
        }
        if ((cacheDLFront.isNotEmpty() && cacheDLBack.isNotEmpty()) || viewModel.frontDLCard.isNotEmpty() && viewModel.backDLCard.isNotEmpty()) {
            if (viewModel.frontDLCard.isNotEmpty() && viewModel.backDLCard.isNotEmpty()) {
                // setUI..
                binding?.dlImgA?.let {
                    Glide.with(requireContext()).load(viewModel.frontDLCard).into(it)
                }
                binding?.dlImgB?.let {
                    Glide.with(requireContext()).load(viewModel.backDLCard).into(it)
                }
            } else if (cacheDLBack.isNotEmpty() && cacheDLFront.isNotEmpty()) {

                Glide.with(requireContext()).load(cacheDLFront)
                    .placeholder(R.drawable.ic_dl_4x)
                    .error(R.drawable.mitra_icon)
                    .into(binding!!.dlImgA)

                Glide.with(requireContext())
                    .load(cacheDLBack)
                    .placeholder(R.drawable.ic_dl_4x)
                    .error(R.drawable.mitra_icon)
                    .into(binding!!.dlImgB)
                binding?.uploadDlCard?.visibility = View.GONE
                binding?.llcontainerViewOneDl?.visibility = View.VISIBLE
                binding?.dlMissingTv?.text = context?.getString(R.string._success)
                binding?.dlMissingTv?.setBackgroundResource(R.drawable.green_rect)
                binding?.dlMissingTv?.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    0,
                    0
                )
                binding?.dlMissingTv?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green
                    )
                )
                binding?.dlUploadImg?.let { it1 ->
                    Glide.with(requireContext()).load(R.drawable.upload_doc).into(it1)
                }

            }
        }
    }


    fun initLoader(context: Context?) {
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


