package com.example.wheremoney.controllers

import com.example.wheremoney.helpers.StrongFunctions
import com.example.wheremoney.models.DateInfo
import com.example.wheremoney.models.Debt
import com.example.wheremoney.interfaces.TextViewType.*
import com.example.wheremoney.interfaces.ResourceType.*
import com.example.wheremoney.interfaces.HeadDriverInterface
import kotlin.math.absoluteValue

/**Контроллер отвечающий за верхушку выдвигающегося окошка
 * */

class HeadDriverMenuCtl(private val viewObject: HeadDriverInterface) {

    private val currencyCtl = CurrencyCtl()

    fun refresh() {
        if (currencyCtl.isSuccess()) {
            syncTextExchangeRates()
            syncTextAboutBudget()
        } else {
            // нет интернета, плохой запрос или, может быть, апокалипсис
            viewObject.setTextInTextView(
                viewObject.getResourceString(SORRY),
                EXCHANGERATESTV
            )
            viewObject.setTextInTextView("", IMUSTTV)
            viewObject.setTextInTextView("", OWETOMETV)
            viewObject.setTextInTextView("", BUDGETTV)
        }
    }

    private fun syncTextExchangeRates() {
        //Курс валют (%s):\n1%s=%.2f%s\n1%s=%.2f%s
        viewObject.setTextInTextView(
            viewObject.getResourceString(EXCHANGERATESFS).format(
                DateInfo("NOW").toString(),
                StrongFunctions().likeCurrency("EUR"),
                currencyCtl.convert(1.0f, "EUR", "RUB"),
                StrongFunctions().likeCurrency("RUB"),
                StrongFunctions().likeCurrency("USD"),
                currencyCtl.convert(1.0f, "USD", "RUB"),
                StrongFunctions().likeCurrency("RUB")
            ), EXCHANGERATESTV
        )
    }

    private fun syncTextAboutBudget() {
        fun aggregate(lst: List<Debt>): Float {
            var sum = 0.0f
            lst.forEach {
                sum += currencyCtl.convert(it.quantity.absoluteValue, it.currency, "RUB")
            }
            return sum
        }

        viewObject.DBExecutor(longFunction = {
            try {
                return@DBExecutor mapOf(
                    "i" to it.debtDao().getIMust(), // Я должен
                    "me" to it.debtDao().getOweToMe() // Мне должны
                )
            } finally {
                it.close()
            }
        }, postFunction = {
            val i = aggregate(it.getValue("i"))
            val me = aggregate(it.getValue("me"))
            // Я должен: %.2f
            viewObject.setTextInTextView(
                viewObject.getResourceString(IMUSTFS).format(i),
                IMUSTTV
            )
            // Мне должны: %.2f
            viewObject.setTextInTextView(
                viewObject.getResourceString(OWETOMEFS).format(me),
                OWETOMETV
            )
            // Бюджет: %.2f
            viewObject.setTextInTextView(
                viewObject.getResourceString(BUDGETFS).format(me - i),
                BUDGETTV
            )
        })
    }
}