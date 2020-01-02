package com.example.wheremoney.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wheremoney.R
import com.example.wheremoney.models.Debt
import kotlinx.android.synthetic.main.card_item.view.*

class MyAdapter(val items: ArrayList<Debt>, val context: Context?) :
    RecyclerView.Adapter<ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // todo: делать логику отображения
        val current = items.get(position)
        holder.tvHowMany?.text = String.format("%.2f (%s)",
            current.quantity, current.currency)
        holder.tvWhen?.text = current.date
        holder.tvWho?.text = current.partner
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
    val tvWho = view.tvWho
    val tvWhen = view.tvWhen
    val tvHowMany = view.tvHowMany
}
