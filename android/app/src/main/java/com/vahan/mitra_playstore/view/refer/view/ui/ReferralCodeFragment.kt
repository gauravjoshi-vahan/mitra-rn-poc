package com.vahan.mitra_playstore.view.refer.view.ui


import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import com.moengage.core.Properties
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentReferralCodeBinding
import com.vahan.mitra_playstore.utils.ApiState
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.utils.captureAllEvents
import com.vahan.mitra_playstore.view.refer.models.ReferralCodeReqModel
import com.vahan.mitra_playstore.view.refer.models.ReferralCodeRespDTO
import com.vahan.mitra_playstore.view.refer.viewmodel.ReferralStatusViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ReferralCodeFragment : DialogFragment() {
    /**
     * ReferralCodeFragment
     * Created by Prakhar
     * Date : 17-Nov-2022
     * This fragment is used for passing a referral code to Share Option like whatsapp and facebook
     */
    private lateinit var binding: FragmentReferralCodeBinding
    private lateinit var viewReferralStatusModel: ReferralStatusViewModel
    private lateinit var dialogLoader: Dialog
    private lateinit var referralCodeResp: ReferralCodeRespDTO

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_referral_code, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setWindowParams()
    }

    private fun setWindowParams() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    // this method is used for initialized the view
    private fun initView() {
        initViewModel()
        getReferralCode()
        clickListeners()

    }

    // initialized the viewModel
    private fun initViewModel() {
        viewReferralStatusModel = ViewModelProvider(this)[ReferralStatusViewModel::class.java]
    }

    private fun clickListeners() {
        binding.apply {
            // click this icon we can copy the referral code and pass DynamicLink for creating the dynamic
            rlCopy.setOnClickListener {
                if (referralCodeResp.code != null)
                    createDynamicLinkManually(referralCodeResp.code)
                else
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
            }
            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    // Get the Referral code from the API
    private fun getReferralCode() {
        val referralCode = ReferralCodeReqModel(
            PrefrenceUtils.retriveData(requireContext(), Constants.PHONENUMBER),
            listOf()
        )
        val dialog = createProgressDialog()
        lifecycleScope.launchWhenStarted {
            viewReferralStatusModel.getReferralCode(referralCode).collect {
                when (it) {
                    ApiState.Loading -> {
                        dialog.show()
                    }
                    is ApiState.Success -> {
                        dialog.dismiss()
                        setData(it.data)
                    }
                    is ApiState.Failure -> {
                        dialog.dismiss()
                        Toast.makeText(requireContext(), "" + it.msg, Toast.LENGTH_SHORT).show()
                        Navigation.findNavController(binding.root).navigate(R.id.nav_error_fragment)
                    }
                }
            }
        }
    }

    // Setting Data
    private fun setData(data: ReferralCodeRespDTO) {
        referralCodeResp = data
        setInstrumentation(referralCodeResp.code)
        Log.d("DATA", "setData: ${referralCodeResp.code}")
        binding.tvReferralCode.text = referralCodeResp.code
    }

    private fun setInstrumentation(code: String?) {
        val properties = Properties()
        val attribute = HashMap<String, Any>()
        properties.addAttribute("code", code)
        attribute["code"] = code!!
        captureAllEvents(requireContext(), "referral_type", attribute, properties)
    }


    private fun createDynamicLinkManually(code: String?) {
        var dynamicLinks = ""
        val invitationLink =
            Constants.DEEPLINK + code
        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link =
                Uri.parse(invitationLink)
            domainUriPrefix = Constants.DOMAIN_URL_PREFIX
            // Open links with this app on Android
            androidParameters {
                minimumVersion = 1
            }
        }

        val dynamicLinkUri = dynamicLink.uri
        dynamicLinks = dynamicLinkUri.toString()
        shorterDynamicLink(invitationLink)


    }

    // This method is used for shortening the dynamic Link
    private fun shorterDynamicLink(dynamicLink: String) {
        val shortLinkTask = Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse(dynamicLink)
            domainUriPrefix = Constants.DOMAIN_URL_PREFIX
            androidParameters {
                minimumVersion = 1
            }
        }.addOnSuccessListener { result ->
            val shortLink = result.shortLink
            shareUsingChooser(shortLink!!)
        }.addOnFailureListener {
            Log.d("log_tag", "==> ${it.localizedMessage}", it)

        }

    }

    // Share Using Native Intent
    private fun shareUsingChooser(shortLink: Uri) {
        val decodedByte = BitmapFactory.decodeResource(resources, R.drawable.referral_banner)
        val imageToShare: Uri = Uri.parse(
            MediaStore.Images.Media.insertImage(
                requireContext().contentResolver,
                decodedByte,
                "Share app",
                null
            )
        )
        val share = Intent(Intent.ACTION_SEND)
        share.type = "image/*"
        share.putExtra(Intent.EXTRA_TEXT, shortLink.toString())
        share.putExtra(Intent.EXTRA_STREAM, imageToShare)
        startActivity(Intent.createChooser(share, "Share via"))
    }

    // init dialogue
    private fun createProgressDialog(): Dialog {
        dialogLoader = Dialog(requireContext())
        dialogLoader.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogLoader.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogLoader.setCancelable(false)
        dialogLoader.setContentView(R.layout.layout_loader)
        val imageViewAnimation: ImageView =
            dialogLoader.findViewById(R.id.animate_icon)
        val rotate = RotateAnimation(
            0f, 180f, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 2000
        rotate.interpolator = LinearInterpolator()
        imageViewAnimation.startAnimation(rotate)
        return dialogLoader
    }


}