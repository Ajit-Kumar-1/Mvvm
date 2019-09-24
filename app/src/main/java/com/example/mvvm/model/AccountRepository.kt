package com.example.mvvm.model

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.mvvm.viewModel.ViewModelCallBack
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class AccountRepository(application: Application) {

    companion object {
        private const val GET_PAGE: String = "getPage"
        private const val PUT_CHANGES: String = "putChanges"
    }

    private val accountDAO: AccountDAO? = AccountDatabase.getInstance(application)?.accountDAO()
    private val service: AccountNetworkService? =
        AccountNetworkService.getInstance()?.create(AccountNetworkService::class.java)

    private var id: Int = 0
    private var pageIndex: Int = 1
    private var payload: JSONObject? = null
    private var viewModelCallBack: ViewModelCallBack? = null
    private var retryRequest: String = "none"

    fun insertAccountIntoDatabase(data: AccountEntity) {
        GlobalScope.launch { accountDAO?.insert(data) }
    }

    fun retryNetworkCall() {
        if (retryRequest == GET_PAGE) getPageRequest(pageIndex, viewModelCallBack)
        else if (retryRequest == PUT_CHANGES) putChangesRequest(payload, id, viewModelCallBack)
    }

    fun getPageRequest(pageIndex: Int, callBack: ViewModelCallBack?) {
        this.pageIndex = pageIndex
        this.viewModelCallBack = callBack
        retryRequest = GET_PAGE
        service?.getAccountsPage(pageIndex = pageIndex)?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                response.body()?.let { callBack?.getPageResponse(JSONObject(it)) }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack?.getPageError(throwable = t)
            }
        })
    }

    fun putChangesRequest(payload: JSONObject?, id: Int, callBack: ViewModelCallBack?) {
        this.id = id
        this.payload = payload
        this.viewModelCallBack = callBack
        retryRequest = PUT_CHANGES
        service?.putAccountDetails(id, payload.toString())?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                response.body()?.let { callBack?.onPutResponse(JSONObject(it)) }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack?.onPutError(throwable = t)
            }
        })
    }

    fun getData(): LiveData<MutableList<AccountEntity>>? = accountDAO?.getAll()

}
