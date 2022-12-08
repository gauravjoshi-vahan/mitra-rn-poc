package com.vahan.mitra_playstore.view.payslip.view.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentPaySlipViewBinding
import com.vahan.mitra_playstore.utils.Constants
import java.io.UnsupportedEncodingException
import java.net.URLEncoder


class PaySlipViewFragment : Fragment() {

    lateinit var binding: FragmentPaySlipViewBinding
    var downloadID: Long? = null
    var urlPDF: String? = null
    var backButtonHeading: String? = null
    var onlyonce: Boolean? = false
    private lateinit var fb: FirebaseAnalytics
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments.apply {
            urlPDF = this?.getString(Constants.REDIRECTION_URL) ?: ""
            backButtonHeading = this?.getString(Constants.HEADING) ?: ""
        }
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_pay_slip_view, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        UXCam.setAutomaticScreenNameTagging(false)
        UXCam.tagScreenName(Constants.PAYMENT_SLIP_VIEW_FRAGMENT)
        if (backButtonHeading.equals(Constants.SIPLY)) {
            binding.applyLayout.visibility = View.GONE
        } else {
            binding.applyLayout.visibility = View.VISIBLE
        }
        fb = FirebaseAnalytics.getInstance(requireContext())
        binding.tvBackButton.text = backButtonHeading
        activity?.registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
        binding.downloadButton.setOnClickListener {
            if (hasStoragePermission(requireActivity())) {
                if (!urlPDF.isNullOrEmpty())
                    saveInDirectory(urlPDF)
                else
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.url_not_found),
                        Toast.LENGTH_LONG
                    ).show()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    123
                )
            }
        }
        binding.ivBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        try {
            val path = "http://docs.google.com/gview?embedded=true&url=${
                URLEncoder.encode(
                    urlPDF!!, "utf-8"
                )
            }"
            openWebView(path)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun openWebView(modify_url: String?) {
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
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

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


    // To handle "Back" key press event for WebView to go back to previous screen.


    private fun hasStoragePermission(context: Activity): Boolean {
        val write =
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val read =
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
    }

    private fun saveInDirectory(lastWeek_earning_history: String?) {

        setInstrumentation()
        val request =
            DownloadManager.Request(Uri.parse(lastWeek_earning_history?.trim()))
        request.setTitle(lastWeek_earning_history)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        }
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "payslip.pdf"
        )
        val downloadManager =
            activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        request.setMimeType("application/pdf")
        request.allowScanningByMediaScanner()
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        downloadID = downloadManager.enqueue(request)
//        try {
//            new FileDownloader(this).execute(lastWeek_earning_history);
//        } catch (Exception e) {
//
//        }
    }

    private fun setInstrumentation() {
        val bundle = Bundle()
        fb.logEvent(Constants.PAYSLIP_DOWNLOADED, bundle)
        val properties = Properties()
        MoEHelper.getInstance(requireContext()).trackEvent(Constants.PAYSLIP_DOWNLOADED, properties)
        UXCam.logEvent(Constants.PAYSLIP_DOWNLOADED)

    }

    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            //Fetching the download id received with the broadcast
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                Toast.makeText(
                    getContext(),
                    getString(R.string.download_completed),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }


}