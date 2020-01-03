package com.example.wheremoney.views

import com.example.wheremoney.helpers.AppDatabase
import com.example.wheremoney.models.Debt

class TheirDebtFragment : AbstractFragment() {
    override fun getQuerySet(it: AppDatabase): List<Debt> {
        try {
            return it.debtDao().getOweToMe()
        } finally {
            it.close()
        }
    }
}