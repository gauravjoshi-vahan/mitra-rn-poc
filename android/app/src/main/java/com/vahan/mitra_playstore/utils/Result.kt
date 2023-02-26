package com.vahan.mitra_playstore.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import java.lang.reflect.InvocationTargetException


fun <T> toResultFlow(call: suspend () -> Response<T>): Flow<ApiState<T>> = flow {
    emit(ApiState.Loading)
    try {
        val response = call()
        if (response.isSuccessful) {
            response.body()?.let {
                emit(ApiState.Success(it))
            }
        } else {
            response.errorBody()?.let { error ->
                error.close()
                Log.e("ERROR", "toResultFlow: " + error.string())
                emit(ApiState.Failure(error.string()))
            }
        }
    } catch (e: Exception) {
        emit(ApiState.Failure(e.message!!))
    }
}.flowOn(Dispatchers.IO)