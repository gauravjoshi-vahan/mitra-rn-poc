package com.vahan.mitra_playstore.view.notification

import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.freshchat.consumer.sdk.Freshchat
import com.google.firebase.analytics.FirebaseAnalytics
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.moengage.inbox.core.MoEInboxHelper
import com.moengage.inbox.core.listener.OnMessagesAvailableListener
import com.moengage.inbox.core.listener.UnClickedCountListener
import com.moengage.inbox.core.model.InboxMessage
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentNotificationBinding
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.adapter.NotificationListAdapter
import com.vahan.mitra_playstore.BuildConfig


class NotificationFragment : Fragment(), OnMessagesAvailableListener, UnClickedCountListener {


    private var count: Int = 0
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var fb: FirebaseAnalytics
    private var ctDialog: Dialog? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        UXCam.setAutomaticScreenNameTagging(false)
        UXCam.tagScreenName(Constants.NOTIFICATION_FRAGMENT)
        val notificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        fb = FirebaseAnalytics.getInstance(requireContext())
        binding.apply {
            btnBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun checkForUpdate() {
        if (PrefrenceUtils.retriveDataInBoolean(requireContext(), Constants.TEST_USER)) {
            if (PrefrenceUtils.retriveData(requireContext(), Constants.ONLY_TEST_USERS_REMOTE_CONFIG).equals("1", ignoreCase = true)) {
                if (PrefrenceUtils.retriveData(requireContext(), Constants.UPDATE_AVAILABLE_REMOTE_CONFIG).equals("1", ignoreCase = true)) {
                    if (PrefrenceUtils.retriveData(requireContext(), Constants.FORCE_UPDATE_REMOTE_CONFIG).equals("1", ignoreCase = true)) {
                        //Need to write logic with Force Update
                        // Log.d("INFO", "checkForUpdate: " + (BuildConfig.VERSION_NAME).toDouble())
                        if (PrefrenceUtils.retriveData(requireContext(), Constants.UPDATE_AVAILABLE_REMOTE_CONFIG_VERSION).toDouble() > BuildConfig.VERSION_NAME.toDouble()) {
                            update(false)
                        }
                    } else {
                        if (PrefrenceUtils.retriveData(requireContext(), Constants.UPDATE_AVAILABLE_REMOTE_CONFIG_VERSION).toDouble() > BuildConfig.VERSION_NAME.toDouble()) {
                            //update(true);
                        }
                    }
                }
            } else if (PrefrenceUtils.retriveData(requireContext(), Constants.ONLY_TEST_USERS_REMOTE_CONFIG).equals("0", ignoreCase = true)) {
                if (PrefrenceUtils.retriveData(requireContext(), Constants.UPDATE_AVAILABLE_REMOTE_CONFIG).equals("1", ignoreCase = true)) {
                    if (PrefrenceUtils.retriveData(requireContext(), Constants.FORCE_UPDATE_REMOTE_CONFIG).equals("1", ignoreCase = true)) {
                        //Need to write logic with Force Update
                        // Log.d("INFO", "checkForUpdate: " + (BuildConfig.VERSION_NAME).toDouble())
                        if (PrefrenceUtils.retriveData(requireContext(), Constants.UPDATE_AVAILABLE_REMOTE_CONFIG_VERSION).toDouble() > BuildConfig.VERSION_NAME.toDouble()) {
                            update(false)
                        }
                    } else {
                        if (PrefrenceUtils.retriveData(requireContext(), Constants.UPDATE_AVAILABLE_REMOTE_CONFIG_VERSION).toDouble() > BuildConfig.VERSION_NAME.toDouble()) {
                            //update(true);
                        }
                    }
                }
            }
        } else if (PrefrenceUtils.retriveData(requireContext(), Constants.ONLY_TEST_USERS_REMOTE_CONFIG).equals("0", ignoreCase = true)) {
            if (PrefrenceUtils.retriveData(requireContext(), Constants.UPDATE_AVAILABLE_REMOTE_CONFIG).equals("1", ignoreCase = true)) {
                if (PrefrenceUtils.retriveData(requireContext(), Constants.FORCE_UPDATE_REMOTE_CONFIG).equals("1", ignoreCase = true)) {
                    //Need to write logic with Force Update
                    // Log.d("INFO", "checkForUpdate: " + (BuildConfig.VERSION_NAME).toDouble())
                    if (PrefrenceUtils.retriveData(requireContext(), Constants.UPDATE_AVAILABLE_REMOTE_CONFIG_VERSION).toDouble() > BuildConfig.VERSION_NAME.toDouble()) {
                        update(false)
                    }
                } else {
                    if (PrefrenceUtils.retriveData(requireContext(), Constants.UPDATE_AVAILABLE_REMOTE_CONFIG_VERSION).toDouble() > BuildConfig.VERSION_NAME.toDouble()) {
                        //update(true);
                    }
                }
            }
        }
    }

    private fun update(cancellable: Boolean) {
        setInstrumentationForUpdate()
        ctDialog = Dialog(requireContext())
        ctDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        ctDialog?.setContentView(R.layout.force_update_layout)
        ctDialog?.findViewById<View>(R.id.btn_download)?.setOnClickListener {
            setInstrumentationForUpdateClick()
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PrefrenceUtils.retriveData(requireContext(), Constants.APK_URL_REMOTE_CONFIG))))
        }
        ctDialog?.setCanceledOnTouchOutside(cancellable)
        ctDialog?.setCancelable(cancellable)
        ctDialog?.show()
    }

    private fun setInstrumentationForUpdateClick() {
        val bundle = Bundle()
        val  properties = Properties()
        fb.logEvent(Constants.UPDATE_NOTIFICATION_CLICKED, bundle)
        MoEHelper.getInstance(requireContext()).trackEvent(Constants.UPDATE_NOTIFICATION_CLICKED, properties)
        UXCam.logEvent(Constants.UPDATE_NOTIFICATION_CLICKED)
        BlitzLlamaSDK.getSdkManager(requireContext()).triggerEvent(Constants.UPDATE_NOTIFICATION_CLICKED)
        val attribute = HashMap<String, Any>()
        Freshchat.trackEvent(requireContext(), Constants.UPDATE_NOTIFICATION_CLICKED, attribute)

    }

    private fun setInstrumentationForUpdate() {
        val bundle = Bundle()
        fb.logEvent(Constants.UPDATE_NOTIFICATION_VIEWED, bundle)
        val properties = Properties()
        MoEHelper.getInstance(requireContext()).trackEvent(Constants.UPDATE_NOTIFICATION_VIEWED, properties)
        UXCam.logEvent(Constants.UPDATE_NOTIFICATION_VIEWED)
        BlitzLlamaSDK.getSdkManager(requireContext()).triggerEvent(Constants.UPDATE_NOTIFICATION_VIEWED)
        val attribute = HashMap<String, Any>()
        Freshchat.trackEvent(requireContext(), Constants.UPDATE_NOTIFICATION_VIEWED, attribute)

    }

    private fun checkForUpdateFirst() {
        if (PrefrenceUtils.retriveDataInBoolean(requireContext(), Constants.CHECKFORFIRSTTIME)) {
        } else {
            checkForUpdate()
        }
    }

    @Override
    override fun onResume() {
        super.onResume()
        setInstrumentationNotificationViewed()
        MoEInboxHelper.getInstance().getUnClickedMessagesCountAsync(requireContext(), this@NotificationFragment)
        loadData()
    }

    private fun setInstrumentationNotificationViewed() {
        val bundle = Bundle()
        fb.logEvent(Constants.UPDATE_NOTIFICATION_VIEWED, bundle)
        val properties = Properties()
        MoEHelper.getInstance(requireContext()).trackEvent(Constants.UPDATE_NOTIFICATION_VIEWED, properties)
        UXCam.logEvent(Constants.UPDATE_NOTIFICATION_VIEWED)
        BlitzLlamaSDK.getSdkManager(requireContext()).triggerEvent(Constants.UPDATE_NOTIFICATION_VIEWED)
        val attribute = HashMap<String, Any>()
        Freshchat.trackEvent(requireContext(), Constants.UPDATE_NOTIFICATION_VIEWED, attribute)

    }

    private fun loadData() {
        //Fetch all inbox messages asynchronously
        MoEInboxHelper.getInstance().fetchAllMessagesAsync(requireContext(), this@NotificationFragment)
    }

    override fun onMessagesAvailable(messageList: List<InboxMessage>) {
        if (messageList.isEmpty()) {
            binding.recNotification.visibility = View.GONE
            binding.containerNotification.visibility = View.VISIBLE
            PrefrenceUtils.insertData(requireContext(), Constants.CURRENT_NOTIFICATION_COUNT, "")
        } else {
            binding.recNotification.visibility = View.VISIBLE
            binding.containerNotification.visibility = View.GONE
            when {
                PrefrenceUtils.retriveData(requireContext(), Constants.CURRENT_NOTIFICATION_COUNT).equals("", ignoreCase = true) -> {
                    count = 1
                    Log.d("Previous_size", "initView: $count")
                }
                PrefrenceUtils.retriveData(requireContext(), Constants.CURRENT_NOTIFICATION_COUNT).toInt() < messageList.size -> {
                    count = 1
                    Log.d("Previous_size", "initView: " + PrefrenceUtils.retriveData(requireContext(), Constants.CURRENT_NOTIFICATION_COUNT).toInt())
                }
                else -> {
                    count = 0
                }
            }
            PrefrenceUtils.insertData(requireContext(), Constants.CURRENT_NOTIFICATION_COUNT, messageList.size.toString())
        }
        binding.recNotification.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = DefaultItemAnimator()
            isNestedScrollingEnabled = false
            adapter = NotificationListAdapter(requireActivity(), messageList, count)
        }

    }

    override fun onCountAvailable(count: Long) {

    }


}