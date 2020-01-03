package com.example.wheremoney.views

import android.content.Intent
import android.util.Log
import com.example.wheremoney.controllers.KeeperRecyclerViewCtl
import com.example.wheremoney.helpers.AppDatabase
import com.example.wheremoney.models.Debt
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TodayFragment : AbstractFragment() {

    override fun changeFab(fab: FloatingActionButton) {
        fab.setOnClickListener {
            addNewDebt()
        }
    }

    override fun onResume() {
        Log.i("TodayFragment", "onResume")
        keeperRVCtl.refresh()
        super.onResume()
    }

    private fun addNewDebt() {
        Log.i("TodayFragment", "addNewDebt")
        val intent = Intent(this.context, AddNewDebtActivity::class.java)
        startActivity(intent)
    }

    override fun getQuerySet(it: AppDatabase): List<Debt> {
        try {
            return it.debtDao().getAll()
        } finally {
            it.close()
        }
    }

}