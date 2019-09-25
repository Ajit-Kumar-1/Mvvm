package com.example.mvvm.viewModel

import org.json.JSONObject

interface ViewModelCallBack {
    fun putAccountChangesResponse(response: JSONObject)
    fun putAccountChangesError(throwable: Throwable)
    fun getAccountsPageResponse(response: JSONObject)
    fun getAccountsPageError(throwable: Throwable)
}
