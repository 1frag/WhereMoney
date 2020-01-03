package com.example.wheremoney.controllers

import android.util.Log
import com.example.wheremoney.helpers.ApiService
import com.example.wheremoney.models.CurrencyResponse
import com.example.wheremoney.models.SecretStorage
import kotlinx.coroutines.*

class CurrencyCtl {
    private val PENDING = 0
    private val FAILED = -1
    private val SUCCESS = 1

    private var state = PENDING
    private var model: CurrencyResponse? = null

    private suspend fun refresh(): Int {
        val apiService = ApiService.create()
        val u = apiService.search(SecretStorage().accessKey)
        val data = u.await()
        return if (!data.isSuccessful) {
            Log.i("CurrencyCtl", "not Successful")
            FAILED
        } else {
            model = data.body()
            if (!model!!.success) {
                Log.i("CurrencyCtl", "not Successful")
                FAILED
            } else {
                SUCCESS
            }
        }
    }

    private fun join() {
        fun inner(): Int = runBlocking {
            if (state == PENDING) {
                state = refresh()
            }
            return@runBlocking state
        }

        if (inner() != SUCCESS) {
            throw Throwable("Неуспешный гет запрос")
        }
    }

    private fun convertToBase(value: Float, currency: String): Float {
        var c = currency
        if (model == null) {
            throw Throwable("Join сработал неверно")
        }
        if (model!!.rates[c] == null) {
            // валюты которых нет - рубли
            c = "RUB"
        }
        return value / (model!!.rates[c])!!
    }

    private fun convertFromBase(value: Float, currency: String): Float {
        var c = currency
        if (model == null) {
            throw Throwable("Join сработал неверно")
        }
        if (model!!.rates[c] == null) {
            // валюты которых нет - рубли
            c = "RUB"
        }
        return value * (model!!.rates[c])!!
    }

    fun convert(value: Float, from: String, to: String): Float {
        this.join()
        return convertFromBase(convertToBase(value, from), to)
    }

}
