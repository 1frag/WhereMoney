package com.example.wheremoney.ui.home

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_new_debt.*
import android.content.Intent
import android.view.MenuItem
import com.example.wheremoney.MainActivity
import kotlinx.android.synthetic.main.content_add_new_debt.*
import java.util.*
import com.example.wheremoney.R
import java.text.SimpleDateFormat
import javax.xml.datatype.DatatypeConstants.MONTHS


class AddNewDebtActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.wheremoney.R.layout.activity_add_new_debt)
        setSupportActionBar(toolbar)
        with(supportActionBar!!) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        toolbar.setNavigationOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val cal = Calendar.getInstance()

        imageView.setOnClickListener {
            val dpd = DatePickerDialog(this, R.style.CalendarStyle,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val myFormat = "dd.MM.yyyy"
                    // FIXME: после того как дата установлена спинер уходит вправо
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    textView7.text = String.format("Вернет до: %s", sdf.format(cal.time))
                }, year, month, day)
            dpd.show()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val myIntent = Intent(applicationContext, MainActivity::class.java)
        startActivityForResult(myIntent, 0)
        return true
    }
}
