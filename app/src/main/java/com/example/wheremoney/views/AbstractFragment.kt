package com.example.wheremoney.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.wheremoney.R
import com.example.wheremoney.controllers.KeeperRecyclerViewCtl
import com.example.wheremoney.helpers.AppDatabase
import com.example.wheremoney.interfaces.KeeperRecyclerViewInterface
import com.example.wheremoney.models.Debt
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

abstract class AbstractFragment : Fragment(), KeeperRecyclerViewInterface {
    open lateinit var keeperRVCtl: KeeperRecyclerViewCtl

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        keeperRVCtl = KeeperRecyclerViewCtl(this)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        keeperRVCtl.refresh()
        changeFab(root.fab)
        return root
    }

    @SuppressLint("RestrictedApi")
    open fun changeFab(fab: FloatingActionButton) {
        //FIXME: SuppressLint
        fab.visibility = View.INVISIBLE
    }

    override fun onIsBadRefresh() {
        Toast.makeText(context, R.string.internet_problem, Toast.LENGTH_LONG).show()
    }

    override fun getQuerySet(it: AppDatabase): List<Debt> {
        try {
            return it.debtDao().getIMust()
        } finally {
            it.close()
        }
    }

    override fun onIsEmptyList(isEmpty: Boolean) {
        if (isEmpty) {
            pictureIfNoOneDebt.visibility = View.VISIBLE
            textIfNoOneDebt.visibility = View.VISIBLE
        } else {
            pictureIfNoOneDebt.visibility = View.INVISIBLE
            textIfNoOneDebt.visibility = View.INVISIBLE
        }
    }

    private fun getDB(): AppDatabase {
        return Room.databaseBuilder(
            this@AbstractFragment.context!!,
            AppDatabase::class.java, "hits.db"
        ).build()
    }

    override fun DBExecutor(
        longFunction: (db: AppDatabase) -> List<Debt>,
        postFunction: (lst: List<Debt>, rv: RecyclerView, context: Context) -> Unit
    ) {
        AsyncTask.execute {
            val lst = longFunction(getDB())
            this@AbstractFragment.activity!!.runOnUiThread {
                postFunction(lst, rv_cards_list, context!!)
            }
        }
    }
}