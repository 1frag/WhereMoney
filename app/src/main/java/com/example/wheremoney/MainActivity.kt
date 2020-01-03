package com.example.wheremoney

import android.os.Bundle
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.navigateUp
import androidx.room.Room
import com.example.wheremoney.controllers.CurrencyCtl
import com.example.wheremoney.helpers.AppDatabase
import com.example.wheremoney.helpers.StrongFunctions
import com.example.wheremoney.models.DateInfo
import com.example.wheremoney.models.Debt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val currencyCtl = CurrencyCtl()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val tvExchangeRates: TextView = navView.getHeaderView(0).findViewById(R.id.tvExchangeRates)
        val tvIMust: TextView = navView.getHeaderView(0).findViewById(R.id.tvIMust)
        val tvOweToMe: TextView = navView.getHeaderView(0).findViewById(R.id.tvOweToMe)
        val tvBudget: TextView = navView.getHeaderView(0).findViewById(R.id.tvBudget)

        if (currencyCtl.isSuccess()) {
            syncTextExchangeRates(tvExchangeRates)
            syncTextAboutBudget(tvIMust, tvOweToMe, tvBudget)
        } else {
            // нет интернета, плохой запрос или, может быть, апокалипсис
            tvExchangeRates.text = resources.getString(R.string.sorry)
            tvIMust.text = ""
            tvOweToMe.text = ""
            tvBudget.text = ""
        }
    }

    private fun syncTextExchangeRates(tv: TextView) {
        //Курс валют (%s):\n1%s=%.2f%s\n1%s=%.2f%s
        tv.text = resources.getString(R.string.exchange_rates).format(
            DateInfo("NOW").toString(),
            StrongFunctions().likeCurrency("EUR"),
            currencyCtl.convert(1.0f, "EUR", "RUB"),
            StrongFunctions().likeCurrency("RUB"),
            StrongFunctions().likeCurrency("USD"),
            currencyCtl.convert(1.0f, "USD", "RUB"),
            StrongFunctions().likeCurrency("RUB")
        )
    }

    private fun syncTextAboutBudget(tvIMust: TextView, tvOweToMe: TextView, tvBudget: TextView) {
        fun aggregate(lst: List<Debt>): Float {
            var sum = 0.0f
            lst.forEach {
                sum += currencyCtl.convert(it.quantity.absoluteValue, it.currency, "RUB")
            }
            return sum
        }

        lifecycleScope.launch {
            val future = async(Dispatchers.IO) {
                val db = Room.databaseBuilder(
                    this@MainActivity,
                    AppDatabase::class.java, "hits.db"
                ).build()
                try {
                    return@async mapOf(
                        "i" to db.debtDao().getIMust(), // Я должен
                        "me" to db.debtDao().getOweToMe() // МНе должны
                    )
                } finally {
                    db.close()
                }
            }
            val result = future.await()
            val i = aggregate(result.getValue("i"))
            val me = aggregate(result.getValue("me"))
            // Я должен: %.2f
            tvIMust.text = resources.getString(R.string.i_must_str).format(i)
            // Мне должны: %.2f
            tvOweToMe.text = resources.getString(R.string.owe_to_me_str).format(me)
            // Бюджет: %.2f
            tvBudget.text = resources.getString(R.string.budget).format(me - i)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
