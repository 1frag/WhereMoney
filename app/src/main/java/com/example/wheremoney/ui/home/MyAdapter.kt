package com.example.wheremoney.ui.home

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wheremoney.R
import com.example.wheremoney.models.DateInfo
import com.example.wheremoney.models.Debt
import kotlinx.android.synthetic.main.card_item.view.*


class MyAdapter(private val items: ArrayList<Debt>, private val context: Context?) :
    RecyclerView.Adapter<ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // todo: делать логику отображения
        val current = items[position]
        chooseStyle(holder, current)
        holder.tvHowMany.text = String.format("%.2f (%s)",
            current.quantity, current.currency)
        holder.tvWho.text = current.partner
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

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val tvWho = view.tvWho!!
    val tvWhen = view.tvWhen!!
    val tvHowMany = view.tvHowMany!!
    val cardView = view.card_view!!
}
