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
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class ProfileViewModel(application: Application) : AndroidViewModel(application), VolleyCallBack {

    private val final: StringValues = StringValues()
    private var repository: AccountRepository = AccountRepository(application)
    private var originalAccount:APIEntity? = null
    var account: MutableLiveData<APIEntity> = MutableLiveData()
    var enabled: MutableLiveData<Boolean> = MutableLiveData()
    var progress1: MutableLiveData<Boolean> = MutableLiveData()
    var progress2: MutableLiveData<Boolean> = MutableLiveData()
    var statusCheck: MutableLiveData<Boolean> = MutableLiveData()
    var refreshRecyclerView: MutableLiveData<Boolean> = MutableLiveData()
    var viewContainer: MutableLiveData<Boolean> = MutableLiveData()
    var maleCheck: MutableLiveData<Boolean> = MutableLiveData()
    var femaleCheck: MutableLiveData<Boolean> = MutableLiveData()
    var retryRequest: MutableLiveData<Boolean> = MutableLiveData()
    var pageIndex: Int = 1
    var active: Boolean = false
    var position: Int = 0

    init{
        retryRequest.value = false
        enabled.value = false
        netCall()
    }

    fun putData() {
        val putMap= HashMap<String,String?>()
        account.value?.apply {
            originalAccount?.let {
                gender = when (maleCheck.value){
                    true -> final.MALE
                    else -> final.FEMALE
                }
                status = when (statusCheck.value){
                    true -> final.ACTIVE
                    else -> final.INACTIVE
                }
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
        if (putMap != HashMap<String, String?>()) {
            progress2.value = true
            account.value?.id?.let {
                repository.putData(
                    payload = JSONObject(putMap as Map<*, *>),
                    id = it,
                    callBack = this as VolleyCallBack
                )
            }
        }
    }

    override fun onPutResponse(response: JSONObject) {
        if (response.getJSONObject(final.META).getBoolean(final.SUCCESS)) {
            repository.apply {
                response.getJSONObject(final.RESULT).let {
                    insert(data = getEntityFromJSON(jsonObject = it))
                    data[position] = it.toString()
                }
            }
            assignment(position)
            refreshRecyclerView.value = false
            Toast.makeText(getApplication(),R.string.information_updated,Toast.LENGTH_SHORT).show()
        }
        else {
            val responseArray: JSONArray = response.getJSONArray(final.RESULT)
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

    fun netCall(){
        progress1.value = true
        repository.getPage(pageIndex = pageIndex, callBack = this as VolleyCallBack)
    }

    override fun getPageResponse(response: JSONObject) {
        var jsonObject: JSONObject = response.getJSONObject(final.META)
        if (jsonObject.getBoolean(final.SUCCESS)) {
            val jsonArray: JSONArray = response.getJSONArray(final.RESULT)
            var success = true
            var previousID: Int = when(repository.data.size>0){
                true -> JSONObject(repository.data.last()).getString(final.ID).toInt()
                else -> 0
            }
            for (i in 0 until jsonArray.length()) {
                jsonObject = jsonArray.getJSONObject(i)
                if (jsonObject.getString(final.ID).toInt() > previousID)
                    repository.apply{
                        data.add(jsonArray.getString(i))
                        insert(data = getEntityFromJSON(jsonObject))
                        previousID = JSONObject(data.last()).getString(final.ID).toInt()
                    }
                else {
                    success = false
                    break
                }
            }
            if (success) {
                if (pageIndex == 1) {
                    assignment(position = 0)
                    viewContainer.value = false
                }
                pageIndex++
                refreshRecyclerView.value = true
            }
        }
        else
            Toast.makeText(getApplication(),
                "${jsonObject.get(final.CODE)}:" + " ${jsonObject.get(final.MESSAGE)}",
                Toast.LENGTH_SHORT).show()
        progress1.value = false
    }

    override fun getPageError(error: VolleyError) {
        progress1.value = false
        Toast.makeText(getApplication(), R.string.network_error, Toast.LENGTH_SHORT).show()
    }

    fun assignment(position:Int){
        account.value = getEntityFromJSON(JSONObject(repository.data[position]))
        originalAccount = getEntityFromJSON(JSONObject(repository.data[position]))
        maleCheck.value = account.value?.gender == final.MALE
        femaleCheck.value = account.value?.gender == final.FEMALE
        statusCheck.value = account.value?.status == final.ACTIVE
        this.position = position
        enabled.value = false
    }

    fun retry(): Unit = repository.retry()

    fun getData(): ArrayList<String> = repository.data

    private fun getEntityFromJSON(jsonObject:JSONObject):APIEntity = jsonObject.let {
        APIEntity(it.getString(final.ID).toInt(),
            it.getString(final.FIRST_NAME).trim(),
            it.getString(final.LAST_NAME).trim(),
            it.getString(final.GENDER).trim(),
            it.getString(final.DOB).trim(),
            it.getString(final.EMAIL).toLowerCase(Locale.ROOT),
            it.getString(final.PHONE).trim(),
            it.getString(final.WEBSITE).trim(),
            it.getString(final.ADDRESS).trim(),
            it.getString(final.STATUS).trim())
    }

}
