package com.example.mvvm.model

import android.app.Application
import androidx.room.Room
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AccountRepository (application: Application){

    private val final=StringValues()
    private val queue: RequestQueue = Volley.newRequestQueue(application)
    private var request:JsonObjectRequest?=null
    var data=ArrayList<String>()
    private val database: MyDatabase = Room.databaseBuilder(application.applicationContext, MyDatabase::class.java,
        final.DATABASE)
        .fallbackToDestructiveMigration()
        .build()
    private val dataDAO: DataDAO= database.dataDao()

    fun insert(data: APIEntity){
        GlobalScope.launch {
            dataDAO.insert(data)
        }
    }
    fun putData(putMap:HashMap<String,String>,position:Int,id:Int,callBack: VolleyCallBack) {
        request = object : JsonObjectRequest(Method.PUT, "${final.USER_DETAILS_URL}${id}",
            JSONObject(putMap as Map<*, *>), Response.Listener<JSONObject> { response ->
                if (response.getJSONObject(final.META).getBoolean(final.SUCCESS))
                    response.getJSONObject(final.RESULT).let {
                        insert(retrieveDetails(it))
                        data[position] = it.toString()
                    }
                callBack.onPutResponse(response)
            },
            Response.ErrorListener { callBack.onPutError(it) }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers[final.ACCESS_TOKEN_KEY] = final.accessToken
                headers[final.CONTENT_TYPE_KEY] = final.CONTENT_TYPE_VALUE
                return headers
            }
        }
        queue.add(request)
    }
    fun getPage(url:String,callBack: VolleyCallBack){
        request = object : JsonObjectRequest(Method.GET, url, null, Response.Listener<JSONObject> { response ->
                var jsonObject = response.getJSONObject(final.META)
            var success=false
                if (jsonObject.getBoolean(final.SUCCESS)) {
                    val jsonArray = response.getJSONArray(final.RESULT)
                    success=true
                    var lastID=when(data.size==0){
                        true->0
                        else->JSONObject(data.last()).getString(final.ID).toInt()
                    }
                    for (i in 0 until jsonArray.length()) {
                        jsonObject = jsonArray.getJSONObject(i)
                        if (jsonObject.getString(final.ID).toInt() > lastID) {
                            data.add(jsonArray.getString(i))
                            insert(retrieveDetails(jsonObject))
                            lastID=JSONObject(data.last()).getString(final.ID).toInt()
                        }
                        else {
                            success = false
                            break
                        }
                    }
                }
                callBack.getPageResponse(response,success)
            }, Response.ErrorListener {
                callBack.getPageError(it)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers[final.ACCESS_TOKEN_KEY] = final.accessToken
                return headers
            }
        }
        queue.add(request)
    }
    fun retry(){
        queue.add(request)
    }
    fun retrieveDetails(jsonObject:JSONObject):APIEntity{
        jsonObject.let {
            return APIEntity(it.getString(final.ID).toInt(), it.getString(final.FIRST_NAME), it.getString(final.LAST_NAME),
                it.getString(final.GENDER), it.getString(final.DOB), it.getString(final.EMAIL).toLowerCase(Locale.ROOT),
                it.getString(final.PHONE), it.getString(final.WEBSITE), it.getString(final.ADDRESS), it.getString(final.STATUS))
        }
    }
}