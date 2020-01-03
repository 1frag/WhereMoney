package com.example.wheremoney.helpers

class StrongFunctions {
    fun likeCurrency(th: String): String {
        return when (th) {
            "RUB" -> "\u20BD"
            "USD" -> "\$"
            "EUR" -> "€"
            else -> th
        }
    }
}