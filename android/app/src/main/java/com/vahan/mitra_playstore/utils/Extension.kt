package com.vahan.mitra_playstore.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.blitzllama.androidSDK.BlitzLlamaSDK
import com.bumptech.glide.RequestBuilder
import com.freshchat.consumer.sdk.Freshchat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import com.moengage.inapp.MoEInAppHelper
import com.uxcam.UXCam
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.models.kotlin.EarnDataModel
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap


fun Context.captureEvents(context: Context,eventName : String, properties : HashMap<String, Any>){
    val propertiesOne = Properties()
    val fa = FirebaseAnalytics.getInstance(context)
    val bundle = Bundle()
    MoEHelper.getInstance(context).trackEvent(eventName, propertiesOne)
    fa.logEvent(eventName, bundle)
}

fun Context.roundOff2digit(amount: Double): String {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.DOWN
    return df.format(amount)
}



fun Context.dateConversionToString(pattern: String, date: String, conversation : String): String {
    val formatter: DateTimeFormatter =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    val date: LocalDateTime = LocalDateTime.parse(date, formatter)
    val formatterB = DateTimeFormatter.ofPattern(conversation)
    return date.format(formatterB)
}



@SuppressLint("SimpleDateFormat")
fun Context.timeConversionForCurrentDate(patten: String): Date? {
    val currentTime: String =
        SimpleDateFormat("hh:mm a").format(Date())
    val format = SimpleDateFormat("hh:mm a")
    return format.parse(currentTime)
}


fun captureAllEvents(
    context: Context,
    eventName: String,
    items: HashMap<String, Any>,
    properties: Properties
){
    val fa = FirebaseAnalytics.getInstance(context)
    val bundle = Bundle()
    Freshchat.trackEvent(context, eventName, items)
    MoEInAppHelper.getInstance().showInApp(context)
    MoEHelper.getInstance(context).trackEvent(eventName, properties)
    fa.logEvent(eventName, bundle)
}

 fun Context.setImage(context: Context,url: String, imageView: ImageView) {
    val requestBuilder: RequestBuilder<PictureDrawable> = GlideApp.with(context)
        .`as`(PictureDrawable::class.java)
        .placeholder(R.drawable.dialog_icon)
        .error(R.drawable.dialog_icon)
        .listener(SvgSoftwareLayerSetter())
    val uri = Uri.parse(url)
    requestBuilder.load(uri).into(imageView)
}

fun Context.startBlitzSurvey(context : Context,eventName : String){
    if (PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE).equals("en")) {
        BlitzLlamaSDK.getSdkManager(context).setSurveyLanguage("en")
    } else {
        BlitzLlamaSDK.getSdkManager(context).setSurveyLanguage("hi")
    }
//    Toast.makeText(context, "CLICKED", Toast.LENGTH_SHORT).show()
    BlitzLlamaSDK.getSdkManager(context).triggerEvent(eventName)
}

fun Context.setupScreen(automaticScreenTaggingName : Boolean, screenName : String){
    UXCam.setAutomaticScreenNameTagging(false)
    UXCam.tagScreenName("Earn Fragment")
}

fun Context.setFreshChatSessions(context : Context, dataModel : EarnDataModel) : String {
    var token = ""
    val freshchatUser = Freshchat.getInstance(context).user
    freshchatUser.firstName = dataModel.user?.name
    freshchatUser.email = dataModel.user?.email
    freshchatUser.setPhone("+91", dataModel.user?.phoneNumber)
    Freshchat.getInstance(context).user = freshchatUser
    dataModel.user?.email?.let {
        Freshchat.getInstance(context).identifyUser(it, null)
    };
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w("TAG", "Fetching FCM registration token failed", task.exception)
            return@OnCompleteListener
        }

        // Get new FCM registration token
        token = task.result
    })
    token?.let { it1 -> Freshchat.getInstance(context).setPushRegistrationToken(it1) }

    return token
}

fun Context.setBlitzLamaSurvey(context: Context, dataModel: EarnDataModel?, eventName : String) {
    BlitzLlamaSDK.getSdkManager(context).createUser(dataModel?.user?.id)
    BlitzLlamaSDK.getSdkManager(context).setUserName(dataModel?.user?.name)
    if (PrefrenceUtils.retriveLangData(context, Constants.LANGUAGE).equals("en")) {
        BlitzLlamaSDK.getSdkManager(context).setSurveyLanguage("en")
    } else {
        BlitzLlamaSDK.getSdkManager(context).setSurveyLanguage("hi")
    }
    BlitzLlamaSDK.getSdkManager(context).triggerEvent(eventName)
}

 fun Context.insertPreferencesData(context: Context, dataModel: EarnDataModel) {
    PrefrenceUtils.insertData(
        context,
        Constants.INSURANCE_ELIGIBILITY,
        dataModel.user!!.insuranceEligibilityStatus
    )
    PrefrenceUtils.insertData(
        context,
        Constants.MINIMUM_ELIGIBLE,
        dataModel.cashoutDetails!!.minAmountEligible.toString()
    )
    PrefrenceUtils.insertDataInBoolean(
        context,
        Constants.ISBANKPRIMARYACCOUNT,
        dataModel.bankAccountDetails!!.isBankDetailsEditAvailable!!
    )
}

fun Context.setMoengageUserDetails(dataModel: EarnDataModel,context: Context){
    MoEHelper.getInstance(context).setNumber(dataModel.user!!.phoneNumber)
    MoEHelper.getInstance(context).setFullName(dataModel.user!!.name!!)
    MoEHelper.getInstance(context).setFirstName(dataModel.user!!.name!!)
    MoEHelper.getInstance(context).setUniqueId(dataModel!!.user!!.id!!)
}

fun Context.setCustomMoengageGenericType(dataModel: EarnDataModel?, context: Context) {
    if (!dataModel!!.jobDetails!!.payrollUserCompanyId.equals("", ignoreCase = true)) {
        MoEHelper.getInstance(context)
            .setUserAttribute(Constants.USERT_TYPE, "Payroll")
        UXCam.setUserProperty(Constants.USERT_TYPE, "Payroll")
        BlitzLlamaSDK.getSdkManager(context)
            .setUserAttribute(Constants.USERT_TYPE, "Payroll", Constants.STRING)
        val userMeta: MutableMap<String, String> = HashMap()
        userMeta[Constants.USERT_TYPE] = "Payroll"
        Freshchat.getInstance(context).setUserProperties(userMeta);
    } else {
        MoEHelper.getInstance(context).setUserAttribute(Constants.USERT_TYPE, "")
        UXCam.setUserProperty(Constants.USERT_TYPE, "")
        BlitzLlamaSDK.getSdkManager(context)
            .setUserAttribute(Constants.USERT_TYPE, "", Constants.STRING)
        val userMeta: MutableMap<String, String> = HashMap()
        userMeta[Constants.USERT_TYPE] = ""
        Freshchat.getInstance(context).setUserProperties(userMeta)
    }
    if (dataModel.user!!.settings?.isTestUser!!) {
        MoEHelper.getInstance(context)
            .setUserAttribute(Constants.IS_TEST_USER, "true")
        UXCam.setUserProperty(Constants.IS_TEST_USER, "true")
        BlitzLlamaSDK.getSdkManager(context)
            .setUserAttribute(Constants.IS_TEST_USER, "true", Constants.STRING)
        val userMeta: MutableMap<String, String> = HashMap()
        userMeta[Constants.IS_TEST_USER] = "true"
        Freshchat.getInstance(context).setUserProperties(userMeta)
    } else {
        MoEHelper.getInstance(context).setUserAttribute(Constants.IS_TEST_USER, "")
        UXCam.setUserProperty(Constants.IS_TEST_USER, "")
        BlitzLlamaSDK.getSdkManager(context)
            .setUserAttribute(Constants.IS_TEST_USER, "", Constants.STRING)
    }
    if (!dataModel.user.city.equals("")) {
        MoEHelper.getInstance(context)
            .setUserAttribute(Constants.CITY, dataModel.user.city!!)
        UXCam.setUserProperty(Constants.CITY, dataModel.user.city)
        BlitzLlamaSDK.getSdkManager(context)
            .setUserAttribute(Constants.CITY, dataModel.user.city, Constants.STRING)
        val userMeta: MutableMap<String, String> = HashMap()
        userMeta[Constants.CITY] = dataModel.user.city
        Freshchat.getInstance(context).setUserProperties(userMeta)
    } else {
        MoEHelper.getInstance(context).setUserAttribute(Constants.CITY, "")
        UXCam.setUserProperty(Constants.CITY, "")
        BlitzLlamaSDK.getSdkManager(context)
            .setUserAttribute(Constants.CITY, "", Constants.STRING)
        val userMeta: MutableMap<String, String> = HashMap()
        userMeta[Constants.CITY] = ""
        Freshchat.getInstance(context).setUserProperties(userMeta)
    }
    if (dataModel.bankAccountDetails!!.available!!) {
        MoEHelper.getInstance(context)
            .setUserAttribute(Constants.BANK_ACCOUNT_DETAILS_AVAILABLE, "true")
        UXCam.setUserProperty(Constants.BANK_ACCOUNT_DETAILS_AVAILABLE, "true")
        BlitzLlamaSDK.getSdkManager(context)
            .setUserAttribute(
                Constants.BANK_ACCOUNT_DETAILS_AVAILABLE,
                "true",
                Constants.STRING
            )
        val userMeta: MutableMap<String, String> = HashMap()
        userMeta[Constants.BANK_ACCOUNT_DETAILS_AVAILABLE] = "true"
        Freshchat.getInstance(context).setUserProperties(userMeta)
    } else {
        MoEHelper.getInstance(context)
            .setUserAttribute(Constants.COMPANY_NAME1, dataModel.jobDetails!!.companyName ?: "")
        MoEHelper.getInstance(context)
            .setUserAttribute(
                Constants.DATA_OF_JOINING1,
                dataModel.jobDetails.dateOfJoining ?: ""
            )
        BlitzLlamaSDK.getSdkManager(context)
            .setUserAttribute(Constants.BANK_ACCOUNT_DETAILS_AVAILABLE, "", Constants.STRING)
        val userMeta: MutableMap<String, String> = HashMap()
        userMeta[Constants.BANK_ACCOUNT_DETAILS_AVAILABLE] = ""
        Freshchat.getInstance(context).setUserProperties(userMeta)
    }
    if (dataModel.documents!!.uploaded!!) {
        MoEHelper.getInstance(context)
            .setUserAttribute(Constants.DOCUMENTS_UPLOADED, "true")
        UXCam.setUserProperty(Constants.DOCUMENTS_UPLOADED, "true")
        BlitzLlamaSDK.getSdkManager(context)
            .setUserAttribute(Constants.DOCUMENTS_UPLOADED, "true", Constants.STRING)
        val userMeta: MutableMap<String, String> = HashMap()
        userMeta[Constants.DOCUMENTS_UPLOADED] = "true"
        Freshchat.getInstance(context).setUserProperties(userMeta)
    } else {
        MoEHelper.getInstance(context)
            .setUserAttribute(Constants.DOCUMENTS_UPLOADED, "")
        UXCam.setUserProperty(Constants.DOCUMENTS_UPLOADED, "")
        BlitzLlamaSDK.getSdkManager(context)
            .setUserAttribute(Constants.DOCUMENTS_UPLOADED, "", Constants.STRING)
        val userMeta: MutableMap<String, String> = HashMap()
        userMeta[Constants.DOCUMENTS_UPLOADED] = ""
        Freshchat.getInstance(context).setUserProperties(userMeta)
    }

    //Manual Generic
    MoEHelper.getInstance(context).setUserAttribute(
        Constants.INSURANCE_ELIGIBILITY_STATUS,
        dataModel.user.insuranceEligibilityStatus ?: ""
    )
    MoEHelper.getInstance(context).setUserAttribute(
        Constants.LOAN_ELIGIBILITY_STATUS,
        dataModel.user.loanEligibilityStatus ?: ""
    )
    MoEHelper.getInstance(context)
        .setUserAttribute(
            Constants.CASHOUT_ELIGIBILITY_STATUS,
            dataModel.user.cashoutEligibilityStatus ?: ""
        )
    MoEHelper.getInstance(context)
        .setUserAttribute(
            Constants.RIDER_ID_1,
            dataModel.jobDetails?.companyEmployeeId ?: ""
        )
    MoEHelper.getInstance(context)
        .setUserAttribute(
            Constants.CITY,
            dataModel.user.city ?: ""
        )

    MoEHelper.getInstance(context)
        .setUserAttribute(Constants.COMPANY_NAME1, dataModel.jobDetails!!.companyName ?: "")
    MoEHelper.getInstance(context)
        .setUserAttribute(Constants.DATA_OF_JOINING1, dataModel.jobDetails.dateOfJoining ?: "")
    dataModel.user.cashoutEligibilityStatus?.let {
        MoEHelper.getInstance(context)
            .setUserAttribute(Constants.CASHOUT_ELIGIBILITY_STATUS, it)
    }
    dataModel.cashoutDetails!!.cashoutFeePercentage?.let {
        MoEHelper.getInstance(context)
            .setUserAttribute(
                Constants.CASHOUT_FEE_PERCENT,
                it
            )
    }

    dataModel.cashoutDetails.cashoutFixedFee ?: 0.let {
        MoEHelper.getInstance(context)
            .setUserAttribute(
                Constants.CASHOUT_FIXED_FEE,
                it
            )
    }


    // Manual UXM
    UXCam.setUserProperty(
        Constants.INSURANCE_ELIGIBILITY_STATUS,
        dataModel.user.insuranceEligibilityStatus
    )
    UXCam.setUserProperty(
        Constants.CASHOUT_ELIGIBILITY_STATUS,
        dataModel.user.cashoutEligibilityStatus
    )
    UXCam.setUserProperty(Constants.COMPANY_NAME1, dataModel.jobDetails.companyName ?: "")
    UXCam.setUserProperty(Constants.DATA_OF_JOINING1, dataModel.jobDetails.dateOfJoining ?: "")
    UXCam.setUserProperty(Constants.USER_NAME, dataModel.user.name ?: "")
    UXCam.setUserProperty(
        Constants.CASHOUT_ELIGIBILITY_STATUS,
        dataModel.user.cashoutEligibilityStatus ?: ""
    )
    UXCam.setUserProperty(
        Constants.CASHOUT_FEE_PERCENT,
        dataModel.cashoutDetails.cashoutFeePercentage!!.toFloat()
    )

    //Blitzma
    BlitzLlamaSDK.getSdkManager(context)
        .setUserAttribute(
            Constants.COMPANY_NAME1,
            dataModel.jobDetails.companyName,
            Constants.STRING
        )
    BlitzLlamaSDK.getSdkManager(context)
        .setUserAttribute(
            Constants.DATA_OF_JOINING1,
            dataModel.jobDetails.dateOfJoining,
            Constants.STRING
        )
    BlitzLlamaSDK.getSdkManager(context)
        .setUserAttribute(Constants.USER_NAME, dataModel.user.name, Constants.STRING)
    BlitzLlamaSDK.getSdkManager(context).setUserAttribute(
        Constants.CASHOUT_ELIGIBILITY_STATUS,
        dataModel.user.cashoutEligibilityStatus,
        Constants.STRING
    )
    BlitzLlamaSDK.getSdkManager(context).setUserAttribute(
        Constants.BUNDLE_CASHOUT_FEE_PERCENTAGE,
        dataModel.cashoutDetails.cashoutFeePercentage.toFloat().toString(),
        Constants.NUMBER
    )

    // Freshchat UserProperty
    val userMeta: MutableMap<String, String> = HashMap()
    userMeta[Constants.COMPANY_NAME1] = dataModel.jobDetails.companyName ?: ""
    userMeta[Constants.DATA_OF_JOINING1] = dataModel.jobDetails.dateOfJoining ?: ""
    userMeta[Constants.USER_NAME] = dataModel.user.name ?: ""
    userMeta[Constants.CASHOUT_ELIGIBILITY_STATUS] =
        dataModel.user.cashoutEligibilityStatus ?: ""
    userMeta[Constants.BUNDLE_CASHOUT_FEE_PERCENTAGE] =
        dataModel.cashoutDetails.cashoutFeePercentage.toFloat().toString() ?: ""
    Freshchat.getInstance(context).setUserProperties(userMeta)


}
