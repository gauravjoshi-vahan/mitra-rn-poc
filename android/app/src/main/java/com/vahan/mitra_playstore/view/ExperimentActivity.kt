package com.vahan.mitra_playstore.view


import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.freshchat.consumer.sdk.Freshchat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.ActivityNotificationViewBinding
import com.vahan.mitra_playstore.model.PrayerWebModel
import com.vahan.mitra_playstore.models.ChromeModel
import com.vahan.mitra_playstore.models.WebViewModifyModel
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import java.io.UnsupportedEncodingException
import java.net.URLEncoder


class ExperimentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationViewBinding
    private var fa: FirebaseAnalytics? = null
    private var webModel: PrayerWebModel? = null
    private var webModelModify: WebViewModifyModel? = null
    private var webModelChrome: ChromeModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_view)
        initView()
    }

    private fun initView() {
        getDataFromRemoteConfig()
        UXCam.setAutomaticScreenNameTagging(true)
        try {
            if (intent != null) {
                if (intent.getStringExtra(Constants.LINK)?.contains("pdf")!!) {
                    try {
                        val path = "http://docs.google.com/gview?embedded=true&url=${
                            URLEncoder.encode(
                                intent.getStringExtra(Constants.LINK), "utf-8"
                            )
                        }"
                        openwebUrlModify(path)
                    } catch (e: UnsupportedEncodingException) {
                        e.printStackTrace()
                    }
                } else {
                    openwebUrlModify(intent.getStringExtra(Constants.LINK))
                }
            }
        } catch (e: Exception) {

        }

        binding.tvBackButton.text = intent.getStringExtra(Constants._TYPE)
        binding.ivBackButton.setOnClickListener { finish() }
        fa = FirebaseAnalytics.getInstance(this)
        setWebViewWithInstrumentation()
    }

    private fun getDataFromRemoteConfig() {
        webModelModify = Gson().fromJson(
            PrefrenceUtils.retriveData(
                this,
                Constants.WEBVIEW_HANDLER_REMOTE_CONFIG_MODIFY
            ), WebViewModifyModel::class.java
        )


        webModelChrome = Gson().fromJson(
            PrefrenceUtils.retriveData(
                this,
                Constants.CHROME_URL_REMOTE_CONFIG
            ), ChromeModel::class.java
        )

    }

    private fun setWebViewWithInstrumentation() {
        val bundle = Bundle()
        bundle.putString(Constants.REDIRECTION_URL, intent.getStringExtra(Constants.LINK))
        bundle.putString(Constants.TYPE, intent.getStringExtra(Constants._TYPE))
        val data = HashMap<String, String>()
        /* data["url"] = intent.getStringExtra("link")!!
         data["type"] = intent.getStringExtra("Type")!!*/
        fa!!.logEvent(Constants.WEBVIEW_OPENED, bundle)
        val properties = Properties()
        properties.addAttribute(Constants.REDIRECTION_URL, intent.getStringExtra(Constants.LINK))
        properties.addAttribute(Constants.TYPE, intent.getStringExtra(Constants._TYPE))
        MoEHelper.getInstance(this).trackEvent(Constants.WEBVIEW_OPENED, properties)
        UXCam.logEvent(Constants.WEBVIEW_OPENED, data)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun openwebUrlModify(modify_url: String?) {
        binding.pgBar.visibility = View.VISIBLE
        val webSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.domStorageEnabled = true;
        binding.webView.isVerticalScrollBarEnabled = true
        binding.webView.isHorizontalScrollBarEnabled = true;
        webSettings.pluginState = WebSettings.PluginState.ON
        binding.webView.visibility = View.VISIBLE
        binding.webView.setDownloadListener { _, s1, s2, s3, l ->
            Log.d(
                "WebView",
                "onDownloadStart"
            )
        }
        val onlyonce = arrayOf(true)
        val finalUrl = modify_url
        Log.d("modify_url", modify_url.toString())

        binding.webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                try {
                    for (i in 0 until webModelChrome?.urls?.size!!) {
                        Log.d("tag_url", url)
                        when {
                            webModelChrome?.urls!![i].chromeUrl.equals(url) -> {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.setPackage("com.android.chrome")
                                return try {
                                    startActivity(intent)
                                    Log.d("tag_url", url)
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
                        this@ExperimentActivity,
                        getString(R.string.please_try_after_sometime),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                for (i in 0 until webModelModify?.webviewHandler?.size!!) {
                    if (webModelModify?.webviewHandler?.get(i)?.url.equals(url)) {
                        startActivity(
                            Intent(
                                this@ExperimentActivity,
                                MainActivity::class.java
                            ).putExtra(
                                Constants.DESIGNATION,
                                webModelModify?.webviewHandler?.get(i)?.destination
                            )
                        )
                        return true
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
                binding.pgBar.visibility = View.GONE
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
                        binding.webView.loadUrl(finalUrl)
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
                    binding.pgBar.visibility = View.GONE
                }
                Log.d("WebView", "onPageFinished" + view.contentHeight)
            }
        }
        if (modify_url != null) {
            binding.webView.loadUrl(modify_url)
        }
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
                    if (binding.webView.canGoBack()) {
                        binding.webView.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        if (PrefrenceUtils.retriveData(this, Constants.API_TOKEN)
                .equals("")
        ) {
            finishAffinity()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
