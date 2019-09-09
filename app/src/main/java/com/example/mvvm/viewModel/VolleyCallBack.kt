package com.example.mvvm.viewModel

import com.android.volley.VolleyError
import org.json.JSONObject

interface VolleyCallBack {
    fun onPutResponse(response: JSONObject)
    fun onPutError(error: VolleyError)
    fun getPageResponse(response:JSONObject,success:Boolean)
    fun getPageError(error: VolleyError)
}