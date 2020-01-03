package com.example.wheremoney.ui.home

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wheremoney.R
import com.example.wheremoney.controllers.CurrencyCtl
import com.example.wheremoney.helpers.StrongFunctions
import com.example.wheremoney.models.DateInfo
import com.example.wheremoney.models.Debt
import kotlinx.android.synthetic.main.card_item.view.*
import kotlin.math.absoluteValue


class MyAdapter(private val items: ArrayList<Debt>, private val context: Context?) :
    RecyclerView.Adapter<ViewHolder>() {
    private var currencyCtl: CurrencyCtl = CurrencyCtl()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = items[position]
        chooseStyle(holder, current)
        defineWhen(holder, current)
        defineOnlyRub(holder, current)

        holder.tvHowMany.text = String.format(
            "%.2f %s",
            current.quantity.absoluteValue,
            StrongFunctions().likeCurrency(current.currency)
        )
        holder.tvWho.text = current.partner
    }

    private fun defineOnlyRub(holder: ViewHolder, debt: Debt) {
        if (debt.currency == "RUB") return
        holder.tvHowManyRubOnly.text = String.format(
            "%.2f %s",
            currencyCtl.convert(debt.quantity.absoluteValue, debt.currency, "RUB"),
            StrongFunctions().likeCurrency("RUB")
        )
    }

    private fun Boolean.toInt() = if (this) 1 else 0

    private fun defineWhen(holder: ViewHolder, debt: Debt) {
        fun getDate(): Int {
            return when {
                DateInfo(debt).equal(DateInfo("NOW")) -> 0
                DateInfo(debt).earlyThanNow() -> -1
                else -> 1
            }
        }

        fun getDirection(): Int {
            return 2 * (debt.quantity > 0).toInt() - 1
        }

        holder.tvWhen.text = generateTextWhen(
            getDate(), getDirection(),
            DateInfo(debt).toString()
        )
    }

    private fun generateTextWhen(date: Int, direction: Int, formatDate: String): String {
        /**date :: 1 -> будет, 0 -> сегодня, -1 -> было
         * direction :: 1 -> мне, -1 -> я им
         * formatDate - красивая дата
         * */
        return "%s %s".format(
            if (direction == 1) "Мне вернут" else "Отдать",
            if (date == 0) "сегодня" else "до ".plus(formatDate)
        )
    }

    private fun chooseStyle(holder: ViewHolder, debt: Debt) {
        if (!debt.infinityDebt && DateInfo(debt).earlyThanNow()) {
            // просрочен срок
            holder.cardView.setCardBackgroundColor(getColor("#FFFF516E"))
            return
        }
        if (debt.quantity > 0) {
            // приятное: "мне должны"
            holder.cardView.setCardBackgroundColor(getColor("#FF4CAF50"))
            return
        }
        if (debt.quantity < 0 && !debt.infinityDebt && DateInfo(debt).equal(DateInfo("NOW"))) {
            // Это сегодня мне надо отдать - синим
            holder.cardView.setCardBackgroundColor(getColor("#FF00A9FF"))
            return
        }

        holder.tvHowManyRubOnly.setTextColor(getColor("#FF000000"))
        holder.tvHowMany.setTextColor(getColor("#FF000000"))
        holder.tvWhen.setTextColor(getColor("#FF000000"))
        holder.tvWho.setTextColor(getColor("#FF000000"))
    }

    private fun getColor(color: String): Int {
        return Color.parseColor(color)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.card_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvWho = view.tvWho!!
    val tvWhen = view.tvWhen!!
    val cardView = view.card_view!!
    val tvHowMany = view.tvHowMany!!
    val tvHowManyRubOnly = view.tvHowManyRubOnly!!
}
