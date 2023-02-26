package com.vahan.mitra_playstore.view.profilepic.ui.fragments

import android.app.Activity.RESULT_OK
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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.JsonObject
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentGenericImageUploadModeBinding
import com.vahan.mitra_playstore.network.SharedViewModel
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.utils.UriUtils
import com.vahan.mitra_playstore.view.HomeActivity
import com.vahan.mitra_playstore.view.bottomsheet.UploadDocBottomSheet
import com.vahan.mitra_playstore.view.documentupload.viewmodel.ImageUploadModel
import com.yalantis.ucrop.UCrop
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class GenericImageUploadMode : Fragment() {

    lateinit var binding: FragmentGenericImageUploadModeBinding
    lateinit var viewModel: ImageUploadModel
    private var receivedUriKey: Uri? = null
    private var tempImageUri: Uri? = null
    private var tempImagePath = ""
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var globalCheck: String? = null
    private var uploadType: String? = null

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                viewModel.imagePath = tempImagePath
                viewModel.createBitMap(tempImageUri!!)
//                Log.d("IMGURI", tempImageUri.toString())
                showCropper(tempImageUri!!)
            }
        }

    private var takePictureFromGalary =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let {
                val path = UriUtils.getPath(requireContext(), it)
                viewModel.imagePath = path
                viewModel.createBitMap(it)
                Log.d("IMGURI", tempImageUri.toString())
                showCropper(it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        FragmentGenericImageUploadModeBinding.inflate(inflater, container, false).run {
            binding = this
            return root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.apply {
            globalCheck = getString("nav")
            uploadType = getString("type")
            PrefrenceUtils.insertData(requireContext(), "nav", globalCheck)
        }
        viewModel = ViewModelProvider(requireActivity())[ImageUploadModel::class.java]
        showBottomDialog()
        if (uploadType == Constants.PROFILE_PIC) setProfilePicStrings()
        else setBankDocUploadStrings()
        clickListeners()
    }

    private fun setProfilePicStrings() {
        binding.apply {
            pageHeaderTxt.text = context?.getString(R.string.profile_pic_page_header_txt)
            beforeUploadContentTxt.text = context?.getString(R.string.profile_pic_header_txt)
            beforeUploadTipContentTxt.text =
                context?.getString(R.string.profile_pic_tip_content_txt)
        }
    }

    private fun setBankDocUploadStrings() {
        binding.apply {
            pageHeaderTxt.text = context?.getString(R.string.bank_doc_upload_page_header_txt)
            beforeUploadContentTxt.text = context?.getString(R.string.bank_doc_upload_header_txt)
            beforeUploadTipContentTxt.text =
                context?.getString(R.string.bank_doc_upload_tip_content_txt)
            beforeUploadIcon.background
            beforeUploadIcon.setImageResource(R.drawable.doc_upload_icon);
        }
    }

    private fun showCropper(fileUri: Uri) {
        val destinationURI = StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString()
        val options = UCrop.Options()
        if (uploadType == Constants.PROFILE_PIC) options.setCircleDimmedLayer(true)
        else options.setCircleDimmedLayer(false)
        options.setMaxScaleMultiplier(5F);
        options.setShowCropFrame(false);
        options.setCropGridStrokeWidth(20);
        options.setCropGridColumnCount(2);
        options.setCropGridRowCount(1);

        UCrop.of(fileUri, Uri.fromFile(File(context?.cacheDir, destinationURI)))
            .withOptions(options).withAspectRatio(0F, 0F).useSourceImageAspectRatio()
            .withMaxResultSize(2000, 2000).start(requireContext(), this);

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
                } else {
                    Toast.makeText(activity, "Please select a valid image", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val finalURI: Uri? = UCrop.getOutput(data!!)
            binding.beforeUpload.visibility = View.GONE
            binding.afterUpload.visibility = View.VISIBLE
            binding.uploadedImage.setImageURI(finalURI)
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(activity, "Couldn't crop the image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clickListeners() {
        binding.optionGallery.setOnClickListener {
            showBottomDialog();
        }
        binding.retakeBtn.setOnClickListener {
            showBottomDialog();
        }

        binding.uploadBtn.setOnClickListener {
            imageUploadAPI()
        }

        binding.backBtn.setOnClickListener {
            if (globalCheck == "Navigation") {
                showStatusPage()
            }
        }
    }

    private fun imageUploadAPI() {
        var imageUrl: String

        val imageType: String = if (uploadType == Constants.PROFILE_PIC) {
            "passportPhoto"
        } else {
            "Bank Passbook or Cancelled Cheque Image"
        }
        val viewSharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        toggleUploadLoadingBtnDisplay(true)
        lifecycleScope.launchWhenStarted {
            viewSharedViewModel.newUploadImageFile(
                requireContext(),
                Constants.TOKENCONSTANT + PrefrenceUtils.retriveData(
                    requireContext(),
                    Constants.API_TOKEN
                ),
                viewModel.bitmapDataImage!!,
                imageType
            )?.observe(requireActivity()) { data ->
                if (data.fileURL?.isNotEmpty() == true) {
                    imageUrl = data.fileURL.toString()
                    if (imageUrl.isNotEmpty()) {
                        verifyImageAPI(imageType, viewSharedViewModel, imageUrl)
                    }
                } else {
                    Toast.makeText(
                        requireActivity(),
                        R.string.validation_failed_please_try_again_later,
                        Toast.LENGTH_SHORT
                    ).show()
                    toggleUploadLoadingBtnDisplay(false)
                }

            }
        }
    }

    private fun toggleUploadLoadingBtnDisplay(inp: Boolean = false) {
        if (inp) {
            binding.uploadBtn.visibility = View.GONE
            binding.uploadingBtn.visibility = View.VISIBLE
            binding.retakeBtn.visibility = View.GONE
            binding.showLoader.visibility = View.VISIBLE
        } else {
            binding.uploadBtn.visibility = View.VISIBLE
            binding.uploadingBtn.visibility = View.GONE
            binding.retakeBtn.visibility = View.VISIBLE
            binding.showLoader.visibility = View.GONE
        }
    }

    private fun verifyImageAPI(
        idType: String,
        viewSharedViewModel: SharedViewModel,
        data: String,
    ) {
        val verifyModel = JsonObject()
        verifyModel.addProperty(Constants.IMAGE_URL, data)
        verifyModel.addProperty(Constants.TYPE, idType)
        verifyModel.addProperty(Constants.SOURCE, "mitra")

        viewSharedViewModel.verifyAadharCard(requireContext(), verifyModel)
            .observe(requireActivity()) {
                if (idType != "") {
                    if (it.isValid !== null && it.isValid) {
                        if (idType == "passportPhoto") {
                            PrefrenceUtils.insertDataInBoolean(
                                activity, Constants.IS_VERIFICATION_STATUS_PROFILE, true
                            )
                            Toast.makeText(
                                requireContext(),
                                context?.getString(R.string.profile_img_upload_success),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                context?.getString(R.string.img_upload_success),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        toggleUploadLoadingBtnDisplay(false)
                        showStatusPage()
                    } else {
                        toggleUploadLoadingBtnDisplay(false)
                        Toast.makeText(
                            requireContext(),
                            context?.getString(R.string.validation_failed_please_try_again_later),
                            Toast.LENGTH_SHORT
                        ).show()

                        if (idType == "passportPhoto") {
                            PrefrenceUtils.insertDataInBoolean(
                                activity, Constants.IS_VERIFICATION_STATUS_PROFILE, false
                            )

                            PrefrenceUtils.insertData(
                                activity, Constants.PROFILEPHOTO, ""
                            )
                        }
                    }
                } else {
                    toggleUploadLoadingBtnDisplay(false)
                    Toast.makeText(
                        requireContext(),
                        context?.getString(R.string.invalid_image_type),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun showStatusPage() {
        requireContext().startActivity(
            Intent(
                requireContext(), HomeActivity::class.java
            ).putExtra("link", Constants.REDIRECTION_URL)
        )
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
                    val file = viewModel.createNewFile(
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
                    takePictureFromGalary.launch("image/*")
                }
            }.show()
        } else {
            givePermission()
        }
    }

    private fun createImageFile(): File {
        val imageDir = requireContext().getExternalFilesDir(
            Environment.DIRECTORY_PICTURES
        )
        return File.createTempFile("img_", ".jpg", imageDir)
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
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            ), 202
        )
    }
}