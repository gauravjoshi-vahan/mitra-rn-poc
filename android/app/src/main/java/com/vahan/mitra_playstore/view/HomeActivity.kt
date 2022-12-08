package com.vahan.mitra_playstore.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.webkit.*
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.freshchat.consumer.sdk.Freshchat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.ActivityHomeBinding
import com.vahan.mitra_playstore.models.ChromeModel
import com.vahan.mitra_playstore.models.WebViewModifyModel
import com.vahan.mitra_playstore.network.SharedViewModel
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.activities.enternumberactivity.view.ui.EnterNumberActivity
import kotlinx.coroutines.launch


class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var fa: FirebaseAnalytics? = null
    private lateinit var viewModel: SharedViewModel
    private var webModelModify: WebViewModifyModel? = null
    private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
    private var urlhanlder: String? = null
    private var webModelChrome: ChromeModel? = null
    override fun onCreate(
        savedInstanceState: Bundle?,
    ) {
        super.onCreate(savedInstanceState)
        fa = FirebaseAnalytics.getInstance(this)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        getDynamicLink()
        getUserData()
    }

    private fun getUserData() {
        val viewSharedViewModel: SharedViewModel =
            ViewModelProvider(this)[SharedViewModel::class.java]
        if (PrefrenceUtils.retriveData(this, Constants.USERTYPE).equals(Constants.NEW_LEAD)) {
            viewSharedViewModel.userData.observe(this) { userResponse ->
                try {
                    if (userResponse.success == true) {
                        PrefrenceUtils.insertData(this, Constants.USERTYPE, userResponse.userType)
                        PrefrenceUtils.insertData(
                            this,
                            Constants.REDIRECTION_URL,
                            userResponse.redirectURL
                        )
                        when {
                            userResponse.userType.equals(Constants.NON_PAYROLL) -> {
                                //show popup
                                //load redirect url after user presses ok in popup
                                // New lead -------> non-payroll user (using redirect url)
                                userResponse.redirectURL?.let { showDialogNonPayroll(it) }
                            }
                            userResponse.userType.equals(Constants.PAYROLL) -> {
                                //show popup
                                //send to mainActivity---(either new lead->payroll or non-payroll->payroll)
                                PrefrenceUtils.insertDataInBoolean(
                                    this,
                                    Constants.CHECKFORPAYROLL,
                                    true
                                )
                                userResponse.redirectURL?.let { showDialogPayroll(it) }
                            }
                        }
                    }
                } catch (e: Exception) {

                }
            }
        } else if (PrefrenceUtils.retriveData(this, Constants.USERTYPE).equals(Constants.NON_PAYROLL)) {
            viewSharedViewModel.userData.observe(this) { userResponse ->
                try {
                    if (userResponse.success == true) {
                        PrefrenceUtils.insertData(this, Constants.USERTYPE, userResponse.userType)
                        PrefrenceUtils.insertData(
                            this,
                            Constants.REDIRECTION_URL,
                            userResponse.redirectURL
                        )
                        when {
                            userResponse.userType.equals(Constants.PAYROLL) -> {
                                //show popup
                                //send to mainActivity---(either new lead->payroll or non-payroll->payroll)
                                PrefrenceUtils.insertDataInBoolean(
                                    this,
                                    Constants.CHECKFORPAYROLL,
                                    true
                                )
                                userResponse.redirectURL?.let {
                                    showDialogPayroll(it)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {

                }
            }
        }
    }

    private fun initView() {
        webModelChrome = Gson().fromJson(
            PrefrenceUtils.retriveData(
                this,
                Constants.CHROME_URL_REMOTE_CONFIG
            ), ChromeModel::class.java
        )
        webModelModify = Gson().fromJson(
            PrefrenceUtils.retriveData(
                this,
                Constants.WEBVIEW_HANDLER_REMOTE_CONFIG_HOME_PAGE
            ), WebViewModifyModel::class.java
        )

        // If User is new_lead_referral ---> Redirection Link open in native App
        if(PrefrenceUtils.retriveData(this@HomeActivity,Constants.REDIRECTION_URL).equals(Constants.NEW_LEAD_REFERRAL)){
            binding.incLayoutOne.containerOne.visibility = View.GONE
            binding.incLayoutTwo.containerOne.visibility = View.VISIBLE
        }else{
            // If User is New LEAD or Non Payroll ---> Redirection Link open in web app
            binding.incLayoutOne.containerOne.visibility = View.VISIBLE
            binding.incLayoutTwo.containerOne.visibility = View.GONE
            openWebView(PrefrenceUtils.retriveData(this, Constants.REDIRECTION_URL))
        }

    }


    private fun  getDynamicLink() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
            .addOnSuccessListener {
                //  var deepLink: Uri? = null
                if (intent.data != null) {
                    //deepLink = pendingDynamicLinkData.link
                    lifecycleScope.launch {
                        if (PrefrenceUtils.retriveData(
                                this@HomeActivity,
                                Constants.API_TOKEN
                            ) == ""
                        ) {
                            startActivity(
                                Intent(this@HomeActivity, EnterNumberActivity::class.java)
                                    .putExtra(Constants.LINK, intent.data.toString())

                            )
                            finish()

                        } else {
                            openWebView(intent.data.toString())
                        }
                    }

                } else {
                    initView()
                }
            }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun openWebView(url: String?) {
        val properties = Properties()
        properties.addAttribute(Constants.REDIRECTION_URL, url)
        properties.addAttribute(Constants.TYPE, PrefrenceUtils.retriveData(this, Constants.USERTYPE))
        MoEHelper.getInstance(this).trackEvent(Constants.WEBVIEW_OPENED, properties)
        binding.incLayoutOne.pgBar.visibility = View.VISIBLE
        val webSettings = binding.incLayoutOne.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.domStorageEnabled = true;
        binding.incLayoutOne.webView.isVerticalScrollBarEnabled = true
        binding.incLayoutOne.webView.isHorizontalScrollBarEnabled = true;
        webSettings.pluginState = WebSettings.PluginState.ON
        binding.incLayoutOne.webView.visibility = View.VISIBLE
        binding.incLayoutOne.webView.setDownloadListener { _, _, _, _, _ ->
            Log.d(
                "WebView",
                "onDownloadStart"
            )
        }
        val onlyonce = arrayOf(true)

        // make this url
        val finalUrl = url
        binding.incLayoutOne.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                try {
                    for (i in 0 until webModelChrome?.urls?.size!!) {
                        when {
                            url.startsWith("tel:") -> {
                                setInstrumentationWebView(Constants.TEL_INTENT)
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse(url)
                                startActivity(intent)
                                return true

                            }
                            url.startsWith("whatsapp:") -> {
                                setInstrumentationWebView(Constants.WHATSAPP_INTENT)
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse(url)
                                startActivity(intent)
                                return true
                            }
                            url.startsWith("mailto:") -> {
                                setInstrumentationWebView(Constants.MAIL_INTENT)
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse(url)
                                startActivity(intent)
                                return true
                            }
                            //Instrumentation need to added
                            webModelChrome?.urls!![i].chromeUrl.equals(url) -> {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.setPackage("com.android.chrome")
                                return try {
                                    startActivity(intent)
                                    true
                                } catch (ex: ActivityNotFoundException) {
                                    // Chrome browser presumably not installed and open Kindle Browser
                                    intent.setPackage("com.amazon.cloud9")
                                    startActivity(intent)
                                    true
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@HomeActivity,
                        getString(R.string.please_try_after_sometime),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                for (i in 0 until webModelModify?.webviewHandler?.size!!) {
                    if (webModelModify?.webviewHandler?.get(i)?.url.equals(url)) {
                        if (webModelModify?.webviewHandler?.get(i)?.destination.equals(Constants.DOWNLOAD)) {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(webModelModify?.webviewHandler?.get(i)!!.downloadLink)
                            )
                            view.context.startActivity(intent)
                            return true
                        } else if (webModelModify?.webviewHandler?.get(i)?.destination.equals(Constants.LOGOUT)) {
                            logoutFunction()
                            return true
                        } else if (webModelModify?.webviewHandler?.get(i)?.destination.equals(Constants.UPLOAD)) {
                            redirectToHome(webModelModify?.webviewHandler?.get(i)?.destination)
                            return true
                        }
                        else if (webModelModify?.webviewHandler?.get(i)?.destination.equals(Constants.VERIFYBANK)) {
                            redirectToHome(webModelModify?.webviewHandler?.get(i)?.destination)
                            return true
                        }
                        else if (webModelModify?.webviewHandler?.get(i)?.destination.equals(Constants.PROFILE_PIC) || webModelModify?.webviewHandler?.get(i)?.destination.equals(Constants.BANK_DOC_UPLOAD)) {
                            redirectToHome(webModelModify?.webviewHandler?.get(i)?.destination)
                            return true
                        }
                    }
                }
                return false
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError,
            ) {
                super.onReceivedError(view, request, error)
                Log.d("WebView", "onReceivedError")
                binding.incLayoutOne.pgBar.visibility = View.GONE
            }

            override fun onReceivedHttpError(
                view: WebView,
                request: WebResourceRequest,
                errorResponse: WebResourceResponse,
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
                if (onlyonce[0]) {
                    onlyonce[0] = false
                    if (finalUrl != null) {
                        binding.incLayoutOne.webView.loadUrl(finalUrl)
                    }
                }
            }

            override fun onPageStarted(view: WebView, url: String, favIcon: Bitmap?) {
                super.onPageStarted(view, url, favIcon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (view.contentHeight == 0) {
                    view.reload()
                } else {
                    binding.incLayoutOne.pgBar.visibility = View.GONE
                }
                Log.d("WebView", "onPageFinished" + view.contentHeight)
            }
        }
        if (url != null) {
            binding.incLayoutOne.webView.loadUrl(url)
        }
    }

    private fun redirectToHome(destination: String?) {
        startActivity(
            Intent(
                this@HomeActivity,
                MainActivity::class.java
            ).putExtra("Navigation", destination)
        )
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
        finishAffinity()
    }

    private fun setInstrumentationWebView(type: String) {
        val bundle = Bundle()
        val properties = Properties()
        val data = HashMap<String, String>()
        val attribute = HashMap<String, Any>()
        Freshchat.trackEvent(this, type, attribute)
        fa?.logEvent(type, bundle)
        MoEHelper.getInstance(this).trackEvent(type, properties)
        UXCam.logEvent(type, data)
        BlitzLlamaSDK.getSdkManager(this).triggerEvent(type)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (binding.incLayoutOne.webView.canGoBack()) {
                        binding.incLayoutOne.webView.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    private fun showDialogPayroll(redirectURL: String) {
        setInstrumentationUserTranscViewed("payroll")
        val dialog = Dialog(this, R.style.CustomAlertDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.payroll_dialogbox)

        val btn_letsGo = dialog.findViewById<Button>(R.id.btn_letsGo)
        val checkbox = dialog.findViewById<CheckBox>(R.id.checkBox)
        btn_letsGo.isEnabled = false
        btn_letsGo.background.alpha = 64;

        checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                btn_letsGo.isEnabled = true
                btn_letsGo.background.alpha = 255;
                btn_letsGo.setOnClickListener {
                    setInstrumentationUserTranscViewedClicked("payroll")
                    val intent = Intent(this@HomeActivity, MainActivity::class.java)
                    startActivity(intent)
                    dialog.dismiss()
                }
            } else {
                btn_letsGo.isEnabled = false
                btn_letsGo.background.alpha = 64;
            }
        }
        dialog.setCancelable(false)
        dialog.show()


    }

    private fun setInstrumentationUserTranscViewedClicked(type: String) {
        val bundle = Bundle()
        val attribute = HashMap<String, Any>()
        val data = HashMap<String, String>()
        val properties = Properties()
        bundle.putString(Constants.TYPE, type)
        attribute[Constants.TYPE] = type
        data[Constants.TYPE] = type
        properties.addAttribute(Constants.TYPE, type)
        fa!!.logEvent(Constants.USER_TRANSITION_TAPPED, bundle)
        MoEHelper.getInstance(this).trackEvent(Constants.USER_TRANSITION_TAPPED, properties)
        Freshchat.trackEvent(this, Constants.USER_TRANSITION_TAPPED, attribute)
        UXCam.logEvent(Constants.USER_TRANSITION_TAPPED, data)
        BlitzLlamaSDK.getSdkManager(this).triggerEvent(Constants.USER_TRANSITION_TAPPED)
    }

    private fun setInstrumentationUserTranscViewed(type: String) {
        val bundle = Bundle()
        val attribute = HashMap<String, Any>()
        val data = HashMap<String, String>()
        val properties = Properties()
        bundle.putString(Constants.TYPE, type)
        attribute[Constants.TYPE] = type
        data[Constants.TYPE] = type
        properties.addAttribute(Constants.TYPE, type)
        fa!!.logEvent(Constants.USER_TRANSITION_VIEWED, bundle)
        MoEHelper.getInstance(this).trackEvent(Constants.USER_TRANSITION_VIEWED, properties)
        Freshchat.trackEvent(this, Constants.USER_TRANSITION_VIEWED, attribute)
        UXCam.logEvent(Constants.USER_TRANSITION_VIEWED, data)
        BlitzLlamaSDK.getSdkManager(this).triggerEvent(Constants.USER_TRANSITION_VIEWED)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun showDialogNonPayroll(redirectURL: String) {
        setInstrumentationUserTranscViewed(Constants.NON_PAYROLL)
        val dialog = Dialog(this, R.style.CustomAlertDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.nonpayroll_dialogbox)

        val btn_letsGo = dialog.findViewById<Button>(R.id.btn_letsGo)
        val checkbox = dialog.findViewById<CheckBox>(R.id.checkBox)
        btn_letsGo.isEnabled = false
        btn_letsGo.background.alpha = 64;

        checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                btn_letsGo.isEnabled = true
                btn_letsGo.background.alpha = 255;
                btn_letsGo.setOnClickListener {
                    setInstrumentationUserTranscViewedClicked(Constants.NON_PAYROLL)
                    openWebView(redirectURL)
                    dialog.dismiss()
                }
            } else {
                btn_letsGo.isEnabled = false
                btn_letsGo.background.alpha = 64;
            }
        }
        dialog.setCancelable(false)
        dialog.show()


    }

    private fun logoutFunction() = AlertDialog.Builder(this)
        .setTitle(this.getString(R.string.alert_logout))
        .setMessage(this.getString(R.string.alert_logout_message))
        .setCancelable(true)
        .setNegativeButton(
            this.getString(R.string.alert_no)
        ) { dialog: DialogInterface, _: Int -> dialog.cancel() }
        .setPositiveButton(
            this.getString(R.string.alert_yes)
        ) { _: DialogInterface?, _: Int ->
            PrefrenceUtils.logoutUser(this)
            startActivity(Intent(this, EnterNumberActivity::class.java))
            finishAffinity()
        }
        .create()
        .show()
}