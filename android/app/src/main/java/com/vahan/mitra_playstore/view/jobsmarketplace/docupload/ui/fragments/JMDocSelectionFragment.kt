package com.vahan.mitra_playstore.view.jobsmarketplace.docupload.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentJmDocSelectionBinding
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.FileUtils
import com.vahan.mitra_playstore.utils.UriUtils
import com.vahan.mitra_playstore.view.bottomsheet.UploadDeleteDocDialog
import com.vahan.mitra_playstore.view.bottomsheet.UploadDocBottomSheet
import com.vahan.mitra_playstore.view.documentupload.viewmodel.*
import java.io.*
import java.util.*


class JMDocSelectionFragment : Fragment() {
    private val PROFILEPIC = "PROFILE_PIC"
    private val PAN_CARD = "PANCARD"
    private val ADHAR_CARD_F = "AADHAAR_CARD_F"
    private val ADHAR_CARD_B = "AADHAAR_CARD_B"
    private val DL_CARD_F = "DRIVING_LICENCE_CARD_F"
    private val DL_CARD_B = "DRIVING_LICENCE_CARD_B"
    private val RC_CARD_F = "VEHICLE_RC_F"
    private val RC_CARD_B = "VEHICLE_RC_B"
    private val BANK_DOCUMENT = "BANK_DOC"
    private var idType = ""
    lateinit var binding: FragmentJmDocSelectionBinding
    lateinit var viewModel: UploadViewModels
    var isFrontDoc = true
    private var receivedUriKey: Uri? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var REQUEST_CODE_Image = 45
    private var tempImageUri: Uri? = null
    private var tempImagePath = ""


    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { sucess ->
            if (sucess) {
                when (getScreenType()) {
                    PROFILEPIC -> {
                        viewModel.profilePicImagePath = tempImagePath
                        viewModel.createBitMap(tempImageUri!!, PROFILE_PIC)
                    }
                    PAN_CARD -> {
                        viewModel.imagePath = tempImagePath
                        viewModel.createBitMap(tempImageUri!!, PAN)
                    }
                    ADHAR_CARD_B -> {
                        viewModel.createBitMap(tempImageUri!!, BACK_AADHAR)
                        viewModel.backAdhaarCard = tempImagePath
                    }
                    ADHAR_CARD_F -> {
                        viewModel.frontAdhaarCard = tempImagePath
                        viewModel.createBitMap(tempImageUri!!, FRONT_AADHAR)
                    }
                    DL_CARD_F -> {
                        viewModel.frontDLCard = tempImagePath
                        viewModel.createBitMap(tempImageUri!!, FRONT_DL)
                    }
                    DL_CARD_B -> {
                        viewModel.backDLCard = tempImagePath
                        viewModel.createBitMap(tempImageUri!!, BACK_DL)
                    }
                    RC_CARD_F -> {
                        viewModel.frontRC = tempImagePath
                        viewModel.createBitMap(tempImageUri!!, FRONT_RC)
                    }
                    RC_CARD_B -> {
                        viewModel.backRC = tempImagePath
                        viewModel.createBitMap(tempImageUri!!, BACK_RC)
                    }
                    BANK_DOCUMENT -> {
                        viewModel.bankDoc = tempImagePath
                        viewModel.createBitMap(tempImageUri!!, BANK_DOC)
                    }

                }
                setImage(tempImagePath)
                isImageAvaileble()
            }

        }

    private fun createImageFile(): File {
        val imageDir = requireContext().getExternalFilesDir(
            Environment.DIRECTORY_PICTURES
        )
        return File.createTempFile(getScreenType(), ".jpg", imageDir)
    }

    private fun getImage() {
        receivedUriKey.let { uri ->
            if (uri != null) {
                val fileexist = File(receivedUriKey!!.path).exists()
                if (fileexist) {
                    when (getScreenType()) {
                        PROFILEPIC -> setImage(viewModel.profilePicImagePath)
                        PAN_CARD -> setImage(viewModel.imagePath)
                        ADHAR_CARD_B -> setImage(viewModel.backAdhaarCard)
                        ADHAR_CARD_F -> setImage(viewModel.frontAdhaarCard)
                        DL_CARD_F -> setImage(viewModel.frontDLCard)
                        DL_CARD_B -> setImage(viewModel.backDLCard)
                        RC_CARD_F -> setImage(viewModel.frontRC)
                        RC_CARD_B -> setImage(viewModel.backRC)
                        BANK_DOCUMENT -> setImage(viewModel.bankDoc)
                    }
                    isImageAvaileble()
                } else
                    Log.d("TAG", "getImage: file null")
            } else
                Log.d("TAG", "getImage: uri null")
        }
    }


    private fun activityResult() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
//            if (activityResult.resultCode == REQUEST_CODE_Image)
                if (activityResult.data != null) {
                    val bitmapImage = activityResult.data!!.extras?.get("data") as Bitmap
                    val bytes = ByteArrayOutputStream()
                    bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                    val path: String = MediaStore.Images.Media.insertImage(
                        requireActivity().contentResolver,
                        bitmapImage,
                        "IMG_" + System.currentTimeMillis(),
                        null
                    )
                    receivedUriKey = Uri.parse(path)
//                    receivedUriKey = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
//                            saveImageAfterQ(bitmapImage)
//                    }else{
//                        saveImageInLegacyStyle(bitmapImage)
//                    }bitmapImage
                    Log.d("TAG", "activityResult: ${activityResult.data}")

                } else {
                    Log.d("TAG", "activityResult: null")
                }

            }
    }

    private var takePictureFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let {
                var path = ""

                path = if (it.toString().contains("com.google")) {
                    UriUtils.getPath(requireContext(), it)
                } else {
                    FileUtils.getRealPath(requireContext(), it)
                }
//                    UriUtils.getPath(requireContext(), it)
                when (getScreenType()) {
                    PROFILEPIC -> {
                        viewModel.createBitMap(it, PROFILE_PIC)
                        viewModel.profilePicImagePath = path
                    }
                    PAN_CARD -> {
                        viewModel.createBitMap(it, PAN)
                        viewModel.imagePath = path
                    }
                    ADHAR_CARD_B -> {
                        viewModel.createBitMap(it, BACK_AADHAR)
                        viewModel.backAdhaarCard = path
                    }
                    ADHAR_CARD_F -> {
                        viewModel.frontAdhaarCard = path
                        viewModel.createBitMap(it, FRONT_AADHAR)
                    }
                    DL_CARD_F -> {
                        viewModel.frontDLCard = path
                        viewModel.createBitMap(it, FRONT_DL)
                    }
                    DL_CARD_B -> {
                        viewModel.createBitMap(it, BACK_DL)
                        viewModel.backDLCard = path
                    }
                    RC_CARD_F -> {
                        viewModel.createBitMap(it, FRONT_RC)
                        viewModel.frontRC = path
                    }
                    RC_CARD_B -> {
                        viewModel.createBitMap(it, BACK_RC)
                        viewModel.backRC = path
                    }
                    BANK_DOCUMENT -> {
                        viewModel.createBitMap(it, BANK_DOC)
                        viewModel.bankDoc = path
                    }
                }
                setImage(path)
                isImageAvaileble()

            }

            if (it != null) {
                var path = ""
                path = if (it.toString().contains("com.google")) {
                    UriUtils.getPath(requireContext(), it)
                } else {
                    FileUtils.getRealPath(requireContext(), it)
                }
//                UriUtils.getPath(requireContext(), it)
                when (getScreenType()) {
                    PROFILEPIC -> {
                        viewModel.createBitMap(it, PROFILE_PIC)
                        viewModel.profilePicImagePath = path
                    }
                    PAN_CARD -> {
                        viewModel.createBitMap(it, PAN)
                        viewModel.imagePath = path
                    }
                    ADHAR_CARD_B -> {
                        viewModel.createBitMap(it, BACK_AADHAR)
                        viewModel.backAdhaarCard = path
                    }
                    ADHAR_CARD_F -> {
                        viewModel.createBitMap(it, FRONT_AADHAR)
                        viewModel.frontAdhaarCard = path
                    }
                    DL_CARD_F -> {
                        viewModel.createBitMap(it, FRONT_DL)
                        viewModel.frontDLCard = path
                    }
                    DL_CARD_B -> {
                        viewModel.createBitMap(it, BACK_DL)
                        viewModel.backDLCard = path
                    }
                    RC_CARD_F -> {
                        viewModel.createBitMap(it, FRONT_RC)
                        viewModel.frontRC = path
                    }
                    RC_CARD_B -> {
                        viewModel.createBitMap(it, BACK_RC)
                        viewModel.backRC = path
                    }
                    BANK_DOCUMENT -> {
                        viewModel.createBitMap(it, BANK_DOC)
                        viewModel.bankDoc = path
                    }
                }
                setImage(path)
                isImageAvaileble()
            }
        }


    fun getScreenType(): String {
        var type = ""
        type = if (idType == "AADHAAR_CARD") {
            if (isFrontDoc) {
                ADHAR_CARD_F
            } else {
                ADHAR_CARD_B
            }
        } else if (idType == "DRIVING_LICENSE") {
            if (isFrontDoc) {
                DL_CARD_F
            } else {
                DL_CARD_B
            }
        } else if (idType == "VEHICLE_RC") {
            if (isFrontDoc) {
                RC_CARD_F
            } else {
                RC_CARD_B
            }
        } else if (idType == "PROFILE_PIC") {
            PROFILEPIC
        } else if (idType == "BANK_DOC") {
            BANK_DOCUMENT
        } else {
            PAN_CARD
        }
        return type
    }

    fun deleteImage() {
        when (getScreenType()) {
            PROFILEPIC -> viewModel.profilePicImagePath = ""
            PAN_CARD -> {
                viewModel.imagePath = ""
                setupPlaceholderImg("front")
            }
            ADHAR_CARD_B -> {
                viewModel.backAdhaarCard = ""
                setupPlaceholderImg("back")
            }
            ADHAR_CARD_F -> {
                viewModel.frontAdhaarCard = ""
                setupPlaceholderImg("front")
            }
            DL_CARD_F -> {
                viewModel.frontDLCard = ""
                setupPlaceholderImg("front")
            }
            DL_CARD_B -> {
                viewModel.backDLCard = ""
                setupPlaceholderImg("back")
            }
            RC_CARD_F -> {
                viewModel.frontRC = ""
                setupPlaceholderImg("front")
            }
            RC_CARD_B -> {
                viewModel.backRC = ""
                setupPlaceholderImg("back")
            }
            BANK_DOCUMENT -> {
                viewModel.bankDoc = ""
                setupPlaceholderImg("front")
            }
        }
        if (isFrontDoc) {
            binding.afterFrontPhotoClick.visibility = View.GONE
            binding.beforeFrontPhotoClick.visibility = View.VISIBLE
            Glide.with(requireContext()).load("").into(binding.frontImg)
        } else {
            binding.afterBackPhotoClick.visibility = View.GONE
            binding.beforeBackPhotoClick.visibility = View.VISIBLE
            Glide.with(requireContext()).load("").into(binding.backImg)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        arguments.apply {
            idType = this?.getString(Constants.ID_TYPE) ?: ""
        }
        FragmentJmDocSelectionBinding.inflate(inflater, container, false).run {
            binding = this
            return root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(UploadViewModels::class.java)
        viewModel.frontAdhaarCard = ""
        viewModel.backAdhaarCard = ""
        viewModel.imagePath = ""
        viewModel.profilePicImagePath = ""
        viewModel.frontDLCard = ""
        viewModel.backDLCard = ""
        viewModel.frontRC = ""
        viewModel.backRC = ""
        viewModel.bankDoc = ""
        initViews()
        binding.frontPlaceholderImg.visibility = View.VISIBLE
        setupPlaceholderImg("front")
        if (idType == "AADHAAR_CARD" || idType == "DRIVING_LICENSE" || idType == "VEHICLE_RC") {
            binding.backPlaceholderImg.visibility = View.VISIBLE
            setupPlaceholderImg("back")
        }
    }

    private fun setupPlaceholderImg(side: String) {
        if (side == "front") {
            when (idType) {
                "AADHAAR_CARD" -> {
                    if (viewModel.frontAdhaarCard.isNotEmpty()) {
                        binding.frontPlaceholderImg.visibility = View.GONE
                    } else {
                        binding.frontPlaceholderImg.visibility = View.VISIBLE
                        binding.frontPlaceholderImg.setImageResource(R.drawable.ic_front_aadhar)
                    }
                }
                "PAN_CARD" -> {
                    if (viewModel.imagePath.isNotEmpty()) {
                        binding.frontPlaceholderImg.visibility = View.GONE
                    } else {
                        binding.frontPlaceholderImg.visibility = View.VISIBLE
                        binding.frontPlaceholderImg.setImageResource(R.drawable.ic_pandcard_3x)
                    }
                }
                "VEHICLE_RC" -> {
                    if (viewModel.frontRC.isNotEmpty()) {
                        binding.frontPlaceholderImg.visibility = View.GONE
                    } else {
                        binding.frontPlaceholderImg.visibility = View.VISIBLE
                        binding.frontPlaceholderImg.setImageResource(R.drawable.ic_rc_front)
                    }
                }
                "BANK_DOC" -> {
                    if (viewModel.bankDoc.isNotEmpty()) {
                        binding.frontPlaceholderImg.visibility = View.GONE
                    } else {
                        binding.frontPlaceholderImg.visibility = View.VISIBLE
                        binding.frontPlaceholderImg.setImageResource(R.drawable.ic_rc_front)
                    }
                }
                else -> {
                    if (viewModel.frontDLCard.isNotEmpty()) {
                        binding.frontPlaceholderImg.visibility = View.GONE
                    } else {
                        binding.frontPlaceholderImg.visibility = View.VISIBLE
                        binding.frontPlaceholderImg.setImageResource(R.drawable.ic_dl_4x)
                    }
                }
            }
        } else {
            when (idType) {
                "AADHAAR_CARD" -> {
                    if (viewModel.backAdhaarCard.isNotEmpty()) {
                        binding.backPlaceholderImg.visibility = View.GONE
                    } else {
                        binding.backPlaceholderImg.visibility = View.VISIBLE
                        binding.backPlaceholderImg.setImageResource(R.drawable.ic_aadhar_back)
                    }
                }
                "VEHICLE_RC" -> {
                    if (viewModel.backRC.isNotEmpty()) {
                        binding.backPlaceholderImg.visibility = View.GONE
                    } else {
                        binding.backPlaceholderImg.visibility = View.VISIBLE
                        binding.backPlaceholderImg.setImageResource(R.drawable.ic_rc_back)
                    }
                }
                else -> {
                    if (viewModel.backDLCard.isNotEmpty()) {
                        binding.backPlaceholderImg.visibility = View.GONE
                    } else {
                        binding.backPlaceholderImg.visibility = View.VISIBLE
                        binding.backPlaceholderImg.setImageResource(R.drawable.driving_back)
                    }
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun initViews() {
        when (idType) {
            "AADHAAR_CARD" -> {
                binding.backDocRL.visibility = View.VISIBLE
                binding.pageHeaderTxt.text = context?.getString(R.string.upload_aadhar_card)
                binding.txtUploadDes.text =
                    context?.getString(R.string.please_upload_uour_aadhar_card)
            }
            "PAN_CARD" -> {
                binding.backDocRL.visibility = View.GONE
                binding.pageHeaderTxt.text = context?.getString(R.string.upload_pan_card)
                binding.txtUploadDes.text =
                    context?.getString(R.string.please_upload_your_pan_card_photo_if_you_need_help_uploading_the_same)
            }
            "VEHICLE_RC" -> {
                binding.backDocRL.visibility = View.VISIBLE
                binding.pageHeaderTxt.text = context?.getString(R.string.upload_rc)
                binding.txtUploadDes.text =
                    context?.getString(R.string.please_upload_uour_rc)
            }
            "BANK_DOC" -> {
                binding.backDocRL.visibility = View.GONE
                binding.pageHeaderTxt.text = context?.getString(R.string.upload_bank_doc)
                binding.txtUploadDes.text =
                    context?.getString(R.string.please_upload_uour_bank_doc)
            }
            else -> {
                binding.backDocRL.visibility = View.VISIBLE
                binding.pageHeaderTxt.text = context?.getString(R.string.upload_driving_license)
                binding.txtUploadDes.text =
                    context?.getString(R.string.upload_driving_license_desc)
            }
        }
        clickListners()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityResult()
    }

    private fun isImageAvaileble(): Boolean {
        val type = false
        if (idType == "AADHAAR_CARD" || idType == "DRIVING_LICENSE" || idType == "VEHICLE_RC") {
            if (viewModel.backAdhaarCard.isNotEmpty() && viewModel.frontAdhaarCard.isNotEmpty()) {
                showSaveButton()
            } else if (viewModel.backDLCard.isNotEmpty() && viewModel.frontDLCard.isNotEmpty()) {
                showSaveButton()
            } else if (viewModel.backRC.isNotEmpty() && viewModel.frontRC.isNotEmpty()) {
                showSaveButton()
            } else {
                hideSaveButton()
            }
        } else if (idType == "BANK_DOC") {
            if (viewModel.bankDoc.isNotEmpty()) {
                showSaveButton()
            } else {
                hideSaveButton()
            }
        } else {
            if (viewModel.imagePath.isNotEmpty()) {
                showSaveButton()
            } else {
                hideSaveButton()
            }
        }
        return type
    }

    private fun showSaveButton() {
        binding.saveButton.isClickable = true
        binding.saveButton.isEnabled = true
        binding.saveButton.setBackgroundResource(R.drawable.curver_corner_12)
    }

    private fun hideSaveButton() {
        binding.saveButton.isClickable = false
        binding.saveButton.isEnabled = false
        binding.saveButton.setBackgroundResource(R.drawable.curver_corner_12_disable)
    }


    private fun clickListners() {
        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.takeFrontPic.setOnClickListener {
            setIsFrontImg(true)
            takePicBtnClick()
        }
        binding.takeBackPic.setOnClickListener {
            setIsFrontImg(false)
            takePicBtnClick()
        }

        binding.deleteFrontImage.setOnClickListener {
            setIsFrontImg(true)
            UploadDeleteDocDialog(requireContext()) {
                deleteImage()
            }.show()
        }

        binding.deleteBackImage.setOnClickListener {
            setIsFrontImg(false)
            UploadDeleteDocDialog(requireContext()) {
                deleteImage()
            }.show()
        }
        binding.saveButton.setOnClickListener {
            viewModel.disableAllUploadButtons = true
            findNavController().navigate(R.id.nav_fragment_jobsmarketplace_upload)
        }
    }

    fun takePicBtnClick() {
        if (checkPermissions(
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
                )
            )
        ) {
            UploadDocBottomSheet(requireContext()) { isCamera ->

                if (isCamera) {
                    val file =
                        viewModel.createNewFile(
                            requireContext().getExternalFilesDir(
                                Environment.DIRECTORY_PICTURES
                            ), idType, getScreenType()
                        )
                    val uri = FileProvider.getUriForFile(
                        requireContext(),
                        "${context?.packageName}.provider",
                        file
                    )
                    receivedUriKey = null
                    tempImageUri = FileProvider.getUriForFile(
                        requireContext(),
                        "${context?.packageName}.provider", createImageFile().also {
                            tempImagePath = it.absolutePath
                        }
                    )

                    cameraLauncher.launch(tempImageUri)

//                        val intenImatToSec = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                        startActivityForResult(intenImatToSec, REQUEST_CODE_Image)
                    //  takePictureViaCamera.launch(uri)
                } else {
                    takePictureFromGallery.launch("image/*")
                }
            }.show()
        } else {
            givePermission()
        }
    }

    fun setIsFrontImg(checkVal: Boolean = false) {
        isFrontDoc = checkVal
    }

    fun setImage(url: String) {
        if (isFrontDoc) {
            binding.afterFrontPhotoClick.visibility = View.VISIBLE
            binding.beforeFrontPhotoClick.visibility = View.GONE
            binding.frontPlaceholderImg.visibility = View.GONE
            Glide.with(requireContext()).load(url).dontAnimate().into(binding.frontImg)
        } else {
            binding.afterBackPhotoClick.visibility = View.VISIBLE
            binding.beforeBackPhotoClick.visibility = View.GONE
            binding.backPlaceholderImg.visibility = View.GONE
            Glide.with(requireContext()).load(url).dontAnimate().into(binding.backImg)
        }
    }

    private fun givePermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            ),
            202
        )
    }

    private fun checkPermissions(arrayOf: Array<String>): Boolean {
        var permission = true
        arrayOf.forEach {
            if (!permission) return@forEach
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    it
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permission = false
            }
        }
        return permission
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_Image && data != null) {
            val bitmapImage = data.extras?.get("data") as Bitmap
            val bytes = ByteArrayOutputStream()
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path: String = MediaStore.Images.Media.insertImage(
                requireActivity().contentResolver,
                bitmapImage,
                "IMG_" + System.currentTimeMillis(),
                null
            )
            receivedUriKey = Uri.parse(path)
            getImage()
            Log.d("TAG", "activityResult: $data")

        } else {
            Log.d("TAG", "onActivityResult: $requestCode")
        }
    }

}