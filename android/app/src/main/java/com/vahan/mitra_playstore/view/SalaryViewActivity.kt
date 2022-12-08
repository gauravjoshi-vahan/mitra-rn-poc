package com.vahan.mitra_playstore.view

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.ActivitySalleryViewBinding
import com.vahan.mitra_playstore.models.ChromeModel
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils


class SalaryViewActivity : BaseActivity() {
    private var binding: ActivitySalleryViewBinding? = null
    private var webModelChrome: ChromeModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sallery_view)
        initView()
        openWebView(intent.getStringExtra(Constants.LINK))
    }

    private fun initView() {
        //
        binding?.tvBackButton?.text = intent.getStringExtra(Constants._TYPE)
        binding?.ivBackButton?.setOnClickListener { view -> finish() }
        getDataFromRemoteConfig()
    }

    private fun getDataFromRemoteConfig() {
        webModelChrome = Gson().fromJson(
            PrefrenceUtils.retriveData(
                this,
                Constants.CHROME_URL_REMOTE_CONFIG
            ), ChromeModel::class.java
        )
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun openWebView(url: String?) {
        binding?.pgBar?.visibility = View.VISIBLE
        val webSettings = binding?.webView?.settings!!
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.domStorageEnabled = true;
        binding?.webView?.isVerticalScrollBarEnabled = true
        binding?.webView?.isHorizontalScrollBarEnabled = true;
        webSettings.pluginState = WebSettings.PluginState.ON
        binding?.webView?.visibility = View.VISIBLE
        binding?.webView?.setDownloadListener { _, s1, s2, s3, l ->
            Log.d(
                "WebView",
                "onDownloadStart"
            )
        }
        val onlyonce = arrayOf(true)
        val finalUrl = url
        binding?.webView?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                for (i in 0 until webModelChrome?.urls?.size!!) {
                    if (webModelChrome?.urls!![i].chromeUrl.equals(url)) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.setPackage("com.android.chrome")
                        try {
                            startActivity(intent)
                        } catch (ex: ActivityNotFoundException) {
                            // Chrome browser presumably not installed and open Kindle Browser
                            intent.setPackage("com.amazon.cloud9")
                            startActivity(intent)
                        }
                        return true
                    }
                }
                // reject anything other
                return false
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                Log.d("WebView", "onReceivedError")
                binding!!.pgBar.visibility = View.GONE
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
                        binding!!.webView.loadUrl(finalUrl)
                    }
                }
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                //                loadingdialog.stateLoading();
                Log.d("WebView", "onPagestart")
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (view.contentHeight == 0) {
                    view.reload()
                } else {
                    binding!!.pgBar.setVisibility(View.GONE)
                }
                Log.d("WebView", "onPageFinished" + view.contentHeight)
            }
        }
        if (url != null) {
            binding?.webView?.loadUrl(url)
        }
    }
}