package com.vahan.mitra_playstore.view.refer.view.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.freshchat.consumer.sdk.Freshchat
import com.google.firebase.analytics.FirebaseAnalytics
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.FragmentInviteFriendsBinding
import com.vahan.mitra_playstore.models.ContactModel
import com.vahan.mitra_playstore.utils.ApiState
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.refer.view.adapter.InviteAdapter
import com.vahan.mitra_playstore.view.refer.models.ReferralInviteRequestModel
import com.vahan.mitra_playstore.view.refer.viewmodel.ReferralStatusViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


@AndroidEntryPoint
class ReferralInviteFriendsFragment : Fragment() {
    private lateinit var binding: FragmentInviteFriendsBinding
    private val CONTACT_REQUEST_CODE = 101
    private val TAG = "PermissionDemo"
    var listInvitedSelected: ArrayList<ContactModel> = ArrayList()
    private val listOfContacts: ArrayList<ContactModel> = ArrayList()
    private val selectedData: ArrayList<ContactModel> = ArrayList()
    private lateinit var dialogLoader: Dialog
    private lateinit var inviteAdapter: InviteAdapter
    private lateinit var viewReferralStatusModel: ReferralStatusViewModel
    private val listOf: ArrayList<String> = ArrayList()
    private lateinit var fa: FirebaseAnalytics
    @RequiresApi(Build.VERSION_CODES.N)
    val data: NavigableSet<ContactModel> = TreeSet(Comparator.comparing(ContactModel::phNo))

    // Compile the ReGex
    var regex: String = "[^a-zA-Z0-9]+"
    val regexStr = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$"


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_invite_friends,
            container,
            false
        )
        initViews()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    private fun initViews() {
        fa = FirebaseAnalytics.getInstance(requireActivity())
        if (PrefrenceUtils.retriveLangData(requireContext(), Constants.LANGUAGE).equals("en")) {
            binding.tvInvitedHint.text =
                resources.getString(R.string.str1) + " " + PrefrenceUtils.retriveData(
                    requireContext(),
                    Constants.REFERRAL_AMOUNT
                ) + " " + resources.getString(R.string.str5)
        } else {
            binding.tvInvitedHint.text =
                "अपने मित्र को मित्रा में शामिल होने के लिए आमंत्रित करें, और आप ₹ ${
                    PrefrenceUtils.retriveData(
                        requireContext(),
                        Constants.REFERRAL_AMOUNT
                    )
                } रुपये कमा सकते हैं।"
        }

        viewReferralStatusModel = ViewModelProvider(this)[ReferralStatusViewModel::class.java]
        setupPermissions()
        clickListener()
    }

    private fun clickListener() {
        binding.btInviteNow.setOnClickListener {
            when {
                listInvitedSelected.size in 1..10 -> {
                    sentInvite(listInvitedSelected)
                }
                listInvitedSelected.size > 10 -> Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.limit_excedded),
                    Toast.LENGTH_SHORT
                ).show()
                listInvitedSelected.size == 0 -> Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.no_contact_is_selected),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.tvDeselectAll.setOnClickListener {
            if (listInvitedSelected.size > 0) {
                inviteAdapter.unselectAll()
            }
        }
        binding.ivBackBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupPermissions() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            makeRequest()
        } else {
            getContact()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getContact() {

        val asyncJob = lifecycleScope.async(Dispatchers.IO) {
            val data = fetchContacts()
            listOfContacts.clear()
            selectedData.clear()
            listOfContacts.addAll(data)
            for (item in listOfContacts) {
                var strGetMOBILE: String?
                val p: Pattern = Pattern.compile(regex)
                val m: Matcher = p.matcher(item.phNo)
                if (!m.matches()) {
                    if (item.phNo.length in 11..13) {
                        if (item.phNo.startsWith("6") ||
                            item.phNo.startsWith("7") ||
                            item.phNo.startsWith("8") ||
                            item.phNo.startsWith("9")
                        ) {
                            if (!item.phNo.contains("*") ||
                                !item.phNo.contains("#")
                            ) {
                                if (item.phNo.startsWith("+")) {
                                    if (item.phNo.length == 13) {
                                        strGetMOBILE = item.phNo.trim().substring(3)
                                        selectedData.add(
                                            ContactModel(
                                                item.name,
                                                strGetMOBILE,
                                                false
                                            )
                                        )
                                    } else if (item.phNo.trim().length == 12) {
                                        strGetMOBILE = item.phNo.trim().substring(2)
                                        selectedData.add(
                                            ContactModel(
                                                item.name,
                                                strGetMOBILE,
                                                false
                                            )
                                        )
                                    }
                                } else if (item.phNo.startsWith("0")) {
                                    if (item.phNo.length == 11) {
                                        strGetMOBILE = item.phNo.trim().substring(1)
                                        selectedData.add(
                                            ContactModel(
                                                item.name,
                                                strGetMOBILE,
                                                false
                                            )
                                        )
                                    } else if (item.phNo.trim().length == 12) {
                                        strGetMOBILE = item.phNo.trim().substring(2)
                                        selectedData.add(
                                            ContactModel(
                                                item.name,
                                                strGetMOBILE,
                                                false
                                            )
                                        )
                                    } else {
                                        strGetMOBILE = item.phNo.trim()
                                        selectedData.add(
                                            ContactModel(
                                                item.name,
                                                strGetMOBILE,
                                                false
                                            )
                                        )
                                    }
                                } else if (item.phNo.startsWith("91")) {
                                    if (item.phNo.length == 12) {
                                        strGetMOBILE = item.phNo.trim().substring(2)
                                        selectedData.add(
                                            ContactModel(
                                                item.name,
                                                strGetMOBILE,
                                                false
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            if (asyncJob.isActive) {
                // show loading dialog to user if the task is taking time
                val progressDialogBuilder = createProgressDialog()
                try {
                    progressDialogBuilder.show()
                    asyncJob.await()
                    setUpRecyclerView()
                } finally {
                    progressDialogBuilder.cancel()
                }
            } else {
                asyncJob.await()
                setUpRecyclerView()
            }

        }

    }

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

    private fun makeRequest() {
        requestPermissions(
            arrayOf(Manifest.permission.READ_CONTACTS), CONTACT_REQUEST_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun fetchContacts(): NavigableSet<ContactModel> {
        val cr = requireActivity().contentResolver
        val cursor = cr.query(
            ContactsContract.Contacts.CONTENT_URI, null,
            null, null, null
        )
        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val id =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val name =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                val hasPhoneNumber =
                    cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                if (hasPhoneNumber > 0) {
                    val pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        ""
                    )
                    if (pCur != null && pCur.count > 0) {
                        while (pCur.moveToNext()) {
                            val phoneNum =
                                pCur.getString(pCur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            val trimmed = phoneNum.replace(Regex("[\\s|\\u00A0]+"), "")
                                .replace(Regex("[^a-zA-Z0-9]"), "")
                            Log.d(
                                TAG,
                                "fetchContacts: ${trimmed.replace(Regex("[^a-zA-Z0-9]"), "")}"
                            )
                            data.add(ContactModel(name, trimmed, false))

                        }
                        pCur.close()
                    }
                }
            }
            cursor.close()
        }
        return data
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            CONTACT_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                } else {
                    getContact()
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }
    }


    private fun setUpRecyclerView() {
        selectedData.sortWith { o1, o2 -> o1.name.compareTo(o2.name) }
        inviteAdapter = InviteAdapter(this, selectedData)
        binding.searchView.queryHint =
            resources.getString(R.string.search_contacts_or_enter_number)
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                Handler(Looper.getMainLooper()).postDelayed({
                    inviteAdapter.filter.filter(query)
                }, 1000)
                return true
            }
        })
        binding.recyclerInvite.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerInvite.adapter = inviteAdapter
    }

    private fun sentInvite(listInvitedSelected: ArrayList<ContactModel>) {

        for (i in 0 until listInvitedSelected.size) {
            listOf.add(listInvitedSelected[i].phNo)
        }
        val inviteReferralData = ReferralInviteRequestModel(listOf)
        val dialog = createProgressDialog()
        lifecycleScope.launchWhenStarted {
            viewReferralStatusModel.sentInviteReferral(inviteReferralData).collect {
                when (it) {
                    ApiState.Loading -> {
                        dialog.show()
                    }
                    is ApiState.Success -> {
                        dialog.dismiss()
                        setReferTypeInstrumentation()
                        val bundle = Bundle()
                        bundle.putString(Constants.MESSAGE, it.data.validReferralsMessage)
                        bundle.putInt(Constants.DUPLICATE_COUNT, it.data.duplicateReferrals!!)
                        bundle.putInt(Constants.VALID_COUNT, it.data.validReferrals!!)
                        bundle.putString(Constants.TYPE, Constants.LANDINGURL_REFERRAL)
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.nav_referral_rsp_status_fragment, bundle)
                    }
                    is ApiState.Failure -> {
                        dialog.dismiss()
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.nav_error_fragment)
                    }
                }
            }
        }
    }

    private fun setReferTypeInstrumentation() {
        val bundle = Bundle()
        val properties = Properties()
        val data = HashMap<String, String>()
        val attribute = HashMap<String, Any>()
        bundle.putString(Constants.TYPE, Constants.PHONE_BOOK)
        properties.addAttribute(Constants.TYPE, (Constants.PHONE_BOOK))
        data[Constants.TYPE] = (Constants.PHONE_BOOK)
        attribute[Constants.TYPE] = (Constants.PHONE_BOOK)
        fa.logEvent(Constants.REFER_TYPE, bundle)
        MoEHelper.getInstance(requireActivity()).trackEvent(Constants.REFER_TYPE, properties)
        UXCam.logEvent(Constants.REFER_TYPE, data)
        Freshchat.trackEvent(requireContext(), Constants.REFER_TYPE, attribute)
    }

}

