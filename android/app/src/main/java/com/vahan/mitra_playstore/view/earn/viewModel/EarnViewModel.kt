package com.vahan.mitra_playstore.view.earn.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.vahan.mitra_playstore.BuildConfig
import com.vahan.mitra_playstore.model.CashOutModel
import com.vahan.mitra_playstore.models.FeedbackTriggersModel
import com.vahan.mitra_playstore.models.FreshchatEnableModel
import com.vahan.mitra_playstore.models.kotlin.Transaction
import com.vahan.mitra_playstore.network.Retrofithit
import com.vahan.mitra_playstore.repository.VahaanRepository
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.BaseApplication
import com.vahan.mitra_playstore.view.crossutilsslot.models.ReqModelSaveAttendance
import com.vahan.mitra_playstore.workmanager.WorkerScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class EarnViewModel @Inject constructor(private val repo: VahaanRepository) : ViewModel() {

    val remoteCashOutData: LiveData<CashOutModel>
        get() = _remoteCashOutData
    private var _remoteCashOutData = MutableLiveData<CashOutModel>()

    val trigger: LiveData<FeedbackTriggersModel>
        get() = _trigger
    private var _trigger = MutableLiveData<FeedbackTriggersModel>()

    val freshchatmodel: LiveData<FreshchatEnableModel>
        get() = _freshchatmodel
    private var _freshchatmodel = MutableLiveData<FreshchatEnableModel>()


    // API CALLS
    val getEarnList = repo.getEarnList()
    val getFetchAttendance = repo.getFetchAttendance()
    fun saveAttendanceForUser(reqModel : ReqModelSaveAttendance) = repo.saveAttendanceForUser(reqModel)
    fun getTransactionDetails(transaction: Transaction) = repo.getTransactionDetails(transaction)
    fun getBanner(
        companyName: String,
        type: String,
        city: String
    ) = repo.getBanner(companyName, type,city)
    fun getBankDetail() = Retrofithit().bankInfo!!
    fun paymentMoney(amount: String, purpose : String) = Retrofithit().paymeMoney(amount,purpose)!!
    fun getRemoteConfigDataForUpdate() {
        _remoteCashOutData.postValue(Gson().fromJson(
            PrefrenceUtils.retriveData(
                BaseApplication.context,
                Constants.REMOTE_CONFIG_CASHOUT_CONDITION
            ),
            CashOutModel::class.java
        ))
        _trigger.postValue(Gson().fromJson(
            PrefrenceUtils.retriveData(
                BaseApplication.context,
                Constants.FEEDBACK_TRIGGERS_REMOTE
            ),
            FeedbackTriggersModel::class.java
        ))

        _freshchatmodel.postValue(Gson().fromJson(
            PrefrenceUtils.retriveData(
                BaseApplication.context,
                Constants.FRESHCHAT_ENABLE_CONDITION_REMOTE_CONFIG
            ),
            FreshchatEnableModel::class.java
        ))

    }
    fun getDeviceInfoS() {
        if (!PrefrenceUtils.retriveData(BaseApplication.context, Constants.SHOWN_VERSION)
                .equals(BuildConfig.VERSION_NAME, ignoreCase = true)
        ) {
            WorkerScheduler.updateDeviceInfo(
                BaseApplication.context!!,
                PrefrenceUtils.retriveData(BaseApplication.context, Constants.PHONENUMBER),
                PrefrenceUtils.retriveData(BaseApplication.context, Constants.API_TOKEN)
            )
            PrefrenceUtils.insertData(BaseApplication.context, Constants.CHECKDEVICEDETAILSONCES, "Two")
        }
        if (PrefrenceUtils.retriveData(BaseApplication.context, Constants.CHECKDEVICEDETAILSONCES)
                .equals("ONCES")
        ) {
            WorkerScheduler.updateDeviceInfo(
                BaseApplication.context!!,
                PrefrenceUtils.retriveData(BaseApplication.context!!, Constants.PHONENUMBER),
                PrefrenceUtils.retriveData(BaseApplication.context!!, Constants.API_TOKEN)
            )
            PrefrenceUtils.insertData(BaseApplication.context!!, Constants.CHECKDEVICEDETAILSONCES, "Two")
        }

    }



}

