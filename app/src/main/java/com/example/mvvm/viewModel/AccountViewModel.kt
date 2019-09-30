package com.example.mvvm.viewModel

import android.app.Application
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mvvm.R
import com.example.mvvm.model.AccountEntity
import com.example.mvvm.model.AccountRepository
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class AccountViewModel(application: Application) : AndroidViewModel(application),
    ViewModelCallBack {

    companion object {
        const val ACTIVE: String = "active"
        const val ADDRESS_KEY: String = "address"
        const val AVATAR_KEY: String = "avatar"
        const val CODE_KEY: String = "code"
        const val DOB_KEY: String = "dob"
        const val EMAIL_KEY: String = "email"
        const val FEMALE: String = "female"
        const val FIELD_KEY: String = "field"
        const val FIRST_NAME_KEY: String = "first_name"
        const val GENDER_KEY: String = "gender"
        const val HREF_KEY: String = "href"
        const val ID_KEY: String = "id"
        const val INACTIVE: String = "inactive"
        const val LAST_NAME_KEY: String = "last_name"
        const val LINKS_KEY: String = "_links"
        const val MALE: String = "male"
        const val MESSAGE_KEY: String = "message"
        const val META_KEY: String = "_meta"
        const val PHONE_KEY: String = "phone"
        const val RESULT_KEY: String = "result"
        const val STATUS_KEY: String = "status"
        const val SUCCESS_KEY: String = "success"
        const val WEBSITE_KEY: String = "website"
    }

    private val repository: AccountRepository = AccountRepository(application)
    private var pageIndex: Int = 1
    private var accountOriginalDetails: AccountEntity? = null
    internal var selectedItemPosition: Int = 0
    var accountCurrentDetails: MutableLiveData<AccountEntity?> = MutableLiveData()
    var paginationProgressSpinnerVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    var putRequestProgressSpinnerVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    var enableAccountDetailEdit: MutableLiveData<Boolean> = MutableLiveData(false)
    var maleRadioButtonValue: MutableLiveData<Boolean> = MutableLiveData(false)
    var femaleRadioButtonValue: MutableLiveData<Boolean> = MutableLiveData(false)
    var statusSwitchValue: MutableLiveData<Boolean> = MutableLiveData(false)
    val activeValue: String = application.resources.getString(R.string.active)
    val inactiveValue: String = application.resources.getString(R.string.inactive)
    var dataExists = false
    var viewDetailsContainerOnPortrait: MutableLiveData<Boolean> = MutableLiveData(false)
    var retryNetworkRequest: MutableLiveData<Boolean> = MutableLiveData(false)
    val adapter = AccountDetailsAdapter(getData()?.value) //Here due to scroll position reset issue

    init {
        getAccountsPage()
    }

    fun getAccountsPage() {
        paginationProgressSpinnerVisibility.value = true
        repository.getPageRequest(pageIndex, this as ViewModelCallBack)
    }

    override fun getAccountsPageResponse(response: JSONObject) {
        if (!response.getJSONObject(META_KEY).getBoolean(SUCCESS_KEY)) getPageFailure(response)
        else if (getDataFromJSONArray(response.getJSONArray(RESULT_KEY))) pageIndex++
        paginationProgressSpinnerVisibility.value = false
    }

    private fun getDataFromJSONArray(array: JSONArray): Boolean {
        var previousID: Int = repository.getData()?.value?.last()?.id ?: 0
        for (i: Int in 0 until array.length())
            getAccountEntityFromJSON(array.getJSONObject(i)).let {
                if (it.id > previousID) repository.insertAccountIntoDatabase(it)
                else return false
                previousID = it.id
            }
        return true
    }

    private fun getAccountEntityFromJSON(jsonObject: JSONObject): AccountEntity = jsonObject.let {
        AccountEntity(
            it.getString(ID_KEY).toInt(),
            it.getString(FIRST_NAME_KEY).trim(),
            it.getString(LAST_NAME_KEY).trim(),
            it.getString(GENDER_KEY).trim(),
            it.getString(DOB_KEY).trim(),
            it.getString(EMAIL_KEY).trim().toLowerCase(Locale.ROOT),
            it.getString(PHONE_KEY).trim(),
            it.getString(WEBSITE_KEY).trim(),
            it.getString(ADDRESS_KEY).trim(),
            it.getString(STATUS_KEY).trim(),
            it.getJSONObject(LINKS_KEY).getJSONObject(AVATAR_KEY).getString(HREF_KEY)
        )
    }

    private fun getPageFailure(response: JSONObject): Unit = response.getJSONObject(META_KEY).let {
        Toast.makeText(
            getApplication(), "${it.get(CODE_KEY)}:" + " ${it.get(MESSAGE_KEY)}", LENGTH_SHORT
        ).show()
    }

    override fun getAccountsPageError(throwable: Throwable) {
        paginationProgressSpinnerVisibility.value = false
        Toast.makeText(getApplication(), throwable.message, LENGTH_SHORT).show()
    }

    fun putAccountDetailChanges(): Unit =
        findChanges(setGenderAndStatus(accountCurrentDetails.value), accountOriginalDetails).let {
            if (it.size > 0) accountCurrentDetails.value?.id?.let { id ->
                repository.putChangesRequest(
                    JSONObject(it as Map<*, *>), id, this as ViewModelCallBack
                )
                putRequestProgressSpinnerVisibility.value = true
            }
            else enableAccountDetailEdit.value = false
        }

    private fun setGenderAndStatus(account: AccountEntity?): AccountEntity? = account?.apply {
        if (maleRadioButtonValue.value == true) gender = MALE
        if (femaleRadioButtonValue.value == true) gender = FEMALE
        status = if (statusSwitchValue.value == true) ACTIVE else INACTIVE
    }

    private fun findChanges(currentAccount: AccountEntity?, originalAccount: AccountEntity?):
            HashMap<String, String?> = HashMap<String, String?>().also { map ->
        currentAccount?.run {
            originalAccount?.let {
                if (it.firstName?.trim() != firstName?.trim()) map[FIRST_NAME_KEY] =
                    firstName?.trim()
                if (it.lastName?.trim() != lastName?.trim()) map[LAST_NAME_KEY] = lastName?.trim()
                if (it.gender?.trim() != gender) map[GENDER_KEY] = gender?.trim()
                if (it.dob?.trim() != dob?.trim()) map[DOB_KEY] = dob?.trim()
                if (it.email?.trim() != email?.trim()) map[EMAIL_KEY] = email?.trim()
                if (it.phone?.trim() != phone?.trim()) map[PHONE_KEY] = phone?.trim()
                if (it.website?.trim() != website?.trim()) map[WEBSITE_KEY] = website?.trim()
                if (it.address?.trim() != address?.trim()) map[ADDRESS_KEY] = address?.trim()
                if (it.status?.trim() != status?.trim()) map[STATUS_KEY] = status?.trim()
            }
        }
    }

    override fun putAccountChangesResponse(response: JSONObject) {
        if (response.getJSONObject(META_KEY).getBoolean(SUCCESS_KEY))
            getAccountEntityFromJSON(response.getJSONObject(RESULT_KEY)).let {
                repository.insertAccountIntoDatabase(it)
                showAccountDetails(it)
                Toast.makeText(getApplication(), R.string.information_updated, LENGTH_SHORT).show()
                enableAccountDetailEdit.value = false
            } else onPutFailure(response)
        putRequestProgressSpinnerVisibility.value = false
    }

    private fun onPutFailure(response: JSONObject): Unit = response.getJSONArray(RESULT_KEY).let {
        for (i in 0 until it.length()) Toast.makeText(
            getApplication(), "${it.getJSONObject(i).getString(FIELD_KEY)}:" +
                    " ${it.getJSONObject(i).getString(MESSAGE_KEY)}", LENGTH_SHORT
        ).show()
    }

    override fun putAccountChangesError(throwable: Throwable) {
        putRequestProgressSpinnerVisibility.value = false
        enableAccountDetailEdit.value = false
        Toast.makeText(getApplication(), throwable.message, LENGTH_SHORT).show()
    }

    fun showAccountDetails(account: AccountEntity?) {
        accountOriginalDetails = account
        accountCurrentDetails.value = accountOriginalDetails?.copy()
        maleRadioButtonValue.value = accountCurrentDetails.value?.gender == MALE
        femaleRadioButtonValue.value = accountCurrentDetails.value?.gender == FEMALE
        statusSwitchValue.value = accountCurrentDetails.value?.status == ACTIVE
        enableAccountDetailEdit.value = false
    }

    fun resetAccount(): Unit = showAccountDetails(accountOriginalDetails)

    fun getData(): LiveData<MutableList<AccountEntity>>? = repository.getData()

    fun retryNetworkRequest() {
        repository.retryNetworkCall()
        retryNetworkRequest.value = false
    }

    fun loadInitialAccount(account: AccountEntity?) {
        showAccountDetails(account)
        dataExists = true
    }

}
