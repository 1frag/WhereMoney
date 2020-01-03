package com.example.wheremoney.helpers

import com.example.wheremoney.models.CurrencyResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("api/latest")
    fun search(@Query("access_key") access_key: String):
            Deferred<Response<CurrencyResponse>>

    companion object Factory {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://data.fixer.io/")
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}