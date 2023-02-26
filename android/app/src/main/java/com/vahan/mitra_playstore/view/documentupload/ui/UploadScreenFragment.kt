package com.vahan.mitra_playstore.view.documentupload.ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentUploadScreenBinding
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.FileUtils
import com.vahan.mitra_playstore.utils.UriUtils
import com.vahan.mitra_playstore.view.BaseApplication
import com.vahan.mitra_playstore.view.bottomsheet.UploadDeleteDocDialog
import com.vahan.mitra_playstore.view.bottomsheet.UploadDocBottomSheet
import com.vahan.mitra_playstore.view.documentupload.viewmodel.*
import java.io.*
import java.util.*


class UploadScreenFragment : Fragment() {
    private val PROFILEPIC = "PROFILE_PIC"
    private val PAN_CARD = "PANCARD"
    private val ADHAR_CARD_F = "AADHAAR_CARD_F"
    private val ADHAR_CARD_B = "AADHAAR_CARD_B"
    private val DL_CARD_F = "DRIVING_LICENCE_CARD_F"
    private val DL_CARD_B = "DRIVING_LICENCE_CARD_B"
    private val RC_CARD_F = "VEHICLE_RC_F"
    private val RC_CARD_B = "VEHICLE_RC_B"
    private var idType = ""
    private var is_job_seeker_upload_docs = ""
    lateinit var binding: FragmentUploadScreenBinding
    lateinit var viewModel: UploadViewModels
    var isFrontAdhaarCard = true
    var isFrontDrivingLicense = true
    var isFrontRc = true
    private var receivedUriKey: Uri? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var REQUEST_CODE_Image = 45
    private var tempImageUri: Uri? = null
    private var tempImagePath = ""


    private val carmeraLauncher =
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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveImageAfterQ(bitmapImage: Bitmap): Uri? {
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        var imageUri: Uri? = null
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }
        //use application context to get contentResolver
        val contentResolver = requireContext().contentResolver

        contentResolver.also { resolver ->
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { resolver.openOutputStream(it) }
        }

        fos?.use { bitmapImage.compress(Bitmap.CompressFormat.JPEG, 70, it) }
        contentValues.clear()
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        contentResolver.update(imageUri!!, contentValues, null, null)
        return imageUri
    }

    private fun saveImageInLegacyStyle(bitmapImage: Bitmap): Uri? {
        val appContext = BaseApplication.context
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        val directory = getExternalStoragePublicDirectory(DIRECTORY_PICTURES)
        val file = File(directory, filename)
        val outStream = FileOutputStream(file)
        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outStream)
        outStream.flush()
        outStream.close()
        MediaScannerConnection.scanFile(
            appContext, arrayOf(file.absolutePath),
            null, null
        )
        return FileProvider.getUriForFile(
            appContext!!, "${appContext.packageName}.provider",
            file
        )
    }

    private var takePictureFromGalary =
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
                }
                setImage(path)
                isImageAvaileble()
            }
        }


    fun getScreenType(): String {
        var type = ""
        type = if (idType == "AADHAAR_CARD") {
            if (isFrontAdhaarCard) {
                ADHAR_CARD_F
            } else {
                ADHAR_CARD_B
            }
        } else if (idType == "DRIVING_LICENSE") {
            if (isFrontDrivingLicense) {
                DL_CARD_F
            } else {
                DL_CARD_B
            }
        } else if (idType == "VEHICLE_RC") {
            if (isFrontRc) {
                RC_CARD_F
            } else {
                RC_CARD_B
            }
        } else if (idType == "PROFILE_PIC") {
            PROFILEPIC
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
        }
        Glide.with(requireContext()).load("").into(binding.docAttachImage)
        binding.afterPhotoClick.visibility = View.GONE
        binding.beforePhotoClick.visibility = View.VISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        arguments.apply {
            idType = this?.getString(Constants.ID_TYPE) ?: ""
            is_job_seeker_upload_docs = this?.getString("navigation_to") ?: ""
        }
        FragmentUploadScreenBinding.inflate(inflater, container, false).run {
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
        initViews()
        UXCam.setAutomaticScreenNameTagging(false)
        UXCam.tagScreenName("UploadScreen Fragment")
        if (is_job_seeker_upload_docs == "job_seeker_upload_docs") {
            binding.placeholderImg.visibility = View.VISIBLE
            binding.cameraIcon.visibility = View.GONE
            setupPlaceholderImg("front")
        }
    }

    private fun setupPlaceholderImg(side: String) {
        if (is_job_seeker_upload_docs == "job_seeker_upload_docs") {
            if (side == "front") {
                when (idType) {
                    "AADHAAR_CARD" -> {
                        if (viewModel.frontAdhaarCard.isNotEmpty()) {
                            binding.placeholderImg.visibility = View.GONE
                        } else {
                            binding.placeholderImg.visibility = View.VISIBLE
                            binding.placeholderImg.setImageResource(R.drawable.ic_front_aadhar)
                        }
                    }
                    "PAN_CARD" -> {
                        if (viewModel.imagePath.isNotEmpty()) {
                            binding.placeholderImg.visibility = View.GONE
                        } else {
                            binding.placeholderImg.visibility = View.VISIBLE
                            binding.placeholderImg.setImageResource(R.drawable.ic_pandcard_3x)
                        }
                    }
//            "PROFILE_PIC" -> {
//                binding.placeholderImg.setImageResource(R.drawable.ic_front_aadhar)
//            }
                    "VEHICLE_RC" -> {
                        if (viewModel.frontRC.isNotEmpty()) {
                            binding.placeholderImg.visibility = View.GONE
                        } else {
                            binding.placeholderImg.visibility = View.VISIBLE
                            binding.placeholderImg.setImageResource(R.drawable.ic_rc_front)
                        }
                    }
                    else -> {
                        if (viewModel.frontDLCard.isNotEmpty()) {
                            binding.placeholderImg.visibility = View.GONE
                        } else {
                            binding.placeholderImg.visibility = View.VISIBLE
                            binding.placeholderImg.setImageResource(R.drawable.ic_dl_4x)
                        }
                    }
                }
            } else {
                when (idType) {
                    "AADHAAR_CARD" -> {
                        if (viewModel.backAdhaarCard.isNotEmpty()) {
                            binding.placeholderImg.visibility = View.GONE
                        } else {
                            binding.placeholderImg.visibility = View.VISIBLE
                            binding.placeholderImg.setImageResource(R.drawable.ic_aadhar_back)
                        }
                    }
//                "PAN_CARD" -> {
//                    binding.placeholderImg.setImageResource(R.drawable.ic_pandcard_3x)
//                }
//            "PROFILE_PIC" -> {
//                binding.placeholderImg.setImageResource(R.drawable.ic_front_aadhar)
//            }
                    "VEHICLE_RC" -> {
                        if (viewModel.backRC.isNotEmpty()) {
                            binding.placeholderImg.visibility = View.GONE
                        } else {
                            binding.placeholderImg.visibility = View.VISIBLE
                            binding.placeholderImg.setImageResource(R.drawable.ic_rc_back)
                        }
                    }
                    else -> {
                        if (viewModel.backDLCard.isNotEmpty()) {
                            binding.placeholderImg.visibility = View.GONE
                        } else {
                            binding.placeholderImg.visibility = View.VISIBLE
                            binding.placeholderImg.setImageResource(R.drawable.driving_back)
                        }
                    }
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun initViews() {
        when (idType) {
            "AADHAAR_CARD" -> {
                binding.aadhaarLayout.visibility = View.VISIBLE
                binding.txtUploadTitle.text = context?.getString(R.string.upload_aadhar_card)
                binding.txtUploadDes.text =
                    context?.getString(R.string.please_upload_uour_aadhar_card)
            }
            "PAN_CARD" -> {
                binding.aadhaarLayout.visibility = View.GONE
                binding.txtUploadTitle.text = context?.getString(R.string.upload_pan_card)
                binding.txtUploadDes.text =
                    context?.getString(R.string.please_upload_your_pan_card_photo_if_you_need_help_uploading_the_same)
            }
            "PROFILE_PIC" -> {
                binding.aadhaarLayout.visibility = View.GONE
                binding.txtUploadTitle.text =
                    context?.getString(R.string.profile_pic_page_header_txt)
                binding.txtUploadDes.text =
                    context?.getString(R.string.profile_pic_page_header_txt)
            }
            "VEHICLE_RC" -> {
                binding.aadhaarLayout.visibility = View.VISIBLE
                binding.txtUploadTitle.text = context?.getString(R.string.upload_rc)
                binding.txtUploadDes.text =
                    context?.getString(R.string.please_upload_uour_rc)
            }
            else -> {
                binding.aadhaarLayout.visibility = View.VISIBLE
                binding.txtUploadTitle.text = context?.getString(R.string.upload_driving_license)
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
        } else if (idType == "PROFILE_PIC") {
            if (viewModel.profilePicImagePath.isNotEmpty()) {
                var path = viewModel.profilePicImagePath
                showSaveButton()
            } else {
                hideSaveButton()
            }
        } else {
            if (viewModel.imagePath.isNotEmpty()) {
                var path = viewModel.imagePath
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
        binding.backButtonUpload.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.takePicture.setOnClickListener {
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

                        carmeraLauncher.launch(tempImageUri)

//                        val intenImatToSec = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                        startActivityForResult(intenImatToSec, REQUEST_CODE_Image)
                        //  takePictureViaCamera.launch(uri)
                    } else {
                        takePictureFromGalary.launch("image/*")
                    }
                }.show()
            } else {
                givePermission()
            }

        }

        binding.deleteImage.setOnClickListener {
            UploadDeleteDocDialog(requireContext()) {
                deleteImage()
            }.show()
        }
        binding.changeBack.setOnClickListener {
            // NEED TO CHECK LOGIC
            changeAdhaarCard(false)
            setupPlaceholderImg("back")
            isFrontAdhaarCard = false
            isFrontDrivingLicense = false
            isFrontRc = false
            if (viewModel.backAdhaarCard.isNotEmpty()) {
                binding.afterPhotoClick.visibility = View.VISIBLE
                binding.beforePhotoClick.visibility = View.GONE
                Glide.with(requireContext()).load(viewModel.backAdhaarCard)
                    .into(binding.docAttachImage)
            } else if (viewModel.backDLCard.isNotEmpty()) {
                binding.afterPhotoClick.visibility = View.VISIBLE
                binding.beforePhotoClick.visibility = View.GONE
                Glide.with(requireContext()).load(viewModel.backDLCard).into(binding.docAttachImage)
            } else if (viewModel.backRC.isNotEmpty()) {
                binding.afterPhotoClick.visibility = View.VISIBLE
                binding.beforePhotoClick.visibility = View.GONE
                Glide.with(requireContext()).load(viewModel.backRC).into(binding.docAttachImage)
            } else {
                binding.beforePhotoClick.visibility = View.VISIBLE
                binding.afterPhotoClick.visibility = View.GONE

            }
        }
        binding.changeFront.setOnClickListener {
            changeAdhaarCard(true)
            setupPlaceholderImg("front")
            isFrontAdhaarCard = true
            isFrontDrivingLicense = true
            isFrontRc = true
            if (viewModel.frontAdhaarCard.isNotEmpty()) {
                binding.afterPhotoClick.visibility = View.VISIBLE
                binding.beforePhotoClick.visibility = View.GONE
                Glide.with(requireContext())
                    .load(viewModel.frontAdhaarCard)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.docAttachImage)
            } else if (viewModel.frontDLCard.isNotEmpty()) {
                binding.afterPhotoClick.visibility = View.VISIBLE
                binding.beforePhotoClick.visibility = View.GONE
                Log.d("UPLOAD_SCREEN_DOCUMENT", "clickListners: ${viewModel.frontDLCard}")
                Glide.with(requireContext())
                    .load(viewModel.frontDLCard)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.docAttachImage)
            } else if (viewModel.frontRC.isNotEmpty()) {
                binding.afterPhotoClick.visibility = View.VISIBLE
                binding.beforePhotoClick.visibility = View.GONE
                Log.d("UPLOAD_SCREEN_DOCUMENT", "clickListners: ${viewModel.frontDLCard}")
                Glide.with(requireContext())
                    .load(viewModel.frontRC)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.docAttachImage)
            } else {
                binding.beforePhotoClick.visibility = View.VISIBLE
                binding.afterPhotoClick.visibility = View.GONE
            }
        }
        binding.saveButton.setOnClickListener {
            if (is_job_seeker_upload_docs == "job_seeker_upload_docs") {
                findNavController().navigate(R.id.nav_fragment_jobsmarketplace_upload)
            } else
                requireActivity().onBackPressed()
        }
    }


    fun setImage(url: String) {
        binding.afterPhotoClick.visibility = View.VISIBLE
        binding.beforePhotoClick.visibility = View.GONE
        binding.placeholderImg.visibility = View.GONE
        Glide.with(requireContext()).load(url).dontAnimate().into(binding.docAttachImage)
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


    private fun changeAdhaarCard(type: Boolean) {
        if (type) {
            binding.changeFront.setTextColor(resources.getColor(R.color.default_200, null))
            binding.changeBack.setTextColor(resources.getColor(R.color.black, null))
        } else {
            binding.changeFront.setTextColor(resources.getColor(R.color.black, null))
            binding.changeBack.setTextColor(resources.getColor(R.color.default_200, null))
        }

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