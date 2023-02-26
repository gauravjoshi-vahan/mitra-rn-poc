package com.vahan.mitra_playstore.view.jobsmarketplace.docupload.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.JsonObject
import com.moengage.core.Properties
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentJmDocUploadBinding
import com.vahan.mitra_playstore.databinding.JmDocUploadListBinding
import com.vahan.mitra_playstore.network.SharedViewModel
import com.vahan.mitra_playstore.utils.*
import com.vahan.mitra_playstore.view.HomeActivity
import com.vahan.mitra_playstore.view.bottomsheet.UploadDocBottomSheet
import com.vahan.mitra_playstore.view.documentupload.viewmodel.ImageUploadModel
import com.vahan.mitra_playstore.view.documentupload.viewmodel.UploadViewModels
import com.vahan.mitra_playstore.view.jobsmarketplace.docupload.datamodels.JMDocInputFieldPostDTO
import com.vahan.mitra_playstore.view.jobsmarketplace.docupload.datamodels.JMDocUploadDTO
import com.vahan.mitra_playstore.view.jobsmarketplace.docupload.interfaces.JMDocUploadClickListener
import com.vahan.mitra_playstore.view.jobsmarketplace.docupload.ui.adapters.JMDocUploadAdapter
import com.vahan.mitra_playstore.view.jobsmarketplace.docupload.ui.adapters.JMDocUploadStatusAdapter
import com.vahan.mitra_playstore.view.jobsmarketplace.docupload.viewmodels.JMUploadViewModel
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.*
import kotlin.collections.set

class JMDocUploadFragment : Fragment(), JMDocUploadClickListener {
    lateinit var binding: FragmentJmDocUploadBinding
    private var fa: FirebaseAnalytics? = null
    lateinit var imgUploadModel: ImageUploadModel
    lateinit var viewSharedModel: SharedViewModel
    lateinit var viewModel: UploadViewModels
    lateinit var docUploadModel: JMUploadViewModel
    lateinit var dialogLoader: Dialog
    var docsMap = HashMap<String, String>()
    private lateinit var uploadListBinding: JmDocUploadListBinding
    val data = ArrayList<JMDocUploadDTO.Document>()
    private var allDocsList = ArrayList<JMDocUploadDTO.Document?>()
    private var additionalInfoList = ArrayList<JMDocUploadDTO.AdditionalInfo?>()

    private var receivedUriKey: Uri? = null
    private var tempImageUri: Uri? = null
    private var tempImagePath = ""
    private var currentAdapterItem = JMDocUploadDTO.Document(
        "card", "PAN Card", "@drawable/ic_pandcard_3x",
//            "https://uploads-a.vahan.co/mitra/1661841395484-Vd9bHx-pancard.jpg",
        false, "", "panCard", "pending", "", "card", ArrayList()
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        FragmentJmDocUploadBinding.inflate(inflater, container, false).run {
            fa = FirebaseAnalytics.getInstance(requireContext())
            binding = this
            return root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imgUploadModel = ViewModelProvider(requireActivity())[ImageUploadModel::class.java]
        viewSharedModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        docUploadModel = ViewModelProvider(requireActivity())[JMUploadViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity())[UploadViewModels::class.java]

        docsMap["drivingLicense"] = "DRIVING_LICENSE"
        docsMap["aadhaarCard"] = "AADHAAR_CARD"
        docsMap["panCard"] = "PAN_CARD"
        docsMap["userSelfie"] = "PROFILE_PIC"
        docsMap["bankPassbookOrCancelledCheque"] = "BANK_DOC"

        Log.d("HA PN", "initView JMDUF: ${PrefrenceUtils.retriveData(requireContext(), Constants.PHONENUMBER)}")
        initLoader(context)
        clickListeners()
        getDocuments("onViewCreated", "", null, null)
        checkForProfilePic()
    }

    private fun checkForProfilePic() {
        val cacheProfilePic = PrefrenceUtils.retriveData(context, Constants.PROFILEPICIMAGE)
        val viewModelProfilePic = viewModel.profilePicImagePath
        if (viewModelProfilePic.isNotEmpty() || cacheProfilePic.isNotEmpty()) {
            setStatusTextView(
                binding.uploadPPFrameLayout,
                binding.uploadPPInProgress,
                binding.missingProfilePic,
                "success"
            )
            binding.beforePPUpload.visibility = View.GONE
            binding.afterPPUpload.visibility = View.VISIBLE
            if (viewModelProfilePic.isNotEmpty()) {
                binding.profilePic.let {
                    Glide.with(requireContext()).load(viewModelProfilePic).into(it)
                }
            } else if (cacheProfilePic.isNotEmpty()) {
                binding.profilePic.let {
                    Glide.with(requireContext()).load(cacheProfilePic).into(it)
                }
            }
        } else {
            setStatusTextView(
                binding.uploadPPFrameLayout,
                binding.uploadPPInProgress,
                binding.missingProfilePic,
                "missing"
            )
            binding.beforePPUpload.visibility = View.VISIBLE
            binding.afterPPUpload.visibility = View.GONE
        }
    }

    private fun getProfilePicFromAPIResponse(profilePicDoc: JMDocUploadDTO.Document?) {
        var imageUrlProfilePic = ""
        if (profilePicDoc?.isCompleted == true) {
            if (profilePicDoc.imageUrl != null && profilePicDoc.imageUrl.toString()
                    .startsWith("https")
            ) {
                imageUrlProfilePic = profilePicDoc.imageUrl.toString()
                PrefrenceUtils.insertData(context, Constants.PROFILEPICIMAGE, imageUrlProfilePic)
            } else {
                PrefrenceUtils.insertData(context, Constants.PROFILEPICIMAGE, "")
            }
        } else {
            PrefrenceUtils.insertData(context, Constants.PROFILEPICIMAGE, "")
        }

        checkForProfilePic()
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                imgUploadModel.imagePath = tempImagePath
                imgUploadModel.createBitMap(tempImageUri!!)
                showCropper(tempImageUri!!)
            }
        }

    private var takePictureFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let {
                val path = UriUtils.getPath(requireContext(), it)
                imgUploadModel.imagePath = path
                imgUploadModel.createBitMap(it)
                Log.d("IMGURI", tempImageUri.toString())
                showCropper(it)
            }
        }

    private fun getDocuments(
        source: String, imageType: String, listBinding: JmDocUploadListBinding?, position: Int?
    ) {
        if (source == "onViewCreated") dialogLoader.show()
//        val dropdownDocsAL1 = ArrayList<String>()
//        val dropdownDocsAL2 = ArrayList<String>()
//        dropdownDocsAL1.add("drivingLicense")
//        dropdownDocsAL1.add("panCard")
//
//        dropdownDocsAL2.add("aadhaarCard")
//        dropdownDocsAL2.add("vehicleRc")
//        val doc1 = JMDocUploadDTO.Document(
//            "dropdown", "Aadhaar Card/Pan Card", "@drawable/ic_pandcard_3x",
////            "https://uploads-a.vahan.co/mitra/1661855241725-FJx4XM-backaadhaarcard.jpg",
//            false,
////            "https://uploads-a.vahan.co/mitra/1661855237829-FOsbOs-frontaadhar.jpg",
//            "", "", "", "", "", dropdownDocsAL1
//        )
////        val doc2 = JMDocUploadDTO.Document(
////            "dropdown", "Aadhaar Card/RC", "@drawable/ic_pandcard_3x",
//////            "https://uploads-a.vahan.co/mitra/1661841395484-Vd9bHx-pancard.jpg",
////            "", false, "", "", "", "", dropdownDocsAL2
////        )
//
//        val doc3 = JMDocUploadDTO.Document(
//            "card", "Bank Passbook/ Cancelled Cheque", "@drawable/ic_dl_4x",
////            "https://uploads-a.vahan.co/mitra/1661841395484-Vd9bHx-pancard.jpg",
//            false, "bankPassbookOrCancelledCheque", "", "pending", "", "card", ArrayList()
//        )
//
//        data.clear()
//        data.add(doc1)
////        data.add(doc2)
//        data.add(doc3)

        viewSharedModel.JMGetDocsAPI(
            PrefrenceUtils.retriveData(
                context, Constants.JOB_SEEKER_ID
            )
//            "f82911fd-0176-4546-8780-6ec9e4863f1a"
        ).observe(viewLifecycleOwner) {
            when {
                it?.statusCode in 400..499 -> {
                    dialogLoader.dismiss()
                    Toast.makeText(
                        requireActivity(),
                        R.string.something_went_wrong.toString() + ": 400",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                it.statusCode >= 500 -> {
                    dialogLoader.dismiss()
                    Navigation.findNavController(binding.root).navigate(R.id.nav_error_fragment)
                    //show toast here for non_payroll
                    Toast.makeText(
                        requireContext(),
                        R.string.something_went_wrong.toString() + ": 500",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    dialogLoader.dismiss()
                    if (it?.documents !== null) {

                        if (source == "onViewCreated") {
                            allDocsList = it.documents
//                        if (checkForProceedBtn()) {
//                            binding.proceedBtn.visibility = View.VISIBLE
//                            val params = RelativeLayout.LayoutParams(
//                                RelativeLayout.LayoutParams.MATCH_PARENT,
//                                RelativeLayout.LayoutParams.WRAP_CONTENT
//                            )
//                            params.setMargins(0, 0, 0, 150)
//                            binding.scrollable.layoutParams = params
//                        } else {
//                            binding.proceedBtn.visibility = View.GONE
//                        }
                            val index = it.documents.indexOfFirst { item ->
                                item?.key == "userSelfie"
                            }
                            val docsWithoutProfilePic = it.documents.filterIndexed { ind, _ ->
                                ind != index
                            }

                            if (it.additionalInfo !== null && it.additionalInfo.size > 0) {
                                additionalInfoList = it.additionalInfo
                            }

                            if (index >= 0) {
                                binding.uploadPPHeader.visibility = View.VISIBLE
                                binding.uploadPPCTAParent.visibility = View.VISIBLE
                                getProfilePicFromAPIResponse(it.documents[index])
                            } else {
                                binding.uploadPPHeader.visibility = View.GONE
                                binding.uploadPPCTAParent.visibility = View.GONE
                            }

                            if (docsWithoutProfilePic.isNotEmpty()) {
                                binding.docUploadPageHeader.text = resources.getString(R.string.upload_document)
                                binding.uploadDocsHeader.visibility = View.VISIBLE
                            } else {
                                binding.docUploadPageHeader.text =  resources.getString(R.string.profile_pic_page_header_txt)
                                binding.uploadDocsHeader.visibility = View.GONE
                            }

                            binding.rvDocItem.adapter = JMDocUploadAdapter(
                                this@JMDocUploadFragment,
                                requireContext(),
                                requireActivity(),
                                docsWithoutProfilePic,
                                additionalInfoList
//                            data
                            )

                            docUploadModel.statusArr.clear()
                            docUploadModel.setDefaultDocStatus()
//                        binding.rvDocItem.rvDocStatusItem?.adapter =
//                            JMDocUploadStatusAdapter(requireContext(), docUploadModel.statusArr)
                        } else {
                            if (source == "autoFillInpField") {
                                if ((it.additionalInfo !== null) && (it.additionalInfo.size > 0)) {
                                    additionalInfoList = it.additionalInfo
                                    if (imageType !== "" && listBinding !== null && position !== null) autoFillInpField(
                                        imageType,
                                        listBinding,
                                        position
                                    )
                                }
                            }
                        }
                    } else {
                        Navigation.findNavController(binding.root).navigate(R.id.nav_error_fragment)
                    }
                }
            }
        }

//        keep this for future code improvements
//        lifecycleScope.launchWhenStarted {
//            docUploadModel.getDocuments("cdc17a24-76ed-404e-a013-3e5d43bd74ce").collect {
//                when (it) {
//                    is ApiState.Success -> {
//                        Log.d("UPLOAD DOC API", it.data.toString())
//                        binding.rvDocItem.adapter = JMDocUploadAdapter(this@JMDocUploadFragment, requireContext(), requireActivity(), it.data.documents)
//                    }
//                    is ApiState.Failure -> {
//
//                    }
//                    ApiState.Loading -> {
//
//                    }
//                }
//            }
//        }

        return
    }

    private fun checkForProceedBtn(): Boolean {
        val cacheProfilePic = PrefrenceUtils.retriveData(context, Constants.PROFILEPICIMAGE)
        val cacheAadharCardFront = PrefrenceUtils.retriveData(context, Constants.AADHARCARDFRONT)
        val cacheAadharCardBack = PrefrenceUtils.retriveData(context, Constants.AADHARCARDBACK)
        val cachePanCard = PrefrenceUtils.retriveData(context, Constants.PANCARDIMAGE)
        val cacheDLFront = PrefrenceUtils.retriveData(context, Constants.DL_FRONT_IMG)
        val cacheDLBack = PrefrenceUtils.retriveData(context, Constants.DL_BACK_IMG)
        val cacheRCFront = PrefrenceUtils.retriveData(context, Constants.RCFRONT)
        val cacheRCBack = PrefrenceUtils.retriveData(context, Constants.RCBACK)
        val cacheBankDoc = PrefrenceUtils.retriveData(context, Constants.BANKDOCIMAGE)

        var isEnabled = true
        for (item in allDocsList) {

            when (item?.key) {
                "userSelfie" -> {
                    if (cacheProfilePic.isEmpty()) {
                        if (viewModel.profilePicImagePath.isEmpty()) {
                            isEnabled = false
                            break
                        }
                    }
                }
                "bankPassbookOrCancelledCheque" -> {
                    if (cacheBankDoc.isEmpty()) {
                        if (viewModel.bankDoc.isEmpty()) {
                            isEnabled = false
                            break
                        }
                    }
                }
                "aadhaarCard" -> {
                    if (cacheAadharCardBack.isEmpty() && cacheAadharCardFront.isEmpty()) {
                        if (viewModel.frontAdhaarCard.isEmpty() && viewModel.backAdhaarCard.isEmpty()) {
                            isEnabled = false
                            break
                        }
                    }
                }
                "panCard" -> {
                    if (cachePanCard.isEmpty()) {
                        if (viewModel.imagePath.isEmpty()) {
                            isEnabled = false
                            break
                        }
                    }
                }
                "drivingLicense" -> {
                    if (cacheDLFront.isEmpty() && cacheDLBack.isEmpty()) {
                        if (viewModel.frontDLCard.isEmpty() && viewModel.backDLCard.isEmpty()) {
                            isEnabled = false
                            break
                        }
                    }
                }
                "vehicleRc" -> {
                    if (cacheRCFront.isEmpty() && cacheRCBack.isEmpty()) {
                        if (viewModel.frontRC.isEmpty() && viewModel.backRC.isEmpty()) {
                            isEnabled = false
                            break
                        }
                    }
                }
            }
        }

        if (additionalInfoList.size > 0) {
            for (field in additionalInfoList) {
                when (field?.docKey) {
                    "panCard" -> {
                        if (viewModel.inputPanNumber == "") {
                            isEnabled = false
                            break
                        }
                    }
                    "drivingLicense" -> {
                        if (viewModel.inputDLNumber == "") {
                            isEnabled = false
                            break
                        }
                    }
                }
            }
        }


        return isEnabled
    }

    private fun clickListeners() {
        binding.beforePPUpload.setOnClickListener {
            showBottomDialog();
        }

        binding.editProfilePic.setOnClickListener {
            showBottomDialog();
        }

        binding.backBtn.setOnClickListener {
            requireContext().startActivity(
                Intent(
                    requireContext(), HomeActivity::class.java
                ).putExtra("link", Constants.REDIRECTION_URL)
//                ).putExtra("link", Constants.REDIRECTION_URL_STRING)
            )
        }

        binding.proceedBtn.setOnClickListener {
            requireContext().startActivity(
                Intent(
                    requireContext(), HomeActivity::class.java
                ).putExtra("link", Constants.REDIRECTION_URL)
//                ).putExtra("link", Constants.REDIRECTION_URL_STRING)
            )
        }
    }

    private fun showBottomDialog() {
        if (checkPermissions(
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
                )
            )
        ) {
            UploadDocBottomSheet(requireContext()) { isCamera ->

                if (isCamera) {
                    val file = imgUploadModel.createNewFile(
                        requireContext().getExternalFilesDir(
                            Environment.DIRECTORY_PICTURES
                        )
                    )
                    val uri = FileProvider.getUriForFile(
                        requireContext(), "${context?.packageName}.provider", file
                    )
                    receivedUriKey = null
                    tempImageUri = FileProvider.getUriForFile(requireContext(),
                        "${context?.packageName}.provider",
                        createImageFile().also {
                            tempImagePath = it.absolutePath
                        })
                    cameraLauncher.launch(tempImageUri)
                } else {
                    takePictureFromGallery.launch("image/*")
                }
            }.show()
        } else {
            givePermission()
        }
    }

    private fun showCropper(fileUri: Uri) {
        val destinationURI = StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString()
        val options = UCrop.Options()
        options.setCircleDimmedLayer(true)
        options.setMaxScaleMultiplier(5F);
        options.setShowCropFrame(false);
        options.setCropGridStrokeWidth(20);
        options.setCropGridColumnCount(2);
        options.setCropGridRowCount(1);

        UCrop.of(fileUri, Uri.fromFile(File(context?.cacheDir, destinationURI)))
            .withOptions(options).withAspectRatio(0F, 0F).useSourceImageAspectRatio()
            .withMaxResultSize(2000, 2000).start(requireContext(), this);

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val finalURI: Uri? = UCrop.getOutput(data!!)
            imgUploadModel.createBitMap(finalURI!!)
            binding.beforePPUpload.visibility = View.GONE
            binding.afterPPUpload.visibility = View.VISIBLE
            binding.profilePic.setImageURI(finalURI)
            imageUploadAPI("userSelfie")
//            Glide.with(requireContext())
//                .load(finalURI)
//                .circleCrop()
//                .into(binding.profilePic);
            binding.profilePic.maxWidth = resources.getDimensionPixelSize(R.dimen.dimen_150dp);
            binding.profilePic.maxHeight = resources.getDimensionPixelSize(R.dimen.dimen_150dp);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(activity, "Couldn't crop the image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun imageUploadAPI(type: String) {
        var imageUrl: String
        val imageType = type
        val viewSharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        val attribute = HashMap<String, Any>()
        val properties = Properties()
        properties.addAttribute("docType", imageType)
        attribute["docType"] = imageType
        captureAllJSEEvents(
            requireContext(), "jse_doc_upload_now_clicked", attribute, properties
        )
        setStatusTextView(
            binding.uploadPPFrameLayout,
            binding.uploadPPInProgress,
            binding.missingProfilePic,
            "uploading"
        )
        lifecycleScope.launchWhenStarted {
            viewSharedViewModel.newUploadImageFile(
                requireContext(), Constants.TOKENCONSTANT + PrefrenceUtils.retriveData(
                    requireContext(), Constants.API_TOKEN
                ), imgUploadModel.bitmapDataImage!!, imageType
            )?.observe(requireActivity()) { data ->
                if (data.fileURL?.isNotEmpty() == true) {
                    imageUrl = data.fileURL!!
                    if (imageUrl.isNotEmpty()) {
//                        verifyImageAPI(imageType, viewSharedViewModel, imageUrl)
                        callJSDocAPI(imageType, viewSharedViewModel, imageUrl, "", null, 0)
                    }
                } else {
                    Toast.makeText(
                        requireActivity(),
                        R.string.validation_failed_please_try_again_later,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }

    private fun callJSDocNumberAPI(
        input: JMDocUploadDTO.AdditionalInfo, inpBinding: JmDocUploadListBinding
    ) {
        var inputPanVal = ""
        var inputDLVal = ""

        inputPanVal = viewModel.inputPanNumber
        inputDLVal = viewModel.inputDLNumber
        val dataToUpdateModel = JMDocInputFieldPostDTO.DataToUpdate(inputDLVal, inputPanVal)
        val apiModel = JMDocInputFieldPostDTO(
            dataToUpdateModel, PrefrenceUtils.retriveData(requireContext(), Constants.USERID)
        )
//        dataToUpdateModel.addProperty(input.key, inputVal)
//        apiModel.add("dataToUpdate", dataToUpdateModel)
//        apiModel.addProperty(
//            "userId", PrefrenceUtils.retriveData(requireContext(), Constants.USERID)
//        )

        Log.d("DOC NUM VAL", apiModel.toString())
        lifecycleScope.launchWhenStarted {
            docUploadModel.postDocInputField(apiModel).collect {
                when (it) {
                    is ApiState.Success -> {
                        showInpFieldStatus("success", input, inpBinding, "callJSDocNumberAPI")
                    }
                    is ApiState.Failure -> {
                        showInpFieldStatus("failure", input, inpBinding, "callJSDocNumberAPI")
                    }
                    is ApiState.Loading -> {
                        showInpFieldStatus("loading", input, inpBinding, "callJSDocNumberAPI")
                    }
                }
            }
        }
    }

    private fun showInpFieldStatus(
        status: String,
        input: JMDocUploadDTO.AdditionalInfo?,
        inpBinding: JmDocUploadListBinding?,
        source: String?
    ) {
        if (status == "success") {
            inpBinding?.docNumberTxt?.isEnabled = false
            inpBinding?.docNumberTxt?.setTextIsSelectable(true);
            inpBinding?.docNumberTxt?.isCursorVisible = false;
            inpBinding?.docNumberTxt?.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.grey
                )
            )

            inpBinding?.inpSubmitInProgress?.clearAnimation()
            inpBinding?.inpSubmitInProgress?.visibility = View.GONE
            inpBinding?.submittedStatusImg?.visibility = View.VISIBLE
            inpBinding?.docNumberSubmit?.visibility = View.GONE
            Handler(Looper.getMainLooper()).postDelayed({
                inpBinding?.submittedStatusImg?.visibility = View.GONE
                inpBinding?.editInpFieldImg?.visibility = View.VISIBLE
                inpBinding?.docNumberSubmit?.visibility = View.GONE
            }, 2000)
            if (source == "callJSDocNumberAPI")
                Toast.makeText(
                    requireContext(),
                    "${docsMap[input?.docKey]} number uploaded",
                    Toast.LENGTH_SHORT
                ).show()
            if (checkForProceedBtn()) {
                binding.proceedBtn.visibility = View.VISIBLE
                val params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 150)
                binding.scrollable.layoutParams = params
            } else {
                binding.proceedBtn.visibility = View.GONE
            }
        } else if (status == "failure") {
            inpBinding?.inpSubmitInProgress?.clearAnimation()
            inpBinding?.inpSubmitInProgress?.visibility = View.GONE
            inpBinding?.editInpFieldImg?.visibility = View.VISIBLE
            inpBinding?.docNumberSubmit?.text = "Submit"
            if (source == "callJSDocNumberAPI")
                Toast.makeText(
                    requireContext(),
                    "${docsMap[input?.docKey]} number upload failed",
                    Toast.LENGTH_SHORT
                ).show()
            if (checkForProceedBtn()) {
                binding.proceedBtn.visibility = View.VISIBLE
                val params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 150)
                binding.scrollable.layoutParams = params
            } else {
                binding.proceedBtn.visibility = View.GONE
            }
        } else if (status == "loading") {
            inpBinding?.editInpFieldImg?.visibility = View.GONE
            inpBinding?.inpSubmitInProgress?.visibility = View.VISIBLE
            inpBinding?.docNumberSubmit?.visibility = View.GONE
            val rotate = RotateAnimation(
                0F, 180F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
            )
            rotate.duration = 4000
            rotate.interpolator = LinearInterpolator()
            inpBinding?.inpSubmitInProgress?.startAnimation(rotate)
        } else {
            inpBinding?.inpSubmitInProgress?.clearAnimation()
            inpBinding?.inpSubmitInProgress?.visibility = View.GONE
            inpBinding?.editInpFieldImg?.visibility = View.GONE
            inpBinding?.docNumberSubmit?.visibility = View.GONE
            inpBinding?.submittedStatusImg?.visibility = View.GONE
        }
    }

    private fun callJSDocAPI(
        imageType: String,
        viewSharedViewModel: SharedViewModel,
        imageUrl: String,
        backsideImgUrl: String,
        listBinding: JmDocUploadListBinding?,
        position: Int
    ) {
//        val verifyModel = JsonObject()
        val apiModel = JsonObject()
        val imgUrl = JsonObject()
        val docsModel = JsonObject()
        docUploadModel.currentDocKey = imageType
        imgUrl.addProperty("imageUrl", imageUrl)
        if (imageType == "aadhaarCard" || imageType == "drivingLicense" || imageType == "vehicleRc") {
            imgUrl.addProperty("otherSideImageUrl", backsideImgUrl)
        }
        //todo: uncomment next line and comment next to next line
        docsModel.add(imageType, imgUrl)
        apiModel.add("documents", docsModel)
        apiModel.addProperty(
            "siId", PrefrenceUtils.retriveData(
                context, Constants.JOB_SEEKER_ID
            )
        )
        lifecycleScope.launchWhenStarted {
            if (view !== null) {
                viewSharedModel.JMPostDocsAPI(context, apiModel)?.observe(viewLifecycleOwner) {
                    when {
                        it?.statusCode in 400..499 -> {
                            viewModel.disableAllUploadButtons = false
                            binding.rvDocItem.adapter?.notifyDataSetChanged()
                            docUploadModel.currentDocKey = ""
                            toggleButtonState(true, listBinding!!.uploadDoc, listBinding)
                            Toast.makeText(
                                requireActivity(),
                                R.string.something_went_wrong.toString() + ": 400",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        it !== null && it.statusCode >= 500 -> {
                            viewModel.disableAllUploadButtons = false
                            binding.rvDocItem.adapter?.notifyDataSetChanged()
                            docUploadModel.currentDocKey = ""
                            toggleButtonState(true, listBinding!!.uploadDoc, listBinding)
                            Navigation.findNavController(binding.root)
                                .navigate(R.id.nav_error_fragment)
                            //show toast here for non_payroll
                            Toast.makeText(
                                requireContext(),
                                R.string.something_went_wrong.toString() + ": 500",
                                Toast.LENGTH_SHORT
                            ).show()
//                        requireContext().startActivity(
//                            Intent(
//                                requireContext(),
//                                HomeActivity::class.java
//                            ).putExtra("link", Constants.REDIRECTION_URL)
//                        )
//                        findNavController().navigate(R.id.nav_error_fragment)

                        }
                        else -> {
                            if (it !== null) {
                                viewModel.disableAllUploadButtons = false
                                binding.rvDocItem.adapter?.notifyDataSetChanged()
                                if (it.documents?.isNotEmpty() == true) {
                                    listBinding?.uploadDoc?.isEnabled = true
                                    listBinding?.uploadDoc?.isFocusable = true
                                    listBinding?.uploadDoc?.isClickable = true
                                    listBinding?.uploadDoc?.setBackgroundColor(
                                        ContextCompat.getColor(
                                            requireContext(), R.color.default_200
                                        )
                                    )
                                    if (it.documents[0]?.status != null && it.documents[0]?.status == true) {
                                        //uncomment status docUploadImg
//                                        listBinding?.docUploadImg?.let { it1 ->
//                                            Glide.with(requireContext()).load(R.drawable.upload_doc)
//                                                .into(it1)
//                                        }
                                        val bundle = Bundle()
                                        bundle.putString(
                                            "docType", imageType
                                        )
//                                        fa?.logEvent("jse_doc_uploaded", bundle)
                                        captureAllFAEvents(
                                            requireContext(), Constants.DOC_UPLOADED_JSE_FA, bundle
                                        )
                                        listBinding?.rvDocStatusItem?.adapter =
                                            JMDocUploadStatusAdapter(
                                                requireContext(), docUploadModel.setStatusArr(
                                                    "verify_success", imageType
                                                ), imageType
                                            )
                                        listBinding?.uploadDoc?.visibility = View.GONE
                                        listBinding?.docViewBtn?.visibility = View.VISIBLE
                                        if (docsMap[imageType] == "PROFILE_PIC") {
                                            setStatusTextView(
                                                binding.uploadPPFrameLayout,
                                                binding.uploadPPInProgress,
                                                binding.missingProfilePic,
                                                "success"
                                            )
                                        } else {
                                            setStatusTextView(
                                                listBinding?.uploadStatusFrameLayout,
                                                listBinding?.uploadInProgress,
                                                listBinding?.missingDoc,
                                                "success"
                                            )
                                        }

                                        if (currentAdapterItem.componentType == "dropdown") {
                                            listBinding?.dropdownParent?.visibility = View.GONE
                                        }

                                        when (docsMap[imageType]) {
                                            "AADHAAR_CARD" -> {
                                                showToastMessage("success", "Aadhaar Card")
                                                PrefrenceUtils.insertDataInBoolean(
                                                    requireContext(),
                                                    Constants.IS_VERIFICATION_STATUS_AADHAR,
                                                    true
                                                )
                                                PrefrenceUtils.insertData(
                                                    requireContext(),
                                                    Constants.AADHARCARDFRONT,
                                                    imageUrl
                                                )
                                                PrefrenceUtils.insertData(
                                                    requireContext(),
                                                    Constants.AADHARCARDBACK,
                                                    backsideImgUrl
                                                )
                                            }
                                            "PAN_CARD" -> {
                                                showToastMessage("success", "PAN Card")
                                                //sending additionalInfoList[0] in showInpFieldStatus as the elemnt index doesn't matter while showing loading state
                                                if (additionalInfoList.size > 0) {
                                                    showInpFieldStatus(
                                                        "loading",
                                                        additionalInfoList[0],
                                                        listBinding,
                                                        "callJSDocAPI"
                                                    )
                                                    getDocuments(
                                                        "autoFillInpField",
                                                        imageType,
                                                        listBinding!!,
                                                        position
                                                    )
                                                }
                                                PrefrenceUtils.insertDataInBoolean(
                                                    requireContext(),
                                                    Constants.IS_VERIFICATION_STATUS_PAN,
                                                    true
                                                )
                                                PrefrenceUtils.insertData(
                                                    requireContext(),
                                                    Constants.PANCARDIMAGE,
                                                    imageUrl
                                                )
                                            }
                                            "DRIVING_LICENSE" -> {
                                                showToastMessage("success", "Driving License")
                                                if (additionalInfoList.size > 0) {
                                                    showInpFieldStatus(
                                                        "loading",
                                                        additionalInfoList[0],
                                                        listBinding,
                                                        "callJSDocAPI"
                                                    )
                                                    getDocuments(
                                                        "autoFillInpField",
                                                        imageType,
                                                        listBinding!!,
                                                        position
                                                    )
                                                }
                                                PrefrenceUtils.insertDataInBoolean(
                                                    requireContext(), Constants.ISDL, true
                                                )
                                                PrefrenceUtils.insertData(
                                                    requireContext(),
                                                    Constants.DL_FRONT_IMG,
                                                    imageUrl
                                                )
                                                PrefrenceUtils.insertData(
                                                    requireContext(),
                                                    Constants.DL_BACK_IMG,
                                                    backsideImgUrl
                                                )
                                            }
                                            "VEHICLE_RC" -> {
                                                showToastMessage("success", "Vehicle RC")
                                                PrefrenceUtils.insertDataInBoolean(
                                                    requireContext(), Constants.ISRC, true
                                                )
                                                PrefrenceUtils.insertData(
                                                    requireContext(), Constants.RCFRONT, imageUrl
                                                )
                                                PrefrenceUtils.insertData(
                                                    requireContext(),
                                                    Constants.RCBACK,
                                                    backsideImgUrl
                                                )
                                            }
                                            "PROFILE_PIC" -> {
                                                showToastMessage("success", "Profile Pic")
                                                PrefrenceUtils.insertDataInBoolean(
                                                    requireContext(),
                                                    Constants.IS_VERIFICATION_STATUS_PROFILE_PIC,
                                                    true
                                                )
                                                PrefrenceUtils.insertData(
                                                    requireContext(),
                                                    Constants.PROFILEPICIMAGE,
                                                    imageUrl
                                                )
                                            }
                                            "BANK_DOC" -> {
                                                showToastMessage("success", "Bank Doc")
                                                PrefrenceUtils.insertDataInBoolean(
                                                    requireContext(),
                                                    Constants.IS_VERIFICATION_STATUS_BANK_DOC,
                                                    true
                                                )
                                                PrefrenceUtils.insertData(
                                                    requireContext(),
                                                    Constants.BANKDOCIMAGE,
                                                    imageUrl
                                                )
                                            }
                                        }
                                        if (checkForProceedBtn()) {
                                            binding.proceedBtn.visibility = View.VISIBLE
                                            val params = RelativeLayout.LayoutParams(
                                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                                RelativeLayout.LayoutParams.WRAP_CONTENT
                                            )
                                            params.setMargins(0, 0, 0, 150)
                                            binding.scrollable.layoutParams = params
                                        } else {
                                            binding.proceedBtn.visibility = View.GONE
                                        }
                                    } else {
                                        //uncomment status docUploadImg
//                                        listBinding?.docUploadImg?.let { it1 ->
//                                            Glide.with(requireContext())
//                                                .load(R.drawable.ic_failed_upload).into(
//                                                    it1
//                                                )
//                                        }
                                        listBinding?.uploadDoc?.visibility = View.VISIBLE
                                        listBinding?.uploadDoc?.setBackgroundColor(
                                            ContextCompat.getColor(
                                                requireContext(), R.color.default_200
                                            )
                                        )
                                        listBinding?.docViewBtn?.visibility = View.GONE

                                        if (docsMap[imageType] == "PROFILE_PIC") {
                                            setStatusTextView(
                                                binding.uploadPPFrameLayout,
                                                binding.uploadPPInProgress,
                                                binding.missingProfilePic,
                                                "failure"
                                            )
                                        } else {
                                            setStatusTextView(
                                                listBinding?.uploadStatusFrameLayout,
                                                listBinding?.uploadInProgress,
                                                listBinding?.missingDoc,
                                                "failure"
                                            )
                                        }
//                            Toast.makeText(
//                                requireContext(),
//                                it.documents[0]?.message
//                                    ?: context?.getString(R.string.validation_failed_please_try_again_later),
//                                Toast.LENGTH_SHORT
//                            ).show()

                                        if (currentAdapterItem.componentType == "dropdown") {
                                            listBinding?.dropdownParent?.visibility = View.VISIBLE
                                        }
                                        when (docsMap[imageType]) {
                                            "AADHAAR_CARD" -> {
                                                showToastMessage("failure", "Aadhaar Card")
                                                viewModel.frontAdhaarCard = "";
                                                viewModel.backAdhaarCard = "";
                                                PrefrenceUtils.insertData(
                                                    requireContext(), Constants.AADHARCARDFRONT, ""
                                                )
                                                PrefrenceUtils.insertData(
                                                    requireContext(), Constants.AADHARCARDBACK, ""
                                                )
                                                PrefrenceUtils.insertDataInBoolean(
                                                    activity,
                                                    Constants.IS_VERIFICATION_STATUS_AADHAR,
                                                    false
                                                )
                                            }
                                            "PAN_CARD" -> {
                                                showToastMessage("failure", "PAN Card")
                                                viewModel.imagePath = ""
                                                PrefrenceUtils.insertDataInBoolean(
                                                    requireContext(),
                                                    Constants.IS_VERIFICATION_STATUS_PAN,
                                                    false
                                                )

                                                PrefrenceUtils.insertData(
                                                    requireContext(), Constants.PANCARDIMAGE, ""
                                                )
                                            }
                                            "DRIVING_LICENSE" -> {
                                                showToastMessage("failure", "Driving License")
//                                                docUploadModel.currentDocKey = ""
                                                viewModel.frontDLCard = ""
                                                viewModel.backDLCard = ""

                                                PrefrenceUtils.insertData(
                                                    requireContext(), Constants.DL_FRONT_IMG, ""
                                                )
                                                PrefrenceUtils.insertData(
                                                    requireContext(), Constants.DL_BACK_IMG, ""
                                                )

                                                PrefrenceUtils.insertDataInBoolean(
                                                    requireContext(), Constants.ISDL, false
                                                )
                                            }
                                            "VEHICLE_RC" -> {
                                                showToastMessage("failure", "Vehicle RC")
                                                viewModel.frontRC = ""
                                                viewModel.backRC = ""

                                                PrefrenceUtils.insertDataInBoolean(
                                                    requireContext(), Constants.ISRC, false
                                                )
                                                PrefrenceUtils.insertData(
                                                    requireContext(), Constants.RCFRONT, ""
                                                )
                                                PrefrenceUtils.insertData(
                                                    requireContext(), Constants.RCBACK, ""
                                                )
                                            }
                                            "PROFILE_PIC" -> {
                                                showToastMessage("failure", "Profile Pic")
                                                viewModel.profilePicImagePath = ""
                                                PrefrenceUtils.insertDataInBoolean(
                                                    requireContext(),
                                                    Constants.IS_VERIFICATION_STATUS_PROFILE_PIC,
                                                    false
                                                )

                                                PrefrenceUtils.insertData(
                                                    requireContext(), Constants.PROFILEPICIMAGE, ""
                                                )
                                            }
                                            "BANK_DOC" -> {
                                                showToastMessage("failure", "Bank Doc")
                                                viewModel.bankDoc = ""
                                                PrefrenceUtils.insertDataInBoolean(
                                                    requireContext(),
                                                    Constants.IS_VERIFICATION_STATUS_BANK_DOC,
                                                    false
                                                )
                                                PrefrenceUtils.insertData(
                                                    requireContext(), Constants.BANKDOCIMAGE, ""
                                                )
                                            }
                                        }

                                        listBinding?.rvDocStatusItem?.adapter =
                                            JMDocUploadStatusAdapter(
                                                requireContext(), docUploadModel.setStatusArr(
                                                    "verify_failure", imageType
                                                ), imageType
                                            )
                                    }
                                    //end of else block to check if documents verification has failed
                                    if (checkForProceedBtn()) {
                                        binding.proceedBtn.visibility = View.VISIBLE
                                        val params = RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.MATCH_PARENT,
                                            RelativeLayout.LayoutParams.WRAP_CONTENT
                                        )
                                        params.setMargins(0, 0, 0, 150)
                                        binding.scrollable.layoutParams = params
                                    } else {
                                        binding.proceedBtn.visibility = View.GONE
                                    }
                                }
                            } else {
                                viewModel.disableAllUploadButtons = false
                                binding.rvDocItem.adapter?.notifyDataSetChanged()
                                docUploadModel.currentDocKey = ""
                                toggleButtonState(true, listBinding!!.uploadDoc, listBinding)
                                Navigation.findNavController(binding.root)
                                    .navigate(R.id.nav_error_fragment)
                                //show toast here for non_payroll
                                Toast.makeText(
                                    requireContext(),
                                    R.string.something_went_wrong.toString() + ": 500",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun autoFillInpField(
        imageType: String, listBinding: JmDocUploadListBinding, position: Int
    ) {
        for (item in additionalInfoList) {
            if (item?.docKey == imageType) {
                if (item.value !== "") {
                    listBinding.docNumberTxt.setText(item.value)
                    showInpFieldStatus(
                        "success", item, listBinding, "autoFillInpField"
                    )
                    when (imageType) {
                        "panCard" -> {
                            viewModel.inputPanNumber = item.value ?: ""
                            listBinding.docNumberTxt.hint = "ABCDE1234F"
                        }
                        "drivingLicense" -> {
                            viewModel.inputDLNumber = item.value ?: ""
                            listBinding.docNumberTxt.hint = "MH0120230032897"
                        }
                    }
                } else {
                    showInpFieldStatus(
                        "empty", item, listBinding, "autoFillInpField"
                    )
                }
            }
        }
    }

    private fun setStatusTextView(
        frameLayout: FrameLayout?, progressBar: ProgressBar?, textView: TextView?, status: String
    ) {
        if (status == "success") {
            textView?.text = context?.getString(R.string._success)
            frameLayout?.setBackgroundResource(R.drawable.green_rect)
            if (progressBar !== null) {
                progressBar.clearAnimation()
                progressBar.visibility = View.GONE
//                progressBar.scaleX = 0F
//                progressBar.scaleY = 0F
            }

            if (textView != null) {
                setStatusTextViewMargins(0, textView)
                textView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_green_tick2, 0, 0, 0
                )
                textView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.green
                    )
                )
                textView.compoundDrawablePadding = 16
            }
        } else {
            when (status) {
                "failure" -> {
                    progressBar?.visibility = View.GONE
                    progressBar?.clearAnimation()
                    if (textView != null) {
                        setStatusTextViewMargins(0, textView)
                    }
                    textView?.text =
                        context?.getString(R.string.freshchat_calendar_meeting_state_failed)
                    textView?.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_alert_icon, 0, 0, 0
                    )
                    textView?.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.tv_missing_bg
                        )
                    )
                    frameLayout?.setBackgroundResource(R.drawable.ic_missing)
                }
                "missing" -> {
                    textView?.text = context?.getString(R.string.missing)
                    textView?.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_alert_icon, 0, 0, 0
                    )
                    textView?.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.tv_missing_bg
                        )
                    )
                    frameLayout?.setBackgroundResource(R.drawable.ic_missing)
                }
                "uploading" -> {
                    frameLayout?.setBackgroundResource(R.drawable.ic_missing)
                    textView?.text = context?.getString(R.string.uploading)
                    textView?.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, 0, 0
                    )
                    if (progressBar !== null) {
                        if (textView != null) {
                            setStatusTextViewMargins(40, textView)
                        }

                        progressBar.visibility = View.VISIBLE
                        val rotate = RotateAnimation(
                            0F,
                            180F,
                            Animation.RELATIVE_TO_SELF,
                            0.5f,
                            Animation.RELATIVE_TO_SELF,
                            0.5f
                        )
                        rotate.duration = 4000
                        rotate.interpolator = LinearInterpolator()
                        progressBar.startAnimation(rotate)
                    }
                }
            }
        }
    }

    private fun showToastMessage(type: String, docType: String) {
        if (type == "success") {
            Toast.makeText(
                requireContext(), "$docType uploaded successfully!", Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                requireContext(), "Please upload a valid $docType", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setStatusTextViewMargins(leftMargin: Int?, textView: TextView) {
        if (leftMargin !== null) {
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(leftMargin, 0, 0, 0)

            //Need to add this check because profile pic layout is not Frame layout
            textView.layoutParams = params
        }
    }

    private fun checkPermissions(arrayOf: Array<String>): Boolean {
        var permission = true
        arrayOf.forEach {
            if (!permission) return@forEach
            if (ContextCompat.checkSelfPermission(
                    requireContext(), it
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permission = false
            }
        }
        return permission
    }

    private fun givePermission() {
        permReqLauncher.launch(
            arrayOf(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            )
        )
    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value == true
            }
            if (granted) {
                showBottomDialog()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please allow permissions to upload documents",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun createImageFile(): File {
        val imageDir = requireContext().getExternalFilesDir(
            Environment.DIRECTORY_PICTURES
        )
        return File.createTempFile("img_", ".jpg", imageDir)
    }

    override fun uploadDocNumberClickListener(
        key: String,
        position: Int,
        listBinding: JmDocUploadListBinding,
        inputField: JMDocUploadDTO.AdditionalInfo?
    ) {
        callJSDocNumberAPI(inputField!!, listBinding)
    }

    override fun uploadDocClickListener(
        key: String,
        position: Int,
        listBinding: JmDocUploadListBinding,
        item: JMDocUploadDTO.Document
    ) {
        val viewSharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        currentAdapterItem = item

        binding.scrollable.post(Runnable {
            binding.scrollable.smoothScrollTo(0, binding.rvDocItem.getChildAt(position).top)
        })

        when (key) {
            "AADHAAR_CARD" -> {
                doubleDocUploadAPI(
                    viewSharedViewModel,
                    viewModel.frontAdhaarCard,
                    viewModel.backAdhaarCard,
                    viewModel.bitmapDataAADHARFRONT!!,
                    viewModel.bitmapDataAADHARBACK!!,
                    "frontAadhaar",
                    "backAadhaar",
                    Constants.AADHARCARDFRONT,
                    Constants.AADHARCARDBACK,
                    "aadhaarCard",
                    listBinding,
                    position
                )
            }
            "PAN_CARD" -> {
                singleDocUploadAPI(
                    viewSharedViewModel,
                    viewModel.imagePath,
                    viewModel.bitmapDataPAN!!,
                    Constants.PANCARDIMAGE,
                    "PanCard",
                    "panCard",
                    listBinding,
                    position
                )
            }
            "DRIVING_LICENSE" -> {
                doubleDocUploadAPI(
                    viewSharedViewModel,
                    viewModel.frontDLCard,
                    viewModel.backDLCard,
                    viewModel.bitmapDataDLFront!!,
                    viewModel.bitmapDataDLBack!!,
                    "DLFront",
                    "DLBack",
                    Constants.DL_FRONT_IMG,
                    Constants.DL_BACK_IMG,
                    "drivingLicense",
                    listBinding,
                    position
                )
            }
            "VEHICLE_RC" -> {
                doubleDocUploadAPI(
                    viewSharedViewModel,
                    viewModel.frontRC,
                    viewModel.backRC,
                    viewModel.bitmapDataRCFront!!,
                    viewModel.bitmapDataRCBack!!,
                    "RCFront",
                    "RCBack",
                    Constants.RCFRONT,
                    Constants.RCBACK,
                    "vehicleRc",
                    listBinding,
                    position
                )
            }
            "BANK_DOC" -> {
                singleDocUploadAPI(
                    viewSharedViewModel,
                    viewModel.bankDoc,
                    viewModel.bitmapDataBankDoc!!,
                    Constants.BANKDOCIMAGE,
                    "BankDoc",
                    "bankPassbookOrCancelledCheque",
                    listBinding,
                    position
                )
            }
        }
    }

    private fun singleDocUploadAPI(
        viewSharedViewModel: SharedViewModel,
        viewModelFront: String,
        viewModelBitmap: Bitmap,
        constantName: String,
        fileName: String,
        imageType: String,
        listBinding: JmDocUploadListBinding,
        position: Int
    ) {
        lifecycleScope.launchWhenStarted {
            if (viewModelFront.isNotEmpty()) {
                val attribute = HashMap<String, Any>()
                val properties = Properties()
                properties.addAttribute("docType", imageType)
                attribute["docType"] = imageType
                captureAllJSEEvents(
                    requireContext(), "jse_doc_upload_now_clicked", attribute, properties
                )
                setStatusTextView(
                    listBinding.uploadStatusFrameLayout,
                    listBinding.uploadInProgress,
                    listBinding.missingDoc,
                    "uploading"
                )
                viewSharedViewModel.newUploadImageFile(
                    requireContext(), Constants.TOKENCONSTANT + PrefrenceUtils.retriveData(
                        requireContext(), Constants.API_TOKEN
                    ), viewModelBitmap, fileName
                )?.observe(requireActivity()) { data ->
                    if (data.fileURL?.isNotEmpty() == true) {
                        PrefrenceUtils.insertData(requireContext(), constantName, data.fileURL)
                        //uncomment status docUploadImg
//                    listBinding.docUploadImg.let { it1 ->
//                        Glide.with(requireContext()).load(R.drawable.for_verify).into(
                        //                            it1
//                        )
//                    }
                        listBinding.rvDocStatusItem.adapter = JMDocUploadStatusAdapter(
                            requireContext(),
                            docUploadModel.setStatusArr("upload_success", imageType),
                            imageType
                        )
                        listBinding.uploadDoc.isEnabled = false
                        listBinding.uploadDoc.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(), R.color.light_color_heading
                            )
                        )
                        callJSDocAPI(
                            imageType,
                            viewSharedViewModel,
                            data.fileURL!!,
                            "",
                            listBinding,
                            position
                        )
                    }
                }
            } else {
                setStatusTextView(
                    listBinding.uploadStatusFrameLayout,
                    listBinding.uploadInProgress,
                    listBinding.missingDoc,
                    "missing"
                )
                toggleButtonState(true, listBinding.uploadDoc, listBinding)
            }
        }
    }

    private fun doubleDocUploadAPI(
        viewSharedViewModel: SharedViewModel,
        viewModelFront: String,
        viewModelBack: String,
        viewModelBitmapFront: Bitmap,
        viewModelBitmapBack: Bitmap,
        filenameFront: String,
        filenameBack: String,
        frontConstantName: String,
        backConstantName: String,
        imageType: String,
        listBinding: JmDocUploadListBinding,
        position: Int
    ) {
        var frontUrl: String
        var backUrl: String
        toggleButtonState(false, listBinding.uploadDoc, listBinding)
        lifecycleScope.launchWhenStarted {
            if (viewModelFront.isNotEmpty()) {
                val attribute = HashMap<String, Any>()
                val properties = Properties()
                properties.addAttribute("docType", imageType)
                attribute["docType"] = imageType
                captureAllJSEEvents(
                    requireContext(), "jse_doc_upload_now_clicked", attribute, properties
                )
                setStatusTextView(
                    listBinding.uploadStatusFrameLayout,
                    listBinding.uploadInProgress,
                    listBinding.missingDoc,
                    "uploading"
                )
                //1st API call for front img
                viewSharedViewModel.newUploadImageFile(
                    requireContext(), Constants.TOKENCONSTANT + PrefrenceUtils.retriveData(
                        requireContext(), Constants.API_TOKEN
                    ), viewModelBitmapFront, filenameFront
                )?.observe(requireActivity()) { data ->
                    if (data.fileURL?.isNotEmpty() == true) {
                        frontUrl = data.fileURL!!
                        PrefrenceUtils.insertData(requireContext(), frontConstantName, data.fileURL)
                        if (viewModelBack.isNotEmpty())
                        //2nd API call for back img
                            viewSharedViewModel.newUploadImageFile(
                                requireContext(),
                                Constants.TOKENCONSTANT + PrefrenceUtils.retriveData(
                                    requireContext(), Constants.API_TOKEN
                                ),
                                viewModelBitmapBack,
                                filenameBack
                            ).observe(requireActivity()) { data ->
                                if (data.fileURL?.isNotEmpty() == true) {
                                    PrefrenceUtils.insertData(
                                        requireContext(), backConstantName, data.fileURL
                                    )
                                    backUrl = data.fileURL!!
                                    if (frontUrl.isNotEmpty() && backUrl.isNotEmpty()) {
                                        callJSDocAPI(
                                            imageType,
                                            viewSharedViewModel,
                                            frontUrl,
                                            backUrl,
                                            listBinding,
                                            position
                                        )
                                    }
                                    //uncomment status docUploadImg
//                                    listBinding.docUploadImg.let { it1 ->
//                                        Glide.with(requireContext()).load(R.drawable.for_verify)
//                                            .into(
//                                                it1
//                                            )
//                                    }
                                    listBinding.rvDocStatusItem.adapter = JMDocUploadStatusAdapter(
                                        requireContext(), docUploadModel.setStatusArr(
                                            "upload_success", imageType
                                        ), imageType
                                    )
                                } else {
                                    toggleButtonState(true, listBinding.uploadDoc, listBinding)
                                }
                            }
                    } else {
                        toggleButtonState(true, listBinding.uploadDoc, listBinding)
                    }
                }
            } else {
                setStatusTextView(
                    listBinding.uploadStatusFrameLayout,
                    listBinding.uploadInProgress,
                    listBinding.missingDoc,
                    "missing"
                )
                toggleButtonState(true, listBinding.uploadDoc, listBinding)
            }
        }
    }

    fun toggleButtonState(
        inp: Boolean, buttonLayout: ConstraintLayout, listBinding: JmDocUploadListBinding
    ) {
        var colorVal = R.color.default_200
        if (!inp) {
            colorVal = R.color.light_color_heading
        }
        listBinding.uploadDoc.isEnabled = inp
        listBinding.uploadDoc.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(), colorVal
            )
        )
    }

    fun initLoader(context: Context?) {
        dialogLoader = Dialog(requireContext())
        dialogLoader.window?.setBackgroundDrawableResource(android.R.color.transparent)
//        dialogLoader.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialogLoader.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE)
        dialogLoader.setTitle("Validating Bank Details")
        dialogLoader.setCancelable(false)
        dialogLoader.setContentView(R.layout.layout_loader)
        val textViewAnimation: TextView = dialogLoader.findViewById<TextView>(R.id.layoutLoaderTxt)
        textViewAnimation.text = "Getting your documents"
        val imageViewAnimation: ImageView = dialogLoader.findViewById<ImageView>(R.id.animate_icon)
        val rotate = RotateAnimation(
            0F, 180F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 4000
        rotate.interpolator = LinearInterpolator()
        imageViewAnimation.startAnimation(rotate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.disableAllUploadButtons = false
    }
}