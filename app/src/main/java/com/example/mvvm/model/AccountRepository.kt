package com.example.mvvm.model

import android.app.Application
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.mvvm.viewModel.VolleyCallBack
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.HashMap
import kotlin.collections.ArrayList

class AccountRepository (application: Application){

    private val final: StringValues = StringValues()
    private val queue: RequestQueue = Volley.newRequestQueue(application)
    private var request:JsonObjectRequest? = null
    private val dataDAO: DataDAO? = MyDatabase.getInstance(application)?.dataDao()
    var data: java.util.ArrayList<String> = ArrayList()

    fun insert(data: APIEntity){
        GlobalScope.launch {
            dataDAO?.insert(data)
        }
    }

    fun putData(payload:JSONObject, id:Int, callBack: VolleyCallBack) {
        request = object : JsonObjectRequest(
            Method.PUT,
            "${final.USER_DETAILS_URL}${id}",
            payload,
            Response.Listener<JSONObject>(callBack::onPutResponse),
            Response.ErrorListener(callBack::onPutError)
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): HashMap<String, String>
                = hashMapOf(final.ACCESS_TOKEN_KEY to final.accessToken)
        }
        queue.add(request)
    }

    fun getPage(pageIndex:Int, callBack: VolleyCallBack){
        request = object : JsonObjectRequest(
            Method.GET,
            final.USERS_PAGE_URL+pageIndex,
            null,
            Response.Listener<JSONObject>(callBack::getPageResponse),
            Response.ErrorListener(callBack::getPageError)
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): HashMap<String, String>
                = hashMapOf(final.ACCESS_TOKEN_KEY to final.accessToken)
        }
        queue.add(request)
    }

    fun retry(){
        queue.add(request)
    }

}
