package com.example.wheremoney.views

import android.os.AsyncTask
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
import androidx.navigation.ui.navigateUp
import androidx.room.Room
import com.example.wheremoney.R
import com.example.wheremoney.controllers.HeadDriverMenuCtl
import com.example.wheremoney.helpers.AppDatabase
import com.example.wheremoney.interfaces.HeadDriverInterface
import com.example.wheremoney.interfaces.ResourceType
import com.example.wheremoney.interfaces.TextViewType
import com.example.wheremoney.models.Debt

class MainActivity : AppCompatActivity(), HeadDriverInterface {

    private val headDriverMenuCtl = HeadDriverMenuCtl(this)

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navView: NavigationView
    private lateinit var tvExchangeRates: TextView
    private lateinit var tvIMust: TextView
    private lateinit var tvOweToMe: TextView
    private lateinit var tvBudget: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        initNavCtl()
        initHeadDriverMenuCtl(navView).refresh()
    }

    private fun initNavCtl() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navController = findNavController(R.id.nav_host_fragment)

        navView = findViewById(R.id.nav_view)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navToday,
                R.id.navMyDebts,
                R.id.navTheirDebts
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun initHeadDriverMenuCtl(navView: NavigationView): HeadDriverMenuCtl {
        val header = navView.getHeaderView(0)
        fun get(id: Int): TextView {
            return header.findViewById(id)
        }

        tvIMust = get(R.id.tvIMust)
        tvBudget = get(R.id.tvBudget)
        tvOweToMe = get(R.id.tvOweToMe)
        tvExchangeRates = get(R.id.tvExchangeRates)
        return headDriverMenuCtl
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun setTextInTextView(text: String, tv: TextViewType) {
        when (tv) {
            TextViewType.EXCHANGERATESTV -> tvExchangeRates
            TextViewType.BUDGETTV -> tvBudget
            TextViewType.OWETOMETV -> tvOweToMe
            TextViewType.IMUSTTV -> tvIMust
        }.text = text
    }

    private fun getDB(): AppDatabase {
        return Room.databaseBuilder(
            this@MainActivity,
            AppDatabase::class.java, "hits.db"
        ).build()
    }

    override fun DBExecutor(
        longFunction: (db: AppDatabase) -> Map<String, List<Debt>>,
        postFunction: (map: Map<String, List<Debt>>) -> Unit
    ) {
        AsyncTask.execute {
            val map = longFunction(getDB())
            postFunction(map)
        }
    }

    override fun getResourceString(r: ResourceType): String {
        return resources.getString(
            when (r) {
                ResourceType.BUDGETFS -> R.string.budget
                ResourceType.EXCHANGERATESFS -> R.string.exchange_rates
                ResourceType.IMUSTFS -> R.string.i_must_str
                ResourceType.OWETOMEFS -> R.string.owe_to_me_str
                ResourceType.SORRY -> R.string.sorry
            }
        )
    }
}
