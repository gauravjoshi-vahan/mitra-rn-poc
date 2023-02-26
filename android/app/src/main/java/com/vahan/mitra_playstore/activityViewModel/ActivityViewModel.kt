package com.vahan.mitra_playstore.activityViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.models.kotlin.NavigationRespLogic
import com.vahan.mitra_playstore.network.RetrofitClient
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.view.earn.model.NudgesModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityViewModel : ViewModel() {

    val nudgeModel: LiveData<NudgesModel> get() = _nudgeModel
    private var _nudgeModel = MutableLiveData<NudgesModel>()


     fun getNudgeData() : MutableLiveData<NudgesModel>{
        val call = RetrofitClient().apiRetrofitInterceptor.nudges
        call.enqueue(object : Callback<NudgesModel?> {
            override fun onResponse(
                call: Call<NudgesModel?>,
                response: Response<NudgesModel?>
            ) {
                val nudgeDetailsModel = NudgesModel(null, null,null)
                if (response.code() in 200..299) {
                    _nudgeModel.postValue(response.body())
                } else if (response.code() in 400..499) {
                    nudgeDetailsModel.statusCode = response.code()
                    _nudgeModel.postValue(nudgeDetailsModel)
                } else if (response.code() >= 500) {
                    nudgeDetailsModel.statusCode = 500
                    _nudgeModel.postValue(nudgeDetailsModel)
                }
            }

            override fun onFailure(call: Call<NudgesModel?>, t: Throwable) {
                val nudgeDetailsModel = NudgesModel(null, null,null)
                nudgeDetailsModel.statusCode = 500
                _nudgeModel.postValue(nudgeDetailsModel)
            }
        })
        return _nudgeModel
    }

}