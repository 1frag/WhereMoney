package com.example.wheremoney.ui.home

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.wheremoney.MainActivity
import com.example.wheremoney.R
import com.example.wheremoney.helpers.AppDatabase
import com.example.wheremoney.models.DateInfo
import com.example.wheremoney.models.Debt
import kotlinx.android.synthetic.main.activity_add_new_debt.*
import kotlinx.android.synthetic.main.content_add_new_debt.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class AddNewDebtActivity : AppCompatActivity() {

    private class Data {
        var date: DateInfo? = null
        var multiplier: Int = 0
        var partnerCorrect: Boolean = false
        var quantityCorrect: Boolean = false
    }

    private var currentData = Data()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_debt)
        setSupportActionBar(toolbar)
        with(supportActionBar!!) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        toolbar.setNavigationOnClickListener {
            onAnyBackButton(fun() {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            })
        }

        val c = Calendar.getInstance()
        val nowYear = c.get(Calendar.YEAR)
        val nowMonth = c.get(Calendar.MONTH)
        val nowDay = c.get(Calendar.DAY_OF_MONTH)
        val cal = Calendar.getInstance()

        imageView.setOnClickListener {
            val dpd = DatePickerDialog(
                this, R.style.CalendarStyle,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val myFormat = "dd.MM.yyyy"
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    val formatDate = sdf.format(cal.time)
                    textView7.text = String.format("Вернет до: %s", formatDate)
                    currentData.date = DateInfo(dayOfMonth, monthOfYear, year)
                }, nowYear, nowMonth, nowDay
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
                    textInputEditText1.error = null
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
                if (!currentData.quantityCorrect) {
                    textInputLayout2.error = "Неправильно введена сумма долга"
                } else {
                    textInputEditText1.error = null
                }
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
            prepareAndDoInsert()
            this@AddNewDebtActivity.finish()
        }
    }

    private fun prepareAndDoInsert() {
        // агрегируя данные отправлять в инсёрт
        doInsertDebt(
            textInputEditText1.text.toString(),
            currentData.date,
            currentData.multiplier * textInputEditText2.text.toString().toFloat(),
            spinner.selectedItem.toString()
        )
    }

    private fun hasBeenChanged(): Boolean {
        // Изменения есть?
        if (textInputEditText1.text.toString() != "") return true
        if (currentData.date != null) return true
        if (textInputEditText2.text.toString() != "") return true
        return false
    }

    private fun onAnyBackButton(howToGoAway: () -> Unit) {
        if (!hasBeenChanged()) {
            // если ничего не изменено то прост супер
            super.onBackPressed()
            return
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setMessage("СОХРАНИТЬ?")
            .setPositiveButton("ДА!") { _: DialogInterface, _: Int ->
                val msg = checkFullnessData()
                if (msg != null) {
                    /* Хочет сохранить но данные не валидны - тогда тост
                        Надо исправить данные или ответ*/
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                } else {
                    prepareAndDoInsert()
                    howToGoAway()
                }
            }
            .setNegativeButton("ОТМЕНА") { _: DialogInterface, _: Int ->
                howToGoAway()
            }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onBackPressed() {
        onAnyBackButton(fun() { super.onBackPressed() })
    }

    private fun doInsertDebt(partner: String, date: DateInfo?, quantity: Float, currency: String) {
        lifecycleScope.launch {
            val curDebt = Debt(
                partner = partner,
                quantity = quantity,
                currency = currency
            )
            if (date != null) {
                curDebt.infinityDebt = false
                curDebt.day = date.day
                curDebt.month = date.month
                curDebt.year = date.year
            }
            val res = async(Dispatchers.IO) {
                val db = Room.databaseBuilder(
                    this@AddNewDebtActivity,
                    AppDatabase::class.java, "hits.db"
                ).build()

                try {
                    return@async db.debtDao().insert(curDebt)
                } finally {
                    db.close()
                }

            }
            res.await()
        }
    }

    private fun checkFullnessData(): String? {
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
            textView3.text = "Я должник" // буду должен отдать (типа минус)
            textInputLayout.hint = "Имя доброго человека"
            useNewPrefixForDateTextView("Вернуть до: ")
            currentData.multiplier = -1
        } else {
            // Я занимаю
            textView3.text = "Я занимаю" // другому (типа мне потом добавят)
            textInputLayout.hint = "Имя должника"
            useNewPrefixForDateTextView("Вернет до: ")
            currentData.multiplier = 1
        }
    }

    private fun useNewPrefixForDateTextView(prefix: String) {
        var dateformat = "возможно никогда"
        if (currentData.date != null) {
            dateformat = String.format(
                "%d.%d.%d",
                currentData.date!!.day,
                currentData.date!!.month,
                currentData.date!!.year
            )
        }
        textView7.text = String.format("%s%s", prefix, dateformat)
    }

    private fun checkQuantityCorrect(txt: String): Boolean {
        val regex = Regex(pattern = "^[1-9][0-9]*(.[0-9]{1,2})?$")
        return regex.containsMatchIn(input = txt)
    }

}
