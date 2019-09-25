package com.example.mvvm.viewModel

import android.app.Application
import android.widget.Toast
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
    private var selectedItemPosition: Int = 0
    private var accountOriginalDetails: AccountEntity? = null
    var accountCurrentDetails: MutableLiveData<AccountEntity> = MutableLiveData()
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
    val adapter = AccountDetailsAdapter(getData()?.value)

    init {
        getAccountsPage()
    }

    fun getAccountsPage() {
        paginationProgressSpinnerVisibility.value = true
        repository.getPageRequest(pageIndex = pageIndex, callBack = this as ViewModelCallBack)
    }

    override fun getAccountsPageResponse(response: JSONObject) {
        if (!response.getJSONObject(META_KEY).getBoolean(SUCCESS_KEY)) getPageFailure(response)
        else if (getDataFromJSONArray(response = response)) pageIndex++
        paginationProgressSpinnerVisibility.value = false
    }

    private fun getDataFromJSONArray(response: JSONObject): Boolean {
        var success = true
        var previousID: Int = if (repository.getData()?.value?.isNotEmpty() == true)
            repository.getData()?.value?.last()?.id ?: 0
        else 0
        val jsonArray: JSONArray = response.getJSONArray(RESULT_KEY)
        for (i: Int in 0 until jsonArray.length()) {
            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
            if (jsonObject.getString(ID_KEY).toInt() > previousID) repository.apply {
                val account: AccountEntity = getAccountEntityFromJSON(jsonObject)
                insertAccountIntoDatabase(account)
                previousID = account.id
            } else {
                success = false
                break
            }
        }
        return success
    }

    private fun getAccountEntityFromJSON(jsonObject: JSONObject): AccountEntity = jsonObject.let {
        AccountEntity(
            it.getString(ID_KEY).toInt(),
            it.getString(FIRST_NAME_KEY).trim(),
            it.getString(LAST_NAME_KEY).trim(),
            it.getString(GENDER_KEY).trim(),
            it.getString(DOB_KEY).trim(),
            it.getString(EMAIL_KEY).toLowerCase(Locale.ROOT),
            it.getString(PHONE_KEY).trim(),
            it.getString(WEBSITE_KEY).trim(),
            it.getString(ADDRESS_KEY).trim(),
            it.getString(STATUS_KEY).trim(),
            it.getJSONObject(LINKS_KEY).getJSONObject(AVATAR_KEY).getString(HREF_KEY)
        )
    }

    private fun getPageFailure(response: JSONObject): Unit = response.getJSONObject(META_KEY).let {
        Toast.makeText(
            getApplication(), "${it.get(CODE_KEY)}:" + " ${it.get(MESSAGE_KEY)}",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun getAccountsPageError(throwable: Throwable) {
        paginationProgressSpinnerVisibility.value = false
        Toast.makeText(getApplication(), throwable.message, Toast.LENGTH_SHORT).show()
    }

    fun putAccountDetailChanges(): Unit = findChanges(
        currentAccount = setGenderAndStatus(account = accountCurrentDetails.value),
        originalAccount = accountOriginalDetails
    ).let {
        if (it.size > 0) accountCurrentDetails.value?.id?.let { id ->
            repository.putChangesRequest(JSONObject(it as Map<*, *>), id, this as ViewModelCallBack)
            putRequestProgressSpinnerVisibility.value = true
        }
    }

    private fun setGenderAndStatus(account: AccountEntity?): AccountEntity? = account?.also {
        if (maleRadioButtonValue.value == true) it.gender = MALE
        if (femaleRadioButtonValue.value == true) it.gender = FEMALE
        it.status = if (statusSwitchValue.value == true) ACTIVE else INACTIVE
    }

    private fun findChanges(currentAccount: AccountEntity?, originalAccount: AccountEntity?):
            HashMap<String, String?> = HashMap<String, String?>().also { map ->
        currentAccount?.run {
            originalAccount?.let {
                if (it.firstName?.trim() != firstName?.trim()) map[FIRST_NAME_KEY] =
                    firstName
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
        if (response.getJSONObject(META_KEY).getBoolean(SUCCESS_KEY)) {
            getAccountEntityFromJSON(response.getJSONObject(RESULT_KEY)).let {
                repository.insertAccountIntoDatabase(it)
                assignAccountDetails(account = it, position = selectedItemPosition)
            }
            Toast.makeText(getApplication(), R.string.information_updated, Toast.LENGTH_SHORT)
                .show()
        } else onPutFailure(response = response)
        putRequestProgressSpinnerVisibility.value = false
    }

    private fun onPutFailure(response: JSONObject): Unit = response.getJSONArray(RESULT_KEY).let {
        for (i in 0 until it.length()) Toast.makeText(
            getApplication(), "${it.getJSONObject(i).getString(FIELD_KEY)}:" +
                    " ${it.getJSONObject(i).getString(MESSAGE_KEY)}", Toast.LENGTH_SHORT
        ).show()
    }

    override fun putAccountChangesError(throwable: Throwable) {
        putRequestProgressSpinnerVisibility.value = false
        Toast.makeText(getApplication(), throwable.message, Toast.LENGTH_SHORT).show()
    }

    fun assignAccountDetails(account: AccountEntity?, position: Int) {
        accountOriginalDetails = account
        accountCurrentDetails.value = accountOriginalDetails?.copy()
        maleRadioButtonValue.value = accountCurrentDetails.value?.gender == MALE
        femaleRadioButtonValue.value = accountCurrentDetails.value?.gender == FEMALE
        statusSwitchValue.value = accountCurrentDetails.value?.status == ACTIVE
        this.selectedItemPosition = position
        enableAccountDetailEdit.value = false
    }

    fun reassignAccountDetails(): Unit =
        assignAccountDetails(accountOriginalDetails, selectedItemPosition)

    fun getSelectedItemPosition(): Int = selectedItemPosition

    fun getData(): LiveData<MutableList<AccountEntity>>? = repository.getData()

    fun retryNetworkRequest(): Unit = repository.retryNetworkCall()

    fun loadInitialAccount(account: AccountEntity?) {
        assignAccountDetails(account, 0)
        dataExists = true
    }

}
