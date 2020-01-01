package com.example.wheremoney.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.wheremoney.R
import com.example.wheremoney.models.AppDatabase
import com.example.wheremoney.models.Debt
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    val animals: ArrayList<Debt> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        addAnimals(this.context!!) // todo: change to add cards
        root.rv_cards_list.layoutManager = LinearLayoutManager(this.context)
        root.rv_cards_list.adapter = MyAdapter(animals, this.context)
        return root
    }

    private fun addAnimals(context: Context) {
        lifecycleScope.launch {
            val res = async(Dispatchers.IO) {
                val db = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, "hits.db"
                ).build()
                return@async db.debtDao().getAll()
            }
            animals.addAll(res.await())
        }
    }
}