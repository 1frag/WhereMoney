package com.example.wheremoney.ui.home

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_new_debt.*
import android.content.Intent
import android.view.MenuItem
import android.widget.Toast
import com.example.wheremoney.MainActivity
import kotlinx.android.synthetic.main.content_add_new_debt.*
import java.util.*
import java.text.SimpleDateFormat
import com.example.wheremoney.*
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.wheremoney.models.AppDatabase
import com.example.wheremoney.models.Debt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AddNewDebtActivity : AppCompatActivity() {

    private class Data {
        var date: String = ""
        var multiplier: Int = 0
        var partnerCorrect: Boolean = false
        var quantityCorrect: Boolean = false
    }

    private var currentData = Data()

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
            val dpd = DatePickerDialog(
                this, R.style.CalendarStyle,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val myFormat = "dd.MM.yyyy"
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    val formatDate = sdf.format(cal.time)
                    textView7.text = String.format("Вернет до: %s", formatDate)
                    currentData.date = formatDate
                }, year, month, day
            )
            dpd.show()
        }
        changeState(switch1.isChecked)
        switch1.setOnCheckedChangeListener { _, isChecked ->
            changeState(isChecked)
        }
        textInputEditText1.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (textInputEditText1.text.toString() == "") {
                    textInputEditText1.error = "Необходимо указать, кто вам должен"
                    currentData.partnerCorrect = false
                } else {
                    currentData.partnerCorrect = true
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        textInputEditText2.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                currentData.quantityCorrect = checkQuantityCorrect(
                    textInputEditText2.text.toString()
                )
            }

            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        button.setOnClickListener {
            val msg = checkFullnessData()
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            doInsertDebt(
                textInputEditText1.text.toString(),
                currentData.date,
                currentData.multiplier * textInputEditText2.text.toString().toFloat(),
                spinner.selectedItem.toString()
            )
        }

    }

    private fun doInsertDebt(partner: String, date: String, quantity: Float, currency: String) {
        lifecycleScope.launch {
            val res = async(Dispatchers.IO) {
                val db = Room.databaseBuilder(
                    this@AddNewDebtActivity,
                    AppDatabase::class.java, "hits.db"
                ).build()
                return@async db.debtDao().insert(
                    Debt(
                        partner = partner, id=0,
                        date = date, quantity = quantity, currency = currency
                    )
                )
            }
            res.await()
        }
    }

    private fun checkFullnessData(): String? {
        if (currentData.date == "") return "Введите дату"
        if (textInputEditText1.text.toString() == "") return "Введите имя"
        if (!currentData.quantityCorrect) return "Сумма невалидна"
        if (spinner.selectedItem == null) throw Throwable("Bug")
        return null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val myIntent = Intent(applicationContext, MainActivity::class.java)
        startActivityForResult(myIntent, 0)
        return true
    }

    private fun changeState(isChecked: Boolean) {
        if (isChecked) {
            // Я должник
            textView3.text = "Я должник"
            textInputLayout.hint = "Имя доброго человека"
            useNewPrefixForDateTextView("Вернуть до: ")
            currentData.multiplier = -1
        } else {
            // Я занимаю
            textView3.text = "Я занимаю"
            textInputLayout.hint = "Имя должника"
            useNewPrefixForDateTextView("Вернет до: ")
            currentData.multiplier = 1
        }
    }

    private fun useNewPrefixForDateTextView(prefix: String) {
        var ourDate = currentData.date
        if (currentData.date == "") {
            ourDate = "возможно никогда"
        }
        textView7.text = String.format("%s%s", prefix, ourDate)
    }

    private fun checkQuantityCorrect(txt: String): Boolean {
        val regex = Regex(pattern = "^[0-9]+(.[0-9]{1,2})?$")
        return regex.containsMatchIn(input = txt)
    }

}
