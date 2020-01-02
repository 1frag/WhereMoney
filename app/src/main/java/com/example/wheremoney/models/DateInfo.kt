package com.example.wheremoney.models

import java.util.*

class DateInfo(var day: Int = 0, var month: Int = 0, var year: Int = 0) {

    private fun earlyThan(other: DateInfo): Boolean {
        if (this.year < other.year) return true
        if (this.month < other.month) return true
        if (this.day < other.day) return true
        return false
    }

    fun equal(other: DateInfo): Boolean {
        if (this.year != other.year) return false
        if (this.month != other.month) return false
        if (this.day != other.day) return false
        return true
    }

    constructor (mark: String) : this() {
        when (mark) {
            "NOW" -> {
                val c = Calendar.getInstance()
                this.year = c.get(Calendar.YEAR)
                this.month = c.get(Calendar.MONTH)
                this.day = c.get(Calendar.DAY_OF_MONTH)
            }
            else -> {
                throw Throwable("Unexpected mark")
            }
        }
    }

    constructor (debt: Debt) : this() {
        this.day = debt.day!!
        this.month = debt.month!!
        this.year = debt.year!!
    }

    fun earlyThanNow(): Boolean {
        return this.earlyThan(DateInfo(mark = "NOW"))
    }
}