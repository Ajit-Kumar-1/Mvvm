package com.example.mvvm.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.VolleyError
import com.example.mvvm.R
import com.example.mvvm.model.APIEntity
import com.example.mvvm.model.AccountRepository
import com.example.mvvm.model.StringValues
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class ProfileViewModel(application: Application) : AndroidViewModel(application), VolleyCallBack{
//class ProfileViewModel:ViewModel(),VolleyCallBack{
    private val final= StringValues()
    private var repository: AccountRepository = AccountRepository(application)
    private var originalAccount:APIEntity?=null
    var account= MutableLiveData<APIEntity>()
    var enabled=MutableLiveData<Boolean>()
    var progress1=MutableLiveData<Boolean>()
    var progress2=MutableLiveData<Boolean>()
    var statusCheck=MutableLiveData<Boolean>()
    var refreshRecyclerView=MutableLiveData<Boolean>()
    var viewContainer=MutableLiveData<Boolean>()
    var maleCheck=MutableLiveData<Boolean>()
    var femaleCheck=MutableLiveData<Boolean>()
    var retryRequest= MutableLiveData<Boolean>()
    var pageIndex=1
    var position=0
    var active=false

    init{
        retryRequest.value=false
        enabled.value=false
        netCall()
    }

    fun putData() {
        account.value?.apply {
            gender=when(maleCheck.value){
                true->final.MALE
                else->final.FEMALE
            }
            status=when(statusCheck.value){
                true->final.ACTIVE
                else->final.INACTIVE
            }
        }
        val putMap = putChanges(Pair(account.value,originalAccount))
        if (putMap != HashMap<String, String?>()) {
            progress2.value = true
            account.value?.id?.let {
                repository.putData(putMap,position, it,this as VolleyCallBack)
            }
        }
    }
    fun netCall(){
        progress1.value=true
        repository.getPage(final.USERS_PAGE_URL+pageIndex,this as VolleyCallBack)
    }
    private fun retrieveDetails(jsonObject:JSONObject):APIEntity= jsonObject.let {
        APIEntity(it.getString(final.ID).toInt(), it.getString(final.FIRST_NAME),
            it.getString(final.LAST_NAME), it.getString(final.GENDER), it.getString(final.DOB),
            it.getString(final.EMAIL).toLowerCase(Locale.ROOT), it.getString(final.PHONE),
            it.getString(final.WEBSITE), it.getString(final.ADDRESS), it.getString(final.STATUS))
    }
    private fun putChanges(data:Pair<APIEntity?,APIEntity?>): java.util.HashMap<String, String?> {
        val putMap = java.util.HashMap<String, String?>()
        data.first?.apply {
            data.second?.let {
                if (it.firstName?.trim() != firstName?.trim())
                    putMap[final.FIRST_NAME] = firstName?.trim()
                if (it.lastName?.trim() != lastName?.trim())
                    putMap[final.LAST_NAME] = lastName?.trim()
                if (it.gender?.trim() != gender)
                    putMap[final.GENDER] = gender?.trim()
                if (it.dob?.trim() != dob?.trim())
                    putMap[final.DOB] = dob?.trim()
                if (it.email?.trim() != email?.trim())
                    putMap[final.EMAIL] = email?.trim()
                if (it.phone?.trim() != phone?.trim())
                    putMap[final.PHONE] = phone?.trim()
                if (it.website?.trim() != website?.trim())
                    putMap[final.WEBSITE] = website?.trim()
                if (it.address?.trim() != address?.trim())
                    putMap[final.ADDRESS] = address?.trim()
                if (it.status?.trim() != status?.trim())
                    putMap[final.STATUS] = status?.trim()
            }
        }
        return putMap
    }
    override fun onPutResponse(response: JSONObject) {
        if (response.getJSONObject(final.META).getBoolean(final.SUCCESS)) {
            assignment(position)
            refreshRecyclerView.value=false
            Toast.makeText(getApplication(), R.string.information_updated, Toast.LENGTH_SHORT).show()
        }
        else {
            val responseArray = response.getJSONArray(final.RESULT)
            for (i in 0 until responseArray.length())
                Toast.makeText(getApplication(),
                    "${responseArray.getJSONObject(i).getString(final.FIELD)}:" +
                        " ${responseArray.getJSONObject(i).getString(final.MESSAGE)}",
                    Toast.LENGTH_SHORT).show()
        }
        progress2.value = false
    }
    override fun onPutError(error: VolleyError){
        progress2.value = false
        Toast.makeText(getApplication(), R.string.network_error, Toast.LENGTH_SHORT).show()
    }
    override fun getPageResponse(response: JSONObject, success:Boolean) {
        val jsonObject = response.getJSONObject(final.META)
        if (!jsonObject.getBoolean(final.SUCCESS))
            Toast.makeText(getApplication(), "${jsonObject.get(final.CODE)}:" +
                    " ${jsonObject.get(final.MESSAGE)}", Toast.LENGTH_SHORT).show()
        else {
            if (success) {
                if (pageIndex == 1) {
                    assignment(0)
                    viewContainer.value=false
                }
                pageIndex++
            }
            refreshRecyclerView.value=true
        }
        progress1.value=false
    }
    override fun getPageError(error: VolleyError) {
        progress1.value=false
    }
    fun assignment(position:Int){
        account.value = retrieveDetails(JSONObject(repository.getData(position)))
        originalAccount = retrieveDetails(JSONObject(repository.getData(position)))
        maleCheck.value=account.value?.gender==final.MALE
        femaleCheck.value=account.value?.gender==final.FEMALE
        statusCheck.value=account.value?.status==final.ACTIVE
        this.position = position
        enabled.value = false
    }
    fun retry()=repository.retry()
    fun getData()=repository.getData()
}