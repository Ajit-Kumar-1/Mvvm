package com.example.mvvm.model

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

interface AccountNetworkService {

    @Headers("$ACCESS_TOKEN_KEY: $ACCESS_TOKEN_VALUE")
    @GET(value = "users")
    fun getAccountsPage(@Query(value = "page", encoded = true) pageIndex: Int): Call<String>

    @Headers(
        "$ACCESS_TOKEN_KEY: $ACCESS_TOKEN_VALUE", "$CONTENT_TYPE_KEY: $CONTENT_TYPE_VALUE"
    )
    @PATCH(value = "users/{id}")
    fun putAccountDetails(@Path(value = "id", encoded = true) id: Int, @Body payload: String):
            Call<String>

    companion object {
        private const val BASE_URL: String = "https://gorest.co.in/public-api/"
        const val ACCESS_TOKEN_KEY: String = "Authorization"
        @Suppress("SpellCheckingInspection")
        const val ACCESS_TOKEN_VALUE: String = "Bearer ECrl5oVQGWxkatlCGzXkIigkiazt3BBtW0SI"
        const val CONTENT_TYPE_KEY: String = "Content-Type"
        const val CONTENT_TYPE_VALUE: String = "application/json"

        private var instance: Retrofit? = null
        fun getInstance(): Retrofit? {
            if (instance == null) instance = Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create()).build()
            return instance
        }
    }

}
