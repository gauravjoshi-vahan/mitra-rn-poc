package com.vahan.mitra_playstore.view

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.ActivityNotificationViewBinding
import com.vahan.mitra_playstore.model.PrayerWebModel
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.MainActivity


class NotificationViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationViewBinding
    private var fa: FirebaseAnalytics? = null
    private var  webModel : PrayerWebModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_view)
        initView()
    }

    private fun initView() {
        getDataFromRemoteConfig()
        if (intent != null) {
            openWebView(intent.getStringExtra("link"))
        }
        binding.tvBackButton.text = intent.getStringExtra("Type")
        binding.ivBackButton.setOnClickListener { finish() }
        fa = FirebaseAnalytics.getInstance(this)
        setWebViewWithInstrumentation()
    }

    private fun getDataFromRemoteConfig() {
        webModel = Gson().fromJson(
            PrefrenceUtils.retriveData(this,
            Constants.WEBVIEW_HANDLER_REMOTE_CONFIG_MODIFY)
            , PrayerWebModel::class.java)
    }

    private fun setWebViewWithInstrumentation() {
        val bundle = Bundle()
        bundle.putString(Constants.REDIRECTION_URL, intent.getStringExtra(Constants.LINK))
        bundle.putString(Constants.TYPE, intent.getStringExtra(Constants._TYPE))
        fa!!.logEvent(Constants.WEBVIEW_OPENED, bundle)
        val properties = Properties()
        properties.addAttribute(Constants.REDIRECTION_URL, intent.getStringExtra(Constants.LINK))
        properties.addAttribute(Constants.TYPE, intent.getStringExtra(Constants._TYPE))
        MoEHelper.getInstance(this).trackEvent(Constants.WEBVIEW_OPENED, properties)
        BlitzLlamaSDK.getSdkManager(this).triggerEvent(Constants.WEBVIEW_OPENED)
    }


    private fun openWebView(url: String?) {
        binding.pgBar.visibility = View.VISIBLE
        val webSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.setDomStorageEnabled(true);
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
        var onlyonce = arrayOf(true)
        val finalUrl = url
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return   if (url == webModel?.webviewHandler?.get(0)?.url) {
                    startActivity(Intent(this@NotificationViewActivity, MainActivity::class.java))
                    return true
                } else if (url == webModel?.webviewHandler?.get(1)?.url){
                    startActivity(Intent(this@NotificationViewActivity, MainActivity::class.java))
                    return true
                }
                else if (url.contains("https://vahanmitra")) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    view.context.startActivity(intent)
                    true
                } else if (url.contains("https://firebasestorage")) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    view.context.startActivity(intent)
                    true
                } else if (url.startsWith("http://") || url.startsWith("https://"))
                    false
                else try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    view.context.startActivity(intent)
                    true
                } catch (e: Exception) {
                    Log.i("", "shouldOverrideUrlLoading Exception:$e")
                    true
                }

            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                Log.d("WebView", "onReceivedError")
                binding.pgBar.visibility = View.GONE
            }

            override fun onReceivedHttpError(
                view: WebView,
                request: WebResourceRequest,
                errorResponse: WebResourceResponse
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
        if (url != null) {
            binding.webView.loadUrl(url)
        }
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
       if(PrefrenceUtils.retriveData(this@NotificationViewActivity,Constants.API_TOKEN).equals("")){
           finishAffinity()
       }else{
           startActivity(Intent(this, MainActivity::class.java))
       }
    }
}