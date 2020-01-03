package com.example.wheremoney.controllers

import android.content.Context
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wheremoney.helpers.AppDatabase
import com.example.wheremoney.interfaces.KeeperRecyclerViewInterface
import com.example.wheremoney.models.Debt

class KeeperRecyclerViewCtl(private val viewObj: KeeperRecyclerViewInterface) {
    private val currencyCtl = CurrencyCtl()

    fun getQuerySet(it: AppDatabase): List<Debt> {
        try {
            return it.debtDao().getAll()
        } finally {
            it.close()
        }
    }

    fun refresh() {
        viewObj.DBExecutor({
            return@DBExecutor viewObj.getQuerySet(it)
        }, { it: List<Debt>, rv: RecyclerView, context: Context ->

            rv.layoutManager = LinearLayoutManager(context)
            rv.adapter = MyAdapter(
                ArrayList(it),
                currencyCtl,
                context
            )
            viewObj.onIsEmptyList(it.isEmpty())

            val swipeHandler = object : SwipeToDeleteCallback(context) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter = rv.adapter as MyAdapter
                    adapter.removeAt(viewHolder.adapterPosition)
                    refresh()
                }
            }
            val itemTouchHelper = ItemTouchHelper(swipeHandler)
            itemTouchHelper.attachToRecyclerView(rv)
            if (!currencyCtl.isSuccess()) {
                viewObj.onIsBadRefresh()
            }
        })
    }
}
