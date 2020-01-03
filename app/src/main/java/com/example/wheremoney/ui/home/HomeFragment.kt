package com.example.wheremoney.ui.home

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.wheremoney.R
import com.example.wheremoney.SwipeToDeleteCallback
import com.example.wheremoney.controllers.CurrencyCtl
import com.example.wheremoney.helpers.AppDatabase
import com.example.wheremoney.models.Debt
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private val debts: ArrayList<Debt> = ArrayList()
    private val currencyCtl = CurrencyCtl()
    //val logger = Logger.getLogger(this.javaClass.simpleName)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        root.fab.setOnClickListener {
            addNewDebt()
            selectAllAgain(this.context!!, root.rv_cards_list)
        }
        selectAllAgain(this.context!!, root.rv_cards_list)
        return root
    }

    private fun selectAllAgain(context: Context, rv: RecyclerView) {
        lifecycleScope.launch {
            val future = async(Dispatchers.IO) {
                val db = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, "hits.db"
                ).build()
                try {
                    return@async db.debtDao().getAll()
                } finally {
                    db.close()
                }
            }
            val result = future.await()

            rv.layoutManager = LinearLayoutManager(context)
            rv.adapter = MyAdapter(debts, currencyCtl, context)
            debts.addAll(result)

            if (debts.size == 0) {
                pictureIfNoOneDebt.visibility = View.VISIBLE
                textIfNoOneDebt.visibility = View.VISIBLE
            } else {
                pictureIfNoOneDebt.visibility = View.INVISIBLE
                textIfNoOneDebt.visibility = View.INVISIBLE
            }

            val swipeHandler = object : SwipeToDeleteCallback(context) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter = rv.adapter as MyAdapter
                    adapter.removeAt(viewHolder.adapterPosition)
                    selectAllAgain(context, rv_cards_list)
                }
            }
            val itemTouchHelper = ItemTouchHelper(swipeHandler)
            itemTouchHelper.attachToRecyclerView(rv)
            if (!currencyCtl.isSuccess()) {
                Toast.makeText(context, R.string.internet_problem, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addNewDebt() {
        val intent = Intent(this.context!!, AddNewDebtActivity::class.java)
        startActivity(intent)
    }
}