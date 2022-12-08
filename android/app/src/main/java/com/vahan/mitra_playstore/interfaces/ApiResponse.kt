package com.vahan.mitra_playstore.interfaces


interface ApiResponse<T> {
    fun getResult(`object`: T?)

}
