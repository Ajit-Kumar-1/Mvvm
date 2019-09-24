package com.example.mvvm.viewModel

import org.json.JSONObject

interface ViewModelCallBack {
    fun onPutResponse(response: JSONObject)
    fun onPutError(throwable: Throwable)
    fun getPageResponse(response: JSONObject)
    fun getPageError(throwable: Throwable)
}
