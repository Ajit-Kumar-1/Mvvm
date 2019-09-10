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

class ProfileViewModel(application: Application) : AndroidViewModel(application), VolleyCallBack {
//class ProfileViewModel:ViewModel(),VolleyCallBack{
    private val final= StringValues()
    var repository: AccountRepository = AccountRepository(application)
    var pageIndex=1
    var position=0
    var account: MutableLiveData<APIEntity> = MutableLiveData()
    private var originalAccount:APIEntity?=null
    var enabled:MutableLiveData<Boolean> = MutableLiveData()
    var active=false
    var progress1:MutableLiveData<Boolean> = MutableLiveData()
    var progress2:MutableLiveData<Boolean> = MutableLiveData()
    var statusCheck:MutableLiveData<Boolean> = MutableLiveData()
    var refreshRecyclerView:MutableLiveData<Boolean> = MutableLiveData()
    var viewContainer:MutableLiveData<Boolean> = MutableLiveData()
    var maleCheck:MutableLiveData<Boolean> = MutableLiveData()
    var femaleCheck:MutableLiveData<Boolean> = MutableLiveData()

    init{
        enabled.value=false
        netCall()
    }

    fun putData() {
        val putMap = HashMap<String, String>()
        account.value?.apply {
            originalAccount?.let {
                if (it.firstName != firstName)
                    putMap[final.FIRST_NAME] = firstName
                if (it.lastName != lastName)
                    putMap[final.LAST_NAME] = lastName
                gender=when(maleCheck.value){
                    true->final.MALE
                    else->final.FEMALE
                }
                status=when(statusCheck.value){
                    true->final.ACTIVE
                    else->final.INACTIVE
                }
                if (it.gender != gender)
                    putMap[final.GENDER] = gender
                if (it.dob != dob)
                    putMap[final.DOB] = dob
                if (it.email != email)
                    putMap[final.EMAIL] = email
                if (it.phone != phone)
                    putMap[final.PHONE] = phone
                if (it.website != website)
                    putMap[final.WEBSITE] = website
                if (it.address != address)
                    putMap[final.ADDRESS] = address
                if (it.status != status)
                    putMap[final.STATUS] = status
            }
        }
        if (putMap != HashMap<String, String>()) {
            progress2.value = true
            account.value?.id?.let { repository.putData(putMap,position, it,this as VolleyCallBack) }
        }
    }
    fun netCall(){
        progress1.value=true
        repository.getPage(final.USERS_PAGE_URL+pageIndex,this as VolleyCallBack)
    }
    private fun retrieveDetails(jsonObject:JSONObject):APIEntity{
        jsonObject.let {
            return APIEntity(it.getString(final.ID).toInt(), it.getString(final.FIRST_NAME), it.getString(final.LAST_NAME),
                it.getString(final.GENDER), it.getString(final.DOB), it.getString(final.EMAIL).toLowerCase(Locale.ROOT),
                it.getString(final.PHONE), it.getString(final.WEBSITE), it.getString(final.ADDRESS), it.getString(final.STATUS))
        }
    }
    override fun onPutResponse(response: JSONObject) {
        if (response.getJSONObject(final.META).getBoolean(final.SUCCESS)) {
            assignment(position)
            refreshRecyclerView.value=false
            Toast.makeText(getApplication(), R.string.information_updated, Toast.LENGTH_SHORT).show()
            progress2.value=false
        }
        else {
            val responseArray = response.getJSONArray(final.RESULT)
            for (i in 0 until responseArray.length())
                Toast.makeText(getApplication(), "${responseArray.getJSONObject(i).getString(final.FIELD)}:" +
                        " ${responseArray.getJSONObject(i).getString(final.MESSAGE)}", Toast.LENGTH_SHORT).show()
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
    fun retry(){
        repository.retry()
    }
    fun assignment(position:Int){
        account.value = retrieveDetails(JSONObject(repository.data[position]))
        originalAccount = retrieveDetails(JSONObject(repository.data[position]))
        maleCheck.value=account.value?.gender==final.MALE
        femaleCheck.value=account.value?.gender==final.FEMALE
        statusCheck.value=account.value?.status==final.ACTIVE
        this.position = position
        enabled.value = false
    }
}