package com.example.wheremoney.interfaces

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.wheremoney.helpers.AppDatabase
import com.example.wheremoney.models.Debt

interface KeeperRecyclerViewInterface {
    fun DBExecutor(
        longFunction: (db: AppDatabase) -> List<Debt>,
        postFunction: (lst: List<Debt>, rv: RecyclerView, context: Context) -> Unit
    )
    fun onIsEmptyList(isEmpty: Boolean)
    fun onIsBadRefresh()
    fun getQuerySet(it: AppDatabase): List<Debt>
}